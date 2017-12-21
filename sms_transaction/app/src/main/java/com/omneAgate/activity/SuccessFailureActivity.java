package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;

public class SuccessFailureActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_failure);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        String message = getIntent().getStringExtra("message");
        continuePage(message);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void continuePage(String message) {
        Util.setTamilText((TextView) findViewById(R.id.buttonContinue), getString(R.string.sales));
        ((TextView) findViewById(R.id.textViewMessage)).setText(message);
        ((Button) findViewById(R.id.buttonContinue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuccessFailureActivity.this, SaleActivity.class));
                finish();
            }
        });
    }
}
