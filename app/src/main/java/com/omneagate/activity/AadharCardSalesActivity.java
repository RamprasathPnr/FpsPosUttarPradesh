package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mantra.mfs100.DeviceInfo;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.POSAadharAuthRequestDto;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.Util.BeneficiarySalesTransaction;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TLSSocketFactory;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.BiometricAlertDialog;
import com.omneagate.activity.dialog.EncrypterUtil;
import com.omneagate.activity.dialog.SessionKeyDetailsUtil;

import org.apache.commons.lang3.StringUtils;
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

public class AadharCardSalesActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener, MFS100Event {
    ImageView imgFinger;
    com.neopixl.pixlui.components.edittext.EditText aadharNumber1, aadharNumber2, aadharNumber3;
    KeyboardView keyview;
    KeyBoardEnum keyBoardFocused;
    RelativeLayout keyBoardCustom;
    String beneficiaryId = "", fingerPrintAadhar = "";
    Button btnStartCapture;
    TextView lblMessage;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    UsbManager mManager;
    long gbldevice = 0;
    int gblquality = 0;
    int StopClick = 0;
    int busy = 1;
    byte[] rawdata;
    byte[] Enroll_Template;
    int mfsVer = 41;
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
    UsbInterface mInterface;
    Date posReqDate, authReqDate;
    BiometricAlertDialog biometricAlertDialog;
    String TAG = "AadharCardSalesActivity";
    DeviceInfo deviceIfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_aadhar_card_sales);
        Util.LoggingQueue(this, "AadharCardSalesActivity ", "onCreate called");
        context = AadharCardSalesActivity.this.getApplicationContext();


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
            copyAssetToDir(AadharCardSalesActivity.this.getAssets(), "uidai_auth_encrypt_preprod.cer", destDir);
        } catch (Exception e) {
            Log.e("Mfs", "copy asset exc..." + e);
        }

        if(Util.mfs100==null)
        {
            Util.mfs100 = new MFS100(this, mfsVer);
        }

        if(Util.mfs100!=null)
        {
            Util.mfs100.SetApplicationContext(this);
        }


        FindFormControls();
        CommonMethod.DeleteDirectory();
        CommonMethod.CreateDirectory();

        findViewById(R.id.btnValidate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAadharNumber();
            }
        });

        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar = new CustomProgressDialog(AadharCardSalesActivity.this);
                progressBar.setCanceledOnTouchOutside(false);
                if (NetworkUtil.getConnectivityStatus(AadharCardSalesActivity.this) == 0) {
                    Util.messageBar(AadharCardSalesActivity.this, getString(R.string.no_connectivity));
                } else {
                    progressBar.show();
                    process();
                }
