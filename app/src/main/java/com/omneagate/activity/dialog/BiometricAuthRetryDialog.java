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
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class BiometricAuthRetryDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user
    private int attempt;

    /*Constructor class for this dialog*/
    public BiometricAuthRetryDialog(Activity _context, int attempt) {
        super(_context);
        this.attempt = attempt;
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_biometric_auth_retry);
        setCancelable(false);
        int remain = 2 - attempt;
        ((TextView) findViewById(R.id.textViewNwTitle)).setText(R.string.bio_auth_failed);
        String userText = "";
        /*if(remain == 1) {
            userText = R.string.one_more_attempt;
        }
        else {
            userText = "Your biometric authentication failed. Please use different finger. You have " + remain + " more attempts.";
        }*/
        ((TextView) findViewById(R.id.textViewNwTextSecond)).setText( R.string.one_more_attempt);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
//                ((BenefBfdScanActivity) context).process();
                dismiss();
                break;
        }
    }


}