package com.wugi.inc.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.wugi.inc.R;
import com.wugi.inc.adapters.PhotoRecyclerAdapter;
import com.wugi.inc.adapters.SearchRecyclerAdapter;
import com.wugi.inc.fragments.BrowseFragment;
import com.wugi.inc.fragments.HomeFragment;
import com.wugi.inc.models.BrowseEvent;
import com.wugi.inc.models.BrowseVenueType;
import com.wugi.inc.models.DressCodeType;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Neighborhood;
import com.wugi.inc.models.SearchType;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;

import static android.content.ContentValues.TAG;

public class SearchActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.segmented_group)
    SegmentedGroup segmentedGroup;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String query;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SearchRecyclerAdapter adapter;
    private Context mContext;
    private SearchType type;
    private int selectedId;

    private ArrayList<Event> eventList = new ArrayList<Event>();
    private ArrayList<Venue> venueList = new ArrayList<Venue>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle extras = getIntent().getExtras();
        this.query = extras.getString("search");

        ButterKnife.bind(this);

        mContext = this;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        this.type = SearchType.EVENT_TYPE;

        segmentedGroup.setOnCheckedChangeListener(this);
        segmentedGroup.check(R.id.btn_event);

        adapter = new SearchRecyclerAdapter(mContext, eventList, venueList, this.type);

        final GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, (int) Utils.convertDpToPixel(0), true));
        recyclerView.addItemDecoration(new MarginDecoration(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        if (selectedId == checkedId)
            return;
        selectedId = checkedId;
        switch (checkedId) {
            case R.id.btn_event:
                getEvents();
                break;
            case R.id.btn_venue:
                getVenueData();
                break;
            default:
                break;
        }
    }

    private Date getTodayFormat() {
//        String dateString = "03/26/2012 11:49:00 AM";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String todayStr = dateFormat.format(today);
        todayStr = todayStr + " 04:00:00 +0000";

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

        this.eventList.clear();
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

                                if (!(event.getName().toLowerCase()).contains(query.toLowerCase())) {
                                    continue;
                                }

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
                                                            final Venue venue = new Venue(document);
                                                            event.setVenue(venue);

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
                                                                                    } else {
                                                                                        Log.d(TAG, "No such document");
                                                                                    }
                                                                                } else {
                                                                                    Log.d(TAG, "get failed with ", task.getException());
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                            if (document.getDocumentReference("neighborhood") != null) {
                                                                DocumentReference neighborhoodReference = document.getDocumentReference("neighborhood");
                                                                db.collection("Neighborhood").document(neighborhoodReference.getId()).get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    DocumentSnapshot document = task.getResult();
                                                                                    if (document != null) {
                                                                                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                                                        Neighborhood neighborhood = new Neighborhood(document);
                                                                                        venue.setNeighborhood(neighborhood);

                                                                                    } else {
                                                                                        Log.d(TAG, "No such document");
                                                                                    }
                                                                                } else {
                                                                                    Log.d(TAG, "get failed with ", task.getException());
                                                                                }
                                                                            }
                                                                        });
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

                                if (document.getDocumentReference("dressCodeType") != null) {
                                    DocumentReference dressCodeTypeReference = document.getDocumentReference("dressCodeType");
                                    db.collection("DressCodeType").document(dressCodeTypeReference.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                            DressCodeType dressCodeType = new DressCodeType(document);
                                                            event.setDressCodeType(dressCodeType);

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }

                                eventList.add(event);
                            }
                            SearchActivity.this.adapter.refresh(eventList, null, SearchType.EVENT_TYPE);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void getVenueData() {
        this.venueList.clear();
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

                                if (!(venue.getName().toLowerCase()).contains(query.toLowerCase())) {
                                    continue;
                                }

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
                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                                if (document.getDocumentReference("neighborhood") != null) {
                                    DocumentReference neighborhoodReference = document.getDocumentReference("neighborhood");
                                    db.collection("Neighborhood").document(neighborhoodReference.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                            Neighborhood neighborhood = new Neighborhood(document);
                                                            venue.setNeighborhood(neighborhood);

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                                venueList.add(venue);
                            }
                            SearchActivity.this.adapter.refresh(null, venueList, SearchType.VENUE_TYPE);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
