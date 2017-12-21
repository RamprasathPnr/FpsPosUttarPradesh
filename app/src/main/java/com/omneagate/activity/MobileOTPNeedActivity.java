package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.OTPTransactionDto;
import com.omneagate.DTO.QROTPResponseDto;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.RMNRequestDto;
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
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.Timer;
import java.util.TimerTask;

public class MobileOTPNeedActivity extends BaseActivity implements SMSListener, View.OnClickListener {

    final Handler handler = new Handler();     //Handler for user
    Timer timer;               //Initial timer
    TimerTask timerTask;          //Timer task intialization
    String rmn = "";    //User registered mobile no
    String number = "";
    private boolean iNeedOtp = true;       //Boolean for user have otp or not
    private int count = 0;        //Retry count

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_otp_need);

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "onCreate() called ");

        setUpInitialPage();
    }


    /*
    *
    * Initial Setup
    *
    * */
    private void setUpInitialPage() {
        networkConnection = new NetworkConnection(this);
       // Util.LoggingQueue(this, "Mobile Need OTP", "Setting up Initial page");
        httpConnection = new HttpClientWrapper();
        appState = (com.omneagate.activity.GlobalAppState) getApplication();
        setUpPopUpPage();
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.mobile_otp);
        viewForMobileOtp();
    }


    /*
    * On click layout user page will be changed
    * */
    private void viewForMobileOtp() {

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "viewForMobileOtp() called ");

        LinearLayout mobileOtp = (LinearLayout) findViewById(R.id.myMobileOTPBackground);
        mobileOtp.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.view_need_otp, null);
        mobileOtp.addView(view);
        findViewById(R.id.invalidMobileNumber).setVisibility(View.GONE);
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
//                boolean isExist = FPSDBHelper.getInstance(com.omneagate.activity.MobileOTPNeedActivity.this).checkMobNoExistence(number);
//                if(isExist) {
                    iNeedOTP();
