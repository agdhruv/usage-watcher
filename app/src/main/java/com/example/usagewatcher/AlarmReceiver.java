package com.example.usagewatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.usagewatcher.datacollectors.CallLogs;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive -- fires every few minutes even if app is killed!");
        CallLogs.collectAndSend(context, Utils.CALL_LOGS_INTERVAL_HOURS);
    }

}
