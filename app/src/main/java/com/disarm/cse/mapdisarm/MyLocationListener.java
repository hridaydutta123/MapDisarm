package com.disarm.cse.mapdisarm;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyLocationListener extends MainActivity implements LocationListener {
    Logger logger;
    String lastLine = "", sCurrentLine;
    BufferedReader br;
    FileInputStream in;
    String phoneVal;
    File inFolder, logFile;

    public MyLocationListener(Logger logger1,String phoneVal1) {
        this.logger = logger1;
        this.phoneVal = phoneVal1;

    }
    @Override
    public void onLocationChanged(Location locFromGps) {
        // called when the listener is notified with a location update from the GPS
        longitude = locFromGps.getLongitude();
        latitude = locFromGps.getLatitude();
        speed=locFromGps.getSpeed();

        // Calculate from GPS
        inFolder = Environment.getExternalStoragePublicDirectory("DMS/Map/tiles");
        File[] foundFiles = inFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {

                return name.startsWith("MapDisarm_Log_" + phoneVal);
            }
        });
        if(foundFiles != null && foundFiles.length > 0)
        {
            logFile = new File(foundFiles[0].toString());
            Log.v("LogFile:", foundFiles[0].toString());
            try {
                in = new FileInputStream(logFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String lastLine = "";

            br = new BufferedReader(new InputStreamReader(in));
            try {
                while ((sCurrentLine = br.readLine()) != null)
                {
                    lastLine = sCurrentLine;
                }
            }
            catch (Exception e)
            {}
            Log.v("LastLine:" ,lastLine);
            Log.v("LastLine Location New:","Lat:" + latitude + "Long:" + longitude);

        }
       //Log.v("Locatio Changed","Lat:" + latitude + "Long:" + longitude);
        if (latitude!=0.0 && longitude!=0.0){
            logger.addRecordToLog(String.valueOf(latitude)+","+ String.valueOf(longitude)+","+String.valueOf(speed));
        }
        Log.v("Speed :",Float.toString(speed));
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
