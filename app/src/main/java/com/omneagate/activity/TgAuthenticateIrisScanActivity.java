package com.omneagate.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iritech.driver.UsbNotification;
import com.iritech.iddk.android.HIRICAMM;
import com.iritech.iddk.android.Iddk2000Apis;
import com.iritech.iddk.android.IddkCaptureMode;
import com.iritech.iddk.android.IddkCaptureOperationMode;
import com.iritech.iddk.android.IddkCaptureStatus;
import com.iritech.iddk.android.IddkCommStd;
import com.iritech.iddk.android.IddkConfig;
import com.iritech.iddk.android.IddkDataBuffer;
import com.iritech.iddk.android.IddkDeviceConfig;
import com.iritech.iddk.android.IddkDeviceInfo;
import com.iritech.iddk.android.IddkEyeSubType;
import com.iritech.iddk.android.IddkImage;
import com.iritech.iddk.android.IddkImageFormat;
import com.iritech.iddk.android.IddkImageKind;
import com.iritech.iddk.android.IddkInteger;
import com.iritech.iddk.android.IddkIrisQuality;
import com.iritech.iddk.android.IddkIsoRevision;
import com.iritech.iddk.android.IddkQualityMode;
import com.iritech.iddk.android.IddkResult;
import com.omneagate.DTO.AuthenticateMember;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSCardPosition;
import com.omneagate.DTO.RCAuthResponse;
import com.omneagate.Iris.DemoConfig;
import com.omneagate.Iris.DemoUtility;
import com.omneagate.Iris.IddkCaptureInfo;
import com.omneagate.Iris.MediaData;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.AuthenticationFailedDialog;
import com.omneagate.activity.dialog.AuthenticationMemberDialog;
import com.omneagate.activity.dialog.AuthenticationSuccessDialog;
import com.omneagate.activity.dialog.EncrypterUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
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

public class TgAuthenticateIrisScanActivity extends BaseActivity implements View.OnClickListener {


    private TextView aadhaarCardNumberMem;
    private ImageView imgIris;
    private Button btnIrisScan;
    private Button btnIrisSubmit;
    private ImageView imageViewBack;
    private UsbNotification mUsbNotification = null;
    private MediaData mMediaData = null;
    private static Iddk2000Apis mApis = null;
    private HIRICAMM mDeviceHandle = null;
    private IddkCaptureStatus mCurrentStatus = null;
    private IddkResult mCaptureResult = null;
    private IddkCaptureInfo mCaptureInfo = null;
    private DemoConfig mManiaConfig = null;

    private ImageView mCaptureView = null;
    private ImageView mCaptureViewLeft = null;
    private TextView mStatusTextView = null;

    private boolean mIspreviewing = false;
    private boolean mIsGalleryLoaded = false;
    private boolean mIsCameraReady = false;
    private boolean mIsCheckDedup = true;
    private Spinner mListOfDevices = null;
    private boolean mIsPermissionDenied = false;
    private boolean mIsJustError = false;
    private String mCurrentDeviceName = "";
    private int mScreenWidth = 0;
    private String mCurrentOutputDir = "";
    private Bitmap mCurrentBitmap = null;
    private int mTotalScore = 0;
    private int mUsableArea = 0;
    private int irisRegCurrentAction = 0;
    private static int mCaptureCount = 0;
    private static final String OUT_DIR = "/iritech/output/";
    String memberName, uid, rationCardNumber, memberId;
    private TextView txt_MemberName;
    private TextView txt_uid;
    private boolean isCaptured;
    private String encodedBioMetricInfo;
    private byte[] isodata;
    Date posReqDate, authReqDate;
    String pidXml;
    private final String TAG = TgSalesIrisScanActivity.class.getCanonicalName();
    private RationCardDetailDialog rationCardDetailDialog;
    private ImageView preImage;
    private TextView fpsIdLay;

    MediaPlayer scan_iris;
    MediaPlayer beep;
    private TextToSpeech textToSpeech;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private Button btnBack;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_authenticate_member_iris_scan);
        initView();
    }




    private void initView() {
        setPopUpPage();
        updateDateTime();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        aadhaarCardNumberMem = (TextView) findViewById(R.id.aadhaar_card_number_mem);
        btnIrisScan = (Button) findViewById(R.id.btnIrisScan);
        btnIrisSubmit = (Button) findViewById(R.id.btnIrisSubmit);
        btnIrisScan.setOnClickListener(this);
        btnIrisSubmit.setOnClickListener(this);
        preImage = (ImageView) findViewById(R.id.preImage);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        fpsIdLay = (TextView) findViewById(R.id.page_header);
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());

