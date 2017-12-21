package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.MenuDataDto;
import com.omneagate.DTO.ValidateOtpDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.ChangeUrlDialog;
import com.omneagate.activity.dialog.LanguageSelectionDialog;
import com.omneagate.activity.dialog.MenuAdapter;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

public class InspectionOTPActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText mEtOtpValue;
    private Button mBVerify;
    private TextView mTvTime;
    private TextView mTvResend;
    private ListPopupWindow popupWindow;
    ValidateOtpDto validateOtpDto;
    private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    private final long startTime = 180 * 1000;
    private final long interval = 1 * 1000;
    String TAG = "InspectionOTPActivity";
    LoginResponseDto loginResponse;
    LoginDto loginCredentials;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
        switch (what) {
            case LOGIN_USER:
                userOtpResponse(message);
                break;
            case RESEND_OTP:
                userResendOTPResponse(message);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_otp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        findView();
        loadViewData();
    }

    private void findView() {
        mEtOtpValue = (EditText) findViewById(R.id.officer_otp);
        mBVerify = (Button) findViewById(R.id.officer_verify_Button);
        mTvTime = (TextView) findViewById(R.id.txt_time);
        mTvResend = (TextView) findViewById(R.id.resendOtp);
        mTvResend.setOnClickListener(null);
    }

    private void loadViewData() {
        countDownTimer = new MyCountDownTimer(startTime, interval);
//        mTvTime.setText(mTvTime.getText() + String.valueOf(startTime / 1000)+" "+R.string.seconds_remaining);
        countDownTimer.start();
        timerHasStarted = true;
        String loginCredentialsStr = getIntent().getStringExtra("LoginCredentials");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        loginCredentials = gson.fromJson(loginCredentialsStr, LoginDto.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTamilHeader((TextView) findViewById(R.id.login_actionbar), R.string.headerAllPageEnglish);
        setTamil(((TextView) findViewById(R.id.login_actionbarTamil)), R.string.headerAllPage);
        Util.setTamilText(((TextView) findViewById(R.id.appName)), getString(R.string.fpsposapplication));
    }

    public void officerOTP(View v) {
        try {
            /*Keyboard disappearance*/
            /*InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(v.getWindowToken(), 0);*/
          /*  startService(new Intent(this, OfflineReportService.class));
            startService(new Intent(this, OfflineCriteriaService.class));
            startService(new Intent(this, UpdateDataService.class));
            Intent intent = new Intent(InspectionOTPActivity.this, InspectionDashboardActivity.class);
            startActivity(intent);
            finish();*/
            ValidateOtpDto validateOtpDto = getOTPValue();
            if (validateOtpDto == null) {
                return;
            }
            authenticateOTP(validateOtpDto);
        } catch (Exception e) {
            Log.e("LoginActivity", "officerOTP exc..." + e);
        }
    }

    public void resendOfficerOTP() {
        try {
//            loginCredentials = getUsernamePassword();
            if (loginCredentials == null) {
                return;
            }
            Log.e(TAG, "resendOfficerOTP loginCredentials..." + loginCredentials);
            authenticateOfficer(loginCredentials);
            /*startActivity(new Intent(InspectionLoginActivity.this, InspectionOTPActivity.class));
            finish();*/
        } catch (Exception e) {
            Log.e(TAG, "officerLogin exc..." + e);
        }
    }

    private void authenticateOfficer(LoginDto loginCredentials) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                loginCredentials.setDeviceId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                String url = "/login/inspection/resend/otp";
                Log.e(TAG, "resend loginCredentials...." + loginCredentials);
                String login = new Gson().toJson(loginCredentials);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.RESEND_OTP, SyncHandler, RequestType.POST, se, this);
            } else {
                Toast.makeText(InspectionOTPActivity.this, R.string.noNetworkConnection, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            dismissProgress();
            Log.e(TAG, "authenticateOfficer exc..." + e);
        }
    }

    private void userResendOTPResponse(Bundle message) {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("resend otp  Resp", response);
            if (response.contains("Unauthorized")) {
                Toast.makeText(InspectionOTPActivity.this, "Your Session is closed", Toast.LENGTH_SHORT).show();
                /*startActivity(new Intent(this,LoginActivity.class));
                finish();*/
            } else {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                loginResponse = gson.fromJson(response, LoginResponseDto.class);
                int statusCode = loginResponse.getStatusCode();
                String messageData = "";
                if (statusCode == 0) {
                    Toast.makeText(InspectionOTPActivity.this, getString(R.string.otpMessage), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(statusCode));
                        if (StringUtils.isEmpty(messageData))
                            messageData = getString(R.string.internalError);
                    } catch (Exception e) {
                        messageData = getString(R.string.internalError);
                    }
                    Toast.makeText(InspectionOTPActivity.this, messageData, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    /**
     * Menu creation
     * Used to change language
     * *
     */
    public void showPopupMenu(View v) {
        List<MenuDataDto> menuDto = new ArrayList<>();
        menuDto.add(new MenuDataDto("Main Login", R.drawable.icon_main_login, "मुख्य लॉग इन"));
        popupWindow = new ListPopupWindow(this);
        ListAdapter adapter = new MenuAdapter(this, menuDto); // The view ids to map the data to
        popupWindow.setAnchorView(v);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(400); // note: don't use pixels, use a dimen resource
        popupWindow.setOnItemClickListener(this); // the callback for when a list item is selected
        popupWindow.show();
        Util.LoggingQueue(this, "Login Entry", "Inside Popup Window");
    }

    @Override
    public void onBackPressed() {
    }

    private ValidateOtpDto getOTPValue() {
        ValidateOtpDto validateOtpDto = new ValidateOtpDto();
        String otpValue = mEtOtpValue.getText().toString();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
//        long fpsId = SessionId.getInstance().getFpsId();
        String userName = loginCredentials.getUserName();
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
        if (StringUtils.isEmpty(otpValue)) {
            Util.messageBar(this, getString(R.string.otp_empty));
            return null;
        } else {
            validateOtpDto.setOtp(otpValue);
            validateOtpDto.setUserName(userName);
            validateOtpDto.setDeviceId(deviceId);
//            validateOtpDto.setFpsId(fpsId);
            return validateOtpDto;
        }
    }

    private void authenticateOTP(ValidateOtpDto validateOtpDto) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/login/validate/inspectionotp";
                String login = new Gson().toJson(validateOtpDto);
                Log.e(TAG, "validateOtpDto..." + login);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGIN_USER, SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            dismissProgress();
            Util.LoggingQueue(this, "authenticateOTP Request Error", e.toString());
        }
    }

    private void userOtpResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("Report Resp", response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            validateOtpDto = gson.fromJson(response, ValidateOtpDto.class);
            int statusCode = validateOtpDto.getStatusCode();
            String messageData = "";
            if (statusCode == 0) {
//                startService(new Intent(this, OfflineReportService.class));
//                startService(new Intent(this, OfflineReportAckService.class));
//                startService(new Intent(this, OfflineCriteriaService.class));
//                startService(new Intent(this, UpdateDataService.class));
//                    String userName = getIntent().getStringExtra("UserName");
                Intent intent = new Intent(InspectionOTPActivity.this, InspectionDashboardActivity.class);
                startActivity(intent);
                finish();
            } else {
                try {
                    messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(statusCode));
                    if (StringUtils.isEmpty(messageData))
                        messageData = getString(R.string.internalError);
                } catch (Exception e) {
                    messageData = getString(R.string.internalError);
                }
                Toast.makeText(InspectionOTPActivity.this, messageData, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Error", "userOtpResponse exc..." + e);
        }
    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resendOtp:
                countDownTimer.start();
                timerHasStarted = true;
                mTvResend.setTextColor(getResources().getColor(R.color.lightgrey));
                mTvResend.setOnClickListener(null);
                mTvTime.setVisibility(View.VISIBLE);
                Log.e(TAG, "resendOfficerOTP loginCredentials.2.." + loginCredentials);
                resendOfficerOTP();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popupWindow.dismiss();
        switch (position) {
            case 0:
                startActivity(new Intent(InspectionOTPActivity.this, LoginActivity.class));
                SessionId.getInstance().setSessionId("");
                finish();
                break;
            case 1:
                new LanguageSelectionDialog(this).show();
                break;
            case 2:
                new ChangeUrlDialog(this).show();
                break;
        }
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
//            mTvTime.setText("Time's up!");
            mTvResend.setTextColor(getResources().getColor(R.color.cpb_blue));
            mTvResend.setOnClickListener(InspectionOTPActivity.this);
            countDownTimer.cancel();
            timerHasStarted = false;
            mTvTime.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mTvTime.setText("" + (millisUntilFinished / 1000) + " " + getString(R.string.seconds_remaining));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "on destroy called");
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }
}
