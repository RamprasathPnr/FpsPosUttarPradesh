package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.omneagate.activity.R;

import java.lang.reflect.Method;
import java.util.List;

public class BluetoothDialog extends Dialog {

    Context activity;
    ListView mListView;
    DeviceListAdapter mAdapter;
    List<BluetoothDevice> mDeviceList;
    BluetoothDevice deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.bluetoothprint);
        setCanceledOnTouchOutside(false);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mListView = (ListView) findViewById(R.id.listView1);
        mAdapter = new DeviceListAdapter(activity);

        mAdapter.setData(mDeviceList);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = mDeviceList.get(position);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    SharedPreferences.Editor preferences = activity.getSharedPreferences("FPSPOS", Context.MODE_PRIVATE).edit();
                    preferences.putString("printer", device.getAddress());
                    preferences.commit();
                    devicePaired();
                } else
                    pairDevice(device);
            }
        });

        mListView.setAdapter(mAdapter);

        activity.registerReceiver(mPairReceiver, new IntentFilter(
                BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    }


    public BluetoothDialog(Context context,
                           List<BluetoothDevice> _mDeviceList) {
        super(context);
        activity = context;
        mDeviceList = _mDeviceList;
    }


    private void bluetoothWorkFinished() {
        activity.unregisterReceiver(mPairReceiver);
        /*((WaiterActivity) activity).bluetoothRegister();*/
    }

    private void devicePaired() {
        bluetoothWorkFinished();
        dismiss();
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
                    SharedPreferences.Editor preferences = context.getSharedPreferences("FPSPOS", Context.MODE_PRIVATE).edit();
                    preferences.putString("printer", deviceName.getAddress());
                    preferences.commit();
                    devicePaired();
                } else if (state == BluetoothDevice.BOND_NONE
                        && prevState == BluetoothDevice.BOND_BONDED) {
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    };


}