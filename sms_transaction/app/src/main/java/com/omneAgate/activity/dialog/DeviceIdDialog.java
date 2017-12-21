package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class DeviceIdDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public DeviceIdDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_deviceid);
        setCancelable(false);
        TextView message = (TextView) findViewById(R.id.textViewNwText);
        TextView apkVersion = (TextView) findViewById(R.id.textViewDevice);
        ((TextView) findViewById(R.id.textViewNwTitle)).setText("Device Id");
        AndroidDeviceProperties props = new AndroidDeviceProperties(context);
        message.setText("Device Id : " + props.getDeviceProperties().getSerialNumber());
        apkVersion.setText("Apk Version  : " + props.getDeviceProperties().getVersionCode());
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setText("OK");
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
                break;
        }
    }


}