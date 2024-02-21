package com.example.rustapp;

import android.annotation.SuppressLint;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class DataUsage extends AppCompatActivity {

    private TextView dataUsageView;
    private NetworkStatsManager networkStatsManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_usage);

        dataUsageView = findViewById(R.id.data_usage);
        networkStatsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);

        long dataUsage = getDataUsage();
        dataUsageView.setText("Data Usage: " + dataUsage + " bytes");
    }

    private long getDataUsage() {
        long startTime = getStartTimeOfToday();
        long endTime = System.currentTimeMillis();

        try {
            NetworkStats.Bucket bucket = networkStatsManager.querySummaryForDevice(
                    ConnectivityManager.TYPE_MOBILE,
                    "", startTime, endTime);

            return bucket.getRxBytes() + bucket.getTxBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long getStartTimeOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}
