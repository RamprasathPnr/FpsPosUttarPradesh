package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.omneagate.Util.TransactionMessage;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;

public class QrCodeWithOTPActivity extends BaseActivity implements SMSListener {

    private int count = 0;//Retry count
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    private String qrCodeData = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_qr_code_with_otp);
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        appState = (GlobalAppState) getApplication();
        Util.setTamilText((Button) findViewById(R.id.otpSubmit), R.string.submit);
        Util.setTamilText((Button) findViewById(R.id.otpCancel), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.textViewOtp), R.string.inputOTP);
        IntentIntegrator.initiateScan(this, R.layout.activity_capture,
                R.id.viewfinder_view, R.id.preview_view, true);
        ((Button) findViewById(R.id.otpCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QrCodeWithOTPActivity.this, SaleActivity.class));
                finish();
            }
        });
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
        final View view = (EditText) findViewById(R.id.editTextOTP);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
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
                ((Button) findViewById(R.id.otpSubmit)).setOnClickListener(null);
                errorNavigation(messages);
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
            Util.LoggingQueue(this, "Error", e.toString());
        }

    }

    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Util.setTamilText((TextView) findViewById(R.id.textViewError), messages);
        Intent intent = new Intent(this, SuccessFailureActivity.class);
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    /**
     * send request to get otp from server
     *
     * @param qrCodeString
     */
    private void getOTPFromServer(String qrCodeString) {
        try {
            QROTPRequestDto qrCode = new QROTPRequestDto();
            AndroidDeviceProperties devices = new AndroidDeviceProperties(this);
            qrCode.setDeviceId(devices.getDeviceProperties().getSerialNumber());
            qrCode.setUfc(qrCodeString);
            qrCodeData = qrCodeString;
            TransactionBaseDto base = new TransactionBaseDto();
            base.setType("com.omneagate.rest.dto.QRRequestDto");
            base.setTransactionType(TransactionTypes.SALE_QR_OTP_GENERATE);
            base.setBaseDto(qrCode);
            UpdateStockRequestDto update = new UpdateStockRequestDto();
            update.setUfc(qrCodeString);
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

    private void removeData(QROTPResponseDto qrResponse) {
        progressBar.dismiss();
        Map<String, String> data = TransactionMessage.getInstance().getTransactionMessage();
        String value = data.get(qrResponse.getUfc());
        if (StringUtils.isNotEmpty(qrResponse.getUfc())) {
            data.remove(qrResponse.getUfc());
            qrResponseData(qrResponse);
        } else {
            Toast.makeText(this, "Connectivity Error", Toast.LENGTH_LONG).show();
        }
    }

    private void checkMessage() {
        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 60000);
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

    /**
     * QR code response received for card
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                try {
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                            resultCode, data);
                    if (scanResult == null) {
                        return;
                    }
                    qrResponse(scanResult.getContents());
                } catch (Exception e) {
                    return;
                }

                break;
            default:
                break;
        }
    }

    //Response from QR reader
    private void qrResponse(String result) {
        SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
        String languageCode = prefs.getString("lCode", "ta");
        Util.changeLanguage(this, languageCode);
        GlobalAppState.language = languageCode;
        if (result == null) {
            startActivity(new Intent(this, SaleActivity.class));
            finish();
        } else {
            String lines[] = result.split("\\r?\\n");
            getOTPFromServer(lines[0]);
        }
    }

    //Response from otp received is true
    private void otpDialogResult(QROTPResponseDto qrResponse) {
        BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
        QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeData);
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
        if (StringUtils.isEmpty(oTP)) {
            return false;
        }
        if (oTP.equals(qrResponse.getOtpTransactionDto().getOtp())) {
            return true;
        } else if (count == 3) {
            Util.messageBar(this, getString(R.string.retryCount));
            ((Button) findViewById(R.id.otpSubmit)).setOnClickListener(null);
            errorNavigation(getString(R.string.retryCount));
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
        stopTimerTask(qrResponse);
    }
}