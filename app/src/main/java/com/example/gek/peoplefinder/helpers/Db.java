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

    private static final String FIELD_MARK_ID = "id";
    private static final String FIELD_MARK_NAME = "name";
    private static final String FIELD_MARK_LATITUDE = "latitude";
    private static final String FIELD_MARK_LONGITUDE = "longitude";

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

    public static void removeMark(String id){
        Realm realm = Realm.getDefaultInstance();
        final Mark result = realm
                .where(Mark.class)
                .equalTo(FIELD_MARK_ID, id)
                .findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (result.isValid()) {
                    result.deleteFromRealm();
                }
            }
        });
    }

    public static Mark findMark(String name){
        Realm realm = Realm.getDefaultInstance();
        Mark markResult = null;
        RealmResults<Mark> searchMarks = realm
                .where(Mark.class)
                .equalTo(FIELD_MARK_NAME, name)
                .findAll()
                .sort(FIELD_MARK_NAME, Sort.ASCENDING);
        if (searchMarks != null && searchMarks.size() > 0){
            markResult = searchMarks.first();
        }
        return markResult;
    }

    public static Mark findMark(String name, LatLng latLng){
        Realm realm = Realm.getDefaultInstance();
        Mark markResult = null;
        RealmResults<Mark> searchMarks = realm
                .where(Mark.class)
                .equalTo(FIELD_MARK_NAME, name)
                .equalTo(FIELD_MARK_LATITUDE, latLng.latitude)
                .equalTo(FIELD_MARK_LONGITUDE, latLng.longitude)
                .findAll()
                .sort(FIELD_MARK_NAME, Sort.ASCENDING);
        if (searchMarks != null && searchMarks.size() > 0){
            markResult = searchMarks.first();
        }
        return markResult;
    }
}
