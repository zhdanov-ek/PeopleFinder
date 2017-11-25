package com.example.gek.peoplefinder.helpers;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import static android.content.Context.MODE_APPEND;

/**
 * Write and read log
 */

public class LogHelper {
    private static final String TAG = "LOG_HELPER";
    private final static String FILE_NAME = "peopleFinder.log";
    private String pathAbsolute;
    private Context ctx;

    public LogHelper(Context ctx) {
        this.ctx = ctx;
        pathAbsolute = ctx.getFilesDir().getAbsolutePath() + "/" + FILE_NAME;
    }


    public void writeLog(String mes){
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(ctx.openFileOutput(FILE_NAME, MODE_APPEND)));
            bw.write(formatDate(new Date()) + " | " + mes + "\n");
            bw.close();
            Log.d(TAG, "writeLog: write " + mes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String readLog(){
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    ctx.openFileInput(FILE_NAME)));
            String str = "";
            while ((str = br.readLine()) != null) {
                result.append(str).append("\n");
            }
            Log.d(TAG, "readLog: success");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public void clearLog(){
        File removeFile = new File(pathAbsolute);
        if (removeFile.exists()) {
            removeFile.delete();
            Log.d(TAG, "clearLog: removed file " + removeFile.getName());
        }
    }

    private String formatDate(Date date){
        return date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

    }
}
