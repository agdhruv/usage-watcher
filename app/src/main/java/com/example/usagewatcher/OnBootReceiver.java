package com.example.usagewatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class OnBootReceiver extends BroadcastReceiver {

    private static String TAG = "OnBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();

        // if the received action is "boot completed", start the background service
        if (intentAction.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // TODO: code to start the background service from the one shared by Mohit
        }
    }
}
