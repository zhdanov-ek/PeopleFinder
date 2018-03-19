package com.example.gek.peoplefinder.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.gek.peoplefinder.LocationService;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.auth.UserManager;
import com.example.gek.peoplefinder.enums.StateMenu;
import com.example.gek.peoplefinder.fragments.LogsFragment;
import com.example.gek.peoplefinder.fragments.MapFragment;
import com.example.gek.peoplefinder.fragments.MarkFragment;
import com.example.gek.peoplefinder.fragments.SettingsFragment;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.example.gek.peoplefinder.interfaces.DrawerMenuStateChanger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity  implements
        FragmentChanger,
        NavigationView.OnNavigationItemSelectedListener,
        DrawerMenuStateChanger,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "A_MAIN";

    private boolean logoutAfterClose;
    private FragmentManager mFragmentManager;
    private GoogleApiClient mGoogleApiClient;
    private NavigationView mNavigationView;
    private StateMenu mStateMenu;


    @BindView(R.id.toolbar) protected Toolbar toolbar;
    @BindView(R.id.drawerLayout) protected DrawerLayout drawerLayout;
    @BindView(R.id.lockPermissions) protected View lockPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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


    @Override
    public void setMenuState(StateMenu state) {
        mStateMenu = state;
        if (mNavigationView != null){
            switch (state){
                case MAP:
                    mNavigationView.setCheckedItem(R.id.nav_map);
                    break;
                case MARK:
                    mNavigationView.setCheckedItem(R.id.nav_mark);
                    break;
                case LOGS:
                    mNavigationView.setCheckedItem(R.id.nav_logs);
                    break;
                case SETTINGS:
                    mNavigationView.setCheckedItem(R.id.nav_settings);
                    break;
            }
        }
    }

    private void initDrawer(){
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        ((TextView)mNavigationView.getHeaderView(0).findViewById(R.id.tvName)).setText(SettingsHelper.getUserName());
        ImageView ivProfileImage = (ImageView) mNavigationView.getHeaderView(0).findViewById(R.id.ivProfileImage);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mStateMenu == StateMenu.MAP){
                finish();
            } else {
                super.onBackPressed();
            }
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
            }
        } else {
            lockPermissions.setVisibility(View.VISIBLE);
            setEnableDrawerMenu(false);
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
                clearBackStack();
                break;

            case R.id.nav_mark:
                showMarkFragment(null);
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
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut(){
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        Connection.getInstance().setServiceRunning(false);
        stopService(new Intent(this, LocationService.class));
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

    private void clearBackStack(){
        for (int i = 1; i < mFragmentManager.getBackStackEntryCount(); ++i) {
            mFragmentManager.popBackStack();
        }
    }

    @Override
    public void showMapFragment(){
        if (checkLocationPermission(Const.RC_INIT_LOCATION)) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.container, new MapFragment(), null);
            ft.addToBackStack(null);
            ft.commit();
            lockPermissions.setVisibility(View.GONE);
            setEnableDrawerMenu(true);
        }
    }

    private void showLogsFragment(){
        if (mStateMenu != StateMenu.LOGS){
            clearBackStack();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.container, new LogsFragment(), null);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void showMarkFragment(Bundle bundle){
        if (mStateMenu != StateMenu.MARK){
            clearBackStack();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            MarkFragment markFragment = new MarkFragment();
            markFragment.setArguments(bundle);
            ft.replace(R.id.container, markFragment, null);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void showSettingsFragment(){
        if (mStateMenu != StateMenu.SETTINGS){
            clearBackStack();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.container, new SettingsFragment(), null);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void setEnableDrawerMenu(boolean isEnable){
        if (mNavigationView != null){
            mNavigationView.getMenu().setGroupVisible(R.id.nav_main_group, isEnable);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!Connection.getInstance().isServiceRunning()){
            startService(new Intent(this, LocationService.class));
        }
        showMapFragment();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.reconnect();
    }

    @OnClick(R.id.btnGrant) protected void onClickGrant(){
        checkLocationPermission(Const.RC_INIT_LOCATION);
    }

    @Override
    public void hideKeyboard(){
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null){
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
