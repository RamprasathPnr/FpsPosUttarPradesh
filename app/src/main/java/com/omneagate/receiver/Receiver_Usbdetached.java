package com.omneagate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.omneagate.DTO.FPSAllotment;
import com.omneagate.activity.BillSuccessActivity;
import com.omneagate.printer.Usb_Printer;

/**
 * Created by user1 on 19/1/17.
 */
public class Receiver_Usbdetached extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Receiver USB Detched", "removed receiver");

//        Usb_Printer.usbdevice =null;
//        Usb_Printer.mUsb =null;
//        if (Usb_Printer.es != null && (!Usb_Printer.es.isShutdown() || !Usb_Printer.es.isTerminated())) {
//
//            Usb_Printer.es.shutdownNow();
//        }

    }
}
