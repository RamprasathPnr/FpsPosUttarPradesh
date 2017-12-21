package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSRationCardDetails;
import com.omneagate.DTO.GeneralResponse;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.MobileOTPDialog;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TgMobileNumberUpdateActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageViewBack;
    private Button bt_sendOTP,btnBack;
    private RationCardDetailDialog rationCardDetailDialog;
    private EditText edtAadhaarNumber, edtMobileNumber;
    private final String TAG = TgMobileNumberUpdateActivity.class.getCanonicalName();
    private String strMobileNum, strAadhaarNum;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_mobile_number_update);
        initView();
    }


    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        Util.setTamilText((TextView) findViewById(R.id.top_textView), getString(R.string.mobile_number_update));
        bt_sendOTP = (Button) findViewById(R.id.bt_sendOTP);
        bt_sendOTP.setOnClickListener(this);

        edtAadhaarNumber = (EditText) findViewById(R.id.edtAadhaarNumber);
        edtMobileNumber = (EditText) findViewById(R.id.edtMobileNumber);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
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

            case R.id.transaction:
                break;
            case R.id.bt_sendOTP:

                if (edtAadhaarNumber.getText().toString().trim().isEmpty()) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.enter_aadhaar, Toast.LENGTH_SHORT).show();
                    return;
                } else if (edtAadhaarNumber.getText().toString().trim().length() < 12) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.enter_crt_aadhaar, Toast.LENGTH_SHORT).show();
                    return;
                } else if (edtMobileNumber.getText().toString().trim().isEmpty()) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.pl_mobile, Toast.LENGTH_SHORT).show();
                    return;
                } else if (edtMobileNumber.getText().toString().length() < 10) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.crt_mobil_number, Toast.LENGTH_SHORT).show();
                    return;
                } else if (edtMobileNumber.getText().toString().startsWith("1")) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.crt_mobil_number, Toast.LENGTH_SHORT).show();
                    return;
                } else if (edtMobileNumber.getText().toString().startsWith("2")) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.crt_mobil_number, Toast.LENGTH_SHORT).show();
                    return;
                } else if (edtMobileNumber.getText().toString().startsWith("3")) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.crt_mobil_number, Toast.LENGTH_SHORT).show();
                    return;
                } else if (edtMobileNumber.getText().toString().startsWith("4")) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.crt_mobil_number, Toast.LENGTH_SHORT).show();
                    return;
                }else if (edtMobileNumber.getText().toString().startsWith("5")) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.crt_mobil_number, Toast.LENGTH_SHORT).show();
                    return;
                }else if (edtMobileNumber.getText().toString().startsWith("6")) {
                    Toast.makeText(TgMobileNumberUpdateActivity.this, R.string.crt_mobil_number, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    SendOTP();
                }


                break;
            default:
                break;
        }

    }

    public void SendOTP() {
        strMobileNum = edtMobileNumber.getText().toString();
        strAadhaarNum = edtAadhaarNumber.getText().toString();
        edtAadhaarNumber.getText().toString();
        new SendMobileOTP().execute();

    }

    class SendMobileOTP extends AsyncTask<String, GeneralResponse, GeneralResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgMobileNumberUpdateActivity.this, getResources().getString(R.string.sending_otp));
            rationCardDetailDialog.show();
        }

        protected GeneralResponse doInBackground(String... arg0) {
            return sendMobileOTP();
        }

        @Override
        protected void onPostExecute(GeneralResponse response) {
            super.onPostExecute(response);
            rationCardDetailDialog.dismiss();
            if (response != null) {
                if (response.getRespMsgCode() != null && response.getRespMsgCode().contains("0")) {
                    MobileOTPDialog mobileOTPDialog = new MobileOTPDialog(TgMobileNumberUpdateActivity.this, strMobileNum, strAadhaarNum);
                    mobileOTPDialog.show();
                }
            }

        }
    }

    private GeneralResponse sendMobileOTP() {
        try {
            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            inputMap.put("mobileNo", edtMobileNumber.getText().toString());
            inputMap.put("custmerUid", edtAadhaarNumber.getText().toString());
            Log.e(TAG, "<==== Transaction ID ====>" + LoginData.getInstance().getTransactionId());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", XMLUtil.PASSWORD);

            GeneralResponse generalResponse = XMLUtil.mobileOTP(inputMap);

            Log.e(TAG, "<===== Mobile OTP Response =====> " + generalResponse);


            return generalResponse;

        } catch (final FPSException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgMobileNumberUpdateActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgMobileNumberUpdateActivity.this, TgDashBoardActivity.class);
        startActivity(backIntent);
        finish();
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
            Intent i = new Intent(TgMobileNumberUpdateActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

}
