package com.example.usagewatcher;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int ALARM_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        CallLogs.getCallLog(getApplicationContext());
//        AppUsage.getAppUsageData(getApplicationContext());

        Intent serviceIntent = new Intent(this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

//        ToggleButton alarmToggle = findViewById(R.id.alarmToggle);
//
//        // Set up the alarm broadcast intent.
//        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
//
//        // check if there already exists the same pending intent, meaning the alarm is already on
//        boolean alarmUp = (PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
//        alarmToggle.setChecked(alarmUp);
//
//        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//        // Set the click listener for the toggle button.
//        alarmToggle.setOnCheckedChangeListener
//                (new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged
//                            (CompoundButton buttonView, boolean isChecked) {
//                        String toastMessage;
//
//                        if (isChecked) {
//                            long repeatInterval = 30L;
//                            long triggerTime = SystemClock.elapsedRealtime(); // the first alarm fires one second later
//
//                            // If the Toggle is turned on, set the repeating alarm with
//                            // a 15 minute interval.
//                            if (alarmManager != null) {
//                                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);
//                            }
//                            // Set the toast message for the "on" case.
//                            toastMessage = "Alarm switched on";
//
//                        } else {
//
//                            if (alarmManager != null) {
//                                alarmManager.cancel(notifyPendingIntent);
//                            }
//                            // Set the toast message for the "off" case.
//                            toastMessage = "Alarm switched off";
//                        }
//
//                        // Show a toast to say the alarm is turned on or off.
//                        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
//                    }
//                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}