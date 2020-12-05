package com.example.usagewatcher.datacollectors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallLogs {

    public static void getCallLog(Context context) {
        // code taken from Stackoverflow
        StringBuilder sb = new StringBuilder();

        // these are the things we want to know about each call
        String[] projection = new String[]{
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        // go through the data to get the call logs
        // TODO: how to get ONLY the last 1 hour's call logs?
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, null);
        assert managedCursor != null;
        while (managedCursor.moveToNext()) {
            String name = managedCursor.getString(0); // name
            String number = managedCursor.getString(1); // number
            String call_type = managedCursor.getString(2); // type
            String date = managedCursor.getString(3); // time (ms since epoch)
            String duration = managedCursor.getString(4); // duration (seconds)

            // convert date from unix to readable string
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long.parseLong(date)));

            String direction = null;
            int dircode = Integer.parseInt(call_type);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    direction = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    direction = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    direction = "MISSED";
                    break;
            }

            sb.append("\nPhone Name :-- " + name + "  Number:--- " + number + " \nCall Type:--- " + direction + " \nCall Date:--- " + dateString + " \nCall duration in sec :--- " + duration);
            sb.append("\n----------------------------------");

            Log.d("CALLLOG", sb.toString());
        }

        // close the cursor after getting all required data: good practice :)
        managedCursor.close();
    }

}
