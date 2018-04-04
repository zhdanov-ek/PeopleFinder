package com.example.gek.peoplefinder;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.example.gek.peoplefinder.helpers.SettingsHelper;
import com.facebook.FacebookSdk;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;

public class PeopleFinderApplication extends Application {

    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/realmmarks";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        SettingsHelper.init(getApplicationContext());
        Realm.init(this);
        FacebookSdk.sdkInitialize(this);
        RealmLog.setLevel(LogLevel.TRACE);
    }
}