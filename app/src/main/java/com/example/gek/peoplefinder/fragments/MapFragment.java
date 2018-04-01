package com.example.gek.peoplefinder.fragments;


import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import com.example.gek.peoplefinder.R;
import com.example.gek.peoplefinder.enums.StateMenu;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.Db;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.example.gek.peoplefinder.helpers.Utils;
import com.example.gek.peoplefinder.helpers.map.MarkInfoWindowAdapter;
import com.example.gek.peoplefinder.helpers.map.MarkRenderer;
import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MapFragment extends BaseFragment implements
        OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<Mark>,
        ClusterManager.OnClusterInfoWindowClickListener<Mark>,
        ClusterManager.OnClusterItemClickListener<Mark>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Mark>,
        GoogleMap.OnMapLongClickListener {

    private static final String TAG = "F_MAP";

    private Realm mRealm;
    private GoogleMap mMap;

    private MapView mMapView;

    private float mMapZoom = -1;
    private float mMapBearing;
    private LatLng mCameraPosition;
    private boolean mIsFirstInitializationCameraPosition = true;
    private List<Mark> mListMarks;
    private Mark mUserMark;

    private ClusterManager<Mark> mClusterManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();
        final RealmResults<Mark> marks = mRealm.where(Mark.class).findAllAsync();
        marks.addChangeListener(changeListener);
        mListMarks = marks;
    }

    private final RealmChangeListener<RealmResults<Mark>> changeListener = new RealmChangeListener<RealmResults<Mark>>() {
        @Override
        public void onChange(RealmResults<Mark> elements) {
            mListMarks = elements;
            String currentUserMarkId = Db.generateUserMarkId();
            for (Mark mark: mListMarks) {
                if (mark.getId().contentEquals(currentUserMarkId)){
                    mUserMark = mark.getCopyObject();
                    break;
                }
            }
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
            mMapView = rootView.findViewById(R.id.map);
            mMapView.onCreate(savedInstanceState);
            mMapView.getMapAsync(this);
        } catch (InflateException e) {
            Log.e(TAG, "Inflate exception");
        }
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

        setToolbarTitle(getString(R.string.title_map));
        mCallbackDrawerMenuStateChanger.setMenuState(StateMenu.MAP);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }


    private void animateMarker(final Marker marker){
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 2 * t);

                if (t > 0.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapSettings();
        mClusterManager = new ClusterManager<Mark>(getContext(), mMap);
        mClusterManager.setRenderer(new MarkRenderer(getActivity(), mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setOnMapLongClickListener(this);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        restoreCameraPosition();
        mMap.setInfoWindowAdapter(new MarkInfoWindowAdapter(getContext()));
        updateUi();
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
        if (mRealm != null) {
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
        if ((mMap != null) && (mListMarks != null)) {
            mMap.clear();
            mClusterManager.clearItems();
            drawMarks();
            if (mIsFirstInitializationCameraPosition) {
                if (mListMarks != null && mListMarks.size() > 0) {
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (Mark mark : mListMarks) {
                        builder.include(mark.getLatLng());
                    }
                    moveCameraToBounds(builder.build());
                }
                mIsFirstInitializationCameraPosition = false;
            }
        }
    }

    private void drawMarks() {
        if (Connection.getInstance().isServiceRunning()) {
            for (Mark mark : mListMarks) {
                mClusterManager.addItem(mark.getCopyObject());
            }
            mClusterManager.cluster();
        }
    }

    private void stashCameraPosition() {
        if ((mMap != null)) {
            mMapBearing = mMap.getCameraPosition().bearing;
            mMapZoom = mMap.getCameraPosition().zoom;
            mCameraPosition = new LatLng(mMap.getCameraPosition().target.latitude,
                    mMap.getCameraPosition().target.longitude);
        }
    }

    private void restoreCameraPosition() {
        if (mMap != null && mMapZoom != -1) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition(mCameraPosition, mMapZoom, 0, mMapBearing));
            mMap.moveCamera(cameraUpdate);
        }
    }


    private void setMapSettings() {
        mMap.getUiSettings().setCompassEnabled(SettingsHelper.isCompassEnabled());
        mMap.getUiSettings().setZoomControlsEnabled(SettingsHelper.isZoomButtonsEnabled());
        mMap.getUiSettings().setRotateGesturesEnabled(SettingsHelper.isRotateGesturesEnabled());
        mMap.getUiSettings().setTiltGesturesEnabled(SettingsHelper.isTiltGesturesEnabled());
        mMap.getUiSettings().setMapToolbarEnabled(false);

        boolean isMyLocationButtonEnabled = SettingsHelper.isMyLocationButtonEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(isMyLocationButtonEnabled);
        if (isMyLocationButtonEnabled
            && (ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public boolean onClusterClick(Cluster<Mark> cluster) {

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        moveCameraToBounds(bounds);

        return true;
    }

    private void moveCameraToBounds(LatLngBounds bounds){
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Mark> cluster) {
    }

    @Override
    public boolean onClusterItemClick(Mark mark) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(final Mark mark) {
        if (!mUserMark.getId().contentEquals(mark.getId())){
            Snackbar snackbar = Snackbar.make(mMapView, mark.getName(), Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.edit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Const.ARG_MARK, mark);
                    mFragmentChanger.showMarkFragment(bundle);
                }
            });
            snackbar.show();
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Snackbar snackbar = Snackbar.make(mMapView, getString(R.string.map_request_for_new_mark), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.yes, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putDouble(Const.ARG_LATITUDE, latLng.latitude);
                bundle.putDouble(Const.ARG_LONGITUDE, latLng.longitude);
                mFragmentChanger.showMarkFragment(bundle);
            }
        });
        snackbar.show();
    }
}
