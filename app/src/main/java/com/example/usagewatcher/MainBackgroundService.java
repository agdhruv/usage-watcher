package com.example.usagewatcher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainBackgroundService extends Service {
    public MainBackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
