package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;

import java.util.Timer;
import java.util.TimerTask;

public class TgAuthenticateMemberActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageViewBack;
    private LinearLayout scan_finger_print_layout;
    private LinearLayout scan_iris_layout;
    private TextView fpsIdLay;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_authenticate_member);
        initView();
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
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
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime);

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            Intent i = new Intent(TgAuthenticateMemberActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }


    private void initView() {
        setPopUpPage();
        updateDateTime();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        scan_finger_print_layout = (LinearLayout) findViewById(R.id.scan_finger_print_layout);
        scan_finger_print_layout.setOnClickListener(this);

        scan_iris_layout = (LinearLayout) findViewById(R.id.scan_iris_layout);
        scan_iris_layout.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        ((TextView) findViewById(R.id.top_textView)).setText(R.string.AUTHENTICATE_MEMBER);
        fpsIdLay = (TextView) findViewById(R.id.page_header);
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
//        ((TextView)findViewById(R.id.aadhaar_card)).setText("276141753198");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imageViewBack:
                onBackPressed();
                break;

            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.scan_finger_print_layout:
                String Uid = ((TextView) findViewById(R.id.aadhaar_card)).getText().toString().trim();
                Log.e("UID FOR MEMBER *****:  ", Uid);
                if (Uid.isEmpty()) {

                    Toast.makeText(TgAuthenticateMemberActivity.this, R.string.enterAadharNo, Toast.LENGTH_SHORT).show();
                } else {
                    Intent scanIntent = new Intent(TgAuthenticateMemberActivity.this, TgAuthenticateMemberFingerScanActivity.class);
                    scanIntent.putExtra("UID", Uid);
                    startActivity(scanIntent);
                    finish();
                }

                break;
            case R.id.scan_iris_layout:
                String Uid_ = ((TextView) findViewById(R.id.aadhaar_card)).getText().toString().trim();
                if (Uid_.isEmpty()) {
                    Toast.makeText(TgAuthenticateMemberActivity.this, R.string.enterAadharNo, Toast.LENGTH_SHORT).show();
                }if (Uid_.length() < 12) {
                Toast.makeText(TgAuthenticateMemberActivity.this, R.string.enterValidNo, Toast.LENGTH_SHORT).show();
            }

                else {
                    Intent irisIntent = new Intent(TgAuthenticateMemberActivity.this, TgAuthenticateIrisScanActivity.class);
                    irisIntent.putExtra("UID", Uid_);
                    startActivity(irisIntent);
                    finish();
                }
                Log.e("UID MEMBER IRIS*****:  ", Uid_);

                break;
            default:
                break;

        }
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgAuthenticateMemberActivity.this, TgDashBoardActivity.class);
        startActivity(backIntent);
        finish();
    }
}
