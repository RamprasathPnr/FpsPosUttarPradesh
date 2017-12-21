package com.omneagate.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.Util.XMLUtil;

import java.util.Timer;
import java.util.TimerTask;

public class TgSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout lLDevice,lLServer;
    private ImageView imageBack;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_settings);
        lLDevice=(LinearLayout)findViewById(R.id.lLDevice);
        lLServer=(LinearLayout)findViewById(R.id.lLServer);
        lLDevice.setOnClickListener(this);
        lLServer.setOnClickListener(this);

        imageBack=(ImageView)findViewById(R.id.imageViewBack);
        imageBack.setOnClickListener(this);
        ((TextView) findViewById(R.id.top_textView)).setText("Settings");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lLDevice:
                Intent deviceIntent=new Intent(TgSettingsActivity.this,TgDeviceSettingActivity.class);
                startActivity(deviceIntent);
                finish();
                break;
            case R.id.lLServer:
                Intent serverIntent=new Intent(TgSettingsActivity.this,TgServerSettingsActivity.class);
                startActivity(serverIntent);
                finish();
                break;
            case R.id.imageViewBack:
                onBackPressed();
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Intent in =new Intent(TgSettingsActivity.this,TgLoginActivity.class);
        startActivity(in);
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();



    }

}
