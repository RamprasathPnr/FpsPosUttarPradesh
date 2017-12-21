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
import com.omneagate.activity.TgSalesActivity;
import com.omneagate.activity.TgSalesConfirmationActivity;
import com.omneagate.printer.Usb_Printer;

/**
 * Created by root on 3/3/17.
 */
public class SaleTransactionCompleted extends Dialog implements View.OnClickListener {


    private final Activity context;
    private TextView tvDialogStatus;
    private Button buttonOk;


    public SaleTransactionCompleted(Activity context) {
        super(context);
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_sale_completed);
        Usb_Printer.auto_print =true;
        buttonOk =(Button)findViewById(R.id.confirmButton);
        buttonOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
//                MySharedPreference.writeString(context, MySharedPreference.RATION_CARD_NUMBER, TgSalesActivity.edtRationcardNumber.getText().toString());
               ((TgSalesConfirmationActivity) context).Print();
                Usb_Printer.auto_print =false;
                dismiss();
                break;
            default:
                break;

        }
    }
}