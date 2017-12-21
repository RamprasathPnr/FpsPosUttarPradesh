package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mantra.mfs100.DeviceInfo;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;
import com.omneagate.DTO.BFDDetailDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProxyDetailDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TLSSocketFactory;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.AddProxySuccess;
import com.omneagate.activity.dialog.BfdRegSuccessDialog;
import com.omneagate.activity.dialog.EncrypterUtil;
import com.omneagate.activity.dialog.SessionKeyDetailsUtil;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TenFingerRegistrationActivity extends BaseActivity implements MFS100Event {
    String fingerPrintAadhar = "", beneficiaryId = "";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    UsbManager mManager;
    long gbldevice = 0;
    int gblquality = 0;
    int StopClick = 0;
    int busy = 1;
    byte[] rawdata;
    byte[] Enroll_Template;
    int mfsVer = 41;
    Context context;
    CommonMethod.ScannerAction scannerAction = CommonMethod.ScannerAction.Capture;
    int width = 316;
    int height = 354;
    Bitmap bitmapImg;
    int minQuality = 60;
    int timeout = 10000;
    byte[] isoFeatureSet_01 = new byte[0];
    byte[] isoFeatureSet_02 = new byte[0];
    byte[] isoFeatureSet_03 = new byte[0];
    byte[] isoFeatureSet_04 = new byte[0];
    byte[] isoFeatureSet_05 = new byte[0];
    byte[] isoFeatureSet_06 = new byte[0];
    byte[] isoFeatureSet_07 = new byte[0];
    byte[] isoFeatureSet_08 = new byte[0];
    byte[] isoFeatureSet_09 = new byte[0];
    byte[] isoFeatureSet_10 = new byte[0];
    UsbDeviceConnection mDeviceConnection;
    UsbInterface mInterface;
    Date posReqDate;
    int retryCount = 0;
    String selectedFinger = "", selectedPosition = "";
    final int[] NFIQ = new int[1];
    DeviceInfo deviceIfo = null;
    String TAG = "TenFingerRegistrationActivity";
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ten_finger_registration);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpInitialScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TenFingerRegistrationActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }


    private void setUpInitialScreen() {
        setUpPopUpPage();
        ((TextView)findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " :" + LoginData.getInstance().getShopNo());
        updateDateTime();
        fingerPrintAadhar = getIntent().getStringExtra("AadharNo");
        Util.LoggingQueue(this, "TenFingerRegistrationActivity", "Setting up TenFingerRegistrationActivity");
//        String rcNo = getIntent().getStringExtra("RcNumber");
        if (getIntent().getStringExtra("MemberType").equalsIgnoreCase("0")) {
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.members_aadhar_registration);
        }
        else if (getIntent().getStringExtra("MemberType").equalsIgnoreCase("1")) {
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.add_proxy);
        }
        Util.setTamilText((TextView) findViewById(R.id.btnSubmit), R.string.scan);
        Util.setTamilText((TextView) findViewById(R.id.btnCancel), R.string.cancel);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if ((isoFeatureSet_01.length > 0) && (isoFeatureSet_02.length > 0) && (isoFeatureSet_03.length > 0) && (isoFeatureSet_04.length > 0) && (isoFeatureSet_05.length > 0)
                            && (isoFeatureSet_06.length > 0) && (isoFeatureSet_07.length > 0) && (isoFeatureSet_08.length > 0) && (isoFeatureSet_09.length > 0) && (isoFeatureSet_10.length > 0)) {
                        process();
                    }
                    else {
                        StartSyncCapture();
                        if(Enroll_Template.length > 0) {
                            if (selectedPosition.equalsIgnoreCase("0")) {
                                if (!(isoFeatureSet_01.length > 0)) {
                                        isoFeatureSet_01 = new byte[Enroll_Template.length];
                                        isoFeatureSet_01 = Enroll_Template;
                                        if (isoFeatureSet_01.length > 0) {
                                            findViewById(R.id.rightSmall).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("1")) {
                                if (!(isoFeatureSet_02.length > 0)) {
                                        isoFeatureSet_02 = new byte[Enroll_Template.length];
                                        isoFeatureSet_02 = Enroll_Template;
                                        if (isoFeatureSet_02.length > 0) {
                                            findViewById(R.id.rightRing).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("2")) {
                                if (!(isoFeatureSet_03.length > 0)) {
                                        isoFeatureSet_03 = new byte[Enroll_Template.length];
                                        isoFeatureSet_03 = Enroll_Template;
                                        if (isoFeatureSet_03.length > 0) {
                                            findViewById(R.id.rightMiddle).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("3")) {
                                if (!(isoFeatureSet_04.length > 0)) {
                                        isoFeatureSet_04 = new byte[Enroll_Template.length];
                                        isoFeatureSet_04 = Enroll_Template;
                                        if (isoFeatureSet_04.length > 0) {
                                            findViewById(R.id.rightIndex).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("4")) {
                                if (!(isoFeatureSet_05.length > 0)) {
                                        isoFeatureSet_05 = new byte[Enroll_Template.length];
                                        isoFeatureSet_05 = Enroll_Template;
                                        if (isoFeatureSet_05.length > 0) {
                                            findViewById(R.id.rightThumb).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("5")) {
                                if (!(isoFeatureSet_06.length > 0)) {
                                        isoFeatureSet_06 = new byte[Enroll_Template.length];
                                        isoFeatureSet_06 = Enroll_Template;
                                        if (isoFeatureSet_06.length > 0) {
                                            findViewById(R.id.leftThumb).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("6")) {
                                if (!(isoFeatureSet_07.length > 0)) {
                                        isoFeatureSet_07 = new byte[Enroll_Template.length];
                                        isoFeatureSet_07 = Enroll_Template;
                                        if (isoFeatureSet_07.length > 0) {
                                            findViewById(R.id.leftIndex).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("7")) {
                                if (!(isoFeatureSet_08.length > 0)) {
                                        isoFeatureSet_08 = new byte[Enroll_Template.length];
                                        isoFeatureSet_08 = Enroll_Template;
                                        if (isoFeatureSet_08.length > 0) {
                                            findViewById(R.id.leftMiddle).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("8")) {
                                if (!(isoFeatureSet_09.length > 0)) {
                                        isoFeatureSet_09 = new byte[Enroll_Template.length];
                                        isoFeatureSet_09 = Enroll_Template;
                                        if (isoFeatureSet_09.length > 0) {
                                            findViewById(R.id.leftRing).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else if (selectedPosition.equalsIgnoreCase("9")) {
                                if (!(isoFeatureSet_10.length > 0)) {
                                        isoFeatureSet_10 = new byte[Enroll_Template.length];
                                        isoFeatureSet_10 = Enroll_Template;
                                        if (isoFeatureSet_10.length > 0) {
                                            findViewById(R.id.leftSmall).setVisibility(View.VISIBLE);
                                            Enroll_Template = new byte[0];
                                        }
                                } else {
                                    Toast.makeText(TenFingerRegistrationActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            }
                            if ((isoFeatureSet_01.length > 0) && (isoFeatureSet_02.length > 0) && (isoFeatureSet_03.length > 0) && (isoFeatureSet_04.length > 0) && (isoFeatureSet_05.length > 0)
                                    && (isoFeatureSet_06.length > 0) && (isoFeatureSet_07.length > 0) && (isoFeatureSet_08.length > 0) && (isoFeatureSet_09.length > 0) && (isoFeatureSet_10.length > 0)) {
                                Util.setTamilText((TextView) findViewById(R.id.btnSubmit), R.string.submit);
                            }
                        }
                    }
                } catch (Exception e) {}
            }
        });

        /*String[] finger = {"RIGHT_LITTLE", "RIGHT_RING", "RIGHT_MIDDLE", "RIGHT_INDEX", "RIGHT_THUMB", "LEFT_THUMB", "LEFT_INDEX", "LEFT_MIDDLE",
                "LEFT_RING", "LEFT_LITTLE"};*/
        ArrayList<String> finger = new ArrayList<String>();
        finger.add("RIGHT_LITTLE");
        finger.add("RIGHT_RING");
        finger.add("RIGHT_MIDDLE");
        finger.add("RIGHT_INDEX");
        finger.add("RIGHT_THUMB");
        finger.add("LEFT_THUMB");
        finger.add("LEFT_INDEX");
        finger.add("LEFT_MIDDLE");
        finger.add("LEFT_RING");
        finger.add("LEFT_LITTLE");
        final Spinner spinnerFinger = (Spinner) findViewById(R.id.fingerSpinner);
        ArrayAdapter<String> fingerAdapt = new ArrayAdapter<>(TenFingerRegistrationActivity.this, android.R.layout.simple_spinner_item, finger);
        fingerAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFinger.setAdapter(fingerAdapt);
        spinnerFinger.setPrompt(getString(R.string.selection));
        spinnerFinger.setFocusable(true);
        spinnerFinger.setFocusableInTouchMode(true);
        spinnerFinger.requestFocus();
        spinnerFinger.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedPosition = String.valueOf(position);
                selectedFinger = spinnerFinger.getSelectedItem().toString();
                Log.e("TenFingerReg","spinnerFinger..."+selectedFinger);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });



        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            posReqDate = sdf.parse(dateString);
        }
        catch(Exception e) {}

        // Copying folder in asset to sd card
        try {
            String destDir = Environment.getExternalStorageDirectory().toString() + "/Fps/";
            copyAssetToDir(TenFingerRegistrationActivity.this.getAssets(), "uidai_auth_prod.cer", destDir);
        }
        catch(Exception e) {
            Log.e("Mfs","copy asset exc..."+e);
        }

        if(Util.mfs100==null)
        {
            Util.mfs100 = new MFS100(this, mfsVer);
        }

        if(Util.mfs100!=null)
        {
            Util.mfs100.SetApplicationContext(this);
        }

        CommonMethod.DeleteDirectory();
        CommonMethod.CreateDirectory();
    }

    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case BFD_REGISTRATION:
                bfdRegSubmissionResponse(message);
                break;
            default:
                errorNavigation("");
                break;
        }
    }

    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Toast.makeText(TenFingerRegistrationActivity.this, messages, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    protected void onStop() {
        super.onStop();
        UnInitScanner();
//        Toast.makeText(TenFingerRegistrationActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        UnInitScanner();
//        Toast.makeText(TenFingerRegistrationActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
    }

    public void process() {
        try {
            progressBar = new CustomProgressDialog(TenFingerRegistrationActivity.this);
            progressBar.setCanceledOnTouchOutside(false);
            if (NetworkUtil.getConnectivityStatus(TenFingerRegistrationActivity.this) == 0) {
                Util.messageBar(TenFingerRegistrationActivity.this, getString(R.string.no_connectivity));
            } else {
                progressBar.show();
                sendKYCRequest();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void sendKYCRequest() {
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
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(TenFingerRegistrationActivity.this, stack);
            String rbdXml;
            final String encodedBioMetricInfo_01 = Base64.encodeToString(isoFeatureSet_01, Base64.DEFAULT);
            final String encodedBioMetricInfo_02 = Base64.encodeToString(isoFeatureSet_02, Base64.DEFAULT);
            final String encodedBioMetricInfo_03 = Base64.encodeToString(isoFeatureSet_03, Base64.DEFAULT);
            final String encodedBioMetricInfo_04 = Base64.encodeToString(isoFeatureSet_04, Base64.DEFAULT);
            final String encodedBioMetricInfo_05 = Base64.encodeToString(isoFeatureSet_05, Base64.DEFAULT);
            final String encodedBioMetricInfo_06 = Base64.encodeToString(isoFeatureSet_06, Base64.DEFAULT);
            final String encodedBioMetricInfo_07 = Base64.encodeToString(isoFeatureSet_07, Base64.DEFAULT);
            final String encodedBioMetricInfo_08 = Base64.encodeToString(isoFeatureSet_08, Base64.DEFAULT);
            final String encodedBioMetricInfo_09 = Base64.encodeToString(isoFeatureSet_09, Base64.DEFAULT);
            final String encodedBioMetricInfo_10 = Base64.encodeToString(isoFeatureSet_10, Base64.DEFAULT);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            final Date authReqDate = sdf.parse(dateString);
            rbdXml = buildRbdXml(dateString, encodedBioMetricInfo_01, encodedBioMetricInfo_02, encodedBioMetricInfo_03, encodedBioMetricInfo_04, encodedBioMetricInfo_05,
                    encodedBioMetricInfo_06, encodedBioMetricInfo_07, encodedBioMetricInfo_08, encodedBioMetricInfo_09, encodedBioMetricInfo_10);
            String fileNameWithPath = Environment.getExternalStorageDirectory().toString() + "/Fps/" + "uidai_auth_encrypt_preprod.cer";
            String UID_AUTH_bfd_certificateName = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_bfd_certificateName");
            if (UID_AUTH_bfd_certificateName != null && StringUtils.isNotEmpty(UID_AUTH_bfd_certificateName.trim()) && (!UID_AUTH_bfd_certificateName.equalsIgnoreCase("null")))
                fileNameWithPath = Environment.getExternalStorageDirectory().toString() + "/Fps/" + UID_AUTH_bfd_certificateName;
            EncrypterUtil encrypterUtil = new EncrypterUtil(fileNameWithPath);
            byte[] sessionKey = encrypterUtil.generateSessionKey();
            byte[] encryptedSessionKey = encrypterUtil.encryptUsingPublicKey(sessionKey);
            SessionKeyDetailsUtil sessionKeyDetails = SessionKeyDetailsUtil.createNormalSkey(encryptedSessionKey);
            byte[] rbdXmlBytes = rbdXml.getBytes();
            byte[] encXMLPIDData = encrypterUtil.encryptUsingSessionKey(sessionKey, rbdXmlBytes);
            byte[] hmac = generateSha256Hash(rbdXmlBytes);
            byte[] encryptedHmacBytes = encrypterUtil.encryptUsingSessionKey(sessionKey, hmac);
            String certificateIdentifier = encrypterUtil.getCertificateIdentifier();
            JSONObject jsonData = new JSONObject();
            jsonData.put("Uid", fingerPrintAadhar);
            String UID_AUTH_TerminalId = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_TerminalId");
            if (UID_AUTH_TerminalId != null && StringUtils.isNotEmpty(UID_AUTH_TerminalId.trim()) && (!UID_AUTH_TerminalId.equalsIgnoreCase("null")))
                jsonData.put("TerminalId", UID_AUTH_TerminalId);
            else
                jsonData.put("TerminalId", "public");
            jsonData.put("EncryptedRbd", Base64.encodeToString(encXMLPIDData, Base64.DEFAULT));
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
            if (false)
                jsonData.put("IsKyc", "true");
            else
                jsonData.put("IsKyc", "false");
            String UID_AUTH_bfd_securityToken = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_bfd_securityToken");
            if (UID_AUTH_bfd_securityToken != null && StringUtils.isNotEmpty(UID_AUTH_bfd_securityToken) && (!UID_AUTH_bfd_securityToken.equalsIgnoreCase("null")))
                jsonData.put("securityToken", UID_AUTH_bfd_securityToken);
            else
                jsonData.put("securityToken", "LIEWRGFBKSDBFKWRIUWERNCKILWGERILGSDKFKSD324786FINAHUB");
            String UID_AUTH_clientId = "" + FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_clientId");
            if (UID_AUTH_clientId != null && StringUtils.isNotEmpty(UID_AUTH_clientId) && (!UID_AUTH_clientId.equalsIgnoreCase("null")))
                jsonData.put("clientId", UID_AUTH_clientId);
            else
                jsonData.put("clientId", "1");
            final String json = jsonData.toString();
            Log.e("Request", "Request json " + json);
            logLargeString(json);
            String uriString = "https://esignprod.finahub.com/AUA/bfd";
            String UID_AUTH_bfd_uriString = ""+FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_bfd_uriString");
            if(UID_AUTH_bfd_uriString != null && StringUtils.isNotEmpty(UID_AUTH_bfd_uriString) && (!UID_AUTH_bfd_uriString.equalsIgnoreCase("null")))
                uriString = UID_AUTH_bfd_uriString;
            final JSONObject jsonBody = new JSONObject(json);
            JsonObjectRequest req = new JsonObjectRequest(uriString, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    Log.e("Response", "Response is " + response.toString());
                    dismissProgress();
                    try {
                        String status = (String) response.get("Status");
                        if (status.equalsIgnoreCase("success")) {
                            String xmlStr = (String) response.get("Xml");
                            String[] strArr1 = xmlStr.split("<Ranks>");
                            String str = strArr1[1];
                            String[] strArr2 = str.split("</Ranks>");
                            String xml = "<Ranks>"+strArr2[0]+"</Ranks>";
                            ArrayList<String> posVal = xmlParsing(xml);
                            BFDDetailDto bfdDetailDto = new BFDDetailDto();
                            for(int i=0;i<posVal.size();i++) {
                                String[] StrArr = posVal.get(i).split("~");
                                if(StrArr[1].equalsIgnoreCase("1")) {
                                    bfdDetailDto.setBestFinger01(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("2")) {
                                    bfdDetailDto.setBestFinger02(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("3")) {
                                    bfdDetailDto.setBestFinger03(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("4")) {
                                    bfdDetailDto.setBestFinger04(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("5")) {
                                    bfdDetailDto.setBestFinger05(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("6")) {
                                    bfdDetailDto.setBestFinger06(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("7")) {
                                    bfdDetailDto.setBestFinger07(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("8")) {
                                    bfdDetailDto.setBestFinger08(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("9")) {
                                    bfdDetailDto.setBestFinger09(StrArr[0]);
                                }
                                else if(StrArr[1].equalsIgnoreCase("10")) {
                                    bfdDetailDto.setBestFinger10(StrArr[0]);
                                }
                            }

                            if (getIntent().getStringExtra("MemberType").equalsIgnoreCase("0")) {
                                long benefId = FPSDBHelper.getInstance(TenFingerRegistrationActivity.this).getBeneficiaryIdFromUid(fingerPrintAadhar);
                                long benefMemberId = FPSDBHelper.getInstance(TenFingerRegistrationActivity.this).getBeneficiaryMemberIdFromUid(fingerPrintAadhar);
                                bfdDetailDto.setStatus(true);
//                                bfdDetailsDto.setBeneficiaryId(benefId);
//                                bfdDetailsDto.setBeneficiaryMemberId(benefMemberId);

                                BeneficiaryDto beneficiaryDto = new BeneficiaryDto();
                                beneficiaryDto.setId(benefId);
                                bfdDetailDto.setBeneficiary(beneficiaryDto);

                                /*BeneficiaryMemberDto beneficiaryMemberDto = new BeneficiaryMemberDto();
                                beneficiaryMemberDto.setId(benefMemberId);
                                bfdDetailDto.setBeneficiaryMember(beneficiaryMemberDto);*/
                            }
                            else if (getIntent().getStringExtra("MemberType").equalsIgnoreCase("1")) {
                                long benefId = getIntent().getLongExtra("BenefId", 0);
//                                long benefMemberId = FPSDBHelper.getInstance(TenFingerRegistrationActivity.this).getProxyMemberIdFromUid(fingerPrintAadhar);
                                bfdDetailDto.setStatus(false);
//                                bfdDetailsDto.setBeneficiaryId(benefId);
//                                bfdDetailsDto.setBeneficiaryMemberId(benefMemberId);

                                BeneficiaryDto beneficiaryDto = new BeneficiaryDto();
                                beneficiaryDto.setId(benefId);
                                bfdDetailDto.setBeneficiary(beneficiaryDto);

                                /*BeneficiaryMemberDto beneficiaryMemberDto = new BeneficiaryMemberDto();
                                beneficiaryMemberDto.setId(benefMemberId);
                                bfdDetailsDto.setBeneficiaryMember(beneficiaryMemberDto);*/

                                String proxyDto = getIntent().getExtras().getString("ProxyDetailsDto");
                                ProxyDetailDto proxyDetailsDto = new Gson().fromJson(proxyDto, ProxyDetailDto.class);
                                bfdDetailDto.setProxyDetail(proxyDetailsDto);
                            }


                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                GregorianCalendar gc = new GregorianCalendar();
                                String dateString = sdf.format(gc.getTime());
                                Date created = sdf.parse(dateString);
                                bfdDetailDto.setCreatedDate(created.getTime());
                            }
                            catch(Exception e) {}
                            bfdDetailDto.setCreatedBy(SessionId.getInstance().getFpsId());
                            sendBfdDetailToServer(bfdDetailDto);

                            /*boolean inserted = FPSDBHelper.getInstance(TenFingerRegistrationActivity.this).insertBfdDetails(bfdDetailDto);
                            if (inserted) {
                                findViewById(R.id.btnSubmit).setOnClickListener(null);
                                findViewById(R.id.btnSubmit).setBackgroundColor(Color.LTGRAY);

                                if (getIntent().getStringExtra("MemberType").equalsIgnoreCase("1")) {
                                    String rcNo = getIntent().getStringExtra("RcNumber");
                                    new AddProxySuccess(TenFingerRegistrationActivity.this, rcNo).show();
                                }
                                else {
                                    new BfdRegSuccessDialog(TenFingerRegistrationActivity.this).show();
                                }
                            }*/
                        }
                    }
                    catch(Exception e) {
                        Log.e("Ten finger","Exception01......"+e);
                    }
                }
            },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dismissProgress();
                            Log.e("Ten finger Reg", "BFD Response  Error is " + error.toString());
                            Util.messageBar(TenFingerRegistrationActivity.this, getString(R.string.connectionError));
                        }
                    });
            queue.add(req);
        }
        catch(Exception e) {
            Log.e("TenFingerReg", "bfd authentication exc...." + e.toString());
            dismissProgress();
            e.printStackTrace();
        }
    }

    public void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.e(TAG, "request..."+str.substring(0, 3000));
            logLargeString("request..." + str.substring(3000));
        } else {
            Log.e(TAG, "request..."+str); // continuation
        }
    }

    private void sendBfdDetailToServer(BFDDetailDto bfdDetailDto) {
        // Bfd Details sync process
        try {
            Util.LoggingQueue(TenFingerRegistrationActivity.this, "Info", "Bfd task started");
            NetworkConnection network = new NetworkConnection(TenFingerRegistrationActivity.this);
            if (network.isNetworkAvailable()) {
                BufferedReader in = null;
                String url = "/bfddetail/add";
                String updateData = new Gson().toJson(bfdDetailDto);
                Log.e("Statistics service", "bfd details request..." + updateData);
                StringEntity se = null;
                se = new StringEntity(updateData, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.BFD_REGISTRATION,
                        SyncHandler, RequestType.POST, se, this);
            }
        }
        catch(Exception e) {
            Util.LoggingQueue(TenFingerRegistrationActivity.this, "bfd details service exception..", e.getMessage());
        }
    }

    private ArrayList<String> xmlParsing(String xmlData) {
        ArrayList<String> posValList = new ArrayList<String>();
        try {
            String xmlRecords = xmlData;
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document dom = db.parse(is);
            NodeList l = dom.getElementsByTagName("Ranks");
            for (int j = 0; j < l.getLength(); ++j) {
                NodeList l2 = dom.getElementsByTagName("Rank");
                for (int k = 0; k < l2.getLength(); ++k) {
                    Node prop = l2.item(k);
                    NamedNodeMap attr = prop.getAttributes();
                    if (null != attr) {
                        String pos = "", val = "";
                        try {
                            pos = attr.getNamedItem("pos").getNodeValue();
                            val = attr.getNamedItem("val").getNodeValue();
                            Log.e("TenFinger", "pos val..." + pos + " , " + val);
                            posValList.add(pos+"~"+val);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posValList;
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

    private String buildRbdXml(String timeStamp, String encodedBiometric_01, String encodedBiometric_02, String encodedBiometric_03, String encodedBiometric_04,
                               String encodedBiometric_05, String encodedBiometric_06, String encodedBiometric_07, String encodedBiometric_08,
                               String encodedBiometric_09, String encodedBiometric_10) {
        StringBuffer buff = new StringBuffer();
        buff.append("<Rbd ts=");
        buff.append("\"" + timeStamp + "\"");
        buff.append(" ver=");
        buff.append("\"" + "1.0" + "\"");
        buff.append(">");
        buff.append("<Bios>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"RIGHT_LITTLE\">");
        buff.append(encodedBiometric_01);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"RIGHT_RING\">");
        buff.append(encodedBiometric_02);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"RIGHT_MIDDLE\">");
        buff.append(encodedBiometric_03);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"RIGHT_INDEX\">");
        buff.append(encodedBiometric_04);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"RIGHT_THUMB\">");
        buff.append(encodedBiometric_05);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"LEFT_THUMB\">");
        buff.append(encodedBiometric_06);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"LEFT_INDEX\">");
        buff.append(encodedBiometric_07);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"LEFT_MIDDLE\">");
        buff.append(encodedBiometric_08);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"LEFT_RING\">");
        buff.append(encodedBiometric_09);
        buff.append("</Bio>");
        buff.append("<Bio nfiq=");
        buff.append("\"" + NFIQ[0] + "\"");
        buff.append(" na=\"1\" pos=\"LEFT_LITTLE\">");
        buff.append(encodedBiometric_10);
        buff.append("</Bio>");
        buff.append("</Bios></Rbd>");
        Log.e("rbdxml", "rbdxml is " + buff.toString());
        return buff.toString();
    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
    }

    private void bfdRegSubmissionResponse(Bundle message) {
        dismissProgress();
        String response = message.getString(FPSDBConstants.RESPONSE_DATA);
        Log.e("Statistics service", "bfd details response..."+response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        BFDDetailDto bfdDetailDto = gson.fromJson(response, BFDDetailDto.class);
        if (bfdDetailDto.getStatusCode() == 0) {
            boolean inserted = FPSDBHelper.getInstance(TenFingerRegistrationActivity.this).insertBfdDetails(bfdDetailDto);
//            FPSDBHelper.getInstance(AddProxyDetailsActivity.this).updateProxyDetailsSyncStatus(proxyDetailsDto.getBeneficiary().getId());
            if (inserted) {
                findViewById(R.id.btnSubmit).setOnClickListener(null);
                findViewById(R.id.btnSubmit).setBackgroundColor(Color.LTGRAY);

                if (getIntent().getStringExtra("MemberType").equalsIgnoreCase("1")) {
                    String rcNo = getIntent().getStringExtra("RcNumber");
                    new AddProxySuccess(TenFingerRegistrationActivity.this, rcNo).show();
                }
                else {
                    new BfdRegSuccessDialog(TenFingerRegistrationActivity.this).show();
                }
            }
        }
        else {
            String messageData = "";
            try {
                messageData = Util.messageSelection(FPSDBHelper.getInstance(TenFingerRegistrationActivity.this).retrieveLanguageTable(bfdDetailDto.getStatusCode()));
            }
            catch(Exception e) {
                if (StringUtils.isEmpty(messageData))
                    messageData = "Bfd sync failed";
            }
            if (StringUtils.isEmpty(messageData))
                messageData = "Bfd sync failed";
            Toast.makeText(TenFingerRegistrationActivity.this, messageData, Toast.LENGTH_SHORT).show();
            Util.LoggingQueue(TenFingerRegistrationActivity.this, "Statistics service", "Error syncing bfd detail..." + messageData);
        }
    }

    private Bitmap StartSyncCapture() {
        try {
            Thread trd =new Thread(new Runnable() {
                @Override
                public void run() {
                    FingerData fingerData = new FingerData();
                    int ret = Util.mfs100.AutoCapture(fingerData, timeout, false, true);
                    Log.e("sample app","ret value..."+ret);
                    Log.e("sample app", "ret value..." + Util.mfs100.GetErrorMsg(ret));
                    if (ret != 0) {
                        Toast.makeText(TenFingerRegistrationActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Enroll_Template = new byte[fingerData.ISOTemplate().length];
                        System.arraycopy(fingerData.ISOTemplate(), 0, Enroll_Template, 0, fingerData.ISOTemplate().length);
                        bitmapImg = BitmapFactory.decodeByteArray(fingerData.FingerImage(), 0, fingerData.FingerImage().length);
                    }
                }
            });
            trd.run();
            trd.join();
        } catch (Exception ex) {
            CommonMethod.writeLog("Exception in ContinuesScan(). Message:- "+ ex.getMessage());
        } finally {}
        return bitmapImg;
    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        int ret = 0;
        if (!hasPermission) {
            Toast.makeText(TenFingerRegistrationActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == 1204 || vid == 11279) {
            if (pid == 34323) {
                ret = Util.mfs100.LoadFirmware();
                if (ret != 0) {
                    Toast.makeText(TenFingerRegistrationActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TenFingerRegistrationActivity.this, "Loadfirmware success", Toast.LENGTH_SHORT).show();
                }
            } else if (pid == 4101) {
                ret = Util.mfs100.Init();
                if (ret != 0) {
                    Toast.makeText(TenFingerRegistrationActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    deviceIfo = Util.mfs100.GetDeviceInfo();
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
//        Toast.makeText(TenFingerRegistrationActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(TenFingerRegistrationActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(TenFingerRegistrationActivity.this, "Uninit Success", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("UnInitScanner.EX", e.toString());
        }
    }

}