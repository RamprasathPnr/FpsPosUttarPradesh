package com.omneagate.printer;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.omneagate.activity.R;

import java.util.Timer;
import java.util.TimerTask;

import lombok.Data;


@Data
public class BlueToothPrinter implements Printer {


    BluetoothDevice selectedDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean connectionStatus = false;
    private String printData;
    private BluetoothSocket mmSocket;
    Context mcontext;
    BluetoothConnector bc;


    public BlueToothPrinter(BluetoothDevice device) {
        selectedDevice = device;
        /* printJob(); */
    }

    // Create a BroadcastReceiver for ACTION_FOUND

    BlueToothPrinter() {
        /* printJob(); */
    }

    public void setSelectedDevice(BluetoothDevice bt) {
        selectedDevice = bt;
    }

    @Override
    public void print() {
        printJob();
    }

    @Override
    public void discover() {

    }

    private void connect(BluetoothDevice device) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (selectedDevice == null) {
            return;
        }
        if (mBluetoothAdapter == null) {
            return;
        }
         bc = new BluetoothConnector(selectedDevice, true,
                mBluetoothAdapter, null);
        try {
            mmSocket = bc.connect().getUnderlyingSocket();
            connectionStatus = true;
        } catch (Exception e) {
            Log.e("socket connection null", "Socket is null");
            mmSocket = null;
            connectionStatus = false;
            clear();

        }
    }

    public void printJob() {
        if (printData == null) {
            Log.e("printdata ", "printdata is not set !!!!!");
            return;
        }
        if (connectionStatus == false)
            connect(selectedDevice);

        if (mmSocket == null) {
            connect(selectedDevice);
            Log.e("Socket is null", "Socket is null");

        }
        IntentPrint(printData);

        /*try {
            mmSocket.getOutputStream().write(printData.getBytes());
            mmSocket.getOutputStream().write("\n\n".getBytes());
            mmSocket.getOutputStream().flush();
        } catch (Exception e) {
            //log.error("Error", e);
            Log.e("BTPrinter", e.toString(), e);
            clear();
        }*/
    }

    public void clear() {
        selectedDevice = null;
    }

    public void setPrintData(Context context,String data)
    {
         printData = data;
         mcontext = context;

    }
    public void IntentPrint(String printData)
    {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String txtvalue= printData;

        try
        {
            byte[] buffer = txtvalue.getBytes();
            byte[] PrintHeader = { (byte) 0x55,0,0 };
            PrintHeader[2]=(byte) buffer.length;
            if(PrintHeader.length>128)
            {
                String value= "\nValue is more than 128 size\n";

            }
            else
            {

                try
                {
                    for(int i=4;i<=PrintHeader.length-1;i++)
                    {
                        mmSocket.getOutputStream().write(PrintHeader[i]);

                    }
                    for(int i=4;i<=buffer.length-1;i++)
                    {
                        mmSocket.getOutputStream().write(buffer[i]);

                    }
                    mmSocket.getOutputStream().flush();
                    printDialogStatus(mcontext.getString(R.string.print_success));

                }
                catch(Exception ex)
                {

                    printDialogStatus(mcontext.getString(R.string.print_failed));

                }
            }
        } catch (Exception e) {
            Log.e("errorprint",e.toString(),e);
              printDialogStatus(mcontext.getString(R.string.print_failed));

        }
    }
    private void printDialogStatus(String printerstatus)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogview = inflater.inflate(R.layout.printerstatusdialog, null);
        final AlertDialog popupDia =builder.create();
        TextView status = (TextView)dialogview.findViewById(R.id.tvResponseTitle);
        popupDia.setView(dialogview);
        popupDia.setCanceledOnTouchOutside(true);
        popupDia.setCancelable(true);
        popupDia.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        status.setText(printerstatus);
        popupDia.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                popupDia.dismiss();
            }
        });
        WindowManager.LayoutParams wmlp = popupDia.getWindow().getAttributes();
        wmlp.gravity = Gravity.BOTTOM;

        popupDia.show();
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                popupDia.dismiss(); // when the task active then close the dialog
                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, 3000); // after 2 second (or 2000 miliseconds), the task will be active.


    }

}
