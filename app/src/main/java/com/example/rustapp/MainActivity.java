package com.example.rustapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.materialswitch.MaterialSwitch;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "MainActivity";
    private int numStarted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApplication().registerActivityLifecycleCallbacks(this);

        ((MaterialSwitch) findViewById(R.id.serviceToggle))
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Intent serviceIntent = new Intent(this, RustService.class);
                    if (isChecked) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            startForegroundService(serviceIntent);
                            startService(serviceIntent);
                        }
                    }
                    else {
                        stopService(serviceIntent);
                    }
                });

        findViewById(R.id.clearLogs).setOnClickListener(view -> {
            try {
                new FileOutputStream(new File(this.getFilesDir(), "fsmon_log.yaml")).close();
                showToast("Logs Cleared!");
            } catch (Exception e) {
                showToast("Failed to clear logs!");
                Log.e(TAG, "Failed to clear logs", e);
            }
        });
    }

    private void startScheduledWork() {
        Log.d(TAG, "Starting scheduled work");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(RustServiceWork.class, 2*60*1000, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag("rust_service_work")
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);
        Log.d(TAG, "Scheduled work started");
    }

    private void stopScheduledWork() {
        Log.d(TAG, "Stopping scheduled work");
        WorkManager.getInstance(this).cancelAllWorkByTag("rust_service_work");
        Log.d(TAG, "Scheduled work stopped");
    }

    @Override
    protected void onDestroy() {
        showToast("MainActivity is being destroyed");
        getApplication().unregisterActivityLifecycleCallbacks(this);
        super.onDestroy();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Activity created");
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        numStarted++;
        if (numStarted == 1) {
            Log.d(TAG, "App is in foreground state");
            stopScheduledWork();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Log.d(TAG, "Activity resumed");
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.d(TAG, "Activity paused");
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        numStarted--;
        if (numStarted == 0) {
            Log.d(TAG, "App is in background state");
            startScheduledWork();
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle savedInstanceState) {
        Log.d(TAG, "Saving instance state");
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d(TAG, "Activity destroyed");
    }
}
