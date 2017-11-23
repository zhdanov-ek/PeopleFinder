package com.example.gek.peoplefinder.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class MapFragment extends Fragment implements
        OnMapReadyCallback {
    
    private static final String TAG = "F_MAP";
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private float mZoomMap = Const.ZOOM_MAP;
    private LatLng mMyLocation;
    private ArrayList<Mark> mListMarks;
    private Boolean mIsAllReady = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        mMyLocation = new LatLng(49.451558, 32.044757);
        mListMarks = new ArrayList<>();
        mListMarks.add(new Mark("Market", 49.441436, 32.065216, new Date()));
        mListMarks.add(new Mark("Bridge", 49.478453, 32.038448, new Date()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        return v;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapSettings();
//        mMap.setOnInfoWindowClickListener(this);
//        mMap.setOnMarkerClickListener(this);
//        mMap.setOnMapClickListener(this);
    }

    private void updateUi() {
        int sizeIconPx = 100;
        RequestOptions options = new RequestOptions().circleCrop().override(sizeIconPx);


        if ((mMap != null) && (mListMarks != null)){
            mMap.clear();
            for (final Mark mark: mListMarks) {

                URL path = null;
                try {
                    path = new URL("https://defcon.ru/wp-content/uploads/2015/12/ico_android-3.png");
                } catch (MalformedURLException e) {
                    Log.d("333333333333", "updateUi: ");
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

            // save current zoom of camera and set normal zoom if first show the map
            if (mMap.getCameraPosition().zoom > Const.ZOOM_MAP){
                mZoomMap = mMap.getCameraPosition().zoom;
            } else {
                if (mZoomMap < mMap.getCameraPosition().zoom){
                    mZoomMap = Const.ZOOM_MAP;
                }
                if (mMyLocation != null){
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMyLocation, mZoomMap);
                    mMap.moveCamera(cameraUpdate);
                }
            }
        }
    }



    private void setMapSettings() {
        mIsAllReady = true;
//                mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        updateUi();
        // TODO: 19.11.2017 Start service for receive Location
    }



}
