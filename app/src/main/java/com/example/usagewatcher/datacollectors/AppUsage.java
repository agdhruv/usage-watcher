package com.example.usagewatcher.datacollectors;

import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import android.util.Log;

import com.example.usagewatcher.datastorage.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class AppUsage {

    public static File app_events_file;
    private static final String TAG = AppUsage.class.getSimpleName();

    @SuppressLint("WrongConstant")
    public static void getAppUsageData(Context context) {

        // init the object that I will use to query the data I need...
        UsageStatsManager usageStatsManager;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        } else {
            usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        }

        // define the time range in which I want to fetch data
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -20); // TODO: specify how long back I want data for
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        // get app usage data in aggregated form now: https://developer.android.com/reference/android/app/usage/UsageStats
        StringBuilder sb = new StringBuilder();
        Map<String, UsageStats> aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(start, end);
        for (String package_name : aggregatedStats.keySet()) {
            UsageStats stats = aggregatedStats.get(package_name);

            assert stats != null;
            sb.append(package_name).append(",").append(stats.getFirstTimeStamp()).append(",").append(stats.getLastTimeStamp()).append("\n");
        }

        Log.d("EventAgg", sb.toString());

    }

    public static void collectAndSendEvents(Context context, int num_hours) {
        Calendar calendar = Calendar.getInstance();
        long start_timestamp = calendar.getTimeInMillis() - (2 * num_hours * 60 * 60 * 1000); // 24 hrs back, assuming 12 hours interval

        String events = getAppEventsSince(context, String.valueOf(start_timestamp));

        // this method initializes FileUtils.dir, so needs to be called first.
        FileUtils.makeDirectory(context.getApplicationContext());
        app_events_file = new File(FileUtils.dir, "APP_EVENTS_Log.csv");

        // write the calls to file, then send it to cloud
        FileUtils.writeToFile(app_events_file, events);
        FileUtils.sendAppEventsFile(context.getApplicationContext());
    }

    @SuppressLint("WrongConstant")
    private static String getAppEventsSince(Context context, String start_timestamp_ms) {

        // init the object that I will use to query the data I need...
        UsageStatsManager usageStatsManager;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        } else {
            usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        }

        long end_timestamp_ms = System.currentTimeMillis();

        // events
        UsageEvents eventsList = usageStatsManager.queryEvents(Long.parseLong(start_timestamp_ms), end_timestamp_ms);

        StringBuilder sb = new StringBuilder();

        while (eventsList.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            eventsList.getNextEvent(event);
            sb.append(event.getTimeStamp()).append(",").append(event.getPackageName()).append(",").append(event.getEventType()).append("\n");
        }

        Log.d(TAG, sb.toString());

        return sb.toString();
    }

}
