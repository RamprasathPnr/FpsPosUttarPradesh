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
import com.omneagate.activity.TgReceiveGoods;
import com.omneagate.activity.TgReceivegoodsComodityList;
import com.omneagate.activity.TgSalesConfirmationActivity;
import com.omneagate.printer.Usb_Printer;

/**
 * Created by root on 13/3/17.
 */
public class RoSucessDialog  extends Dialog implements View.OnClickListener {


    private final Activity context;
    private TextView tvDialogStatus;
    private Button buttonOk;


    public RoSucessDialog(Activity context) {
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

        buttonOk =(Button)findViewById(R.id.confirmButton);
        buttonOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:

               /* Intent in = new Intent(context, TgReceiveGoods.class);
                context.startActivity(in);
                context.finish();*/
                ((TgReceivegoodsComodityList) context).Print();
                Usb_Printer.auto_print =false;
                dismiss();
                break;
            default:
                break;

        }
    }
}