package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

public class SuccessFailureUpdationActivity extends BaseActivity {
    String aregisterFlag = "", mobileFlag = "", aadharFlag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_failure_update);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Util.LoggingQueue(SuccessFailureUpdationActivity.this, "SuccessFailureUpdationActivity", "onCreate() called ");

        String message = getIntent().getStringExtra("error");

        aregisterFlag = getIntent().getStringExtra("aregister");
        mobileFlag = getIntent().getStringExtra("mobile");
        aadharFlag = getIntent().getStringExtra("aadhar");


        Util.LoggingQueue(SuccessFailureUpdationActivity.this, "SuccessFailureUpdationActivity", "onCreate() called message "+message);


        continuePage(message);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        Util.LoggingQueue(this, "Success/Error page", "Back pressed");
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void continuePage(String message) {
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setUpPopUpPage();
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.updateRationCard));
        Util.setTamilText((TextView) findViewById(R.id.buttonContinue), getString(R.string.sales));
        int value = Integer.parseInt(message);
        Drawable drawable;
        String userMessage = "";
        if (value == 0) {
            if(android.os.Build.VERSION.SDK_INT >= 21){
                drawable = getResources().getDrawable(R.drawable.icon_sucess, getTheme());
            } else {
                drawable = getResources().getDrawable(R.drawable.icon_sucess);
            }
            ((TextView) findViewById(R.id.textViewMessage)).setTextColor(Color.parseColor("#029555"));
            findViewById(R.id.buttonContinue).setBackgroundColor(Color.parseColor("#029555"));
//            userMessage = getString(R.string.aadhar_mobile_success);
            if(GlobalAppState.language.equalsIgnoreCase("hi")) {
                userMessage = aregisterFlag + mobileFlag + aadharFlag + " மேம்படுத்தப்பட்டுவிட்டது";
            }
            else {
                userMessage = aregisterFlag + mobileFlag + aadharFlag + " updated successfully";
            }
        }
        else if (value == 1) {
            if(android.os.Build.VERSION.SDK_INT >= 21){
                drawable = getResources().getDrawable(R.drawable.icon_fail, getTheme());
            } else {
                drawable = getResources().getDrawable(R.drawable.icon_fail);
            }
            ((TextView) findViewById(R.id.textViewMessage)).setTextColor(Color.parseColor("#ED1C24"));
            findViewById(R.id.buttonContinue).setBackgroundColor(Color.parseColor("#ED1C24"));
            userMessage = getString(R.string.no_change);
        }
        else {
            if(android.os.Build.VERSION.SDK_INT >= 21){
                drawable = getResources().getDrawable(R.drawable.icon_fail, getTheme());
            } else {
                drawable = getResources().getDrawable(R.drawable.icon_fail);
            }
            ((TextView) findViewById(R.id.textViewMessage)).setTextColor(Color.parseColor("#ED1C24"));
            findViewById(R.id.buttonContinue).setBackgroundColor(Color.parseColor("#ED1C24"));
            userMessage = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(value));
            if (userMessage == null) {
                userMessage = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(5037));
            }
        }
        ((ImageView)findViewById(R.id.successfails)).setImageDrawable(drawable);
        ((TextView) findViewById(R.id.textViewMessage)).setText(userMessage);
        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuccessFailureUpdationActivity.this, RationCardUpdateActivity.class));
                Util.LoggingQueue(SuccessFailureUpdationActivity.this, "Success/Error page", "Continue button called");
                finish();
            }
        });
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
