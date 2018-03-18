package com.example.gek.peoplefinder.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Mark extends RealmObject implements ClusterItem {

    @PrimaryKey
    private String id;
    private String name;
    private String imageUrl;
    private double latitude;
    private double longitude;
    private Date date;
    private boolean isPerson;

    public Mark() {
    }

    public Mark(String id, String name, String imageUrl, double latitude, double longitude, Date date, boolean isPerson) {
        this.id = id;;
        this.name = name;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.isPerson = isPerson;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPerson() {
        return isPerson;
    }
    public void setIsPerson(boolean person) {
        isPerson = person;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public Mark getCopyObject(){
        return new Mark(id, name, imageUrl, latitude, longitude, date, isPerson);
    }

    public LatLng getLatLng(){
        return new LatLng(latitude, longitude);
    }
}