//                submitValues(Util.fingerPrintAadhar, beneficiaryId);
            }
        });

        findViewById(R.id.btnStartCapture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmapImg = StartSyncCapture();
                imgFinger.setImageBitmap(bitmapImg);
                isoFeatureSet = Enroll_Template;
                try {
                    if (isoFeatureSet.length > 0) {
                        findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
                        findViewById(R.id.btnCancel).setVisibility(View.VISIBLE);
                        findViewById(R.id.btnStartCapture).setOnClickListener(null);
                        findViewById(R.id.btnStartCapture).setBackgroundColor(Color.LTGRAY);
                    }
                } catch (Exception e) {
                }
            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();/*Bitmap bitmapImg = null;
        try {
            bitmapImg = (Bitmap) getIntent().getParcelableExtra("Image");
            imgFinger.setImageBitmap(bitmapImg);
            isoFeatureSet = Enroll_Template;
            try {
                if (isoFeatureSet.length > 0) {
                    findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
                    findViewById(R.id.btnCancel).setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }*/
            }
        } catch (Exception e) {
        }
    }

    void process() {
        try {
            sendKYCRequest();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        Util.LoggingQueue(AadharCardSalesActivity.this, "AadharCardSalesActivity ",
                "processMessage() called message -> " + message + " Type -> " + what);

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
            default:
                break;
        }
    }

    private void sendKYCRequest() {
        try {
            HttpStack stack = null;
            try {
                stack = new HurlStack(null, new TLSSocketFactory());
            } catch (KeyManagementException e) {
                stack = new HurlStack();
            } catch (NoSuchAlgorithmException e) {
                stack = new HurlStack();
            }
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(context, stack);
            String json = getRequestJsonData();
            logLargeString(json.toString());
            String uriString = "https://esignprod.finahub.com/KREServer/kyc";
            String UID_AUTH_uriString = ""+FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_uriString");
            if(UID_AUTH_uriString != null && StringUtils.isNotEmpty(UID_AUTH_uriString) && (!UID_AUTH_uriString.equalsIgnoreCase("null")))
                uriString = UID_AUTH_uriString;
            final JSONObject jsonBody = new JSONObject(json);
            JsonObjectRequest req = new JsonObjectRequest(uriString, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    try {
//                        logLargeString(response.toString());
                        String status = (String) response.get("Status");
                        // Insert datas into biometric_authentication table
                        POSAadharAuthRequestDto posAadharAuthRequestDto = new POSAadharAuthRequestDto();
                        posAadharAuthRequestDto.setUid(fingerPrintAadhar);
                        posAadharAuthRequestDto.setFpsId(SessionId.getInstance().getFpsId());
                        posAadharAuthRequestDto.setBeneficiaryId(Long.valueOf(beneficiaryId));
                        posAadharAuthRequestDto.setAuthReponse(response.toString());
                        posAadharAuthRequestDto.setPosRequestDate(posReqDate.getTime());
                        posAadharAuthRequestDto.setAuthRequestDate(authReqDate.getTime());
                        posAadharAuthRequestDto.setFingerPrintData(isoFeatureSet);
                        if (status.equalsIgnoreCase("Y")) {
                            posAadharAuthRequestDto.setAuthenticationStatus(true);
                        } else if (status.equalsIgnoreCase("N")) {
                            posAadharAuthRequestDto.setAuthenticationStatus(false);
                        }
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            GregorianCalendar gc = new GregorianCalendar();
                            String dateString = sdf.format(gc.getTime());
                            Date authRespDate = sdf.parse(dateString);
                            posAadharAuthRequestDto.setAuthResponseDate(authRespDate.getTime());
                            posAadharAuthRequestDto.setPosResponseDate(authRespDate.getTime());
                        } catch (Exception e) {}
                        FPSDBHelper.getInstance(AadharCardSalesActivity.this).insertBiometric(posAadharAuthRequestDto);
                        dismissProgress();
                        if (status.equalsIgnoreCase("Y")) {
                            String rationCardNumber = FPSDBHelper.getInstance(AadharCardSalesActivity.this).getRationCardNumber(beneficiaryId);
                            getEntitlement(rationCardNumber);
                            imgFinger.setImageBitmap(null);
                            aadharNumber1.setText("");
                            aadharNumber2.setText("");
                            aadharNumber3.setText("");
                            fingerPrintAadhar = "";
                            beneficiaryId = "";
                        } else {
//                            String errorCode = (String) response.get("ErrorCode");
//                            if (errorCode.equalsIgnoreCase("300")) {
                                biometricAlertDialog = new BiometricAlertDialog(GlobalAppState.getInstance().getBaseContext(), getString(R.string.finger_print_mismatch));
                                biometricAlertDialog.show();
//                            } else {
//                                biometricAlertDialog = new BiometricAlertDialog(GlobalAppState.getInstance().getBaseContext(), getString(R.string.connectionError));
//                                biometricAlertDialog.show();
//                            }
                        }
                    } catch (JSONException e) {
                        dismissProgress();
                    }
                }
            },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgress();
                        }
                    });
            queue.add(req);
        } catch (Exception e) {
            Log.e("Mfs", "authentication exc...." + e.toString());
            dismissProgress();
            e.printStackTrace();
        }
    }

    private String getRequestJsonData() {
        JSONObject jsonData = new JSONObject();
        try {
            String encodedBioMetricInfo = null;
            String pidXml;
            encodedBioMetricInfo = Base64.encodeToString(isoFeatureSet, Base64.DEFAULT);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            authReqDate = sdf.parse(dateString);
            pidXml = buildPidXml(dateString, encodedBioMetricInfo, null);
            String fileNameWithPath = Environment.getExternalStorageDirectory().toString() + "/Fps/" + "uidai_auth_encrypt_preprod.cer";
            String UID_AUTH_certificateName = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_certificateName");
            if (UID_AUTH_certificateName != null && StringUtils.isNotEmpty(UID_AUTH_certificateName.trim()) && (!UID_AUTH_certificateName.equalsIgnoreCase("null")))
                fileNameWithPath = Environment.getExternalStorageDirectory().toString() + "/Fps/" + UID_AUTH_certificateName;
            EncrypterUtil encrypterUtil = new EncrypterUtil(fileNameWithPath);
            byte[] sessionKey = encrypterUtil.generateSessionKey();
            byte[] encryptedSessionKey = encrypterUtil.encryptUsingPublicKey(sessionKey);
            SessionKeyDetailsUtil sessionKeyDetails = SessionKeyDetailsUtil.createNormalSkey(encryptedSessionKey);
            byte[] pidXmlBytes = pidXml.getBytes();
            byte[] encXMLPIDData = encrypterUtil.encryptUsingSessionKey(sessionKey, pidXmlBytes);
            byte[] hmac = generateSha256Hash(pidXmlBytes);
            byte[] encryptedHmacBytes = encrypterUtil.encryptUsingSessionKey(sessionKey, hmac);
            String certificateIdentifier = encrypterUtil.getCertificateIdentifier();
            jsonData.put("Uid", fingerPrintAadhar);
            String UID_AUTH_TerminalId = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_TerminalId");
            if (UID_AUTH_TerminalId != null && StringUtils.isNotEmpty(UID_AUTH_TerminalId.trim()) && (!UID_AUTH_TerminalId.equalsIgnoreCase("null")))
                jsonData.put("TerminalId", UID_AUTH_TerminalId);
            else
                jsonData.put("TerminalId", "public");
            jsonData.put("EncryptedPid", Base64.encodeToString(encXMLPIDData, Base64.DEFAULT));
            jsonData.put("EncryptedHmac", Base64.encodeToString(encryptedHmacBytes, Base64.DEFAULT));
            jsonData.put("Ci", certificateIdentifier);
            jsonData.put("Ts", dateString);
            jsonData.put("EncryptedSessionKey", Base64.encodeToString(sessionKeyDetails.getSkeyValue(), Base64.DEFAULT));
            String UID_AUTH_Fdc = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_Fdc");
            if (UID_AUTH_Fdc != null && StringUtils.isNotEmpty(UID_AUTH_Fdc) && (!UID_AUTH_Fdc.equalsIgnoreCase("null")))
                jsonData.put("Fdc", UID_AUTH_Fdc);
            else
                jsonData.put("Fdc", "NC");
            String UID_AUTH_Lov = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_Lov");
            if (UID_AUTH_Lov != null && StringUtils.isNotEmpty(UID_AUTH_Lov) && (!UID_AUTH_Lov.equalsIgnoreCase("null")))
                jsonData.put("Lov", UID_AUTH_Lov);
            else
                jsonData.put("Lov", "560103");
            String UID_AUTH_PublicIp = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_PublicIp");
            if (UID_AUTH_PublicIp != null && StringUtils.isNotEmpty(UID_AUTH_PublicIp) && (!UID_AUTH_PublicIp.equalsIgnoreCase("null")))
                jsonData.put("PublicIp", UID_AUTH_PublicIp);
            else
                jsonData.put("PublicIp", "127.0.0.1");
            String UID_AUTH_Udc = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_Udc");
            if (UID_AUTH_Udc != null && StringUtils.isNotEmpty(UID_AUTH_Udc) && (!UID_AUTH_Udc.equalsIgnoreCase("null")))
                jsonData.put("Udc", UID_AUTH_Udc);
            else
                jsonData.put("Udc", "MTA-231755");
            if (true)
                jsonData.put("IsKyc", "true");
            else
                jsonData.put("IsKyc", "false");
            String UID_AUTH_securityToken = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_securityToken");
            if (UID_AUTH_securityToken != null && StringUtils.isNotEmpty(UID_AUTH_securityToken) && (!UID_AUTH_securityToken.equalsIgnoreCase("null")))
                jsonData.put("securityToken", UID_AUTH_securityToken);
            else
                jsonData.put("securityToken", "KREBSDOUWENVASHUWBKJBSDFINAHUB8713");
            String UID_AUTH_clientId = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_clientId");
            if (UID_AUTH_clientId != null && StringUtils.isNotEmpty(UID_AUTH_clientId) && (!UID_AUTH_clientId.equalsIgnoreCase("null")))
                jsonData.put("clientId", UID_AUTH_clientId);
            else
                jsonData.put("clientId", "1");
        }
        catch(Exception e) {}
        return jsonData.toString();
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

    private String buildPidXml(String timeStamp, String encodedBiometric, String encodedBiometric2ndFingur) {
        StringBuffer buff = new StringBuffer();
        buff.append("<Pid ts=");
        buff.append("\"" + timeStamp + "\"");
        buff.append(">");
        buff.append("<Bios>");
        buff.append("<Bio type=\"FMR\" posh=\"UNKNOWN\">");
        buff.append(encodedBiometric);
        buff.append("</Bio>");
        if (encodedBiometric2ndFingur != null) {
            buff.append("<Bio type=\"FMR\" posh=\"UNKNOWN\">");
            buff.append(encodedBiometric2ndFingur);
            buff.append("</Bio>");
        }
        buff.append("</Bios></Pid>");
        return buff.toString();
    }

    private void validateAadharNumber() {
        String cardNumber1 = aadharNumber1.getText().toString();
        String cardNumber2 = aadharNumber2.getText().toString();
        String cardNumber3 = aadharNumber3.getText().toString();
        if (StringUtils.isEmpty(cardNumber1) && StringUtils.isEmpty(cardNumber2) && StringUtils.isEmpty(cardNumber3)) {
            Util.messageBar(AadharCardSalesActivity.this, getString(R.string.aadhar_empty));
        } else {
            /*if (cardNumber1.length() != 4 || cardNumber2.length() != 4 || cardNumber3.length() != 4) {
                Util.messageBar(AadharCardSalesActivity.this, getString(R.string.invalidAadharNo));
                return;
            }
            if (String.valueOf(cardNumber1.charAt(0)).equalsIgnoreCase("0")) {
                Util.messageBar(AadharCardSalesActivity.this, getString(R.string.aadharNumberZero));
                return;
            }
            if (String.valueOf(cardNumber1.charAt(0)).equalsIgnoreCase("1")) {
                Util.messageBar(AadharCardSalesActivity.this, getString(R.string.aadharNumberOne));
                return;
            }*/
            fingerPrintAadhar = cardNumber1 + cardNumber2 + cardNumber3;
            beneficiaryId = FPSDBHelper.getInstance(AadharCardSalesActivity.this).retrieveBeneficiaryId(fingerPrintAadhar);
            if (beneficiaryId.equalsIgnoreCase("")) {
                Toast.makeText(AadharCardSalesActivity.this, getString(R.string.noFpsAadharNumber), Toast.LENGTH_LONG).show();
            } else {
                ((LinearLayout) findViewById(R.id.fingerPrintLayout)).setVisibility(View.VISIBLE);
                findViewById(R.id.btnValidate).setOnClickListener(null);
                findViewById(R.id.btnValidate).setBackgroundColor(Color.LTGRAY);
            }
        }
    }

    private void listenersForEditText() {
        aadharNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber1.getText().toString().length() == 4)     //size as per your requirement
                {
                    aadharNumber2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        aadharNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber2.getText().toString().length() == 4) {
                    aadharNumber3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        aadharNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber3.getText().toString().length() == 4) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (v.getId() == R.id.firstText) {
            checkVisibility();
            changeLayout(false);
            aadharNumber1.requestFocus();
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.secondText) {
            checkVisibility();
            changeLayout(false);
            aadharNumber2.requestFocus();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.thirdText) {
            checkVisibility();
            changeLayout(false);
            aadharNumber3.requestFocus();
            keyBoardFocused = KeyBoardEnum.FINALSTRING;
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (v.getId() == R.id.firstText && hasFocus) {
            checkVisibility();
            changeLayout(false);
            aadharNumber1.requestFocus();
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.secondText && hasFocus) {
            checkVisibility();
            changeLayout(false);
            aadharNumber2.requestFocus();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.thirdText && hasFocus) {
            checkVisibility();
            changeLayout(false);
            aadharNumber3.requestFocus();
            keyBoardFocused = KeyBoardEnum.FINALSTRING;
        }
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }

    private void changeLayout(boolean value) {
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.membersAadharRegMaster);
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
                if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    String text = aadharNumber1.getText().toString();
                    if (aadharNumber1.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber1.setText(text);
                        aadharNumber1.setSelection(text.length());
                    }
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    String text = aadharNumber2.getText().toString();
                    if (aadharNumber2.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber2.setText(text);
                        aadharNumber2.setSelection(text.length());
                    } else {
                        aadharNumber1.requestFocus();
                    }
                } else if (keyBoardFocused == KeyBoardEnum.FINALSTRING) {
                    String text = aadharNumber3.getText().toString();
                    if (aadharNumber3.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber3.setText(text);
                        aadharNumber3.setSelection(text.length());
                    } else {
                        aadharNumber2.requestFocus();
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    aadharNumber1.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    aadharNumber2.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.FINALSTRING) {
                    aadharNumber3.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }


    private void getEntitlement(String qrCodeString) {
        progressBar = new CustomProgressDialog(this);
        try {
            Util.LoggingQueue(AadharCardSalesActivity.this, "AadharCardSalesActivity", "Inside getEntitlement() qrCodeString->"+qrCodeString);

            progressBar.show();
            BeneficiaryDto benef = FPSDBHelper.getInstance(this).beneficiaryFromOldCard(qrCodeString);
            //Log.e("RationCardSalesActivity", "BeneficiaryDto..." + benef.toString());
            if (benef != null) {
                BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
                //Util.LoggingQueue(this, "Entitlement", "Calculating entitlement");
                QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeString);
                if (qrCodeResponseReceived != null)
                   // Log.e("RationCardSalesActivity", "QRTransactionResponseDto..." + qrCodeResponseReceived.toString());
                if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
                    NetworkConnection network = new NetworkConnection(this);
                    if (network.isNetworkAvailable()) {
                        qrCodeResponseReceived.setMode('G');
                    } else {
                        qrCodeResponseReceived.setMode('F');
                    }
                    Util.LoggingQueue(AadharCardSalesActivity.this, "Ration card Sales", "Moving to Sales Entry Page");
                    qrCodeResponseReceived.setRegistered(true);
                    EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                    /*startActivity(new Intent(this, SalesEntryActivity.class));
                    finish();*/

                    /** 08/07/2016
                     * SaleType defines Mode while inserting into FPSDB.db in SalesSummaryWithOutOTPActivity.class
                     * Online Mode  - G
                     * No Offline Mode for Aadhar card Biometric based sale
                     */
                    Intent intent = new Intent(this, SalesEntryActivity.class);
                    intent.putExtra("SaleType", "AadharCardSale");
                    startActivity(intent);
                    finish();
                } else if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() == 0) {
                    Util.LoggingQueue(AadharCardSalesActivity.this, "Ration card Sales", "Beneficiary Entitlement details not available");
                    errorNavigation(getString(R.string.entitlemnt_finished));
                } else {
                    Util.LoggingQueue(AadharCardSalesActivity.this, "Ration card Sales", "Beneficiary Entitlement details not available");
                    errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
                }
            } else {
                Util.LoggingQueue(AadharCardSalesActivity.this, "Ration card Sales", "Beneficiary Data is not available in db");
                errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.getStackTrace().toString());
           // Log.e("RationCardSalesActivity", e.toString(), e);
            errorNavigation(getString(R.string.invalid_card_no));
        } finally {
            if (progressBar != null)
                progressBar.dismiss();
        }
    }

    private void errorNavigation(String messages) {
        Intent intent = new Intent(this, SuccessFailureSalesActivity.class);
        Util.LoggingQueue(this, "Error in QRcode", "Navigating to error page");
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleOrderActivity.class));
        fingerPrintAadhar = "";
        finish();
    }

    protected void onStop() {
        super.onStop();
        UnInitScanner();
//        Toast.makeText(AadharCardSalesActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnInitScanner();
//        Toast.makeText(AadharCardSalesActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {}

        try {
            if ((biometricAlertDialog != null) && biometricAlertDialog.isShowing()) {
                biometricAlertDialog.dismiss();
            }
        } catch (final Exception e) {}
        finally {
            biometricAlertDialog = null;
        }
    }

    public void FindFormControls() {
        btnStartCapture = (Button) findViewById(R.id.btnStartCapture);
        lblMessage = (TextView) findViewById(R.id.lblMessage);
        imgFinger = (ImageView) findViewById(R.id.imgFinger);
        setUpPopUpPage();
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.aadhar_based);
        Util.setTamilText((TextView) findViewById(R.id.aadharLabel), R.string.aadharNo);
        Util.setTamilText((Button) findViewById(R.id.btnSubmit), R.string.submit);
        Util.setTamilText((Button) findViewById(R.id.btnCancel), R.string.cancel);
        Util.setTamilText((Button) findViewById(R.id.btnValidate), R.string.validate);
        Util.setTamilText((Button) findViewById(R.id.btnStartCapture), R.string.scan);
