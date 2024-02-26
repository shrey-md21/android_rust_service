package com.example.rustapp;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RustServiceWork extends Worker {
    public RustServiceWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("RustServiceWork", "doWork: Work started");

        Context context = getApplicationContext();
        Intent serviceIntent = new Intent(context, RustService.class);
        ContextCompat.startForegroundService(context, serviceIntent);

        Log.d("RustServiceWork", "doWork: Work completed");
        return Result.success();
    }
}
