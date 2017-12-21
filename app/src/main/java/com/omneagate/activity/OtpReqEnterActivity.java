package com.omneagate.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProxyDetailDto;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.Util.BeneficiarySalesTransaction;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.TLSSocketFactory;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.EncrypterUtil;
import com.omneagate.activity.dialog.OtpFailedDialog;
import com.omneagate.activity.dialog.OtpSuccessDialog;
import com.omneagate.activity.dialog.SessionKeyDetailsUtil;
import com.omneagate.service.HttpClientWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class OtpReqEnterActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    String fingerPrintAadhar = "", mobileNo = "";
    /*private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    UsbManager mManager;
    long gbldevice = 0;
    int gblquality = 0;
    int StopClick = 0;
    int busy = 1;
    byte[] rawdata;
    byte[] Enroll_Template;
    int mfsVer = 31;
    SharedPreferences settings;
    Context context;
    CommonMethod.ScannerAction scannerAction = CommonMethod.ScannerAction.Capture;
    int width = 316;
    int height = 354;
    Bitmap bitmapImg;
    int minQuality = 60;
    int timeout = 10000;
    byte[] isoFeatureSet;
    UsbDeviceConnection mDeviceConnection;
    UsbInterface mInterface;*/
    Date posReqDate;
    int retryCount = 0;
    RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;
    KeyboardView keyview, keyboardViewAlpha;
    KeyBoardEnum keyBoardFocused;
    EditText otpEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.otp_request);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpInitialScreen();
    }

    private void setUpInitialScreen() {
        setUpPopUpPage();
        otpEt = (EditText) findViewById(R.id.enterOtpEt);
        otpEt.setOnClickListener(this);
        otpEt.setShowSoftInputOnFocus(false);
        otpEt.setOnFocusChangeListener(OtpReqEnterActivity.this);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            posReqDate = sdf.parse(dateString);
        } catch (Exception e) {
        }
        // Copying folder in asset to sd card
        try {
            String destDir = Environment.getExternalStorageDirectory().toString() + "/Fps/";
            copyAssetToDir(OtpReqEnterActivity.this.getAssets(), "uidai_auth_prod.cer", destDir);
        } catch (Exception e) {
            Log.e("Mfs", "copy asset exc..." + e);
        }
        /*settings = PreferenceManager.getDefaultSharedPreferences(this);
        mfsVer = 31;
        mManager = ((UsbManager) getSystemService(Context.USB_SERVICE));
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                FindDeviceAndRequestPermission();
            }
        });
        trd.start();*/
        CommonMethod.DeleteDirectory();
        CommonMethod.CreateDirectory();

        Util.LoggingQueue(this, "OtpReqEnterActivity", "Setting up OtpReqEnterActivity");
//        String sNo = getIntent().getStringExtra("BenefMemberSno");
        String rcNo = getIntent().getStringExtra("RcNumber");
        String memberType = getIntent().getStringExtra("MemberType");
        String benefId = getIntent().getStringExtra("BenefId");
        String proxyId = getIntent().getStringExtra("ProxyId");
        BeneficiaryDto benef = FPSDBHelper.getInstance(OtpReqEnterActivity.this).beneficiaryFromOldCard(rcNo);
        if (memberType.equalsIgnoreCase("Beneficiary")) {
            BeneficiaryMemberDto beneficiaryMemberDto = FPSDBHelper.getInstance(OtpReqEnterActivity.this).getSpecificBeneficiaryMember(benef.getFamilyHeadAadharNumber());
            fingerPrintAadhar = beneficiaryMemberDto.getUid();
            mobileNo = benef.getMobileNumber();
            ((TextView) findViewById(R.id.benefNameTv)).setText(beneficiaryMemberDto.getName());
        } else if (memberType.equalsIgnoreCase("Proxy")) {
            ProxyDetailDto proxyDetailsDto = FPSDBHelper.getInstance(OtpReqEnterActivity.this).retrieveSpecificProxy(proxyId, benefId);
            fingerPrintAadhar = proxyDetailsDto.getUid();
            mobileNo = proxyDetailsDto.getMobile();
            ((TextView) findViewById(R.id.benefNameTv)).setText(proxyDetailsDto.getName());

        }

