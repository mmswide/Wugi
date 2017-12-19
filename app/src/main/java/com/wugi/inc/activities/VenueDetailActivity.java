package com.wugi.inc.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.models.Event;
import com.wugi.inc.models.Venue;
import com.wugi.inc.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class VenueDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)         Toolbar toolbar;
    @BindView(R.id.iv_venue)        CircleImageView iv_venue;
    @BindView(R.id.tv_venue_name)   TextView tv_venue_name;
    @BindView(R.id.tv_address)      TextView tv_address;
    @BindView(R.id.tv_distance)     TextView tv_distance;
    @BindView(R.id.ll_call)         LinearLayout ll_call;
    @BindView(R.id.ll_site)         LinearLayout ll_site;
    @BindView(R.id.ll_map)          LinearLayout ll_map;
    @BindView(R.id.tv_venue_type)  TextView tv_venue_type;
    @BindView(R.id.tv_neighbor)        TextView tv_neighbor;
    @BindView(R.id.tv_parking)   TextView tv_parking;

    Venue venue;
    private static final int REQUEST_PHONE_CALL = 1;

    SharedPreferences sharedpreferences;
    Double latitude;
    Double longitude;

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

        this.setupUI();
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
        tv_distance.setText(String.format("📍 %.03f miles", distance));

        String dotStr = "• ";
        tv_venue_type.setText(dotStr + venue.getType());
        tv_neighbor.setText(dotStr + venue.getNeighborhood());

        ArrayList<String> parkingStrs = new ArrayList<String>();
        for (String parking : venue.getParking()) {
            parkingStrs.add(dotStr + parking);
        }

        tv_parking.setText(TextUtils.join("\n", parkingStrs));
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
}