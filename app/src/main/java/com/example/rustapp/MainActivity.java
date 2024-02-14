package com.example.rustapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks {
    private int numStarted = 0;
    private static final String LOG_FILE_NAME = "rust_logs.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApplication().registerActivityLifecycleCallbacks(this);

        ((MaterialSwitch) findViewById(R.id.serviceToggle))
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Intent serviceIntent = new Intent(this, RustService.class);
                    if (isChecked)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            startForegroundService(serviceIntent);
                        else
                            startService(serviceIntent);
                    else
                        stopService(serviceIntent);
                });

        findViewById(R.id.clearLogs).setOnClickListener(view -> {
            try {
                new FileOutputStream(new File(this.getFilesDir(), "fsmon_log.yaml")).close();
                showToast("Logs Cleared!");
            } catch (Exception e) {
                showToast("Failed to clear logs!");
            }
        });
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