//        fpsIdLay.setText(getString(R.string.shop_id) + " : " + LoginData.getInstance().getShopNo() + " Welcome " + LoginData.getInstance().getFpsUsername());
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.AUTHENTICATE_MEMBER) + " > " + getString(R.string.scan_iris));
        txt_uid = (TextView) findViewById(R.id.aadhaar_card_number_mem);
        uid = getIntent().getExtras().getString("UID");
        txt_uid.setText("" + uid);

        scan_iris= MediaPlayer.create(this, R.raw.scan_iris);
        beep = MediaPlayer.create(this, R.raw.censorbeep);

        initApp();
        networkConnection = new NetworkConnection(TgAuthenticateIrisScanActivity.this);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    if(Util.needSpeakOutForFingerprint && !GlobalAppState.getInstance().language.equals("te")) {
                        textToSpeech.setLanguage(Locale.US);
                        String toSpeak = "Place Your IRIS on IRIS Scanner";
                        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }

                }
            }
        });
        if(Util.needSpeakOutForFingerprint && GlobalAppState.getInstance().language.equals("te")){
            scan_iris.start();
        }
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgAuthenticateIrisScanActivity.this, TgAuthenticateMemberActivity.class);
        startActivity(backIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnIrisScan:
                if (networkConnection.isNetworkAvailable()) {
                    if (mListOfDevices.getSelectedItem().toString() == mCurrentDeviceName) {
                        preImage.setVisibility(View.GONE);
                        startCamera(true);
                    } else {
                        //User chooses another IriShield to start a capturing process. We must release any resources of the current IriShield device
                        mApis.closeDevice(mDeviceHandle);
                        //Reset any internal states of the application
                        setInitState();
                        //Get device handle and start a capturing process
                        IddkResult ret = mApis.openDevice(mListOfDevices.getSelectedItem().toString(), mDeviceHandle);
                        if (ret.intValue() == IddkResult.IDDK_OK || ret.intValue() == IddkResult.IDDK_DEVICE_ALREADY_OPEN) {
                            updateCurrentStatus(getString(R.string.device_connected));
                            btnIrisScan.setEnabled(true);
                            mIsJustError = false;
                            mCurrentDeviceName = mListOfDevices.getSelectedItem().toString();
                            startCamera(true);
                        } else {
                            if (ret.getValue() == IddkResult.IDDK_DEVICE_ACCESS_DENIED) {
                                preImage.setVisibility(View.VISIBLE);
                                updateCurrentStatus(getString(R.string.device_access_denied));
                            } else {
                                preImage.setVisibility(View.VISIBLE);
                                updateCurrentStatus("Open device failed. Scanning device ...");
                            }
                        }
                    }
                } else {
                    displayNoInternetDailog();
                }



                break;
            case R.id.btnIrisSubmit:

                if (isCaptured) {
                    new SendIrisAuthenticationMember().execute();
                } else {
                    Toast.makeText(TgAuthenticateIrisScanActivity.this, "Please Capture Iris", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    void displayNoInternetDailog(){
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgAuthenticateIrisScanActivity.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
    }
    private class SendIrisAuthenticationMember extends AsyncTask<String, AuthenticateMember, AuthenticateMember> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgAuthenticateIrisScanActivity.this, getString(R.string.authenticatingFingerPrint));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        @Override
        protected AuthenticateMember doInBackground(String... params) {
            return sendMemberAuthentication();
        }

        @Override
        protected void onPostExecute(AuthenticateMember authenticateMemberDto) {
            super.onPostExecute(authenticateMemberDto);
            rationCardDetailDialog.dismiss();
            Log.e(TAG, "dealerAuthResponse :" + authenticateMemberDto.getRespMsgCode());
            Log.e(TAG, "dealerAuthResponse :" + authenticateMemberDto.getRespMessage());
        }
    }


    private AuthenticateMember sendMemberAuthentication() {
        AuthenticateMember authenticateMemberDto = new AuthenticateMember();
        try {
            encodedBioMetricInfo = Base64.encodeToString(isodata, Base64.DEFAULT);

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

            pidXml = buildPidXml(dateString, encodedBioMetricInfo, null);

            String fileNameWithPath =
                    Environment.getExternalStorageDirectory().toString() + "/Fps/" + "uidai_auth_prod.cer";

            Log.e(TAG, "file Name " + fileNameWithPath);
            EncrypterUtil encrypterUtil = new EncrypterUtil(fileNameWithPath);

            String expDate = encrypterUtil.getCertificateIdentifier();

            Log.e(TAG, "Certificate Expiry Date : " + expDate);

            byte[] sessionKey = encrypterUtil.generateSessionKey();
            String sKey = Base64.encodeToString(sessionKey, Base64.DEFAULT);
            byte[] encryptedSessionKey = encrypterUtil.encryptUsingPublicKey(sessionKey);
            String encryptedSkey = Base64.encodeToString(encryptedSessionKey, Base64.DEFAULT);
            String certificateIdentifier = encrypterUtil.getCertificateIdentifier();

           /* int month = gc.get(Calendar.MONTH) + 1;
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


            /*<ser:postMemberBioAuthVerify>
            <distCode>536</distCode>
            <shopNo>1674401</shopNo>
            <dealerType></dealerType>
            <uidNo>963825508408</uidNo>
            <data>Data</data>
            <skey>Skey</skey>
            <hmac>Hmac</hmac>
            <transactionId>1609211435330083</transactionId>
            <password>**************************</password>
            </ser:postMemberBioAuthVerify>*/

            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            inputMap.put("dealerType", "2");
            inputMap.put("uidNo", uid);
            inputMap.put("data", encryptedPidXmlStr);
            inputMap.put("skey", encryptedSkey);
            inputMap.put("hmac", encryptedHmacStr);

            Log.e("encryptedPidXmlStr", encryptedPidXmlStr);
            Log.e("encryptedSkey", encryptedSkey);
            Log.e(TAG, "<=====Transaction ID =======>" + LoginData.getInstance().getTransactionId());

            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");

            authenticateMemberDto = XMLUtil.authenticateMember(inputMap);

            return authenticateMemberDto;

        }catch (final FPSException e) {
            // Toast.makeText(TgLoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Exception FPSDealerDetails " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    AuthenticationMemberDialog authenticationMemberDialog = new AuthenticationMemberDialog(TgAuthenticateMemberFingerScanActivity.this, authenticateMemberDto.getRespMessage());
//                    authenticationMemberDialog.show();
                    AuthenticationMemberDialog authenticationMemberDialog = new AuthenticationMemberDialog(TgAuthenticateIrisScanActivity.this, e.getMessage());
                    authenticationMemberDialog.show();
//                    Toast.makeText(TgAuthenticateMemberFingerScanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return authenticateMemberDto;
        }

        catch (Exception e) {
            e.printStackTrace();
            return authenticateMemberDto;
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
        buff.append("<Bio type=\"IIR\" posh=\"UNKNOWN\">");
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


    private void initApp() {
        initGUI();

        //Get an instance of the IDDK library
        mApis = Iddk2000Apis.getInstance(this);

        //Application data initialization
        mDeviceHandle = new HIRICAMM();
        mCurrentStatus = new IddkCaptureStatus();
        mCaptureResult = new IddkResult();
        mManiaConfig = new DemoConfig();
        mCaptureInfo = new IddkCaptureInfo();

        //This is an opt. But we should do it as a hobby
        IddkResult ret = new IddkResult();
        IddkConfig iddkConfig = new IddkConfig();
        iddkConfig.setCommStd(IddkCommStd.IDDK_COMM_USB);
        iddkConfig.setEnableLog(false);
        ret = Iddk2000Apis.setSdkConfig(iddkConfig);
        if (ret.getValue() != IddkResult.IDDK_OK) {
            showDialog("Warning", "Cannot configure the IriTech SDK. The application may not run properly.");
        }

        //Get notification instance
        mUsbNotification = UsbNotification.getInstance(this);

        //Register detached event for the IriShield
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);
    }

    private void initGUI() {
        mCaptureView = (ImageView) findViewById(R.id.captureImage);

        //This view is used to get left eye streaming image (if Binocular device)
        mCaptureViewLeft = (ImageView) findViewById(R.id.captureImageLeft);

        mStatusTextView = (TextView) findViewById(R.id.time_textView);

        mMediaData = new MediaData(getApplicationContext());

        mListOfDevices = (Spinner) findViewById(R.id.list_of_devices_id);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
    }


    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                //Make a notice to user
              /*  mUsbNotification.cancelNofitications();
                mUsbNotification.createNotification("IriShield is disconnected.");*/

                //Play a sound when a IriShield is detached from the Android system
                mMediaData.deviceDisconnected.start();

                //Send a message to main thread
                final Message msg = Message.obtain(mHandler, 0, null);
                mHandler.dispatchMessage(msg);
            }
        }
    };


    private void setInitState() {
        mIsCameraReady = false;
        mIsPermissionDenied = false;
        mIsGalleryLoaded = false;
        mIspreviewing = false;
        mCurrentStatus.setValue(IddkCaptureStatus.IDDK_IDLE);
        mIsJustError = false;

        DemoUtility.sleep(1000);

        mStatusTextView.setText(getString(R.string.device_not_connected));
        mCaptureView.setImageBitmap(null);
        mCaptureViewLeft.setImageBitmap(null);

        btnIrisScan.setEnabled(false);

    }


    private void openDevice() {
        //Clear any internal states
        IddkResult ret = new IddkResult();
        mCaptureView.setImageBitmap(null);
        mCaptureViewLeft.setImageBitmap(null);

        mIsCameraReady = false;
        mIsGalleryLoaded = false;
        mCurrentStatus.setValue(IddkCaptureStatus.IDDK_IDLE);
        mIspreviewing = false;

        //Disable start and stop buttons

        btnIrisScan.setEnabled(false);

        //Scan and open IriShield again
        ArrayList<String> deviceDescs = new ArrayList<String>();
        ret = mApis.scanDevices(deviceDescs);
        if (ret.intValue() == IddkResult.IDDK_OK && deviceDescs.size() > 0) {
            //Show the list of IriShields attached to the Android system
            updateListOfDevices(deviceDescs);

            //We open the IriShield at index 0 as default
            ret = mApis.openDevice(deviceDescs.get(0), mDeviceHandle);
            if (ret.intValue() == IddkResult.IDDK_OK || ret.intValue() == IddkResult.IDDK_DEVICE_ALREADY_OPEN) {
                //Check device version
                //Our Android SDK not working well with IriShield device version <= 2.24
                IddkDeviceInfo deviceInfo = new IddkDeviceInfo();
                ret = mApis.getDeviceInfo(mDeviceHandle, deviceInfo);
                if (ret.getValue() == IddkResult.IDDK_OK) {
                    int majorVersion = deviceInfo.getKernelVersion();
                    int minorVersion = deviceInfo.getKernelRevision();

                    if (majorVersion <= 2 && minorVersion <= 24) {
                        showDialog("Error", "This application is not compatible with IriShield device version <= 2.24");
                        return;
                    }
                } else {
                    //Error occurs here
                    handleError(ret);
                    return;
                }

                updateCurrentStatus(getString(R.string.device_connected));

                //We can enable the start button from now

                btnIrisScan.setEnabled(true);

                //Reset error status
                mIsJustError = false;

                //Save the current device name
                mCurrentDeviceName = deviceDescs.get(0);
            } else {
                //Device not found or something wrong occurs
                if (ret.getValue() == IddkResult.IDDK_DEVICE_ACCESS_DENIED) {
                    updateCurrentStatus(getString(R.string.device_access_denied));
                } else {
                    updateCurrentStatus("Open device failed. Scanning device ...");
                }
            }
        } else {
            //There is no IriShield attached to the Android system
            updateListOfDevices(null);
            updateCurrentStatus(getString(R.string.device_not_connected));
        }
    }


    public void handleError(IddkResult error) {
        mIsCameraReady = false;
        mIsPermissionDenied = false;

        //If there is a problem with the connection
        if ((error.getValue() == IddkResult.IDDK_DEVICE_IO_FAILED) ||
                (error.getValue() == IddkResult.IDDK_DEVICE_IO_DATA_INVALID) ||
                (error.getValue() == IddkResult.IDDK_DEVICE_IO_TIMEOUT)) {

            showDialog("Error", "The program cannot run properly due to connection problem." +
                    "We suggest to do the following actions:\n\t1. Unplug and plugin the device.\n\t2. Restart the application");

            updateCurrentStatus("Device connection failed.");
            btnIrisScan.setEnabled(false);
            mCaptureView.setImageBitmap(null);
            mCaptureViewLeft.setImageBitmap(null);

            mIsJustError = true;
        } else {
            showDialog("Warning", DemoUtility.getErrorDesc(error));
        }
    }


    void showDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_menu_notifications);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing
            }
        });
        alertDialog.show();
    }


    private void updateCurrentStatus(final String newStatus) {
        mStatusTextView.post(new Runnable() {
            public void run() {
                mStatusTextView.setText(newStatus);
            }
        });
    }


    private void updateListOfDevices(ArrayList<String> listOfDevices) {
        ArrayAdapter<String> adapter = null;
        if (listOfDevices == null || listOfDevices.size() == 0) {
            //Clear all the items in the spinner and notify it to refresh
            adapter = (ArrayAdapter<String>) mListOfDevices.getAdapter();
            if (adapter != null) {
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
            return;
        }
        adapter = new ArrayAdapter<String>(this, R.xml.spinner_text_style, listOfDevices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mListOfDevices.setAdapter(adapter);
    }


    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setInitState();
            openDevice();
        }
    };


    private void startCamera(boolean sound) {
        IddkResult ret = new IddkResult();
        if (!mIsCameraReady) {
            IddkInteger imageWidth = new IddkInteger();
            IddkInteger imageHeight = new IddkInteger();
            ret = mApis.initCamera(mDeviceHandle, imageWidth, imageHeight);
            if (ret.intValue() != IddkResult.IDDK_OK) {
                updateCurrentStatus("Failed to initialize the camera.");
                handleError(ret);
                return;
            }

            mIsCameraReady = true;

            updateCurrentStatus(getString(R.string.camera_ready));
        }
        if (!mIspreviewing) {
            //We disable the start button

            btnIrisScan.setEnabled(false);

            if (sound) mMediaData.moveEyeClosePlayer.start();
            mCurrentStatus.setValue(IddkCaptureStatus.IDDK_IDLE);

            //Get the current setting values
            if (setNativeConfig() < 0) {
                updateCurrentStatus("Failed to get current setting values.");
                return;
            }

            //Start a capturing process
            CaptureTask captureTask = new CaptureTask(mCaptureView, mCaptureViewLeft);
            captureTask.execute(mApis, mCaptureResult, mCurrentStatus);

            mIspreviewing = true;
        }
    }


    private int setNativeConfig() {
        //Reset
        mTotalScore = 0;
        mUsableArea = 0;
        irisRegCurrentAction = 0;

        //Get the preferences in settings menu
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String operationMode = sharedPref.getString("operation_mode_pref", "Auto Capture");
        if (operationMode.equals("0")) {
            mCaptureInfo.setCaptureOperationMode(IddkCaptureOperationMode.IDDK_AUTO_CAPTURE);
        } else if (operationMode.equals("1")) {
            mCaptureInfo.setCaptureOperationMode(IddkCaptureOperationMode.IDDK_OPERATOR_INITIATED_AUTO_CAPTURE);
        }

        String countStr = sharedPref.getString("count_interval_pref", "3");
        int iCount = 3;
        try {
            iCount = Integer.parseInt(countStr);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            showDialog("Error !", "Please correct count number in settings menu !");
            return -1;
        }
        mCaptureInfo.setCount(iCount);

        String captureModeStr = sharedPref.getString("capture_mode_pref", "0");
        if (captureModeStr.equals("0")) {
            mCaptureInfo.setCaptureMode(IddkCaptureMode.IDDK_TIMEBASED);
        } else if (captureModeStr.equals("1")) {
            mCaptureInfo.setCaptureMode(IddkCaptureMode.IDDK_FRAMEBASED);
        }

        String qualityMode = sharedPref.getString("quality_mode_pref", "0");
        if (qualityMode.equals("0")) {
            mCaptureInfo.setQualitymode(IddkQualityMode.IDDK_QUALITY_NORMAL);
        } else if (qualityMode.equals("1")) {
            mCaptureInfo.setQualitymode(IddkQualityMode.IDDK_QUALITY_HIGH);
        } else if (qualityMode.endsWith("2")) {
            mCaptureInfo.setQualitymode(IddkQualityMode.IDDK_QUALITY_VERY_HIGH);
        }

        // Save image settings
        String prefixName = sharedPref.getString("prefix_name_pref", "Unknown");
        mCaptureInfo.setPrefixName(prefixName + "_" + mCaptureCount);
        mCaptureCount++;

        boolean isSaveBestImages = sharedPref.getBoolean("best_images_pref", true);
        mCaptureInfo.setSaveBest(isSaveBestImages);

        String outputDirStr = sharedPref.getString("output_dir_pref", Environment.getExternalStorageDirectory().getPath() + OUT_DIR);
        if (!outputDirStr.trim().endsWith("/")) {
            outputDirStr += "/";
        }
        mCurrentOutputDir = outputDirStr;

        //Set current device configuration
        IddkDeviceConfig deviceConfig = new IddkDeviceConfig();
        IddkResult iRet = mApis.getDeviceConfig(mDeviceHandle, deviceConfig);
        boolean isShowImages = (iRet.getValue() == IddkResult.IDDK_OK) ? deviceConfig.isEnableStream() : true;
        if (isShowImages == false)
            showDialog("Warning", "Streamming function is disabled in the device !");
        mCaptureInfo.setShowStream(isShowImages);
        if (!isShowImages) {
            mCaptureView.setImageBitmap(null);
            mCaptureViewLeft.setImageBitmap(null);
        }

        mIsCheckDedup = sharedPref.getBoolean("check_dedup_pref", true);

        return 0;
    }


    private class CaptureTask extends AsyncTask<Object, Bitmap, Integer> {
        ImageView captureView = null; //Right eye
        ImageView captureViewLeft = null; //Left eye
        ImageView preImage_;  //preview image

        IddkResult iRet;
        boolean isBinocularDevice = false;

        public CaptureTask(View captureView, View captureViewLeft) {
            this.captureView = (ImageView) captureView;
            this.captureViewLeft = (ImageView) captureViewLeft;
//            this.preImage_=(ImageView) preImage;

            if (captureViewLeft != null) {
                IddkInteger isBino = new IddkInteger();
                mApis.Iddk_IsBinocular(mDeviceHandle, isBino);

                this.captureViewLeft.setImageBitmap(null);
                this.captureViewLeft.setVisibility(View.INVISIBLE);
                this.captureViewLeft.getLayoutParams().height = 1;
                this.captureViewLeft.getLayoutParams().width = 1;

               /*     this.captureView.getLayoutParams().width = mScreenWidth - 10;
                    this.captureView.getLayoutParams().height = (this.captureView.getLayoutParams().width / 4) * 3;
*/
//                preImage_.setVisibility(View.INVISIBLE);
                captureView.setVisibility(View.VISIBLE);
                this.captureView.getLayoutParams().width = 500;
                this.captureView.getLayoutParams().height = 200;

            }
        }

        /*****************************************************************************
         * Capturing process is implemented here. It runs simultaneously with the main
         * thread and update streaming images to captureView. After the capturing
         * process ends, we get the best image and save it in a default directory.
         *****************************************************************************/
        protected Integer doInBackground(Object... params) {
            ArrayList<IddkImage> monoImages = new ArrayList<IddkImage>();
            IddkCaptureStatus captureStatus = new IddkCaptureStatus(IddkCaptureStatus.IDDK_IDLE);

            iRet = (IddkResult) params[1];
            Iddk2000Apis mApis = (Iddk2000Apis) params[0];

            boolean bRun = true;
            boolean eyeDetected = false;
            IddkEyeSubType subType = null;

            subType = new IddkEyeSubType(IddkEyeSubType.IDDK_UNKNOWN_EYE);

            IddkInteger maxEyeSubtypes = new IddkInteger();

            iRet = mApis.startCapture(mDeviceHandle,
                    mCaptureInfo.getCaptureMode(),
                    mCaptureInfo.getCount(),
                    mCaptureInfo.getQualitymode(),
                    mCaptureInfo.getCaptureOperationMode(),
                    subType, true, null);

            if (iRet.intValue() != IddkResult.IDDK_OK) {
                mCaptureResult = iRet;
                return -1;
            }

            while (bRun) {
                if (mCaptureInfo.isShowStream()) {
                    iRet = mApis.getStreamImage(mDeviceHandle, monoImages, maxEyeSubtypes, captureStatus);

                    if (iRet.intValue() == IddkResult.IDDK_OK) {

                        Bitmap streamImage = convertBitmap(monoImages.get(0).getImageData(), monoImages.get(0).getImageWidth(), monoImages.get(0).getImageHeight());
                        publishProgress(streamImage);

                    } else if (iRet.intValue() == IddkResult.IDDK_SE_NO_FRAME_AVAILABLE) {
                        // when GetStreamImage returns IDDK_SE_NO_FRAME_AVAILABLE,
                        // it does not always mean that capturing process has been finished or encountered problems.
                        // It may be because new stream images are not available.
                        // We need to query the current capture status to know what happens.
                        iRet = mApis.getCaptureStatus(mDeviceHandle, captureStatus);
                        mCurrentStatus.setValue(captureStatus.getValue());
                    }
                } else {
                    iRet = mApis.getCaptureStatus(mDeviceHandle, captureStatus);
                    mCurrentStatus.setValue(captureStatus.getValue());
                    DemoUtility.sleep(60);
                }

                //If GetStreamImage and GetCaptureStatus cause no error, process the capture status
                if (iRet.intValue() == IddkResult.IDDK_OK) {
                    //Eye(s) is(are) detected
                    if (captureStatus.intValue() == IddkCaptureStatus.IDDK_CAPTURING) {
                        if (!eyeDetected) {
                            updateCurrentStatus(getString(R.string.eye_detected));
                            mMediaData.eyeDetectedPlayer.start();
                            eyeDetected = true;
                            mCurrentStatus.setValue(IddkCaptureStatus.IDDK_CAPTURING);
                        }
                    } else if (captureStatus.intValue() == IddkCaptureStatus.IDDK_COMPLETE) {
                        //Capture has finished
                        updateCurrentStatus(getString(R.string.capture_finished));
                        mMediaData.captureFinishedPlayer.start();
                        bRun = false;
                        mCurrentStatus.setValue(IddkCaptureStatus.IDDK_COMPLETE);
                    } else if (captureStatus.intValue() == IddkCaptureStatus.IDDK_ABORT) {
                        //Capture has been aborted
                        bRun = false;
                        mCurrentStatus.setValue(IddkCaptureStatus.IDDK_ABORT);
                    }
                } else {
                    //Terminate the capture if errors occur
                    bRun = false;
                }
            }

            mCaptureResult = iRet;
            if (mCurrentStatus.getValue() == IddkCaptureStatus.IDDK_COMPLETE) {
                //Get the best image
                byte[] data = new byte[700416];
                byte[] data2 = new byte[700416];
                IddkDataBuffer idkbuffer = new IddkDataBuffer();


                IddkResult result = mApis.getResultIsoImage(mDeviceHandle,

                        new IddkIsoRevision(IddkIsoRevision.IDDK_IISO_2011),

                        new IddkImageFormat(IddkImageFormat.IDDK_IFORMAT_MONO_JPEG2000),

                        new IddkImageKind(IddkImageKind.IDDK_IKIND_K7_2_5), (byte) 7,

                        new IddkEyeSubType(IddkEyeSubType.IDDK_UNKNOWN_EYE),

                        idkbuffer);

                if (result.getValue() == IddkResult.IDDK_OK

                        || result.getValue() == IddkResult.IDDK_SE_LEFT_FRAME_UNQUALIFIED

                        || result.getValue() == IddkResult.IDDK_SE_RIGHT_FRAME_UNQUALIFIED) {

                    isCaptured = true;
                    isodata = idkbuffer.getData();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.btnIrisSubmit).setEnabled(true);
                            findViewById(R.id.btnIrisSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));
                            new SendIrisAuthenticationMember().execute();
                        }
                    });
                } else {
                    isCaptured = false;
                }


                ArrayList<IddkImage> monoBestImage = new ArrayList<IddkImage>();
                iRet = mApis.getResultImage(mDeviceHandle, new IddkImageKind(IddkImageKind.IDDK_IKIND_K1)
                        , new IddkImageFormat(IddkImageFormat.IDDK_IFORMAT_MONO_RAW),
                        (byte) 1, monoBestImage, maxEyeSubtypes);

                if ((!isBinocularDevice && iRet.intValue() == IddkResult.IDDK_OK) ||
                        (isBinocularDevice && (iRet.intValue() == IddkResult.IDDK_OK || iRet.intValue() == IddkResult.IDDK_SE_LEFT_FRAME_UNQUALIFIED || iRet.intValue() == IddkResult.IDDK_SE_RIGHT_FRAME_UNQUALIFIED))) {
                    //Showing the best image so that user can see it
                    Bitmap bestImage = null;
                    Bitmap bestImageRight = null;
                    Bitmap bestImageLeft = null;

                    bestImage = convertBitmap(monoBestImage.get(0).getImageData(), monoBestImage.get(0).getImageWidth(), monoBestImage.get(0).getImageHeight());
                    publishProgress(bestImage);


                    //Print the total score and usable area
                    ArrayList<IddkIrisQuality> quality = new ArrayList<IddkIrisQuality>();
                    iRet = mApis.getResultQuality(mDeviceHandle, quality, maxEyeSubtypes);

                    if (mManiaConfig.th_qmscore_show)
                     //   updateCurrentStatus("Total Score = " + quality.get(0).getTotalScore() + ", Usable Area = " + quality.get(0).getUsableArea());


                    //Save the best image
                    if (mCaptureInfo.isSaveBest()) {
                        Calendar c = Calendar.getInstance();
                        int date = c.get(Calendar.DATE);
                        int month = c.get(Calendar.MONTH) + 1;
                        int year = c.get(Calendar.YEAR);

                        StringBuilder outputDirStr = new StringBuilder();
                        outputDirStr.append(mCurrentOutputDir).append("/");
                        outputDirStr.append(year).append("-").append(month).append("-").append(date).append("/");

                        File file = new File(outputDirStr.toString());
                        if (!file.exists() && !file.mkdirs()) {
                            updateCurrentStatus("Cannot create best image directory.");
                            return -1;
                        }

                        FileOutputStream out = null;
                        FileOutputStream outLeft = null;
                        FileOutputStream outRight = null;
                        try {
                            outputDirStr.append(mCaptureInfo.getPrefixName()).append("_").append(c.get(Calendar.HOUR_OF_DAY)).append("_").append(c.get(Calendar.MINUTE)).append("_")
                                    .append(c.get(Calendar.SECOND)).append("_s").append(quality.get(0).getTotalScore()).append("_u").append(quality.get(0).getUsableArea());

                            out = new FileOutputStream(outputDirStr.toString() + ".jpg");
                            if (out != null) {
                                bestImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            }
                           // DemoUtility.SaveBin(outputDirStr.toString() + ".raw", monoBestImage.get(0).getImageData());

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            iRet.setValue(IddkResult.IDDK_UNEXPECTED_ERROR);
                        }
                    }
                }
                if (iRet.intValue() == IddkResult.IDDK_SE_NO_QUALIFIED_FRAME) {
                    //No qualified images
                    iRet.setValue(IddkResult.IDDK_SE_NO_QUALIFIED_FRAME);
                    updateCurrentStatus("No frame qualified !");
                    mMediaData.noEyeQualifiedPlayer.start();
                }
            }

            return 0;
        }

        /*****************************************************************************
         * Convert the Grayscale image from the camera to bitmap format that can be
         * used to show to the users.
         *****************************************************************************/
        private Bitmap convertBitmap(byte[] rawImage, int imageWidth, int imageHeight) {
            byte[] Bits = new byte[rawImage.length * 4]; //That's where the RGBA array goes.

            int j;
            for (j = 0; j < rawImage.length; j++) {
                Bits[j * 4] = (byte) (rawImage[j]);
                Bits[j * 4 + 1] = (byte) (rawImage[j]);
                Bits[j * 4 + 2] = (byte) (rawImage[j]);
                Bits[j * 4 + 3] = -1; //That's the alpha
            }

            //Now put these nice RGBA pixels into a Bitmap object
            mCurrentBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
            mCurrentBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));

            return mCurrentBitmap;
        }

        /*****************************************************************************
         * Update the current streaming image to the captureView
         *****************************************************************************/
        protected void onProgressUpdate(Bitmap... bm) {
            captureView.setImageBitmap(bm[0]);

        }

        /*****************************************************************************
         * Post processing after the capturing process ends
         *****************************************************************************/
        protected void onPostExecute(Integer result) {
            Log.e("onPostExecute", "onPostExecute");
            IddkResult stopResult = stopCamera(false);
            if (iRet.getValue() != IddkResult.IDDK_OK && stopResult.getValue() != iRet.getValue()) {
                handleError(iRet);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsCameraReady && !mIsPermissionDenied)
            openDevice();

        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime);
    }


    /*****************************************************************************
     * Application wants to pause and resume later.
     *****************************************************************************/
    @Override
    protected void onPause() {
        stopCamera(false);
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
    protected void onDestroy() {
        //If we are in previewing, stop it
        stopCamera(false);

        //Release the handle
        mApis.closeDevice(mDeviceHandle);

        mUsbNotification.cancelNofitications();

        if (mUsbReceiver != null)
            unregisterReceiver(mUsbReceiver);

        super.onDestroy();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }
    }

    private IddkResult stopCamera(boolean sound) {
        IddkResult iRet = new IddkResult();
        iRet.setValue(IddkResult.IDDK_OK);
        if (mIspreviewing) {
            //We enable the start button

            btnIrisScan.setEnabled(true);

            if (sound) {
                mMediaData.captureFinishedPlayer.start();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            iRet = mApis.stopCapture(mDeviceHandle);
            if (iRet.getValue() != IddkResult.IDDK_OK) {
                handleError(iRet);
                return iRet;
            }

            mIspreviewing = false;
        }

        if (mIsCameraReady) {
            iRet = mApis.deinitCamera(mDeviceHandle);
            if (iRet.getValue() != IddkResult.IDDK_OK) {
                handleError(iRet);
                return iRet;
            }

            mIsCameraReady = false;
        }
        return iRet;
    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgAuthenticateIrisScanActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}