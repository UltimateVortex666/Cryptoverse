package com.example.ankan.cryptocurrencies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

public class UsbConnectorReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            Toast.makeText(context, "USB device attached", Toast.LENGTH_LONG).show();
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            Toast.makeText(context, "USB device detached", Toast.LENGTH_LONG).show();
        }
    }
}
