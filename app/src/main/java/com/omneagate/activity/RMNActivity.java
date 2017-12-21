package com.omneagate.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.OTPTransactionDto;
import com.omneagate.DTO.QROTPResponseDto;
import com.omneagate.DTO.RMNRequestDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.TransactionController.SMSListener;
import com.omneagate.TransactionController.Transaction;
import com.omneagate.TransactionController.TransactionFactory;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.Timer;
import java.util.TimerTask;

public class RMNActivity extends BaseActivity implements SMSListener {

    final Handler handler = new Handler();
    Timer timer;
    TimerTask timerTask;
    String rmn = "";
    private int count = 0;//Retry count

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_rmn);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        appState = (GlobalAppState) getApplication();
        Util.setTamilText((Button) findViewById(R.id.otpSubmit), R.string.submit);
        Util.setTamilText((Button) findViewById(R.id.otpCancel), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.textViewOtp), R.string.inputOTP);
        Util.setTamilText((TextView) findViewById(R.id.textViewMobile), R.string.rMN);
        findViewById(R.id.otpSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                checkValidation();
            }
        });
        findViewById(R.id.otpCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.omneagate.activity.RMNActivity.this, SaleActivity.class));
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
        String mobileNo = ((EditText) findViewById(R.id.editTextMobile)).getText().toString();
        if (mobileNo.length() == 10) {
            rmn = mobileNo;
            getOTPFromServer(mobileNo);
        } else {
            Util.messageBar(this, getString(R.string.invalidMobile));
        }
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case QR_CODE:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                }
                catch(Exception e) {}
                QRCodeResponseReceived(message);
                break;
            default:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                }
                catch(Exception e) {}
                break;
        }

    }

    //After qrCode sent to server and response received this function calls
    private void qrResponseData(final QROTPResponseDto qrCodeResponseReceived) {
        final View view = findViewById(R.id.editTextMobile);
        findViewById(R.id.otpLayout).setVisibility(View.VISIBLE);
        view.setEnabled(false);
        findViewById(R.id.editTextOTP).setEnabled(true);
        ((TextView) findViewById(R.id.textViewError)).setText("");
        findViewById(R.id.otpSubmit).setOnClickListener(new View.OnClickListener() {
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
                String messages = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(qrCodeResponseReceived.getStatusCode()));
                Util.messageBar(this, messages);
                errorNavigation(messages);
            }
        } catch (Exception e) {
            Log.e("RMNActivity", e.toString(), e);
            Util.LoggingQueue(this, "Error in RMN", e.toString());
        }

    }

    //Handler for 5 secs
    private void errorNavigation(String messages) {
        if (StringUtils.isEmpty(messages)) {
            messages = "Error in Creating OTP";
        }
        Intent intent = new Intent(this, SuccessFailureActivity.class);
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    /**
     * send request to get otp from server
     *
     * @param mobileNo
     */
    private void getOTPFromServer(String mobileNo) {
        try {
            RMNRequestDto qrCode = new RMNRequestDto();
            AndroidDeviceProperties devices = new AndroidDeviceProperties(this);
            qrCode.setDeviceId(devices.getDeviceProperties().getSerialNumber());
            qrCode.setMobileNumber(mobileNo);
            TransactionBaseDto base = new TransactionBaseDto();
            base.setType("com.omneagate.rest.dto.RMNRequestDto");
            base.setTransactionType(TransactionTypes.SALE_RMN_GENERATE);
            base.setBaseDto(qrCode);
            UpdateStockRequestDto update = new UpdateStockRequestDto();
            update.setRmn(rmn);
            update.setUfc("");
/*            if (GlobalAppState.transactionType == 0) {
                GlobalAppState.listener = this;
                checkMessage();
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                Transaction trans = TransactionFactory.getTransaction(0);
                trans.process(this, base, update);
            } else {

            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }
    }*/
            if (networkConnection.isNetworkAvailable()) {
                if (SessionId.getInstance().getSessionId().length() > 0) {
                    String url = "/transaction/process";
                    String qrCodes = new Gson().toJson(base);
                    StringEntity se = new StringEntity(qrCodes, HTTP.UTF_8);
                    progressBar = new CustomProgressDialog(this);
                    progressBar.setCancelable(false);
                    progressBar.show();
                    httpConnection.sendRequest(url, null, ServiceListenerType.QR_CODE,
                            SyncHandler, RequestType.POST, se, this);
                } else {
                    sendBySMS(base, update);
                }
            } else {
                sendBySMS(base, update);
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("RMNActivity", e.toString(), e);
        }
    }

    private void sendBySMS(TransactionBaseDto base, UpdateStockRequestDto update) {
        GlobalAppState.listener = this;
        checkMessage();
        progressBar = new CustomProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.show();
        Transaction trans = TransactionFactory.getTransaction(0);
        trans.process(this, base, update);
    }

    //Response from otp received is true
    private void otpDialogResult(QROTPResponseDto qrResponse) {
//        BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
       /* CardSalesTransaction beneficiary = new CardSalesTransaction(this);
        QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(rmn);
        appState.refId = qrResponse.getReferenceId();
        if (qrCodeResponseReceived != null) {
            qrCodeResponseReceived.setMode('Q');
            EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
            startActivity(new Intent(this, SalesEntryActivity.class));
            finish();
        } else {
            Util.messageBar(this, getString(R.string.cardActivate));
        }*/
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
            findViewById(R.id.otpSubmit).setEnabled(false);
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
        Log.i("qrResponse", qrResponse.toString());
        stopTimerTask(qrResponse);
    }

    private void removeData(QROTPResponseDto qrResponse) {
        progressBar.dismiss();
        if (StringUtils.isNotEmpty(qrResponse.getUfc())) {
            qrResponseData(qrResponse);
        } else {
            Toast.makeText(com.omneagate.activity.RMNActivity.this, "Connectivity Error", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, com.omneagate.activity.RMNActivity.class));
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
