package com.example.usagewatcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.usagewatcher.datacollectors.AppUsage;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int CALL_LOGS_ALARM_REQUEST_CODE = 0;
    private static final int APP_LOGS_ALARM_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startMainForegroundService();
        setupCallLogsAlarm();
        setupAppLogsAlarm();

        // TODO: dump phone data (model, android version -- what else is available?)

//        AppUsage.getAppUsageData(getApplicationContext());
        TextView view = findViewById(R.id.androidID);
        view.setText(Utils.getDeviceUniqueId(getApplicationContext()));

    }

    /*
    Starts the foreground service that handles data that needs to be tracked continuously,
    i.e., GPS, accelerometer, gyroscope.
     */
    private void startMainForegroundService() {
        // TODO: only start the service if it isn't running already

        // start the foreground service
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

    }

    private void setupCallLogsAlarm() {
        // Set up the alarm broadcast intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        notifyIntent.putExtra("alarmType", "CallLogs");

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, CALL_LOGS_ALARM_REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        long repeatInterval = Utils.CALL_LOGS_INTERVAL_HOURS * 60 * 60 * 1000; // interval to repeat after (in ms)

        // set the repeating alarm
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, notifyPendingIntent);
        }
    }

    private void setupAppLogsAlarm() {
        // Set up the alarm broadcast intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        notifyIntent.putExtra("alarmType", "AppLogs");

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, APP_LOGS_ALARM_REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        long repeatInterval = Utils.APP_LOGS_INTERVAL_HOURS * 60 * 60 * 1000; // interval to repeat after (in ms)

        // set the repeating alarm
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, notifyPendingIntent);
        }
    }

    public void openLocationSettings(View v) {
        // show notification notif if notification is disabled
        Context context = MainActivity.this;
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {

        // if location is disabled, show a button to enable it
        Button locationButton = findViewById(R.id.locationButton);
        if (!Permissions.isLocationEnabled(MainActivity.this)) {
            locationButton.setVisibility(View.VISIBLE);
        } else {
            locationButton.setVisibility(View.INVISIBLE);
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}