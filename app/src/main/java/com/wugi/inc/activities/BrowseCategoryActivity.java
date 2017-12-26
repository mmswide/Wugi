package com.wugi.inc.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.wugi.inc.R;
import com.wugi.inc.adapters.BrowseCategoryRecyclerAdapter;
import com.wugi.inc.models.BrowseEvent;
import com.wugi.inc.models.BrowseVenue;
import com.wugi.inc.models.BrowseVenueType;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Type;
import com.wugi.inc.models.Venue;
import com.wugi.inc.utils.Utils;
import com.wugi.inc.views.MarginDecoration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;

public class BrowseCategoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    private RecyclerView recyclerView;
    private BrowseCategoryRecyclerAdapter adapter;
    private Context mContext;
    private ArrayList<Event> eventList = new ArrayList<Event>();
    private ArrayList<Event> venueEventList = new ArrayList<Event>();
    private ArrayList<Venue> venueTypeList = new ArrayList<Venue>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Type type;
    private BrowseEvent browseEvent;
    private BrowseVenue browseVenue;
    private BrowseVenueType venueType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_category);

        mContext = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Bundle extras = getIntent().getExtras();
        this.type = Type.values()[extras.getInt("type")];

        if (this.type == Type.EVENT_TYPE) {
            String jsonEventString = extras.getString("event_type");
            Gson gson = new Gson();
            this.browseEvent = gson.fromJson(jsonEventString, BrowseEvent.class);
        } else if (this.type == Type.VENUE_TYPE) {
            String jsonVenueString = extras.getString("event_type");
            Gson gson = new Gson();
            this.browseVenue = gson.fromJson(jsonVenueString, BrowseVenue.class);
        } else if (this.type == Type.TYPE_TYPE) {
            String jsonTypeString = extras.getString("event_type");
            Gson gson = new Gson();
            this.venueType = gson.fromJson(jsonTypeString, BrowseVenueType.class);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new BrowseCategoryRecyclerAdapter(mContext, eventList, this.type);
        adapter.setHeader(this.browseEvent, this.browseVenue, this.venueType);

        final GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, (int) Utils.convertDpToPixel(0), true));
        recyclerView.addItemDecoration(new MarginDecoration(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isPositionHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });

        if (this.type == Type.EVENT_TYPE) {
            getEvents();
        } else if (this.type == Type.VENUE_TYPE) {
            getVenues();
        } else if (this.type == Type.TYPE_TYPE) {
            getVenueTypes();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private void getEvents() {
        Date today = getTodayFormat();

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date weekDate = cal.getTime();

        eventList.clear();
        final ProgressDialog progressDialog = Utils.createProgressDialog(this);

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
                                                            BrowseEvent localBrowseEvent = new BrowseEvent(document);
                                                            event.setBrowseEvent(localBrowseEvent);

                                                            if (type == Type.EVENT_TYPE && event.getBrowseEvent().getDocumentId().equals(browseEvent.getDocumentId())) {
                                                                eventList.add(event);
                                                                adapter.refresh(eventList, null, null, type);
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

    private void getVenues() {
        Date today = getTodayFormat();

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_YEAR, 7);
        Date weekDate = cal.getTime();

        this.venueEventList.clear();
        final ProgressDialog progressDialog = Utils.createProgressDialog(this);

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

                                if (document.getDocumentReference("browseVenue") != null) {
                                    DocumentReference browseVenueReference = document.getDocumentReference("browseVenue");
                                    db.collection("BrowseVenue").document(browseVenueReference.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                            BrowseVenue localBrowseVenue = new BrowseVenue(document);
                                                            event.setBrowseVenue(localBrowseVenue);

                                                            if (type == Type.VENUE_TYPE && event.getBrowseVenue().getDocumentId().equals(browseVenue.getDocumentId())) {
                                                                boolean flag = false;
                                                                for (Event localEvent : venueEventList) {
                                                                    if (localEvent.getVenue().getDocumentId().equals(event.getVenue().getDocumentId())) {
                                                                        flag = true;
                                                                        break;
                                                                    }
                                                                }
                                                                if (flag == false) {
                                                                    venueEventList.add(event);
                                                                    adapter.refresh(null, venueEventList, null, type);
                                                                }
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

    private void getVenueTypes() {
        this.venueTypeList.clear();
        final ProgressDialog progressDialog = Utils.createProgressDialog(this);
        db.collection("Venue")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                final Venue venue = new Venue(document);

                                if (document.getDocumentReference("browseVenueType") != null) {
                                    DocumentReference browseEventReference = document.getDocumentReference("browseVenueType");
                                    db.collection("BrowseVenueType").document(browseEventReference.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                            BrowseVenueType browseVenueType = new BrowseVenueType(document);
                                                            venue.setBrowseVenueType(browseVenueType);

                                                            if (browseVenueType.getDocumentId().equals(venueType.getDocumentId())) {
                                                                venueTypeList.add(venue);
                                                                adapter.refresh(null, null, venueTypeList, type);
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
}
