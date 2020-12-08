package com.example.usagewatcher;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

import static android.app.AppOpsManager.MODE_ALLOWED;

public class Permissions {

    public static String[] getPermissionsRequiredArray() {
        ArrayList<String> temp_permissions = new ArrayList<>();
        temp_permissions.add(Manifest.permission.READ_CALL_LOG);
        temp_permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        temp_permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            temp_permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }

        return temp_permissions.toArray(new String[0]);
    }

    public static boolean hasDialogPermissions(Context context) {
        String[] permissions = getPermissionsRequiredArray();
        if (context != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestDialogPermissions(Activity activity) {
        // get required permissions
        String[] all_permissions = getPermissionsRequiredArray();
        // request for permissions
        ActivityCompat.requestPermissions(activity, all_permissions, 0);
    }

    public static boolean hasAppUsagePermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.getPackageName());
        return mode == MODE_ALLOWED;
    }

    public static void requestAppUsagePermission(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            context.startActivity(intent);
        }
    }
}
