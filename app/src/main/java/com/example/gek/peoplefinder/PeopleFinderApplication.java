package com.example.gek.peoplefinder;

import android.app.Application;

import com.facebook.FacebookSdk;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;

public class PeopleFinderApplication extends Application {

    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "realm://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/realmtasks";
    public static final String DEFAULT_LIST_ID = "80EB1620-165B-4600-A1B1-D97032FDD9A0";
    public static String DEFAULT_LIST_NAME = "My Tasks";

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        FacebookSdk.sdkInitialize(this);
        RealmLog.setLevel(LogLevel.TRACE);
    }
}