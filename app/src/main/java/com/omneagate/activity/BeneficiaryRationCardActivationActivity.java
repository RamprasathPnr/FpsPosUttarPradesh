package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BaseDto;
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
//import com.omneagate.Util.DownloadDataProcessor;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.Timer;
import java.util.TimerTask;

public class BeneficiaryRationCardActivationActivity extends BaseActivity implements SMSForCardListener {

    final Handler handler = new Handler();
    //Progressbar for waiting
    CustomProgressDialog progressBar;
    //HttpConnection service
    HttpClientWrapper httpConnection;
    Timer timer;
    TimerTask timerTask;
    EditText prefixCard, cardTypeCard, suffixCard, registeredMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.smsactivity_beneficiary_activation);
        httpConnection = new HttpClientWrapper();
        setUpCardPage();
    }


    private void setUpCardPage() {
        setUpPopUpPage();
        Util.LoggingQueue(this, "Ration Card Registration", "Setting up main page");
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        appState = (GlobalAppState) getApplication();
        ((TextView) findViewById(R.id.user_username)).setText(getString(R.string.user_name) + SessionId.getInstance().getUserName().toUpperCase());
        ((TextView) findViewById(R.id.user_fps_store)).setText(getString(R.string.fps_code) + SessionId.getInstance().getFpsCode());
        Util.setTamilText((TextView) findViewById(R.id.textViewTop), R.string.card_registration);
        Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.submit_button), R.string.submit);
        prefixCard = (EditText) findViewById(R.id.firstText);
        cardTypeCard = (EditText) findViewById(R.id.secondText);
        suffixCard = (EditText) findViewById(R.id.thirdText);
        registeredMobile = (EditText) findViewById(R.id.mobileNumberActivation);
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.omneagate.activity.BeneficiaryRationCardActivationActivity.this, CardActivationActivity.class));
                finish();
            }
        });
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCardNumber();
            }
        });
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        prefixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (prefixCard.getText().toString().length() == 2)     //size as per your requirement
                {
                    cardTypeCard.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cardTypeCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cardTypeCard.getText().toString().length() == 1)     //size as per your requirement
                {
                    suffixCard.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        suffixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (suffixCard.getText().toString().length() == 7)     //size as per your requirement
                {
                    registeredMobile.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void getCardNumber() {

        String cardNumber1 = prefixCard.getText().toString();
        String cardNumber2 = cardTypeCard.getText().toString();
        String cardNumber3 = suffixCard.getText().toString();
        String mobileNumber = registeredMobile.getText().toString();
        if (StringUtils.isEmpty(cardNumber1) || StringUtils.isEmpty(cardNumber2) || StringUtils.isEmpty(cardNumber3)) {
            Util.messageBar(this, getString(R.string.invalid_card_no));
            return;
        }

        if (cardNumber1.length() != 2 || cardNumber2.length() != 1 || cardNumber3.length() != 7) {
            Util.messageBar(this, getString(R.string.invalid_card_no));
            return;
        }
        if (StringUtils.isNotEmpty(mobileNumber)) {
            if (mobileNumber.length() != 10) {
                Util.messageBar(this, getString(R.string.invalidMobile));
                return;
            }
        }
        String cardNumber = cardNumber1 + cardNumber2 + cardNumber3;
        Util.LoggingQueue(this, "Ration Card Registration", "Card no:" + cardNumber + "::mobile no:" + mobileNumber);
        submitCard(cardNumber, mobileNumber);
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
        GlobalAppState.listener = null;
        activateData(qrResponse);
    }

    public void activateData(BenefActivNewDto qrResponse) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        enterOtpPage(qrResponse);
    }

    private void enterOtpPage(BenefActivNewDto qrResponse) {
        try {
            String response = new Gson().toJson(qrResponse);
            Intent intent = new Intent(this, BeneficiaryCardActivationOTPActivity.class);
            Util.LoggingQueue(this, "Ration Card Registration", "Navigating to otp page");
            intent.putExtra("response", response);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("CardActivation", e.toString(), e);
            Util.LoggingQueue(this, "Ration Card Registration", "Error otp navigation:" + e.getStackTrace().toString());
        }
    }


    private void submitCard(String cardNumber, String mobileNumber) {
        try {
            BenefActivNewDto benefActivNew = new BenefActivNewDto();
            benefActivNew.setMobileNum(mobileNumber);
            benefActivNew.setRationCardNumber(cardNumber);
            benefActivNew.setDeviceNum(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            TransactionBaseDto transaction = new TransactionBaseDto();
            transaction.setTransactionType(TransactionTypes.CARDNUMBER_BASED_ACTIVATION);
            transaction.setType("com.omneagate.rest.dto.BenefActivNewDto");
            transaction.setBaseDto(benefActivNew);
            if (NetworkUtil.getConnectivityStatus(this) == 0 || SessionId.getInstance().getSessionId().length() <= 0) {
                UpdateStockRequestDto update = new UpdateStockRequestDto();
                update.setRmn(mobileNumber);
                update.setUfc("");
                if (GlobalAppState.smsAvailable) {
                    GlobalAppState.smsListener = this;
                    checkMessage();
                    progressBar = new CustomProgressDialog(this);
                    progressBar.setCancelable(false);
                    progressBar.show();
                    CardRegistration trans = CardRegistrationFactory.getTransaction(0);
                    //  trans.process(this, transaction, benefActivNew);
                } else {
                    Util.messageBar(this, getString(R.string.no_connectivity));
                }
            } else {
                String url = "/transaction/process";
                String beneRegReq = new Gson().toJson(transaction);
                StringEntity se = new StringEntity(beneRegReq, HTTP.UTF_8);
                Util.LoggingQueue(this, "Ration Card Registration", "Bene Reg Req" + beneRegReq);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_REGISTRATION,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Log.e("CardActivation", e.toString(), e);
            Util.LoggingQueue(this, "Ration Card Registration", "Error:" + e.getStackTrace().toString());
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CardActivationActivity.class));
        Util.LoggingQueue(this, "Ration Card Registration", "On back pressed");
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
            case CARD_REGISTRATION:
                registrationSubmissionResponse(message);
                break;
            default:
                break;
        }

    }


    private void registrationSubmissionResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Util.LoggingQueue(this, "Ration Card Registration", "Activation resp:" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto base = gson.fromJson(response, BaseDto.class);
            String messageData = "";
            Log.i("Response", response);
            if (base.getStatusCode() == 0) {
                messageData = getString(R.string.card_activation_success);
                Util.LoggingQueue(this, "Ration Card Registration", "Sync process called");
              /*  DownloadDataProcessor downloads = new DownloadDataProcessor(this);
                downloads.processor();*/
            } else {
                messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(base.getStatusCode()));
                if (StringUtils.isEmpty(messageData))
                    messageData = getString(R.string.card_activation_failed);
                Util.LoggingQueue(this, "Ration Card Registration", "Error in activation:" + messageData);
            }
            errorNavigation(messageData);
        } catch (Exception e) {
            Log.e("CardActivation", e.toString(), e);
            Util.LoggingQueue(this, "Ration Card Registration", "Error:" + e.getStackTrace().toString());
            errorNavigation(getString(R.string.internalError));
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
