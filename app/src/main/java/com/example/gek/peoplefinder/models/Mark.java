package com.example.gek.peoplefinder.models;


import java.util.Date;

public class Mark {
    private String name;
    private double latitude;
    private double longitude;
    private Date date;

    public Mark() {
    }

    public Mark(String name, double latitude, double longitude, Date date) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
