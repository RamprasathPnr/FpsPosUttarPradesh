package com.omneagate.weighingMachine;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.omneagate.Util.CustomProgressDialog;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Aalok on 3/10/2017.
 */
public class BLEController implements BluetoothAdapter.LeScanCallback {
    private static final String DEVICE_NAME = "Pheonix Scale";
    private static final UUID DATA_SERVICE = UUID.fromString("0000FFF0-0000-1000-8000-00805F9B34FB");
    private static final UUID NOTIFICATION_CHARACTERSTIC = UUID.fromString("0000FFF4-0000-1000-8000-00805F9B34FB");
    private static final UUID WRITE_CHARACTERSTIC = UUID.fromString("0000FFF3-0000-1000-8000-00805F9B34FB");

    private static final UUID CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothGatt ConnectedGattM;

    private HashMap<String, BluetoothDevice> DevicesM;

    BLEControllerCallback BleControllerCallbackM;
    Context ParentContextM;
    public boolean IsConnectedM = false;
    String ConnectedDeviceNameM = "";
    CustomProgressDialog progressBar;

    public String getConnectedDeviceName()
    {
        return ConnectedDeviceNameM;
    }


    public void SetCallBack(BLEControllerCallback BleControllerCallbackP)
    {
        BleControllerCallbackM = BleControllerCallbackP;
    }

