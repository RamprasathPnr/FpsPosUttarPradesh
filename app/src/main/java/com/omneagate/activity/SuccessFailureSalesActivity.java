package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

public class SuccessFailureSalesActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_failure);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        String message = getIntent().getStringExtra("message");
        continuePage(message);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, com.omneagate.activity.SaleOrderActivity.class));
        Util.LoggingQueue(this, "Sales Success/Error page", "Back pressed Called");
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void continuePage(String message) {
        setUpPopUpPage();
//        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.success_status);
        ((TextView) findViewById(R.id.top_textView)).setText(R.string.success_status);
        Util.setTamilText((TextView) findViewById(R.id.buttonContinue), getString(R.string.sales));
        if (StringUtils.isEmpty(message)) {
            message = getString(R.string.internalError);
        }
        Util.LoggingQueue(this, "Sales Success/Error page", "Error:" + message);
        ((TextView) findViewById(R.id.textViewMessage)).setText(message);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.omneagate.activity.SuccessFailureSalesActivity.this, SaleOrderActivity.class));
                Util.LoggingQueue(com.omneagate.activity.SuccessFailureSalesActivity.this, "Sales Success/Error page", "Continue button called");
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
