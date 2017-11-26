package com.example.gek.peoplefinder.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

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
        boolean isValid = true;
        try {
            double lat = Double.parseDouble(stringLat);
            isValid =  ((lat >= -90) && (lat <= 90));
        } catch (NumberFormatException e){
            isValid = false;
        }
        return isValid;
    }

    public static Boolean validateLng(String stringLng) {
        boolean isValid = true;
        try {
            double lng = Double.parseDouble(stringLng);
            isValid =  ((lng >= -180) && (lng <= 180));
        } catch (NumberFormatException e){
            isValid = false;
        }
        return isValid;
    }
}
