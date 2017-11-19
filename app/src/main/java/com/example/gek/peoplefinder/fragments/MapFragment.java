package com.example.gek.peoplefinder.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private GoogleApiClient mGoogleApiClient;

    private float mZoomMap = Const.ZOOM_MAP;
    private LatLng mMyLocation;
    private ArrayList<Mark> mListMarks;
    private Boolean mIsAllReady = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mMyLocation = new LatLng(49.451558, 32.044757);
        mListMarks = new ArrayList<>();
        mListMarks.add(new Mark("Market", 49.441436, 32.065216, new Date()));
        mListMarks.add(new Mark("Bridge", 49.478453, 32.038448, new Date()));

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(getActivity());
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        mGoogleApiClient = builder.build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        return v;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setOnInfoWindowClickListener(this);
//        mMap.setOnMarkerClickListener(this);
//        mMap.setOnMapClickListener(this);
        connectToGoogleApiClient();
    }

    private void updateUi(){
        if ((mMap != null) && (mListMarks != null)){
            mMap.clear();
            for (Mark mark: mListMarks) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mark.getLatitude(), mark.getLongitude()))
                        .title(mark.getName()));
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationAndMapSettings();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void locationAndMapSettings() {
        if (mGoogleApiClient.isConnected()) {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    || (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
                mIsAllReady = true;
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                updateUi();
                // TODO: 19.11.2017 Start service for receive Location
            }
        } else {
            connectToGoogleApiClient();
        }
    }

    private void connectToGoogleApiClient() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Const.REQUEST_CODE_LOCATION);
        } else {
            mGoogleApiClient.connect();
        }
    }
}
