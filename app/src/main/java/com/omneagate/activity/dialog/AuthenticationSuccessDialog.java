package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.DTO.RCAuthResponse;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.activity.R;
import com.omneagate.activity.TgDashBoardActivity;
import com.omneagate.activity.TgLoginActivity;
import com.omneagate.activity.TgProductListActivity;

/**
 * Created by root on 22/2/17.
 */
public class AuthenticationSuccessDialog extends Dialog implements View.OnClickListener {


    private final Activity context;
    private String strDialogText,strActivityName;
    private TextView dialogText;
    private Button confirmButton;
    private RCAuthResponse rcAuthResponse;


    public AuthenticationSuccessDialog(Activity context, String dialogText,String activityName) {
        super(context);
        this.context = context;
        this.strDialogText = dialogText;
        this.strActivityName=activityName;
    }

    public AuthenticationSuccessDialog(Activity context, String dialogText, String activityName, RCAuthResponse rcAuthResponse){
        super(context);
        this.context = context;
        this.strDialogText = dialogText;
        this.strActivityName=activityName;
        this.rcAuthResponse=rcAuthResponse;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_authentication_success);

        ConfigureInitView();

    }

    private void ConfigureInitView(){
        dialogText=(TextView)findViewById(R.id.dialogText);
        confirmButton=(Button)findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);
        dialogText.setText(""+strDialogText);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                dismiss();
                if(strActivityName.equalsIgnoreCase("TgScanFingerPrintActivity") ||strActivityName.equalsIgnoreCase("TgIrisScanActivity") ) {
                    Intent loginIntent = new Intent(context, TgDashBoardActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(loginIntent);
                    context.finish();
                }else{
                    EntitlementResponse.getInstance().setRcAuthResponse(null);
                    EntitlementResponse.getInstance().clear();
                    EntitlementResponse.getInstance().setRcAuthResponse(rcAuthResponse);
                    Intent loginIntent = new Intent(context, TgProductListActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(loginIntent);
                    context.finish();
                }
                break;
        }
    }
}