package com.example.usagewatcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

public class Utils {

    public static final int CALL_LOGS_INTERVAL_HOURS = 1;
    public static final int APP_LOGS_INTERVAL_HOURS = 1;

    @SuppressLint("HardwareIds")
    public static String getDeviceUniqueId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
