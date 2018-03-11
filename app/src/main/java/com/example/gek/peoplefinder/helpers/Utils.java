package com.example.gek.peoplefinder.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class for simple static methods
 */

public class Utils {

    // Open system settings of program
    public static void openPermissionSettings(Context ctx) {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + ctx.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ctx.startActivity(intent);
    }

    public static Boolean validateLat(String stringLat) {
        boolean isValid;
        try {
            double lat = Double.parseDouble(stringLat);
            isValid =  ((lat >= -90) && (lat <= 90));
        } catch (NumberFormatException e){
            isValid = false;
        }
        return isValid;
    }

    public static Boolean validateLng(String stringLng) {
        boolean isValid;
        try {
            double lng = Double.parseDouble(stringLng);
            isValid =  ((lng >= -180) && (lng <= 180));
        } catch (NumberFormatException e){
            isValid = false;
        }
        return isValid;
    }

    /** Define direction from point A to point B in human readable format */
    public static String getDirection( LatLng start, LatLng finish){
        if (start != null && finish != null) {
            double radians = Math.atan2(
                    (finish.longitude - start.longitude),
                    (finish.latitude - start.latitude));
            double compassReading = radians * (180 / Math.PI);

            String[] directions = new String[]{"North", "NorthEast", "East", "SouthEast", "South",
                    "SouthWest", "West", "NorthWest", "North"};
            int index = (int) Math.round(compassReading / 45);
            if (index < 0) {
                index = index + 8;
            }
            return directions[index];
        } else {
            return null;
        }
    }

    public static String getDistance(LatLng start, LatLng finish) {
        if (start != null && finish != null){
            int Radius = 6371;// radius of earth in Km

            double dLat = Math.toRadians(finish.latitude - start.latitude);
            double dLon = Math.toRadians(finish.longitude - start.longitude);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(start.latitude))
                    * Math.cos(Math.toRadians(finish.latitude)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult = Radius * c;

            int meter = (int)(valueResult * 1000);

            String result;
            if (meter > 999) {
                int km = meter / 1000;
                meter = meter % 1000;
                result = km + " km " + meter + " m";
            } else {
                result = meter + " m";
            }
            return result;
        } else {
            return null;
        }

    }

}
