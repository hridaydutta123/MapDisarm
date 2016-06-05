package com.disarm.cse.mapdisarm;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sanna.cse.disarmlibrary.MyService;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import bishakh.psync.SyncService;

import static com.disarm.cse.mapdisarm.R.id.time;


public class MainActivity extends Activity{
    private static final int MY_PERMISSIONS_REQUEST = 1;
    File imagesFolder;
    String fileNameFromCheckbox = "";
    int flag = 0;
    Calendar c = Calendar.getInstance();
    MapService myService;
    boolean syncServiceBound = false;
    int boolWebViewFlag = 1;
    float speed;
    public static final int OUT_OF_SERVICE = 0;
    public static final int TEMPORARILY_UNAVAILABLE = 1;
    public static final int AVAILABLE = 2;
    double latitude, longitude;
    LocationManager lm;
    Context context;
    Location location;
    boolean gps_enabled, network_enabled;
    LocationListener locationListener;
    CheckBox cb1, cb2, cb3, cb4;
    Button psynctgl, openWebView;
    ContextCompat cc = new ContextCompat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        //checkForProvider();
        //locationCheck();
       /*LocationListener locationListenernew = new MyLocationListener();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenernew);
        lm.requestLocationUpdates(LocationListener.NET,1000,1,locationListenernew);
        */

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationListener = new MyLocationListener();
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,4 , locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000,4,locationListener);

        if (lm != null) {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Toast.makeText(getApplicationContext(), "Lat0:" + latitude + "Long0:" + longitude,
                        Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getApplicationContext(), "Lat:" + latitude + "Long:" + longitude,
                    Toast.LENGTH_LONG).show();

        }

        final Button button = (Button) findViewById(R.id.capture_image);
        button.setBackgroundColor(Color.GRAY);
        button.setEnabled(false);
        cb1 = (CheckBox) findViewById(R.id.Food);
        cb2 = (CheckBox) findViewById(R.id.Shelter);
        cb3 = (CheckBox) findViewById(R.id.Victim);
        cb4 = (CheckBox) findViewById(R.id.Health);


        CompoundButton.OnCheckedChangeListener checker = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton cb, boolean b) {
                if (cb1.isChecked() || cb2.isChecked() || cb3.isChecked() || cb4.isChecked()) {
                    button.setEnabled(true);
                    button.setBackgroundColor(Color.BLACK);
                } else if (button.isEnabled()) {
                    button.setEnabled(false);
                    button.setBackgroundColor(Color.GRAY);
                }

            }

        };
        cb1.setOnCheckedChangeListener(checker);
        cb2.setOnCheckedChangeListener(checker);
        cb3.setOnCheckedChangeListener(checker);
        cb4.setOnCheckedChangeListener(checker);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {

                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST);
                    }

                }

                if (cb1.isChecked()) {
                    if (flag == 0) {
                        fileNameFromCheckbox = fileNameFromCheckbox + "Food";
                        flag = 1;
                    } else
                        fileNameFromCheckbox = fileNameFromCheckbox + '-' + "Food";
                }
                if (cb2.isChecked()) {
                    if (flag == 0) {
                        fileNameFromCheckbox = fileNameFromCheckbox + "Shelter";
                        flag = 1;
                    } else
                        fileNameFromCheckbox = fileNameFromCheckbox + '-' + "Shelter";
                }
                if (cb3.isChecked()) {
                    if (flag == 0) {
                        fileNameFromCheckbox = fileNameFromCheckbox + "Victim";
                        flag = 1;
                    } else
                        fileNameFromCheckbox = fileNameFromCheckbox + '-' + "Victim";
                }
                if (cb4.isChecked()) {
                    if (flag == 0) {
                        fileNameFromCheckbox = fileNameFromCheckbox + "Health";
                        flag = 1;
                    } else
                        fileNameFromCheckbox = fileNameFromCheckbox + '-' + "Health";
                }
                String time = new SimpleDateFormat("yyyyMMddHHmmss").format(c.getTime());
                imagesFolder = Environment.getExternalStoragePublicDirectory("DMS/Map");
                File image = new File(imagesFolder, "IMG_" + fileNameFromCheckbox + '_' + time + ".jpg");
                Uri uriSavedImage = Uri.fromFile(image);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivity(intent);


            }
        });

        psynctgl = (Button) findViewById(R.id.psynctgl);
        openWebView = (Button) findViewById(R.id.openWebView);

        psynctgl.setBackgroundColor(Color.WHITE);
        openWebView.setBackgroundColor(Color.CYAN);
        // Start Syncing Activity
        psynctgl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openWebView.setEnabled(true);
                final Intent syncServiceIntent = new Intent(getBaseContext(), MapService.class);
                bindService(syncServiceIntent, syncServiceConnection, Context.BIND_AUTO_CREATE);
                startService(syncServiceIntent);
                Toast.makeText(getApplicationContext(), "Starting to Sync", Toast.LENGTH_SHORT).show();
                boolWebViewFlag = 0;
            }
        });

        // Start WebView
        openWebView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (boolWebViewFlag == 1) {
                    Toast.makeText(getApplicationContext(), "Press Sync Start Button First",
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button disarmConnect = (Button)findViewById(R.id.dc);
        disarmConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getBaseContext(), MyService.class));
            }
        });

    }
