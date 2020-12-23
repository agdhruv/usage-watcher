package com.example.usagewatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.usagewatcher.datacollectors.AppUsage;
import com.example.usagewatcher.datacollectors.CallLogs;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmIntentType = intent.getStringExtra("alarmType");

        assert alarmIntentType != null;
        if (alarmIntentType.equals("CallLogs")) {
            Log.d(TAG, "onReceive -- CallLogs");
            CallLogs.collectAndSend(context, Utils.CALL_LOGS_INTERVAL_HOURS);
        } else if (alarmIntentType.equals("AppLogs")) {
            Log.d(TAG, "onReceive -- AppLogs");
            AppUsage.collectAndSendEvents(context, Utils.APP_LOGS_INTERVAL_HOURS);
        }
    }

}
