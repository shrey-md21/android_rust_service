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
        String logMessages = "Service Created";
        Log.d("RustService",logMessages);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String logMessages = "Service Started";
        Log.d("RustService",logMessages);

        // create and display notification
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());

        // service is started
        startService(this.getFilesDir().toString());

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        String logMessages = "Service Stopped/Closed";
        Log.d("RustService", logMessages);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID,
                    "RustServiceChannel", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, DEFAULT_CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }

        builder.setContentTitle("Rust Service")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_DEFAULT);

        String logMessages = "Notification built and sent";
        Log.d("RustService", logMessages);

        return builder.build();
    }
}
