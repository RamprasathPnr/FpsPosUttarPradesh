package com.omneagate.activity.SMSActivation;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.TransactionController.*;
import com.omneagate.TransactionController.CardRegistration;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.BaseActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.SaleActivity;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BeneficiaryCardActivationActivity extends BaseActivity {

    //Progressbar for waiting
    CustomProgressDialog progressBar;

    //HttpConnection service
    HttpClientWrapper httpConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smsactivity_beneficiary_activation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        httpConnection = new HttpClientWrapper();
        ((LinearLayout) findViewById(R.id.otpAndCard)).setVisibility(View.VISIBLE);
        setUpCardPage();
    }


    private void setUpCardPage() {
        ((LinearLayout) findViewById(R.id.otpLayout)).setVisibility(View.GONE);
        ((Button) findViewById(R.id.otpSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                getCardNumber();
            }
        });
        ((Button) findViewById(R.id.otpCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BeneficiaryCardActivationActivity.this, SaleActivity.class));
                finish();
            }
        });

    }

    private void getCardNumber() {
        String cardNumber = ((EditText) findViewById(R.id.editTextCard)).getText().toString();
        cardNumber = cardNumber.replaceAll("[^a-zA-Z0-9]", "");
        if (validCard(cardNumber)) {
            validateCard(cardNumber);
        } else {
            Util.messageBar(this, "Invalid Card Number");
        }
    }

    private void validateCard(String cardNumber) {
        BenefActivNewDto beneficiary = FPSDBHelper.getInstance(this).getCardDetails(cardNumber.toUpperCase());
        if (StringUtils.isNotEmpty(beneficiary.getRationCardNumber())) {
            enterOtpPage();
        } else {
            enterMobilePage();
        }
    }

    private void enterOtpPage() {
        ((LinearLayout) findViewById(R.id.otpLayout)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.mobileLayout)).setVisibility(View.GONE);
        ((EditText) findViewById(R.id.editTextCard)).setEnabled(false);
        ((Button) findViewById(R.id.otpSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                submitOtp();
            }
        });
        ((Button) findViewById(R.id.otpCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BeneficiaryCardActivationActivity.this, BeneficiaryCardActivationActivity.class));
                finish();
            }
        });
    }

    private void submitOtp() {
        try {
            String cardNumber = ((EditText) findViewById(R.id.editTextCard)).getText().toString().trim();
            cardNumber = cardNumber.replaceAll("[^a-zA-Z0-9]", "");
            String rmn = ((EditText) findViewById(R.id.editTextOTP)).getText().toString().trim();
            if (StringUtils.isEmpty(rmn)) {
                Util.messageBar(this, getString(R.string.mobileOTPWrong));
                return;
            } else {
                if (rmn.length() != 7) {
                    Util.messageBar(this, getString(R.string.mobileOTPWrong));
                    return;
                }
            }
            BenefActivNewDto benefActivNew = new BenefActivNewDto();
            benefActivNew.setOtp(rmn);
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
                UpdateStockRequestDto update = new UpdateStockRequestDto();
                update.setRmn(rmn);
                update.setUfc("");
//                GlobalAppState.listener = this;
                /*checkMessage();*/
               /* progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();*/
                CardRegistration trans = CardRegistrationFactory.getTransaction(0);
                trans.process(this, transaction, benefActivNew);
            }else {
                String url = "/transaction/process";
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

    private void enterMobilePage() {
        ((LinearLayout) findViewById(R.id.otpLayout)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mobileLayout)).setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.editTextCard)).setEnabled(false);
        ((Button) findViewById(R.id.otpSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager im = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                submitCard();
            }
        });
        ((Button) findViewById(R.id.otpCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BeneficiaryCardActivationActivity.this, BeneficiaryCardActivationActivity.class));
                finish();
            }
        });

    }


    private void submitCard() {
        try {
            String cardNumber = ((EditText) findViewById(R.id.editTextCard)).getText().toString().trim();
            cardNumber = cardNumber.replaceAll("[^a-zA-Z0-9]", "");
            String rmn = ((EditText) findViewById(R.id.editTextMobile)).getText().toString().trim();
            if (StringUtils.isEmpty(rmn)) {
                Util.messageBar(this, getString(R.string.invalidMobile));
                return;
            } else {
                if (rmn.length() != 10) {
                    Util.messageBar(this, getString(R.string.invalidMobile));
                    return;
                }
            }
            if (FPSDBHelper.getInstance(this).insertRegistration(rmn, cardNumber)) {
                BenefActivNewDto benefActivNew = new BenefActivNewDto();
                benefActivNew.setMobileNum(rmn);
                benefActivNew.setRationCardNumber(cardNumber);
                benefActivNew.setDeviceNum(Settings.Secure.getString(
                        getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                TransactionBaseDto transaction = new TransactionBaseDto();
                transaction.setTransactionType(TransactionTypes.BENEFICIARY_REGREQUEST_NEW);
                transaction.setType("com.omneagate.rest.dto.BenefActivNewDto");
                transaction.setBaseDto(benefActivNew);
                if(NetworkUtil.getConnectivityStatus(this) == 0|| SessionId.getInstance().getSessionId().length()<=0){
                    UpdateStockRequestDto update = new UpdateStockRequestDto();
                    update.setRmn(rmn);
                    update.setUfc("");
//                GlobalAppState.listener = this;
                /*checkMessage();*/
               /* progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();*/
                    com.omneagate.TransactionController.CardRegistration trans = CardRegistrationFactory.getTransaction(0);
                    trans.process(this, transaction, benefActivNew);
                }else {
                    String url = "/transaction/process";
                    String login = new Gson().toJson(transaction);
                    StringEntity se = new StringEntity(login, HTTP.UTF_8);
                    progressBar = new CustomProgressDialog(this);
                    progressBar.setCancelable(false);
                    progressBar.show();
                    httpConnection.sendRequest(url, null, ServiceListenerType.CARD_REGISTRATION,
                            SyncHandler, RequestType.POST, se, this);
                }
            } else {
                Util.messageBar(this, getString(R.string.rmnExist));
            }
        } catch (Exception e) {
            Log.e("error", e.toString(), e);
        }
    }

    private boolean validCard(String message) {
        boolean valid = false;
        try {
            if (message.length() <= 15) {
                /*String message1 = message.substring(0, 2);
                String message2 = message.substring(2, 3);
                String message3 = message.substring(3);
                if (StringUtils.isNumeric(message1) && StringUtils.isNumeric(message3) && StringUtils.isAlpha(message2)) {
                    valid = true;
                } else {
                    valid = false;
                }*/
                valid = true;
            }
        } catch (Exception e) {
            valid = false;
            Log.e("Error", e.toString(), e);
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        switch (what) {
            case CARD_REGISTRATION:
                registrationSubmissionResponse(message);
                break;
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
            BenefActivNewDto benefActivNewDto = gson.fromJson(response, BenefActivNewDto.class);
            Log.e("Response", "Resp" + benefActivNewDto);
            if (benefActivNewDto.getStatusCode() == 0) {
                Intent intent = new Intent(this, BeneficiarySubmissionActivity.class);
                intent.putExtra("response", response);
                startActivity(intent);
                finish();
               /* FPSDBHelper.getInstance(this).updateRegistration(updateStock.getRationCardNumber(), "S", "Success");
                validateCard(updateStock.getRationCardNumber());*/
            } else {
                String messages = FPSDBHelper.getInstance(this).retrieveLanguageTable(benefActivNewDto.getStatusCode(), GlobalAppState.language).getDescription();

                Util.messageBar(this, messages);
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }


    private void registrationSubmissionResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            if (response != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                BenefActivNewDto benefActivNewDto = gson.fromJson(response, BenefActivNewDto.class);
                if (benefActivNewDto.getStatusCode() == 0) {
                    FPSDBHelper.getInstance(this).updateRegistration(benefActivNewDto.getRationCardNumber(), "S", "Success");
                    validateCard(benefActivNewDto.getRationCardNumber());
                } else {
                    String messages = FPSDBHelper.getInstance(this).retrieveLanguageTable(benefActivNewDto.getStatusCode(), GlobalAppState.language).getDescription();

                    Util.messageBar(this, messages);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

}