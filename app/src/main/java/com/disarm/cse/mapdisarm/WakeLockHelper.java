package com.disarm.cse.mapdisarm;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class WakeLockHelper {
    private static PowerManager.WakeLock cpuWakeLock;
    private static final String TAG = "MapDisarm";

    /**
     * Register a wake lock to power management in the device.
     *
     * @param context Context to use
     * @param awake if true the device cpu will keep awake until false is called back. if true is
     * passed several times only the first time after a false call will take effect,
     * also if false is passed and previously the cpu was not turned on (true call)
     * does nothing.
     */
    public static void keepCpuAwake(Context context, boolean awake) {
        if (cpuWakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                cpuWakeLock =
                        pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
                cpuWakeLock.setReferenceCounted(true);
            }
        }
        if (cpuWakeLock != null) { //May be null if pm is null
            if (awake) {
                cpuWakeLock.acquire();
                Log.d(TAG, "Adquired CPU lock");
            } else if (cpuWakeLock.isHeld()) {
                cpuWakeLock.release();
                Log.d(TAG, "Released CPU lock");
            }
        }
    }


}
