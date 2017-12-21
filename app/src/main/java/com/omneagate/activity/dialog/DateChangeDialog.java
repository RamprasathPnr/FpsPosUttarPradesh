package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
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
 * This dialog will appear on the time of user logout
 */
public class DateChangeDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user
    private final long serverTime;

    /*Constructor class for this dialog*/
    public DateChangeDialog(Activity _context, long timeServer) {
        super(_context);
        context = _context;
        serverTime = timeServer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_date_change);
        setCancelable(false);
        Date serverTimeNow = new Date(serverTime);
        SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm:ss");

        ((TextView) findViewById(R.id.serverTime)).setText(simpleDate.format(serverTimeNow));
        ((TextView) findViewById(R.id.deviceTime)).setText(simpleDate.format(new Date()));
        Button yesButton = (Button) findViewById(R.id.buttonYes);
        yesButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
                submitCloseSale();
                dismiss();
                break;
        }
    }

    private void submitCloseSale() {
        context.startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
     /*startActivity(new Intent(context, Lo.class));
     finish();*/
    }
}