package com.disarm.cse.mapdisarm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import bishakh.psync.SyncService;

import static com.disarm.cse.mapdisarm.R.id.time;


public class MainActivity extends Activity {
    File imagesFolder;
    String fileNameFromCheckbox = "";
    String calendarDateTime = "";
    int flag = 0;
    Calendar c = Calendar.getInstance();
    MyService myService;
    boolean syncServiceBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesFolder = new File(Environment.getExternalStorageDirectory(), "MapDisarm");

        if(!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }

        final Button button = (Button) findViewById(R.id.capture_image);
        button.setEnabled(false);
        final CheckBox cb1 = (CheckBox)findViewById(R.id.Food);
        final CheckBox cb2 = (CheckBox)findViewById(R.id.Shelter);
        final CheckBox cb3 = (CheckBox)findViewById(R.id.Victim);
        final CheckBox cb4 = (CheckBox)findViewById(R.id.Health);

        CompoundButton.OnCheckedChangeListener checker = new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton cb, boolean b) {
                if(cb1.isChecked() || cb2.isChecked() || cb3.isChecked() || cb4.isChecked()){
                    button.setEnabled(true);
                }
                else if(button.isEnabled()){
                    button.setEnabled(false);
                }

            }

        };
        cb1.setOnCheckedChangeListener(checker);
        cb2.setOnCheckedChangeListener(checker);
        cb3.setOnCheckedChangeListener(checker);
        cb4.setOnCheckedChangeListener(checker);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cb1.isChecked()) {
                    if (flag == 0) {
                        fileNameFromCheckbox = fileNameFromCheckbox + "Food";
                        flag = 1;
                    } else
                        fileNameFromCheckbox = fileNameFromCheckbox + '-' + "Food";
                }
                if(cb2.isChecked()) {
                    if (flag == 0) {
                        fileNameFromCheckbox = fileNameFromCheckbox + "Shelter";
                        flag = 1;
                    } else
                        fileNameFromCheckbox = fileNameFromCheckbox + '-' + "Shelter";
                }
                if(cb3.isChecked()) {
                    if (flag == 0) {
                        fileNameFromCheckbox = fileNameFromCheckbox + "Victim";
                        flag = 1;
                    } else
                        fileNameFromCheckbox = fileNameFromCheckbox + '-' + "Victim";
                }if(cb4.isChecked()) {
                    if (flag == 0) {
                        fileNameFromCheckbox = fileNameFromCheckbox + "Health";
                        flag = 1;
                    } else
                        fileNameFromCheckbox = fileNameFromCheckbox + '-' + "Health";
                }
                String time = new SimpleDateFormat("yyyyMMddHHmmss").format(c.getTime());
                File image = new File(imagesFolder,"IMG_" + fileNameFromCheckbox + '_' + time + ".jpg");
                Uri uriSavedImage = Uri.fromFile(image);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                startActivity(intent);

            }
        });

        final Button psynctgl = (Button) findViewById(R.id.psynctgl);
        final Button openWebView = (Button) findViewById(R.id.openWebView);

        // Start Syncing Activity
        psynctgl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    final Intent syncServiceIntent = new Intent(getBaseContext(), MyService.class);
                    bindService(syncServiceIntent, syncServiceConnection, Context.BIND_AUTO_CREATE);
                    startService(syncServiceIntent);
                    Toast.makeText(getApplicationContext(), "Starting to Sync", Toast.LENGTH_SHORT).show();

            }
        });

        // Start WebView
        openWebView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Toast.makeText(getApplicationContext(), imagesFolder.toString()+ "/offlineMap/index.html" ,
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);
            }
        });


    }
    private ServiceConnection syncServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MyService.SyncServiceBinder binder = (MyService.SyncServiceBinder) service;
            myService = binder.getService();
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
        final Intent syncServiceIntent = new Intent(getBaseContext(), MyService.class);
        stopService(syncServiceIntent);
        unbindService(syncServiceConnection);
        super.onDestroy();
    }
}
