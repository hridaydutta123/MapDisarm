package com.disarm.cse.mapdisarm;

import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.io.*;

public class Logger {

    public static FileHandler logger = null;
    private static String filename = "MapDisarm_Log";

    static boolean isExternalStorageAvailable = false;
    static boolean isExternalStorageWriteable = false;
    static String state = Environment.getExternalStorageState();

    public static void addRecordToLog(String message) {

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            isExternalStorageAvailable = isExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            isExternalStorageAvailable = true;
            isExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            isExternalStorageAvailable = isExternalStorageWriteable = false;
        }

        File dir = Environment.getExternalStoragePublicDirectory("DMS/Map");
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if(!dir.exists()) {
                Log.d("Dir created ", "Dir created ");
                dir.mkdirs();
            }

            File logFile = new File(dir,filename+".txt");

            if (!logFile.exists())  {
                try  {
                    Log.d("File created ", "File created ");
                    logFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = df.format(Calendar.getInstance().getTime());
                Log.v("Date",date);
                buf.write(message+','+date +"\r\n");

                buf.flush();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
