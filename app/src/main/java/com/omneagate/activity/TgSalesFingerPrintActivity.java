package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantra.mfs100.DeviceInfo;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.MantraDto.Opts;
import com.omneagate.DTO.MantraDto.PidOptions;
import com.omneagate.DTO.RCAuthResponse;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FpsMemberData;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.AuthenticationFailedDialog;
import com.omneagate.activity.dialog.EncrypterUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.SalesFingerDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.omneagate.Util.XMLUtil.getRCAuthResponse;


/**
 * Created by root on 20/2/17.
 */
public class TgSalesFingerPrintActivity extends BaseActivity implements MFS100Event {

    private Button btnSubmit, btnScan, btnScanWithIris,btnBack;
    private ImageView imageViewBack;
    private TextView fpsmembersName;
    private TextView fpsmemberUid;
    String memberName, memberId, rationCardNo, Uid;
    Date posReqDate, authReqDate;
    String pidXml;

    byte[] Enroll_Template;
    ImageView imgFinger;
    byte[] isoFeatureSet;
    int timeout = 10000;
    Bitmap bitmapImg;
    DeviceInfo deviceIfo = null;
    int mfsVer = 41;

    String encodedBioMetricInfo = null;
    private RationCardDetailDialog rationCardDetailDialog;
    private TextToSpeech textToSpeech;
    MediaPlayer scan_fingerPrint;
    MediaPlayer beep;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
   // private Boolean isWebserviceFailed;
    private final String TAG=TgSalesFingerPrintActivity.class.getCanonicalName();

