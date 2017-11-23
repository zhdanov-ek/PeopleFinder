package com.example.gek.peoplefinder;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.gek.peoplefinder.activities.MainActivity;
import com.example.gek.peoplefinder.helpers.Connection;
import com.example.gek.peoplefinder.helpers.Const;
import com.example.gek.peoplefinder.helpers.LogHelper;
import com.google.android.gms.maps.model.LatLng;


import java.util.Date;

public class LocationService extends Service {

    private Handler handler = new Handler();
    private static final String TAG = "LOCATION_SERVICE";
    private LogHelper logHelper;
    private LocationManager mLocationManager;

    public LocationService() {
    }

    /** Get current location */
    private android.location.LocationListener mLocationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Connection.getInstance().setLastLocation(
                        new LatLng(location.getLatitude(), location.getLongitude()));
//                writePositionToDb(location.getLatitude(), location.getLongitude());
                logHelper.writeLog(location.getProvider() + " pass location", new Date());
                stopLocationUpdates();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(LocationService.this, "Provider enabled " + provider, Toast.LENGTH_SHORT).show();
            logHelper.writeLog(provider + " enabled", new Date());
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(LocationService.this, "Provider disabled " + provider, Toast.LENGTH_SHORT).show();
            logHelper.writeLog(provider + " disabled", new Date());
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Connection.getInstance().setServiceRunning(true);
        if (logHelper == null){
            logHelper = new LogHelper(getBaseContext());
        }
        Log.d(TAG, "onStartCommand: setServiceRunning - true");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnableGetLocation);
        showNotification();
        return START_STICKY;
    }


    // start every n-seconds for get location if service running
    private Runnable runnableGetLocation = new Runnable() {
        @Override
        public void run() {
            if (Connection.getInstance().getServiceRunning()){
                logHelper.writeLog("start Runnable", new Date());
                handler.postDelayed(this, Connection.getInstance().getFrequencyLocationUpdate());
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        }
    };


    /** set request for retrieve current location */
    private void startLocationUpdates() {
        if (checkLocationPermission()) {
            // Register the listener with the Location Manager to receive location updates
            String provider;
            switch (Connection.getInstance().getLocationProvider()){
                case Const.PROVIDER_GPS:
                    provider = LocationManager.GPS_PROVIDER;
                    break;
                case Const.PROVIDER_NETWORK:
                    provider = LocationManager.NETWORK_PROVIDER;
                    break;
                default:
                    provider = LocationManager.NETWORK_PROVIDER;
                    break;
            }
            mLocationManager.requestLocationUpdates(provider, 0, 0, mLocationListener);
        } else {
            Toast.makeText(getBaseContext(), "No permissions for location", Toast.LENGTH_SHORT).show();
        }
    }


    /** stop listen update about our location */
    private void stopLocationUpdates() {
        mLocationManager.removeUpdates(mLocationListener);
        logHelper.writeLog("Remove location updates", new Date());

    }


//    private void writePositionToDb(Double latitude, Double longitude){
//        Log.d(TAG, "writePositionToDb: ");
//        Warior warior = new Warior();
//        warior.setLatitude(latitude);
//        warior.setLongitude(longitude);
//        warior.setName(Connection.getInstance().getUserName());
//        warior.setTeam(Connection.getInstance().getTeam());
//        warior.setKey(Connection.getInstance().getUserKey());
//        warior.setDate(new Date());
//        FbHelper.updateWariorPosition(warior);
//        logHelper.writeLog(latitude + " - " + longitude + " (write to DB)", new Date());
//    }

    private void showNotification() {
        NotificationCompat.Builder nfBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_man_run)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.mes_service_work))
                        .setShowWhen(false);

        // create pending intent used when tapping on the app notification
        // open up ScreenMapFragment
        Intent intent = new Intent(this, MainActivity.class);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this, 0, intent, 0);
        nfBuilder.setContentIntent(pendingIntent);

        // end notification and mark service how high priority
        startForeground(Const.NOTIFICATION_ID, nfBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    private Boolean checkLocationPermission() {
        return ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.M));
    }


    @Override
    public void onDestroy() {
        mLocationManager.removeUpdates(mLocationListener);
        Log.d(TAG, "onDestroy: disconnect from GoogleApiClient");
        super.onDestroy();
    }

}