package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.omneagate.activity.R;
import com.omneagate.activity.TgMobileNumUpdateDetailActivity;
import com.omneagate.activity.TgMobileNumberUpdateActivity;

/**
 * Created by root on 3/3/17.
 */
public class MobileOTPDialog extends Dialog implements View.OnClickListener {
    private Activity context;
    private EditText edtEnterOtp;
    private Button btResendOtp, btCancel, btSubmit;
    private String mobileNum, aadhaarNum;

    public MobileOTPDialog(Activity context, String mobileNum, String aadhaarNum) {
        super(context);
        this.context = context;
        this.mobileNum = mobileNum;
        this.aadhaarNum = aadhaarNum;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_enter_mobile_otp);
        InitView();
    }

    private void InitView() {
        try {
            edtEnterOtp = (EditText) findViewById(R.id.edtEnterOtp);
            btResendOtp = (Button) findViewById(R.id.btResendOtp);
            btResendOtp.setOnClickListener(this);
            btCancel = (Button) findViewById(R.id.btCancel);
            btCancel.setOnClickListener(this);
            btSubmit = (Button) findViewById(R.id.btSubmit);
            btSubmit.setOnClickListener(this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btResendOtp:
                ((TgMobileNumberUpdateActivity) context).SendOTP();
                dismiss();
                break;
            case R.id.btCancel:
                dismiss();
                break;
            case R.id.btSubmit:
                if (edtEnterOtp.getText().toString().trim().isEmpty()) {
                    Toast.makeText(context, context.getResources().getString(R.string.Please_enter_the_OTP), Toast.LENGTH_SHORT).show();
                    return;
                } else if (edtEnterOtp.getText().toString().trim().length() < 6){
                    Toast.makeText(context, context.getResources().getString(R.string.Please_enter_crt_the_OTP), Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    String otp = edtEnterOtp.getText().toString();
                    Intent in = new Intent(context, TgMobileNumUpdateDetailActivity.class);
                    in.putExtra("customerUid", aadhaarNum);
                    in.putExtra("OTP", otp);
                    in.putExtra("MobileNo", mobileNum);
                    context.startActivity(in);
                    context.finish();
                }

                break;
            default:
                break;
        }

    }
}
