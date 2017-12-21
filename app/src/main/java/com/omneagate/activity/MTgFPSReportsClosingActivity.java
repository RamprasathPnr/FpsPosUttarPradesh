package com.omneagate.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MTgFPSReportsClosingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageViewBack;

    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtg_fpsreports_closing);
        message = getIntent().getStringExtra("mode");
        initView();

        // mode=Integer.parseInt(message);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                Intent backIntent = new Intent(MTgFPSReportsClosingActivity.this,TgReportsDashBoardActivity.class);
                startActivity(backIntent);
                finish();
                break;

            default:
                break;

        }

    }

    private void initView() {
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);


    ((TextView) findViewById(R.id.top_textView)).setText(message);






    }

    @Override
    public void onBackPressed() {
        Intent backIntent =new Intent(MTgFPSReportsClosingActivity.this,TgReportsDashBoardActivity.class);
        startActivity(backIntent);
        finish();

    }
}