    private String encryptedPidXmlStr;
    private String encryptedSkey;
    private String encryptedHmacStr;
    private String mc,mi,rdsVer,rdsId,dpId,udc,ci;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_sales_finger_print);
        initView();

    }

    private void initView() {

        setPopUpPage();
        String rationCardNumber = FpsMemberData.getInstance().getRcNo();

        ((TextView) findViewById(R.id.ration_number)).setText(getString(R.string.rc_number) + " " + rationCardNumber);

        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo());
        updateDateTime();
        imgFinger = (ImageView) findViewById(R.id.imgFinger);

        scan_fingerPrint= MediaPlayer.create(this, R.raw.scan_fingerprint);
        beep = MediaPlayer.create(this, R.raw.censorbeep);

        networkConnection = new NetworkConnection(TgSalesFingerPrintActivity.this);

        if(!Util.needAadhaarAuth2) {

            if (Util.mfs100 == null) {
                Util.mfs100 = new MFS100(this, mfsVer);
            }

            if (Util.mfs100 != null) {
                Util.mfs100.SetApplicationContext(this);
            }

            CommonMethod.DeleteDirectory();
            CommonMethod.CreateDirectory();

            Bitmap bitmapImg = null;
            try {
                bitmapImg = (Bitmap) getIntent().getParcelableExtra("Image");
                imgFinger.setImageBitmap(bitmapImg);
                isoFeatureSet = Enroll_Template;
                if (isoFeatureSet.length > 0) {
                    beep.start();
                    findViewById(R.id.btnSubmit).setEnabled(true);
                    findViewById(R.id.btnSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(TgSalesFingerPrintActivity.this, TgSalesEntryActivity.class);
                startActivity(intentBack);
                finish();

            }
        });
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   new SendBenficiaryDetails().execute();

            }
        });

        btnScanWithIris = (Button) findViewById(R.id.btnScanWithIris);

        /*memberName = getIntent().getExtras().getString("memberName");
        memberId = getIntent().getExtras().getString("memberId");
        rationCardNo = getIntent().getExtras().getString("rationCardNo");
        Uid = getIntent().getExtras().getString("uid");*/

        btnScanWithIris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TgSalesFingerPrintActivity.this, TgSalesIrisScanActivity.class);
                i.putExtra("memberName",memberName);
                i.putExtra("uid",Uid);
                i.putExtra("memberId",memberId);
                i.putExtra("rationCardNo",rationCardNo);
                startActivity(i);
                finish();
            }
        });


        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (networkConnection.isNetworkAvailable()) {
                    if(Util.needAadhaarAuth2){
                        RdScan();
                    }else{
                        scan();
                    }
                } else {
                    displayNoInternetDailog();
                }
            }
        });

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imageViewBack = new Intent(TgSalesFingerPrintActivity.this, TgSalesEntryActivity.class);
                startActivity(imageViewBack);
                finish();

            }
        });

        fpsmembersName = (TextView) findViewById(R.id.fpsmembersname);
        fpsmemberUid = (TextView) findViewById(R.id.fpsmemberuid);

        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.sales_top_heading) + " > " + getString(R.string.scan_finger_print));


        memberName = getIntent().getExtras().getString("memberName");
        memberId = getIntent().getExtras().getString("memberId");
        rationCardNo = getIntent().getExtras().getString("rationCardNo");
        Uid = getIntent().getExtras().getString("uid");


        fpsmembersName.setText(memberName);
        fpsmemberUid.setText(Uid);

        if(Util.needUidmMsking && Uid.length()==12){
            fpsmemberUid.setText(""+Util.maskAadhaarNumber(Uid,"XXXXXXXX####"));
        }else{
            fpsmemberUid.setText(""+Uid);
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    if(Util.needSpeakOutForFingerprint && !GlobalAppState.getInstance().language.equals("te")) {
                        textToSpeech.setLanguage(Locale.US);
                        String toSpeak = "Place Your Finger on Fingerprint Scanner";
                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }

                }
            }
        });
        if(GlobalAppState.getInstance().language.equals("te")){
            scan_fingerPrint.start();
        }

    }

    void displayNoInternetDailog(){
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgSalesFingerPrintActivity.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
    }

    private void scan() {
        Bitmap bitmapImg = StartSyncCapture();
        // imgFinger.setImageResource(0);
        imgFinger.setImageBitmap(null);
        imgFinger.setImageBitmap(bitmapImg);
        isoFeatureSet = Enroll_Template;
        try {
            if (isoFeatureSet.length > 0) {
                if(Util.needSpeakOutForFingerprint) {
                    beep.start();
                }
                Log.e("BenefBfdScan", "isoFeatureSet..." + Arrays.toString(isoFeatureSet));
                //findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
                findViewById(R.id.btnSubmit).setEnabled(true);
                findViewById(R.id.btnSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));
                new SendBenficiaryDetails().execute();

            }
        } catch (Exception e) {
        }
    }

    private class SendBenficiaryDetails extends AsyncTask<String, RCAuthResponse, RCAuthResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgSalesFingerPrintActivity.this, getString(R.string.authenticatingFingerPrint));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        @Override
        protected RCAuthResponse doInBackground(String... params) {
           if(Util.needAadhaarAuth2){
               return authBeneficiaryMember2();
           }else{
               return authBeneficiaryMember();
           }

        }

        @Override
        protected void onPostExecute(RCAuthResponse rcAuthResponse) {
            super.onPostExecute(rcAuthResponse);
            rationCardDetailDialog.dismiss();
            Log.e("ScanFingerPrintActivit", "dealerAuthResponse :" + rcAuthResponse.getRespMsgCode());
            Log.e("ScanFingerPrintActivit", "dealerAuthResponse :" + rcAuthResponse.getRespMessage());
           /* try {
                if (rcAuthResponse.getRespMsgCode() != null) {
                    if (rcAuthResponse.getRespMsgCode().equalsIgnoreCase("0")) {

//                        AuthenticationSuccessDialog authenticationSuccessDialog = new AuthenticationSuccessDialog(TgSalesFingerPrintActivity.this, getString(R.string.authentication_success),"TgSalesFingerPrintActivity",rcAuthResponse);
//                        authenticationSuccessDialog.setCanceledOnTouchOutside(false);
//                        authenticationSuccessDialog.show();

                        EntitlementResponse.getInstance().setRcAuthResponse(null);
                        EntitlementResponse.getInstance().clear();
                        EntitlementResponse.getInstance().setRcAuthResponse(rcAuthResponse);
                        Intent loginIntent = new Intent(TgSalesFingerPrintActivity.this, TgProductListActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        finish();

                    } else {
                        AuthenticationFailedDialog authenticationFailedDialog = new AuthenticationFailedDialog(TgSalesFingerPrintActivity.this, getString(R.string.authentication_failed));
                        authenticationFailedDialog.setCanceledOnTouchOutside(false);
                        authenticationFailedDialog.show();

                    }
                }
            } catch (Exception e) {
                Log.e("ScanFingerPrint", "Exception " + e.toString());
            }*/
        }
    }

    private RCAuthResponse authBeneficiaryMember() {
        RCAuthResponse rcAuthResponse = new RCAuthResponse();
        try {
            encodedBioMetricInfo = Base64.encodeToString(isoFeatureSet, Base64.DEFAULT);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Date currentData;
            if(Util.needInternalClock && GlobalAppState.serverDate !=null){
                currentData=GlobalAppState.serverDate;
            }else{
                currentData = new Date();
            }

           // GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(currentData);
            authReqDate = sdf.parse(dateString);

            //   pidXml = buildPidXml(dateString, encodedBioMetricInfo, null);

            String fileNameWithPath =
                    Environment.getExternalStorageDirectory().toString() + "/Fps/" + "uidai_auth_prod.cer";

            Log.e("TgScanFingerPrint", "file Name " + fileNameWithPath);
            EncrypterUtil encrypterUtil = new EncrypterUtil(fileNameWithPath);

            String expDate = encrypterUtil.getCertificateIdentifier();

            Log.e("TgScanFingerPrint", "Certificate Expiry Date : " + expDate);

            byte[] sessionKey = encrypterUtil.generateSessionKey();
            String sKey = Base64.encodeToString(sessionKey, Base64.DEFAULT);
            byte[] encryptedSessionKey = encrypterUtil.encryptUsingPublicKey(sessionKey);
            String encryptedSkey = Base64.encodeToString(encryptedSessionKey, Base64.DEFAULT);
            String certificateIdentifier = encrypterUtil.getCertificateIdentifier();

         /*   int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);*/

            String pidXml = buildPidXml(dateString, encodedBioMetricInfo, null);
            byte[] pidXmlBytes = pidXml.getBytes();
            byte[] encryptedPidXmlBytes = encrypterUtil.encryptUsingSessionKey(sessionKey, pidXmlBytes);
            String encryptedPidXmlStr = Base64.encodeToString(encryptedPidXmlBytes, Base64.DEFAULT);
            byte[] hmacBytes = generateSha256Hash(pidXmlBytes);
            String hmacStr = Base64.encodeToString(hmacBytes, Base64.DEFAULT);
            byte[] encryptedHmacBytes = encrypterUtil.encryptUsingSessionKey(sessionKey, hmacBytes);
            String encryptedHmacStr = Base64.encodeToString(encryptedHmacBytes, Base64.DEFAULT);


            encryptedPidXmlStr = encryptedPidXmlStr.replaceAll("\n", "");
            encryptedSkey = encryptedSkey.replaceAll("\n", "");
            encryptedHmacStr = encryptedHmacStr.replaceAll("\n", "");

            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();

            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            LoginData.getInstance().setUid(Uid);
            LoginData.getInstance().setMemberName(memberName);
            inputMap.put("uidNo", Uid);
            LoginData.getInstance().setRationCardNo(rationCardNo);
            inputMap.put("rationCard", rationCardNo);
            LoginData.getInstance().setMemberId(memberId);
            inputMap.put("memberId", memberId);
            inputMap.put("data", encryptedPidXmlStr);
            inputMap.put("skey", encryptedSkey);
            inputMap.put("hmac", encryptedHmacStr);
            inputMap.put("bioType", "FMR");
            Log.e(TAG,"<======= Request Time =======> "+dateString);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            Date currentDate1 = new Date();
          /*  if(Util.needInternalClock && GlobalAppState.serverDate !=null){
                currentDate1=GlobalAppState.serverDate;
            }else{
                currentDate1 = new Date();
            }*/

            String requestTime = sdf1.format(currentDate1);
            inputMap.put("requestTime",requestTime);


            String isWebserviceFailed = null;
            SharedPreferences prefs = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE);
            if (prefs != null) {
                isWebserviceFailed = prefs.getString("LastAuthenticationFailed", null);
            }
            if (isWebserviceFailed == null) {
                inputMap.put("responseTime", null);
            }else if(isWebserviceFailed !=null && isWebserviceFailed.equalsIgnoreCase("false")){
                inputMap.put("responseTime", null);
            } else if (isWebserviceFailed != null && isWebserviceFailed.equalsIgnoreCase("true")) {
                if (LoginData.getInstance().getResponseTime() != null) {
                    inputMap.put("responseTime", LoginData.getInstance().getResponseTime());
                } else {
                    inputMap.put("responseTime", null);
                }

            }

            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            inputMap.put("payType","" + LoginData.getInstance().getPayType());

            rcAuthResponse = getRCAuthResponse(inputMap);

            if (rcAuthResponse.getRespMsgCode() != null) {
                if (rcAuthResponse.getRespMsgCode().equalsIgnoreCase("0")) {
                    //isWebserviceFailed=false;
                    SharedPreferences.Editor editor = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE).edit();
                    editor.putString("LastAuthenticationFailed", "false");
                    editor.commit();
                    Date currentDate2 = new Date();
                    /*if(Util.needInternalClock && GlobalAppState.serverDate !=null){
                        currentDate2=GlobalAppState.serverDate;
                    }else{
                        currentDate2 = new Date();
                    }*/

                    String responseTime = sdf1.format(currentDate2);
                    LoginData.getInstance().setResponseTime(responseTime);
                    Log.e(TAG,"<======= Response Time =======> "+responseTime);

                    EntitlementResponse.getInstance().setRcAuthResponse(null);
                    EntitlementResponse.getInstance().clear();
                    EntitlementResponse.getInstance().setRcAuthResponse(rcAuthResponse);

                    boolean isValeMoreThanZero = false;
                    for (int i = 0; i < rcAuthResponse.getItemsAllotedList().size(); i++) {
                        if (rcAuthResponse.getItemsAllotedList().get(i).getProductBalanceQty() != null
                                && rcAuthResponse.getItemsAllotedList().get(i).getProductBalanceQty() > 0) {
                            isValeMoreThanZero = true;
                            break;

                        }

                    }
                    if (isValeMoreThanZero) {
                        Intent loginIntent = new Intent(TgSalesFingerPrintActivity.this, TgProductListActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SalesFingerDialog salesFingerDialog = new SalesFingerDialog(TgSalesFingerPrintActivity.this);
                                salesFingerDialog.setCanceledOnTouchOutside(false);
                                salesFingerDialog.show();
                            }});

                    }


                }else{
                    //isWebserviceFailed=true;
                    SharedPreferences.Editor editor = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE).edit();
                    editor.putString("LastAuthenticationFailed", "true");
                    editor.commit();
                    Date currentDate2 = new Date();
                   /* if(Util.needInternalClock && GlobalAppState.serverDate !=null){
                        currentDate2=GlobalAppState.serverDate;
                    }else{
                        currentDate2 = new Date();
                    }*/

                    String responseTime = sdf1.format(currentDate2);
                    LoginData.getInstance().setResponseTime(responseTime);
                }
            }


            return rcAuthResponse;

        }catch (final FPSException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences.Editor editor = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE).edit();
                    editor.putString("LastAuthenticationFailed", "true");
                    editor.commit();

                   // isWebserviceFailed = true;
                    Date currentDate2 = new Date();
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                    String responseTime = sdf2.format(currentDate2);
                    LoginData.getInstance().setResponseTime(responseTime);

                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgSalesFingerPrintActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return rcAuthResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
            return rcAuthResponse;
        }

    }

    private String buildPidXml(String timeStamp, String encodedBiometric, String encodedBiometric2ndFingur) {
        StringBuffer buff = new StringBuffer();
        buff.append("<Pid ts=");
        buff.append("\"" + timeStamp + "\"");
        buff.append(" ver=");
        buff.append("\"" + "1.0" + "\"");
        buff.append(">");
        buff.append("<Bios>");
        buff.append("<Bio type=\"FMR\" posh=\"UNKNOWN\">");
        buff.append(encodedBiometric);
        buff.append("</Bio>");
        buff.append("</Bios></Pid>");
        return buff.toString();
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

    private Bitmap StartSyncCapture() {
        try {
            Thread trd = new Thread(new Runnable() {
                @Override
                public void run() {
                    FingerData fingerData = new FingerData();
                    int ret = Util.mfs100.AutoCapture(fingerData, timeout, false, true);
                    Log.e("sample app", "ret value..." + ret);
                    Log.e("sample app", "ret value..." + Util.mfs100.GetErrorMsg(ret));
                    if (ret != 0) {
                        Toast.makeText(TgSalesFingerPrintActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                    } else {
                        Enroll_Template = new byte[fingerData.ISOTemplate().length];
                        System.arraycopy(fingerData.ISOTemplate(), 0, Enroll_Template, 0, fingerData.ISOTemplate().length);
                        bitmapImg = BitmapFactory.decodeByteArray(fingerData.FingerImage(), 0, fingerData.FingerImage().length);
                    }
                }
            });
            trd.run();
            trd.join();
        } catch (Exception ex) {
            CommonMethod.writeLog("Exception in ContinuesScan(). Message:- " + ex.getMessage());
        } finally {
        }
        return bitmapImg;
    }

    @Override
    public void onBackPressed() {
        Intent intentSale = new Intent(TgSalesFingerPrintActivity.this, TgSalesEntryActivity.class);
        startActivity(intentSale);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnInitScanner();

        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if(scan_fingerPrint!=null){
            scan_fingerPrint.stop();
            scan_fingerPrint.release();
            scan_fingerPrint = null;
        }
        if(beep!=null){
            beep.stop();
            beep.release();
            beep = null;
        }

    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        int ret = 0;
        if (!hasPermission) {
            Toast.makeText(TgSalesFingerPrintActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == 1204 || vid == 11279) {
            if (pid == 34323) {
                ret = Util.mfs100.LoadFirmware();
                if (ret != 0) {
                    Toast.makeText(TgSalesFingerPrintActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TgSalesFingerPrintActivity.this, "Loadfirmware success", Toast.LENGTH_SHORT).show();
                }
            } else if (pid == 4101) {
                ret = Util.mfs100.Init();
                if (ret != 0) {
                    Toast.makeText(TgSalesFingerPrintActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
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

    }

    @Override
    public void OnHostCheckFailed(String error) {
        try {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        UnInitScanner();
    }


    public void UnInitScanner() {
        try {
            deviceIfo = null;
            if (Util.mfs100 != null) {
                Util.mfs100.Dispose();
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
    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
         logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgSalesFingerPrintActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
    private void RdScan(){
        try {
            String pidOption = getPIDOptions();
            Log.e(TAG,"PID OPTION : "+pidOption);
            if (pidOption != null) {
                Intent intent2 = new Intent();
                intent2.setAction("in.gov.uidai.rdservice.fp.CAPTURE");
                intent2.putExtra("PID_OPTIONS", pidOption);
                startActivityForResult(intent2, 2);
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }
    private String getPIDOptions() {
        try {
            int fingerCount = 1;
            int fingerType = 0;
            int fingerFormat = 0;
            String pidVer = "2.0";
            String timeOut = "10000";
            String posh = "UNKNOWN";


            Opts opts = new Opts();
            opts.fCount = String.valueOf(fingerCount);
            opts.fType = String.valueOf(fingerType);
            opts.iCount = "0";
            opts.iType = "0";
            opts.pCount = "0";
            opts.pType = "0";
            opts.format = String.valueOf(fingerFormat);
            opts.pidVer = pidVer;
            opts.timeout = timeOut;
//            opts.otp = "";
            opts.posh = posh;
            String env = "P";
          /*  switch (rgEnv.getCheckedRadioButtonId()) {
                case R.id.rbStage:
                    env = "S";
                    break;
                case R.id.rbPreProd:
                    env = "PP";
                    break;
                case R.id.rbProd:
                    env = "P";
                    break;
            }*/
            opts.env = env;

            PidOptions pidOptions = new PidOptions();
            pidOptions.ver = pidVer;
            pidOptions.Opts = opts;

            Serializer serializer = new Persister();
            StringWriter writer = new StringWriter();
            serializer.write(pidOptions, writer);
            return writer.toString();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (data != null) {
                            String result = data.getStringExtra("PID_DATA");
                            parseXmlData(result);
                            Log.e(TAG,"PID DATA : "+result);
                            Serializer serializer = new Persister();
                            //  PidData pidData = serializer.read(PidData.class, result);

                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error while deserialize pid data", e);
                    }
                }
                break;
        }
    }
    private void parseXmlData(String xmlData){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new java.io.StringReader(xmlData)));
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("PidData");

            Log.e(TAG,"<---------------START-------------->");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    NamedNodeMap mapResp = eElement.getElementsByTagName("Resp").item(0).getAttributes();
                    String errorCode=((Node)mapResp.getNamedItem("errCode")).getTextContent();
                    String errorInfo=((Node)mapResp.getNamedItem("errInfo")).getTextContent();
                    System.out.println("errCode   : "+errorCode);
                    System.out.println("errorInfo   : "+errorInfo);
                    if(errorCode.equalsIgnoreCase("0")){

                        Log.e(TAG,"Data : " + eElement.getElementsByTagName("Data").item(0).getTextContent());
                        encryptedPidXmlStr=eElement.getElementsByTagName("Data").item(0).getTextContent();
                        Log.e(TAG,"Hmac : " + eElement.getElementsByTagName("Hmac").item(0).getTextContent());
                        encryptedHmacStr=eElement.getElementsByTagName("Hmac").item(0).getTextContent();
                        Log.e(TAG,"Skey : " + eElement.getElementsByTagName("Skey").item(0).getTextContent());
                        encryptedSkey=eElement.getElementsByTagName("Skey").item(0).getTextContent();

                        NamedNodeMap mapDeviceINfo = eElement.getElementsByTagName("DeviceInfo").item(0).getAttributes();
                        dpId=((Node)mapDeviceINfo.getNamedItem("dpId")).getTextContent();
                        Log.e(TAG,"dpId : "+dpId);

                        mc=((Node)mapDeviceINfo.getNamedItem("mc")).getTextContent();
                        Log.e(TAG,"mc :"+mc);

                        mi=((Node)mapDeviceINfo.getNamedItem("mi")).getTextContent();
                        Log.e(TAG,"mi :"+mi);

                        rdsId=((Node)mapDeviceINfo.getNamedItem("rdsId")).getTextContent();
                        Log.e(TAG,"rdsId :"+rdsId);

                        rdsVer=((Node)mapDeviceINfo.getNamedItem("rdsVer")).getTextContent();
                        Log.e(TAG,"rdsVer :"+rdsVer);

                        udc=((Node)mapDeviceINfo.getNamedItem("dc")).getTextContent();
                        Log.e(TAG,"dc :"+udc);

                        NamedNodeMap mapSkey = eElement.getElementsByTagName("Skey").item(0).getAttributes();
                        ci=((Node)mapSkey.getNamedItem("ci")).getTextContent();
                        Log.e(TAG,"ci   : "+ci);

                        beep.start();
                        new SendBenficiaryDetails().execute();

                    }else{
                        Toast.makeText(TgSalesFingerPrintActivity.this, ""+errorInfo, Toast.LENGTH_SHORT).show();
                    }

                }
            }
            Log.e(TAG,"<---------------END-------------->");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private RCAuthResponse authBeneficiaryMember2() {
        RCAuthResponse rcAuthResponse = new RCAuthResponse();
        try {
            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();

            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            LoginData.getInstance().setUid(Uid);
            LoginData.getInstance().setMemberName(memberName);
            inputMap.put("uidNo", Uid);
            LoginData.getInstance().setRationCardNo(rationCardNo);
            inputMap.put("rationCard", rationCardNo);
            LoginData.getInstance().setMemberId(memberId);
            inputMap.put("memberId", memberId);
            inputMap.put("data", encryptedPidXmlStr);
            inputMap.put("skey", encryptedSkey);
            inputMap.put("hmac", encryptedHmacStr);
            inputMap.put("bioType", "FMR");

            inputMap.put("udc", udc);
            inputMap.put("dpId", dpId);
            inputMap.put("rdsId", rdsId);
            inputMap.put("rdsVer", rdsVer);
            inputMap.put("mi", mi);
            inputMap.put("mc", mc);
            inputMap.put("ci", ci);


            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
            Date currentDate1 = new Date();
            String requestTime = sdf1.format(currentDate1);
            inputMap.put("requestTime",requestTime);

            String isWebserviceFailed = null;
            SharedPreferences prefs = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE);
            if (prefs != null) {
                isWebserviceFailed = prefs.getString("LastAuthenticationFailed", null);
            }
            if (isWebserviceFailed == null) {
                inputMap.put("responseTime", null);
            }else if(isWebserviceFailed !=null && isWebserviceFailed.equalsIgnoreCase("false")){
                inputMap.put("responseTime", null);
            } else if (isWebserviceFailed != null && isWebserviceFailed.equalsIgnoreCase("true")) {
                if (LoginData.getInstance().getResponseTime() != null) {
                    inputMap.put("responseTime", LoginData.getInstance().getResponseTime());
                } else {
                    inputMap.put("responseTime", null);
                }

            }

            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());

            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            inputMap.put("payType","" + LoginData.getInstance().getPayType());

            rcAuthResponse = getRCAuthResponse(inputMap);

            if (rcAuthResponse.getRespMsgCode() != null) {
                if (rcAuthResponse.getRespMsgCode().equalsIgnoreCase("0")) {

                    SharedPreferences.Editor editor = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE).edit();
                    editor.putString("LastAuthenticationFailed", "false");
                    editor.commit();

                    Date currentDate2 = new Date();
                    String responseTime = sdf1.format(currentDate2);
                    LoginData.getInstance().setResponseTime(responseTime);
                    Log.e(TAG,"<======= Response Time =======> "+responseTime);

                    EntitlementResponse.getInstance().setRcAuthResponse(null);
                    EntitlementResponse.getInstance().clear();
                    EntitlementResponse.getInstance().setRcAuthResponse(rcAuthResponse);

                    boolean isValeMoreThanZero = false;
                    for (int i = 0; i < rcAuthResponse.getItemsAllotedList().size(); i++) {
                        if (rcAuthResponse.getItemsAllotedList().get(i).getProductBalanceQty() != null
                                && rcAuthResponse.getItemsAllotedList().get(i).getProductBalanceQty() > 0) {
                            isValeMoreThanZero = true;
                            break;

                        }

                    }
                    if (isValeMoreThanZero) {
                        Intent loginIntent = new Intent(TgSalesFingerPrintActivity.this, TgProductListActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SalesFingerDialog salesFingerDialog = new SalesFingerDialog(TgSalesFingerPrintActivity.this);
                                salesFingerDialog.setCanceledOnTouchOutside(false);
                                salesFingerDialog.show();
                            }});
                    }

                }else{
                    //isWebserviceFailed=true;
                    SharedPreferences.Editor editor = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE).edit();
                    editor.putString("LastAuthenticationFailed", "true");
                    editor.commit();
                    Date currentDate2 = new Date();
                   /* if(Util.needInternalClock && GlobalAppState.serverDate !=null){
                        currentDate2=GlobalAppState.serverDate;
                    }else{
                        currentDate2 = new Date();
                    }*/

                    String responseTime = sdf1.format(currentDate2);
                    LoginData.getInstance().setResponseTime(responseTime);
                }
            }


            return rcAuthResponse;

        }catch (final FPSException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences.Editor editor = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE).edit();
                    editor.putString("LastAuthenticationFailed", "true");
                    editor.commit();

                    // isWebserviceFailed = true;
                    Date currentDate2 = new Date();
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
                    String responseTime = sdf2.format(currentDate2);
                    LoginData.getInstance().setResponseTime(responseTime);

                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgSalesFingerPrintActivity.this, "" + e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return rcAuthResponse;
        }
        catch (Exception e) {
            e.printStackTrace();
            return rcAuthResponse;
        }

    }

}