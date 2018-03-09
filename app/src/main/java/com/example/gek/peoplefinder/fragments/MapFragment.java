package com.example.gek.peoplefinder.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.enums.StateMenu;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MapFragment extends BaseFragment implements
        OnMapReadyCallback {
    
    private static final String TAG = "F_MAP";

    private Realm mRealm;
    private GoogleMap mMap;

    private MapView mMapView;

    private float mMapZoom;
    private float mMapBearing;
    private LatLng mCameraPosition;
    private boolean mIsFirstInitializationCameraPosition = true;
    private List<Mark> mListMarks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMapZoom = SettingsHelper.getMapZoom();
        mMapBearing = SettingsHelper.getMapBearing();
        mRealm = Realm.getDefaultInstance();
        final RealmResults<Mark> marks = mRealm.where(Mark.class).findAllAsync();
        mListMarks = marks;
        marks.addChangeListener(changeListener);
    }

    private final RealmChangeListener<RealmResults<Mark>> changeListener = new RealmChangeListener<RealmResults<Mark>>() {
        @Override
        public void onChange(RealmResults<Mark> elements) {
            mListMarks = elements;
            updateUi();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = null;
        try {
            rootView = inflater.inflate(R.layout.fragment_map, container, false);
                MapsInitializer.initialize(this.getActivity());
                mMapView = (MapView) rootView.findViewById(R.id.map);
                mMapView.onCreate(savedInstanceState);
                mMapView.getMapAsync(this);
        }
        catch (InflateException e){
            Log.e(TAG, "Inflate exception");
        }
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        mCallbackDrawerMenuStateChanger.setMenuState(StateMenu.MAP);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsFirstInitializationCameraPosition = true;
        mMapView.onResume();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapSettings();
//        mMap.setOnInfoWindowClickListener(this);
//        mMap.setOnMarkerClickListener(this);
//        mMap.setOnMapClickListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        stashCameraPosition();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SettingsHelper.setMapZoom(mMap.getCameraPosition().zoom);
        SettingsHelper.setMapBearing(mMap.getCameraPosition().bearing);
        if (mRealm != null){
            mRealm.close();
        }
        mMapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void updateUi() {
        stashCameraPosition();

        if ((mMap != null) && (mListMarks != null)){
            mMap.clear();
            drawMarks();
            if (mIsFirstInitializationCameraPosition){
                restoreCameraPosition(Connection.getInstance().getLastLocation());
                mIsFirstInitializationCameraPosition = false;
            } else {
                restoreCameraPosition(mCameraPosition);
            }
        }
    }

    private void drawMarks(){
        if (Connection.getInstance().isServiceRunning()){
            int sizeIconPx = 100;
            RequestOptions options = new RequestOptions().circleCrop().override(sizeIconPx);
            for (final Mark mark: mListMarks) {
                URL path = null;
                try {
                    path = new URL("https://defcon.ru/wp-content/uploads/2015/12/ico_android-3.png");
                } catch (MalformedURLException e) {
                    Log.d(TAG, "Parsing URL with image of profile is failed");
                }

                Glide.with(this)
                        .asBitmap()
                        .apply(options)
                        .load(path)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(mark.getLatitude(), mark.getLongitude()))
                                        .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                        .title(mark.getName()));
                            }
                        });
            }
        }
    }

    private void stashCameraPosition(){
        if ((mMap != null) && (mMap.getCameraPosition().zoom > Const.MAP_ZOOM_DEFAULT)){
            mMapBearing = mMap.getCameraPosition().bearing;
            mMapZoom = mMap.getCameraPosition().zoom;
            mCameraPosition = new LatLng(mMap.getCameraPosition().target.latitude,
                    mMap.getCameraPosition().target.longitude);
        }
    }

    private void restoreCameraPosition(LatLng position) {
        if (position != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(position, mMapZoom, 0, mMapBearing));
            mMap.moveCamera(cameraUpdate);
        }
    }


    private void setMapSettings() {
//                mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        updateUi();
    }
}
