package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.AadharSeedingDto;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.LoginPasswordDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

public class OapAnpRegistrationConfirmActivity extends BaseActivity {


    BenefActivNewDto benefActivNewDto;

    String phNumberEntered = "";
    String phNumberReturnedfromServer = "";

    String OldRationNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_oap_anp_registration_confirm);
        Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "onCreate() called ");


        String message = getIntent().getStringExtra("data");
        benefActivNewDto = new Gson().fromJson(message, BenefActivNewDto.class);
        benefActivNewDto.setValueAdded(true);

        Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "onCreate()  benefActivNewDto " +
                benefActivNewDto);

        try{
            phNumberEntered = benefActivNewDto.getMobileNum();

            Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "onCreate()  phNumberEntered " +
                    phNumberEntered);

        }catch (Exception e){
            Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "phNumberEntered  Exception " +
                    e);
        }


        setInitialPage();
    }


    private void setInitialPage() {
        Util.LoggingQueue(this, "OapAnpRegistrationConfirmActivity", "Starting setInitialPage ");
        setUpPopUpPage();
//        setOapAnpText((TextView) findViewById(R.id.top_textView), R.string.oap_anp_card_registration);
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.oap_anp_card_registration_confirm));
        if(GlobalAppState.language.equalsIgnoreCase("hi")) {
            ((TextView) findViewById(R.id.top_textView)).setTextSize(30);
            ((TextView) findViewById(R.id.top_textView)).setTypeface(Typeface.DEFAULT_BOLD);
        }
        Util.setTamilText((TextView) findViewById(R.id.registration_text), R.string.check_details);
        Util.setTamilText((TextView) findViewById(R.id.rationCardNoLabel), R.string.ration_card_number);
        Util.setTamilText((TextView) findViewById(R.id.aRegisterNoLabel), R.string.aRegisterNo);
        Util.setTamilText((TextView) findViewById(R.id.mobileNoLabel), R.string.mob_number);
        Util.setTamilText((TextView) findViewById(R.id.rationCardTypeLabel), R.string.cardCap);
        Util.setTamilText((TextView) findViewById(R.id.aadharNumberLabel), R.string.aadharNo);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setUserData();
    }

    private void setUserData() {
        TextView rationCardNumber = (TextView) findViewById(R.id.rationCardNoValue);
        String cardNo = benefActivNewDto.getRationCardNumber();
        String cardNumber = StringUtils.substring(cardNo, 0, 2) + "/" + StringUtils.substring(cardNo, 2, 3) + "/" + StringUtils.substring(cardNo, 3, 10);
        rationCardNumber.setText(cardNumber);
        ((TextView) findViewById(R.id.aRegNoValue)).setText(benefActivNewDto.getAregisterNum());
        ((TextView) findViewById(R.id.mobileNoValue)).setText(benefActivNewDto.getMobileNum());
        ((TextView) findViewById(R.id.rationCardTypeValue)).setText(benefActivNewDto.getCardTypeDef());
//        if (!benefActivNewDto.isChecked()) {
        ((TextView) findViewById(R.id.aadharNumberValue)).setText(benefActivNewDto.getAadhaarSeedingDto().getUid());
//        ((TextView) findViewById(R.id.aadharNumberValue)).setText(benefActivNewDto.getAadharSeedingDto().getUid());
//        }
        Util.setTamilText((TextView) findViewById(R.id.button_edit), R.string.edit);
        Util.setTamilText((TextView) findViewById(R.id.button_cancel), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.button_Submit), R.string.submit);
        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.LoggingQueue(com.omneagate.activity.OapAnpRegistrationConfirmActivity.this, "Oap anp RegistrationConfirmActivity", "Cancell called ... Moving to card activation");
                Intent intent = new Intent(com.omneagate.activity.OapAnpRegistrationConfirmActivity.this, CardActivationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.button_Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitCard();
               /* Intent intent = new Intent(OapAnpRegistrationConfirmActivity.this, OapAnpActivationSuccessActivity.class);
                intent.putExtra("data", new Gson().toJson(benefActivNewDto));
                startActivity(intent);*/
            }
        });
        findViewById(R.id.button_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

        Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "processMessage() called message = "+message);
        Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "processMessage() called what = "+what);
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
            case LOGIN_USER:
                userLoginResponse(message);
                break;
            case ERROR_MSG:
                Util.messageBar(this, getString(R.string.connectionRefused));
                break;
            default:
//                errorNavigation("");
                break;
        }
    }

    /**
     * After login response received from server successfully in android
     *
     * @param message in bundle that received
     */
    private void userLoginResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Util.LoggingQueue(this, "Login Request response", response);
