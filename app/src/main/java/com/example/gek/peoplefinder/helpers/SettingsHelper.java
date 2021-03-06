package com.example.gek.peoplefinder.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Storing settings of app
 */

public class SettingsHelper {
    private static final int DEFAULT_FREQUENCY = 20 * 1000;

    private static final String PARAM_USER_NAME = "user_name";
    private static final String PARAM_USER_EMAIL = "user_email";
    private static final String PARAM_USER_PROFILE_URL = "user_image_profile_url";
    private static final String PARAM_FREQUENCY = "rate";
    private static final String PARAM_SHOW_OLD_PERSONS = "old_persons";
    private static final String PARAM_PROVIDER = "provider";

    private static final String PARAM_ROTATE_GESTURES = "rotate_gestures";
    private static final String PARAM_TILT_GESTURES = "tilt_gestures";
    private static final String PARAM_MY_LOCATION_BUTTON = "my_location_button";
    private static final String PARAM_COMPASS = "compass";
    private static final String PARAM_ZOOM_BUTTONS = "zoom_buttons";

    private static SharedPreferences mPref;

    private SettingsHelper(){
    }

    public static void init(Context applicationContext){
        mPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }

    public static void clearAll(){
        mPref.edit().clear().apply();
    }

    private static void updatePref(String key, String value){
        SharedPreferences.Editor edit = mPref.edit();
        edit.putString(key, value);
        edit.apply();
    }

    private static void updatePref(String key, boolean value){
        SharedPreferences.Editor edit = mPref.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    private static void updatePref(String key, int value){
        SharedPreferences.Editor edit = mPref.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    private static void updatePref(String key, float value){
        SharedPreferences.Editor edit = mPref.edit();
        edit.putFloat(key, value);
        edit.apply();
    }


    // ---------------------- SETTINGS ---------------------
    public static void setUserName(String name){
        updatePref(PARAM_USER_NAME, name);
    }
    public static String getUserName(){
        return mPref.getString(PARAM_USER_NAME, null);
    }

    public static void setUserEmail(String email){
        updatePref(PARAM_USER_EMAIL, email);
    }
    public static String getUserEmail(){
        return mPref.getString(PARAM_USER_EMAIL, null);
    }

    public static void setUserProfileImageUrl(String url){
        updatePref(PARAM_USER_PROFILE_URL, url);
    }
    public static String getUserProfileImageUrl(){
        return mPref.getString(PARAM_USER_PROFILE_URL, null);
    }

    public static void setShowOldPersons(boolean isNeedShow){
        updatePref(PARAM_SHOW_OLD_PERSONS, isNeedShow);
    }
    public static boolean isNeedShowOldPersons(){
        return mPref.getBoolean(PARAM_SHOW_OLD_PERSONS, true);
    }

    public static void setFrequencyOfUpdate(int rate){
        updatePref(PARAM_FREQUENCY, rate);
    }
    public static int getFrequencyOfUpdate(){
        return mPref.getInt(PARAM_FREQUENCY, DEFAULT_FREQUENCY);
    }

    public static void setLocationProvider(int locationProvider){
        updatePref(PARAM_PROVIDER, locationProvider);
    }
    public static int getLocationProvider(){
        return mPref.getInt(PARAM_PROVIDER, Const.PROVIDER_NETWORK);
    }

    public static void setRotateGesturesEnabled(boolean isEnabled){
        updatePref(PARAM_ROTATE_GESTURES, isEnabled);
    }
    public static boolean isRotateGesturesEnabled(){
        return mPref.getBoolean(PARAM_ROTATE_GESTURES, true);
    }

    public static void setTiltGesturesEnabled(boolean isEnabled){
        updatePref(PARAM_TILT_GESTURES, isEnabled);
    }
    public static boolean isTiltGesturesEnabled(){
        return mPref.getBoolean(PARAM_TILT_GESTURES, true);
    }

    public static void setMyLocationButtonEnabled(boolean isEnabled){
        updatePref(PARAM_MY_LOCATION_BUTTON, isEnabled);
    }
    public static boolean isMyLocationButtonEnabled(){
        return mPref.getBoolean(PARAM_MY_LOCATION_BUTTON, true);
    }

    public static void setCompassEnabled(boolean isEnabled){
        updatePref(PARAM_COMPASS, isEnabled);
    }
    public static boolean isCompassEnabled(){
        return mPref.getBoolean(PARAM_COMPASS, true);
    }

    public static void setZoomButtonsEnabled(boolean isEnabled){
        updatePref(PARAM_ZOOM_BUTTONS, isEnabled);
    }
    public static boolean isZoomButtonsEnabled(){
        return mPref.getBoolean(PARAM_ZOOM_BUTTONS, true);
    }


}
