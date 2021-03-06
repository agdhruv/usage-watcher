package com.example.usagewatcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usagewatcher.datacollectors.AccelerometerService;
import com.example.usagewatcher.datacollectors.GyroscopeService;
import com.example.usagewatcher.datacollectors.LocationService;
import com.example.usagewatcher.datastorage.FileHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Utils {

    public static File dir;

    @SuppressLint("HardwareIds")
    public static String getDeviceUniqueId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    static void displayToast(Context c, String message) {
        Toast toast = Toast.makeText(c, message, Toast.LENGTH_LONG);
        View view = toast.getView();
        assert view != null;
        view.setBackgroundResource(R.color.black);
        TextView text_view = view.findViewById(android.R.id.message);
        text_view.setTextColor(c.getResources().getColor(R.color.white));
        toast.show();
    }

    // create folder
    public static void makeDirectory(Context context) {
        String folder_main = "UsageWatcherLogs";
        dir = new File(context.getFilesDir(), folder_main);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d("file_response", "Folder could not be made");
            }
        }
    }

    // send file to the cloud
    private static void sendFile(final Context context, final String new_file, final String file_type, final File file_to_send) {
        Runnable r = new Runnable() {
            public void run() {
                //send file to server
                FileHandler fileHandler = new FileHandler(context, new File(dir, new_file), file_type);
                fileHandler.writeLogsToFile(file_to_send);
                fileHandler.uploadLogFile();
                Log.d("file", file_to_send.getName() + " sent");
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    public static void sendAccFile(final Context c) {
        sendFile(c, "acc_upload.csv", "acc", AccelerometerService.acc_log_file);
    }

    public static void sendGyroFile(final Context c) {
        sendFile(c, "gyro_upload.csv", "gyro", GyroscopeService.gyro_log_file);
    }

    public static void sendGPSFile(final Context c) {
        sendFile(c, "gps_upload.csv", "gps", LocationService.gps_log_file);
    }

    // write data to file
    public static void writeToFile(File file, String s) {
        try {
            if (!file.exists()) {
                if(!file.createNewFile()){
                    Log.d("file_response", file + " could not be created");
                }
            }
            FileOutputStream fos = new FileOutputStream(file, true);
            Log.d("file_write", file.getName() + " written " + s);
            PrintWriter pw = new PrintWriter(fos);
            pw.println(s);
            pw.flush();
            pw.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("file_response", "File not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
