package com.example.rustapp;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RustService extends Service {

//    private boolean running = false;
    private static final String DEFAULT_CHANNEL_ID = "RustServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String LOG_FILE_NAME = "rust_logs.txt";

    static {
        System.loadLibrary("rustapp");
    }

    private static native void startService(String filesDir);

    @Override
    public void onCreate() {
        super.onCreate();
//        running = true;
//        MyThread thread = new MyThread();
//        thread.start();
        String logMessages = "Service Created";
        Log.d("RustService",logMessages);
        appendLog(logMessages);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String logMessages = "Service Started";
        Log.d("RustService",logMessages);
        appendLog(logMessages);

        // create and display notification
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());

        // service is started
        startService(this.getFilesDir().toString());

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
//        running = false;
        String logMessages = "Service Stopped/Closed";
        Log.d("RustService", logMessages);
        appendLog(logMessages);
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
        appendLog(logMessages);

        return builder.build();
    }

    private void appendLog(String logMessages) {

        File logFile = new File(getFilesDir(), LOG_FILE_NAME);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(logMessages);
            buf.newLine();
            buf.close();

        } catch (IOException e) {
            Log.e("RustService", "Error writing to log file: " + e.getMessage());
        }
    }
//    private String getCurrentTimeStamp() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return sdf.format(new Date());
//    }

//    class MyThread extends Thread {
//        public void run() {
//            int i = 0;
//            while(running) {
//                try {
//                    Thread.sleep(10000);
//                    String logMessages = "Thread running. Count: " + i;
//                    Log.d("RustService", logMessages);
//                    appendLog(logMessages);
//                    i++;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

}
