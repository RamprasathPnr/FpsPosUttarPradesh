package com.omneagate.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.bean.ComBean;
import com.comport.SerialHelper;
import com.omneagate.DTO.Entitlement;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.Product;
import com.omneagate.DTO.RCAuthResponse;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.MySharedPreference;
import com.omneagate.Util.ProductMap;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.ManualEntryDialog;
import com.omneagate.activity.dialog.QuantityEnterDailog;
import com.omneagate.activity.dialog.QuantityManualEntered;
import com.omneagate.printer.BluetoothConnector_new;
import com.omneagate.weighingMachine.BLEController;
import com.omneagate.weighingMachine.BLEControllerCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.SerialPortFinder;

public class TgProductListActivity extends BaseActivity implements BLEControllerCallback {
    private LinearLayout continue_layout;
    private Button btContinue,btnBack;

    private RCAuthResponse authResponse;
    private ProductListAdapter productListAdapter;
    private List<Product> productList;
    RecyclerView recyclerViewProducts;
    private ImageView imageViewBack;
    private Double conQuantity;


    private final String TAG = TgProductListActivity.class.getCanonicalName();

    private static boolean receiver = false;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    String device_address = "";
    String device_name="";
    Set<BluetoothDevice> pairedDevices;
    private ArrayList<BluetoothDevice> devicelist;
    private ConnectRunnable connector;
    BluetoothSocket mmSocket;
    private BluetoothConnector_new blue;
    private LayoutInflater inflater;
    static boolean exception = false;
    private boolean isSocketConnected = false;
    OutputStream mmOutputStream;
    private android.support.v7.app.AlertDialog.Builder builder;
    private android.support.v7.app.AlertDialog alertDialog;
    InputStream mInputStream;
    Button ConnectDevice, getWeight;
    static int exception_count = 0;
    private String line = null;

    QuantityEnterDailog quantityEnterDailog;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;

    // QuantityManualEntered quantityManualEntered; with keypad

    ManualEntryDialog manualEntryDialog;

    private static final String AFSC_CARD_TYPE = "4";
    private static final String FSC_CARD_TYPE = "5";
    private static final String AAP_CARD_TYPE = "9";

    private static final String RICEAAP_CODE = "106";
    private static final String RICEAFSC_CODE = "107";
    private static final String RICEFSC_CODE = "108";

    private static final int MSG_NOTIFICATION = 101;
    private static final int MSG_PROGRESS = 201;

    List<Product> closingbalanceList;

    public Map<String, Product> productPrice = new HashMap<String, Product>();

    BLEController bleControllerM;

    Date LastDateTimeM = new Date();

    boolean ActivityIsActiveM = false;

    String LastWeightM = "";
    String LastMuM = "";
    int connectionType = 0;
    int weightingScale = 0;

    SerialControl ComPort;
    DispQueueThread DispQueue;
    SerialPortFinder mSerialPortFinder;


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
        setContentView(R.layout.activity_tg__sales__activity__date);
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        ((TextView) findViewById(R.id.ration_number)).setText(getString(R.string.rc_number) + " " + LoginData.getInstance().getRationCardNo());
        updateDateTime();
        InitView();

