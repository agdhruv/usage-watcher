package com.example.usagewatcher.datacollectors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;

import com.example.usagewatcher.datastorage.FileUtils;

import java.io.File;

public class AccelerometerService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private static String TAG = AccelerometerService.class.getSimpleName();

    public static File acc_log_file = new File(FileUtils.dir, "ACC_Log.csv");
    private int data_logged_and_not_sent;

    public AccelerometerService() {
        // 141+e seconds: 22.3 KB
        // 1 seconds: 0.158 KB
        // 1 day: 13.65 MB
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        data_logged_and_not_sent = 0;
        requestAccelerometerUpdates();

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Axis of the rotation sample, not normalized yet.
        float axisX = event.values[0];
        float axisY = event.values[1];
        float axisZ = event.values[2];

        // taken from: https://issuetracker.google.com/u/3/issues/36916900?pli=1
        long unixTime = System.currentTimeMillis() + ((event.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000L);

        FileUtils.writeToFile(acc_log_file, unixTime + "," + axisX + "," +  axisY + "," + axisZ);
        data_logged_and_not_sent += 1;

        // if enough data has been logged, send it to the cloud
        if (data_logged_and_not_sent > 100) {
            FileUtils.sendAccFile(AccelerometerService.this);
            data_logged_and_not_sent = 0;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        removeAccelerometerUpdates();
    }

    private void requestAccelerometerUpdates() {
        // registers the listener
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void removeAccelerometerUpdates() {
        // unregisters the listener
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}

