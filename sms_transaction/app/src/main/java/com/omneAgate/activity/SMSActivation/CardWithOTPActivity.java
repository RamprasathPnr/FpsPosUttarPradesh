package com.omneagate.activity.SMSActivation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.OTPTransactionDto;
import com.omneagate.DTO.QROTPRequestDto;
import com.omneagate.DTO.QROTPResponseDto;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.TransactionController.SMSListener;
import com.omneagate.TransactionController.Transaction;
import com.omneagate.TransactionController.TransactionFactory;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.BeneficiarySalesTransaction;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.activity.BaseActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.SaleActivity;
import com.omneagate.activity.SalesEntryActivity;
import com.omneagate.activity.SuccessFailureActivity;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.Timer;
import java.util.TimerTask;

public class CardWithOTPActivity extends BaseActivity implements SMSListener {

    private int count = 0;//Retry count
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    //Progressbar for waiting
    CustomProgressDialog progressBar;
    GlobalAppState appState;

    //HttpConnection service
    HttpClientWrapper httpConnection;
    String rmn = "";
    NetworkConnection networkConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_card_rmn);
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        appState = (GlobalAppState) getApplication();
        Util.setTamilText((Button) findViewById(R.id.otpSubmit), R.string.submit);
        Util.setTamilText((Button) findViewById(R.id.otpCancel), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.textViewOtp), R.string.inputCard);
        Util.setTamilText((TextView) findViewById(R.id.textViewMobile), R.string.inputCard);
        ((Button) findViewById(R.id.otpSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                checkValidation();
            }
        });
        ((Button) findViewById(R.id.otpCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CardWithOTPActivity.this, SaleActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }

    /**
     * check mobile number and request otp from server
     */
    private void checkValidation() {
        String cardNo = ((EditText) findViewById(R.id.editTextMobile)).getText().toString().trim();
        cardNo = cardNo.replaceAll("[^a-zA-Z0-9]", "");
        if (cardNo == null || cardNo.length() == 0) {
            Util.messageBar(this, "Card Number should not empty");
            return;
        }
        if (TransactionBase.getInstance().getTransactionBase().getTransactionType() == TransactionTypes.SALE_QR_OTP_DISABLED) {
            CardSalesTransaction sales = new CardSalesTransaction(this);
            QRTransactionResponseDto qrCodeResponseReceived = sales.getBeneficiaryDetails(cardNo.toUpperCase());
            if (qrCodeResponseReceived != null) {
                qrCodeResponseReceived.setMode('Q');
                EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                startActivity(new Intent(this, CardSalesActivity.class));
                finish();
            }

        } else {
            getOTPFromServer(cardNo);

        }
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case QR_CODE:
                if (progressBar != null) {
                    progressBar.dismiss();
                }
                QRCodeResponseReceived(message);
                break;
            default:
                if (progressBar != null) {
                    progressBar.dismiss();
                }
                break;
        }

    }

    //After qrCode sent to server and response received this function calls
    private void qrResponseData(final QROTPResponseDto qrCodeResponseReceived) {
        final View view = (EditText) findViewById(R.id.editTextMobile);
        ((LinearLayout) findViewById(R.id.otpLayout)).setVisibility(View.VISIBLE);
        view.setEnabled(false);
        ((EditText) findViewById(R.id.editTextOTP)).setEnabled(true);
        ((TextView) findViewById(R.id.textViewError)).setText("");
        ((Button) findViewById(R.id.otpSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (checkOTPValidate(qrCodeResponseReceived)) {
                    otpDialogResult(qrCodeResponseReceived);
                }
            }
        });
    }

    //Orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.changeLanguage(this, GlobalAppState.language);
        super.onSaveInstanceState(outState);
    }

    //QrCode response from server for respective card
    private void QRCodeResponseReceived(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            QROTPResponseDto qrCodeResponseReceived = gson.fromJson(response,
                    QROTPResponseDto.class);
            if (qrCodeResponseReceived.getStatusCode() == 0) {
                qrResponseData(qrCodeResponseReceived);
            } else {
                String messages = FPSDBHelper.getInstance(this).retrieveLanguageTable(qrCodeResponseReceived.getStatusCode(), GlobalAppState.language).getDescription();
                Util.messageBar(this, messages);
                errorNavigation(messages);
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
            Util.LoggingQueue(this, "Error in RMN", e.toString());
        }

    }

    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Intent intent = new Intent(this, SuccessFailureActivity.class);
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    /**
     * send request to get otp from server
     *
     * @param cardNumber
     */
    private void getOTPFromServer(String cardNumber) {
        try {
            QROTPRequestDto qrCode = new QROTPRequestDto();
            AndroidDeviceProperties devices = new AndroidDeviceProperties(this);
            qrCode.setDeviceId(devices.getDeviceProperties().getSerialNumber());
            qrCode.setUfc(cardNumber);
            TransactionBaseDto base = new TransactionBaseDto();
            base.setType("com.omneagate.rest.dto.QRRequestDto");
            base.setTransactionType(TransactionTypes.SALE_QR_OTP_GENERATE);
            base.setBaseDto(qrCode);
            UpdateStockRequestDto update = new UpdateStockRequestDto();
            update.setUfc(cardNumber);
            if (GlobalAppState.transactionType == 0) {
                GlobalAppState.listener = this;
                checkMessage();
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                Transaction trans = TransactionFactory.getTransaction(0);
                trans.process(this, base, update);
            } else {
                String url = "/transaction/process";
                String qrCodes = new Gson().toJson(base);
                Log.e("QR COde", qrCodes);
                StringEntity se = new StringEntity(qrCodes, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.QR_CODE,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }
    }


    //Response from otp received is true
    private void otpDialogResult(QROTPResponseDto qrResponse) {
        BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
        QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrResponse.getUfc());
        appState.refId = qrResponse.getReferenceId();
        if (qrCodeResponseReceived != null) {
            qrCodeResponseReceived.setMode('Q');
            EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
            startActivity(new Intent(this, SalesEntryActivity.class));
            finish();
        } else {
            Util.messageBar(this, getString(R.string.cardActivate));
        }
    }

    /**
     * OTP validation
     *
     * @param qrResponse received from server
     *                   return true if otp equals user entry else false
     */
    private boolean checkOTPValidate(QROTPResponseDto qrResponse) {
        EditText mobileOtp = (EditText) findViewById(R.id.editTextOTP);
        String oTP = mobileOtp.getText().toString();
        if (oTP.equals(qrResponse.getOtpTransactionDto().getOtp())) {
            return true;
        } else if (count == 3) {
            Util.messageBar(this, getString(R.string.retryCount));
            errorNavigation(getString(R.string.retryCount));
            ((Button) findViewById(R.id.otpSubmit)).setEnabled(false);
        } else
            Util.messageBar(this, getString(R.string.mobileOTPWrong));
        count++;
        return false;

    }

    @Override
    public void smsReceived(UpdateStockRequestDto stockRequestDto) {
        appState.refId = stockRequestDto.getRefNumber();
        QROTPResponseDto qrResponse = new QROTPResponseDto();
        OTPTransactionDto otpData = new OTPTransactionDto();
        otpData.setId(stockRequestDto.getOtpId());
        otpData.setOtp(stockRequestDto.getOtp());
        qrResponse.setOtpTransactionDto(otpData);
        qrResponse.setUfc(stockRequestDto.getUfc());
        qrResponse.setReferenceId(stockRequestDto.getRefNumber());
        Log.e("Res", qrResponse.toString());
        stopTimerTask(qrResponse);
    }

    private void removeData(QROTPResponseDto qrResponse) {
        progressBar.dismiss();
        if (StringUtils.isNotEmpty(qrResponse.getUfc())) {
            qrResponseData(qrResponse);
        } else {
            Toast.makeText(CardWithOTPActivity.this, "Connectivity Error", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, CardWithOTPActivity.class));
            finish();
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
                        stopTimerTask(new QROTPResponseDto());
                    }
                });
            }
        };
    }

    public void stopTimerTask(QROTPResponseDto qrResponse) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        GlobalAppState.listener = null;
        removeData(qrResponse);
    }
}
