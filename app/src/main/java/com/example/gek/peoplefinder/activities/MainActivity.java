package com.example.gek.peoplefinder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.auth.UserManager;
import com.example.gek.peoplefinder.fragments.LogsFragment;
import com.example.gek.peoplefinder.fragments.MapFragment;
import com.example.gek.peoplefinder.fragments.MarkFragment;
import com.example.gek.peoplefinder.fragments.SettingsFragment;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.example.gek.peoplefinder.helpers.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity  implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "A_MAIN";
    private boolean logoutAfterClose;
    private FragmentManager mFragmentManager;
    private GoogleApiClient mGoogleApiClient;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initDrawer();
        mFragmentManager = getSupportFragmentManager();

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        mGoogleApiClient = builder.build();
        if (checkLocationPermission(Const.RC_INIT_LOCATION)){
            mGoogleApiClient.connect();
        }
    }



    private void initDrawer(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.tvName)).setText(SettingsHelper.getUserName());
        ImageView ivProfileImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ivProfileImage);
        String urlImage = SettingsHelper.getUserProfileImageUrl();
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .error(R.drawable.ic_person)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(this)
                    .load(urlImage)
                    .apply(options)
                    .into(ivProfileImage);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private boolean checkLocationPermission(int requestCode) {
        boolean isGranted = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            isGranted = true;
        } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    requestCode);
            isGranted = false;
        } else {
            isGranted = true;
        }
        return isGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Const.RC_INIT_LOCATION:
                    mGoogleApiClient.connect();
                    break;
                case Const.RC_OPEN_MAP:
                    Log.d(TAG, "onRequestPermissionsResult: granted");
//                    showMapFragment();
                    break;
                case Const.RC_START_SERVICE:
                    // TODO: 11/23/2017 start service
                    break;
            }
        }
    }


//    private void showSnackToSettingsOpen() {
//        Snackbar.make(toolbar, R.string.permission_location_not_granted, Snackbar.LENGTH_LONG)
//                .setAction(R.string.action_settings, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Utils.openPermissionSettings(getBaseContext());
//                    }
//                })
//                .show();
//    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_map:
                showMapFragment();
                break;

            case R.id.nav_mark:
                showMarkFragment();
                break;

            case R.id.nav_logs:
                showLogsFragment();
                break;

            case R.id.nav_settings:
                showSettingsFragment();
                break;

            case R.id.nav_sign_out:
                signOut();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut(){
        // TODO: 11/23/2017 stop service
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.setAction(SignInActivity.ACTION_IGNORE_CURRENT_USER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        logoutAfterClose = true;
    }

    @Override
    protected void onStop() {
//        if (adapter != null) {
//            touchHelper.attachToRecyclerView(null);
//            adapter = null;
//        }
//        realm.removeAllChangeListeners();
//        realm.close();
//        realm = null;
        if (logoutAfterClose) {
            /*
             * We need call logout() here since onCreate() of the next Activity is already
             * executed before reaching here.
             */
            UserManager.logoutActiveUser();
            SettingsHelper.signOut();
            logoutAfterClose = false;
        }

        super.onStop();
    }

//    private void clearBackStack(){
//        Log.d(TAG, "clearBackStack: entry count before = " + mFragmentManager.getBackStackEntryCount());
//        for (int i = 1; i < mFragmentManager.getBackStackEntryCount(); ++i) {
//            mFragmentManager.popBackStack();
//            Log.d(TAG, "clearBackStack: remove one fragment");
//        }
//        Log.d(TAG, "clearBackStack: entry count after = " + mFragmentManager.getBackStackEntryCount());
//    }

    private void showMapFragment(){
        if (checkLocationPermission(Const.RC_OPEN_MAP)){
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.container, new MapFragment(), null);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void showLogsFragment(){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.container, new LogsFragment(), null);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showMarkFragment(){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.container, new MarkFragment(), null);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showSettingsFragment(){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.container, new SettingsFragment(), null);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.reconnect();
    }
}
