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
    Logger logger;
    public MyLocationListener(Logger logger1) {
        this.logger = logger1;
    }
    @Override
    public void onLocationChanged(Location locFromGps) {
        // called when the listener is notified with a location update from the GPS
        longitude = locFromGps.getLongitude();
        latitude = locFromGps.getLatitude();
        speed=locFromGps.getSpeed();
        Log.v("Locatio Changed","Lat:" + latitude + "Long:" + longitude);
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