//                }
//                else {
//                    String messages = Util.messageSelection(FPSDBHelper.getInstance(MobileOTPNeedActivity.this).retrieveLanguageTable(5030));
//                     Util.messageBar(this, messages);
//                    errorNavigation(messages);
//                    Util.messageBar(com.omneagate.activity.MobileOTPNeedActivity.this, getString(messages));
//                }
            }
        });
    }

    /*
    * User need OTP
    * */
    private void iNeedOTP() {
        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "iNeedOTP() called ");
        if (StringUtils.isEmpty(number)) {
            Util.messageBar(this, getString(R.string.mobile_no_empty));
        } else {
            if (number.length() < 10) {
                findViewById(R.id.invalidMobileNumber).setVisibility(View.VISIBLE);
            } else {
                boolean isExist = FPSDBHelper.getInstance(com.omneagate.activity.MobileOTPNeedActivity.this).checkMobNoExistence(number);
                if(isExist) {
                    rmn = number;
                    getOTPFromServer(number);
                }
                else {
                    String messages = Util.messageSelection(FPSDBHelper.getInstance(MobileOTPNeedActivity.this).retrieveLanguageTable(5030));
                    errorNavigation(messages);
                }

            }
        }
    }

    /**
     * send request to get otp from server
     *
     * @param mobileNo
     */
    private void getOTPFromServer(String mobileNo) {
        try {

            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "getOTPFromServer() called ");


            RMNRequestDto qrCode = new RMNRequestDto();
            AndroidDeviceProperties devices = new AndroidDeviceProperties(this);
            qrCode.setDeviceId(devices.getDeviceProperties().getSerialNumber());
            qrCode.setMobileNumber(mobileNo);
            qrCode.setFpsStoreId(SessionId.getInstance().getFpsId());
            TransactionBaseDto base = new TransactionBaseDto();
            base.setType("com.omneagate.rest.dto.RMNRequestDto");
            base.setTransactionType(TransactionTypes.SALE_RMN_GENERATE);
            base.setBaseDto(qrCode);
            UpdateStockRequestDto update = new UpdateStockRequestDto();
            update.setRmn(rmn);
            update.setUfc("");

            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "getOTPFromServer() TransactionBaseDto =  "+base);


            if (networkConnection.isNetworkAvailable()) {
                if (SessionId.getInstance().getSessionId().length() > 0) {
                    String url = "/transaction/process";
                    String qrCodes = new Gson().toJson(base);
                    Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "getOTPFromServer() TransactionBaseDto JSON =  "+qrCodes);
                    StringEntity se = new StringEntity(qrCodes, HTTP.UTF_8);
                    progressBar = new CustomProgressDialog(this);
                    progressBar.setCancelable(false);
                    progressBar.show();
                    httpConnection.sendRequest(url, null, ServiceListenerType.QR_CODE,
                            SyncHandler, RequestType.POST, se, this);
                } else {
//                    sendBySMS(base, update);
                    Util.messageBar(this, getString(R.string.no_connectivity));
                }
            } else {
//                sendBySMS(base, update);
                Util.messageBar(this, getString(R.string.no_connectivity));
            }
        } catch (Exception e) {
            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "getOTPFromServer() Exception =  "+e);
        }
    }

    /*
    *
    * Send sms transaction
    *
    * */
    private void sendBySMS(TransactionBaseDto base, UpdateStockRequestDto update) {


        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "sendBySMS() TransactionBaseDto  =  "+base);
        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "sendBySMS() UpdateStockRequestDto  =  "+update);

        com.omneagate.activity.GlobalAppState.listener = this;
        if (com.omneagate.activity.GlobalAppState.smsAvailable) {
            checkMessage();
            progressBar = new CustomProgressDialog(this);
            progressBar.setCancelable(false);
            progressBar.show();
            Transaction trans = TransactionFactory.getTransaction(0);
            trans.process(this, base, update);
        } else {
            Util.messageBar(this, getString(R.string.no_connectivity));
        }
    }


    /*
    * Concrete Method for SMS receiver
    * */
    @Override
    public void smsReceived(UpdateStockRequestDto stockRequestDto) {


        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "smsReceived() UpdateStockRequestDto  =  "+stockRequestDto);

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


    //Concrete method
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {


        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "processMessage() message  =  "+message);
        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "processMessage() what  =  "+what);


        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {

        }

        switch (what) {
            case QR_CODE:

                QRCodeResponseReceived(message);
                break;
            case ERROR_MSG:

                Toast.makeText(getBaseContext(), R.string.connectionRefused, Toast.LENGTH_LONG).show();

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

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "removeNumber()   =  ");

        if (number.length() > 0) {
            number = number.substring(0, number.length() - 1);
        } else {
            number = "";
        }
        setText();
    }

    private void addNumber(String text) {

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "addNumber() text  =  "+text);

        try {
            if (iNeedOtp) {
                if (number.length() >= 10) {
                    return;
                }
            } else {
                if (number.length() >= 7) {
                    return;
                }
            }

            number = number + text;
            setText();
        } catch (Exception e) {

            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "addNumber() Exception  =  "+e);

        }
    }

    private void setText() {
        ((TextView) findViewById(R.id.mobileNumberOTP)).setText(number);
    }

    /*
    * Timer start up
    * */
    private void checkMessage() {

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "checkMessage()   =  ");

        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 75000);
    }

    /*
    *
    * Initialization for timer
    * */
    public void initializeTimerTask() {
        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "initializeTimerTask()   =  ");


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

    /*
    * Stop Timer for message receives
    * */
    public void stopTimerTask(QROTPResponseDto qrResponse) {

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "stopTimerTask()   =  ");

        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        GlobalAppState.listener = null;
        removeData(qrResponse);
    }


    /*
    *
    * SMS Connection Error when data
    * not received in time
    *
    * */
    private void removeData(QROTPResponseDto qrResponse) {

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "removeData()  QROTPResponseDto =  " +qrResponse);

        progressBar.dismiss();
        if (StringUtils.isNotEmpty(qrResponse.getUfc())) {
            qrResponseData(qrResponse);
        } else {
            errorNavigation("Connectivity Error");
        }
    }

    /**
     * OTP validation
     *
     * @param qrResponse received from server
     *                   return true if otp equals user entry else false
     */
    private boolean checkOTPValidate(QROTPResponseDto qrResponse) {
        Util.LoggingQueue(this, "Mobile Need OTP", "OTP checking");
        if (number.equals(qrResponse.getOtpTransactionDto().getOtp())) {
            return true;
        } else if (count == 3) {
            Util.LoggingQueue(this, "Mobile Need OTP", "Retry count exceeds");
            Util.messageBar(this, getString(R.string.retryCount));
            errorNavigation(getString(R.string.retryCount));
        } else {
            Util.LoggingQueue(this, "Mobile Need OTP", "Invalid OTP");
            Util.messageBar(this, getString(R.string.mobileOTPWrong));
        }
        count++;
        return false;

    }

    /*
    * After qrCode sent to server and response received this function calls
    *
    * */
    private void qrResponseData(final QROTPResponseDto qrCodeResponseReceived) {

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "qrResponseData()  QROTPResponseDto =  " +qrCodeResponseReceived);

        iNeedOtp = false;
        number = "";
        LinearLayout mobileOtp = (LinearLayout) findViewById(R.id.myMobileOTPBackground);
        mobileOtp.removeAllViews();
        LayoutInflater vi = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = vi.inflate(R.layout.view_received_otp, null);
        mobileOtp.addView(view);

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

                /*if (checkOTPValidate(qrCodeResponseReceived))
                    otpDialogResult(qrCodeResponseReceived);*/

                // 03-08-2016
                // Invalid OTP Handling
                try{
                    if (checkOTPValidate(qrCodeResponseReceived)) {
                        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "checkOTPValidate(qrCodeResponseReceived) Returns TRUE");
                        otpDialogResult(qrCodeResponseReceived);
                    }
                    else{
                       // Toast.makeText(MobileOTPNeedActivity.this, getString(R.string.invalid_otp), Toast.LENGTH_LONG).show();
                        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "checkOTPValidate(qrCodeResponseReceived) Returns FALSE");
                    }
                }catch (Exception e){
                    Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "checkOTPValidate(qrCodeResponseReceived) Exception = "+e);
                }
            }
        });
    }

    /*
    *
    * Response from otp received is true
    *
    * */
    private void otpDialogResult(QROTPResponseDto qrResponse) {
        try {

            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "otpDialogResult()  QROTPResponseDto =  " +qrResponse);

            TransactionBaseDto transaction;//Transaction base for sending data to server
            BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
            if (qrResponse.getUfc() != null)
                Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "otpDialogResult()  Response  ufc =  " +qrResponse.getUfc());

            QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrResponse.getUfc());
            if (qrCodeResponseReceived != null)
                Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "otpDialogResult()  Response  qrCodeResponseReceived =  " +qrCodeResponseReceived.toString());


            appState.refId = qrResponse.getReferenceId();
            if (qrCodeResponseReceived != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
//                qrCodeResponseReceived.setMode('Q');
                qrCodeResponseReceived.setMode('C');

                Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "otpDialogResult() Good  ");

                qrCodeResponseReceived.setOtpId(qrResponse.getOtpTransactionDto().getId());
                qrCodeResponseReceived.setOTP(qrResponse.getOtpTransactionDto().getOtp());
                transaction = new TransactionBaseDto();
                EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                transaction.setTransactionType(TransactionTypes.SALE_RMN_AUTHENTICATE);
                //transaction.setType("com.omneagate.rest.dto.RMNRequestDto");
                TransactionBase.getInstance().setTransactionBase(transaction);
                //startActivity(new Intent(this, SalesEntryActivity.class));

                Intent intent = new Intent(this, SalesEntryActivity.class);
                intent.putExtra("SaleType", "OTPSale");
                startActivity(intent);
                finish();
            } else {
                Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "otpDialogResult() Internal Error in entitlement =  ");

                Util.messageBar(this, getString(R.string.connectionError));
            }
        } catch (Exception e) {

            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "otpDialogResult() Exception Internal Error in entitlement =  ");

            Util.messageBar(this, getString(R.string.connectionError));
        }
    }

    @Override
    public void onBackPressed() {

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "onBackPressed() called ");
        startActivity(new Intent(this, MobileOTPOptionsActivity.class));
        finish();
    }

    /*
    *
    * QrCode response from server for respective card
    *
    * */
    private void QRCodeResponseReceived(Bundle message) {
        try {

            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "QRCodeResponseReceived() message  =  "+message);


            String response = message.getString(FPSDBConstants.RESPONSE_DATA);

            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "QRCodeResponseReceived() response  =  "+response);

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            QROTPResponseDto qrCodeResponseReceived = gson.fromJson(response,
                    QROTPResponseDto.class);
            if (qrCodeResponseReceived.getStatusCode() == 0) {
                qrResponseData(qrCodeResponseReceived);
            } else {
                String messages = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(qrCodeResponseReceived.getStatusCode()));
                Util.LoggingQueue(this, "Mobile Need OTP", "Error msg:" + messages);
                // Util.messageBar(this, messages);
                errorNavigation(messages);
            }



        } catch (Exception e) {

            Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "QRCodeResponseReceived() Exception  =  "+e);


        }

    }

    /*
    *
    * Error navigation  pages
    * */
    private void errorNavigation(String messages) {

        Util.LoggingQueue(MobileOTPNeedActivity.this, "MobileOTPNeedActivity", "errorNavigation() messages  =  "+messages);

        if (StringUtils.isEmpty(messages)) {
            messages = getString(R.string.error_otp_create);
        }
        Intent intent = new Intent(this, SuccessFailureSalesActivity.class);
        intent.putExtra("message", messages);
        startActivity(intent);
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
