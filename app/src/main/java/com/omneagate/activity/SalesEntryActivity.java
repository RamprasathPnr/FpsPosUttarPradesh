package com.omneagate.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EntitlementDTO;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.EntitlementCompletedAlertDialog;
import com.omneagate.activity.dialog.OutOfStockAlertDialog;
import com.omneagate.activity.dialog.SaleDialog;
import com.omneagate.activity.dialog.UserDetailsDialog;
import com.omneagate.printer.BluetoothConnector_new;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FPS User view the entitled and he can enter the availed
 */
public class SalesEntryActivity extends BaseActivity {
    GridLayout productGrid;
    ArrayList<String> outOfStock;
    ArrayList<String> entitlementCompleted;
    private QRTransactionResponseDto entitlementResponseDTO;
    /*List of item entitled and price is in this variable*/
    private List<EntitlementDTO> entitleList;
    SaleDialog saleDialog;
    boolean stockValidation = false;

    // Added for Quanity Weight

    private static boolean receiver = false;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    String device_address = "";
    Set<BluetoothDevice> pairedDevices;
    private ArrayList<BluetoothDevice> devicelist;
    private ConnectRunnable connector;
    BluetoothSocket mmSocket;
    private BluetoothConnector_new blue;
    private LayoutInflater inflater;
    static boolean exception = false;
    private boolean isSocketConnected =false;
    OutputStream mmOutputStream;
    private android.support.v7.app.AlertDialog.Builder builder;
    private android.support.v7.app.AlertDialog alertDialog;
    InputStream mInputStream;
    Button ConnectDevice, getWeight;
    static int exception_count = 0;
    private String TAG = SalesEntryActivity.class.getCanonicalName();




    private boolean qtySet=false;

