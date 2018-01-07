package com.wugi.inc.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.adapters.GalleryViewHolder;
import com.wugi.inc.fragments.GalleryFragment;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Gallery;
import com.wugi.inc.models.Venue;
import com.wugi.inc.utils.Utils;
import com.wugi.inc.views.SquareImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class VenueDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.iv_venue)        CircleImageView iv_venue;
    @BindView(R.id.tv_venue_name)   TextView tv_venue_name;
    @BindView(R.id.tv_address)      TextView tv_address;
    @BindView(R.id.tv_distance)     TextView tv_distance;
    @BindView(R.id.ll_call)         LinearLayout ll_call;
    @BindView(R.id.ll_site)         LinearLayout ll_site;
    @BindView(R.id.ll_map)          LinearLayout ll_map;
    @BindView(R.id.tv_venue_type)   TextView tv_venue_type;
    @BindView(R.id.tv_neighbor)     TextView tv_neighbor;
    @BindView(R.id.tv_parking)      TextView tv_parking;
    @BindView(R.id.rl_gallery)      LinearLayout rl_gallery;
    @BindView(R.id.tv_photo_num)    TextView tv_photo_num;
    @BindView(R.id.card_view1)      CardView cardView1;
    @BindView(R.id.card_view2)      CardView cardView2;
    @BindView(R.id.card_view3)      CardView cardView3;
    @BindView(R.id.card_view4)      CardView cardView4;
    @BindView(R.id.thumbnail1)      SquareImageView thumbnail1;
    @BindView(R.id.thumbnail2)      SquareImageView thumbnail2;
    @BindView(R.id.thumbnail3)      SquareImageView thumbnail3;

    Venue venue;
    private static final int REQUEST_PHONE_CALL = 1;

    SharedPreferences sharedpreferences;
    Double latitude;
    Double longitude;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Gallery> galleryList = new ArrayList<Gallery>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        sharedpreferences = getSharedPreferences(MainActivity.wugiPreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(MainActivity.Latitude)) {
            this.latitude = Double.longBitsToDouble(sharedpreferences.getLong(MainActivity.Latitude, 0));
        } else {
            this.latitude = 0.0;
        }

        if (sharedpreferences.contains(MainActivity.Longitude)) {
            this.longitude = Double.longBitsToDouble(sharedpreferences.getLong(MainActivity.Longitude, 0));
        } else {
            this.longitude = 0.0;
        }

        Bundle extras = getIntent().getExtras();
        String jsonVenueString = extras.getString("venue");
        Gson gson = new Gson();
        this.venue = gson.fromJson(jsonVenueString, Venue.class);

        getGalleries();

        this.setupUI();

        showGallery();
    }

    private void getGalleries() {
        final ProgressDialog progressDialog = Utils.createProgressDialog(this);
        db.collection("Gallery")
                .whereEqualTo("active", true)
                .orderBy("eventDate", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                final Gallery gallery = new Gallery(document);
                                if (document.getDocumentReference("venueid") != null) {
                                    DocumentReference venueReference = document.getDocumentReference("venueid");

                                    if (venueReference.getId().equals(VenueDetailActivity.this.venue.getDocumentId())) {
                                        db.collection("Venue").document(venueReference.getId()).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document != null) {
                                                                Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                                                                Venue venue = new Venue(document);
                                                                gallery.setVenue(venue);

                                                            } else {
                                                                Log.d(TAG, "No such document");
                                                            }
                                                        } else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                        VenueDetailActivity.this.galleryList.add(gallery);
                                        rl_gallery.setVisibility(View.VISIBLE);
                                        tv_photo_num.setText(String.valueOf(VenueDetailActivity.this.galleryList.size()));

                                        showGallery();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    void setupUI() {
        Picasso.with(this).load(venue.getImageThumbURL()).into(iv_venue);
        tv_venue_name.setText(venue.getName());

        ArrayList<String> addrStrs = new ArrayList<String>();
        for (String addr : venue.getAddress()) {
            addrStrs.add(addr);
        }
        tv_address.setText(TextUtils.join("\n", addrStrs));

        double distance = Utils.distance(this.latitude,
                this.longitude,
                this.venue.getGeolocation().getLatitude(),
                this.venue.getGeolocation().getLongitude(), "M");
        tv_distance.setText(String.format("%.02f miles", distance));

        String dotStr = "<font color=#438c7e size=32>• </font>";
        tv_venue_type.setText(Html.fromHtml(dotStr + venue.getType()));
        tv_neighbor.setText(Html.fromHtml(dotStr + venue.getNeighborhood()));

        ArrayList<String> parkingStrs = new ArrayList<String>();
        for (int i=0;i<venue.getParking().size();i++) {
            String parking = venue.getParking().get(i);
            if (i != venue.getParking().size()-1) {
                parkingStrs.add(dotStr + parking + "<br>");
            } else {
                parkingStrs.add(dotStr + parking);
            }
        }

        tv_parking.setText(Html.fromHtml(TextUtils.join("\n", parkingStrs)));

        if (this.galleryList.size() == 0) {
            rl_gallery.setVisibility(View.GONE);
        } else {
            if (this.galleryList.size() > 0) {
                rl_gallery.setVisibility(View.VISIBLE);
                tv_photo_num.setText(String.valueOf(this.galleryList.size()));
            } else {
                rl_gallery.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.ll_call)
    void onCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String phoneNum = "tel:"+ this.venue.getPhoneNumber();
        callIntent.setData(Uri.parse(phoneNum));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(VenueDetailActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(VenueDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            }        else
            {
                startActivity(callIntent);
            }
        } else {
            startActivity(callIntent);
        }

    }

    @OnClick(R.id.ll_site)
    void onOpenWeb() {
        String url = this.venue.getSiteURL();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.ll_map)
    void onOpenMap() {
        String url = String.format("http://maps.google.com/maps?saddr=%.06f&daddr=%.06f",
                this.venue.getGeolocation().getLatitude(),
                this.venue.getGeolocation().getLongitude());
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);
    }

    @OnClick(R.id.card_view4)
    void showPhotos() {
        Intent intent = new Intent(VenueDetailActivity.this, GalleryActivity.class);
        Gson gson = new Gson();
        String jsonGalleryListString = gson.toJson(this.galleryList);
        intent.putExtra("galleryList", jsonGalleryListString);
        startActivity(intent);
    }

    void showGallery() {
        int size = this.galleryList.size();
        if (size > 3) {
            cardView4.setVisibility(View.VISIBLE);

            final Gallery gallery1 = this.galleryList.get(0);
            Picasso.with(this).load(gallery1.getCover()).into(thumbnail1);

            thumbnail1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                    Gson gson = new Gson();
                    String jsonGalleryString = gson.toJson(gallery1);
                    intent.putExtra("gallery", jsonGalleryString);
                    VenueDetailActivity.this.startActivity(intent);
                }
            });

            final Gallery gallery2 = this.galleryList.get(0);
            Picasso.with(this).load(gallery2.getCover()).into(thumbnail2);

            thumbnail2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                    Gson gson = new Gson();
                    String jsonGalleryString = gson.toJson(gallery2);
                    intent.putExtra("gallery", jsonGalleryString);
                    VenueDetailActivity.this.startActivity(intent);
                }
            });

            final Gallery gallery3 = this.galleryList.get(0);
            Picasso.with(this).load(gallery3.getCover()).into(thumbnail3);

            thumbnail3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                    Gson gson = new Gson();
                    String jsonGalleryString = gson.toJson(gallery3);
                    intent.putExtra("gallery", jsonGalleryString);
                    VenueDetailActivity.this.startActivity(intent);
                }
            });
        } else {

            switch (size) {
                case 0:
                    cardView1.setVisibility(View.GONE);
                    cardView2.setVisibility(View.GONE);
                    cardView3.setVisibility(View.GONE);
                    cardView4.setVisibility(View.GONE);
                    break;
                case 1:
                    cardView1.setVisibility(View.VISIBLE);
                    cardView2.setVisibility(View.GONE);
                    cardView3.setVisibility(View.GONE);
                    cardView4.setVisibility(View.GONE);

                    final Gallery gallery1 = this.galleryList.get(0);
                    Picasso.with(this).load(gallery1.getCover()).into(thumbnail1);

                    thumbnail1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                            Gson gson = new Gson();
                            String jsonGalleryString = gson.toJson(gallery1);
                            intent.putExtra("gallery", jsonGalleryString);
                            VenueDetailActivity.this.startActivity(intent);
                        }
                    });
                    break;
                case 2:
                    cardView1.setVisibility(View.VISIBLE);
                    cardView2.setVisibility(View.VISIBLE);
                    cardView3.setVisibility(View.GONE);
                    cardView4.setVisibility(View.INVISIBLE);

                    final Gallery gallery_21 = this.galleryList.get(0);
                    Picasso.with(this).load(gallery_21.getCover()).into(thumbnail1);
                    thumbnail1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                            Gson gson = new Gson();
                            String jsonGalleryString = gson.toJson(gallery_21);
                            intent.putExtra("gallery", jsonGalleryString);
                            VenueDetailActivity.this.startActivity(intent);
                        }
                    });

                    final Gallery gallery_22 = this.galleryList.get(1);
                    Picasso.with(this).load(gallery_22.getCover()).into(thumbnail2);

                    thumbnail2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                            Gson gson = new Gson();
                            String jsonGalleryString = gson.toJson(gallery_22);
                            intent.putExtra("gallery", jsonGalleryString);
                            VenueDetailActivity.this.startActivity(intent);
                        }
                    });

                    break;
                case 3:
                    cardView1.setVisibility(View.VISIBLE);
                    cardView2.setVisibility(View.VISIBLE);
                    cardView3.setVisibility(View.VISIBLE);
                    cardView4.setVisibility(View.INVISIBLE);

                    final Gallery gallery_31 = this.galleryList.get(0);
                    Picasso.with(this).load(gallery_31.getCover()).into(thumbnail1);
                    thumbnail1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                            Gson gson = new Gson();
                            String jsonGalleryString = gson.toJson(gallery_31);
                            intent.putExtra("gallery", jsonGalleryString);
                            VenueDetailActivity.this.startActivity(intent);
                        }
                    });

                    final Gallery gallery_32 = this.galleryList.get(1);
                    Picasso.with(this).load(gallery_32.getCover()).into(thumbnail2);

                    thumbnail2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                            Gson gson = new Gson();
                            String jsonGalleryString = gson.toJson(gallery_32);
                            intent.putExtra("gallery", jsonGalleryString);
                            VenueDetailActivity.this.startActivity(intent);
                        }
                    });

                    final Gallery gallery_33 = this.galleryList.get(2);
                    Picasso.with(this).load(gallery_33.getCover()).into(thumbnail3);

                    thumbnail3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(VenueDetailActivity.this, PhotoActivity.class);
                            Gson gson = new Gson();
                            String jsonGalleryString = gson.toJson(gallery_33 );
                            intent.putExtra("gallery", jsonGalleryString);
                            VenueDetailActivity.this.startActivity(intent);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }
}
