package com.omneagate.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import lombok.Data;


@Data
public class BlueToothPrinter implements Printer {


    private BluetoothAdapter mBluetoothAdapter;

    private boolean connectionStatus = false;

    private String printData;
    BluetoothDevice selectedDevice;

    private BluetoothSocket mmSocket = null;

    public void setSelectedDevice(BluetoothDevice bt) {
        selectedDevice = bt;
    }

    // Create a BroadcastReceiver for ACTION_FOUND

    @Override
    public void print() {
        printJob();
    }

    @Override
    public void discover() {

    }

    public BlueToothPrinter(BluetoothDevice device) {
        selectedDevice = device;
        /* printJob(); */
    }

    BlueToothPrinter() {
        /* printJob(); */
    }

    private void connect(BluetoothDevice device) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (selectedDevice == null) {
            return;
        }
        if (mBluetoothAdapter == null) {
            return;
        }
        BluetoothConnector bc = new BluetoothConnector(selectedDevice, true,
                mBluetoothAdapter, null);
        try {
            mmSocket = bc.connect().getUnderlyingSocket();
            connectionStatus = true;
        } catch (Exception e) {
            //log.error("Error connecting blue tooth socket", e);
            mmSocket = null;
            connectionStatus = false;
            clear();

        }
    }

    public void printJob() {
        System.out
                .println("printdata is not set !!!!!printdata is not set !!!!!"
                        + printData);
        if (printData == null) {
            Log.e("printdata ", "printdata is not set !!!!!");
            return;
        }
        if (connectionStatus == false)
            connect(selectedDevice);

        if (mmSocket == null) {
            Log.e("Socket is null", "Socket is null");
            return;
        }

        try {
            mmSocket.getOutputStream().write(printData.getBytes());
            mmSocket.getOutputStream().write("\n\n".getBytes());
            mmSocket.getOutputStream().flush();
        } catch (Exception e) {
            //log.error("Error", e);
            Log.e("BTPrinter", e.toString(), e);
            clear();
        }
    }

    public void clear() {
        selectedDevice = null;
    }

}
