package com.example.usagewatcher;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.usagewatcher.datacollectors.AccelerometerService;
import com.example.usagewatcher.datacollectors.GyroscopeService;
import com.example.usagewatcher.datacollectors.LocationService;
import com.example.usagewatcher.datastorage.FileUtils;

public class BackgroundService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = BackgroundService.class.getSimpleName();

    // variables for accelerometer and gyroscope services
    private Intent accelerometer_intent;
    private Intent gyroscope_intent;

    // variables for location service
    private static LocationService locationService = null;
    private static boolean locationServiceBound = false;
    private static final ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            locationService = binder.getService();
            locationServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            locationServiceBound = false;
        }
    };

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // call function function to make log directory if it's not already made
        FileUtils.makeDirectory(getApplicationContext());

        // bind location service to this service
        Intent location_service_intent = new Intent(getApplicationContext(), LocationService.class);
        bindService(location_service_intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

        // start accelerometer service
        accelerometer_intent = new Intent(BackgroundService.this, AccelerometerService.class);
        startService(accelerometer_intent);

        // start gyroscope service
        gyroscope_intent = new Intent(BackgroundService.this, GyroscopeService.class);
        startService(gyroscope_intent);

        // set up battery receiver

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // create notification channel and the notification to deliver when foreground service starts
        createNotificationChannel();
        Notification notification = createNotification();

        // start foreground service and deliver notification
        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        // stop accelerometer service
        stopService(accelerometer_intent);

        // remove location updates
        if (locationServiceBound) {
            locationService.removeLocationUpdates();
            unbindService(locationServiceConnection);
            locationServiceBound = false;
        }

        // stop accelerometer service
        stopService(gyroscope_intent);

        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        Notification notification;
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.data_collection_ongoing))
                .setSmallIcon(R.drawable.notification_dot)
                .build();

        return notification;
    }
}
