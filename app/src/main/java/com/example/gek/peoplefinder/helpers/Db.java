package com.example.gek.peoplefinder.helpers;

import android.util.Log;

import com.example.gek.peoplefinder.models.Mark;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * DB helper
 */

public class Db {
    private static final String TAG = "H_DB";

    public static String generateUserMarkId(){
        return Connection.getInstance().getUserName() + Connection.getInstance().getUserEmail();
    }

    public static void addMark(String name, String imageUrl, double latitude, double longitude, boolean isUserLocation){
        Realm realm = Realm.getDefaultInstance();

        final Mark mark = new Mark();
        String id;
        if (isUserLocation){
            id = generateUserMarkId();
        } else {
            id = name;
        }
        mark.setId(id);
        mark.setName(name);
        mark.setImageUrl(imageUrl);
        mark.setLatitude(latitude);
        mark.setLongitude(longitude);
        mark.setDate(new Date());
        mark.setIsPerson(isUserLocation);

        realm.beginTransaction();
        realm.insertOrUpdate(mark);
        realm.commitTransaction();
        realm.close();
    }

    private static void printDb(){
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Mark> marks = realm.where(Mark.class).findAll();
        for (Mark mark : marks) {
            Log.d(TAG, "printDb: " + mark.getName());
        }
        realm.close();
    }
}
