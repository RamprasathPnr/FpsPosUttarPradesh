package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.activity.R;
import com.omneagate.activity.TgLoginActivity;

/**
 * Created by root on 27/3/17.
 */
public class TgGenericErrorDialog extends Dialog implements View.OnClickListener {


    private final Activity context;
    private TextView tvDialogStatus;
    private Button buttonOk;
    private String strDialogText;
    private boolean logoutStatus=false;


    public TgGenericErrorDialog(Activity context, String dialogText) {
        super(context);
        this.context = context;
        this.strDialogText = dialogText;
    }
    public TgGenericErrorDialog(Activity context,String dialogText,boolean logoutStatus){
        super(context);
        this.context = context;
        this.strDialogText = dialogText;
        this.logoutStatus =logoutStatus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.generic_error_message);

        tvDialogStatus =(TextView)findViewById(R.id.tv_dialogStatus);
        tvDialogStatus.setText(""+strDialogText);
        buttonOk =(Button)findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                if(logoutStatus){
                    Intent in =new Intent(context, TgLoginActivity.class);
                    context.startActivity(in);
                    context.finish();
                    dismiss();
                }else{
                    dismiss();
                }

                break;
            default:
                break;

        }
    }
}