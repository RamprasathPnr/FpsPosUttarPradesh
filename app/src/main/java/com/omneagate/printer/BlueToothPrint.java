package com.omneagate.printer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.BillSuccessActivity;
import com.omneagate.activity.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


/**
 * Created for BlueToothPrint
 */
public class BlueToothPrint extends Thread {
    Set<BluetoothDevice> pairedDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    BillDto updateStockRequestDto;
    Activity context;
    String rationCardNumber;

    public BlueToothPrint(Activity context, BillDto updateStockRequestDto, String cardNumber) {
        this.updateStockRequestDto = updateStockRequestDto;
        this.context = context;
        this.rationCardNumber = cardNumber;
    }

    private void findBT() {
        try {
            Log.e("findBT", "findBT()");
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Log.e("bluetooth", "" + mBluetoothAdapter.toString());
            if (mBluetoothAdapter == null) {
                Log.e("bluetooth", "No bluetooth adapter available");
            }
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
            context.registerReceiver(mReceiver, filter);
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            Log.e("BluetoothpairedDevices", "" + pairedDevices);
            //int RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
            //String mDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            // Log.e("pairedDevices.getClass", ""+pairedDevices.getClass().toString());
//            BluetoothPrintData();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    mmDevice = device;
                    Log.e("getAddress", "" + device.getAddress());
                    Log.e("name", "" + device.getName());
                    Log.e("desc content", "" + device.describeContents());
                    Log.e("fetchuuid", "" + device.fetchUuidsWithSdp());
                    Log.e("getbondstate", "" + device.getBondState());
                    Log.e("type", "" + device.getType());
                    Log.e("uuids", "" + device.getUuids());
                    break;
                }
                openBT();//socket connection
                BluetoothPrintData();
            } else {
                Log.e("BluetoothpairedDevices", "no paired devices...");
                printDialogStatus(context.getString(R.string.no_printer));
                ((BillSuccessActivity) context).enableButton2();
            }
        } catch (Exception e) {
            Log.e("findBT Error", e.toString(), e);
        } finally {
            ((BillSuccessActivity) context).enableButton();
        }
    }

    public void printCall() {
        try {
            findBT();
            /*openBT();//socket connection
            BluetoothPrintData();*/
        } catch (Exception e) {
            Log.e("Print Call", e.toString(), e);
        } finally {
            ((BillSuccessActivity) context).enableButton();
        }
    }

    /*private void BluetoothPrintData() {
        StringBuilder textData = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date convertedDate = new Date();
        Calendar myCalendar = Calendar.getInstance();
        int am_pm = myCalendar.get(Calendar.AM_PM);
        String amOrpm = ((am_pm == Calendar.AM) ? "am" : "pm");
        try {
            convertedDate = dateFormat.parse(updateStockRequestDto.getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
        String refNo = updateStockRequestDto.getTransactionId();
        String billDate = dateFormat.format(convertedDate);
        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>(updateStockRequestDto.getBillItemDto());
        NumberFormat formatter = new DecimalFormat("00.00");
//        formatter.setRoundingMode(RoundingMode.CEILING);
//        NumberFormat formatSignle = new DecimalFormat("#0.00");
//        formatSignle.setRoundingMode(RoundingMode.CEILING);
        textData.append("                " + context.getString(R.string.print_title) + "    ");
        textData.append("\n");
        textData.append("\n");
        textData.append("" + context.getString(R.string.print_billid) + "     : " + refNo + "\n");
        textData.append("" + context.getString(R.string.print_date) + "        : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData.append("" + context.getString(R.string.print_shopcode) + "   : " + SessionId.getInstance().getFpsCode() + "\n");
        textData.append("" + context.getString(R.string.print_cardno) + "     : " + rationCardNumber + "\n");
        textData.append("-----------------------------------------\n");
        textData.append("#    " + context.getString(R.string.print_commodity) + "        " + context.getString(R.string.print_qty) + "      " + context.getString(R.string.print_price) + "\n");
        textData.append("-----------------------------------------\n");
        int i = 1;
        for (BillItemProductDto bItems : billItems) {
            String productName = bItems.getProductName() + "                                 ";
            String unit = bItems.getProductUnit();
            if (unit.equals("LTR")) {
                unit = "LT";
            }
            String quantity = "" + formatter.format(bItems.getQuantity()) + "(" + unit + ")";
            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
            String price = "" + amt1;
            String serialNo = i + "                        ";
            textData.append("" + StringUtils.substring(serialNo, 0, 2) + "  " + StringUtils.substring(productName, 0, 11) + "     " + quantity + "  " + pad(price, 7, " ") + "\n");
            i++;
        }
        textData.append("-----------------------------------------\n");
        String amt2 = Util.priceRoundOffFormat(updateStockRequestDto.getAmount());
        String ledgerAmount = "   " + pad(amt2, 7, " ");
        textData.append("      " + context.getString(R.string.print_total) + "            " + ledgerAmount + "\n");
        textData.append("-----------------------------------------\n");
        textData.append("\n");
        textData.append("            " + context.getString(R.string.print_wishes) + " \n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
       IntentPrint(textData.toString());


       // Start of Printing content text size testing //
      *//*  StringBuilder textData2 = new StringBuilder();
        textData2.append("                " + context.getString(R.string.print_title) + "    ");
        textData2.append("\n");
        textData2.append("" + context.getString(R.string.print_billid) + "     : " + refNo + "\n");
        textData2.append("" + context.getString(R.string.print_date) + "        : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData2.append("-----------------------------------------\n");
        textData2.append("#    " + context.getString(R.string.print_commodity) + "        " + context.getString(R.string.print_qty) + "      " + context.getString(R.string.print_price) + "\n");
        textData2.append("-----------------------------------------\n");
        IntentPrint(textData2.toString());*//*
        // End of Printing content text size testing //




    }*/

    private void BluetoothPrintData() {
        StringBuilder textData = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date convertedDate = new Date();
        Calendar myCalendar = Calendar.getInstance();
        int am_pm = myCalendar.get(Calendar.AM_PM);
        String amOrpm = ((am_pm == Calendar.AM) ? "am" : "pm");
        try {
            convertedDate = dateFormat.parse(updateStockRequestDto.getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        String refNo = updateStockRequestDto.getTransactionId();
        String billDate = dateFormat.format(convertedDate);
        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>(updateStockRequestDto.getBillItemDto());
        NumberFormat formatter = new DecimalFormat("00.00");
//        formatter.setRoundingMode(RoundingMode.CEILING);
//        NumberFormat formatSignle = new DecimalFormat("#0.00");
//        formatSignle.setRoundingMode(RoundingMode.CEILING);
        /*textData.append("          " + context.getString(R.string.print_title1) + "    " + "\n");
        textData.append("             " + context.getString(R.string.print_title2) + "    ");
        textData.append("\n");
        textData.append("\n");
        textData.append("" + context.getString(R.string.print_shopcode) + "   : " + SessionId.getInstance().getFpsCode() + "\n");
        textData.append("" + context.getString(R.string.print_billid) + "           : " + refNo + "\n");
        textData.append("" + context.getString(R.string.print_date) + "      : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData.append("" + context.getString(R.string.print_cardno) + "      : " + rationCardNumber + "\n");
        textData.append("----------------------------------------------\n");
        textData.append("#    " + context.getString(R.string.print_commodity) + "\n");
        textData.append("   " + context.getString(R.string.print_unit_rate) + "  " + context.getString(R.string.print_qty) + "    " + context.getString(R.string.print_price) + "\n");
        textData.append("----------------------------------------------\n");*/



        textData.append("            " + context.getString(R.string.print_title1) + "    " + "\n");
        textData.append("           " + context.getString(R.string.print_title2) + "    ");
        textData.append("\n");
        textData.append("\n");
        textData.append("" + context.getString(R.string.print_shopcode) + "  : " + SessionId.getInstance().getFpsCode() + "\n");
        textData.append("" + context.getString(R.string.print_billid) + "    : " + refNo + "\n");
        textData.append("" + context.getString(R.string.print_date) + "       : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData.append("" + context.getString(R.string.print_cardno) + "    : " + rationCardNumber + "\n");
        textData.append("--------------------------------\n");
        textData.append("# " + context.getString(R.string.print_commodity)+" \n" + "  UNIT PRICE" + " | " + "QTY" + " | " + "PRICE" + "\n");
//        textData.append("                    PRICE \n");
        textData.append("--------------------------------\n");



        Log.e("Print2", textData.toString());
        int i = 1;
        for (BillItemProductDto bItems : billItems) {
            String productName = "";
            String unit = "";
            /*if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                productName = bItems.getLocalProductName() + "                                 ";
                unit = bItems.getLocalProductUnit();
                if (bItems.getProductUnit().equals("LTR")) {
                    if (bItems.getLocalProductUnit() != null) unit = bItems.getLocalProductUnit();
                    else unit = "LT";
                }
            } else {*/
                productName = bItems.getProductName() + "                                 ";
                unit = bItems.getProductUnit();
                if (unit.equals("LTR")) {
                    unit = "LT";
                }
//            }
            String quantity = "" + formatter.format(bItems.getQuantity()) + "(" + unit + ")";
            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
//            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
            String price = "" + amt1;
            String unitPriceRoundOff = priceRoundOffFormat(bItems.getCost());
            String serialNo = i + "";
            /*textData.append("" + StringUtils.substring(serialNo, 0, 2) + ")" + fixedLengthString(productName, 18) + "");*/
            textData.append("" + serialNo + ") " + fixedLengthString(productName, 23) + "\n");
            /*textData.append("" + unitPriceRoundOff + " | " + quantity + " | " + pad(price, 7, " ") + "\n");*/
            textData.append("   " + unitPriceRoundOff + " | " + quantity + " | " + price + "\n\n");
            i++;
        }
        textData.append("--------------------------------\n");
        String amt2 = Util.priceRoundOffFormat(updateStockRequestDto.getAmount());
        String ledgerAmount = " " + pad(amt2, 7, " ");
        textData.append(" " + context.getString(R.string.print_total) + "        " + ledgerAmount + "\n");
        textData.append("--------------------------------\n");
        textData.append("\n");
        textData.append("       " + context.getString(R.string.print_wishes) + " \n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        Log.e("Print", textData.toString());
//        mPrinter.printUnicodeText(textData.toString(), Layout.Alignment.ALIGN_NORMAL, mDefaultTextPaint);
        IntentPrint(textData.toString());
        // Start of Printing content text size testing //
      /*  StringBuilder textData2 = new StringBuilder();
        textData2.append("                " + context.getString(R.string.print_title) + "    ");
        textData2.append("\n");
        textData2.append("" + context.getString(R.string.print_billid) + "     : " + refNo + "\n");
        textData2.append("" + context.getString(R.string.print_date) + "        : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData2.append("-----------------------------------------\n");
        textData2.append("#    " + context.getString(R.string.print_commodity) + "        " + context.getString(R.string.print_qty) + "      " + context.getString(R.string.print_price) + "\n");
        textData2.append("-----------------------------------------\n");
        IntentPrint(textData2.toString());*/
        // End of Printing content text size testing //
    }

    private String priceRoundOffFormat(Double priceValue) {
        BigDecimal currQuantity = new BigDecimal(priceValue);
        currQuantity.setScale(2, RoundingMode.HALF_EVEN);
        priceValue = (double) Math.round(priceValue * 100);
        priceValue = priceValue / 100;
        NumberFormat formatter = new DecimalFormat("#00.00");
        String pr = formatter.format(priceValue);
        Double unitPriValue = Double.parseDouble(pr);
        String unitPrice = formatter.format(unitPriValue);
        return unitPrice;
    }

    private String fixedLengthString(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    // Tries to open a connection to the bluetooth printer device
    private void openBT() throws IOException {
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mBluetoothAdapter.cancelDiscovery();
            Log.e("state", "" + mmDevice.getBondState());
            mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            if (mmDevice.getBondState() == 2) {
                mmSocket.connect();
                mmOutputStream = mmSocket.getOutputStream();
            }
            Log.e("socket", "" + mmSocket);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
        } catch (Exception e) {
            Log.e("connection error", e.toString(), e);
        } finally {
            ((BillSuccessActivity) context).enableButton();
        }
    }

    private void IntentPrint(String txtValue) {
        try {
            Log.e("txtValue", "txtValue..." + txtValue);
            Thread.sleep(2000);
            byte[] buffer = txtValue.getBytes();
            Log.e("buffer", "" + buffer.toString());
            byte[] PrintHeader = {(byte) 0x55, 0, 0};
            Log.e("PrintHeader", "" + PrintHeader);
            PrintHeader[2] = (byte) buffer.length;
            Log.e("PrintHeader[2]", "" + PrintHeader[2]);
            if (PrintHeader.length > 128) {
                String value = "\nValue is more than 128 size\n";
            } else {
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
                if (mmOutputStream != null)
                    mmOutputStream.flush();
                printDialogStatus(context.getString(R.string.print_success));
                context.unregisterReceiver(mReceiver);
                if (mmOutputStream != null)
                    mmOutputStream.close();
                if (mmInputStream != null)
                    mmInputStream.close();
                mmSocket.close();
            }
        } catch (Exception e) {
            Log.e("errorPrint", e.toString(), e);
        } finally {
            ((BillSuccessActivity) context).enableButton();
        }
    }

    private String pad(String value, int length, String with) {
        StringBuilder result = new StringBuilder(length);
        // Pre-fill a String value
        result.append(fill(Math.max(0, length - value.length()), with));
        result.append(value);
        return result.toString();
    }

    private void printDialogStatus(final String printerStatus) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                Util.messageBar(context, printerStatus);
                Log.e("Local class", context.getLocalClassName());
                if (context.getLocalClassName().contains("BillSuccessActivity")) {
                    ((BillSuccessActivity) context).enableButtonAfterPrint();
                }
            }
        });
    }

    private String fill(int length, String with) {
        StringBuilder sb = new StringBuilder(length);
        while (sb.length() < length) {
            sb.append(with);
        }
        return sb.toString();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
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
                ((BillSuccessActivity) context).enableButton();
            }
        }
    };
}