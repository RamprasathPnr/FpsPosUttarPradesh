package com.omneagate.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class TgDashBoardActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout btSale;
    private LinearLayout btReceiveGoods;
    private LinearLayout btReports;
    private LinearLayout btMobileNumberUpdate;
    private LinearLayout btAuthenticateMember;
    private LinearLayout btPortability;
    private ImageView imageViewBack;

    private TextView fpsIdLay;
    private LinearLayout btReceiveKerosene;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_dash_board);
        initView();
    }

    private void initView() {
        try {
            setPopUpPage();

            btSale = (LinearLayout) findViewById(R.id.btSale);
            btReceiveGoods = (LinearLayout) findViewById(R.id.btReceiveGoods);
            btReports = (LinearLayout) findViewById(R.id.btReports);
            btMobileNumberUpdate = (LinearLayout) findViewById(R.id.btMobileNumberUpdate);
            btAuthenticateMember = (LinearLayout) findViewById(R.id.btAuthenticateMember);
            btReceiveKerosene = (LinearLayout) findViewById(R.id.btReceiveKerosene);
            btPortability = (LinearLayout) findViewById(R.id.btPortability);

            btnBack = (Button) findViewById(R.id.btnBack);
            btnBack.setOnClickListener(this);

            btSale.setOnClickListener(this);
            btReceiveGoods.setOnClickListener(this);
            btReports.setOnClickListener(this);
            btMobileNumberUpdate.setOnClickListener(this);
            btAuthenticateMember.setOnClickListener(this);
            btPortability.setOnClickListener(this);

            btReceiveKerosene.setOnClickListener(this);

            imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
            imageViewBack.setOnClickListener(this);


            ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
            updateDateTime();
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.dashboard);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btSale:
                Intent saleIntent = new Intent(TgDashBoardActivity.this, TgPaymentModeActivity.class);
                startActivity(saleIntent);
                finish();
                break;
            case R.id.btReceiveGoods:
                Intent submitIntent = new Intent(TgDashBoardActivity.this, TgReceiveGoods.class);
                startActivity(submitIntent);
                finish();
                break;
            case R.id.btReports:
                Intent reportIntent = new Intent(TgDashBoardActivity.this, TgReportsDashBoardActivity.class);
                startActivity(reportIntent);
                finish();
                break;
            case R.id.btMobileNumberUpdate:
                Intent mobileNumberIntent = new Intent(TgDashBoardActivity.this, TgMobileNumberUpdateActivity.class);
                startActivity(mobileNumberIntent);
                finish();
                break;
            case R.id.btAuthenticateMember:
                Intent authenticateMemberIntent = new Intent(TgDashBoardActivity.this, TgAuthenticateMemberActivity.class);
                startActivity(authenticateMemberIntent);
                finish();
                break;

            case R.id.btReceiveKerosene:
                Intent receiveKerosene = new Intent(TgDashBoardActivity.this, TgReceiveKeroseneGoodsActivity.class);
                startActivity(receiveKerosene);
                finish();
                break;
            case R.id.btPortability:
                Intent intentPortabiity = new Intent(TgDashBoardActivity.this, TgPortabilityRequestActivity.class);
                startActivity(intentPortabiity);
                finish();
                break;

            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.btnBack:
                onBackPressed();
                break;



        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TgDashBoardActivity.this);
        builder.setMessage(R.string.log)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TgDashBoardActivity.this.finish();
                        new GetLogout().execute();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }
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
            Intent i = new Intent(TgDashBoardActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

}
