package com.example.gek.peoplefinder.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private LatLng mMyLocation;
    private ArrayList<Mark> mListMarks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListMarks = new ArrayList<>();
        mListMarks.add(new Mark("Market", 49.441436, 32.065216, new Date()));
        mListMarks.add(new Mark("Bridge", 49.478453, 32.038448, new Date()));
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
        updateUi();
    }

    private void updateUi(){
        if ((mMap != null) && (mListMarks != null)){
            mMap.clear();
            for (Mark mark: mListMarks) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mark.getLatitude(), mark.getLongitude()))
                        .title(mark.getName()));
            }
        }
    }
}
