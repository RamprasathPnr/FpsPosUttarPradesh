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

import com.omneagate.activity.BenefBfdScanActivity;
import com.omneagate.activity.MobileOTPNeedActivity;
import com.omneagate.activity.OtpReqEnterActivity;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class BiometricAuthRetryFailedDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user
    String benefIdStr = "", proxyIdStr = "", rationCardNo = "", encodedBiometric = "", memberTypeStr = "";

    /*Constructor class for this dialog*/
    public BiometricAuthRetryFailedDialog(Activity _context, String rcNo, String benefId, String proxyId, String encodedBioInfo, String memberType) {
        super(_context);
        context = _context;
        benefIdStr = benefId;
        proxyIdStr = proxyId;
        rationCardNo = rcNo;
        encodedBiometric = encodedBioInfo;
        memberTypeStr = memberType;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_biometric_auth_failed_retry);
        setCancelable(false);
        TextView message = (TextView) findViewById(R.id.textViewNwText);
//        String userText = "You have exceeded your maximum attempts. You will be navigated to OTP authentication.";
        ((TextView) findViewById(R.id.textViewNwTitle)).setText(R.string.bio_auth_failed);
        message.setText(R.string.exceeded_limit);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                Intent intent = new Intent(context, OtpReqEnterActivity.class);
                intent.putExtra("BenefId", benefIdStr);
                intent.putExtra("ProxyId", proxyIdStr);
                intent.putExtra("RcNumber", rationCardNo);
                intent.putExtra("EncodedBiometric", encodedBiometric);
                intent.putExtra("MemberType", memberTypeStr);
                context.startActivity(intent);
                dismiss();
                break;
        }
    }




}