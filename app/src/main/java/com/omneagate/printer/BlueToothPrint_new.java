package com.omneagate.printer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.activity.BillSuccessActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created for BlueToothPrint
 */
public class BlueToothPrint_new {
    private static boolean receiver = false;
    private String printdata;
    Set<BluetoothDevice> pairedDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Activity context;
    static int exception_count = 0;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    private AlertDialog alertDialog;
    private ArrayList<BluetoothDevice> devicelist;
    private BroadcastReceiver mReceiver;
    private BluetoothConnector_new blue;
    static boolean exception = false;
    SharedPreferences sharedPrefs;
    private ConnectRunnable connector;
    static BlueToothPrint_new bluetoothnew;
//    private boolean tamillang = false;

    public static BlueToothPrint_new getinstance(Activity activity) {
        if (bluetoothnew == null) {
            bluetoothnew = new BlueToothPrint_new(activity);
        }
        return bluetoothnew;
    }

    private final BroadcastReceiver bluetoothreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
//                        Toast.makeText(context, "Bluetooth turned  on", Toast.LENGTH_SHORT).show();
//
//                        if (exception)
//                        connect();
                        exception_count += 1;
                        break;
                    case BluetoothAdapter.STATE_OFF:
//                        Toast.makeText(context, "Bluetooth turned  off", Toast.LENGTH_SHORT).show();
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
    private String printerDeviceAddress = "printeraddress";

    public BlueToothPrint_new(Activity context) {
        exception_count = 0;
        this.context = context;
        sharedPrefs = context.getSharedPreferences("FPS_POS",
                MODE_PRIVATE);
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
            context.registerReceiver(bluetoothreceiver, filter);
        }
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
//    private void findBT() {
//        try {
////            Log.e("findBT", "findBT()");
////            Log.e("bluetooth", "" + mBluetoothAdapter.toString());
//            //            Log.e("getAddress", "" + device.getAddress());
////            Log.e("name", "" + device.getName());
////            Log.e("desc content", "" + device.describeContents());
////            Log.e("fetchuuid", "" + device.fetchUuidsWithSdp());
////            Log.e("getbondstate", "" + device.getBondState());
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
////                Log.e("type", "" + device.getType());
////            }
////            Log.e("uuids", "" + device.getUuids());
////                    break;
////            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
////            intial_receiver();
////            context.registerReceiver(mReceiver, filter);
//            Log.e("BluetoothpairedDevices", "" + pairedDevices);
//            //int RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
//            //String mDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
//            // Log.e("pairedDevices.getClass", ""+pairedDevices.getClass().toString());
//            if (pairedDevices.size() > 0) {
//            } else {
//                Log.e("BluetoothpairedDevices", "no paired devices...");
////                printDialogStatus(context.getString(R.string.no_printer));
////                ((BillSuccessActivity) context).enableButton2();
//            }
//        } catch (Exception e) {
//            Log.e("findBT Error", e.toString(), e);
//        } finally {
////            ((BillSuccessActivity) context).enableButton();
//        }
//    }

    private void intial_receiver() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String action = intent.getAction();
                    // When discovery finds a device
                    if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                        // Get the BluetoothDevice object from the Intent
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        String deviceName = device.getName();
                        String deviceAddress = device.getAddress();
                        Log.e("bluetoothActionA", "" + deviceName + "--->" + deviceAddress);
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        Log.e("bluetoothActionB", "" + action);
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                        Log.e("bluetoothActionC", "" + action);
                    }
                } catch (Exception e) {
                    System.out.println("Broadcast Error : " + e.toString());
                } finally {
//                ((BillSuccessActivity) context).enableButton();
                }
            }
        };
    }