     /*   bleControllerM = new BLEController();
        bleControllerM.SetContext(this);
        bleControllerM.Initialize();
        bleControllerM.SetCallBack(this);
        bleControllerM.StartScan(true, 1);*/

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
        try {
            connectionType = Integer.parseInt(MySharedPreference.readString(getApplicationContext(),
                    "ConnectionType", "0"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            weightingScale = Integer.parseInt(MySharedPreference.readString(getApplicationContext(),
                    "weighingScale", "0"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //  mHandler.postDelayed(CheckDateNotification, 2000);
        if(LoginData.getInstance().getWeighingScale().equalsIgnoreCase("0")) {  // weighingScale value from server
            if (weightingScale == 0 && connectionType == 0) {
                Log.e(TAG, "onCreate Bluetooth");
                bleControllerM = new BLEController();
                bleControllerM.SetContext(this);
                bleControllerM.Initialize();
                bleControllerM.SetCallBack(this);
                bleControllerM.StartScan(true, 1);
                mHandler.postDelayed(CheckDateNotification, 2000);
            } else if (weightingScale == 0 && connectionType == 1 || weightingScale == 1 && connectionType == 1) {
                Log.e(TAG, "onCreate RJ11");
                ComPort = new SerialControl();
                DispQueue = new DispQueueThread();
                DispQueue.start();
                ComPort.setPort("/dev/ttyMT1");
                ComPort.setBaudRate("9600");
            } else if (weightingScale == 1 && connectionType == 0 || weightingScale == 2 && connectionType == 0) {
                opendialog();
            }
        }



    }

    public Double getProductPrice(String productCode){
        Double value=0.0;;
        for(int i=0;i<closingbalanceList.size();i++){
            if(closingbalanceList.get(i).getCode().equals(productCode)){
                value=closingbalanceList.get(i).getUnitRate();
            }

        }
        System.out.println(value);
        return value;


    }

    public Double getClosingBalance(String productCode){
        Double value=0.0;;
        for(int i=0;i<closingbalanceList.size();i++){
            if(closingbalanceList.get(i).getCode().equals(productCode)){
                value=closingbalanceList.get(i).getClosingBalance();
            }

        }
        System.out.println(value);
        return value;


    }

   /* public  Double getClosingBalance(String productCode){
        Double value=0.0;

    }*/

    @Override
    public void onBackPressed() {
        Intent in =new Intent(TgProductListActivity.this,TgSalesActivity.class);
        startActivity(in);
        finish();
    }

    private void InitView() {
        try {
            setPopUpPage();

            progressBar = new CustomProgressDialog(com.omneagate.activity.TgProductListActivity.this);
            ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.sales_top_heading));
            imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
            imageViewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            btnBack = (Button) findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

           // authResponse = (RCAuthResponse) getIntent().getSerializableExtra("RCAuthResponse");
            authResponse=EntitlementResponse.getInstance().getRcAuthResponse();
            recyclerViewProducts = (RecyclerView) findViewById(R.id.lv_products);
            recyclerViewProducts.setLayoutManager(new LinearLayoutManager(TgProductListActivity.this));
            productList = authResponse.getItemsAllotedList();
            closingbalanceList =FPSDBHelper.getInstance(TgProductListActivity.this).getAllClosingBalance();

            for (int i = 0; i < productList.size(); i++) {

                for (int j = 0; j < closingbalanceList.size(); j++) {

                    if (productList.get(i).getCode().equals(closingbalanceList.get(j).getCode())) {
                        productList.get(i).setUnitRate(closingbalanceList.get(j).getUnitRate());
                        productList.get(i).setClosingBalance(closingbalanceList.get(j).getClosingBalance());
                    }
                }

            }

            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getCode().equals(ProductMap.RICE_CODE)) {
                    String cardType = EntitlementResponse.getInstance().getRcAuthResponse().getCommBDetails().getTypeId();
                   Log.e(TAG,"TgProductListActivity : "+cardType);
                    if (AFSC_CARD_TYPE.equals(cardType)) {
                        productList.get(i).setUnitRate(getProductPrice(RICEAFSC_CODE));
                        productList.get(i).setClosingBalance(getClosingBalance(RICEAFSC_CODE));
                        Log.e(TAG,"RICEAFSC_CODE : "+productList.get(i).getUnitRate());
                    } else if (FSC_CARD_TYPE.equals(cardType)) {
                        productList.get(i).setUnitRate(getProductPrice(RICEFSC_CODE));
                        productList.get(i).setClosingBalance(getClosingBalance(RICEFSC_CODE));
                     //   Log.e(TAG,"RICEFSC_CODE : "+getProductPrice(RICEFSC_CODE));
                    } else if (AAP_CARD_TYPE.equals(cardType)) {
                        productList.get(i).setUnitRate(getProductPrice(RICEAAP_CODE));
                        productList.get(i).setClosingBalance(getClosingBalance(RICEAAP_CODE));
                       // Log.e(TAG,"RICEAAP_CODE : "+getProductPrice(RICEAAP_CODE));
                    }

                }

            }

            Log.e(TAG, "Total Product size : " + productList.toString());
            productListAdapter = new ProductListAdapter(TgProductListActivity.this, productList);
            recyclerViewProducts.setAdapter(productListAdapter);

            btContinue = (Button) findViewById(R.id.btContinue);
            btContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean valueEntered = false;
                    if (productList != null) {
                        for (Product product : productList) {
                            if ((product.getQuantityEntered()!=null) && (product.getQuantityEntered() > 0.0)) {
                                valueEntered = true;
                            }
                        }
                    }


                    if (valueEntered) {
                        EntitlementResponse.getInstance().getRcAuthResponse().setItemsAllotedList(productList);
                        Intent i = new Intent(TgProductListActivity.this, TgSalesConfirmationActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(TgProductListActivity.this, R.string.Please_Select_the_Commodity, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Log.e("","mHandler - handleMessage >>>>> "+msg);
            BluetoothGattCharacteristic characteristic;
            switch (msg.what)
            {
                case MSG_NOTIFICATION:
                    byte[] NotificationDataL = (byte[]) msg.obj;
                    ProcessNotification(NotificationDataL);
                    break;
                case MSG_PROGRESS:
                    String TraceMsgL = (String) msg.obj;
                    break;

            }
        }
    };

    private Runnable CheckDateNotification = new Runnable()
    {
        @Override
        public void run()
        {

            float TimeOffSetAllowedL = 2.0f;
            Date CurrentDateL = new Date();
            long MilliSecondsL = CurrentDateL.getTime() - LastDateTimeM.getTime();
            float SecondsL = (float)MilliSecondsL/1000f;
            if (SecondsL > TimeOffSetAllowedL)
            {
                LastWeightM = "";
                UpdateWeight("-----");
                //MuDisplayM.setText("--");
                BLEController TheBleControllerL = new BLEController();
                TheBleControllerL.Disconnect();
                mHandler.postDelayed(ReconnectAttempt, 3000);
            }
            else
            {
                mHandler.postDelayed(CheckDateNotification, 1000);
            }
        }
    };
    private Runnable ReconnectAttempt = new Runnable()
    {
        @Override
        public void run()
        {
            if (!ActivityIsActiveM)
            {
                return;
            }
            BLEController TheBleControllerL = new BLEController();
            if (TheBleControllerL.IsConnectedM)
            {
                return;
            }
            TheBleControllerL.StartScan(false, 2);
            mHandler.postDelayed(ReconnectAttempt, 2000);
        }
    };

    void ProcessNotification(byte[] DataP) {
        int DataLengthL = DataP.length;
        if (DataLengthL < 2) {
            //packet needs to at least contain start byte and end byte
            return;
        }


        int PacketLengthL = DataP.length;
        int StartPacketIndexL = 0;
        while (DataP[StartPacketIndexL] != 0x21) {
            StartPacketIndexL++;
            if (StartPacketIndexL >= PacketLengthL) {
                return;//"!" not found in packet
            }
        }

        int EndPacketIndexL = StartPacketIndexL;
        while (DataP[EndPacketIndexL] != 0x23) {
            EndPacketIndexL++;
            if (EndPacketIndexL >= PacketLengthL) {
                return;//"#" not found in packet
            }
        }

        //strip the start and end bytes
        byte[] CleanPacketL = new byte[(EndPacketIndexL - StartPacketIndexL) - 1];
        System.arraycopy(DataP, StartPacketIndexL + 1, CleanPacketL, 0, CleanPacketL.length);
        DataP = CleanPacketL;
        DataLengthL = DataP.length;

        boolean LEDDataPresentL = false;
        byte LastByteL = DataP[DataP.length - 1];
        String MeasuringUnitL = "";
        if (LastByteL <= 0x0F) {
            DataLengthL -= 1;
            LEDDataPresentL = true;

            if (DataLengthL < 3) {
                return;
            }

            if (DataP[DataLengthL - 3] > 0x39) {
                MeasuringUnitL = new String(DataP, DataLengthL - 4, 4);
                DataLengthL -= 4;
            }
        }
        String WeightDataL = new String(DataP, 0, DataLengthL);


        UpdateWeight(WeightDataL);
        //MuDisplayM.setText(MeasuringUnitL.trim());

        LastWeightM = WeightDataL;
        LastMuM = MeasuringUnitL.trim();


    }

    private void UpdateWeight(String WeightStringP)
    {
       // WeightDisplayM.setText(WeightStringP);
        TextView quantity = null;
        if (quantityEnterDailog != null) {
            quantity = (TextView) quantityEnterDailog.findViewById(R.id.edtQuantity);
            if (quantity != null) {
                //   Log.e(TAG, "quantity is not null " + line);
                quantity.setText(WeightStringP);
            } else {

                //      Log.e(TAG, "quantity is null");
            }
        }
    }

    @Override
    public void OnBleScanComplete(HashMap<String, BluetoothDevice> DevicesP) {
        try {
            if(progressBar!=null){
                progressBar.dismiss();
            }
            Set<String> keySet = DevicesP.keySet();
            ArrayList<BluetoothDevice> btList = new ArrayList<BluetoothDevice>();
            for (String key : keySet) {
                btList.add(DevicesP.get(key));
            }
            Log.e("","total list size"+btList.size());
            if (btList.size() > 0) {
                dialog(btList, R.layout.bluetooth_device_alert, getResources().getString(R.string.title_bluetooth_devices));
            } else {
                Toast.makeText(TgProductListActivity.this, "Please Connect Weighing Scale", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void ShowProgressMessage(String MessageP) {
        if (!progressBar.isShowing())
        {
            progressBar.show();
        }
    }

    @Override
    public void DeviceIsDisconnected() {

    }

    @Override
    public void ErrorsOccured(String ErrorsP) {

    }

    @Override
    public void NotificationReceived(byte[] NotificationDataP) {
        if(progressBar!=null){
            progressBar.dismiss();
        }
        mHandler.sendMessage(Message.obtain(null, MSG_NOTIFICATION, NotificationDataP));

    }

    private class ConnectRunnable extends Thread {

        public void run() {
            try {
                if (mmSocket == null || !mmSocket.isConnected()) {
                    blue = new BluetoothConnector_new(TgProductListActivity.this, mmDevice, false, mBluetoothAdapter, null);
                    mmSocket = blue.connect();
                }
                if (mmSocket != null) {
                    Log.e(TAG, "<=== Socket conneccted Sucessfully ===>");
                    isSocketConnected = true;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isSocketConnected) {
                                if (progressBar != null) {
                                    progressBar.dismiss();
                                }
                            }

                        }
                    });

                    exception = false;
                    mInputStream = mmSocket.getInputStream();
                    mmOutputStream = mmSocket.getOutputStream();


                    BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));
                    while ((line = reader.readLine()) != null) {

                        Log.e(TAG, "line value"+line);

                        try {
                            if (line != null && !line.equalsIgnoreCase("") && !line.equalsIgnoreCase("O-LD Er") && !line.contains("-")) {
                                line = line.trim();
                                line = line.replace("=", "");
                                line = line.replace("+", "");
                                conQuantity = Double.parseDouble(line);
                            } else {
                                Log.e(TAG, "line empty");
                              //  conQuantity = 0.0;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                          //  conQuantity =0.0;
                         }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView quantity = null;
                                if (quantityEnterDailog != null) {
                                    quantity = (TextView) quantityEnterDailog.findViewById(R.id.edtQuantity);
                                    if (quantity != null && conQuantity!=null) {
                                        quantity.setText(""+conQuantity);
                                    } else {

                                        //      Log.e(TAG, "quantity is null");
                                    }
                                }

                            }
                        });

                    }

                } else {
                    exception = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TgProductListActivity.this, getResources().getString(R.string.no_printer), Toast.LENGTH_SHORT).show();

                        }
                    });
                    Log.e("Tag", "bluetooth not connected");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Tag", "bluetooth not connected");
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    if (mmSocket != null) {
                        //mmSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public void setPurchasedQuantity(int position, double qtyRequested) {
        try {
            productList.get(position).setQuantityEntered(qtyRequested);

            Double quantity = new Double(qtyRequested);
            int i_quantity = quantity.intValue();

            double totalamount= i_quantity * productList.get(position).getUnitRate();
            productList.get(position).setAmount(totalamount);

            productListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void opendialog() {
        try {

            GlobalAppState globalAppState = (GlobalAppState) getApplicationContext();
            if (globalAppState != null) {
                device_address = globalAppState.getWeighGaugeBTDeviceAddress();
                device_name=globalAppState.getWeighGaugeName();
            }

            if (device_address != null && !device_address.isEmpty() && device_name!=null && !device_name.isEmpty()) {
                if (mmDevice == null || !mmDevice.getAddress().equals(device_address)) {
                    mmDevice = mBluetoothAdapter.getRemoteDevice(device_address);
                }
               // connect();
                if(mmDevice==null) {
                    Toast.makeText(TgProductListActivity.this, "unable to find device", Toast.LENGTH_SHORT).show();
                    return;
                }
                connectPhonix(mmDevice);
            } else {
                getdevicelist();
                if (devicelist != null && devicelist.size() > 0) {

                    dialog(devicelist, R.layout.bluetooth_device_alert, getResources().getString(R.string.title_bluetooth_devices));

                } else {
                    Toast.makeText(TgProductListActivity.this, getString(R.string.pair_device), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    public void connect() {
        connectCallOn();
        //connectPhonix();
    }

    public void connectCallOn() {
        if (connector != null) {
            connector = null;
        }
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();

        progressBar = new CustomProgressDialog(com.omneagate.activity.TgProductListActivity.this);
        progressBar.show();

        connector = new ConnectRunnable();
        connector.start();
    }

    public void connectPhonix(BluetoothDevice deviceName) {

        //BLEController TheBleControllerL = new BLEController();
        bleControllerM.ConnectToBluetoothDevice(deviceName, false);

        progressBar.show();
    }

    private View dialog(final ArrayList<BluetoothDevice> devicelist, int cus_layout, String title) {
        inflater = (LayoutInflater) TgProductListActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        builder = new android.support.v7.app.AlertDialog.Builder(TgProductListActivity.this);
        View layout = inflater.inflate(cus_layout,
                null);
        LinearLayout llay = (LinearLayout) layout.findViewById(R.id.container);
        ScrollView sv = (ScrollView) layout.findViewById(R.id.scrollView);
        for (int i = 0; i < devicelist.size(); i++) {
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutparams.setMargins(10, 10, 10, 10);
            TextView txt = new TextView(TgProductListActivity.this);
            txt.setLayoutParams(layoutparams);
            txt.setText(devicelist.get(i).getName() + "\n" + devicelist.get(i).getAddress());
            txt.setTextSize(20);
            txt.setTag(i);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mmDevice = devicelist.get((int) v.getTag());
                    // sharedPrefs.edit().putString(printerDeviceAddress, mmDevice.getAddress()).commit();
                    // connect();//socket connection
                    //  connectPhonix(mmDevice);
                    if (weightingScale == 0 && connectionType == 0) {
                        connectPhonix(mmDevice);
                    } else if(weightingScale == 1 && connectionType == 0 || weightingScale == 2 && connectionType == 0){
                        connect();
                    }
                   /* GlobalAppState globalAppState = (GlobalAppState) getApplicationContext();
                    if (globalAppState != null) {
                        globalAppState.setWeighGaugeBTDeviceAddress(mmDevice.getAddress());
                        globalAppState.setWeighGaugeName(devicelist.get((int) v.getTag()).getName());
                    }*/

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
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onResume() {
        if(LoginData.getInstance().getWeighingScale().equalsIgnoreCase("0")) {
            if (weightingScale == 0 && connectionType == 0) {
                Log.e(TAG, "onResume Bluetooth");
                ActivityIsActiveM = true;
            } else if (weightingScale == 0 && connectionType == 1 || weightingScale == 1 && connectionType == 1) {
                Log.e(TAG, "onResume RJ11");
                CloseComPort(ComPort);
                OpenComPort(ComPort);
            }
        }
        super.onResume();
        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes
    }

    @Override
    protected void onPause() {
        if(LoginData.getInstance().getWeighingScale().equalsIgnoreCase("0")) {
            if (weightingScale == 0 && connectionType == 0) {
                Log.e(TAG, "onPause Bluetooth");
                if (bleControllerM != null) {
                    bleControllerM.Disconnect();
                }
                ActivityIsActiveM = false;
            } else if (weightingScale == 0 && connectionType == 1 || weightingScale == 1 && connectionType == 1) {
                Log.e(TAG, "onPause RJ11");
            }
        }
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }
    }

    public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductHolder> {
        private Context context;

        private LayoutInflater mLayoutInflater;
        private List<Product> fpsProductList;


        public ProductListAdapter(Context context, List<Product> fpsProductList) {
            this.context = context;
            this.fpsProductList = fpsProductList;
            this.mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mLayoutInflater.inflate(R.layout.list_item_products, parent, false);
            return new ProductHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ProductHolder holder, final int position) {
            holder.tvItemName.setText(fpsProductList.get(position).getDisplayName());
            holder.tvItemAvailQty.setText("" + fpsProductList.get(position).getProductBalanceQty());

            if(fpsProductList.get(position).getQuantityEntered()!=null) {
                holder.tvItemQtyRequested.setText("" + fpsProductList.get(position).getQuantityEntered());
            }else{
                holder.tvItemQtyRequested.setText("");
            }
            holder.tvItemunits.setText(""+fpsProductList.get(position).getUnitName());

            if(fpsProductList.get(position).getUnitRate() !=null){
                holder.tvItemUnitRate.setText(""+fpsProductList.get(position).getUnitRate());
            }else{
                holder.tvItemUnitRate.setText("");
            }

            if(fpsProductList.get(position).getAmount() !=null) {
                holder.tvItemQtyAmount.setText("" + fpsProductList.get(position).getAmount());
            }else{
                holder.tvItemQtyAmount.setText("");
            }

            if(fpsProductList.get(position).getClosingBalance() !=null) {
                holder.tvClosingBalance.setText("" + fpsProductList.get(position).getClosingBalance());
            }else{
                holder.tvClosingBalance.setText("");
            }

            if(fpsProductList.get(position).getClosingBalance() <=0 || fpsProductList.get(position).getProductBalanceQty()<=0){
                holder.llLinearLayout.setVisibility(View.GONE);
            }else{
                holder.llLinearLayout.setVisibility(View.VISIBLE);
            }

 /*quantityManualEntered = new QuantityManualEntered(context, product, position);
                            quantityManualEntered.show();*/

            holder.llQtyRequested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Product product = fpsProductList.get(position);
                    if(fpsProductList.get(position).getProductBalanceQty() > 0) {
                        if (fpsProductList.get(position).isAutoWeight() && LoginData.getInstance().getWeighingScale().equalsIgnoreCase("0")) {
                            quantityEnterDailog = new QuantityEnterDailog(context, product, position);
                            quantityEnterDailog.show();

                        } else {

                            manualEntryDialog =new ManualEntryDialog(context,product,position);
                            manualEntryDialog.show();
                        }
                    }else {
                        Toast.makeText(TgProductListActivity.this, getString(R.string.entitlemnt_finished),Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return fpsProductList.size();
        }

        public class ProductHolder extends RecyclerView.ViewHolder {
            private TextView tvItemName;
            private TextView tvItemAvailQty;
            private TextView tvItemunits;
            private TextView tvItemUnitRate;
            private TextView tvItemQtyRequested;
            private TextView tvItemQtyAmount;
            private TextView tvClosingBalance;
            private LinearLayout llQtyRequested;
            private LinearLayout llLinearLayout;

            public ProductHolder(View v) {
                super(v);
                tvItemName = (TextView) v.findViewById(R.id.tvItemName);
                tvItemAvailQty = (TextView) v.findViewById(R.id.tvItemAvailQty);
                tvItemunits = (TextView) v.findViewById(R.id.tvItemUnit);
                tvItemUnitRate = (TextView) v.findViewById(R.id.tvItemUnitRate);
                tvItemQtyRequested = (TextView) v.findViewById(R.id.tvItemQtyRequested);
                tvItemQtyAmount = (TextView) v.findViewById(R.id.tvItemQtyAmount);
                tvClosingBalance = (TextView) v.findViewById(R.id.tvClosingBalance);
                llQtyRequested =(LinearLayout)v.findViewById(R.id.lin_qtyRequested);
                llLinearLayout=(LinearLayout)v.findViewById(R.id.title_layout);

            }
        }
    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgProductListActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
    private class SerialControl extends SerialHelper {
        @Override
        protected void onDataReceived(final ComBean ComRecData)
        {
            DispQueue.AddQueue(ComRecData);

        }
    }

    private class DispQueueThread extends Thread {
        private Queue<ComBean> QueueList = new LinkedList<ComBean>();

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                final ComBean ComData;


                while ((ComData = QueueList.poll()) != null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            DispRecData(ComData);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                }

            }

        }

        public synchronized void AddQueue(ComBean ComData) {
            QueueList.add(ComData);
        }
    }

    private void DispRecData(ComBean ComRecData) {
        StringBuilder sMsg = new StringBuilder();
        sMsg.append(new String(ComRecData.bRec));

        try {
            TextView quantity = null;
            if (quantityEnterDailog != null) {
                quantity = (TextView) quantityEnterDailog.findViewById(R.id.edtQuantity);
                if (quantity != null) {
                    quantity.setText(sMsg.substring(1, 8));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void CloseComPort(SerialHelper ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }

    private void OpenComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
        } catch (SecurityException e) {
            ShowMessage("SecurityException!");
        } catch (IOException e) {
            ShowMessage("IOException!");
        } catch (InvalidParameterException e) {
            ShowMessage("InvalidParameterException!");
        }
    }

    private void ShowMessage(String sMsg) {
        Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
    }

}