//            Log.e("LoginActivity", "Login Request response:" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            LoginResponseDto loginResponse = gson.fromJson(response, LoginResponseDto.class);
            dismissProgress();
            if (loginResponse != null) {
                if (loginResponse.isAuthenticationStatus()) {
                    SessionId.getInstance().setSessionId(loginResponse.getSessionid());
                } else {
                    Util.messageBar(this, getString(R.string.loginInvalidUserPassword));
                }
            } else {
                Util.messageBar(this, getString(R.string.serviceNotAvailable));
            }
        } catch (Exception e) {
            dismissProgress();
            Util.LoggingQueue(this, "Login Request Error", e.toString());
            Util.messageBar(this, getString(R.string.inCorrectUnamePword));
//            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void dismissProgress() {
        if (progressBar != null) {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Util.LoggingQueue(this, "RegistrationConfirmActivity", "On back pressed");
        editCard();

    }


    String phNumberStatus = "";
    private void registrationSubmissionResponse(Bundle message) {
        try {
            dismissProgress();
            Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "registrationSubmissionResponse() called ");
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto base = gson.fromJson(response, BaseDto.class);
            Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "registrationSubmissionResponse() getStatusCode = " + base.getStatusCode());

            if (base.getStatusCode() == 5077 || base.getStatusCode() == 6048) {
                    Util.messageBar(this, getString(R.string.ufc_not_available));
            } else {

                BeneficiaryDto beneficiaryDto = gson.fromJson(response, BeneficiaryDto.class);
                String messageData = "";
                Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "registrationSubmissionResponse() beneficiaryDto.getStatusCode() = " + beneficiaryDto.getStatusCode());
                Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "registrationSubmissionResponse() " +
                        "Mobile Number from server = " + beneficiaryDto.getMobileNumber());

                phNumberReturnedfromServer = beneficiaryDto.getMobileNumber();

                try {

                    if (phNumberReturnedfromServer == null) {
                        phNumberStatus =null;
                        // Null Returned in case I :  if no phone number is given to server
                        // Or
                        // Null Returned in case II : if the given phone number is alredy available in server

                        if (phNumberEntered.isEmpty() || phNumberEntered == null) {
                            // Case I
                            // if no phone number is given to server
                            phNumberStatus = "no_phNumber_given";
                        } else {
                            // Case II
                            // if the given phone number is alredy available in server
                            // Should enter this phone number in DB
                            //beneficiaryDto.setMobileNumber(null);
                            phNumberStatus = "exists";

                        }
                    } else {
                        // New phone number is added - Success
                        phNumberStatus = "success";
                    }


                } catch (Exception e) {

                }


                if (beneficiaryDto.getStatusCode() == 0 || beneficiaryDto.getStatusCode() == 5036 && !response.contains("timestamp")) {
                    Set<BeneficiaryDto> beneficiarySet = new HashSet<>();
                    beneficiarySet.add(beneficiaryDto);

                    Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "Inserting Phone Number  = " + beneficiaryDto.getMobileNumber());
                    OldRationNumber = beneficiaryDto.getOldRationNumber();


                    new InsertBeneficiaryDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, beneficiarySet);
                    //new UpdateRegistrationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, beneficiaryDto.getOldRationNumber());

                    try {
                        benefActivNewDto.getAadhaarSeedingDto().setBeneficiaryID(beneficiaryDto.getId());
                        FPSDBHelper.getInstance(this).beneficiaryMemberAadhar(benefActivNewDto.getAadhaarSeedingDto());
                    } catch (Exception e) {

                        Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "beneficiaryMemberAadhar() Exception = " + e);

                    }
                    Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "Moving to OapAnpActivationSuccessActivity");

                    Intent intent = new Intent(this, OapAnpActivationSuccessActivity.class);
                    intent.putExtra("data", new Gson().toJson(benefActivNewDto));

                    intent.putExtra("phNumberStatus", "" + phNumberStatus);


                    startActivity(intent);
                    finish();
                } else {
                    if (beneficiaryDto.getStatusCode() == 400 || beneficiaryDto.getStatusCode() == 401 || beneficiaryDto.getStatusCode() == 403 || beneficiaryDto.getStatusCode() == 5000) {
                        loginDevice(SessionId.getInstance().getLocalpasword());
                        return;
                    } else if (beneficiaryDto.getStatusCode() == 7006 || beneficiaryDto.getStatusCode() == 30001) {
                        Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", " Error code = " + beneficiaryDto.getStatusCode());

                        Util.messageBar(this, getString(R.string.aadhar_available));
                        return;
                    } else if (beneficiaryDto.getStatusCode() == 500) {
                        Util.messageBar(this, getString(R.string.connectionRefused));
                    } else if (beneficiaryDto.getStatusCode() == 12020) {
                        Util.messageBar(this, getString(R.string.oap_anp_invalid_adult_count));
                    } else if (beneficiaryDto.getStatusCode() == 12021) {
                        Util.messageBar(this, getString(R.string.oap_anp_invalid_child_count));
                    } else if (beneficiaryDto.getStatusCode() == 12022) {
                        Util.messageBar(this, getString(R.string.oap_anp_invalid_cylinder_count));
                    } else {
                        messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(beneficiaryDto.getStatusCode()));
                        if (StringUtils.isEmpty(messageData))
                            messageData = getString(R.string.card_activation_failed);
                        Util.LoggingQueue(this, "Ration Card Registration", "Error in activation:" + messageData);
                        Toast.makeText(OapAnpRegistrationConfirmActivity.this, messageData, Toast.LENGTH_SHORT).show();
                    }

                }
            }

            }catch(Exception e){
                dismissProgress();
                Util.messageBar(this, getString(R.string.connectionRefused));
            }


    }


    public void loginDevice(String password) {
        try {
            LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(this).getUserDetails(SessionId.getInstance().getUserId());
            LoginDto loginCredentials = new LoginDto();
            loginCredentials.setUserName(loginResponseDto.getUserDetailDto().getUserId());
            loginCredentials.setDeviceId(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            loginCredentials.setPassword(password);
            String login = new Gson().toJson(loginCredentials);
            StringEntity stringEntity = new StringEntity(login, HTTP.UTF_8);
            if (NetworkUtil.getConnectivityStatus(this) == 0) {
                Util.messageBar(this, getString(R.string.no_connectivity));
            } else {
                httpConnection = new HttpClientWrapper();
                String url = "/login/validateuser";
                Util.LoggingQueue(this, "Ration Card Registration", "Sending Benefeciary registration request to FPS server" + stringEntity);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGIN_USER,
                        SyncHandler, RequestType.POST, stringEntity, this);
            }
        } catch (Exception e) {
            Log.e("Error", "LoginError", e);
        }
    }

    private void editCard() {
        Intent intent = new Intent(this, BeneficiaryOapAnpActivationActivity.class);
        benefActivNewDto.setAadhaarSeedingDto(null);
        intent.putExtra("data", new Gson().toJson(benefActivNewDto));
        startActivity(intent);
        finish();
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

    private void submitCard() {
        try {

            Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "submitCard() called ");
            benefActivNewDto.setDeviceNum(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            TransactionBaseDto transaction = new TransactionBaseDto();
            transaction.setTransactionType(TransactionTypes.CARDNUMBER_BASED_ACTIVATION);
            transaction.setType("com.omneagate.rest.dto.BenefActivNewDto");
            transaction.setBaseDto(benefActivNewDto);

            if (NetworkUtil.getConnectivityStatus(this) == 0 || SessionId.getInstance().getSessionId().length() <= 0) {
                Util.messageBar(this, getString(R.string.no_connectivity));
            } else {
                findViewById(R.id.button_Submit).setOnClickListener(null);
                findViewById(R.id.button_Submit).setBackgroundColor(Color.LTGRAY);

                new InsertOfflineRegistration().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                httpConnection = new HttpClientWrapper();
                String url = "/transaction/process";
                String beneRegReq = new Gson().toJson(transaction);
                StringEntity se = new StringEntity(beneRegReq, HTTP.UTF_8);
                Util.LoggingQueue(this, "Oap anp Card Registration", "Sending Benefeciary registration request to FPS server" + beneRegReq);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_REGISTRATION,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "submitCard() Exception " + e.toString());


            if (progressBar != null)
                progressBar.dismiss();
//            errorNavigation(getString(R.string.internalError));
            Util.messageBar(this, getString(R.string.internalError));
        }
    }

    private class InsertBeneficiaryDataTask extends AsyncTask<Set<BeneficiaryDto>, Void, Boolean> {

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final Set<BeneficiaryDto>... args) {

            Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "InsertBeneficiaryDataTask called args[0] = " +args[0]);

            FPSDBHelper.getInstance(com.omneagate.activity.OapAnpRegistrationConfirmActivity.this).insertBeneficiaryData(args[0], "OapAnpRegistrationConfirmActivity");




            return true;
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean unused) {
           // FPSDBHelper.getInstance(com.omneagate.activity.OapAnpRegistrationConfirmActivity.this).updateCardRegistration(OldRationNumber);

            Toast.makeText(OapAnpRegistrationConfirmActivity.this, getString(R.string.cardActivated), Toast.LENGTH_LONG).show();


        }
    }

    private class UpdateRegistrationTask extends AsyncTask<String, Void, Boolean> {


        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            Util.LoggingQueue(OapAnpRegistrationConfirmActivity.this, "OapAnpRegistrationConfirmActivity", "UpdateRegistrationTask called args[0] = " +args[0]);

            FPSDBHelper.getInstance(com.omneagate.activity.OapAnpRegistrationConfirmActivity.this).updateCardRegistration(args[0]);
            return true;
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean unused) {



        }
    }

    private class InsertOfflineRegistration extends AsyncTask<String, Void, Boolean> {


        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            FPSDBHelper.getInstance(com.omneagate.activity.OapAnpRegistrationConfirmActivity.this).insertOffLineRegistration(benefActivNewDto);
            return true;
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean unused) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
