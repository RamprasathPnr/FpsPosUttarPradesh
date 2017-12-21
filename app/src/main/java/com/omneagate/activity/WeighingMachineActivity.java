package com.omneagate.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.activity.dialog.BluetoothDialog;
import com.omneagate.printer.BluetoothConnector_new;
import com.omneagate.printer.TamilPrinting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;



public class WeighingMachineActivity extends AppCompatActivity implements View.OnClickListener {

    private static boolean receiver = false;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    String device_address ="";
    Set<BluetoothDevice> pairedDevices;
    private ArrayList<BluetoothDevice> devicelist;
    private ConnectRunnable connector;
    BluetoothSocket mmSocket;
    private BluetoothConnector_new blue;
    private LayoutInflater inflater;
    static boolean exception = false;
    OutputStream mmOutputStream;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    InputStream mInputStream;
    Button ConnectDevice,getWeight;
    TextView OutputValue;
    static int exception_count = 0;
    private String TAG=WeighingMachineActivity.class.getCanonicalName();
    Handler bluetoothIn;
    final int handlerState = 0;

    String readMessage="";

    private final BroadcastReceiver bluetoothreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        exception_count += 1;
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        mBluetoothAdapter.enable();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weighing_machine);
        OutputValue=(TextView)findViewById(R.id.OutputValue);

        getWeight=(Button)findViewById(R.id.GetWeight);
        getWeight.setOnClickListener(this);
        ConnectDevice=(Button)findViewById(R.id.ConnectDevice);
        ConnectDevice.setOnClickListener(this);

        exception = false;
        if (mBluetoothAdapter == null)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                while (!mBluetoothAdapter.isEnabled()) {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
        if (!receiver) {
            receiver = true;
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothreceiver, filter);
        }
        bluetoothIn =new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                if (msg.what == handlerState) {
                     readMessage += (String) msg.obj;
                    Log.e(TAG,"Received Response Message : "+readMessage);
                    OutputValue.setText(readMessage);
                }
            }

        };

    }

    public void opendialog() {
       try {

           if (device_address != null && !device_address.isEmpty()) {
               if (mmDevice == null || !mmDevice.getAddress().equals(device_address)) {
                   mmDevice = mBluetoothAdapter.getRemoteDevice(device_address);
               }
               connect();
           } else {
               getdevicelist();
               if (devicelist != null && devicelist.size() > 0)
                   dialog(devicelist, R.layout.bluetooth_device_alert, getResources().getString(R.string.title_bluetooth_devices));
               else {
                   Toast.makeText(WeighingMachineActivity.this, getString(R.string.pair_device), Toast.LENGTH_SHORT).show();
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
    public void connect() {
        if (connector != null) {
            connector = null;
        }
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
        connector = new ConnectRunnable();
        connector.start();
    }

    private void getdevicelist() {
        mBluetoothAdapter.startDiscovery();
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        mBluetoothAdapter.cancelDiscovery();
        devicelist = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice device : pairedDevices) {
            devicelist.add(device);
        }
    }
    private class ConnectRunnable extends Thread {

        public void run() {
            try {
                if (mmSocket == null || !mmSocket.isConnected()) {
                    blue = new BluetoothConnector_new(WeighingMachineActivity.this, mmDevice, false, mBluetoothAdapter, null);
                    mmSocket = blue.connect();
                }
                if (mmSocket != null) {
                    exception = false;
                    mInputStream = mmSocket.getInputStream();
                    mmOutputStream =mmSocket.getOutputStream();

                    byte[] buffer = new byte[1024];
                    int bytes;

                        // Keep looping to listen for received messages
                        while (true) {
                            try {
                                bytes = mInputStream.read(buffer);            //read bytes from input buffer
                                String readMessage = new String(buffer, 0, bytes);
                                Log.e(TAG,"Weight : "+readMessage);
                                // Send the obtained bytes to the UI Activity via handler
                                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                            } catch (IOException e) {
                                break;
                            }
                        }
/*

                    if (mInputStream != null) {
                        StringBuilder sb = new StringBuilder();
                        String line;

                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream, "UTF-8"));
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                       Log.e(TAG,"Get weight"+sb.toString());
                       // bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                    }
*/

                } else {
                    exception = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeighingMachineActivity.this, getResources().getString(R.string.no_printer), Toast.LENGTH_SHORT).show();

                        }
                    });
                    Log.e("Tag", "bluetooth not connected");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Tag", "bluetooth not connected");
            }
        }
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();
            try {
                mmOutputStream.write(msgBuffer);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }

    }
    private View dialog(final ArrayList<BluetoothDevice> devicelist, int cus_layout, String title) {
        inflater = (LayoutInflater)WeighingMachineActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        builder = new AlertDialog.Builder(WeighingMachineActivity.this);
        View layout = inflater.inflate(cus_layout,
                null);
        LinearLayout llay = (LinearLayout) layout.findViewById(R.id.container);
        ScrollView sv = (ScrollView) layout.findViewById(R.id.scrollView);
        for (int i = 0; i < devicelist.size(); i++) {
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutparams.setMargins(10, 10, 10, 10);
            TextView txt = new TextView(WeighingMachineActivity.this);
            txt.setLayoutParams(layoutparams);
            txt.setText(devicelist.get(i).getName() + "\n" + devicelist.get(i).getAddress());
            txt.setTextSize(20);
            txt.setTag(i);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mmDevice = devicelist.get((int) v.getTag());
                   // sharedPrefs.edit().putString(printerDeviceAddress, mmDevice.getAddress()).commit();
                    connect();//socket connection
                    alertDialog.hide();
                }
            });
            llay.addView(txt);
        }
        sv.invalidate();
        sv.requestLayout();
        TextView tv = (TextView) layout.findViewById(R.id.title);
        tv.setText(title);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        return layout;
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ConnectDevice:
                Log.e(TAG,"Connect Device Clicked");
                 opendialog();
                break;
            case R.id.GetWeight:
                Log.e(TAG,"Demand Data");
             //   opendialog();
                readMessage="";
                if(connector !=null){
                    connector.write("W");
                }

                break;
            default:
                break;
        }

    }
}
