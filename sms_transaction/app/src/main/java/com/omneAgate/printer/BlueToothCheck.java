package com.omneagate.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user1 on 21/4/15.
 */
public class BlueToothCheck {
    private BluetoothAdapter mBluetoothAdapter;
    Context context;
    SingBroadcastReceiver mReceiver;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    String content;

    public BlueToothCheck(Context context, String content) {
        this.context = context;
        this.content = content;
    }

    public boolean checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
            mReceiver = new SingBroadcastReceiver();
            IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mReceiver, ifilter);
            mBluetoothAdapter.startDiscovery();
            timer = new Timer();
            initialiseTimerTask();
            timer.schedule(timerTask, 30000);
        }
        return true;
    }

    private void initialiseTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        mBluetoothAdapter.cancelDiscovery();
                        searchFinished();
                    }
                });
            }
        };
    }

    private void searchFinished() {
        try {
            context.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.e("Unregister", e.toString(), e);
        }
    }

    private class SingBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                SharedPreferences pref = context.getSharedPreferences("FPSPOS", Context.MODE_PRIVATE);
                String myPairedDevice = pref.getString("printer", "");
                if (myPairedDevice.equals(device.getAddress())) {
                    mBluetoothAdapter.cancelDiscovery();
                    BlueToothPrinter bt = new BlueToothPrinter(device);
                    bt.setPrintData(content);
                    bt.printJob();
                    searchFinished();
                }
            }
        }
    }

    ;

}
