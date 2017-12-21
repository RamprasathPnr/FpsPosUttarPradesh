package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.Util.Util;
import com.omneagate.activity.R;
import com.omneagate.activity.SMSActivation.CardRegistrationActivity;

import org.apache.commons.lang3.StringUtils;

/**
 * This dialog will appear on the time of user login
 */
public class OTPDialog extends Dialog implements
        View.OnClickListener {
    EditText otpText;
    //    Context from the user
    private final Activity context;

    BenefActivNewDto beneficiary;

    /*Constructor class for this dialog*/
    public OTPDialog(Activity _context, BenefActivNewDto beneficiary) {
        super(_context);
        context = _context;
        this.beneficiary = beneficiary;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_otp_data);
        setCancelable(false);
        ((TextView) findViewById(R.id.textViewNwTitle)).setText("OTP Details");
        ((TextView) findViewById(R.id.textViewNoOfDaysLabel)).setText("Enter OTP");
        otpText = (EditText) findViewById(R.id.editTextNoOfDays);
        otpText.setHint("Enter OTP");
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        cancelButton.setOnClickListener(this);
        Button buttonRegenerate = (Button) findViewById(R.id.buttonRegenerate);
        buttonRegenerate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                String noOfDays = ((EditText) findViewById(R.id.editTextNoOfDays)).getText().toString().trim();
                if (storeInLocal(noOfDays)) {
                    ((CardRegistrationActivity) context).submitOtp(beneficiary, noOfDays);
                    dismiss();
                }else{
                    InputMethodManager im = (InputMethodManager) context.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Util.messageBar(context,"Invalid OTP");
                }
                break;
            case R.id.buttonNwCancel:
                dismiss();
                break;
            case R.id.buttonRegenerate:
                ((CardRegistrationActivity) context).submitOtpRegenerationRequest(beneficiary);
                dismiss();
                break;
        }
    }


    /**
     * Store changed ip in shared preference
     * returns true if value present else false
     */
    private boolean storeInLocal(String noOfDays) {
        if (StringUtils.isEmpty(noOfDays) || noOfDays == null) {
            return false;
        }
        if (noOfDays.length() == 7)
            return true;
        else {
            return false;
        }
    }
}

