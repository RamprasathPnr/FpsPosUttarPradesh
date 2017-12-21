package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.activity.R;

/**
 * Created by root on 22/2/17.
 */
public class AuthenticationFailedDialog extends Dialog implements View.OnClickListener {


    private final Activity context;
    private TextView tvDialogStatus;
    private Button buttonOk;
    private String strDialogText;


    public AuthenticationFailedDialog(Activity context, String dialogText) {
        super(context);
        this.context = context;
        this.strDialogText = dialogText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_authentication_failed);

        tvDialogStatus =(TextView)findViewById(R.id.tv_dialogStatus);
        tvDialogStatus.setText(""+strDialogText);
        buttonOk =(Button)findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                dismiss();
                break;
            default:
                break;

        }
    }
}