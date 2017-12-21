package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

public class SuccessFailureUpdateActivity extends BaseActivity {


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
        startActivity(new Intent(this, BeneficiaryMenuActivity.class));
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void continuePage(String message) {
        setUpPopUpPage();
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.success_activation);
        Util.LoggingQueue(this, "Card Activation end", "Setting up main page");
        Util.setTamilText((TextView) findViewById(R.id.buttonContinue), getString(R.string.sales));
        if (StringUtils.isEmpty(message)) {
            message = getString(R.string.cardAlreadyActivated);
        }
        Util.setTamilText(((TextView) findViewById(R.id.textViewMessage)), message);
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
                onBackPressed();
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
