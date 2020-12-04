package com.example.usagewatcher;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class AccelerometerService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private static int random = (int)(Math.random() * 50 + 1);
    private static String TAG = AccelerometerService.class.getSimpleName() + String.valueOf(random);

    public AccelerometerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
        ;

        Log.d(TAG, String.valueOf(axisX) + " " + String.valueOf(unixTime));
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
        // TODO: see how many values we are getting per second. can we change the sampling frequency?
    }

    public void removeAccelerometerUpdates() {
        // unregisters the listener
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}

