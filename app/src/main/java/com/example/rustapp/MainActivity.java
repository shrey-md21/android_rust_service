package com.example.rustapp;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks {
    private int numStarted = 0;
    private static final String LOG_FILE_NAME = "rust_logs.txt";
    private TextView appTimer, appTimeSpent;
    private double timeSpent = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApplication().registerActivityLifecycleCallbacks(this);

        ((MaterialSwitch) findViewById(R.id.serviceToggle))
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Intent serviceIntent = new Intent(this, RustService.class);
                    if (isChecked) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            startForegroundService(serviceIntent);
                        else
                            startService(serviceIntent);
                    }
                    else {
                        stopService(serviceIntent);
                    }
                });

        appTimer = findViewById(R.id.appTimer);
        appTimeSpent = findViewById(R.id.appTimeSpent);

        findViewById(R.id.clearLogs).setOnClickListener(view -> {
            try {
                new FileOutputStream(new File(this.getFilesDir(), "fsmon_log.yaml")).close();
                showToast("Logs Cleared!");
            } catch (Exception e) {
                showToast("Failed to clear logs!");
            }
        });



    }

    private BroadcastReceiver timeUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(RustService.TIMER_UPDATED)) {
                double time = intent.getDoubleExtra(RustService.TIME_EXTRA, 0.0);
                updateTimer(time);
            }
        }
    };

    private void updateTimer(double time) {
        String timeString = getTimeString(time);
        String timeSpentString = getTimeString(time);
        appTimer.setText(timeString);
//        come on shreyas, figure out how you have to manage the state.
        appTimeSpent.setText(timeSpentString);

    }

    private void resetTimer() {
        appTimer.setText(0);
    }

    private String getTimeString(double time) {
        int hours = (int) (time / 3600);
        int minutes = (int) ((time % 3600)/60);
        int seconds = (int) (time % 60);

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(RustService.TIMER_UPDATED);
        MainActivity.this.registerReceiver(timeUpdateReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {

        showToast("MainActivity is being destroyed");
        getApplication().unregisterActivityLifecycleCallbacks(this);
        saveLogcatToFile();
        super.onDestroy();

    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void saveLogcatToFile() {
        String folderName = "Logs";
        File folderDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), folderName);

        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }
        File logFile = new File(folderDir, LOG_FILE_NAME);

        try {
            Process process = Runtime.getRuntime().exec("logcat -d | grep com.example.rustapp");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buf.write(line);
                buf.newLine();
            }
            buf.close();

            showToast("Logcat saved to: " + logFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            showToast("Error saving logcat to file: " + e.getMessage());
        }
    }
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        numStarted++;
        if (numStarted == 1) {
            Log.d("MainActivity", "App is in foreground state");
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        numStarted--;
        if (numStarted == 0) {
            Log.d("MainActivity", "App is in background state");
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
