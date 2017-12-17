package com.wugi.inc.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.wugi.inc.R;
import com.wugi.inc.adapters.UpcomingRecyclerAdapter;
import com.wugi.inc.models.Event;
import com.wugi.inc.utils.Utils;
import com.wugi.inc.views.GridSpacingItemDecoration;

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

    private ArrayList<Date> daysList = new ArrayList<Date>();
    ArrayList<Event> filtered = new ArrayList<Event>();
    private int selectedIndex = 0;
    private Date activeDate;

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
                                Event event = new Event(document);

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

        todayStr = "2017-12-01" + " 04:00:00 +0000";
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
            Date nextDate = this.getNextDate(event.getStartDate());
            if (event.getStartDate().after(this.activeDate) && nextDate.before(this.activeDate)) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        adapter = new UpcomingRecyclerAdapter(mContext, filtered);

        final GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, (int) Utils.convertDpToPixel(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isPositionHeader(position) ? layoutManager.getSpanCount() : 1;
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
