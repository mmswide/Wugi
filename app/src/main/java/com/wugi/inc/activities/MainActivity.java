package com.wugi.inc.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.fragments.BrowseFragment;
import com.wugi.inc.fragments.GalleryFragment;
import com.wugi.inc.fragments.HomeFragment;
import com.wugi.inc.fragments.NotificationFragment;
import com.wugi.inc.fragments.SettingFragment;
import com.wugi.inc.fragments.UpcomingFragment;
import com.wugi.inc.utils.LocationHelper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.wugi.inc.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.Set;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener,OnRequestPermissionsResultCallback,
        PermissionUtils.PermissionResultCallback,
        HomeFragment.OnFragmentInteractionListener,
        UpcomingFragment.OnFragmentInteractionListener,
        GalleryFragment.OnFragmentInteractionListener,
        BrowseFragment.OnFragmentInteractionListener,
        NotificationFragment.OnFragmentInteractionListener,
        SettingFragment.OnFragmentInteractionListener {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private Button saveButton;
    private TextView tv_name;

    private FirebaseAuth mAuth;

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

    private Location mLastLocation;

    // Google client to interact with Google API

    private GoogleApiClient mGoogleApiClient;

    double latitude;
    double longitude;

    // list of permissions

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted;

    SharedPreferences sharedpreferences;
    public static final String wugiPreference = "WugiPref";
    public static final String Latitude = "latitudeKey";
    public static final String Longitude = "longitudeKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(wugiPreference,
                Context.MODE_PRIVATE);

        permissionUtils=new PermissionUtils(MainActivity.this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        saveButton = (Button) findViewById(R.id.button_save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingFragment fragment = (SettingFragment) getSupportFragmentManager().findFragmentByTag(SettingFragment.class.getName());
                fragment.saveProfile();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = HomeFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
                saveButton.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        initNavigationDrawer();

        // check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        Menu menuNav = navigationView.getMenu();
        MenuItem item = menuNav.findItem(R.id.setting);
        if (mAuth.getCurrentUser() == null) {
            item.setVisible(false);
        } else {
            item.setVisible(true);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                Fragment fragment = null;
                Class fragmentClass = null;

                switch (id){
                    case R.id.home:
                        fragmentClass = HomeFragment.class;
                        saveButton.setVisibility(View.GONE);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.upcoming:
                        fragmentClass = UpcomingFragment.class;
                        saveButton.setVisibility(View.GONE);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.photos:
                        fragmentClass = GalleryFragment.class;
                        saveButton.setVisibility(View.GONE);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.browse:
                        fragmentClass = BrowseFragment.class;
                        saveButton.setVisibility(View.GONE);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.notification:
                        fragmentClass = NotificationFragment.class;
                        saveButton.setVisibility(View.GONE);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.setting:
                        fragmentClass = SettingFragment.class;
                        saveButton.setVisibility(View.VISIBLE);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        drawerLayout.closeDrawers();
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(MainActivity.this, EnterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return true;
                }

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, fragmentClass.getName());
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, fragmentClass.getName()).commit();

                return true;
            }
        });
        FirebaseUser mUser = mAuth.getCurrentUser();

//        View headerView= LayoutInflater.from(this).inflate(R.layout.nav_header, null);
        View headerView = navigationView.getHeaderView(0);

        ImageView iv_profile = (ImageView) headerView.findViewById(R.id.iv_profile);
        tv_name = (TextView)headerView.findViewById(R.id.tv_name);

        final SearchView searchView = (SearchView) headerView.findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals("")) {
                    Toast.makeText(MainActivity.this, "Please input search text", Toast.LENGTH_SHORT).show();
                    return false;
                }
                drawerLayout.closeDrawers();
                searchView.setQuery("", false);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("search", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        if (mUser != null) {
//            if (header != null) {
//                navigationView.removeHeaderView(headerView);
//            }
//            navigationView.addHeaderView(headerView);
            iv_profile.setVisibility(View.VISIBLE);
            tv_name.setVisibility(View.VISIBLE);

            String uid = mAuth.getCurrentUser().getUid();
            String imageUrl = mAuth.getCurrentUser().getPhotoUrl().toString();
            String name = mAuth.getCurrentUser().getDisplayName().toString();

            Picasso.with(this).load(imageUrl).into(iv_profile);

            tv_name.setText(name);
        } else {
//            if (header != null)
//                navigationView.removeHeaderView(headerView);
            iv_profile.setVisibility(View.GONE);
            tv_name.setVisibility(View.GONE);
        }

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public void updateProfile() {
        String name = mAuth.getCurrentUser().getDisplayName().toString();
        tv_name.setText(name);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Method to display the location on UI
     * */

    private void getLocation() {

        if (isPermissionGranted) {

            try
            {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putLong(Latitude, Double.doubleToRawLongBits(mLastLocation.getLatitude()));
                    editor.putLong(Longitude, Double.doubleToLongBits(mLastLocation.getLongitude()));
                    editor.commit();
                }


            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }

        }

    }

    /**
     * Creating google api client object
     * */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }




    /**
     * Method to verify google play services on the device
     * */

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    // Permission check functions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
        getLocation();

    }




    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }
}
