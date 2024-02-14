package com.example.rustapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements Application.ActivityLifecycleCallbacks {
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

        ((Button) findViewById(R.id.clearLogs)).setOnClickListener(view -> {
            try {
                new FileOutputStream(new File(this.getFilesDir(), "fsmon_log.yaml")).close();
                Toast.makeText(this, "Logs Cleared!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to clear logs!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {

        Toast.makeText(this, "MainActivity is being destroyed", Toast.LENGTH_SHORT).show();
        getApplication().unregisterActivityLifecycleCallbacks(this);
        super.onDestroy();

    }
    private int numStarted = 0;
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        numStarted++;
        if (numStarted == 1) {
            Log.d("RustService", "App is in foreground state");
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
            Log.d("RustService", "App is in background state");
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
