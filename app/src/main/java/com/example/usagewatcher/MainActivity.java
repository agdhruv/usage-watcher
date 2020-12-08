package com.example.usagewatcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.usagewatcher.datacollectors.CallLogs;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int ALARM_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startMainForegroundService();
        setupAlarm();

        // TODO: dump phone data (model, android version -- what else is available?)

        // AppUsage.getAppUsageData(getApplicationContext());

        // let the user know that data collection has started
        Utils.displayToast(getApplicationContext(), getString(R.string.data_collection_has_started));

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

    private void setupAlarm() {

        // Set up the alarm broadcast intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        long repeatInterval = Utils.CALL_LOGS_INTERVAL_HOURS * 60 * 60 * 1000; // interval to repeat after (in ms)

        // set the repeating alarm
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, notifyPendingIntent);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}