package com.example.usagewatcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

public class Utils {

    @SuppressLint("HardwareIds")
    public static String getDeviceUniqueId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
