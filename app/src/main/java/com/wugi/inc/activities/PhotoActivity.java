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
import com.wugi.inc.adapters.PhotoRecyclerAdapter;
import com.wugi.inc.models.BrowseEvent;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Gallery;
import com.wugi.inc.models.Photo;
import com.wugi.inc.models.Type;
import com.wugi.inc.models.Venue;
import com.wugi.inc.utils.Utils;
import com.wugi.inc.views.GridSpacingItemDecoration;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class PhotoActivity extends AppCompatActivity {

    Toolbar toolbar;
    private RecyclerView recyclerView;
    private PhotoRecyclerAdapter adapter;
    private Context mContext;

    private ArrayList<Photo> photoList = new ArrayList<Photo>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Gallery gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mContext = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Bundle extras = getIntent().getExtras();
        String jsonGalleryString = extras.getString("gallery");
        Gson gson = new Gson();
        this.gallery = gson.fromJson(jsonGalleryString, Gallery.class);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new PhotoRecyclerAdapter(mContext, photoList, this.gallery);

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

        this.getPhotos();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getPhotos() {
        final ProgressDialog progressDialog = Utils.createProgressDialog(this);

        db.collection("Photos")
                .whereEqualTo("active", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                final Photo photo = new Photo(document);

                                if (document.getDocumentReference("gallery") != null) {
                                    final DocumentReference galleryReference = document.getDocumentReference("gallery");
                                    Log.d(TAG, galleryReference.getId() + " =>*********** ");
                                    Log.d(TAG, gallery.getDocumentID() + " =>++++++++ ");
                                    Log.d(TAG, gallery.getDocumentID() + " =>++++++++ " + (gallery.getDocumentID().equals(galleryReference.getId())));
                                    if (!gallery.getDocumentID().equals(galleryReference.getId()) ) {
                                        return;
                                    }
                                    db.collection("Gallery").document(galleryReference.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document != null) {
                                                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());

                                                            Gallery gallery = new Gallery(document);
                                                            photo.setGallery(gallery);

                                                        } else {
                                                            Log.d(TAG, "No such document");
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                                PhotoActivity.this.photoList.add(photo);
                            }
                            PhotoActivity.this.adapter.refresh(photoList);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}
