package com.example.ankan.cryptocurrencies;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;


public class BatteryLowReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        if (batteryLevel < 50) {
            Toast.makeText(context, "Battery level is low: " + batteryLevel + "%", Toast.LENGTH_LONG).show();
        }
    }
}
