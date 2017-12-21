package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;

import java.util.Timer;
import java.util.TimerTask;

public class TgPaymentModeActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout AepsMode;
    private LinearLayout cashMode;
    private LinearLayout cardMode;
    private LinearLayout tWalletMode;
    private ImageView imageViewBack;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private Button btnBack;


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tgpayment_mode);
        initView();


    }


    @Override
    public void onBackPressed() {
        Intent salesIntent = new Intent(TgPaymentModeActivity.this, TgDashBoardActivity.class);
        startActivity(salesIntent);
        finish();
    }

    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        AepsMode = (LinearLayout) findViewById(R.id.AepsMode);
        cashMode = (LinearLayout) findViewById(R.id.cashMode);
        cardMode = (LinearLayout) findViewById(R.id.CardMode);
        tWalletMode=(LinearLayout)findViewById(R.id.tWalletMode);

//        AepsMode.setOnClickListener(this);
        cashMode.setOnClickListener(this);
        tWalletMode.setOnClickListener(this);
//        CardMode.setOnClickListener(this);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        Util.setTamilText((TextView) findViewById(R.id.top_textView), getString(R.string.select_mode));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.CardMode:
                LoginData.getInstance().setCashMode("Card");
                LoginData.getInstance().setPayType(4);
                Intent intetSale1 =new Intent(TgPaymentModeActivity.this,TgSalesActivity.class);
                startActivity(intetSale1);
                finish();
                break;
            case R.id.AepsMode:
                LoginData.getInstance().setCashMode("Aeps");
                LoginData.getInstance().setPayType(2);
                Intent intetSale2 =new Intent(TgPaymentModeActivity.this,TgSalesActivity.class);
                startActivity(intetSale2);
                finish();
                break;
            case R.id.cashMode:
                LoginData.getInstance().setCashMode("Cash");
                LoginData.getInstance().setPayType(1);
                Intent intetSale3 =new Intent(TgPaymentModeActivity.this,TgSalesActivity.class);
                startActivity(intetSale3);
                finish();
                break;
            case R.id.tWalletMode:
                LoginData.getInstance().setCashMode("Twallet");
                LoginData.getInstance().setPayType(3);
                Intent tWalletMode =new Intent(TgPaymentModeActivity.this,TgSalesActivity.class);
                startActivity(tWalletMode);
                finish();
                break;
            case R.id.imageViewBack:
                Intent imageViewBack =new Intent(TgPaymentModeActivity.this,TgDashBoardActivity.class);
                startActivity(imageViewBack);
                finish();

            case R.id.btnBack:
                Intent intentBack =new Intent(TgPaymentModeActivity.this,TgDashBoardActivity.class);
                startActivity(intentBack);
                finish();
                break;

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime);

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgPaymentModeActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

}
