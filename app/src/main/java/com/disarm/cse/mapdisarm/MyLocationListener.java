package com.disarm.cse.mapdisarm;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sanna on 06-06-2016.
 */
public class MyLocationListener extends MainActivity implements LocationListener {
    @Override
    public void onLocationChanged(Location locFromGps) {
        // called when the listener is notified with a location update from the GPS
        longitude = locFromGps.getLongitude();
        latitude = locFromGps.getLatitude();
        speed=locFromGps.getSpeed();
        //Toast.makeText(MainActivity.this, "On location Changed", Toast.LENGTH_LONG).show();
       // Toast.makeText(getApplicationContext(), "Lat:" + latitude + "Long:" + longitude,
                //Toast.LENGTH_LONG).show();
        Log.v("MyLocationlistener","On Location Changed");
        Log.v("MyLocationlistener","Lat:" + latitude + "Long:" + longitude);
        if (latitude!=0.0 && longitude!=0.0){
            Logger.addRecordToLog(String.valueOf(latitude)+","+ String.valueOf(longitude)+","+String.valueOf(speed));
        }
        /*try {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            Toast.makeText(getApplicationContext(), "Problem with GPS", Toast.LENGTH_LONG).show();
        }*/

        Log.v("speed :",Float.toString(speed));
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
