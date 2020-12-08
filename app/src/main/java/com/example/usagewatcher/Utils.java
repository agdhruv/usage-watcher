package com.example.usagewatcher;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usagewatcher.datacollectors.AccelerometerService;
import com.example.usagewatcher.datacollectors.CallLogs;
import com.example.usagewatcher.datacollectors.GyroscopeService;
import com.example.usagewatcher.datacollectors.LocationService;
import com.example.usagewatcher.datastorage.FileHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Utils {

    public static final int CALL_LOGS_INTERVAL_HOURS = 12;

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

}
