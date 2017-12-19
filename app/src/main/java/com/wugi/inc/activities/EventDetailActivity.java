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
import com.wugi.inc.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;



public class EventDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.iv_event)
    ImageView iv_event;
    @BindView(R.id.iv_venue)
    CircleImageView iv_venue;
    @BindView(R.id.tv_event_name)
    TextView tv_event_name;
    @BindView(R.id.tv_event_day)
    TextView tv_event_day;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_distance)
    TextView tv_distance;
    @BindView(R.id.ll_call)
    LinearLayout ll_call;
    @BindView(R.id.ll_site)
    LinearLayout ll_site;
    @BindView(R.id.ll_map)
    LinearLayout ll_map;
    @BindView(R.id.tv_description)
    TextView tv_description;
    @BindView(R.id.tv_theme)
    TextView tv_theme;
    @BindView(R.id.tv_dress_code)
    TextView tv_dress_code;
    @BindView(R.id.tv_age)
    TextView tv_age;

    private Event event;
    private static final int REQUEST_PHONE_CALL = 1;
    SharedPreferences sharedpreferences;
    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

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
        String jsonEventString = extras.getString("event");
        Gson gson = new Gson();
        this.event = gson.fromJson(jsonEventString, Event.class);

        this.setupUI();
    }

    void setupUI() {
        Picasso.with(this).load(event.getImageURL()).into(iv_event);
        Picasso.with(this).load(event.getVenue().getImageThumbURL()).into(iv_venue);
        tv_event_name.setText(event.getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd yyyy");
        String startDateStr = dateFormat.format(event.getStartDate());
        tv_event_day.setText(startDateStr);

        ArrayList<String> addrStrs = new ArrayList<String>();
        for (String addr : event.getVenue().getAddress()) {
            addrStrs.add(addr);
        }
        tv_address.setText(TextUtils.join("\n", addrStrs));

        double distance = Utils.distance(this.latitude,
                this.longitude,
                this.event.getVenue().getGeolocation().getLatitude(),
                this.event.getVenue().getGeolocation().getLongitude(), "M");
        tv_distance.setText(String.format("📍 %.03f miles", distance));

        String dotStr = "• ";
        tv_description.setText(dotStr + event.getDescription());
        tv_theme.setText(dotStr + event.getTheme());

        ArrayList<String> dressStrs = new ArrayList<String>();
        for (String dress : event.getDressCode()) {
            dressStrs.add(dotStr + dress);
        }

        tv_dress_code.setText(TextUtils.join("\n", dressStrs));
        tv_age.setText(dotStr + event.getAge());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String phoneNum = "tel:" + this.event.getVenue().getPhoneNumber();
        callIntent.setData(Uri.parse(phoneNum));
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                }
            }
        }
    }

    @OnClick(R.id.iv_venue)
    void showVenueDetail() {
        Intent intent = new Intent(EventDetailActivity.this, VenueDetailActivity.class);
        Gson gson = new Gson();
        String jsonVenueString = gson.toJson(event.getVenue());
        intent.putExtra("venue", jsonVenueString);
        startActivity(intent);
    }

    @OnClick(R.id.ll_call)
    void onCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String phoneNum = "tel:"+ this.event.getVenue().getPhoneNumber();
        callIntent.setData(Uri.parse(phoneNum));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(EventDetailActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EventDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
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
        String url = this.event.getVenue().getSiteURL();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.ll_map)
    void onOpenMap() {
        String url = String.format("http://maps.google.com/maps?saddr=%.06f&daddr=%.06f",
                this.event.getVenue().getGeolocation().getLatitude(),
                this.event.getVenue().getGeolocation().getLongitude());
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(intent);
    }

}