    public void SetContext(Context ContextP)
    {
        ParentContextM = ContextP;
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
        }
    };

    private Runnable mStopRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            StopScan();
        }
    };

    public void Initialize()
    {
         BluetoothManager manager = (BluetoothManager) ParentContextM.getSystemService(ParentContextM.BLUETOOTH_SERVICE);
         mBluetoothAdapter = manager.getAdapter();

     //   mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


    }

    public void StartScan(boolean ShowUiP, int ScanTimeInSecondsP)
    {
        try {
            DevicesM = new HashMap<String, BluetoothDevice>();
            DevicesM.clear();

            if (ShowUiP) {
                BleControllerCallbackM.ShowProgressMessage("Scanning for Devices...");
            }
            if(mBluetoothAdapter !=null)
            mBluetoothAdapter.startLeScan(this);

            mHandler.postDelayed(mStopRunnable, ScanTimeInSecondsP * 2000);//5000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void StopScan()
    {
        try
        {
            if(mBluetoothAdapter !=null)
            mBluetoothAdapter.stopLeScan(this);
            BleControllerCallbackM.OnBleScanComplete(DevicesM);
        }
        catch (Exception ex)
        {

        }
    }

    public void ConnectToDevice(String DeviceNameP, boolean ShowUiP)
    {
        if (IsConnectedM)
        {
            return;
        }
        BluetoothDevice BluetoothDeviceL = DevicesM.get(DeviceNameP);

        if (BluetoothDeviceL == null)
        {
            return;
        }

        if (ShowUiP)
        {
            BleControllerCallbackM.ShowProgressMessage("Trying to Connect " + BluetoothDeviceL.getName());
        }
        ConnectedGattM = BluetoothDeviceL.connectGatt(ParentContextM, false, GattCallbackM);
        IsConnectedM = true;
        ConnectedDeviceNameM = DeviceNameP;
    }

    public void ConnectToBluetoothDevice(BluetoothDevice DeviceNameP, boolean ShowUiP)
    {
        if (IsConnectedM)
        {
            return;
        }
        BluetoothDevice BluetoothDeviceL =DeviceNameP;

        if (BluetoothDeviceL == null)
        {
            return;
        }

        if (ShowUiP)
        {
            BleControllerCallbackM.ShowProgressMessage("Trying to Connect " + BluetoothDeviceL.getName());
        }


        ConnectedGattM = BluetoothDeviceL.connectGatt(ParentContextM, false, GattCallbackM);
        IsConnectedM = true;
        Log.e("","Weighing machine status : "+IsConnectedM);


       // ConnectedDeviceNameM = DeviceNameP;
    }

    public void Disconnect()
    {
        if (ConnectedGattM != null)
        {
            ConnectedGattM.disconnect();
            ConnectedGattM = null;
        }
        IsConnectedM = false;
    }

    @Override
    public void onLeScan(BluetoothDevice DeviceP, int rssi, byte[] scanRecord)
    {
        String DeviceNameL = DeviceP.getName();
        if (DeviceNameL == null)
        {
            return;
        }
        if (DeviceNameL.toUpperCase().startsWith("PHOENIX"))
        {
            if (!DevicesM.containsKey(DeviceP.getName()))
            {
                DevicesM.put(DeviceP.getName(), DeviceP);


            }

        }
    }

    public void ProcessCharacterSticData(byte[] DataP)
    {
        BleControllerCallbackM.NotificationReceived(DataP);
    }

    public void SendData(String DataP)
    {
        if (!IsConnectedM)
        {
            return;
        }
        if (ConnectedGattM == null)
        {
            return;
        }
        BluetoothGattCharacteristic WriteCharactersticL;
        WriteCharactersticL = ConnectedGattM.getService(DATA_SERVICE).getCharacteristic(WRITE_CHARACTERSTIC);

        if (WriteCharactersticL == null)
        {
            return;
        }
        String DataL = " " + DataP;
        byte[] DataBytesL = DataL.getBytes();
        DataBytesL[0] = (byte) DataP.length();
        WriteCharactersticL.setValue(DataBytesL);
        ConnectedGattM.writeCharacteristic(WriteCharactersticL);
    }

    private BluetoothGattCallback GattCallbackM = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt BlueToothGattP, int StatusP, int newState)
        {
            if (StatusP == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED)
            {
                BlueToothGattP.discoverServices();
            }
            else if (StatusP == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                ConnectedGattM = null;
                IsConnectedM = false;

                BlueToothGattP.close();
                BleControllerCallbackM.DeviceIsDisconnected();
            }
            else if (StatusP != BluetoothGatt.GATT_SUCCESS)
            {
                ConnectedGattM = null;
                IsConnectedM = false;
                BlueToothGattP.disconnect();
                BlueToothGattP.close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt BluetoothGattP, int status)
        {
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                EnableNotifications(BluetoothGattP);
            }
            else
            {
                //BleControllerCallbackM.ShowProgressMessage("Errors Occurred while discovering services.");
            }
        }

        private void EnableNotifications(BluetoothGatt BluetoothGattP)
        {
            BluetoothGattCharacteristic CharacteristicL;
            CharacteristicL = BluetoothGattP.getService(DATA_SERVICE).getCharacteristic(NOTIFICATION_CHARACTERSTIC);

            if (CharacteristicL != null)
            {
                //BleControllerCallbackM.ShowProgressMessage("Retrieved Notification Service.");
            }

            //Enable local notifications
            BluetoothGattP.setCharacteristicNotification(CharacteristicL, true);

            //Enabled remote notifications
            BluetoothGattDescriptor desc = CharacteristicL.getDescriptor(CONFIG_DESCRIPTOR);
            if (desc != null)
            {
                //BleControllerCallbackM.ShowProgressMessage("Retrieved Config Descriptor.");
            }
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            BluetoothGattP.writeDescriptor(desc);

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt BluetoothGattP, BluetoothGattCharacteristic CharacteristicP)
        {
            if (CharacteristicP.getValue() == null)
            {
                BleControllerCallbackM.ErrorsOccured("Error obtaining characteristic value");
                return;
            }
            byte[] DataL = CharacteristicP.getValue();
            ProcessCharacterSticData(DataL);
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
        {
        }

        private String connectionState(int status)
        {
            switch (status)
            {
                case BluetoothProfile.STATE_CONNECTED:
                    return "Connected";
                case BluetoothProfile.STATE_DISCONNECTED:
                    return "Disconnected";
                case BluetoothProfile.STATE_CONNECTING:
                    return "Connecting";
                case BluetoothProfile.STATE_DISCONNECTING:
                    return "Disconnecting";
                default:
                    return String.valueOf(status);
            }
        }
    };
}
