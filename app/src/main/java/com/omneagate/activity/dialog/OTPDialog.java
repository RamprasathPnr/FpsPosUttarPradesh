package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.Util.Util;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;

/**
 * This dialog will appear on the time of user login
 */
public class OTPDialog extends Dialog implements
        View.OnClickListener {
    //    Context from the user
    private final Activity context;

    BenefActivNewDto beneficiary;

    String number = "";

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
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_otp_data);
        setCancelable(false);
        LinearLayout mobileOtp = (LinearLayout) findViewById(R.id.myMobileOTPBackground);
        mobileOtp.removeAllViews();
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = vi.inflate(R.layout.view_received_otp, null);
        mobileOtp.addView(view);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        cancelButton.setOnClickListener(this);
        Button buttonRegenerate = (Button) findViewById(R.id.buttonRegenerate);
        buttonRegenerate.setOnClickListener(this);
        Util.LoggingQueue(context, "Card Request", "OTP dialog created");
        findViewById(R.id.button_one).setOnClickListener(this);
        findViewById(R.id.button_two).setOnClickListener(this);
        findViewById(R.id.button_three).setOnClickListener(this);
        findViewById(R.id.button_four).setOnClickListener(this);
        findViewById(R.id.button_five).setOnClickListener(this);
        findViewById(R.id.button_six).setOnClickListener(this);
        findViewById(R.id.button_seven).setOnClickListener(this);
        findViewById(R.id.button_eight).setOnClickListener(this);
        findViewById(R.id.button_nine).setOnClickListener(this);
        findViewById(R.id.button_zero).setOnClickListener(this);
        findViewById(R.id.imageView5).setOnClickListener(this);
        findViewById(R.id.button_bkSp).setOnClickListener(this);
        ((TextView) findViewById(R.id.enterOtp)).setTextColor(Color.parseColor("#c77000"));
        findViewById(R.id.buttonNeedOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOtp();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwCancel:
                dismiss();
                break;
            case R.id.buttonRegenerate:
                dismiss();
                break;
            case R.id.button_one:
                addNumber("1");
                break;
            case R.id.button_two:
                addNumber("2");
                break;
            case R.id.button_three:
                addNumber("3");
                break;
            case R.id.button_four:
                addNumber("4");
                break;
            case R.id.button_five:
                addNumber("5");
                break;
            case R.id.button_six:
                addNumber("6");
                break;
            case R.id.button_seven:
                addNumber("7");
                break;
            case R.id.button_eight:
                addNumber("8");
                break;
            case R.id.button_nine:
                addNumber("9");
                break;
            case R.id.button_zero:
                addNumber("0");
                break;
            case R.id.button_bkSp:
                removeNumber();
                break;
            case R.id.imageView5:
                number = "";
                setText();
                break;
            default:
                break;
        }
    }

    private void removeNumber() {
        if (number.length() > 0) {
            number = number.substring(0, number.length() - 1);
        } else {
            number = "";
        }
        setText();
    }

    private void addNumber(String text) {
        try {
            if (number.length() >= 7) {
                return;
            }

            number = number + text;
            setText();
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    private void setText() {
        ((TextView) findViewById(R.id.mobileNumberOTP)).setTextColor(Color.parseColor("#c77000"));
        ((TextView) findViewById(R.id.mobileNumberOTP)).setText(number);
    }

    private void getOtp() {
        if (StringUtils.isEmpty(number)) {
            Toast.makeText(context, "Enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (number.length() != 7) {
            Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        Util.LoggingQueue(context, "Card Request", "OTP dialog page:" + beneficiary.toString());
        dismiss();
    }

}

