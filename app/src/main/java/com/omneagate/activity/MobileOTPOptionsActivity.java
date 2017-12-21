package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.Util;

public class MobileOTPOptionsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_moble_option);
        setUpSaleOrders();
    }


    /**
     * Initial setUp
     */
    private void setUpSaleOrders() {
        Util.LoggingQueue(this, "MobileOTPOptionsActivity", "Setting up main page");

        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.mobile_otp);
        Util.setTamilText((TextView) findViewById(R.id.i_need_otp), R.string.i_need_otp);
        Util.setTamilText((TextView) findViewById(R.id.i_have_otp), R.string.i_have_otp);

        findViewById(R.id.qrCodeSales).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileOTPNeed();
            }
        });
        findViewById(R.id.mobileOTPSales).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileOTP();
            }
        });
        setUpPopUpPage();
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /*
    *  Mobile OTP page calling
    * */
    private void mobileOTP() {
        startActivity(new Intent(this, MobileOTPActivity.class));
        Util.LoggingQueue(this, "Mobile Based", "I have otp called");
        finish();
    }

    /*
   *  Mobile OTP page calling
   * */
    private void mobileOTPNeed() {
        startActivity(new Intent(this, MobileOTPNeedActivity.class));
        Util.LoggingQueue(this, "Mobile Based", "I need Otp called");
        finish();
    }


    /**
     * Called when user press back button
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleOrderActivity.class));
        Util.LoggingQueue(this, "Mobile Based", "Back pressed");
        finish();
    }

    //Concrete method
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
    }
}
