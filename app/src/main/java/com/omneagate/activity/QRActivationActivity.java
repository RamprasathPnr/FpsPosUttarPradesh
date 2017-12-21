package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.QRRequestDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.process.RegularSyncProcess;
import com.omneagate.service.BaseSchedulerService;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;



//Beneficiary Activity to check Beneficiary Activation
public class QRActivationActivity extends BaseActivity implements View.OnClickListener {

    String qrCode = "", number = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_mobile_otp_need);
        httpConnection = new HttpClientWrapper();
        Util.LoggingQueue(this, "QR Card Activation", "Setting up QR card activation");
        String packageString = getApplicationContext().getPackageName();
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(packageString);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    /**
     * QR code response received for card
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
               /* if (resultCode == Activity.RESULT_OK) {
                    try {
                        String contents = data.getStringExtra(Intents.Scan.RESULT);
                        Log.e("EncryptedUFC",contents);
                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(this, "QRcode sales", "QR exception called:" + e.toString());
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(QRCodeSalesActivity.class.getSimpleName(),"Scan cancelled");
                }*/

                break;

            default:
                break;
        }
    }

    //Response from QR reader
    private void qrResponse(String result) {
        String languageCode = FPSDBHelper.getInstance(this).getMasterData("language");
        Util.changeLanguage(this, languageCode);
        GlobalAppState.language = languageCode;
        if (result == null) {
            startActivity(new Intent(this, CardActivationActivity.class));
            Util.LoggingQueue(this, "QR Card Activation", "Qr incorrect");
            finish();
        } else {
            String lines[] = result.split("\\r?\\n");
            qrCode = lines[0];
            setUpInitialPage(qrCode);

        }
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


    private void addNumber(String text) {
        try {
            if (number.length() >= 10) {
                return;
            }

            number = number + text;
            setText();
        } catch (Exception e) {
            Log.e("QRActivation", e.toString(), e);
        }
    }

    private void setText() {
        ((TextView) findViewById(R.id.mobileNumberOTP)).setText(number);
    }

    private void setUpInitialPage(String qrCode) {
        setUpPopUpPage();
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.qrcard_registration);
        viewForMobileOtp(qrCode);
    }

    /*
* On click layout user page will be changed
* */
    private void viewForMobileOtp(final String qrCode) {
        LinearLayout mobileOtp = (LinearLayout) findViewById(R.id.myMobileOTPBackground);
        mobileOtp.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.view_need_otp, null);
        mobileOtp.addView(view);
        Util.LoggingQueue(this, "QR Card Activation", "Getting mobile number from user");
        Util.setTamilText((TextView) findViewById(R.id.invalidMobileNumber), R.string.enter_mobile_no);
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
        view.findViewById(R.id.buttonNeedOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOTPFromServer(qrCode);
            }
        });
    }

    private void removeNumber() {
        if (number.length() > 0) {
            number = number.substring(0, number.length() - 1);
        } else {
            number = "";
        }
        setText();
    }

    /**
     * send request to get otp from server
     *
     * @param qrCodeString
     */
    private void getOTPFromServer(String qrCodeString) {
        try {
            if (number.length() == 10 || number.length() == 0) {
                QRRequestDto qrCode = new QRRequestDto();
                qrCode.setUfc(qrCodeString);
                qrCode.setMobileNum(number);
                qrCode.setDeviceId(Settings.Secure.getString(
                        getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                TransactionBaseDto base = new TransactionBaseDto();
                base.setType("com.omneagate.rest.dto.QRRequestDto");
                base.setTransactionType(TransactionTypes.BENEFICIARY_REGISTRATION_NEW);
                base.setBaseDto(qrCode);
                String url = "/transaction/process";
                String qrCodes = new Gson().toJson(base);
                StringEntity se = new StringEntity(qrCodes, HTTP.UTF_8);
                Util.LoggingQueue(this, "QR Card Activation", "Activation request:" + qrCodes);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.QR_CODE,
                        SyncHandler, RequestType.POST, se, this);
            } else {
                Util.messageBar(this, getString(R.string.invalidMobile));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "QR Card Activation", "Error request:" + e.getStackTrace().toString());
            Log.e("QRActivation", e.toString(), e);
        }
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
            case QR_CODE:
                QRCodeResponseReceived(message);
                break;
            default:
                Util.messageBar(this, getString(R.string.connectionRefused));

                break;
        }

    }


    //QrCode response from server for respective card
    private void QRCodeResponseReceived(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Util.LoggingQueue(this, "QR Card Activation", "Activation resp:" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto base = gson.fromJson(response, BaseDto.class);
            String messageData = "";
            Log.i("Response", response);
            if (base.getStatusCode() == 0) {
                messageData = getString(R.string.card_activation_success);
                Util.LoggingQueue(this, "QR Card Activation", "Sync process called");
                RegularSyncProcess rsp = new RegularSyncProcess();
                BaseSchedulerService bsAllocation = (BaseSchedulerService) rsp;
                bsAllocation.process(this);
            } else {
                messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(base.getStatusCode()));
                if (StringUtils.isEmpty(messageData))
                    messageData = getString(R.string.card_activation_failed);
                Util.LoggingQueue(this, "QR Card Activation", "Error in activation:" + messageData);
            }
            errorNavigation(messageData);
        } catch (Exception e) {
            Log.e("QRActivation", e.toString(), e);
            Util.LoggingQueue(this, "QR Card Activation Resp", "Error:" + e.getStackTrace().toString());
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
    public void onBackPressed() {
        startActivity(new Intent(this, CardActivationActivity.class));
        Util.LoggingQueue(this, "QR Card Activation", "Back press called");
        finish();
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