//        fingerPrintDto = new POSAadharAuthRequestDto();
        aadharNumber1 = (com.neopixl.pixlui.components.edittext.EditText) findViewById(R.id.firstText);
        aadharNumber2 = (com.neopixl.pixlui.components.edittext.EditText) findViewById(R.id.secondText);
        aadharNumber3 = (com.neopixl.pixlui.components.edittext.EditText) findViewById(R.id.thirdText);
        aadharNumber1.setShowSoftInputOnFocus(false);
        aadharNumber1.disableCopyAndPaste();
        aadharNumber2.setShowSoftInputOnFocus(false);
        aadharNumber2.disableCopyAndPaste();
        aadharNumber3.setShowSoftInputOnFocus(false);
        aadharNumber3.disableCopyAndPaste();
        aadharNumber1.setOnFocusChangeListener(AadharCardSalesActivity.this);
        aadharNumber1.setOnClickListener(this);
        aadharNumber2.setOnFocusChangeListener(this);
        aadharNumber2.setOnClickListener(this);
        aadharNumber3.setOnFocusChangeListener(this);
        aadharNumber3.setOnClickListener(this);
        listenersForEditText();
        keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
        Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
        //create KeyboardView object
        keyview = (KeyboardView) findViewById(R.id.customkeyboard);
        //attache the keyboard object to the KeyboardView object
        keyview.setKeyboard(keyboard);
        //show the keyboard
        keyview.setVisibility(KeyboardView.VISIBLE);
        keyview.setPreviewEnabled(false);
        //take the keyboard to the front
        keyview.bringToFront();
        //register the keyboard to receive the key pressed
        keyview.setOnKeyboardActionListener(new KeyList());
    }

    private Bitmap StartSyncCapture() {
        try {
            Thread trd = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FingerData fingerData = new FingerData();
                        int ret = Util.mfs100.AutoCapture(fingerData, timeout, false, true);
                        Log.e("sample app", "ret value..." + ret);
                        Log.e("sample app", "ret value..." + Util.mfs100.GetErrorMsg(ret));
                        if (ret != 0) {
                            Toast.makeText(AadharCardSalesActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                        } else {
                            Enroll_Template = new byte[fingerData.ISOTemplate().length];
                            System.arraycopy(fingerData.ISOTemplate(), 0, Enroll_Template, 0, fingerData.ISOTemplate().length);
                            bitmapImg = BitmapFactory.decodeByteArray(fingerData.FingerImage(), 0, fingerData.FingerImage().length);
                        }
                    } catch (Exception ex) {
                        CommonMethod.writeLog("Exception in ContinuesScan(). Message:- " + ex.getMessage());
                    }
                }
            });
            trd.run();
            trd.join();
        }
        catch(Exception e) {}
        return bitmapImg;
    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        int ret = 0;
        if (!hasPermission) {
            Toast.makeText(AadharCardSalesActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == 1204 || vid == 11279) {
            if (pid == 34323) {
                ret = Util.mfs100.LoadFirmware();
                if (ret != 0) {
                    Toast.makeText(AadharCardSalesActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AadharCardSalesActivity.this, "Loadfirmware success", Toast.LENGTH_SHORT).show();
                }
            } else if (pid == 4101) {
                ret = Util.mfs100.Init();
                if (ret != 0) {
                    Toast.makeText(AadharCardSalesActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    deviceIfo = Util.mfs100.GetDeviceInfo();
//                    Toast.makeText(AadharCardSalesActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void OnPreview(FingerData fingerData) {

    }

    @Override
    public void OnCaptureCompleted(boolean b, int i, String s, FingerData fingerData) {

    }

    @Override
    public void OnDeviceDetached() {
        UnInitScanner();
//        Toast.makeText(AadharCardSalesActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnHostCheckFailed(String err) {
        try {
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {}
    }

    public void UnInitScanner() {
        try {
            deviceIfo =  null;
            if(Util.mfs100 != null) {
                int ret = Util.mfs100.UnInit();
                if (ret != 0) {
//                    Toast.makeText(AadharCardSalesActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(AadharCardSalesActivity.this, "Uninit Success", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("UnInitScanner.EX", e.toString());
        }
    }

    public void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.e(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.e(TAG, str); // continuation
        }
    }

}

