package com.omneagate.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.activity.dialog.BluetoothDialog;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user1 on 21/4/15.
 */
public class BluetoothPairing {

    Context context;

    final Handler handler = new Handler();
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    Timer timer;
    TimerTask timerTask;
    SingBroadcastReceiver mReceiver;
    CustomProgressDialog progressBar;

    public BluetoothPairing(Context context) {
        this.context = context;

    }

    public void bluetoothPair() {
        progressBar = new CustomProgressDialog(context);
        progressBar.show();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth Not Supported", Toast.LENGTH_LONG).show();
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
    }

    private void initialiseTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        mBluetoothAdapter.cancelDiscovery();
                        bluetoothDeviceList();
                    }
                });
            }
        };
    }

    private void bluetoothDeviceList() {
        try {
            context.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        } finally {
            progressBar.dismiss();
            new BluetoothDialog(context, mDeviceList).show();
        }

    }

    private class SingBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);
            }
        }
    }

    ;


}
