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

import com.omneagate.DTO.RCAuthResponse;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.activity.OtpReqEnterActivity;
import com.omneagate.activity.R;
import com.omneagate.activity.TgDashBoardActivity;
import com.omneagate.activity.TgProductListActivity;

/**
 * Created by root on 22/2/17.
 */
public class AuthenticationMemberDialog extends Dialog implements View.OnClickListener {


    private final Activity context;
    private String ReponseMessage;
    private TextView dialogText;
    private Button confirmButton;



    public AuthenticationMemberDialog(Activity context, String ReponseMessage) {
        super(context);
        this.context = context;
        this.ReponseMessage = ReponseMessage;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_authenticate_member);

        ConfigureInitView();

    }

    private void ConfigureInitView(){
        dialogText=(TextView)findViewById(R.id.dialogText);
        confirmButton=(Button)findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(this);
        dialogText.setText(""+ReponseMessage);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                dismiss();
                Intent intent = new Intent(context, TgDashBoardActivity.class);
                context.startActivity(intent);
                break;
        }
    }
}