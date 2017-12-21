package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.TransactionController.CardRegistration;
import com.omneagate.TransactionController.CardRegistrationFactory;
import com.omneagate.TransactionController.SMSForCardListener;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class BeneficiaryCardActivationOTPActivity extends BaseActivity implements SMSForCardListener, View.OnClickListener {

    final Handler handler = new Handler();
    //Progressbar for waiting
    CustomProgressDialog progressBar;
    //HttpConnection service
    HttpClientWrapper httpConnection;
    Timer timer;
    String number = "";
    TimerTask timerTask;
    BenefActivNewDto beneficiary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_mobile_otp_activation);
        httpConnection = new HttpClientWrapper();
        String response = getIntent().getExtras().getString("response");
        registrationSubmissionResponse(response);
        Util.LoggingQueue(this, "Mobile OTP Reg", "Setting up main page");
        setUpCardPage();
    }

    private void registrationSubmissionResponse(String message) {
        try {
            if (message != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                Util.LoggingQueue(this, "Mobile OTP Reg", "Response:" + message);
                beneficiary = gson.fromJson(message, BenefActivNewDto.class);
            }
        } catch (Exception e) {
            Log.e("CardActivationOTP", e.toString(), e);
        }
    }


    /*
   * Set border for the layout
   *
   * */
    private void setBackGroundForLayout() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#ffffff")); // Changes this drawbale to use a single color instead of a gradient
        gd.setStroke(2, Color.parseColor("#c77000"));
        RelativeLayout tv = (RelativeLayout) findViewById(R.id.layout_otp);
        tv.setBackground(gd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_one:
                addNumber("1");
                break;
            case R.id.button_two:
                addNumber("2");
                break;
            case R.id.button_three:
                addNumber("3");
                break;
            case R.id.button_four:
                addNumber("4");
                break;
            case R.id.button_five:
                addNumber("5");
                break;
            case R.id.button_six:
                addNumber("6");
                break;
            case R.id.button_seven:
                addNumber("7");
                break;
            case R.id.button_eight:
                addNumber("8");
                break;
            case R.id.button_nine:
                addNumber("9");
                break;
            case R.id.button_zero:
                addNumber("0");
                break;

            case R.id.button_bkSp:
                removeNumber();
                break;
            case R.id.imageView5:
                number = "";
                setText();
                break;
            default:
                break;
        }

    }

    private void removeNumber() {
        if (number.length() > 0) {
            number = number.substring(0, number.length() - 1);
        } else {
            number = "";
        }
        setText();
    }


    private void addNumber(String text) {
        try {
            if (number.length() >= 7) {
                return;
            }

            number = number + text;
            setText();
        } catch (Exception e) {
            Log.e("CardActivationOTP", e.toString(), e);
        }
    }

    private void setText() {
        ((TextView) findViewById(R.id.mobileNumberOTP)).setTextColor(Color.parseColor("#c77000"));
        ((TextView) findViewById(R.id.mobileNumberOTP)).setText(number);
    }

    private void setUpCardPage() {
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpPopUpPage();
        appState = (com.omneagate.activity.GlobalAppState) getApplication();
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.card_activation));
        LinearLayout mobileOtp = (LinearLayout) findViewById(R.id.myMobileOTPBackground);
        mobileOtp.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = vi.inflate(R.layout.view_received_otp, null);
        mobileOtp.addView(view);
        setBackGroundForLayout();
        Util.LoggingQueue(this, "Mobile OTP Reg", "Otp for reg request");
        findViewById(R.id.button_one).setOnClickListener(this);
        findViewById(R.id.button_two).setOnClickListener(this);
        findViewById(R.id.button_three).setOnClickListener(this);
        findViewById(R.id.button_four).setOnClickListener(this);
        findViewById(R.id.button_five).setOnClickListener(this);
        findViewById(R.id.button_six).setOnClickListener(this);
        findViewById(R.id.button_seven).setOnClickListener(this);
        findViewById(R.id.button_eight).setOnClickListener(this);
        findViewById(R.id.button_nine).setOnClickListener(this);
        findViewById(R.id.button_zero).setOnClickListener(this);
        findViewById(R.id.button_bkSp).setOnClickListener(this);
        findViewById(R.id.imageView5).setOnClickListener(this);
        ((TextView) findViewById(R.id.enterOtp)).setTextColor(Color.parseColor("#c77000"));
        findViewById(R.id.buttonNeedOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOtp();
            }
        });
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getOtp() {
        if (StringUtils.isEmpty(number)) {
            Util.messageBar(this, getString(R.string.enter_otp));
            return;
        }

        if (number.length() != 7) {
            Util.messageBar(this, getString(R.string.invalid_otp));
            return;
        }
        submitOtp();
    }


    @Override
    public void smsCardReceived(BenefActivNewDto benefActivNewDto) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        stopTimerTask(benefActivNewDto);
    }

    private void checkMessage() {
        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 75000);
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        stopTimerTask(new BenefActivNewDto());
                    }
                });
            }
        };
    }

    public void stopTimerTask(BenefActivNewDto qrResponse) {

        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        com.omneagate.activity.GlobalAppState.listener = null;
        activateData(qrResponse);
    }

    public void activateData(BenefActivNewDto qrResponse) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        sendNextPage(qrResponse);
    }


    private void submitOtp() {
        try {
            BenefActivNewDto benefActivNew = new BenefActivNewDto();
            benefActivNew.setOtp(number);
            benefActivNew.setMobileNum(beneficiary.getMobileNum());
            benefActivNew.setTransactionId(beneficiary.getTransactionId());
            benefActivNew.setRationCardNumber(beneficiary.getRationCardNumber().toUpperCase());
            benefActivNew.setDeviceNum(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            benefActivNew.setOtpEntryTime(dateFormat.format(new Date()));
            TransactionBaseDto transaction = new TransactionBaseDto();
            transaction.setTransactionType(TransactionTypes.BENEFICIARY_VALIDATION_NEW);
            transaction.setType("com.omneagate.rest.dto.BenefActivNewDto");
            transaction.setBaseDto(benefActivNew);
            Log.i("Req", benefActivNew.toString());
            if (NetworkUtil.getConnectivityStatus(this) == 0 || SessionId.getInstance().getSessionId().length() <= 0) {
                UpdateStockRequestDto update = new UpdateStockRequestDto();
                update.setRmn(beneficiary.getMobileNum());
                update.setUfc("");
                if (com.omneagate.activity.GlobalAppState.smsAvailable) {
                    GlobalAppState.smsListener = this;
                    checkMessage();
                    progressBar = new CustomProgressDialog(this);
                    progressBar.setCancelable(false);
                    progressBar.show();
                    CardRegistration trans = CardRegistrationFactory.getTransaction(0);
                    trans.process(this, transaction, benefActivNew);
                } else {
                    Util.messageBar(this, getString(R.string.no_connectivity));
                }
            } else {
                String url = "/transaction/process";
                String otpCheckRequest = new Gson().toJson(transaction);
                Util.LoggingQueue(this, "Mobile OTP Reg", "Otp reg request:" + otpCheckRequest);
                StringEntity se = new StringEntity(otpCheckRequest, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_OTP,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Log.e("CardActivationOTP", e.toString(), e);
            Util.LoggingQueue(this, "Mobile OTP Reg", "Sending req error:" + e.getStackTrace().toString());
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CardActivationActivity.class));
        Util.LoggingQueue(this, "Mobile OTP Reg", "Back pressed");
        finish();
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}

        switch (what) {
            case CARD_OTP:
                responseOtp(message);
                break;
            default:
                break;
        }

    }

    private void responseOtp(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Util.LoggingQueue(this, "Mobile OTP Reg", "Response for otp:" + response);
            BenefActivNewDto benefActivNewDto = gson.fromJson(response, BenefActivNewDto.class);
            sendNextPage(benefActivNewDto);
        } catch (Exception e) {
            Log.e("CardActivationOTP", e.toString(), e);
            Util.LoggingQueue(this, "Mobile OTP Reg", "Error in parsing:" + e.getStackTrace().toString());
        }
    }

    private void sendNextPage(BenefActivNewDto benefActivNewDto) {
        if (benefActivNewDto.getStatusCode() == 0) {
            String response = new Gson().toJson(benefActivNewDto);
            /*Intent intent = new Intent(this, BeneficiarySubmissionActivity.class);
            intent.putExtra("response", response);
            startActivity(intent);*/
            finish();
        } else {
            String messages = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(benefActivNewDto.getStatusCode()));
            Util.messageBar(this, messages);
            if (StringUtils.isEmpty(messages))
                messages = getString(R.string.invalid_otp);
            Util.LoggingQueue(this, "Mobile OTP Reg", "Error in resp:" + messages);
            errorNavigation(messages);
        }

    }

    /*
      *
      * Error navigation  pages
      * */
    private void errorNavigation(String messages) {
        if (StringUtils.isEmpty(messages)) {
            messages = getString(R.string.card_activation_failed);
        }
        Intent intent = new Intent(this, SuccessFailureActivationActivity.class);
        Util.LoggingQueue(this, "Mobile OTP Reg", "Activation failed:" + messages);
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
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
