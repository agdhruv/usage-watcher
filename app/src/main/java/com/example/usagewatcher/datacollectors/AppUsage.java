package com.example.usagewatcher.datacollectors;

import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;
import java.util.Map;

public class AppUsage {

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
        calendar.add(Calendar.MINUTE, -2); // TODO: specify how long back I want data for
        long start = calendar.getTimeInMillis();
        long end = System.currentTimeMillis();

        // get app usage data in aggregated form now
        Map<String, UsageStats> aggregatedStats = usageStatsManager.queryAndAggregateUsageStats(start, end);
        // TODO: go through this map and store all the data that is required: https://developer.android.com/reference/android/app/usage/UsageStats
        for (String package_name : aggregatedStats.keySet()) {
            UsageStats stats = aggregatedStats.get(package_name);
        }

        // events
        UsageEvents eventsList = usageStatsManager.queryEvents(start, end);

        while (eventsList.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            eventsList.getNextEvent(event);
            Log.d("EVENTLIST", event.getPackageName() + " " + String.valueOf(event.getEventType()));
        }

    }

}
