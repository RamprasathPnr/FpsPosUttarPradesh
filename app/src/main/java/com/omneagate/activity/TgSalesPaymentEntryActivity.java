package com.omneagate.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.XMLUtil;

import java.util.Timer;
import java.util.TimerTask;

public class TgSalesPaymentEntryActivity extends BaseActivity {
    private Button btnSubmit ;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg__sales_payment_mode);
        setPopUpPage();
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(TgSalesPaymentEntryActivity.this,TgReceiveGoods.class);
                startActivity(i);
                finish();
            }
        });
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
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgSalesPaymentEntryActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
