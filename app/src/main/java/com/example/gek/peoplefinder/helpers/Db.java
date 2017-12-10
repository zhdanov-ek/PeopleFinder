package com.example.gek.peoplefinder.helpers;

import android.util.Log;

import com.example.gek.peoplefinder.models.Mark;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * DB helper
 */

public class Db {
    private static final String TAG = "H_DB";


    public static void addMark(String name, double latitude, double longitude){
        Realm realm = Realm.getDefaultInstance();

        int newId = 0;
        RealmResults<Mark> allMarks = realm.where(Mark.class).findAllSorted("id", Sort.DESCENDING);
        if (allMarks.size() > 0) {
            newId = allMarks.first().getId() + 1;
        }

        final Mark mark = new Mark();
        mark.setId(newId);
        mark.setName(name);
        mark.setLatitude(latitude);
        mark.setLongitude(longitude);
        mark.setDate(new Date());

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
