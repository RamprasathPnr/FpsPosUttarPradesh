package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.client.android.Intents;
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
import com.omneagate.Util.BeneficiarySalesQRTransaction;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.TransactionMessage;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;



//Beneficiary Activity to check Beneficiary Activation
public class QRCodeSalesActivity extends BaseActivity implements SMSListener, View.OnClickListener {

    final Handler handler = new Handler();
    TransactionBaseDto transaction;          //Transaction base DTO
    Timer timer;
    TimerTask timerTask;
    String number = "";
    private int count = 0;//Retry count
    private String qrCodeData = "";

    // Zbar variables
    /*private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_qrcode_otp);
//        setContentView(R.layout.main_zbar);

        Util.LoggingQueue(this, "QRCodeSalesActivity", "onCreate called ");

        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        appState = (GlobalAppState) getApplication();
        transaction = new TransactionBaseDto();
        setUpInitialScreen();
        launchQRScanner();
    }

    private void launchQRScanner() {
        /*autoFocusHandler = new Handler();
        mCamera = getCameraInstance();
        *//* Instance barcode scanner *//*
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);*/

        Util.LoggingQueue(this, "QRCodeSalesActivity", "QR scanner called");
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
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String contents = data.getStringExtra(Intents.Scan.RESULT);
                        Util.LoggingQueue(this, "QRCodeSalesActivity", "EncryptedUFC = " +contents);

                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(this, "QRCodeSalesActivity", "QR Exception" + e.toString());
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Util.LoggingQueue(this, "QRCodeSalesActivity", "Scan cancelled" );

                }

                break;

            default:
                break;
        }
    }

    private void setUpInitialScreen() {
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Util.LoggingQueue(this, "QRcode sales", "Setting up QRcode sales activity");
    }

    //Response from QR reader
    private void qrResponse(String result) {
        String languageCode = FPSDBHelper.getInstance(this).getMasterData("language");
        Util.changeLanguage(this, languageCode);
        GlobalAppState.language = languageCode;
        if (result == null) {

            Util.LoggingQueue(this, "QRCodeSalesActivity", "Scan Result is null = ");

            startActivity(new Intent(this, SaleOrderActivity.class));
            finish();
        } else {
            String qrCode = Util.DecryptedBeneficiary(this, result);
            if (StringUtils.isEmpty(qrCode)) {
                Util.LoggingQueue(this, "QRCodeSalesActivity ", "QRcode invalid  Scan Again");
                startActivity(new Intent(this, SaleOrderActivity.class));
                finish();
                return;
            }
            String lines[] = result.split("\\r?\\n");

            Util.LoggingQueue(this, "QRCodeSalesActivity", "Resulted ufc_code = " +lines[0]);

            getEntitlement(lines[0]);
        }
    }

    /**
     * Send FPS_ID and QRCode to get entitlement
     *
     * @params qrCode received from card
     */
    private void getEntitlement(String qrCodeString) {
        try {
            Util.LoggingQueue(this, "QRCodeSalesActivity", "getEntitlement() called qrCodeString = " +qrCodeString);

            BeneficiarySalesQRTransaction beneficiary = new BeneficiarySalesQRTransaction(this);
            Util.LoggingQueue(this, "QRCodeSalesActivity", "Resulted BeneficiaryDto = " +beneficiary.toString());

            QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeString);
            Util.LoggingQueue(this, "QRCodeSalesActivity", "Resulted QRTransactionResponseDto = " +qrCodeResponseReceived);


            if (qrCodeResponseReceived != null) {
               // Log.e("QRCodeSalesActivity", "QRTransactionResponseDto is not null = " + qrCodeResponseReceived.toString());
            }
            if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
//                qrCodeResponseReceived.setMode('Q');
                qrCodeResponseReceived.setMode('A');

                NetworkConnection network = new NetworkConnection(this);

                    if (network.isNetworkAvailable()) {
                        qrCodeResponseReceived.setMode('A');
                    } else {
                        qrCodeResponseReceived.setMode('E');
                    }




                EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                if (SessionId.getInstance().isQrOTPEnabled()) {
                    Util.LoggingQueue(this, "QRCodeSalesActivity", "isQrOTPEnabled() is true" + SessionId.getInstance().isQrOTPEnabled());

                    getOTPFromServer(qrCodeString);
                } else {
                    Util.LoggingQueue(this, "QRCodeSalesActivity", "isQrOTPEnabled() is false");

                    /** 11-07-2016
                     * MSFixes
                     * Added to get type of sale
                     *
                     */
                    Log.e("QRCodeSalesActivity", "Moving to SalesSummaryActivity with SaleType = QrCodeSale" );

                    Intent intent = new Intent(this, SalesSummaryActivity.class);
                    intent.putExtra("SaleType", "QrCodeSale");
                    startActivity(intent);
                    finish();
                }
            } else {
                Log.e("QRCodeSalesActivity", "Beneficiary Mismatch" );

                errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.getStackTrace().toString());
            Log.e("QRCodeSalesActivity", e.toString(), e);
            errorNavigation(getString(R.string.qrCodeInvalid));
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
                Util.messageBar(this, getString(R.string.connectionRefused));

                break;
        }
    }


    //QrCode response from server for respective card
    private void QRCodeResponseReceived(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Util.LoggingQueue(this, "OTP received", response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            QROTPResponseDto qrCodeResponseReceived = gson.fromJson(response,
                    QROTPResponseDto.class);
            if (qrCodeResponseReceived.getStatusCode() == 0) {
                qrResponseData(qrCodeResponseReceived);
            } else {

                String messages = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(qrCodeResponseReceived.getStatusCode()));
                Util.LoggingQueue(this, "Error in OTP", messages);
                errorNavigation(messages);
            }
        } catch (Exception e) {
            Log.e("QRCodeSalesActivity", e.toString(), e);
            Util.LoggingQueue(this, "Error in OTP", e.toString());
        }

    }

    /**
     * send request to get otp from server
     *
     * @param qrCodeString
     */
    private void getOTPFromServer(String qrCodeString) {
        try {
            Util.LoggingQueue(this, "QRCodeSalesActivity", "getOTPFromServer() is called  , qrCodeString = " + qrCodeString);

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
            if (networkConnection.isNetworkAvailable()) {
                if (SessionId.getInstance().getSessionId().length() > 0) {
                    String url = "/transaction/process";
                    Util.LoggingQueue(this, "QRCodeSalesActivity", "url = " + url);

                    String qrCodes = new Gson().toJson(base);
                    Util.LoggingQueue(this, "QRCodeSalesActivity", "toJson qrCodes = " + qrCodes);

                    StringEntity se = new StringEntity(qrCodes, HTTP.UTF_8);
                    progressBar = new CustomProgressDialog(this);
                    progressBar.setCancelable(false);
                    progressBar.show();
                    httpConnection.sendRequest(url, null, ServiceListenerType.QR_CODE,
                            SyncHandler, RequestType.POST, se, this);
                } else {
                    Util.LoggingQueue(this, "QRCodeSalesActivity", "ELSE SessionId.getInstance().getSessionId().length() = " + SessionId.getInstance().getSessionId().length());

                    sendBySMS(base, update);

                }
            } else {
                Util.LoggingQueue(this, "QRCodeSalesActivity", "ELSE networkConnection.isNetworkAvailable() = " + networkConnection.isNetworkAvailable());

                sendBySMS(base, update);
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "QRCodeSalesActivity", "qrCodes Exception = " + e.toString());

        }
    }

    private void sendBySMS(TransactionBaseDto base, UpdateStockRequestDto update) {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "sendBySMS() is called  TransactionBaseDto = " + base);
        Util.LoggingQueue(this, "QRCodeSalesActivity", "sendBySMS() is called  UpdateStockRequestDto = " + update);

        GlobalAppState.listener = this;
        checkMessage();
        progressBar = new CustomProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.show();
        Transaction trans = TransactionFactory.getTransaction(0);
        trans.process(this, base, update);
    }

    private void removeData(QROTPResponseDto qrResponse) {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "removeData() is called  QROTPResponseDto = " + qrResponse);

        progressBar.dismiss();
        Map<String, String> data = TransactionMessage.getInstance().getTransactionMessage();
        String value = data.get(qrResponse.getUfc());
        if (StringUtils.isNotEmpty(qrResponse.getUfc())) {
            data.remove(qrResponse.getUfc());
            qrResponseData(qrResponse);
        } else {
            errorNavigation(getString(R.string.connectionError));
        }
    }


    /*
* After qrCode sent to server and response received this function calls
* */
    private void qrResponseData(final QROTPResponseDto qrCodeResponseReceived) {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "qrResponseData() is called  QROTPResponseDto = " + qrCodeResponseReceived);

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
        findViewById(R.id.buttonNeedOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkOTPValidate(qrCodeResponseReceived)) {
                    otpDialogResult(qrCodeResponseReceived);
                }
            }
        });
    }

    private void removeNumber() {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "removeNumber() is called  ");

        if (number.length() > 0) {
            number = number.substring(0, number.length() - 1);
        } else {
            number = "";
        }
        setText();
    }

    private void checkMessage() {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "checkMessage() is called  ");

        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 60000);
    }

    public void initializeTimerTask() {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "initializeTimerTask() ");

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
        Util.LoggingQueue(this, "QRCodeSalesActivity", "stopTimerTask() is called  QROTPResponseDto = " + qrResponse);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        GlobalAppState.listener = null;
        removeData(qrResponse);
    }

    @Override
    public void onBackPressed() {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "onBackPressed() is called  ");

        startActivity(new Intent(this, SaleOrderActivity.class));
        finish();
    }

    @Override
    public void smsReceived(UpdateStockRequestDto stockRequestDto) {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "smsReceived() is called  UpdateStockRequestDto = " + stockRequestDto);

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

    //Response from otp received is true
    private void otpDialogResult(QROTPResponseDto qrResponse) {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "otpDialogResult() is called  QROTPResponseDto = " + qrResponse);

        BeneficiarySalesQRTransaction beneficiary = new BeneficiarySalesQRTransaction(this);
        QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeData);
        appState.refId = qrResponse.getReferenceId();
        Util.LoggingQueue(this, "QRCodeSalesActivity", "otpDialogResult() is called  QRTransactionResponseDto = " + qrCodeResponseReceived.toString());


        if (qrCodeResponseReceived != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
//            qrCodeResponseReceived.setMode('Q');
            qrCodeResponseReceived.setMode('A');
            EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
            //startActivity(new Intent(this, SalesEntryActivity.class));
            Util.LoggingQueue(this, "QRCodeSalesActivity", "otpDialogResult() is called  Moving to SalesEntryActivity  SaleType = QrCodeSale");

            Intent intent = new Intent(this, SalesEntryActivity.class);
            intent.putExtra("SaleType", "QrCodeSale");
            startActivity(intent);

            finish();
        } else {
            Util.LoggingQueue(this, "QRCodeSalesActivity", "otpDialogResult() Error in calculating Entitlement");

            errorNavigation(getString(R.string.internalError));
        }
    }


    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "errorNavigation() is called  messages = " + messages);

        Intent intent = new Intent(this, SuccessFailureSalesActivity.class);
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Util.LoggingQueue(this, "QRCodeSalesActivity", "onClick() is called  ");

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
        Util.LoggingQueue(this, "QRCodeSalesActivity", "addNumber() is called text   = " +text);

        try {
            if (number.length() >= 7) {
                return;
            }
            number = number + text;
            setText();
        } catch (Exception e) {
           // Log.e("QRCodeSalesActivity", e.toString(), e);
        }
    }

    private void setText() {



        Util.LoggingQueue(this, "QRCodeSalesActivity", "setText() is called   ");

        ((TextView) findViewById(R.id.mobileNumberOTP)).setText(number);
    }

    /**
     * OTP validation
     *
     * @param qrResponse received from server
     *                   return true if otp equals user entry else false
     */
    private boolean checkOTPValidate(QROTPResponseDto qrResponse) {

        Util.LoggingQueue(this, "QRCodeSalesActivity", "checkOTPValidate() is called  QROTPResponseDto =   "  +qrResponse );

        String oTP = number;
        if (StringUtils.isEmpty(oTP)) {

            Util.LoggingQueue(this, "QRCodeSalesActivity", "checkOTPValidate() Error in OTP , Empty OTP page =   oTP ="  +oTP );

            return false;
        }
        if (oTP.equals(qrResponse.getOtpTransactionDto().getOtp())) {
            return true;
        } else if (count == 3) {

            Util.LoggingQueue(this, "QRCodeSalesActivity", "checkOTPValidate() Error in OTP , Retry count exceed , count == 3"  +oTP );

            Util.messageBar(this, getString(R.string.retryCount));
            errorNavigation(getString(R.string.retryCount));
        } else {

            Util.LoggingQueue(this, "QRCodeSalesActivity", "checkOTPValidate() Error in OTP , Invalid page, count == 3"  +oTP );

            Util.messageBar(this, getString(R.string.mobileOTPWrong));
        }

        count++;
        return false;
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



    /*public void onPause() {
        super.onPause();
        releaseCamera();
    }

    *//** A safe way to get an instance of the Camera object. *//*
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                String content = "";
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
//                    scanText.setText("barcode result " + sym.getData());
                    content = sym.getData();
                    barcodeScanned = true;
                }
                if(!content.equalsIgnoreCase("")) {
                    qrResponse(content);
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };*/
}
