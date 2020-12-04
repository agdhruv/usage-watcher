package com.example.usagewatcher;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private OnBootReceiver mReceiver = new OnBootReceiver();

    private Intent accelerometer_intent;
    private Intent gyroscope_intent;
    private Intent current_trip_service_intent;

    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotificationManager;


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
//        getAppUsageData();

//        accelerometer_intent = new Intent(MainActivity.this, AccelerometerService.class);
//        startService(accelerometer_intent);
//
//        gyroscope_intent = new Intent(MainActivity.this, GyroscopeService.class);
//        startService(gyroscope_intent);

        ToggleButton alarmToggle = findViewById(R.id.alarmToggle);

        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        alarmToggle.setChecked(alarmUp);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Set the click listener for the toggle button.
        alarmToggle.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged
                            (CompoundButton buttonView, boolean isChecked) {
                        String toastMessage;
                        if (isChecked) {

                            Log.d("Alarm", "isChecked");
                            long repeatInterval = 60L;

                            long triggerTime = SystemClock.elapsedRealtime();

                            // If the Toggle is turned on, set the repeating alarm with
                            // a 15 minute interval.
                            if (alarmManager != null) {
                                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);
                            }
                            // Set the toast message for the "on" case.
                            toastMessage = "Alarm switched on";

                        } else {
                            // Cancel notification if the alarm is turned off.
                            mNotificationManager.cancelAll();

                            if (alarmManager != null) {
                                alarmManager.cancel(notifyPendingIntent);
                            }
                            // Set the toast message for the "off" case.
                            toastMessage = "Alarm switched off";
                        }

                        // Show a toast to say the alarm is turned on or off.
                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                    }
                });

        // Create the notification channel.
        createNotificationChannel();
    }

    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Stand up notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Notifies every 15 minutes to stand up and walk");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }


    @SuppressLint("WrongConstant")
    public void getAppUsageData() {

        // init the object that I will use to query the data I need...
        UsageStatsManager usageStatsManager;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        } else {
            usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService("usagestats");
        }

        // define the time range in which I want to fetch data
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1); // TODO: specify how long back I want data for
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        // get app usage data in aggregated form now
        Map<String, UsageStats> aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(start, end);
        // TODO: go through this map and store all the data that is required: https://developer.android.com/reference/android/app/usage/UsageStats
        for (String package_name : aggregatedStats.keySet()) {
            UsageStats stats = aggregatedStats.get(package_name);
        }

        // events
        UsageEvents eventsList = usageStatsManager.queryEvents(start, end);

        while (eventsList.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            eventsList.getNextEvent(event);
            Log.d("EVENTLIST", event.getPackageName() + " " + String.valueOf(event.getEventType()));
        }

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