package com.example.rustapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.TextView;

public class BatteryStatus extends MainActivity{
    public TextView battery_information = findViewById(R.id.battery_information);
//    registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            boolean present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            battery_information.setText("health" + health + "\n" +
                                        "level" + level + "\n" +
                                        "plugged" + plugged + "\n" +
                                        "status : " + status + "\n" +
                                        "present : " + present + "\n" +
                                        "technology : " + technology + "\n" +
                                        "temperature : " + temperature + "\n" +
                                        "voltage : " + voltage + "\n");
        }
    };
}
