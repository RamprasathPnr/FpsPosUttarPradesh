package com.omneagate.activity.SMSActivation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.OTPTransactionDto;
import com.omneagate.DTO.OtpRegenerateDto;
import com.omneagate.DTO.QROTPResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.TransactionController.*;
import com.omneagate.TransactionController.CardRegistration;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.BaseActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.SaleActivity;
import com.omneagate.service.HttpClientWrapper;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

//FPS user can view the summary of selection
public class CardRegistrationActivity extends BaseActivity implements SMSForCardListener {

    //Progressbar for waiting
    GlobalAppState appState;

    NetworkConnection networkConnection;

    ListView listView;

    CustomProgressDialog progressBar;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    //HttpConnection service
    HttpClientWrapper httpConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_card_registration);
        appState = (GlobalAppState) getApplication();
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        listView = (ListView) findViewById(R.id.listView);
        final List<BenefActivNewDto> benefActivNewDtos = FPSDBHelper.getInstance(this).allBeneficiaryDetails();
        benefActivNewDtos.addAll(FPSDBHelper.getInstance(this).allBeneficiaryDetailsPending());
        listView.setAdapter(new CardRegistrationAdapter(this, benefActivNewDtos));
       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new OTPDialog(CardRegistrationActivity.this, benefActivNewDtos.get(position)).show();
            }
        });*/
        if (benefActivNewDtos.size() == 0) {
            listView.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.reg_norecord)).setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    //Orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.messageBar(this, GlobalAppState.language);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        switch (what) {
            case CARD_OTP:
                responseOtp(message);
                break;
            case CARD_OTP_REGENERATE:
                otpRegenerateRequest(message);
                break;
            default:
                break;
        }
    }

    @Override
    public void smsCardReceived(BenefActivNewDto benefActivNewDto) {
        stopTimerTask(benefActivNewDto);
    }
    private void otpRegenerateRequest(Bundle message){
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("Response Message",response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            OtpRegenerateDto benefActivNewDto = gson.fromJson(response, OtpRegenerateDto.class);
            if (benefActivNewDto.getStatusCode() == 0) {
                Util.messageBar(this, "OTP generated and messaged to RMN");
            } else {
                Util.messageBar(this, FPSDBHelper.getInstance(this).retrieveLanguageTable(benefActivNewDto.getStatusCode(), GlobalAppState.language).getDescription());
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }

    }
    private void responseOtp(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BenefActivNewDto benefActivNewDto = gson.fromJson(response, BenefActivNewDto.class);
            if (benefActivNewDto.getStatusCode() == 0) {
                Intent intent = new Intent(this, BeneficiarySubmissionActivity.class);
                intent.putExtra("response", response);
                startActivity(intent);
                finish();
               /* FPSDBHelper.getInstance(this).updateRegistration(updateStock.getRationCardNumber(), "S", "Success");
                validateCard(updateStock.getRationCardNumber());*/
            } else {
                Util.messageBar(this, FPSDBHelper.getInstance(this).retrieveLanguageTable(benefActivNewDto.getStatusCode(), GlobalAppState.language).getDescription());
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    public void activateData(BenefActivNewDto beneficiary) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String response = gson.toJson(beneficiary);
        Intent intent = new Intent(this, BeneficiarySubmissionActivity.class);
        intent.putExtra("response", response);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }

    public void submitOtp(BenefActivNewDto benefActivNewDto, String otp) {
        try {
            String cardNumber = benefActivNewDto.getRationCardNumber().toUpperCase().trim();
            String rmn = benefActivNewDto.getMobileNum().trim();
            BenefActivNewDto benefActivNew = new BenefActivNewDto();
            benefActivNew.setOtp(otp);
            benefActivNew.setMobileNum(rmn);
            benefActivNew.setRationCardNumber(cardNumber);
            benefActivNew.setDeviceNum(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            benefActivNew.setOtpEntryTime(dateFormat.format(new Date()));
            TransactionBaseDto transaction = new TransactionBaseDto();
            transaction.setTransactionType(TransactionTypes.BENEFICIARY_VALIDATION_NEW);
            transaction.setType("com.omneagate.rest.dto.BenefActivNewDto");
            transaction.setBaseDto(benefActivNew);
            if(NetworkUtil.getConnectivityStatus(this) == 0|| SessionId.getInstance().getSessionId().length()<=0){
                GlobalAppState.smsListener = this;
                checkMessage();
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                CardRegistration trans = CardRegistrationFactory.getTransaction(0);
                trans.process(this, transaction, benefActivNew);
            }else {
                String url = "/transaction/process";
                Log.w("TEst",transaction.toString());
                String login = new Gson().toJson(transaction);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_OTP,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Log.e("error", e.toString(), e);
        }
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
    public void submitOtpRegenerationRequest(BenefActivNewDto benefActivNewDto) {
        try {
            String cardNumber = benefActivNewDto.getRationCardNumber().toUpperCase().trim();
            String rmn = benefActivNewDto.getMobileNum().trim();
            OtpRegenerateDto benefActivNew = new OtpRegenerateDto();
            benefActivNew.setMobileNumber(rmn);
            benefActivNew.setRationCardNumber(cardNumber);
            benefActivNew.setDeviceNumber(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            benefActivNew.setTransactionType(TransactionTypes.BENEFICIARY_REGREQUEST_NEW);
            if(NetworkUtil.getConnectivityStatus(this) == 0|| SessionId.getInstance().getSessionId().length()<=0){
                UpdateStockRequestDto update = new UpdateStockRequestDto();
                update.setRmn(rmn);
                update.setUfc("");
//                GlobalAppState.listener = this;
                /*checkMessage();*/
               /* progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();*/
                CardRegistration trans = CardRegistrationFactory.getTransaction(0);
//                trans.process(this, transaction, benefActivNew);
            }else {
                String url = "/otp/regenerate";
                String login = new Gson().toJson(benefActivNew);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_OTP_REGENERATE,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Log.e("error", e.toString(), e);
        }
    }
}
