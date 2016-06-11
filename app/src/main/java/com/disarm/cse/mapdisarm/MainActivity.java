package com.disarm.cse.mapdisarm;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sanna.cse.disarmlibrary.MyService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends Activity {

    private static final int PERMISSION_ALL = 1;
    File imagesFolder,dir;
    String fileNameFromCheckbox = "";
    int flag = 0;
    Calendar c = Calendar.getInstance();
    int boolWebViewFlag = 1;
    float speed;
    double latitude, longitude;
    LocationManager lm;
    Location location;
    boolean gps_enabled, network_enabled;
    LocationListener locationListener;
    CheckBox cb1, cb2, cb3, cb4;
    ToggleButton psynctgl,disarmConnect;Button openWebView,captureImage;
    ServiceConnectionBinder sc = new ServiceConnectionBinder();
    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    String phoneVal="DefaultNode";
    final static String TARGET_BASE_PATH = "/storage/emulated/0/DMS/Map/";
    Logger logger;


    // Receive Current Battery Status
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dir = Environment.getExternalStoragePublicDirectory("DMS/Map/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        // Copy files from assets folder
        copyFileOrDir("");


        // Receive Button Response
        psynctgl = (ToggleButton) findViewById(R.id.psynctgl);
        openWebView = (Button) findViewById(R.id.openWebView);
        openWebView.setBackgroundColor(Color.CYAN);

        captureImage = (Button) findViewById(R.id.capture_image);
        captureImage.setBackgroundColor(Color.GRAY);
        captureImage.setEnabled(false);

        // Take isChecked values from the checkboxes
        cb1 = (CheckBox) findViewById(R.id.Food);
        cb2 = (CheckBox) findViewById(R.id.Shelter);
        cb3 = (CheckBox) findViewById(R.id.Victim);
        cb4 = (CheckBox) findViewById(R.id.Health);

        disarmConnect = (ToggleButton)findViewById(R.id.dc);


        // Read Device source from ConfigFile.txt
        File file = new File(dir,"ConfigFile.txt");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            phoneVal = new String(data, "UTF-8");
            Toast.makeText(getApplicationContext(), "Phone : " + phoneVal,
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!checkPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        // Call logger constructor using phoneVal
        logger = new Logger(phoneVal);


        // Check for Battery Receiver
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        // Check if gps and network provider is on or off
        if (!gps_enabled && !network_enabled) {

            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        }

        locationListener = new MyLocationListener(logger,phoneVal);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000,1,locationListener);

        if (lm != null) {
            // Check for lastKnownLocation
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {

                // Get latitude and longitude values
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            }
        }


        CompoundButton.OnCheckedChangeListener checker = new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton cb, boolean b) {
                if (cb1.isChecked() || cb2.isChecked() || cb3.isChecked() || cb4.isChecked()) {
                    captureImage.setEnabled(true);
                    captureImage.setBackgroundColor(Color.BLACK);
                } else if (captureImage.isEnabled()) {
                    captureImage.setEnabled(false);
                    captureImage.setBackgroundColor(Color.GRAY);
                }

            }

        };
        cb1.setOnCheckedChangeListener(checker);
        cb2.setOnCheckedChangeListener(checker);
        cb3.setOnCheckedChangeListener(checker);
        cb4.setOnCheckedChangeListener(checker);


        // Open Camera on captureImage button click
        captureImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
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

                // Store the image file in the location DMS/Map/
                String time = new SimpleDateFormat("yyyyMMddHHmmss").format(c.getTime());
                imagesFolder = Environment.getExternalStoragePublicDirectory("DMS/Map/tiles");
                File image = new File(imagesFolder, "IMG_" + fileNameFromCheckbox + '_' + latitude + longitude + "_" + time + ".jpg");
                Uri uriSavedImage = Uri.fromFile(image);

                // Open Camera intent
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivity(intent);
            }
        });



        // Start Syncing Activity
        psynctgl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();
                if (on) {
                    openWebView.setEnabled(true);
                    final Intent syncServiceIntent = new Intent(getBaseContext(), MapService.class);
                    bindService(syncServiceIntent, sc.syncServiceConnection, Context.BIND_AUTO_CREATE);
                    startService(syncServiceIntent);
                    Toast.makeText(getApplicationContext(), "Starting to Sync", Toast.LENGTH_SHORT).show();
                    boolWebViewFlag = 0;
                }else{
                    openWebView.setEnabled(false);
                    final Intent syncServiceIntent = new Intent(getBaseContext(), MapService.class);
                    stopService(syncServiceIntent);
                    stopService(new Intent(getApplicationContext(),MapService.class));
                    unbindService(sc.syncServiceConnection);
                    stopService(new Intent(getBaseContext(), MyService.class));
                    boolWebViewFlag = 1;
                }
            }
        });

        // Open Webview on button click
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

        // Start DisarmConnect Service
        disarmConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();
                if (on){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        showRequestPermissionWriteSettings();
                        //startService(new Intent(getBaseContext(), MyService.class));
                        //Toast.makeText(MainActivity.this, "DisarmConnect Started", Toast.LENGTH_SHORT).show();
                    }else {
                        startService(new Intent(getBaseContext(), MyService.class));
                    }
                }else{
                    stopService(new Intent(getBaseContext(), MyService.class));
                }
            }
        });

    }

    private void copyFileOrDir(String path) {
        AssetManager assetManager = this.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() "+path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath =  TARGET_BASE_PATH + path;
                Log.i("tag", "path="+fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images1") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir "+fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";

                    if (!path.startsWith("images1") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                        copyFileOrDir( p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".jpg")) // extension was added to avoid compression on APK file
                newFileName = TARGET_BASE_PATH + filename.substring(0, filename.length()-4);
            else
                newFileName = TARGET_BASE_PATH + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }

    }

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
        unregisterReceiver(mBatInfoReceiver);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.psynctgl);
        if (toggle.isChecked()) {
            final Intent syncServiceIntent = new Intent(getBaseContext(), MapService.class);
            stopService(syncServiceIntent);
            stopService(new Intent(getApplicationContext(),MapService.class));
            unbindService(sc.syncServiceConnection);
            stopService(new Intent(getBaseContext(), MyService.class));
        }
        super.onDestroy();

    }


    private boolean checkPermissions(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void showRequestPermissionWriteSettings() {
        boolean hasSelfPermission = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasSelfPermission = Settings.System.canWrite(this);
        }
        if (hasSelfPermission) {
            startService(new Intent(getBaseContext(), MyService.class));
            Toast.makeText(MainActivity.this, "DisarmConnect Started", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Press DisarmConnect once again", Toast.LENGTH_SHORT).show();
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


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
