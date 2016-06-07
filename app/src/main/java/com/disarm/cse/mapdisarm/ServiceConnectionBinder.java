package com.disarm.cse.mapdisarm;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by Sanna on 06-06-2016.
 */
public class ServiceConnectionBinder  {
    MapService myService;
    boolean syncServiceBound = false;
    public ServiceConnection syncServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MapService.SyncServiceBinder binder = (MapService.SyncServiceBinder) service;
            myService = binder.getService();
            syncServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            syncServiceBound = false;
        }
    };

}