//    public void printCall() {
//        try {
//            findBT();
//            /*openBT();//socket connection
//            BluetoothPrintData();*/
//        } catch (Exception e) {
//            Log.e("Print Call", e.toString(), e);
//        } finally {
////            ((BillSuccessActivity) context).enableButton();
//        }
//    }

    private String getstring(int i) {
        return context.getResources().getString(i);
    }

    // Tries to open a connection to the bluetooth printer device
    public void connect() {
        if (connector != null) {
            connector.cancel();
            connector = null;
        }
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
        connector = new ConnectRunnable();
        connector.start();
    }

    public void opendialog(String printdata) {
        this.printdata = printdata;
        String device_address = sharedPrefs.getString(printerDeviceAddress, "");
        if (!device_address.isEmpty()) {
//            if (mBluetoothAdapter == null)
//                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mmDevice == null || !mmDevice.getAddress().equals(device_address)) {
                mmDevice = mBluetoothAdapter.getRemoteDevice(device_address);
            }
            connect();
        } else {
            getdevicelist();
            if (devicelist != null && devicelist.size() > 0)
                dialog(devicelist, R.layout.bluetooth_device_alert, context.getResources().getString(R.string.title_bluetooth_devices));
            else {
                Toast.makeText(context, getstring(R.string.pair_device), Toast.LENGTH_SHORT).show();
                ((BillSuccessActivity) context).enableButton2();
            }
        }
    }

    private class ConnectRunnable extends Thread {
        public void run() {
            try {
                if (mmSocket == null || !mmSocket.isConnected()) {
                    blue = new BluetoothConnector_new(context, mmDevice, false, mBluetoothAdapter, null);
                    mmSocket = blue.connect();
                }
                if (mmSocket != null) {
                    exception = false;
                    mmOutputStream = mmSocket.getOutputStream();
                    if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                        TamilPrinting.getinstance().print(mmOutputStream, context, (Activity) context, printdata, context.getSharedPreferences("FPS_POS",
                                MODE_PRIVATE).getString("printeraddress", ""), 23);
                    } else {
                        IntentPrint(printdata);
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, context.getResources().getString(R.string.print_success), Toast.LENGTH_SHORT).show();
                            ((BillSuccessActivity) context).enableButton2();
                        }
                    });
                    Log.e("Tag", "bluetooth connected");
                } else {
                    exception = true;
//                    Toast.makeText(context, context.getResources().getString(R.string.waittoast), Toast.LENGTH_SHORT).show();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, context.getResources().getString(R.string.no_printer), Toast.LENGTH_SHORT).show();
                            ((BillSuccessActivity) context).enableButton2();
                        }
                    });
                    Log.e("Tag", "bluetooth not connected");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Tag", "bluetooth not connected");
            }
        }

        public void cancel() {
//            try {
//                if (mmSocket != null)
//                    mmSocket.close();
//            } catch (IOException e) {
//                Log.d("TAG", "Canceled connection", e);
//            }
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            if (mmSocket != null)
                mmSocket.close();
            Log.v("TAG", "SockectClosed");
        } catch (IOException ex) {
            Log.d("TAG", "Could not close exisiting socket", ex);
        }
    }

    public void IntentPrint(String txtValue) {
        try {
//            Thread.sleep(2000);
            Log.e("txtValue", txtValue);
            byte[] buffer = txtValue.getBytes();
            Log.e("buffer", "" + buffer);
            byte[] PrintHeader = {(byte) 0x55, 0, 0};
            Log.e("PrintHeader", "" + PrintHeader);
            PrintHeader[2] = (byte) buffer.length;
            Log.e("PrintHeader[2]", "" + PrintHeader[2]);
            if (PrintHeader.length > 128) {
                String value = "\nValue is more than 128 size\n";
            } else {
//                Set font size here
//                byte[] arrayOfByte1 = { 27, 33, 0 };
//                byte[] format = { 27, 33, 0 };
//                format[2] = ((byte)(0x1 | arrayOfByte1[2]));
//                mmOutputStream.write(format);
                for (int i = 4; i <= PrintHeader.length - 1; i++) {
                    //Log.e("PrintHeader.length", ""+PrintHeader.length);
                    if (mmOutputStream != null) {
                        mmOutputStream.write(PrintHeader[i]);
                        // Log.e("1* ", "PrintHeader[i] = "+PrintHeader[i]);
                    }
                }
                for (int i = 4; i <= buffer.length - 1; i++) {
                    if (mmOutputStream != null) {
                        //  byte[] format = { 27, 33, 0 };
                        // Bold
                        //format[2] = ((byte)(0x8 | PrintHeader[2]));
                        //Small
                        // format[2] = ((byte)(0x1 | PrintHeader[2]));
                        //UnderLine
                        //format[2] = ((byte)(0x80 | PrintHeader[2]));
                        ////  byte[] format = {15, 33, 35 }; // manipulate your font size in the second parameter
                        //  byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment
                        //  mmOutputStream.write(format);
                        //  mmOutputStream.write(center);
                        // out.write(str.getBytes(),0,str.getBytes().length);
                        mmOutputStream.write(buffer[i]);
                        // Log.e("2* ", "buffer[i] = "+buffer[i]);
                    }
                }
//                if (mmOutputStream != null)
//                    mmOutputStream.flush();
//                printDialogStatus(context.getString(R.string.print_success));
//                if (mReceiver != null)
//                    context.unregisterReceiver(mReceiver);
//                if (mmOutputStream != null)
//                    mmOutputStream.close();
//                if (mmInputStream != null)
//                    mmInputStream.close();
//                mmSocket.close();
//                blue.bluetoothSocket.close();
                try {
                    if (receiver) {
                        context.unregisterReceiver(bluetoothreceiver);
                        receiver = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    receiver = false;
                }
            }
        } catch (Exception e) {
            Log.e("errorPrint", e.toString(), e);
        } finally {
//            ((BillSuccessActivity) context).enableButton();
        }
    }

    private String pad(String value, int length, String with) {
        StringBuilder result = new StringBuilder(length);
        // Pre-fill a String value
        result.append(fill(Math.max(0, length - value.length()), with));
        result.append(value);
        return result.toString();
    }
//    private void printDialogStatus(final String printerStatus) {
//        context.runOnUiThread(new Runnable() {
//            public void run() {
//                Util.messageBar(context, printerStatus);
//                Log.e("Local class", context.getLocalClassName());
//                if (context.getLocalClassName().contains("BillSuccessActivity")) {
//                    ((BillSuccessActivity) context).enableButtonAfterPrint();
//                }
//            }
//        });
//    }

    private String fill(int length, String with) {
        StringBuilder sb = new StringBuilder(length);
        while (sb.length() < length) {
            sb.append(with);
        }
        return sb.toString();
    }
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                String action = intent.getAction();
//                // When discovery finds a device
//                if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                    // Get the BluetoothDevice object from the Intent
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    String deviceName = device.getName();
//                    String deviceAddress = device.getAddress();
//                    Log.e("bluetoothActionA", "" + deviceName + "--->" + deviceAddress);
//                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                    Log.e("bluetoothActionB", "" + action);
//                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//                    Log.e("bluetoothActionC", "" + action);
//                }
//            } catch (Exception e) {
//                System.out.println("Broadcast Error : " + e.toString());
//            } finally {
////                ((BillSuccessActivity) context).enableButton();
//            }
//        }
//    };

    private View dialog(final ArrayList<BluetoothDevice> devicelist, int cus_layout, String title) {
        inflater = (LayoutInflater)
                context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        builder = new AlertDialog.Builder(context);
        View layout = inflater.inflate(cus_layout,
                null);
        LinearLayout llay = (LinearLayout) layout.findViewById(R.id.container);
        ScrollView sv = (ScrollView) layout.findViewById(R.id.scrollView);
        for (int i = 0; i < devicelist.size(); i++) {
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutparams.setMargins(10, 10, 10, 10);
            TextView txt = new TextView(context);
            txt.setLayoutParams(layoutparams);
            txt.setText(devicelist.get(i).getName() + "\n" + devicelist.get(i).getAddress());
            txt.setTextSize(20);
            txt.setTag(i);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mmDevice = devicelist.get((int) v.getTag());
                    sharedPrefs.edit().putString(printerDeviceAddress, mmDevice.getAddress()).commit();
                    connect();//socket connection
//                        Log.e("socket status",""+Boolean.toString(blue.success));
////                        mmSocket=blue.bluetoothSocket.getOutputStream();
//                        while (!blue.success) {
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            Log.e("socket status",""+Boolean.toString(blue.success));
//                            if (blue.success)
//                                break;
//                        }
//                    mBluetoothAdapter.disable();
                    alertDialog.hide();
                }
            });
            llay.addView(txt);
        }
//            }
//        }
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
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == MY_BLUETOOTH_ENABLE_REQUEST_ID) {
//            if (resultCode == RESULT_OK) {
//                // Request granted - bluetooth is turning on...
//            }
//            if (resultCode == RESULT_CANCELED) {
//                // Request denied by user, or an error was encountered while
//                // attempting to enable bluetooth
//            }
//        }
//    }
}