    private String line=null;

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
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_entitlement);
        networkConnection = new NetworkConnection(this);
        entitlementResponseDTO = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto();
        String stock_validation = "" + FPSDBHelper.getInstance(SalesEntryActivity.this).getMasterData("stock_validation");
        if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
            if (stock_validation.equalsIgnoreCase("1")) {
                stockValidation = true;
            }
        }
        setUpInitialPage();

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

        opendialog();
    }

    /**
     * Initial Setup
     */
    private void setUpInitialPage() {
        setUpPopUpPage();
        Util.LoggingQueue(this, "Sales Entry", "Inside Sales entry page");
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sale_entry_activity);
        Util.setTamilText((TextView) findViewById(R.id.full_entitlements), R.string.full_entitlements);
        Util.setTamilText((TextView) findViewById(R.id.commodity), R.string.commodity);
        Util.setTamilText((TextView) findViewById(R.id.entitled_qty), R.string.entitled_qty);
        Util.setTamilText((TextView) findViewById(R.id.purchased_qty), R.string.purchased_qty);
        Util.setTamilText((TextView) findViewById(R.id.availed_qty), R.string.availed_qty);
        Util.setTamilText((TextView) findViewById(R.id.qty_to_bill), R.string.qty_to_bill);
        Util.setTamilText((TextView) findViewById(R.id.submitEntitlement), R.string.submit);
        Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.card_user_details), R.string.card_user_details);
        Util.setTamilText((TextView) findViewById(R.id.unit), R.string.unit);
        int rows;
        try {
            entitleList = entitlementResponseDTO.getEntitlementList();
            Log.e("Sales entry activity", "entitleList..." + entitleList.toString());
            rows = entitleList.size();
        } catch (Exception e) {
            rows = 0;
        }
        productGrid = (GridLayout) findViewById(R.id.gridLayout);
        productGrid.removeAllViews();
        productGrid.setRowCount(rows);
        productGrid.setColumnCount(6);
        productGrid.setUseDefaultMargins(false);
        productGrid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        productGrid.setRowOrderPreserved(false);
        setLayoutForProducts(rows);
        findViewById(R.id.submitEntitlement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSummaryPage();
            }
        });
        findViewById(R.id.full_entitlement).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserDetailsDialog(com.omneagate.activity.SalesEntryActivity.this, entitlementResponseDTO.getUfc()).show();
            }
        });
        findViewById(R.id.info_sale_entry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFullEntitlement();
            }
        });
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //GridLayout for product with group
    private void setLayoutForProducts(int rowCount) {
        for (int position = 0, column = 0, rows = 0; position < rowCount * 6; position++, column++) {
            if (column == 6) {
                column = 0;
                rows++;
            }
            if (column == 2 || column == 3 || column == 4) {
                if (findPreviousEquals(rows)) {
                    Log.e("Equal", "Equal Row");
                } else {
                    int value = valueForRows(rows);
                    productGrid.addView(getViewForColumns(rows, column, value));
                }
            } else if (column == 0) {
                productGrid.addView(getProductView(rows, column));
            } else if (column == 5) {
                productGrid.addView(getProductAmountView(rows, column));
            } else if (column == 1) {
                productGrid.addView(getUnitView(rows, column));
            }
        }
    }

    private class ConnectRunnable extends Thread {

        public void run() {
            try {
                if (mmSocket == null || !mmSocket.isConnected()) {
                    blue = new BluetoothConnector_new(SalesEntryActivity.this, mmDevice, false, mBluetoothAdapter, null);
                    mmSocket = blue.connect();
                }
                if (mmSocket != null) {
                    Log.e(TAG,"<=== Socket conneccted Sucessfully ===>");
                    isSocketConnected=true;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(isSocketConnected){
                                if(progressBar!=null){
                                    progressBar.dismiss();
                                }
                            }

                        }
                    });

                    exception = false;
                    mInputStream = mmSocket.getInputStream();
                    mmOutputStream = mmSocket.getOutputStream();

                /*byte[] buffer = new byte[1024];
                    int bytes;
                    while (true) {
                        try {
                            bytes = mInputStream.read(buffer);            //read bytes from input buffer
                            String readMessage = new String(buffer, 0, bytes);
                            Log.e(TAG, "readMessage : " + readMessage);
                            // Send the obtained bytes to the UI Activity via handler
                            bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                        } catch (IOException e) {
                            break;
                        }
                    }*/

                    TextView quantity =null;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));
                           while ((line = reader.readLine()) != null) {
                               //Log.i(TAG, "readMessage-line : " + line);
                               try{
                                  // Thread.sleep(3000);
                               }catch(Exception e){

                               }
                               line=line.trim();
                               line=line.replace("Kg","");
                               line=line.replace("+","");

                              // Log.i(TAG, "readMessage-line 2 : " + line);

                               runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                       TextView quantity =null;
                                       if(saleDialog!=null) {
                                           quantity = (TextView) saleDialog.findViewById(R.id.sale_dialog_text);
                                           if (quantity != null) {
                                          //     Log.i(TAG, "quantit is not null " + line);
                                               quantity.setText(line);
                                           } else {

                                           //    Log.i(TAG, "quantit is null");
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
                            Toast.makeText(SalesEntryActivity.this, getResources().getString(R.string.no_printer), Toast.LENGTH_SHORT).show();

                        }
                    });
                    Log.e("Tag", "bluetooth not connected");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Tag", "bluetooth not connected");
            }
            finally {
                try{
                    if(mmSocket!=null){
                        //mmSocket.close();
                    }
                }
                catch (Exception e){
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

    private View getUnitView(int rows, int column) {
        LayoutInflater lin = LayoutInflater.from(this);
        View convertView = lin.inflate(R.layout.layout_back_product_unit, null);
        TextView productUnit = (TextView) convertView.findViewById(R.id.productName);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = 60;
        param.width = 80;
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(rows);
        convertView.setLayoutParams(param);
        if (GlobalAppState.language.equals("hi") && StringUtils.isNotEmpty(entitleList.get(rows).getLproductUnit())) {
            Util.setTamilText(productUnit, entitleList.get(rows).getLproductUnit());
        } else
            productUnit.setText(entitleList.get(rows).getProductUnit());
        return convertView;
    }

    private View getProductView(int rows, int column) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = 60;
        param.width = 280;
        entitleList = entitlementResponseDTO.getEntitlementList();
        LayoutInflater lin = LayoutInflater.from(this);
        View convertView = lin.inflate(R.layout.layout_back_product_name, null);
        TextView productName = (TextView) convertView.findViewById(R.id.productName);
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(rows);
        convertView.setLayoutParams(param);
        if (GlobalAppState.language.equals("hi") && StringUtils.isNotEmpty(entitleList.get(rows).getLproductName())) {
//            Util.setTamilText(productName, entitleList.get(rows).getLproductName());
            productName.setText(entitleList.get(rows).getLproductName());
            productName.setTextSize(17);
        } else
            productName.setText(entitleList.get(rows).getProductName());
        return convertView;
    }

    private View getProductAmountView(int rows, int column) {
        LayoutInflater lin = LayoutInflater.from(this);
        View convertView = lin.inflate(R.layout.layout_back_product_amount, null);
        TextView productUnit = (TextView) convertView.findViewById(R.id.productName);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = 60;
        param.width = 137;
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(rows);
        convertView.setLayoutParams(param);
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        String qty = Util.quantityRoundOffFormat(entitleList.get(rows).getBought());
        productUnit.setText(qty);
        productUnit.setId(rows);
        productUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stockValidation) {
                    if (EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(v.getId()).getCurrentQuantity() > 0.0) {
                        try {

                            if (!saleDialog.isShowing()) {
                                saleDialog = new SaleDialog(com.omneagate.activity.SalesEntryActivity.this, v.getId());
                                saleDialog.show();
                            }
                        } catch (Exception e) {
                            saleDialog = new SaleDialog(com.omneagate.activity.SalesEntryActivity.this, v.getId());
                            saleDialog.show();
                        }
                    } else {
                        Util.messageBar(com.omneagate.activity.SalesEntryActivity.this, getString(R.string.entitlemnt_finished));
                    }
                } else {
                    try {
                        if (!saleDialog.isShowing()) {
                            saleDialog = new SaleDialog(com.omneagate.activity.SalesEntryActivity.this, v.getId());
                            saleDialog.show();
                        }
                    } catch (Exception e) {
                        saleDialog = new SaleDialog(com.omneagate.activity.SalesEntryActivity.this, v.getId());
                        saleDialog.show();
                    }
                }
            }
        });
        return convertView;
    }

    public void opendialog() {
        try {

            GlobalAppState globalAppState = (GlobalAppState) getApplicationContext();
            if (globalAppState != null) {
                device_address = globalAppState.getWeighGaugeBTDeviceAddress();
            }

            if (device_address != null && !device_address.isEmpty()) {
                if (mmDevice == null || !mmDevice.getAddress().equals(device_address)) {
                    mmDevice = mBluetoothAdapter.getRemoteDevice(device_address);
                }
                connect();
            } else {
                getdevicelist();
                if (devicelist != null && devicelist.size() > 0) {

                    dialog(devicelist, R.layout.bluetooth_device_alert, getResources().getString(R.string.title_bluetooth_devices));

                } else {
                    Toast.makeText(SalesEntryActivity.this, getString(R.string.pair_device), Toast.LENGTH_SHORT).show();
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
        if (connector != null) {
            connector = null;
        }
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();

        progressBar = new CustomProgressDialog(com.omneagate.activity.SalesEntryActivity.this);
        progressBar.show();

        connector = new ConnectRunnable();
        connector.start();
    }

    private View dialog(final ArrayList<BluetoothDevice> devicelist, int cus_layout, String title) {
        inflater = (LayoutInflater) SalesEntryActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        builder = new android.support.v7.app.AlertDialog.Builder(SalesEntryActivity.this);
        View layout = inflater.inflate(cus_layout,
                null);
        LinearLayout llay = (LinearLayout) layout.findViewById(R.id.container);
        ScrollView sv = (ScrollView) layout.findViewById(R.id.scrollView);
        for (int i = 0; i < devicelist.size(); i++) {
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutparams.setMargins(10, 10, 10, 10);
            TextView txt = new TextView(SalesEntryActivity.this);
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
                    GlobalAppState globalAppState = (GlobalAppState) getApplicationContext();
                    if (globalAppState != null) {
                        globalAppState.setWeighGaugeBTDeviceAddress(mmDevice.getAddress());
                    }
                    //globalAppState.setWeighGaugeName(mmDevice.getName());
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

    private View getViewForColumns(int rows, int column, int value) {
        LayoutInflater lin = LayoutInflater.from(this);
        View convertView = lin.inflate(R.layout.layout_back_product_unit, null);
        TextView productUnit = (TextView) convertView.findViewById(R.id.productName);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = 60 * value;//+ ((value - 1) * 2)
        param.width = 137;
        param.columnSpec = GridLayout.spec(column);
        param.rowSpec = GridLayout.spec(rows, value);
        convertView.setLayoutParams(param);
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        if (column == 2) {
            String qty1 = Util.quantityRoundOffFormat(entitleList.get(rows).getEntitledQuantity());
            productUnit.setText(qty1);
        } else if (column == 3) {
            String qty2 = Util.quantityRoundOffFormat(entitleList.get(rows).getPurchasedQuantity());
            productUnit.setText(qty2);
        } else {
            String qty3 = Util.quantityRoundOffFormat(entitleList.get(rows).getCurrentQuantity());
            productUnit.setText(qty3);
        }
        return convertView;
    }

    //Is previous is
    private boolean findPreviousEquals(int position) {
        if (position > 0) {
            long productId = entitleList.get(position).getGroupId();
            if (productId == entitleList.get(position - 1).getGroupId()) {
                return true;
            }
        }
        return false;
    }

    //Span rows in gridlayout
    private int valueForRows(int position) {
        Long productId = entitleList.get(position).getGroupId();
        Log.e("sales entry activity", "productId..." + productId);
        List<EntitlementDTO> entitleData = entitlementResponseDTO.getUserEntitlement().get(productId);
        return entitleData.size();
    }

    //Used to set full entitlemnt for the product
    private void setFullEntitlement() {
        Util.LoggingQueue(this, "Sales Entry", "Full entitlement clicked");
        outOfStock = new ArrayList<String>();
        entitlementCompleted = new ArrayList<String>();
        for (int i = 0; i < entitleList.size(); i++) {
            EntitlementDTO currentEntitle = entitleList.get(i);
            // Collecting out of stock products
            if (currentEntitle.getEntitledQuantity() > 0.0) {
                Boolean fullEntitlement = isFPSStockOut(currentEntitle.getCurrentQuantity(), currentEntitle.getProductId());
                if (!fullEntitlement) {
                    if (GlobalAppState.language.equalsIgnoreCase("hi")) {
                        outOfStock.add(currentEntitle.getLproductName());
                    } else if (GlobalAppState.language.equalsIgnoreCase("en")) {
                        outOfStock.add(currentEntitle.getProductName());
                    }
                }
            }
            // Collecting fully entitled products
            if (GlobalAppState.language.equalsIgnoreCase("hi")) {
                if (currentEntitle.getEntitledQuantity() > 0.0) {
                    double remainingQuantity = currentEntitle.getEntitledQuantity() - currentEntitle.getPurchasedQuantity();
                    if (!(remainingQuantity > 0.0)) {
                        entitlementCompleted.add(currentEntitle.getLproductName());
                    }
                }
            } else if (GlobalAppState.language.equalsIgnoreCase("en")) {
                if (currentEntitle.getEntitledQuantity() > 0.0) {
                    double remainingQuantity = currentEntitle.getEntitledQuantity() - currentEntitle.getPurchasedQuantity();
                    if (!(remainingQuantity > 0.0)) {
                        entitlementCompleted.add(currentEntitle.getProductName());
                    }
                }
            }
            if (stockValidation) {
                if (!isGroupSelected(i) && isFPSStockAvailable(currentEntitle.getCurrentQuantity(), currentEntitle.getProductId())) {
                    String qty = Util.quantityRoundOffFormat(entitleList.get(i).getCurrentQuantity());
                    ((TextView) findViewById(i)).setText(qty);
                    double total = currentEntitle.getCurrentQuantity() * currentEntitle.getProductPrice();
                    EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(i).setBought(currentEntitle.getCurrentQuantity());
                    EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(i).setTotalPrice(total);
                }
            } else {
                String qty = Util.quantityRoundOffFormat(entitleList.get(i).getCurrentQuantity());
                ((TextView) findViewById(i)).setText(qty);
                double total = currentEntitle.getCurrentQuantity() * currentEntitle.getProductPrice();
                EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(i).setBought(currentEntitle.getCurrentQuantity());
                EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(i).setTotalPrice(total);
            }
        }

        if (stockValidation) {
            if (outOfStock.size() > 0) {
//            ShowAlertDialogWithListview();
                new OutOfStockAlertDialog(this, outOfStock).show();
            } else if (entitlementCompleted.size() > 0) {
//            ShowAlertDialogWithListview();
                new EntitlementCompletedAlertDialog(this, entitlementCompleted).show();
            }
        }
    }

    public void ShowAlertDialogWithListview() {
        final CharSequence[] products = outOfStock.toArray(new String[outOfStock.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Out Of Stock");
        dialogBuilder.setItems(products, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
            }
        });
        final AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
        /*dialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                alertDialogObject.dismiss();
            }
        });*/
    }

    //Any product in this group already selected
    private boolean isGroupSelected(int position) {
        for (int i = 0; i < position; i++) {
            String textValue = ((TextView) findViewById(i)).getText().toString().trim();
            if (entitleList.get(i).getGroupId() == entitleList.get(position).getGroupId() && Double.parseDouble(textValue) > 0.0) {
                return true;
            }
        }
        return false;
    }

    /**
     * FPS stock available or not
     * if quantity availed is greater than stock returns false else true
     *
     * @param bought quantity and productId
     */
    private boolean isFPSStockAvailable(double bought, long productId) {
        try {
            FPSStockDto fpsStockDto = FPSDBHelper.getInstance(this).getAllProductStockDetails(productId);
            double quantity = 0.0;
            if (fpsStockDto != null) {
                quantity = fpsStockDto.getQuantity();
            }
            return quantity >= bought;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isFPSStockOut(double bought, long productId) {
        try {
            FPSStockDto fpsStockDto = FPSDBHelper.getInstance(this).getAllProductStockDetails(productId);
            double quantity = 0.0;
            if (fpsStockDto != null) {
                quantity = fpsStockDto.getQuantity();
            }
            return quantity >= bought;
        } catch (Exception e) {
            return false;
        }
    }

    public void setEntitlementText(int id, double entitlement) {
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
//        entitlement = Util.quantityRoundOffFormat(entitlement);
        ((TextView) findViewById(id)).setText(Util.quantityRoundOffFormat(entitlement));
    }

    /**
     * check validation for summary page navigation
     * check entitlement of products available for user
     * stock available in the store
     * if any fails showing error message
     */
    private void showSummaryPage() {
        boolean valueEntered = false;
        entitleList = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList();
        for (EntitlementDTO entitlementResult : entitleList) {
            if (entitlementResult.getBought() > 0) {
                valueEntered = true;
                break;
            }
        }
        Map<Long, List<EntitlementDTO>> userEntitle = new HashMap<>();
        for (EntitlementDTO entitle : entitleList) {
            if (userEntitle.containsKey(entitle.getGroupId())) {
                userEntitle.get(entitle.getGroupId()).add(entitle);
            } else {
                List<EntitlementDTO> entitles = new ArrayList<>();
                entitles.add(entitle);
                userEntitle.put(entitle.getGroupId(), entitles);
            }
        }

        if (stockValidation) {
            if (!getValueForList(userEntitle)) {
                Util.messageBar(this, getString(R.string.exceedsLimit));
                return;
            }
        }

        if (valueEntered) {
            Util.LoggingQueue(this, "Sales Entry", entitleList.toString());
            if (TransactionBase.getInstance().getTransactionBase().getTransactionType() == TransactionTypes.SALE_QR_OTP_DISABLED) {
                startActivity(new Intent(this, SalesSummaryWithOutOTPActivity.class));
                finish();
            } else {
                overridePendingTransition(0, 0);
                startActivity(new Intent(this, SalesSummaryActivity.class));
                finish();
            }
        } else {
            Util.LoggingQueue(this, "Sales Entry", "No Items selected");
            Util.messageBar(this, getString(R.string.noItemSelected));
        }
    }

    private boolean getValueForList(Map<Long, List<EntitlementDTO>> userEntitle) {
        boolean userValue = true;
        for (Long keys : userEntitle.keySet()) {
            List<EntitlementDTO> entitles = userEntitle.get(keys);
            double bought = getListBySize(entitles);
            if (bought > entitles.get(0).getCurrentQuantity()) {
                userValue = false;
            }
        }
        return userValue;
    }

    private double getListBySize(List<EntitlementDTO> entitles) {
        double totalSize = 0.0;
        for (EntitlementDTO entitlementDTO : entitles) {
            totalSize = totalSize + entitlementDTO.getBought();
        }
        return totalSize;
    }

    /**
     * server Response is set
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Util.LoggingQueue(this, "Sales Entry", "Back pressed called");
        startActivity(new Intent(this, SaleOrderActivity.class));
        finish();
    }


    //Concrete method
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
