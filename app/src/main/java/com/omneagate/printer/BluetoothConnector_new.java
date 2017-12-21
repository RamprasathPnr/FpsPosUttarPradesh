package com.omneagate.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by user1 on 22/9/16.
 */
public class BluetoothConnector_new {
    private final Context context;
    BluetoothSocketWrapper bluetoothSocket;
    private BluetoothDevice device;
    private boolean secure;
    private BluetoothAdapter adapter;
    private List<UUID> uuidCandidates;
    private int candidate = 0;
    boolean success = false;
    private int exceptioncount = 0;

    /**
     * @param device         the device
     * @param secure         if connection should be done via a secure socket
     * @param adapter        the Android BT adapter
     * @param uuidCandidates a list of UUIDs. if null or empty, the Serial PP id is used
     */
    public BluetoothConnector_new(Context context, BluetoothDevice device, boolean secure, BluetoothAdapter adapter,
                                  List<UUID> uuidCandidates) {
        exceptioncount = 0;
        this.context = context;
        this.device = device;
        this.secure = secure;
        this.adapter = adapter;
        this.uuidCandidates = uuidCandidates;
        if (this.uuidCandidates == null || this.uuidCandidates.isEmpty()) {
            this.uuidCandidates = new ArrayList<UUID>();
            this.uuidCandidates.add(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//            this.uuidCandidates.add(UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
        }

    }

    public BluetoothSocket connect() throws IOException {
        success = false;
        while (!success) {
            if (adapter.isEnabled()) {
                selectSocket();
                exceptioncount += 1;
                try {
//                    Thread.sleep(500);
                    bluetoothSocket.connect();
                    success = true;
                    Log.e("Tag", "Socket connected");
                    break;
                } catch (Exception e) {
                    //try the fallback
                    Log.e("Tag", "Socket not connected");
                    if (bluetoothSocket.getUnderlyingSocket().isConnected()) {
                        bluetoothSocket.close();
                    }
                    try {
                        bluetoothSocket = new FallbackBluetoothSocket(bluetoothSocket.getUnderlyingSocket());
//                        Thread.sleep(500);
                        bluetoothSocket.connect();
                        success = true;
                        Log.e("Tag", "Socket fallback connected");
                        break;
                    } catch (Exception e1) {
                        Log.e("BT", "Fallback failed. Cancelling.", e1);
//                        try {
//                            Thread.sleep(2000);
//                        } catch (Exception e2) {
//                            e2.printStackTrace();
//                        }
                    }
                    Log.e("exceptioncount", "" + exceptioncount);
                    if (exceptioncount > 2) {
                        if(BlueToothPrint_new.exception_count > 2){
                            break;
                        }
                        exceptioncount = 0;
                        adapter.disable();
//                        bluetoothSocket = null;
//                        break;
                    }
                }
            } else {
                adapter.enable();
                try {
                    Thread.sleep(3000);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
//                break;
            }
        }
        if (!success) {
            bluetoothSocket = null;
            BlueToothPrint_new.exception = true;
//            adapter.disable();
//            adapter.enable();
//            throw new IOException("Could not connect to device: " + device.getAddress());
        }
        return bluetoothSocket.getUnderlyingSocket();
    }

    private boolean selectSocket() throws IOException {
//        if (candidate >= uuidCandidates.size()) {
//            return false;
//        }
        BluetoothSocket tmp;
        UUID uuid = uuidCandidates.get(0);
        Log.i("BT", "Attempting to connect to Protocol: " + uuid);
        if (secure) {
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } else {
            tmp = device.createInsecureRfcommSocketToServiceRecord(uuid);
        }
        bluetoothSocket = new NativeBluetoothSocket(tmp);
//        if (candidate == 0)
//            candidate = 1;
//        else
//            candidate = 0;
        return true;
    }

    public static interface BluetoothSocketWrapper {
        InputStream getInputStream() throws IOException;

        OutputStream getOutputStream() throws IOException;

        String getRemoteDeviceName();

        void connect() throws IOException;

        String getRemoteDeviceAddress();

        void close() throws IOException;

        BluetoothSocket getUnderlyingSocket();
    }

    public static class NativeBluetoothSocket implements BluetoothSocketWrapper {
        private BluetoothSocket socket;

        public NativeBluetoothSocket(BluetoothSocket tmp) {
            this.socket = tmp;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        @Override
        public String getRemoteDeviceName() {
            return socket.getRemoteDevice().getName();
        }

        @Override
        public void connect() throws IOException {
            socket.connect();
        }

        @Override
        public String getRemoteDeviceAddress() {
            return socket.getRemoteDevice().getAddress();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }

        @Override
        public BluetoothSocket getUnderlyingSocket() {
            return socket;
        }
    }

    public class FallbackBluetoothSocket extends NativeBluetoothSocket {
        private BluetoothSocket fallbackSocket;

        public FallbackBluetoothSocket(BluetoothSocket tmp) throws FallbackException {
            super(tmp);
            try {
                Class<?> clazz = tmp.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                fallbackSocket = (BluetoothSocket) m.invoke(tmp.getRemoteDevice(), params);
            } catch (Exception e) {
                throw new FallbackException(e);
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fallbackSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return fallbackSocket.getOutputStream();
        }

        @Override
        public void connect() throws IOException {
            fallbackSocket.connect();
        }

        @Override
        public void close() throws IOException {
            fallbackSocket.close();
        }
    }

    public static class FallbackException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public FallbackException(Exception e) {
            super(e);
        }
    }
}
