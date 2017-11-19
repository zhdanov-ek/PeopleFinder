package com.example.gek.peoplefinder.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.auth.UserManager;
import com.example.gek.peoplefinder.fragments.MapFragment;
import com.example.gek.peoplefinder.helpers.SettingsHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "A_MAIN";
    private boolean logoutAfterClose;

    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // container = (FrameLayout) findViewById(R.id.container);
        initDrawer();


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        MapFragment mapFragment = new MapFragment();
        fragmentTransaction.add(R.id.container, mapFragment, null);
        fragmentTransaction.commit();
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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut(){
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
}
