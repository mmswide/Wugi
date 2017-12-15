package com.wugi.inc.activities;

import android.media.Image;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.wugi.inc.R;
import com.wugi.inc.fragments.HomeFragment;
import com.wugi.inc.fragments.SettingFragment;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,
        SettingFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();


        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = HomeFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        initNavigationDrawer();
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                Fragment fragment = null;
                Class fragmentClass = null;

                switch (id){
                    case R.id.home:
                        Toast.makeText(getApplicationContext(),"Home",Toast.LENGTH_SHORT).show();
                        fragmentClass = HomeFragment.class;
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.upcoming:
                        Toast.makeText(getApplicationContext(),"Upcoming",Toast.LENGTH_SHORT).show();
                        fragmentClass = HomeFragment.class;
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.photos:
                        Toast.makeText(getApplicationContext(),"Photos",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.browse:
                        Toast.makeText(getApplicationContext(),"Browse",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.notification:
                        Toast.makeText(getApplicationContext(),"Notification",Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.setting:
                        Toast.makeText(getApplicationContext(),"Setting",Toast.LENGTH_SHORT).show();
                        fragmentClass = SettingFragment.class;
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        finish();
                }

                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                return true;
            }
        });
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            String uid = mAuth.getCurrentUser().getUid();
            String imageUrl = mAuth.getCurrentUser().getPhotoUrl().toString();
            String email = mAuth.getCurrentUser().getEmail().toString();

            View header = navigationView.getHeaderView(0);
            ImageView iv_profile = (ImageView) header.findViewById(R.id.iv_profile);
            Picasso.with(this).load(imageUrl).into(iv_profile);
            TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
            tv_email.setText(email);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
