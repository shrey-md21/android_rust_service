package com.example.rustapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RustService extends Service {
    private static final String DEFAULT_CHANNEL_ID = "RustServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    public static final String TIME_EXTRA = "timeExtra";

    public static final String TIMER_UPDATED = "timeUpdated";
    private Timer timer;

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
        startForeground(NOTIFICATION_ID, BuildNotification());

        // service is started
        startService(this.getFilesDir().toString());

        // default time to 0
        double time = 0.0;
        startTimer(time);

        // to show notification that the app is active once again
        startForeground(NOTIFICATION_ID, RestartNotification());

        return Service.START_STICKY;
    }

    private void startTimer(double time) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimeTask(time), 0, 1000);
    }

    private class TimeTask extends TimerTask {
        private double time;
        TimeTask(double time) {
            this.time = time;
        }
        @Override
        public void run() {
            Intent intent = new Intent(TIMER_UPDATED);
            time++;
            intent.putExtra(TIME_EXTRA, time);
            sendBroadcast(intent);
        }
    }

    @Override
    public void onDestroy() {
        String logMessages = "Service Stopped";
        Log.d("RustService", logMessages);

        if (timer != null) {
            timer.cancel();
        }
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

    private Notification BuildNotification() {
        Notification.Builder builder;
        builder = new Notification.Builder(this, DEFAULT_CHANNEL_ID);

        builder.setContentTitle("Rust Service")
                .setContentText("Running...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_DEFAULT);

        String logMessages = "Notification built and sent";
        Log.d("RustService", logMessages);

        return builder.build();
    }

    private Notification RestartNotification() {
        Notification.Builder builder;
        builder = new Notification.Builder(this, DEFAULT_CHANNEL_ID);

        builder.setContentTitle("Rust Service is Active")
                .setContentText("Tap to open the App")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(createPendingIntent())
                .setPriority(Notification.PRIORITY_DEFAULT);

        return builder.build();
    }

    private PendingIntent createPendingIntent() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
