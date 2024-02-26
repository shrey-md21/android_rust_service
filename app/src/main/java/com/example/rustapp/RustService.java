package com.example.rustapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


public class RustService extends Service {
    private static final String DEFAULT_CHANNEL_ID = "RustServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    static {
        System.loadLibrary("rustapp");
    }

    private static native void startService(String filesDir);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("RustService", "onCreate: Service created");
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("RustService", "onStartCommand: Service started");

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());

        startService(this.getFilesDir().toString());

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("RustService", "onDestroy: Service destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("RustService", "createNotificationChannel: Creating notification channel");
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID,
                    "RustServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            Log.d("RustService", "createNotificationChannel: Notification channel created");
        }
    }

    private Notification buildNotification() {
        Log.d("RustService", "buildNotification: Building notification");
        Notification.Builder builder;
        builder = new Notification.Builder(this, DEFAULT_CHANNEL_ID);

        builder.setContentTitle("Rust Service")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_DEFAULT);

        Log.d("RustService", "buildNotification: Notification built");

        return builder.build();
    }
}

