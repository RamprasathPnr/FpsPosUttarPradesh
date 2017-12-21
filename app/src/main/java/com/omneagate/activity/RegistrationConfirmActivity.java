package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
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

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

public class RegistrationConfirmActivity extends BaseActivity {


    BenefActivNewDto benefActivNewDto;
    TransactionBaseDto transaction;
    String OldRationNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_registration_confirm);

        Util.LoggingQueue(this, "RegistrationConfirmActivity", "onCreate() called ");

        String message = getIntent().getStringExtra("data");
        benefActivNewDto = new Gson().fromJson(message, BenefActivNewDto.class);
        benefActivNewDto.setValueAdded(true);
        setInitialPage();

    }


    private void setInitialPage() {
        Util.LoggingQueue(this, "RegistrationConfirmActivity", "Starting setInitialPage ");
        setUpPopUpPage();
        Util.setTamilText((TextView) findViewById(R.id.aadharNumber), R.string.aadharNo);
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.card_registration_confirm);
        Util.setTamilText((TextView) findViewById(R.id.registration_text), R.string.normal_check_details);
        Util.setTamilText((TextView) findViewById(R.id.ration_card_no), R.string.normal_ration_card_number);
        Util.setTamilText((TextView) findViewById(R.id.a_reg_number), R.string.aRegisterNo);
        Util.setTamilText((TextView) findViewById(R.id.noOfCylinderTitle), R.string.cylinderHints);
        Util.setTamilText((TextView) findViewById(R.id.card_type), R.string.normal_cardCap);
        Util.setTamilText((TextView) findViewById(R.id.number_adults), R.string.number_adult_cap);
        Util.setTamilText((TextView) findViewById(R.id.number_child), R.string.number_child_cap);
        Util.setTamilText((TextView) findViewById(R.id.mob_number), R.string.mob_number);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setUserData();
    }

    private void setUserData() {
        TextView rationCardNumber = (TextView) findViewById(R.id.ration_card_value);
        String cardNo = benefActivNewDto.getRationCardNumber();
        String cardNumber = StringUtils.substring(cardNo, 0, 2) + "/" + StringUtils.substring(cardNo, 2, 3) + "/" + StringUtils.substring(cardNo, 3, 10);
        rationCardNumber.setText(cardNumber);
        ((TextView) findViewById(R.id.a_register_value)).setText(benefActivNewDto.getAregisterNum());
        ((TextView) findViewById(R.id.cylinder_value)).setText(String.valueOf(benefActivNewDto.getNumOfCylinder()));
        ((TextView) findViewById(R.id.number_adult)).setText(String.valueOf(benefActivNewDto.getNumOfAdults()));
        ((TextView) findViewById(R.id.number_child_count)).setText(String.valueOf(benefActivNewDto.getNumOfChild()));
        ((TextView) findViewById(R.id.card_value)).setText(benefActivNewDto.getCardTypeDef());
        ((TextView) findViewById(R.id.number_mobile)).setText(benefActivNewDto.getMobileNum());
        if (!benefActivNewDto.isChecked()) {
            ((TextView) findViewById(R.id.aadharValue)).setText(benefActivNewDto.getAadhaarSeedingDto().getUid());
        }
        Util.setTamilText((TextView) findViewById(R.id.button_edit), R.string.edit);
        Util.setTamilText((TextView) findViewById(R.id.button_cancel), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.button_Submit), R.string.submit);
        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.LoggingQueue(RegistrationConfirmActivity.this, "RegistrationConfirmActivity", "Cancell called ... Moving to card activation");
                Intent intent = new Intent(RegistrationConfirmActivity.this, CardActivationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.button_Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Log.e("reg confirm activity","submit activation");
                submitCard();
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
        /*if (progressBar != null) {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        }*/
        Util.LoggingQueue(RegistrationConfirmActivity.this, "RegistrationConfirmActivity ", "processMessage() called message -> " + message + " Type -> " + what);

        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {

        }

        switch (what) {

            case ERROR_MSG:

                Toast.makeText(getBaseContext(), R.string.connectionRefused, Toast.LENGTH_LONG).show();

                break;
            case CARD_REGISTRATION:
                registrationSubmissionResponse(message);
                break;
            case LOGIN_USER:
                userLoginResponse(message);
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
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            LoginResponseDto loginResponse = gson.fromJson(response, LoginResponseDto.class);
            dismissProgress();
            if (loginResponse != null) {
                if (loginResponse.isAuthenticationStatus()) {
                    SessionId.getInstance().setSessionId(loginResponse.getSessionid());
                    cardActvationProcess(transaction);
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
            Log.e("LoginActivity", e.toString(), e);
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


   /* public class  UpdateRegistrationInLocalDBTask extends  AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }

        @Override
        protected Void doInBackground(Void... params) {



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }*/



    private void registrationSubmissionResponse(Bundle message) {
        try {

            dismissProgress();
            Util.LoggingQueue(RegistrationConfirmActivity.this, "RegistrationConfirmActivity", "registrationSubmissionResponse() message = " + message);

            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();


            /** 11-07-2016
             * MSFixes
             * Added to alert already existing phone number
             *
             */




            BaseDto base = gson.fromJson(response, BaseDto.class);


            Util.LoggingQueue(RegistrationConfirmActivity.this, "RegistrationConfirmActivity", "registrationSubmissionResponse() getStatusCode = " + base.getStatusCode());

            if (base.getStatusCode() == 5077 || base.getStatusCode() == 6048 ) {

                Util.messageBar(this, getString(R.string.ufc_not_available));
            } else {
                BeneficiaryDto beneficiaryDto = gson.fromJson(response, BeneficiaryDto.class);
                String messageData = "";


                Util.LoggingQueue(RegistrationConfirmActivity.this, "RegistrationConfirmActivity", "registrationSubmissionResponse() beneficiaryDto = " + beneficiaryDto);


                String phNoFromResponse = "" + beneficiaryDto.getMobileNumber();
                Log.e("phNoFromResponse", "No = " + phNoFromResponse);
                String phNoFromOriginalBeneDto = "" + benefActivNewDto.getMobileNum();
                Log.e("phNoFromOriginalBeneDto", "No = " + phNoFromOriginalBeneDto);
                String registeredMobileNoInfo = "";


                if(phNoFromResponse == null || phNoFromResponse.equalsIgnoreCase("null")) {
                    if(phNoFromOriginalBeneDto == null || phNoFromOriginalBeneDto.isEmpty()){
                        registeredMobileNoInfo = "";
                        Log.e("empty", "no phone given");
                    }else{
                        registeredMobileNoInfo = "exists";
                        Log.e("exists", "null");
                    }
                }else{
                    registeredMobileNoInfo = "success";
                    Log.e("success", "not exists");
                }

               /* if (phNoFromOriginalBeneDto.equalsIgnoreCase("")) {
                    registeredMobileNoInfo = "";
                    Log.e("empty", "no phone given");

                } else if (phNoFromResponse == null || phNoFromResponse.equalsIgnoreCase("null")) {
                    registeredMobileNoInfo = "exists";
                    Log.e("exists", "null");

                } else if (phNoFromResponse.equalsIgnoreCase(phNoFromOriginalBeneDto)) {
                    registeredMobileNoInfo = "success";
                    Log.e("success", "not exists");

                }*/

                if (beneficiaryDto.getStatusCode() == 0 || beneficiaryDto.getStatusCode() == 5036 && !response.contains("timestamp")) {
                    Util.LoggingQueue(this, "Ration Card Registration", "Received Status code" + beneficiaryDto.getStatusCode());
                    Set<BeneficiaryDto> beneficiarySet = new HashSet<>();

                   /* if (phNoFromOriginalBeneDto.equalsIgnoreCase("")) {
                        // registeredMobileNoInfo = "";
                        beneficiaryDto.setMobileNumber("");
                    } else if (phNoFromResponse.equalsIgnoreCase("null")) {
                        // registeredMobileNoInfo = "exists";
                        beneficiaryDto.setMobileNumber(null);
                    } else if (phNoFromResponse.equalsIgnoreCase(phNoFromOriginalBeneDto)) {
                        //registeredMobileNoInfo = "success";
                        beneficiaryDto.setMobileNumber(benefActivNewDto.getMobileNum());
                    }
*/
                    beneficiarySet.add(beneficiaryDto);


                    OldRationNumber = beneficiaryDto.getOldRationNumber();

                    new InsertBeneficiaryDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, beneficiarySet);
                    // new UpdateRegistrationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, beneficiaryDto.getOldRationNumber());

                    try {
                        benefActivNewDto.getAadhaarSeedingDto().setBeneficiaryID(beneficiaryDto.getId());
                        FPSDBHelper.getInstance(this).beneficiaryMemberAadhar(benefActivNewDto.getAadhaarSeedingDto());
                    } catch (Exception e) {


                        Util.LoggingQueue(this, "Ration Card Registration", "beneficiaryMemberAadhar Exception = " + e);

                    }


                    Intent intent = new Intent(this, ActivationSuccessActivity.class);
                    intent.putExtra("data", new Gson().toJson(benefActivNewDto));
                    /** 11-07-2016
                     * MSFixes
                     * Added to alert already existing phone number
                     *
                     */
                    intent.putExtra("registeredMobileNoInfo", "" + registeredMobileNoInfo);
                    startActivity(intent);
                    finish();
                } else {
                    if (beneficiaryDto.getStatusCode() == 400 || beneficiaryDto.getStatusCode() == 401 || beneficiaryDto.getStatusCode() == 403 || beneficiaryDto.getStatusCode() == 5000) {
                        loginDevice(SessionId.getInstance().getLocalpasword());
                        return;
                    } else if (beneficiaryDto.getStatusCode() == 7006 || beneficiaryDto.getStatusCode() == 30001) {
                        Util.messageBar(this, getString(R.string.aadhar_available));
                        return;
                    } else if (beneficiaryDto.getStatusCode() == 500) {
                        Util.messageBar(this, getString(R.string.connectionRefused));
                    } else {
                        messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(beneficiaryDto.getStatusCode()));
                        if (StringUtils.isEmpty(messageData))
                            messageData = getString(R.string.card_activation_failed);
                        Util.LoggingQueue(RegistrationConfirmActivity.this, "RegistrationConfirmActivity", "Error in activation: = " + messageData);

                    }

                }

            }

        } catch (Exception e) {
            dismissProgress();
            Util.LoggingQueue(RegistrationConfirmActivity.this, "RegistrationConfirmActivity", "registrationSubmissionResponse() Exception = " + e);
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
        Intent intent = new Intent(this, RationCardActivationAadharActivity.class);
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
            Util.LoggingQueue(this, "Ration Card Registration", "Submit Card called");
            benefActivNewDto.setDeviceNum(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            transaction = new TransactionBaseDto();
            transaction.setTransactionType(TransactionTypes.CARDNUMBER_BASED_ACTIVATION);
            transaction.setType("com.omneagate.rest.dto.BenefActivNewDto");
            transaction.setBaseDto(benefActivNewDto);

            if (NetworkUtil.getConnectivityStatus(this) == 0 ) {
                Util.messageBar(this, getString(R.string.no_connectivity));
            } else if( SessionId.getInstance().getSessionId().length() <= 0) {
              //  new LoginPasswordDialog(this).show();
                loginDevice(SessionId.getInstance().getLocalpasword());
            } else
            {
                findViewById(R.id.button_Submit).setOnClickListener(null);
                findViewById(R.id.button_Submit).setBackgroundColor(Color.LTGRAY);
                cardActvationProcess(transaction);

            }
        } catch (Exception e) {
            Log.e("RegistrationConfirm", e.toString(), e);
            Util.LoggingQueue(this, "Ration Card Registration", "Error:" + e.getMessage());
            if (progressBar != null)
                progressBar.dismiss();
//            errorNavigation(getString(R.string.internalError));
            Util.messageBar(this, getString(R.string.internalError));
        }
    }

    private class InsertBeneficiaryDataTask extends AsyncTask<Set<BeneficiaryDto>, Void, Boolean> {

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final Set<BeneficiaryDto>... args) {
            Util.LoggingQueue(RegistrationConfirmActivity.this, "Ration Card Registration", "InsertBeneficiaryDataTask started ");

            FPSDBHelper.getInstance(com.omneagate.activity.RegistrationConfirmActivity.this).insertBeneficiaryData(args[0], "RegistrationConfirmActivity");




            return true;
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean unused) {
           // FPSDBHelper.getInstance(com.omneagate.activity.RegistrationConfirmActivity.this).updateCardRegistration(OldRationNumber);

            Toast.makeText(RegistrationConfirmActivity.this, getString(R.string.cardActivated), Toast.LENGTH_LONG).show();


        }
    }

    private class UpdateRegistrationTask extends AsyncTask<String, Void, Boolean> {


        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {

            Util.LoggingQueue(RegistrationConfirmActivity.this, "Ration Card Registration", "UpdateRegistrationTask started ");

            FPSDBHelper.getInstance(com.omneagate.activity.RegistrationConfirmActivity.this).updateCardRegistration(args[0]);
            return true;
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean unused) {
        }
    }

    private class InsertOfflineRegistration extends AsyncTask<String, Void, Boolean> {


        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            FPSDBHelper.getInstance(com.omneagate.activity.RegistrationConfirmActivity.this).insertOffLineRegistration(benefActivNewDto);
            return true;
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean unused) {
        }
    }

    private void cardActvationProcess(TransactionBaseDto transaction)
    {
        try {
            new InsertOfflineRegistration().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            httpConnection = new HttpClientWrapper();
            String url = "/transaction/process";
            String beneRegReq = new Gson().toJson(transaction);
            Util.LoggingQueue(this, "RegistrationConfirmActivity", "cardActvationProcess() called TransactionBaseDto = "+transaction);


            StringEntity se = null;
            se = new StringEntity(beneRegReq, HTTP.UTF_8);
            Util.LoggingQueue(this, "Ration Card Registration", "Sending Benefeciary registration request to FPS server" + beneRegReq);
            progressBar = new CustomProgressDialog(this);
            progressBar.setCancelable(false);
            progressBar.show();
            httpConnection.sendRequest(url, null, ServiceListenerType.CARD_REGISTRATION,
                    SyncHandler, RequestType.POST, se, this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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