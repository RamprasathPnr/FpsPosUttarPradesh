package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.omneagate.DTO.BFDDetailDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.POSAadharAuthRequestDto;
import com.omneagate.DTO.ProxyDetailDto;
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
import com.omneagate.activity.dialog.BiometricAuthRetryDialog;
import com.omneagate.activity.dialog.BiometricAuthRetryFailedDialog;
import com.omneagate.activity.dialog.EncrypterUtil;
import com.omneagate.activity.dialog.RationCardListAdapter;
import com.omneagate.activity.dialog.SessionKeyDetailsUtil;
import com.omneagate.service.HttpClientWrapper;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

public class BenefBfdScanActivity extends BaseActivity implements MFS100Event {

    RationCardListAdapter rationCardListAdapter;
    ArrayList<String> benefDetailsList;
    String fingerPrintAadhar = "", beneficiaryId = "", mobileNo = "";
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
    byte[] isoFeatureSet;
    UsbDeviceConnection mDeviceConnection;
    UsbInterface mInterface;
    Date posReqDate, authReqDate;
    int retryCount = 0;
    ImageView imgFinger;
//    HashMap<String,String> rank;
    BFDDetailDto bfdDetailDto;
    String encodedBioMetricInfo = null;
    String pidXml;
    DeviceInfo deviceIfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.benef_bfd_scan);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpInitialScreen();
    }

    private void setUpInitialScreen() {
        setUpPopUpPage();
        imgFinger = (ImageView) findViewById(R.id.imgFinger);
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
            //copyAssetToDir(AadharCardSalesActivity.this.getAssets(), "uidai_auth_encrypt_preprod.cer", destDir);
            copyAssetToDir(BenefBfdScanActivity.this.getAssets(), "uidai_auth_prod.cer", destDir);
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

        Bitmap bitmapImg = null;
        try {
            bitmapImg = (Bitmap) getIntent().getParcelableExtra("Image");
            imgFinger.setImageBitmap(bitmapImg);
            isoFeatureSet = Enroll_Template;
            try {
                if (isoFeatureSet.length > 0) {
                    findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
//                    findViewById(R.id.btnCancel).setVisibility(View.VISIBLE);
                }
            }
            catch(Exception e) {}
        }
        catch(Exception e) {}

        Util.LoggingQueue(this, "BenefBfdScanActivity", "Setting up BenefBfdScanActivity");
//        String sNo = getIntent().getStringExtra("BenefId");
        String benefId = getIntent().getStringExtra("BenefId");
        String proxyId = getIntent().getStringExtra("ProxyId");
        String rcNo = getIntent().getStringExtra("RcNumber");
        String memberType = getIntent().getStringExtra("MemberType");
        BeneficiaryDto benef = FPSDBHelper.getInstance(BenefBfdScanActivity.this).beneficiaryFromOldCard(rcNo);
        if (memberType.equalsIgnoreCase("Beneficiary")) {
            fingerPrintAadhar = benef.getFamilyHeadAadharNumber();
            mobileNo = benef.getMobileNumber();
            BeneficiaryMemberDto beneficiaryMemberDto = FPSDBHelper.getInstance(BenefBfdScanActivity.this).getSpecificBeneficiaryMember(fingerPrintAadhar);
            ((TextView) findViewById(R.id.benefNameTv)).setText(beneficiaryMemberDto.getName());
        }
        else if (memberType.equalsIgnoreCase("Proxy")) {
            ProxyDetailDto proxyDetailsDto = FPSDBHelper.getInstance(BenefBfdScanActivity.this).retrieveSpecificProxy(proxyId, benefId);
            fingerPrintAadhar = proxyDetailsDto.getUid();
            mobileNo = proxyDetailsDto.getMobile();
            ((TextView) findViewById(R.id.benefNameTv)).setText(proxyDetailsDto.getName());
        }

        if(memberType.equalsIgnoreCase("Beneficiary")) {
            bfdDetailDto = FPSDBHelper.getInstance(BenefBfdScanActivity.this).getLocalBfdDetailsForHead(benefId);
        }
        else if(memberType.equalsIgnoreCase("Proxy")) {
            bfdDetailDto = FPSDBHelper.getInstance(BenefBfdScanActivity.this).getLocalBfdDetailsForProxy(benefId, proxyId);
        }

        beneficiaryId = FPSDBHelper.getInstance(BenefBfdScanActivity.this).retrieveBeneficiaryId(fingerPrintAadhar);
        String cardType = FPSDBHelper.getInstance(BenefBfdScanActivity.this).getCardTypeFromId(benef.getCardTypeId());
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sales_top_heading);
        Util.setTamilText((TextView) findViewById(R.id.rcNoHeading), R.string.ration_card_number1);
        Util.setTamilText((TextView) findViewById(R.id.rcTypeHeading), R.string.ration_card_type);
        ((TextView) findViewById(R.id.rcNoValue)).setText(benef.getOldRationNumber());
        ((TextView) findViewById(R.id.rcTypeValue)).setText(cardType);
        Util.setTamilText((TextView) findViewById(R.id.aadharVerificationTv), R.string.aadhar_verification);
        if(retryCount<=2) {
            ((TextView) findViewById(R.id.aadharVerificationValueTv)).setText(String.valueOf(retryCount + 1) + "/2");
        }

        ((TextView) findViewById(R.id.aadharTv)).setText(fingerPrintAadhar);
        ((TextView) findViewById(R.id.mobileTv)).setText(mobileNo);
        Util.setTamilText((TextView) findViewById(R.id.authHeading), R.string.authentication);
        Util.setTamilText((Button) findViewById(R.id.btnSubmit), R.string.submit);
        Util.setTamilText((Button) findViewById(R.id.btnScan), R.string.scan);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isoFeatureSet != null) {
                    process();
                } else {
                    Toast.makeText(BenefBfdScanActivity.this, getString(R.string.not_scanned), Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
        /*findViewById(R.id.bestFirstLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightOne();
                scan();
            }
        });
        findViewById(R.id.bestSecondLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightTwo();
                scan();
            }
        });
        findViewById(R.id.bestThirdLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightThree();
                scan();
            }
        });*/
        setBestThree();
    }

    private void highlightOne() {
        findViewById(R.id.bestFirstLayout).setBackgroundColor(getResources().getColor(R.color.lightgreen));
        findViewById(R.id.bestSecondLayout).setBackgroundColor(getResources().getColor(R.color.white));
        findViewById(R.id.bestThirdLayout).setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void highlightTwo() {
        findViewById(R.id.bestFirstLayout).setBackgroundColor(getResources().getColor(R.color.white));
        findViewById(R.id.bestSecondLayout).setBackgroundColor(getResources().getColor(R.color.lightgreen));
        findViewById(R.id.bestThirdLayout).setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void highlightThree() {
        findViewById(R.id.bestFirstLayout).setBackgroundColor(getResources().getColor(R.color.white));
        findViewById(R.id.bestSecondLayout).setBackgroundColor(getResources().getColor(R.color.white));
        findViewById(R.id.bestThirdLayout).setBackgroundColor(getResources().getColor(R.color.lightgreen));
    }

    private void scan() {
        Bitmap bitmapImg = StartSyncCapture();
        imgFinger.setImageBitmap(null);
        imgFinger.setImageBitmap(bitmapImg);
        isoFeatureSet = Enroll_Template;
        try {
            if (isoFeatureSet.length > 0) {
                Log.e("BenefBfdScan", "isoFeatureSet..." + Arrays.toString(isoFeatureSet));
                findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
//                        findViewById(R.id.btnCancel).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
    }

    private void setBestThree() {
        /*rank = new HashMap<String,String>();
        rank.put(bfdDetailDto.getBestFinger01(), "RIGHT_LITTLE");
        rank.put(bfdDetailDto.getBestFinger02(), "RIGHT_RING");
        rank.put(bfdDetailDto.getBestFinger03(), "RIGHT_MIDDLE");
        rank.put(bfdDetailDto.getBestFinger04(), "RIGHT_INDEX");
        rank.put(bfdDetailDto.getBestFinger05(), "RIGHT_THUMB");
        rank.put(bfdDetailDto.getBestFinger06(), "LEFT_THUMB");
        rank.put(bfdDetailDto.getBestFinger07(), "LEFT_INDEX");
        rank.put(bfdDetailDto.getBestFinger08(), "LEFT_MIDDLE");
        rank.put(bfdDetailDto.getBestFinger09(), "LEFT_RING");
        rank.put(bfdDetailDto.getBestFinger10(), "LEFT_LITTLE");*/
        if(bfdDetailDto != null) {
            ((TextView) findViewById(R.id.bestFingerOneTv)).setText(bfdDetailDto.getBestFinger01());
            ((TextView) findViewById(R.id.bestFingerTwoTv)).setText(bfdDetailDto.getBestFinger02());
            ((TextView) findViewById(R.id.bestFingerThreeTv)).setText(bfdDetailDto.getBestFinger03());
        }
        else {
            findViewById(R.id.noBfdLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.bestThreeLayout).setVisibility(View.GONE);
        }
    }

    public void process() {
        try {
            progressBar = new CustomProgressDialog(BenefBfdScanActivity.this);
            progressBar.setCanceledOnTouchOutside(false);
            if (NetworkUtil.getConnectivityStatus(BenefBfdScanActivity.this) == 0) {
                retryCount++;
                if(retryCount<=2) {
                    ((TextView) findViewById(R.id.aadharVerificationValueTv)).setText(String.valueOf(retryCount + 1) + "/2");
                }
                if (retryCount >= 2) {
                    saveKYCRequestDetails();
                    String sNo = getIntent().getStringExtra("BenefMemberSno");
                    String rcNo = getIntent().getStringExtra("RcNumber");
                    String memberType = getIntent().getStringExtra("MemberType");
                    String benefId = getIntent().getStringExtra("BenefId");
                    String proxyId = getIntent().getStringExtra("ProxyId");
                    final String encodedBioMetricInfo2 = Base64.encodeToString(isoFeatureSet, Base64.DEFAULT);
                    new BiometricAuthRetryFailedDialog(BenefBfdScanActivity.this, rcNo, benefId, proxyId, encodedBioMetricInfo2, memberType).show();
                }
                else {
                    new BiometricAuthRetryDialog(BenefBfdScanActivity.this, retryCount).show();
                }


                /*Util.messageBar(BenefBfdScanActivity.this, getString(R.string.no_connectivity));
                Intent intent = new Intent(BenefBfdScanActivity.this, MobileOTPNeedActivity.class);
                intent.putExtra("AadharNumber", fingerPrintAadhar);
                String rationCardNo = getIntent().getStringExtra("RcNumber");
                intent.putExtra("RationCardNumber", rationCardNo);
                intent.putExtra("AadharLinked", true);
                BenefBfdScanActivity.this.startActivity(intent);*/
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
                stack = new HurlStack();
            } catch (NoSuchAlgorithmException e) {
                stack = new HurlStack();
            }
            com.android.volley.RequestQueue queue = Volley.newRequestQueue(BenefBfdScanActivity.this, stack);
            String json = getRequestJsonData();
            String uriString = "https://esignprod.finahub.com/KREServer/kyc";
            String UID_AUTH_uriString = ""+FPSDBHelper.getInstance(this).getMasterData("UID_AUTH_uriString");
            if(UID_AUTH_uriString != null && StringUtils.isNotEmpty(UID_AUTH_uriString) && (!UID_AUTH_uriString.equalsIgnoreCase("null")))
                uriString = UID_AUTH_uriString;
            final JSONObject jsonBody = new JSONObject(json);
            JsonObjectRequest req = new JsonObjectRequest(uriString, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    try {
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
                        }
                        else if (status.equalsIgnoreCase("N")) {
                            posAadharAuthRequestDto.setAuthenticationStatus(false);
                        }
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            GregorianCalendar gc = new GregorianCalendar();
                            String dateString = sdf.format(gc.getTime());
                            Date authRespDate = sdf.parse(dateString);
                            posAadharAuthRequestDto.setAuthResponseDate(authRespDate.getTime());
                            posAadharAuthRequestDto.setPosResponseDate(authRespDate.getTime());
                        }
                        catch(Exception e) {}
                        FPSDBHelper.getInstance(BenefBfdScanActivity.this).insertBiometric(posAadharAuthRequestDto);
                        dismissProgress();
                        if (status.equalsIgnoreCase("Y")) {
                            String rationCardNumber = FPSDBHelper.getInstance(BenefBfdScanActivity.this).getRationCardNumber(beneficiaryId);
                            getEntitlement(rationCardNumber);
                            imgFinger.setImageBitmap(null);
                            fingerPrintAadhar = "";
                            beneficiaryId = "";
                        }
                        else {
                            retryCount++;
                            if(retryCount<=2) {
                                ((TextView) findViewById(R.id.aadharVerificationValueTv)).setText(String.valueOf(retryCount + 1) + "/2");
                            }
                            if (retryCount >= 2) {
//                                saveKYCRequestDetails();
                                String rationCardNo = getIntent().getStringExtra("RcNumber");
                                String memberType = getIntent().getStringExtra("MemberType");
                                String benefId = getIntent().getStringExtra("BenefId");
                                String proxyId = getIntent().getStringExtra("ProxyId");
                                new BiometricAuthRetryFailedDialog(BenefBfdScanActivity.this, rationCardNo, benefId, proxyId, encodedBioMetricInfo, memberType).show();
                            }
                            else {
                                new BiometricAuthRetryDialog(BenefBfdScanActivity.this, retryCount).show();
                            }
                        }
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
                            Log.e("Response Error", "Response  Error is " + error.toString());
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
                                FPSDBHelper.getInstance(BenefBfdScanActivity.this).insertBiometric(posAadharAuthRequestDto);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/


                            retryCount++;
                            if(retryCount<=2) {
                                ((TextView) findViewById(R.id.aadharVerificationValueTv)).setText(String.valueOf(retryCount + 1) + "/2");
                            }
                            if (retryCount >= 2) {
                                saveKYCRequestDetails();
                                String rationCardNo = getIntent().getStringExtra("RcNumber");
                                String memberType = getIntent().getStringExtra("MemberType");
                                String benefId = getIntent().getStringExtra("BenefId");
                                String proxyId = getIntent().getStringExtra("ProxyId");
                                new BiometricAuthRetryFailedDialog(BenefBfdScanActivity.this, rationCardNo, benefId, proxyId, encodedBioMetricInfo, memberType).show();
                            }
                            else {
                                new BiometricAuthRetryDialog(BenefBfdScanActivity.this, retryCount).show();
                            }
                        }
                    });
            queue.add(req);
        }
        catch(Exception e) {
            Log.e("Mfs", "authentication exc...." + e.toString());
            dismissProgress();
            e.printStackTrace();
        }
    }

    private String getRequestJsonData() {
        JSONObject jsonData = new JSONObject();
        try {
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

    private void saveKYCRequestDetails() {
        try {
          byte[] bytes = isoFeatureSet;
          String aadharNo = fingerPrintAadhar;
          String benefId = getIntent().getStringExtra("BenefId");
          FPSDBHelper.getInstance(this).insertKYCRequestDetails(bytes, aadharNo, benefId);
        } catch (Exception e) {
            Log.e("BFDScanActivity ", "saveKYCRequestDetails exc...." + e.toString());
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
                }
                catch(Exception e) {}
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

    private void getEntitlement(String qrCodeString) {
        progressBar = new CustomProgressDialog(this);
        try {
            progressBar.show();
            BeneficiaryDto benef = FPSDBHelper.getInstance(this).beneficiaryFromOldCard(qrCodeString);
            Log.e("RationCardSalesActivity", "BeneficiaryDto..."+benef.toString());
            if (benef != null) {
                BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
                Util.LoggingQueue(this, "Entitlement", "Calculating entitlement");
                QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeString);
                if (qrCodeResponseReceived != null)
                    Log.e("RationCardSalesActivity", "QRTransactionResponseDto..." + qrCodeResponseReceived.toString());
                if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
                    NetworkConnection network = new NetworkConnection(this);
                    if (network.isNetworkAvailable()) {
                        qrCodeResponseReceived.setMode('G');
                    }
                    else {
                        qrCodeResponseReceived.setMode('F');
                    }
                    Util.LoggingQueue(BenefBfdScanActivity.this, "Ration card Sales", "Moving to Sales Entry Page");
                    qrCodeResponseReceived.setRegistered(true);
                    EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                    /*startActivity(new Intent(this, SalesEntryActivity.class));
                    finish();*/
                    Intent intent = new Intent(this, SalesEntryActivity.class);
                    intent.putExtra("SaleType", "AadharCardSale");
                    startActivity(intent);
                    finish();
                } else if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() == 0) {
                    Util.LoggingQueue(BenefBfdScanActivity.this, "Ration card Sales", "Beneficiary Entitlement details not available");
                    errorNavigation(getString(R.string.entitlemnt_finished));
                }
                else {
                    Util.LoggingQueue(BenefBfdScanActivity.this, "Ration card Sales", "Beneficiary Entitlement details not available");
                    errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
                }
            } else {
                Util.LoggingQueue(BenefBfdScanActivity.this, "Ration card Sales", "Beneficiary Data is not available in db");
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

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
    }

    protected void onStop() {
        super.onStop();
        UnInitScanner();
//        Toast.makeText(BenefBfdScanActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        UnInitScanner();
//        Toast.makeText(BenefBfdScanActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
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
                        Toast.makeText(BenefBfdScanActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(BenefBfdScanActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == 1204 || vid == 11279) {
            if (pid == 34323) {
                ret = Util.mfs100.LoadFirmware();
                if (ret != 0) {
                    Toast.makeText(BenefBfdScanActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BenefBfdScanActivity.this, "Loadfirmware success", Toast.LENGTH_SHORT).show();
                }
            } else if (pid == 4101) {
                ret = Util.mfs100.Init();
                if (ret != 0) {
                    Toast.makeText(BenefBfdScanActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    deviceIfo = Util.mfs100.GetDeviceInfo();
//                    Toast.makeText(BenefBfdScanActivity.this, "", Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(BenefBfdScanActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(BenefBfdScanActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(BenefBfdScanActivity.this, "Uninit Success", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("UnInitScanner.EX", e.toString());
        }
    }
    
}