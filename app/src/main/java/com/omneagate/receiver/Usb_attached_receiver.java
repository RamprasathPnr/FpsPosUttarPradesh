package com.omneagate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.omneagate.printer.Usb_Printer;

import java.util.HashMap;

/**
 * Created by user1 on 1/2/17.
 */
public class Usb_attached_receiver extends BroadcastReceiver {
    String TAG = "Usb_attached_receiver";
//    static ArrayList<UsbManager> usbmanagerlist = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "attached receiver");
//        Toast.makeText(context, "Device Attached ", Toast.LENGTH_SHORT).show();
//        if (usbmanagerlist.size() == 2){
//            usbmanagerlist.clear();
//            Log.e(TAG, "usbmanagerlist.clear()");
//        }


        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (deviceList.size() > 0) {
            for (UsbDevice device : deviceList.values()) {
                Log.e(TAG, "rec interface count..." + device.getVendorId() + "  =  " + device.getInterfaceCount());
                if (device.getVendorId() == 4070 && device.getInterfaceCount() == 1) {
                    Usb_Printer.usbdevice = device;
                    Usb_Printer.usbmanager = mUsbManager;
//                    MainActivity.usbdevice = device;
//                    MainActivity.mmanager=  mUsbManager;
                    Log.e(TAG, "MainActivity.usbdevice added");
//                    if (mUsbManager.hasPermission(device)) {
//                        Toast.makeText(this, "Connecting...", Toast.LENGTH_SHORT).show();
//
//                        this.es.submit(new TaskOpen(this.mUsb, mUsbManager, device, this));
//                    } else {
//                        mUsbManager.requestPermission(device, mPermissionIntent);
//                    }
                }
            }
        }
    }
}
