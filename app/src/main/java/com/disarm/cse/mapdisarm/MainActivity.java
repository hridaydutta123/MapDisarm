package com.disarm.cse.mapdisarm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sanna.cse.disarmlibrary.MyService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {
    private static final int PERMISSION_ALL = 1;
    File imagesFolder;
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
    ToggleButton psynctgl,disarmConnect;Button openWebView;
    ServiceConnectionBinder sc = new ServiceConnectionBinder();
    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        psynctgl = (ToggleButton) findViewById(R.id.psynctgl);
        openWebView = (Button) findViewById(R.id.openWebView);
        openWebView.setBackgroundColor(Color.CYAN);

        if (!checkPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

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

        locationListener = new MyLocationListener();
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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 4, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000,4,locationListener);

        if (lm != null) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
                String time = new SimpleDateFormat("yyyyMMddHHmmss").format(c.getTime());
                imagesFolder = Environment.getExternalStoragePublicDirectory("DMS/Map");
                File image = new File(imagesFolder, "IMG_" + fileNameFromCheckbox + '_' + time + ".jpg");
                Uri uriSavedImage = Uri.fromFile(image);
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
                    final Intent syncServiceIntent = new Intent(getBaseContext(), MapService.class);
                    stopService(syncServiceIntent);
                }
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

        disarmConnect = (ToggleButton)findViewById(R.id.dc);
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
        unbindService(sc.syncServiceConnection);
        stopService(new Intent(getBaseContext(), MyService.class));
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
