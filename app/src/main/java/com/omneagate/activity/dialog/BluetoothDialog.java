package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.omneagate.activity.LoginActivity;
import com.omneagate.activity.R;

import java.lang.reflect.Method;
import java.util.List;

public class BluetoothDialog extends Dialog {


    Context activity;

    com.omneagate.activity.dialog.DeviceListAdapter mAdapter;

    List<BluetoothDevice> mDeviceList;

    BluetoothDevice deviceName;

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent
                        .getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                                BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(
                        BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                        BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED
                        && prevState == BluetoothDevice.BOND_BONDING) {
                    bluetoothWorkFinished();
                } else if (state == BluetoothDevice.BOND_NONE
                        && prevState == BluetoothDevice.BOND_BONDED) {
                }

                mAdapter.notifyDataSetChanged();
            }
        }
    };


    public BluetoothDialog(Context context,
                           List<BluetoothDevice> _mDeviceList) {
        super(context);
        activity = context;
        mDeviceList = _mDeviceList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.bluetoothprint);
        System.out.println("I have on the page");
        setCanceledOnTouchOutside(false);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ListView mListView = (ListView) findViewById(R.id.listView1);
        mAdapter = new DeviceListAdapter(activity);

        mAdapter.setData(mDeviceList);
        if (mDeviceList.size() > 0) {
            ((TextView) findViewById(R.id.noDevices)).setVisibility(View.INVISIBLE);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = mDeviceList.get(position);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.i("Paired", device.getAddress());
                    deviceName = device;
                    bluetoothWorkFinished();
                } else {
                    pairDevice(device);
                }
            }
        });
        activity.registerReceiver(mPairReceiver, new IntentFilter(
                BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }

    private void bluetoothWorkFinished() {
        dismiss();
        activity.unregisterReceiver(mPairReceiver);
        ((LoginActivity) activity).bluetoothRegister(deviceName);
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond",
                    (Class[]) null);
            method.invoke(device, (Object[]) null);
            deviceName = device;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}