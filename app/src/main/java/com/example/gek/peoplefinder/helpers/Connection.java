package com.example.gek.peoplefinder.helpers;

import com.google.android.gms.maps.model.LatLng;

/**
 * Store status of AUTH, state of service and value of basic parameters
 */

public class Connection {
    private static Connection instance;
    private String userName;
    private String userEmail;
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
        lastLocation = null;
        isServiceRunning = false;
        refreshData();
    }

    public void refreshData(){
        isShowOldPersons = SettingsHelper.isNeedShowOldPersons();
        frequencyLocationUpdate = SettingsHelper.getFrequencyOfUpdate();
        userName = SettingsHelper.getUserName();
        userEmail = SettingsHelper.getUserEmail();
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

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LatLng getLastLocation() {
        return lastLocation;
    }
    public void setLastLocation(LatLng lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Boolean isServiceRunning() {
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
    public void setFrequencyLocationUpdate(int ms) {
        SettingsHelper.setFrequencyOfUpdate(ms);
        this.frequencyLocationUpdate = ms;
    }

    public int getLocationProvider() {
        return locationProvider;
    }
    public void setLocationProvider(int locationProvider) {
        this.locationProvider = locationProvider;
    }
}