/**
   public void checkForProvider() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        }else{
        locationCheck();}

    }

    public void locationCheck() {
        Toast.makeText(MainActivity.this, "locationcheck 1", Toast.LENGTH_SHORT).show();
        if (gps_enabled || network_enabled) {

            Toast.makeText(MainActivity.this, "locationcheck if 2", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationListener = new MyLocationListener();
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1 , locationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,1,locationListener);

            if (lm != null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Toast.makeText(getApplicationContext(), "Lat0:" + latitude + "Long0:" + longitude,
                            Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getApplicationContext(), "Lat:" + latitude + "Long:" + longitude,
                        Toast.LENGTH_LONG).show();

            }
        }
        else{
            Toast.makeText(MainActivity.this, "locatioCheck Else 3", Toast.LENGTH_SHORT).show();
        }
    }

*
**/







    private final class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location locFromGps) {
            // called when the listener is notified with a location update from the GPS
            longitude = locFromGps.getLongitude();
            latitude = locFromGps.getLatitude();
            speed=locFromGps.getSpeed();
            Toast.makeText(getApplicationContext(), "On location Changed", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Lat:" + latitude + "Long:" + longitude,
                    Toast.LENGTH_LONG).show();
            if (latitude!=0.0 && longitude!=0.0){
            Logger.addRecordToLog(String.valueOf(latitude)+","+ String.valueOf(longitude)+","+String.valueOf(speed));
            }
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                Toast.makeText(getApplicationContext(), "Problem with GPS", Toast.LENGTH_LONG).show();
            }

            Log.v("speed :",Float.toString(speed));
        }

        @Override
        public void onProviderDisabled(String provider) {
            // called when the GPS provider is turned off (user turning off the GPS on the phone)
            Toast.makeText(getApplicationContext(), "On provider disabled", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // called when the GPS provider is turned on (user turning on the GPS on the phone)
            Toast.makeText(getApplicationContext(), "On provider enabled", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // called when the status of the GPS provider changes
            Toast.makeText(getApplicationContext(), "On Status Changed", Toast.LENGTH_LONG).show();

        }
    }
    private ServiceConnection syncServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Toast.makeText(MainActivity.this, "on service 1", Toast.LENGTH_SHORT).show();
            MapService.SyncServiceBinder binder = (MapService.SyncServiceBinder) service;
            myService = binder.getService();
            Toast.makeText(MainActivity.this, "on service 2", Toast.LENGTH_SHORT).show();
            syncServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            syncServiceBound = false;
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        final Intent syncServiceIntent = new Intent(getBaseContext(), MapService.class);

        stopService(syncServiceIntent);
        unbindService(syncServiceConnection);
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
