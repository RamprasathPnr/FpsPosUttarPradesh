package com.omneagate.activity;

import android.content.Intent;
import android.content.res.AssetManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mantra.mfs100.DeviceInfo;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GeneralResponse;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.AuthenticationFailedDialog;
import com.omneagate.activity.dialog.AuthenticationSuccessDialog;
import com.omneagate.activity.dialog.EncrypterUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TgMobileNumUpdateDetailActivity extends BaseActivity implements View.OnClickListener, MFS100Event {

    private ImageView imageViewBack, img_iris_member, img_iris_dealer;
    private String mobileNum, aadhaarNum, OTP;
    private RationCardDetailDialog rationCardDetailDialog;
    Spinner spinnerFinger;

    byte[] Enroll_Template;
    ImageView imgFinger;
    byte[] isoFeatureSet;
    int timeout = 10000;
    Bitmap bitmapImg;
    DeviceInfo deviceIfo = null;
    int mfsVer = 41;

    byte[] isoFeatureSet_01 = new byte[0];
    byte[] isoFeatureSet_02 = new byte[0];
    private Button btnFingerScan;
    private Button btnFingerPrintSubmit;
    private Button btnScanWithIris;
    String selectedFinger = "", selectedPosition = "";
    Date posReqDate, authReqDate;
    private final String TAG = TgMobileNumberUpdateActivity.class.getCanonicalName();

    TextToSpeech textToSpeech;
    MediaPlayer scan_fingerPrint;
    MediaPlayer beep;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_mobile_num_update_detail);
        initView();

        Util.setTamilText((TextView) findViewById(R.id.top_textView), getString(R.string.mobile_number_update));
    }


    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();

        if (Util.mfs100 == null) {
            Util.mfs100 = new MFS100(this, mfsVer);
        }

        if (Util.mfs100 != null) {
            Util.mfs100.SetApplicationContext(this);
        }

        try {
            String destDir = Environment.getExternalStorageDirectory().toString() + "/Fps/";
            copyAssetToDir(TgMobileNumUpdateDetailActivity.this.getAssets(), "uidai_auth_prod.cer", destDir);
        } catch (Exception e) {
            Log.e("Mfs", "copy asset exc..." + e);
        }

        CommonMethod.DeleteDirectory();
        CommonMethod.CreateDirectory();

       /* Bitmap bitmapImg = null;
        try {
            bitmapImg = (Bitmap) getIntent().getParcelableExtra("Image");
            imgFinger.setImageBitmap(bitmapImg);
            isoFeatureSet = Enroll_Template;
            if (isoFeatureSet.length > 0) {
                findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            e.printStackTrace();
        }*/

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        img_iris_member = (ImageView) findViewById(R.id.img_iris_member);
        img_iris_dealer = (ImageView) findViewById(R.id.img_iris_dealer);


        aadhaarNum = getIntent().getExtras().getString("customerUid");
        OTP = getIntent().getExtras().getString("OTP");
        mobileNum = getIntent().getExtras().getString("MobileNo");


        ((TextView) findViewById(R.id.txt_mobile_num)).setText(getResources().getText(R.string.MOBILE_NO) + " " + mobileNum);
        ((TextView) findViewById(R.id.txt_aadhaar_card_num)).setText(getResources().getText(R.string.AADHAAR_CARD) + " " + aadhaarNum);


        btnFingerScan = (Button) findViewById(R.id.btnFingerScan);
        btnFingerScan.setOnClickListener(this);

        btnFingerPrintSubmit = (Button) findViewById(R.id.btnFingerPrintSubmit);
        btnFingerPrintSubmit.setOnClickListener(this);

        btnScanWithIris = (Button) findViewById(R.id.btnScanWithIris);
        btnScanWithIris.setOnClickListener(this);

        ArrayList<String> finger = new ArrayList<String>();
        finger.add(getResources().getString(R.string.member_));
        finger.add(getResources().getString(R.string.DEALER));

        spinnerFinger = (Spinner) findViewById(R.id.fingerSpinner);
        ArrayAdapter<String> fingerAdapt = new ArrayAdapter<>(TgMobileNumUpdateDetailActivity.this, android.R.layout.simple_spinner_item, finger);
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
                Log.e("TenFingerReg", "spinnerFinger..." + selectedFinger);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        scan_fingerPrint = MediaPlayer.create(this, R.raw.scan_fingerprint);
        beep = MediaPlayer.create(this, R.raw.censorbeep);

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

        if(Util.needSpeakOutForFingerprint && GlobalAppState.getInstance().language.equals("te")){
            scan_fingerPrint.start();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;
            default:
                break;

            case R.id.btnFingerScan:
                if (selectedFinger == null || selectedFinger.equalsIgnoreCase("") || selectedFinger.equalsIgnoreCase("Select")) {
                    Toast.makeText(TgMobileNumUpdateDetailActivity.this, R.string.Please_select_the_member_or_dealer, Toast.LENGTH_SHORT).show();
                } else {
                    StartSyncCapture();
                    if (Enroll_Template != null) {
                        if (Enroll_Template.length > 0) {
                            if (selectedPosition.equalsIgnoreCase("0")) {
                                if (!(isoFeatureSet_01.length > 0)) {
                                    isoFeatureSet_01 = new byte[Enroll_Template.length];
                                    isoFeatureSet_01 = Enroll_Template;
                                    if (isoFeatureSet_01.length > 0) {
                                        beep.start();
                                        findViewById(R.id.btnFingerPrintSubmit).setEnabled(true);
                                        findViewById(R.id.btnFingerPrintSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));
                                        img_iris_member.setImageBitmap(bitmapImg);
                                        Enroll_Template = new byte[0];

                                    }
                                } else {
                                    Toast.makeText(TgMobileNumUpdateDetailActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (!(isoFeatureSet_02.length > 0)) {
                                    isoFeatureSet_02 = new byte[Enroll_Template.length];
                                    isoFeatureSet_02 = Enroll_Template;
                                    if (isoFeatureSet_02.length > 0) {
                                        beep.start();
                                        Enroll_Template = new byte[0];
                                        img_iris_dealer.setImageBitmap(bitmapImg);

                                    }
                                } else {
                                    Toast.makeText(TgMobileNumUpdateDetailActivity.this, R.string.scanned_already, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
                break;
            case R.id.btnFingerPrintSubmit:
                new SendMobileNumberUpdate().execute();
                break;
            case R.id.btnScanWithIris:
                Intent scanIrisIntent = new Intent(TgMobileNumUpdateDetailActivity.this, TgMobileNumberUpdateWithIrisActivity.class);
                scanIrisIntent.putExtra("customerUid", aadhaarNum);
                scanIrisIntent.putExtra("OTP", OTP);
                scanIrisIntent.putExtra("MobileNo", mobileNum);
                startActivity(scanIrisIntent);
                finish();
                break;
        }

    }


    private class SendMobileNumberUpdate extends AsyncTask<String, GeneralResponse, GeneralResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgMobileNumUpdateDetailActivity.this, getString(R.string.authenticatingFingerPrint));
            rationCardDetailDialog.show();
        }

        @Override
        protected GeneralResponse doInBackground(String... params) {
            return sendMobileNumber();
        }

        @Override
        protected void onPostExecute(GeneralResponse generalResponse) {
            super.onPostExecute(generalResponse);

            rationCardDetailDialog.dismiss();
            try {
                if (generalResponse.getRespMsgCode() != null) {
                    if (generalResponse.getRespMsgCode().equalsIgnoreCase("0")) {
                        AuthenticationSuccessDialog authenticationSuccessDialog = new AuthenticationSuccessDialog(TgMobileNumUpdateDetailActivity.this, getString(R.string.authentication_success), "TgScanFingerPrintActivity");
                        authenticationSuccessDialog.setCanceledOnTouchOutside(false);
                        authenticationSuccessDialog.show();

                    }
                }
            } catch (Exception e) {
                Log.e("ScanFingerPrint", "Exception " + e.toString());

            }

        }
    }

    private GeneralResponse sendMobileNumber() {
        try {


            final String encodedBioMetricInfo_01 = Base64.encodeToString(isoFeatureSet_01, Base64.DEFAULT);
            final String encodedBioMetricInfo_02 = Base64.encodeToString(isoFeatureSet_02, Base64.DEFAULT);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            authReqDate = sdf.parse(dateString);

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

            int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);

            //MEMBER

            String pidXmlMember = buildPidXml(dateString, encodedBioMetricInfo_01, null);
            byte[] pidXmlBytesMember = pidXmlMember.getBytes();
            byte[] encryptedPidXmlBytesMember = encrypterUtil.encryptUsingSessionKey(sessionKey, pidXmlBytesMember);
            String encryptedPidXmlStrMember = Base64.encodeToString(encryptedPidXmlBytesMember, Base64.DEFAULT);
            byte[] hmacBytesMember = generateSha256Hash(pidXmlBytesMember);
            String hmacStrMember = Base64.encodeToString(hmacBytesMember, Base64.DEFAULT);
            byte[] encryptedHmacBytesMember = encrypterUtil.encryptUsingSessionKey(sessionKey, hmacBytesMember);
            String encryptedHmacStrMember = Base64.encodeToString(encryptedHmacBytesMember, Base64.DEFAULT);

            encryptedPidXmlStrMember = encryptedPidXmlStrMember.replaceAll("\n", "");
            encryptedSkey = encryptedSkey.replaceAll("\n", "");
            encryptedHmacStrMember = encryptedHmacStrMember.replaceAll("\n", "");

            //DEALER

            String pidXmlDealer = buildPidXml(dateString, encodedBioMetricInfo_02, null);
            byte[] pidXmlBytesDealer = pidXmlDealer.getBytes();
            byte[] encryptedPidXmlBytesDealer = encrypterUtil.encryptUsingSessionKey(sessionKey, pidXmlBytesDealer);
            String encryptedPidXmlStrDealer = Base64.encodeToString(encryptedPidXmlBytesDealer, Base64.DEFAULT);
            byte[] hmacBytesDealer = generateSha256Hash(pidXmlBytesDealer);
            String hmacStrDealer = Base64.encodeToString(hmacBytesDealer, Base64.DEFAULT);
            byte[] encryptedHmacBytesDealer = encrypterUtil.encryptUsingSessionKey(sessionKey, hmacBytesDealer);
            String encryptedHmacStrDealer = Base64.encodeToString(encryptedHmacBytesDealer, Base64.DEFAULT);


            encryptedPidXmlStrDealer = encryptedPidXmlStrDealer.replaceAll("\n", "");
            encryptedHmacStrDealer = encryptedHmacStrDealer.replaceAll("\n", "");


            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            inputMap.put("mobileNo", mobileNum);
            inputMap.put("mobileOtp", OTP);
            inputMap.put("custmerUid", aadhaarNum);
            inputMap.put("custmerPBlock", encryptedPidXmlStrMember);
            inputMap.put("custmerskey", encryptedSkey);
            inputMap.put("custmerhmac", encryptedHmacStrMember);
            inputMap.put("timeStamp", dateString);
            inputMap.put("dealerUid", LoginData.getInstance().getFpsUserUid());
            inputMap.put("dealerPBlock", encryptedPidXmlStrDealer);
            inputMap.put("dealerskey", encryptedSkey);
            inputMap.put("dealerhmac", encryptedHmacStrDealer);
            inputMap.put("consent", aadhaarNum);

            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", XMLUtil.PASSWORD);

            GeneralResponse generalResponse = XMLUtil.updateMobileNumber(inputMap);

            return generalResponse;

        } catch (final FPSException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgMobileNumUpdateDetailActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
                        Toast.makeText(TgMobileNumUpdateDetailActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgMobileNumUpdateDetailActivity.this, TgMobileNumberUpdateActivity.class);
        startActivity(backIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            Log.i("Main", "cancel timer");
            logoutTimeTask = null;
        }
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            Log.i("Main", "cancel timer");
            logoutTimeTask = null;
        }

    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        int ret = 0;
        if (!hasPermission) {
            Toast.makeText(TgMobileNumUpdateDetailActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == 1204 || vid == 11279) {
            if (pid == 34323) {
                ret = Util.mfs100.LoadFirmware();
                if (ret != 0) {
                    Toast.makeText(TgMobileNumUpdateDetailActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TgMobileNumUpdateDetailActivity.this, "Load firmware success", Toast.LENGTH_SHORT).show();
                }
            } else if (pid == 4101) {
                ret = Util.mfs100.Init();
                if (ret != 0) {
                    Toast.makeText(TgMobileNumUpdateDetailActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
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
        UnInitScanner();
//        Toast.makeText(TenFingerRegistrationActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnHostCheckFailed(String err) {
        try {
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
        }
    }

    public void UnInitScanner() {
        try {
            deviceIfo = null;
            if (Util.mfs100 != null) {
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
            Intent i = new Intent(TgMobileNumUpdateDetailActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