//        beneficiaryId = FPSDBHelper.getInstance(OtpReqEnterActivity.this).retrieveBeneficiaryId(fingerPrintAadhar);
        String cardType = FPSDBHelper.getInstance(OtpReqEnterActivity.this).getCardTypeFromId(benef.getCardTypeId());
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sales_top_heading);
        Util.setTamilText((TextView) findViewById(R.id.rcNoHeading), R.string.ration_card_number1);
        Util.setTamilText((TextView) findViewById(R.id.rcTypeHeading), R.string.ration_card_type);
        Util.setTamilText((TextView) findViewById(R.id.otpHeadingTv), R.string.enter_otp_here);
        Util.setTamilText((TextView) findViewById(R.id.otpRequestHeadingTv), R.string.request_otp);
        ((TextView) findViewById(R.id.rcNoValue)).setText(benef.getOldRationNumber());
        ((TextView) findViewById(R.id.rcTypeValue)).setText(cardType);
        ((TextView) findViewById(R.id.aadharTv)).setText(fingerPrintAadhar);
        ((TextView) findViewById(R.id.mobileTv)).setText(mobileNo);
        Util.setTamilText((TextView) findViewById(R.id.authHeading), R.string.authentication);
        Util.setTamilText((Button) findViewById(R.id.btnSubmit), R.string.submit);
        Util.setTamilText((Button) findViewById(R.id.btnCancel), R.string.cancel);
        Util.setTamilText((Button) findViewById(R.id.btnRequestOtp), R.string.request_otp);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btnRequestOtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                otpRequestProcess();

            }
        });
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otptext = otpEt.getText().toString().trim();
                if (otptext.length() > 0)
                    otpAuthenticationProcess();
            }
        });
        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
        keyboardumber = (RelativeLayout) findViewById(R.id.keyboardNumber);
        keyboardAlpha = (RelativeLayout) findViewById(R.id.keyboardAlpha);
        Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
        Keyboard keyboardAlp = new Keyboard(this, R.layout.keyboard_alpha);
        //create KeyboardView object
        keyview = (KeyboardView) findViewById(R.id.customkeyboard);
        keyboardViewAlpha = (KeyboardView) findViewById(R.id.customkeyboardAlpha);
        keyboardViewAlpha.setKeyboard(keyboardAlp);
        //attache the keyboard object to the KeyboardView object
        keyview.setKeyboard(keyboard);
        //show the keyboard
        keyview.setVisibility(KeyboardView.VISIBLE);
        keyboardViewAlpha.setVisibility(KeyboardView.VISIBLE);
        keyview.setPreviewEnabled(false);
        keyboardViewAlpha.setPreviewEnabled(false);
        //take the keyboard to the front
        keyview.bringToFront();
        keyboardViewAlpha.bringToFront();
        //register the keyboard to receive the key pressed
        keyview.setOnKeyboardActionListener(new KeyList());
        keyboardViewAlpha.setOnKeyboardActionListener(new KeyListAlpha());

        listenersForEditText();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enterOtpEt) {
            checkVisibility();
            keyBoardAppear();
            otpEt.requestFocus();
            keyBoardFocused = KeyBoardEnum.OTPNUMBER;
            changeLayout(true);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.enterOtpEt && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            otpEt.requestFocus();
            keyBoardFocused = KeyBoardEnum.OTPNUMBER;
            changeLayout(true);
        }
    }

    private void listenersForEditText() {
        otpEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void otpRequestProcess() {
        try {
            progressBar = new CustomProgressDialog(OtpReqEnterActivity.this);
            progressBar.setCanceledOnTouchOutside(false);
            if (NetworkUtil.getConnectivityStatus(OtpReqEnterActivity.this) == 0) {
                /*retryCount++;
                if (retryCount >= 2) {
                    String rationCardNo = getIntent().getStringExtra("RcNumber");
                    new BiometricAuthRetryFailedDialog(OtpReqEnterActivity.this, fingerPrintAadhar, rationCardNo).show();
                }
                else {
                    new BiometricAuthRetryDialog(OtpReqEnterActivity.this, retryCount).show();
                }*/
//                Util.messageBar(OtpReqEnterActivity.this, getString(R.string.no_connectivity));
                String rcNo = getIntent().getStringExtra("RcNumber");
                new OtpFailedDialog(OtpReqEnterActivity.this, rcNo).show();
//                return;
               /* Intent intent = new Intent(OtpReqEnterActivity.this, MobileOTPNeedActivity.class);
                intent.putExtra("AadharNumber", fingerPrintAadhar);
                String rationCardNo = getIntent().getStringExtra("RcNumber");
                intent.putExtra("RationCardNumber", rationCardNo);
                intent.putExtra("AadharLinked", true);
                OtpReqEnterActivity.this.startActivity(intent);*/
            } else {
                progressBar.show();
                sendOTPRequest();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void otpAuthenticationProcess() {
        try {
            progressBar = new CustomProgressDialog(OtpReqEnterActivity.this);
            progressBar.setCanceledOnTouchOutside(false);
            if (NetworkUtil.getConnectivityStatus(OtpReqEnterActivity.this) == 0) {
                /*retryCount++;
                if (retryCount >= 2) {
                    String rationCardNo = getIntent().getStringExtra("RcNumber");
                    new BiometricAuthRetryFailedDialog(OtpReqEnterActivity.this, fingerPrintAadhar, rationCardNo).show();
                }
                else {
                    new BiometricAuthRetryDialog(OtpReqEnterActivity.this, retryCount).show();
                }*/
//                Util.messageBar(OtpReqEnterActivity.this, getString(R.string.no_connectivity));
                String rcNo = getIntent().getStringExtra("RcNumber");
                new OtpFailedDialog(OtpReqEnterActivity.this, rcNo).show();
//                return;
               /* Intent intent = new Intent(OtpReqEnterActivity.this, MobileOTPNeedActivity.class);
                intent.putExtra("AadharNumber", fingerPrintAadhar);
                String rationCardNo = getIntent().getStringExtra("RcNumber");
                intent.putExtra("RationCardNumber", rationCardNo);
                intent.putExtra("AadharLinked", true);
                OtpReqEnterActivity.this.startActivity(intent);*/
            } else {
                progressBar.show();
                sendOTPAuthentication();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void sendOTPRequest() {
        try {
            HttpStack stack = null;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            }
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(OtpReqEnterActivity.this, stack);
            String encodedBioMetricInfo = null;
            String pidXml;
//            encodedBioMetricInfo = Base64.encodeToString(isoFeatureSet, Base64.DEFAULT);
            encodedBioMetricInfo = getIntent().getStringExtra("EncodedBiometric");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            final Date authReqDate = sdf.parse(dateString);
//            pidXml = buildPidXml(dateString, encodedBioMetricInfo, null);
            pidXml = buildPidXmlForOtpRequest(dateString, encodedBioMetricInfo, null);
            String fileNameWithPath = Environment.getExternalStorageDirectory().toString() + "/Fps/" + "uidai_auth_prod.cer";
            EncrypterUtil encrypterUtil = new EncrypterUtil(fileNameWithPath);
            byte[] sessionKey = encrypterUtil.generateSessionKey();
            byte[] encryptedSessionKey = encrypterUtil.encryptUsingPublicKey(sessionKey);
            SessionKeyDetailsUtil sessionKeyDetails = SessionKeyDetailsUtil.createNormalSkey(encryptedSessionKey);
            byte[] pidXmlBytes = pidXml.getBytes();
            byte[] encXMLPIDData = encrypterUtil.encryptUsingSessionKey(sessionKey, pidXmlBytes);
            byte[] hmac = generateSha256Hash(pidXmlBytes);
            byte[] encryptedHmacBytes = encrypterUtil.encryptUsingSessionKey(sessionKey, hmac);
            String certificateIdentifier = encrypterUtil.getCertificateIdentifier();
            JSONObject jsonData = new JSONObject();
            jsonData.put("Uid", fingerPrintAadhar);
            jsonData.put("TerminalId", "public");
            jsonData.put("EncryptedPid", Base64.encodeToString(encXMLPIDData, Base64.DEFAULT));
            jsonData.put("EncryptedHmac", Base64.encodeToString(encryptedHmacBytes, Base64.DEFAULT));
            jsonData.put("Ci", certificateIdentifier);
            jsonData.put("Ts", dateString);
            jsonData.put("EncryptedSessionKey", Base64.encodeToString(sessionKeyDetails.getSkeyValue(), Base64.DEFAULT));
            jsonData.put("Fdc", "NC");
            jsonData.put("Lov", "560103");
            jsonData.put("PublicIp", "127.0.0.1");
            jsonData.put("Udc", "MTA-231755");
            if (false)
                jsonData.put("IsKyc", "true");
            else
                jsonData.put("IsKyc", "false");
            jsonData.put("securityToken", "KREBSDOUWENVASHUWBKJBSDFINAHUB8713");
            jsonData.put("clientId", "1");
            final String json = jsonData.toString();
            Log.e("Request", "Request json " + json);
//            final String uriString = "https://devkua.finahub.com/KUAServer/otp";
            final String uriString = "https://esignprod.finahub.com/KREServer/otp";
            final JSONObject jsonBody = new JSONObject(json);
            JsonObjectRequest req = new JsonObjectRequest(uriString, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    Log.e("Response", "Response is " + response.toString());
                    try {
                        String status = (String) response.get("Status");
                        if (status.equalsIgnoreCase("Y")) {
                            Toast.makeText(OtpReqEnterActivity.this, "OTP generated successfully.", Toast.LENGTH_LONG).show();
//                            new BiometricAuthRetryDialog(OtpReqEnterActivity.this, retryCount).show();
                        } else if (status.equalsIgnoreCase("N")) {
//                            Toast.makeText(OtpReqEnterActivity.this, "OTP not generated. Try again.", Toast.LENGTH_LONG).show();
                            String rcNo = getIntent().getStringExtra("RcNumber");
                            new OtpFailedDialog(OtpReqEnterActivity.this, rcNo).show();
                        }
                        /*Insert datas into biometric_authentication table*/
//                       POSAadharAuthRequestDto posAadharAuthRequestDto = new POSAadharAuthRequestDto();
//                        posAadharAuthRequestDto.setUid(fingerPrintAadhar);
//                        posAadharAuthRequestDto.setFpsId(SessionId.getInstance().getFpsId());
//                        posAadharAuthRequestDto.setBeneficiaryId(Long.valueOf(beneficiaryId));
//                        posAadharAuthRequestDto.setAuthReponse(response.toString());
//                        if (status.equalsIgnoreCase("Y")) {
//                            posAadharAuthRequestDto.setAuthenticationStatus(true);
//                        }
//                        else if (status.equalsIgnoreCase("N")) {
//                            posAadharAuthRequestDto.setAuthenticationStatus(false);
//                        }
//                        posAadharAuthRequestDto.setPosRequestDate(posReqDate.getTime());
//                        posAadharAuthRequestDto.setAuthRequestDate(authReqDate.getTime());
//                        try {
//                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                            GregorianCalendar gc = new GregorianCalendar();
//                            String dateString = sdf.format(gc.getTime());
//                            Date authRespDate = sdf.parse(dateString);
//                            posAadharAuthRequestDto.setAuthResponseDate(authRespDate.getTime());
//                            posAadharAuthRequestDto.setPosResponseDate(authRespDate.getTime());
//                        }
//                        catch(Exception e) {}
//                        posAadharAuthRequestDto.setFingerPrintData(isoFeatureSet);
//                        FPSDBHelper.getInstance(OtpReqEnterActivity.this).insertBiometric(posAadharAuthRequestDto);
//                        dismissProgress();
//                        if (status.equalsIgnoreCase("Y")) {
//                            String rationCardNumber = FPSDBHelper.getInstance(OtpReqEnterActivity.this).getRationCardNumber(beneficiaryId);
//                            Log.e("Mfs", "rationCardNumber..." + rationCardNumber);
//                            getEntitlement(rationCardNumber);
//                            fingerPrintAadhar = "";
//                            beneficiaryId = "";
//                        }
//                        else {
//                            String errorCode = (String) response.get("ErrorCode");
//                            if (errorCode.equalsIgnoreCase("300")) {
//                                new BiometricAlertDialog(GlobalAppState.getInstance().getBaseContext(), getString(R.string.finger_print_mismatch)).show();
//                            }
//                            else {
//                                new BiometricAlertDialog(GlobalAppState.getInstance().getBaseContext(), getString(R.string.connectionError)).show();
//                            }
//                            retryCount++;
//                            if (retryCount >= 2) {
//                                String rationCardNo = getIntent().getStringExtra("RcNumber");
//                                new BiometricAuthRetryFailedDialog(OtpReqEnterActivity.this, fingerPrintAadhar, rationCardNo).show();
//                            }
//                            else {
//                                new BiometricAuthRetryDialog(OtpReqEnterActivity.this, retryCount).show();
//                            }
//                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                    dismissProgress();
                }
            },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgress();
                            Log.e("Response Error", "Response  Error is " + error.toString());
//                            Toast.makeText(OtpReqEnterActivity.this, "OTP not generated. Try again.", Toast.LENGTH_LONG).show();
                            String rcNo = getIntent().getStringExtra("RcNumber");
                            new OtpFailedDialog(OtpReqEnterActivity.this, rcNo).show();
                            /*try {
                                // Insert datas into biometric_authentication table
                                POSAadharAuthRequestDto posAadharAuthRequestDto = new POSAadharAuthRequestDto();
                                posAadharAuthRequestDto.setUid(fingerPrintAadhar);
                                posAadharAuthRequestDto.setFpsId(SessionId.getInstance().getFpsId());
                                posAadharAuthRequestDto.setBeneficiaryId(Long.valueOf(beneficiaryId));
                                posAadharAuthRequestDto.setAuthReponse(error.toString());
                                posAadharAuthRequestDto.setAuthenticationStatus(false);
                                posAadharAuthRequestDto.setPosRequestDate(posReqDate.getTime());
                                posAadharAuthRequestDto.setAuthRequestDate(authReqDate.getTime());
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    GregorianCalendar gc = new GregorianCalendar();
                                    String dateString = sdf.format(gc.getTime());
                                    Date authRespDate = sdf.parse(dateString);
                                    posAadharAuthRequestDto.setAuthResponseDate(authRespDate.getTime());
                                    posAadharAuthRequestDto.setPosResponseDate(authRespDate.getTime());
                                }
                                catch(Exception e) {}
                                posAadharAuthRequestDto.setFingerPrintData(isoFeatureSet);
                                FPSDBHelper.getInstance(OtpReqEnterActivity.this).insertBiometric(posAadharAuthRequestDto);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            retryCount++;
                            if (retryCount >= 2) {
                                String rationCardNo = getIntent().getStringExtra("RcNumber");
                                new BiometricAuthRetryFailedDialog(OtpReqEnterActivity.this, fingerPrintAadhar, rationCardNo).show();
                            }
                            else {
                                new BiometricAuthRetryDialog(OtpReqEnterActivity.this, retryCount).show();
                            }*/
                        }
                    });
            queue.add(req);
        } catch (Exception e) {
            Log.e("Mfs", "authentication exc...." + e.toString());
            dismissProgress();
            e.printStackTrace();
        }
    }

    private void sendOTPAuthentication() {
        try {
            HttpStack stack = null;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                stack = new HurlStack();
            }
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(OtpReqEnterActivity.this, stack);
            String encodedBioMetricInfo = null;
            String pidXml;
//            encodedBioMetricInfo = Base64.encodeToString(isoFeatureSet, Base64.DEFAULT);
//            encodedBioMetricInfo = getIntent().getStringExtra("EncodedBiometric");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            final Date authReqDate = sdf.parse(dateString);
            String otptext = otpEt.getText().toString().trim();
            pidXml = buildPidXmlForOtpAuthentication(dateString, otptext);
            String fileNameWithPath = Environment.getExternalStorageDirectory().toString() + "/Fps/" + "uidai_auth_encrypt_preprod.cer";
            EncrypterUtil encrypterUtil = new EncrypterUtil(fileNameWithPath);
            byte[] sessionKey = encrypterUtil.generateSessionKey();
            byte[] encryptedSessionKey = encrypterUtil.encryptUsingPublicKey(sessionKey);
            SessionKeyDetailsUtil sessionKeyDetails = SessionKeyDetailsUtil.createNormalSkey(encryptedSessionKey);
            byte[] pidXmlBytes = pidXml.getBytes();
            byte[] encXMLPIDData = encrypterUtil.encryptUsingSessionKey(sessionKey, pidXmlBytes);
            byte[] hmac = generateSha256Hash(pidXmlBytes);
            byte[] encryptedHmacBytes = encrypterUtil.encryptUsingSessionKey(sessionKey, hmac);
            String certificateIdentifier = encrypterUtil.getCertificateIdentifier();
            JSONObject jsonData = new JSONObject();
            jsonData.put("Uid", fingerPrintAadhar);
            jsonData.put("TerminalId", "public");
            jsonData.put("EncryptedPid", Base64.encodeToString(encXMLPIDData, Base64.DEFAULT));
            jsonData.put("EncryptedHmac", Base64.encodeToString(encryptedHmacBytes, Base64.DEFAULT));
            jsonData.put("Ci", certificateIdentifier);
            jsonData.put("Ts", dateString);
            jsonData.put("EncryptedSessionKey", Base64.encodeToString(sessionKeyDetails.getSkeyValue(), Base64.DEFAULT));
            jsonData.put("Fdc", "NC");
            jsonData.put("Lov", "560103");
            jsonData.put("PublicIp", "127.0.0.1");
            jsonData.put("Udc", "MTA-231755");
            jsonData.put("bio", "N");
            jsonData.put("otp", "Y");

            jsonData.put("IsKyc", "true");

            jsonData.put("securityToken", "KREBSDOUWENVASHUWBKJBSDFINAHUB8713");
            jsonData.put("clientId", "1");
            final String json = jsonData.toString();
            Log.e("Request", "Request json " + json);
//            final String uriString = "https://devkua.finahub.com/KUAServer/kyc";
            final String uriString = "https://esignprod.finahub.com/KREServer/kyc";
            final JSONObject jsonBody = new JSONObject(json);
            JsonObjectRequest req = new JsonObjectRequest(uriString, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    Log.e("Response", "otp Response is " + response.toString());
                    try {
                        String status = (String) response.get("Status");
                        if (status.equalsIgnoreCase("Y")) {
                            String rcNo = getIntent().getStringExtra("RcNumber");
                            new OtpSuccessDialog(OtpReqEnterActivity.this, rcNo).show();
                        } else {
                            String rcNo = getIntent().getStringExtra("RcNumber");
                            new OtpFailedDialog(OtpReqEnterActivity.this, rcNo).show();
                        }
                        // Insert datas into biometric_authentication table
                        /*POSAadharAuthRequestDto posAadharAuthRequestDto = new POSAadharAuthRequestDto();
                        posAadharAuthRequestDto.setUid(fingerPrintAadhar);
                        posAadharAuthRequestDto.setFpsId(SessionId.getInstance().getFpsId());
                        posAadharAuthRequestDto.setBeneficiaryId(Long.valueOf(beneficiaryId));
                        posAadharAuthRequestDto.setAuthReponse(response.toString());
                        if (status.equalsIgnoreCase("Y")) {
                            posAadharAuthRequestDto.setAuthenticationStatus(true);
                        }
                        else if (status.equalsIgnoreCase("N")) {
                            posAadharAuthRequestDto.setAuthenticationStatus(false);
                        }
                        posAadharAuthRequestDto.setPosRequestDate(posReqDate.getTime());
                        posAadharAuthRequestDto.setAuthRequestDate(authReqDate.getTime());
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            GregorianCalendar gc = new GregorianCalendar();
                            String dateString = sdf.format(gc.getTime());
                            Date authRespDate = sdf.parse(dateString);
                            posAadharAuthRequestDto.setAuthResponseDate(authRespDate.getTime());
                            posAadharAuthRequestDto.setPosResponseDate(authRespDate.getTime());
                        }
                        catch(Exception e) {}
                        posAadharAuthRequestDto.setFingerPrintData(isoFeatureSet);
                        FPSDBHelper.getInstance(OtpReqEnterActivity.this).insertBiometric(posAadharAuthRequestDto);
                        dismissProgress();
                        if (status.equalsIgnoreCase("Y")) {
                            String rationCardNumber = FPSDBHelper.getInstance(OtpReqEnterActivity.this).getRationCardNumber(beneficiaryId);
                            Log.e("Mfs", "rationCardNumber..." + rationCardNumber);
                            getEntitlement(rationCardNumber);
                            fingerPrintAadhar = "";
                            beneficiaryId = "";
                        }
                        else {
                            String errorCode = (String) response.get("ErrorCode");
                            if (errorCode.equalsIgnoreCase("300")) {
                                new BiometricAlertDialog(GlobalAppState.getInstance().getBaseContext(), getString(R.string.finger_print_mismatch)).show();
                            }
                            else {
                                new BiometricAlertDialog(GlobalAppState.getInstance().getBaseContext(), getString(R.string.connectionError)).show();
                            }
                            retryCount++;
                            if (retryCount >= 2) {
                                String rationCardNo = getIntent().getStringExtra("RcNumber");
                                new BiometricAuthRetryFailedDialog(OtpReqEnterActivity.this, fingerPrintAadhar, rationCardNo).show();
                            }
                            else {
                                new BiometricAuthRetryDialog(OtpReqEnterActivity.this, retryCount).show();
                            }
                        }*/
                    } catch (JSONException e) {
                        dismissProgress();
                        e.printStackTrace();
                    }
                }
            },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgress();
                            Log.e("Response Error", "otp Response  Error is " + error.toString());
                            String rcNo = getIntent().getStringExtra("RcNumber");
                            new OtpFailedDialog(OtpReqEnterActivity.this, rcNo).show();
                            /*try {
                                // Insert datas into biometric_authentication table
                                POSAadharAuthRequestDto posAadharAuthRequestDto = new POSAadharAuthRequestDto();
                                posAadharAuthRequestDto.setUid(fingerPrintAadhar);
                                posAadharAuthRequestDto.setFpsId(SessionId.getInstance().getFpsId());
                                posAadharAuthRequestDto.setBeneficiaryId(Long.valueOf(beneficiaryId));
                                posAadharAuthRequestDto.setAuthReponse(error.toString());
                                posAadharAuthRequestDto.setAuthenticationStatus(false);
                                posAadharAuthRequestDto.setPosRequestDate(posReqDate.getTime());
                                posAadharAuthRequestDto.setAuthRequestDate(authReqDate.getTime());
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    GregorianCalendar gc = new GregorianCalendar();
                                    String dateString = sdf.format(gc.getTime());
                                    Date authRespDate = sdf.parse(dateString);
                                    posAadharAuthRequestDto.setAuthResponseDate(authRespDate.getTime());
                                    posAadharAuthRequestDto.setPosResponseDate(authRespDate.getTime());
                                }
                                catch(Exception e) {}
                                posAadharAuthRequestDto.setFingerPrintData(isoFeatureSet);
                                FPSDBHelper.getInstance(OtpReqEnterActivity.this).insertBiometric(posAadharAuthRequestDto);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            retryCount++;
                            if (retryCount >= 2) {
                                String rationCardNo = getIntent().getStringExtra("RcNumber");
                                new BiometricAuthRetryFailedDialog(OtpReqEnterActivity.this, fingerPrintAadhar, rationCardNo).show();
                            }
                            else {
                                new BiometricAuthRetryDialog(OtpReqEnterActivity.this, retryCount).show();
                            }*/
                        }
                    });
            queue.add(req);
        } catch (Exception e) {
            Log.e("Mfs", "authentication exc...." + e.toString());
            dismissProgress();
            e.printStackTrace();
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            default:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BenefProxyDetailsActivity.class);
        String rcNo = getIntent().getStringExtra("RcNumber");
        intent.putExtra("RcNumber", rcNo);
        startActivity(intent);
        finish();
    }

    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Intent intent = new Intent(this, SuccessFailureSalesActivity.class);
        Util.LoggingQueue(this, "Error in QRcode", "Navigating to error page");
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    public static void copyAssetToDir(AssetManager assetManager, String assetName, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String dirLastName = assetName;
        int lastIndex = dirLastName.lastIndexOf(File.separator);
        if (-1 < lastIndex) {
            dirLastName = dirLastName.substring(lastIndex + 1);
        }
        String destSubDir = destDir + File.separator + dirLastName;

        String[] assets = assetManager.list(assetName);
        if (0 == assets.length) {
            //It's a file
            copyFileAsset(assetManager, assetName, destDir);
        } else {
            //It's a directory
            for (String asset : assets) {
                copyAssetToDir(assetManager, assetName + File.separator + asset, destSubDir);
            }
        }
    }

    private static void copyFileAsset(AssetManager assetManager, String assetName, String destDir) throws IOException {
        String assetLastName = assetName;
        int lastIndex = assetLastName.lastIndexOf(File.separator);
        if (-1 < lastIndex) {
            assetLastName = assetLastName.substring(lastIndex + 1);
        }

        InputStream in = assetManager.open(assetName);
        String newFileName = destDir + File.separator + assetLastName;
        OutputStream out = new FileOutputStream(newFileName);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        in = null;
        out.flush();
        out.close();
        out = null;
    }

    public byte[] generateSha256Hash(byte[] message) {
        String algorithm = "SHA-256";
        String SECURITY_PROVIDER = "BC";
        byte[] hash = null;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm, SECURITY_PROVIDER);
            digest.reset();
            hash = digest.digest(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }

    private String buildPidXmlForOtpRequest(String timeStamp, String encodedBiometric, String encodedBiometric2ndFingur) {
        StringBuffer buff = new StringBuffer();
        buff.append("<Pid ts=");
        buff.append("\"" + timeStamp + "\"");
        buff.append(">");
        buff.append("<Bios>");
        buff.append("<Bio type=\"FMR\" posh=\"UNKNOWN\">");
        buff.append(encodedBiometric);
        buff.append("</Bio>");
        return buff.toString();
    }

    private String buildPidXmlForOtpAuthentication(String timeStamp, String otp) {
        StringBuffer buff = new StringBuffer();
        buff.append("<Pid ts=");
        buff.append("\"" + timeStamp + "\"");
        buff.append(" ver=");
        buff.append("\"" + "1.0" + "\"");
        buff.append(">");
        buff.append("<Pv otp=");
        buff.append("\"" + otp + "\"");
//        buff.append(" pin=");
//        buff.append("\"" + "" + "\"");
        buff.append("/>");
        buff.append("</Pid>");
        return buff.toString();
    }

    /*BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                FindDeviceAndRequestPermission();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                busy = 0;
                if (gbldevice != 0) {
                    mfs100api.MFS100StopXcan(gbldevice);
                    mfs100api.MFS100Uninit(gbldevice);
                    gbldevice = 0;

                }
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null
                                && (device.getVendorId() == 1204 || device
                                .getVendorId() == 11279)) {
                            if (device.getProductId() == 34323) {
                                // Loading Firmware..
                                mDeviceConnection = mManager.openDevice(device);
                                mInterface = device.getInterface(0);
                                load_firmware();
                            } else if (device.getProductId() == 4101) {
                                // Initializing Sensor..
                                mDeviceConnection = mManager.openDevice(device);
                                mInterface = device.getInterface(0);
                                Init_Sensor();
                            }
                        }
                    } else {
                        try {
                            Toast.makeText(context, "Permission Denied",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }
    };

    public void load_firmware() {
        try {
            long ret = mfs100api.MFS100LoadFirmware(mfsVer);
            if (ret == 0) {}
            else {
                Toast.makeText(this,"Load firmware failed, error " + String.valueOf(ret), Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Load firmware failed, unhandled exception", Toast.LENGTH_LONG).show();
        }
    }

    public void Init_Sensor() {
        try {
            int fd = mDeviceConnection.getFileDescriptor();
            byte[] serial = new byte[8];
            long devicevar = 0;
            devicevar = mfs100api.MFS100Init(serial, 80, 16384, mfsVer);
            int ret = mfs100api.MFS100LastErrorCode();
            if (ret != 0) {
                mfs100api.CheckError(ret);
                return;
            } else {
                gbldevice = devicevar;
            }
            String str = EncodingUtils.getAsciiString(serial, 0, 7);
            if (gbldevice == 0) {
                mfs100api.CheckError((int) gbldevice);
            } else {}
        } catch (Exception ex) {
            Toast.makeText(this, "Init failed, unhandled exception", Toast.LENGTH_LONG).show();
        }
    }

    public void UnInit_Scanner() {
        try {
            int ret = 0;
            ret = mfs100api.MFS100Uninit(gbldevice);
            if (ret != 0) {
                mfs100api.CheckError(ret);

            } else {
                gbldevice = 0;
            }
        }
        catch (Exception ex) {}
    }

    int FindDeviceAndRequestPermission() {
        settings = getSharedPreferences("url",0);
        mfsVer =settings.getInt("modelMantra",31);
        int rs = 0;
        try {
            HashMap<String, UsbDevice> deviceList = mManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                long productid = device.getProductId();
                long vendorid = device.getVendorId();
                if ((vendorid == 1204 || vendorid == 11279) && (productid == 34323 || productid == 4101)) {
                    PendingIntent mPermissionIntent = null;
                    mPermissionIntent = PendingIntent.getBroadcast(this, 0,
                            new Intent(ACTION_USB_PERMISSION), 0);
                    mManager.requestPermission(device, mPermissionIntent);

                    return 0;
                }
            }
            rs = -1;
            return rs;
        } catch (Exception ex) {
            return -1;
        }
    }*/

    public void getEntitlement(String qrCodeString) {
        progressBar = new CustomProgressDialog(this);
        try {
            progressBar.show();
            BeneficiaryDto benef = FPSDBHelper.getInstance(this).beneficiaryFromOldCard(qrCodeString);
            Log.e("RationCardSalesActivity", "BeneficiaryDto..." + benef.toString());
            if (benef != null) {
                BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
                Util.LoggingQueue(this, "Entitlement", "Calculating entitlement");
                QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeString);
                if (qrCodeResponseReceived != null)
                    Log.e("RationCardSalesActivity", "QRTransactionResponseDto..." + qrCodeResponseReceived.toString());
                if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
                    NetworkConnection network = new NetworkConnection(this);
                    if (network.isNetworkAvailable()) {
                        qrCodeResponseReceived.setMode('C');
                    } else {
                        qrCodeResponseReceived.setMode('F');
                    }
                    Util.LoggingQueue(OtpReqEnterActivity.this, "Ration card Sales", "Moving to Sales Entry Page");
                    qrCodeResponseReceived.setRegistered(true);
                    EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                    startActivity(new Intent(this, SalesEntryActivity.class));
                    finish();
                    /*Intent intent = new Intent(this, SalesEntryActivity.class);
                    intent.putExtra("SaleType", "AadharCardSale");
                    startActivity(intent);
                    finish();*/
                } else if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() == 0) {
                    Util.LoggingQueue(OtpReqEnterActivity.this, "Ration card Sales", "Beneficiary Entitlement details not available");
                    errorNavigation(getString(R.string.entitlemnt_finished));
                } else {
                    Util.LoggingQueue(OtpReqEnterActivity.this, "Ration card Sales", "Beneficiary Entitlement details not available");
                    errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
                }
            } else {
                Util.LoggingQueue(OtpReqEnterActivity.this, "Ration card Sales", "Beneficiary Data is not available in db");
                errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.getStackTrace().toString());
            Log.e("RationCardSalesActivity", e.toString(), e);
            errorNavigation(getString(R.string.invalid_card_no));
        } finally {
            if (progressBar != null)
                progressBar.dismiss();
        }
    }

    private void keyBoardAppear() {
        keyboardumber.setVisibility(View.VISIBLE);
        keyboardAlpha.setVisibility(View.GONE);
    }

    private void changeKeyboard() {
        try {
            keyboardumber.setVisibility(View.GONE);
            keyboardAlpha.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("Error", "keyboard");
        }
    }

    private void changeLayout(boolean value) {
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.proxyLayoutMaster);
        relativelayout.removeView(keyBoardCustom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (value) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.leftMargin = 30;
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.rightMargin = 30;
        }
        lp.bottomMargin = 30;
        keyBoardCustom.setPadding(10, 10, 10, 10);
        relativelayout.addView(keyBoardCustom, lp);
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }

    class KeyList implements KeyboardView.OnKeyboardActionListener {
        public void onKey(View v, int keyCode, KeyEvent event) {

        }

        public void onText(CharSequence text) {

        }

        public void swipeLeft() {

        }

        public void onKey(int primaryCode, int[] keyCodes) {

        }

        public void swipeUp() {

        }

        public void swipeDown() {

        }

        public void swipeRight() {

        }

        public void onPress(int primaryCode) {
            if (primaryCode == 8) {
                if (keyBoardFocused == KeyBoardEnum.OTPNUMBER) {
                    String text = otpEt.getText().toString();
                    if (otpEt.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        otpEt.setText(text);
                        otpEt.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);

            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.OTPNUMBER) {
                    otpEt.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    class KeyListAlpha implements KeyboardView.OnKeyboardActionListener {
        public void onKey(View v, int keyCode, KeyEvent event) {

        }

        public void onText(CharSequence text) {

        }

        public void swipeLeft() {

        }

        public void onKey(int primaryCode, int[] keyCodes) {

        }

        public void swipeUp() {

        }

        public void swipeDown() {

        }

        public void swipeRight() {

        }

        public void onPress(int primaryCode) {
            if (primaryCode == 8) {
                if (keyBoardFocused == KeyBoardEnum.OTPNUMBER) {
                    String text = otpEt.getText().toString();
                    if (otpEt.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        otpEt.setText(text);
                        otpEt.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                /*if (keyBoardFocused == KeyBoardEnum.CARDTYPE) {
                    otpEt.append("" + ch);
                }*/
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }


    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}