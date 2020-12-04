package com.example.usagewatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class OnBootReceiver extends BroadcastReceiver {

    private static String TAG = "OnBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();

        // if the received action is "boot completed", start the background service
        assert intentAction != null;
        if (intentAction.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // TODO: code to start the background service from the one shared by Mohit
            Log.d(TAG, "boot receiver onreceive");
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}
