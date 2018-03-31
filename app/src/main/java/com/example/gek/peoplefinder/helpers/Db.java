package com.example.gek.peoplefinder.helpers;

import com.example.gek.peoplefinder.models.Mark;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * DB helper
 */

public class Db {

    public static String generateUserMarkId(){
        return Connection.getInstance().getUserName() + Connection.getInstance().getUserEmail();
    }

    public static Mark addMark(String name, String imageUrl, double latitude, double longitude, boolean isUserLocation){
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
        return mark;
    }

    public static Mark findMark(String name, LatLng latLng){
        Realm realm = Realm.getDefaultInstance();
        Mark markResult = null;
        RealmResults<Mark> searchMarks = realm
                .where(Mark.class)
                .equalTo("name", name)
                .findAllSorted("name", Sort.ASCENDING);
        if (searchMarks != null && searchMarks.size() > 0){
            markResult = searchMarks.first();
        }
        return markResult;
    }
}
