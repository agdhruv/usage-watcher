package com.example.usagewatcher;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private OnBootReceiver mReceiver = new OnBootReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Utils.hasPermissions(MainActivity.this)) {
            Utils.requestPermissions(MainActivity.this);
        }

        if (!Utils.checkAppUsagePermission(MainActivity.this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

//        getCallLog();
        getAppUsageData();
    }


    @SuppressLint("WrongConstant")
    public void getAppUsageData() {

        UsageStatsManager usageStatsManager;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        } else {
            usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService("usagestats");
        }

        // try to get the app usage data now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, start, end);


    }


    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void getCallLog() {
        // code taken from Stackoverflow
        StringBuilder sb = new StringBuilder();

        // these are the things we want to know about each call
        String[] projection = new String[]{
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        // go through the data to get the call logs
        // TODO: how to get ONLY the last 1 hour's call logs?
        Cursor managedCursor = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, null);
        assert managedCursor != null;
        while (managedCursor.moveToNext()) {
            String name = managedCursor.getString(0); // name
            String number = managedCursor.getString(1); // number
            String call_type = managedCursor.getString(2); // type
            String date = managedCursor.getString(3); // time (ms since epoch)
            String duration = managedCursor.getString(4); // duration (seconds)

            // convert date from unix to readable string
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long.parseLong(date)));

            String direction = null;
            int dircode = Integer.parseInt(call_type);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    direction = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    direction = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    direction = "MISSED";
                    break;
            }

            sb.append("\nPhone Name :-- " + name + "  Number:--- " + number + " \nCall Type:--- " + direction + " \nCall Date:--- " + dateString + " \nCall duration in sec :--- " + duration);
            sb.append("\n----------------------------------");

            Log.d("CALLLOG", sb.toString());
        }

        // close the cursor after getting all required data: good practice :)
        managedCursor.close();
    }
}