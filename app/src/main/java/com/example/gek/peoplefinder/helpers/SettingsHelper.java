package com.example.gek.peoplefinder.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Storing settings of app
 */

public class SettingsHelper {

    private static final String PARAM_USER_NAME = "user_name";
    private static final String PARAM_USER_EMAIL = "user_email";
    private static final String PARAM_USER_PROFILE_URL = "user_image_profile_url";

    private static SharedPreferences mPref;

    private SettingsHelper(){
    }

    public static void init(Context applicationContext){
        mPref = PreferenceManager.getDefaultSharedPreferences(applicationContext);
    }

    public static void signOut(){
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
}
