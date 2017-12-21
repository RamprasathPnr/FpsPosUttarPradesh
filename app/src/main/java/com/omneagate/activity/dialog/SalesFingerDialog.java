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

import com.omneagate.Util.Util;
import com.omneagate.activity.AadharCardSalesActivity;
import com.omneagate.activity.BenefBfdScanActivity;
import com.omneagate.activity.BeneficiaryMenuActivity;
import com.omneagate.activity.OtpReqEnterActivity;
import com.omneagate.activity.R;
import com.omneagate.activity.TgDashBoardActivity;

/**
 * This dialog will appear on the time of user logout
 */
public class SalesFingerDialog extends Dialog implements View.OnClickListener {

    private final Activity context;  //    Context from the user
    String rcNo = "";

    /*Constructor class for this dialog*/
    public SalesFingerDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_bfd_reg_success_one);
        setCancelable(false);

       // String userText = "All commodities taken.";
        String userText="Current month quota is already drawn/taken";
        ((TextView) findViewById(R.id.tv_dialogStatus)).setText(userText);
        Button okButton = (Button) findViewById(R.id.buttonOk);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                dismiss();
                context.startActivity(new Intent(context, TgDashBoardActivity.class));
                context.finish();
                break;
        }
    }


}