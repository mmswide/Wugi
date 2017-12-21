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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.wugi.inc.R;
import com.wugi.inc.adapters.UpcomingRecyclerAdapter;
import com.wugi.inc.models.BrowseEvent;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Venue;
import com.wugi.inc.utils.OnSwipeTouchListener;
import com.wugi.inc.utils.Utils;
import com.wugi.inc.views.GridSpacingItemDecoration;
import com.wugi.inc.views.MarginDecoration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpcomingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpcomingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpcomingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private UpcomingRecyclerAdapter adapter;
    private Context mContext;
    private ArrayList<Event> eventList = new ArrayList<Event>();
    ArrayList<LinearLayout> llArrays = new ArrayList<LinearLayout>();

    private ArrayList<Date> daysList = new ArrayList<Date>();
    ArrayList<Event> filtered = new ArrayList<Event>();
    private int selectedIndex = 0;
    private Date activeDate;

    TextView tv_first, tv_firstNum;
    TextView tv_second, tv_secondNum;
    TextView tv_third, tv_thirdNum;
    TextView tv_forth, tv_forthNum;
    TextView tv_fifth, tv_fifthNum;
    TextView tv_sixth, tv_sixthNum;
    TextView tv_seventh, tv_seventhNum;
    LinearLayout ll_first, ll_second, ll_third, ll_forth, ll_fifth, ll_sixth, ll_seventh;

    private void getEvents() {
        Date today = getTodayFormat();

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date weekDate = cal.getTime();

        this.eventList.clear();
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
                                final Event event = new Event(document);

                                if (document.getDocumentReference("venue") != null) {
                                    DocumentReference venueReference = document.getDocumentReference("venue");
                                    db.collection("Venue").document(venueReference.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                            Venue venue = new Venue(document);
                                                            event.setVenue(venue);

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }

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
                                                            event.setBrowseEvent(browseEvent);

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }

                                UpcomingFragment.this.eventList.add(event);
                            }
                            getActiveData();
                            UpcomingFragment.this.adapter.refresh(filtered);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private Date getTodayFormat() {
//        String dateString = "03/26/2012 11:49:00 AM";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String todayStr = dateFormat.format(today);

        todayStr = "2017-12-01" + " 04:00:00 +0000";

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

    private void getNextSevenDays() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String todayStr = sdf.format(today);

        todayStr = "2017-12-01" + " 07:00:00 +0000";
        Date convertedDate = new Date();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZZZ");

        try {
            convertedDate = outputFormat.parse(todayStr);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 1; i <= 7; i++) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(convertedDate);
            cal.add(Calendar.DAY_OF_MONTH, i); //Adds a day

            daysList.add(cal.getTime());
        }
    }

    private Date getNextDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1); //Adds a day

        return cal.getTime();
    }

    private void getActiveData() {
        this.filtered.clear();
        for (Event event : this.eventList) {
            Date nextDate = this.getNextDate(this.activeDate);
            if (event.getStartDate().after(this.activeDate) && event.getStartDate().before(nextDate)) {
                filtered.add(event);
            }
        }
    }

    public UpcomingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpcomingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpcomingFragment newInstance(String param1, String param2) {
        UpcomingFragment fragment = new UpcomingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    void setActiveDate(int index) {
        activeDate = this.daysList.get(index);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setActiveData(int index) {
        ArrayList<TextView> tvArrays = new ArrayList<TextView>();
        tvArrays.add(tv_firstNum);
        tvArrays.add(tv_secondNum);
        tvArrays.add(tv_thirdNum);
        tvArrays.add(tv_forthNum);
        tvArrays.add(tv_fifthNum);
        tvArrays.add(tv_sixthNum);
        tvArrays.add(tv_seventhNum);
        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        for (int i=0; i < tvArrays.size(); i++) {
            TextView textView = tvArrays.get(i);
            if (i == index) {
                textView.setBackground(mContext.getDrawable(R.drawable.circle_shape));
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.White));
            } else {
                textView.setBackground(transparentDrawable);
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getNextSevenDays();

        selectedIndex = 0;
        setActiveDate(selectedIndex);
        getEvents();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getSelectedData(int index) {
        selectedIndex = index;
        setActiveDate(selectedIndex);

        getActiveData();
        UpcomingFragment.this.adapter.refresh(filtered);
    }

    private String getDayOfWeek(Date d) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }
    private String getDayFromDate(Date date) {
        String dayStr = (String) DateFormat.format("dd",   date);
        return dayStr;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        tv_first = (TextView) view.findViewById(R.id.tv_first);
        tv_firstNum = (TextView) view.findViewById(R.id.tv_firstNum);
        tv_second = (TextView) view.findViewById(R.id.tv_second);
        tv_secondNum = (TextView) view.findViewById(R.id.tv_secondNum);
        tv_third = (TextView) view.findViewById(R.id.tv_third);
        tv_thirdNum = (TextView) view.findViewById(R.id.tv_thirdNum);
        tv_forth = (TextView) view.findViewById(R.id.tv_fourth);
        tv_forthNum = (TextView) view.findViewById(R.id.tv_fourthNum);
        tv_fifth = (TextView) view.findViewById(R.id.tv_fifth);
        tv_fifthNum = (TextView) view.findViewById(R.id.tv_fifthNum);
        tv_sixth = (TextView) view.findViewById(R.id.tv_sixth);
        tv_sixthNum = (TextView) view.findViewById(R.id.tv_sixthNum);
        tv_seventh = (TextView) view.findViewById(R.id.tv_seventh);
        tv_seventhNum = (TextView) view.findViewById(R.id.tv_seventhNum);

        ll_first = (LinearLayout) view.findViewById(R.id.ll_first);
        ll_second = (LinearLayout) view.findViewById(R.id.ll_second);
        ll_third = (LinearLayout) view.findViewById(R.id.ll_third);
        ll_forth = (LinearLayout) view.findViewById(R.id.ll_forth);
        ll_fifth = (LinearLayout) view.findViewById(R.id.ll_fifth);
        ll_sixth = (LinearLayout) view.findViewById(R.id.ll_sixth);
        ll_seventh = (LinearLayout) view.findViewById(R.id.ll_seventh);


        tv_first.setText(getDayOfWeek(this.daysList.get(0)));
        tv_second.setText(getDayOfWeek(this.daysList.get(1)));
        tv_third.setText(getDayOfWeek(this.daysList.get(2)));
        tv_forth.setText(getDayOfWeek(this.daysList.get(3)));
        tv_fifth.setText(getDayOfWeek(this.daysList.get(4)));
        tv_sixth.setText(getDayOfWeek(this.daysList.get(5)));
        tv_seventh.setText(getDayOfWeek(this.daysList.get(6)));

        tv_firstNum.setText(getDayFromDate(this.daysList.get(0)));
        tv_secondNum.setText(getDayFromDate(this.daysList.get(1)));
        tv_thirdNum.setText(getDayFromDate(this.daysList.get(2)));
        tv_forthNum.setText(getDayFromDate(this.daysList.get(3)));
        tv_fifthNum.setText(getDayFromDate(this.daysList.get(4)));
        tv_sixthNum.setText(getDayFromDate(this.daysList.get(5)));
        tv_seventhNum.setText(getDayFromDate(this.daysList.get(6)));


        llArrays.add(ll_first);
        llArrays.add(ll_second);
        llArrays.add(ll_third);
        llArrays.add(ll_forth);
        llArrays.add(ll_fifth);
        llArrays.add(ll_sixth);
        llArrays.add(ll_seventh);

        setActiveData(0);

        for (int i=0; i < llArrays.size(); i++) {
            LinearLayout layout = llArrays.get(i);
            final int index = i;
            layout.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    setActiveData(index);
                    getSelectedData(index);
                }
            });
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new UpcomingRecyclerAdapter(mContext, filtered);

        final GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, (int) Utils.convertDpToPixel(10), true));
        recyclerView.addItemDecoration(new MarginDecoration(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.setOnTouchListener(new OnSwipeTouchListener(mContext) {
            public void onSwipeRight() {
                int index = selectedIndex + 1;
                if (index > 6)
                    return;
                setActiveData(index);
                getSelectedData(index);
            }
            public void onSwipeLeft() {
                int index = selectedIndex - 1;
                if (index < 0)
                    return;
                setActiveData(index);
                getSelectedData(index);
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
