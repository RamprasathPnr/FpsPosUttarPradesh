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

public class WrongDeviceDateDialog extends Dialog implements View.OnClickListener {

    private final Activity context;

    public WrongDeviceDateDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_wrong_device_date);
        setCancelable(false);
        Button yesButton = (Button) findViewById(R.id.buttonYes);
        yesButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
                context.startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
                dismiss();
                break;
        }
    }


}