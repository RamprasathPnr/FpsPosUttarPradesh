package com.omneagate.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.omneagate.printer.Usb_Printer;

import java.util.HashMap;

/**
 * Created by root on 4/3/17.
 */
public class IrisScannerAttached extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       Log.e("Iris Device","Device Connected");

        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (deviceList.size() > 0) {
            for (UsbDevice device : deviceList.values()) {
                Log.e("Iris", "rec interface count..." + device.getVendorId() + "  =  " + device.getInterfaceCount());
                if (device.getVendorId() == 8035) {

                    Log.e("Iris", "MainActivity.usbdevice added");
                    if (mUsbManager.hasPermission(device)) {

                    } else {
                        PendingIntent mPermissionIntent=PendingIntent.getBroadcast(context, 0, new Intent("com.omneagate.activity.USB_PERMISSION"), 0);
                        mUsbManager.requestPermission(device, mPermissionIntent);
                    }
                }
            }
        }
    }
}
