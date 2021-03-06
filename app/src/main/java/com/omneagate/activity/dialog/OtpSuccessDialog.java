package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.activity.AadharCardSalesActivity;
import com.omneagate.activity.BenefBfdScanActivity;
import com.omneagate.activity.OtpReqEnterActivity;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class OtpSuccessDialog extends Dialog implements View.OnClickListener {

    private final Activity context;  //    Context from the user
    String rcNo = "";

    /*Constructor class for this dialog*/
    public OtpSuccessDialog(Activity _context, String rcNumber) {
        super(_context);
        context = _context;
        rcNo = rcNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_otp_success);
        setCancelable(false);
        ((TextView) findViewById(R.id.textViewNwTitle)).setText(R.string.otp_success);
//        String userText = "Your OTP authentication is success. You will be navigated to Entitlement page.";
        ((TextView) findViewById(R.id.textViewNwTextSecond)).setText(R.string.otp_success_body);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                ((OtpReqEnterActivity) context).getEntitlement(rcNo);
                dismiss();
                break;
        }
    }


}