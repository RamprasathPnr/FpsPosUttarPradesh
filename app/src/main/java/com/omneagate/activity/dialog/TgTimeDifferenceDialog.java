package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.activity.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 20/4/17.
 */
public class TgTimeDifferenceDialog extends Dialog implements View.OnClickListener{

    private Activity context;
    private String serverTime;


    public TgTimeDifferenceDialog(Activity context,String serverTime) {
        super(context);
        this.serverTime=serverTime;
        this.context=context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.tgdialog_date_change);
        setCancelable(false);
     //   Date serverTimeNow = new Date(serverTime);
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        ((TextView) findViewById(R.id.txt_server_time)).setText(context.getString(R.string.server_time)+" "+serverTime);
        ((TextView) findViewById(R.id.txt_device_time)).setText(context.getString(R.string.device_time)+" "+simpleDate.format(new Date()));
        Button yesButton = (Button) findViewById(R.id.buttonOk);
        yesButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                changeDeviceTime();
                dismiss();
                break;
        }
    }

    private void changeDeviceTime() {
        context.startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));

    }
}
