package com.example.gek.peoplefinder;

import android.app.Application;

import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.facebook.FacebookSdk;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;

public class PeopleFinderApplication extends Application {

    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/realmmarks";

    @Override
    public void onCreate() {
        super.onCreate();
        SettingsHelper.init(getApplicationContext());
        Realm.init(this);
        FacebookSdk.sdkInitialize(this);
        RealmLog.setLevel(LogLevel.TRACE);
    }
}