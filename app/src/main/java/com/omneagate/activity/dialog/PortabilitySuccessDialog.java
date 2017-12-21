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
import com.omneagate.activity.TgDashBoardActivity;
import com.omneagate.activity.TgReceivegoodsComodityList;
import com.omneagate.printer.Usb_Printer;

/**
 * Created by root on 29/8/17.
 */
public class PortabilitySuccessDialog extends Dialog implements View.OnClickListener {


    private final Activity context;
    private TextView tvDialogStatus;
    private Button buttonOk;


    public PortabilitySuccessDialog(Activity context) {
        super(context);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.rocompletedsucessfully);
        tvDialogStatus =  (TextView)findViewById(R.id.dialogText);

        tvDialogStatus.setText("Request Posted Successfully");

        buttonOk = (Button)findViewById(R.id.confirmButton);
        buttonOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                Intent nextIntent = new Intent(context, TgDashBoardActivity.class);
                context.startActivity(nextIntent);
                dismiss();
                break;
            default:
                break;

        }
    }
}