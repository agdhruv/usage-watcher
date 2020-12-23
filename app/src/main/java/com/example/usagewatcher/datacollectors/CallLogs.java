package com.example.usagewatcher.datacollectors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.example.usagewatcher.datastorage.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallLogs {

    public static File calls_log_file;

    // hash of phone number (last 10 digits)
    // WhatsApp calling

    /*
    Fetches the call logs for the last num_hours and sends them to the cloud.
    However, it fetches data for (2 * num_hours) for redundancy. We don't want to loose date just
    in case any one run of this code fails.
     */
    public static void collectAndSend(Context context, int num_hours) {
        Calendar calendar = Calendar.getInstance();
        long start_timestamp = calendar.getTimeInMillis() - (2 * num_hours * 60 * 60 * 1000); // 24 hrs back, assuming 12 hours interval

        String calls = getCallLogSince(context, String.valueOf(start_timestamp));

        // this method initializes FileUtils.dir, so needs to be called first.
        FileUtils.makeDirectory(context.getApplicationContext());
        calls_log_file = new File(FileUtils.dir, "CALLS_Log.csv");

        // write the calls to file, then send it to cloud
        FileUtils.writeToFile(calls_log_file, calls);
        FileUtils.sendCallLogsFile(context.getApplicationContext());
    }

    private static String getCallLogSince(Context context, String start_timestamp_ms) {
        // method's code taken from Stackoverflow

        // to store the CSV file
        StringBuilder sb = new StringBuilder();

        // these are the things we want to know about each call
        String[] projection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        // sql syntax-like WHERE clause
        String selection = "CAST(" + CallLog.Calls.DATE + " as LONG) >= CAST(? as LONG)";
        String[] selectionArgs = new String[]{
                start_timestamp_ms
        };

        // go through the data to get the call logs
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, null);
        assert managedCursor != null;

        // go over all records and get required data
        while (managedCursor.moveToNext()) {
            String number = managedCursor.getString(0); // number
            String call_type = managedCursor.getString(1); // type
            String date = managedCursor.getString(2); // time (ms since epoch)
            String duration = managedCursor.getString(3); // duration (seconds)

            // convert date from unix to readable string
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long.parseLong(date)));

            String direction;
            int direction_code = Integer.parseInt(call_type);
            switch (direction_code) {
                case CallLog.Calls.OUTGOING_TYPE:
                    direction = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    direction = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    direction = "MISSED";
                    break;
                default:
                    direction = "UNKNOWN";
            }

            // store last 4 digits of the phone number
            String lastFourDigits;
            if (number.length() > 4) {
                lastFourDigits = number.substring(number.length() - 4);
            } else {
                lastFourDigits = number;
            }

            sb.append(date).append(",").append(lastFourDigits).append(",").append(dateString).append(",").append(duration).append(",").append(direction).append(",").append(direction_code).append("\n");

        }

        // close the cursor after getting all required data: good practice :)
        managedCursor.close();

        return sb.toString();
    }

}
