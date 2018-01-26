package com.wugi.inc.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.wugi.inc.R;
import com.wugi.inc.adapters.BrowseRecyclerAdapter;
import com.wugi.inc.models.BrowseEvent;
import com.wugi.inc.models.BrowseVenueType;
import com.wugi.inc.models.Type;
import com.wugi.inc.utils.OnSwipeTouchListener;
import com.wugi.inc.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;

public class BrowseFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<BrowseEvent> browseEventList = new ArrayList<BrowseEvent>();
    private ArrayList<BrowseEvent> browseVenueList = new ArrayList<BrowseEvent>();
    private ArrayList<BrowseVenueType> browseVenueTYpeList = new ArrayList<BrowseVenueType>();
    private BrowseRecyclerAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private Context mContext;

    private OnFragmentInteractionListener mListener;

    TextView tv_event, tv_venue, tv_type;
    private Type type;

    public BrowseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Date getTodayFormat() {
//        String dateString = "03/26/2012 11:49:00 AM";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String todayStr = dateFormat.format(today);
        todayStr = todayStr + " 04:00:00 +0000";

//        todayStr = "2017-12-01" + " 04:00:00 +0000";

        Date convertedDate = new Date();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZZZ");
        try {
            convertedDate = outputFormat.parse(todayStr);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(convertedDate);
        return convertedDate;
    }

    private void getBrowseEvents() {
        Date today = getTodayFormat();

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date weekDate = cal.getTime();

        this.browseEventList.clear();
        final ProgressDialog progressDialog = Utils.createProgressDialog(getContext());
        db.collection("Event")
                .whereGreaterThanOrEqualTo("startDate", today)
                .whereLessThanOrEqualTo("startDate", weekDate)
                .orderBy("startDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.getDocumentReference("browseEvent") != null) {
                                    DocumentReference browseEventReference = document.getDocumentReference("browseEvent");
                                    db.collection("BrowseEvent").document(browseEventReference.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                            BrowseEvent browseEvent = new BrowseEvent(document);

                                                            boolean flag = false;
                                                            for (BrowseEvent event : browseEventList) {
                                                                if (event.getDocumentId().equals(browseEvent.getDocumentId())) {
                                                                    flag = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (flag == false) {
                                                                BrowseFragment.this.browseEventList.add(browseEvent);
                                                                BrowseFragment.this.adapter.refresh(browseEventList, null, null, Type.EVENT_TYPE);
                                                            }

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void getBrowseVenues() {
        Date today = getTodayFormat();

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date weekDate = cal.getTime();

        this.browseVenueList.clear();
        final ProgressDialog progressDialog = Utils.createProgressDialog(getContext());
        db.collection("Event")
                .whereGreaterThanOrEqualTo("startDate", today)
                .whereLessThanOrEqualTo("startDate", weekDate)
                .orderBy("startDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.getDocumentReference("browseEvent") != null) {
                                    DocumentReference browseVenueReference = document.getDocumentReference("browseEvent");
                                    db.collection("BrowseEvent").document(browseVenueReference.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                            BrowseEvent browseVenue = new BrowseEvent(document);

                                                            boolean flag = false;
                                                            for (BrowseEvent venue : browseVenueList) {
                                                                if (venue.getDocumentId().equals(browseVenue.getDocumentId())) {
                                                                    flag = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (flag == false) {
                                                                BrowseFragment.this.browseVenueList.add(browseVenue);
                                                                BrowseFragment.this.adapter.refresh(null, browseVenueList, null, Type.VENUE_TYPE);
                                                            }

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void getTypeData() {
        this.browseVenueTYpeList.clear();
        final ProgressDialog progressDialog = Utils.createProgressDialog(getContext());
        db.collection("Venue")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if (document.contains("browseVenueType") ) {
                                    if ( document.getDocumentReference("browseVenueType") != null) {
                                        final ProgressDialog dialog = Utils.createProgressDialog(getContext());
                                        DocumentReference browseEventReference = document.getDocumentReference("browseVenueType");
                                        db.collection("BrowseVenueType").document(browseEventReference.getId()).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        dialog.dismiss();
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document != null) {
                                                                Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                                BrowseVenueType browseVenueType = new BrowseVenueType(document);

                                                                boolean flag = false;
                                                                for (BrowseVenueType venueType : browseVenueTYpeList) {
                                                                    if (venueType.getDocumentId().equals(browseVenueType.getDocumentId())) {
                                                                        flag = true;
                                                                        break;
                                                                    }
                                                                }
                                                                if (flag == false) {
                                                                    BrowseFragment.this.browseVenueTYpeList.add(browseVenueType);
                                                                    BrowseFragment.this.adapter.refresh(null, null, browseVenueTYpeList, Type.TYPE_TYPE);
                                                                }

                                                            } else {
                                                                Log.d(TAG, "No such document");
                                                            }
                                                        } else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getBrowseEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        tv_event = (TextView) view.findViewById(R.id.tv_event);
        tv_venue = (TextView) view.findViewById(R.id.tv_venue);
        tv_type = (TextView) view.findViewById(R.id.tv_type);
        tv_event.setOnClickListener(this);
        tv_venue.setOnClickListener(this);
        tv_type.setOnClickListener(this);

        this.type = Type.EVENT_TYPE;

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new BrowseRecyclerAdapter(mContext, browseEventList, this.type);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener(new OnSwipeTouchListener(mContext) {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onSwipeRight() {
                int index = type.getTypeCode() - 1;
                if (index < 0)
                    return;
                type = Type.values()[index];
                setActiveData(type.getTypeCode());
                if (type == Type.EVENT_TYPE)
                    getBrowseEvents();
                if (type == Type.VENUE_TYPE) {
                    getBrowseVenues();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onSwipeLeft() {
                int index = type.getTypeCode() + 1;
                if (index > 2)
                    return;
                type = Type.values()[index];
                setActiveData(type.getTypeCode());
                if (type == Type.VENUE_TYPE) {
                    getBrowseVenues();
                }
                if (type == Type.TYPE_TYPE)
                    getTypeData();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setActiveData(int index) {
        ArrayList<TextView> tvArrays = new ArrayList<TextView>();
        tvArrays.add(tv_event);
        tvArrays.add(tv_venue);
        tvArrays.add(tv_type);
        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        for (int i=0; i < tvArrays.size(); i++) {
            TextView textView = tvArrays.get(i);
            if (i == index) {
                textView.setBackground(mContext.getDrawable(R.drawable.rounded_corner));
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.White));
            } else {
                textView.setBackground(transparentDrawable);
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_event) {
            this.type = Type.EVENT_TYPE;
            this.setActiveData(this.type.getTypeCode());
            getBrowseEvents();
        } else if (view.getId() == R.id.tv_venue) {
            this.type = Type.VENUE_TYPE;
            this.setActiveData(this.type.getTypeCode());
            getBrowseVenues();
        } else if (view.getId() == R.id.tv_type) {
            this.type = Type.TYPE_TYPE;
            this.setActiveData(this.type.getTypeCode());
            getTypeData();
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
