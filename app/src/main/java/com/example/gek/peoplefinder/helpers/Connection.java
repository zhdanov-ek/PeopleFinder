package com.example.gek.peoplefinder.helpers;

import com.google.android.gms.maps.model.LatLng;

/**
 * Store status of AUTH, state of service and value of basic parameters
 */

public class Connection {
    private static Connection instance;
    private String userName;
    private LatLng lastLocation;
    private Boolean isServiceRunning;
    private Boolean isShowOldPersons;
    private int frequencyLocationUpdate;
    private int locationProvider;

    public static synchronized Connection getInstance(){
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    // Constructor
    private Connection(){
        userName = "";
        isServiceRunning = false;
        isShowOldPersons = SettingsHelper.isNeedShowOldPersons();
        frequencyLocationUpdate = SettingsHelper.getFrequencyOfUpdate();
        userName = SettingsHelper.getUserName();
        locationProvider = SettingsHelper.getLocationProvider();
    }



    public String toString(){
        String s = "name=" + userName +
                ", serviceRunning=" + isServiceRunning;
        return s;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LatLng getLastLocation() {
        return lastLocation;
    }
    public void setLastLocation(LatLng lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Boolean getServiceRunning() {
        return isServiceRunning;
    }
    public void setServiceRunning(Boolean serviceRunning) {
        isServiceRunning = serviceRunning;
    }

    public Boolean isShowOldPersons() {
        return isShowOldPersons;
    }
    public void setShowOldPersons(Boolean isShowOldPersons) {
        this.isShowOldPersons = isShowOldPersons;
    }

    public int getFrequencyLocationUpdate() {
        return frequencyLocationUpdate;
    }
    public void setFrequencyLocationUpdate(int frequencyLocationUpdate) {
        this.frequencyLocationUpdate = frequencyLocationUpdate;
    }

    public int getLocationProvider() {
        return locationProvider;
    }
    public void setLocationProvider(int locationProvider) {
        this.locationProvider = locationProvider;
    }
}
