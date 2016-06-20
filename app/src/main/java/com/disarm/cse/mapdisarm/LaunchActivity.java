package com.disarm.cse.mapdisarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hridoy on 8/6/16.
 */
public class LaunchActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File dir = Environment.getExternalStoragePublicDirectory("DMS/Map");
        if (!dir.exists()) {
            dir.mkdir();
        }
        final File configFile = new File(dir,"ConfigFile.txt");

        if (configFile.exists())  {
            // TODO Auto-generated method stub
            Intent iinent = new Intent(LaunchActivity.this, MainActivity.class);
            startActivity(iinent);
            finish();

        }


        setContentView(R.layout.activity_launch);
        final Button submitButton = (Button) findViewById(R.id.submitButton);


        submitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final EditText phoneText1  = (EditText) findViewById(R.id.phoneText);
                final String phoneTextVal = phoneText1.getText().toString();

                if(phoneTextVal.length() == 10 && phoneTextVal.matches("^[789]\\d{9}$")) {
                    if (!configFile.exists())  {
                        try  {
                            Log.d("Config File created ", " Config File created ");
                            configFile.createNewFile();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    try {


                        BufferedWriter buf = new BufferedWriter(new FileWriter(configFile, true));
                        buf.write(phoneTextVal);
                        buf.flush();
                        buf.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // TODO Auto-generated method stub
                    Intent iinent = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(iinent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Enter a valid number",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

}
