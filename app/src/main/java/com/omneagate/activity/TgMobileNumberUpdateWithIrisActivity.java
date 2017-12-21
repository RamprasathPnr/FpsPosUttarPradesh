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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GeneralResponse;
import com.omneagate.Iris.DemoConfig;
import com.omneagate.Iris.DemoUtility;
import com.omneagate.Iris.IddkCaptureInfo;
import com.omneagate.Iris.MediaData;
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

public class TgMobileNumberUpdateWithIrisActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imageViewBack;
    private Button btScan, btSubmit;

    private UsbNotification mUsbNotification = null;
    private MediaData mMediaData = null;
    private static Iddk2000Apis mApis = null;
    private HIRICAMM mDeviceHandle = null;
    private IddkCaptureStatus mCurrentStatus = null;
    private IddkResult mCaptureResult = null;
    private IddkCaptureInfo mCaptureInfo = null;
    private DemoConfig mManiaConfig = null;

    private ImageView mCaptureMemberView = null;
    private ImageView mCaptureDealerView = null;
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
    private boolean isCapturedMember,isCapturedDealer;
    private String encodedBioMetricInfo;
    private byte[] isodata;
    private byte[] isodata1;
    Date posReqDate, authReqDate;
    String pidXml;
    private String mobileNum, aadhaarNum, OTP;
    private ImageView preImageMember, preImageDealer;
    String selectedIris = "", selectedPosition = "";
    private final String TAG = TgMobileNumberUpdateWithIrisActivity.class.getCanonicalName();
    MediaPlayer scan_iris;
    MediaPlayer beep;
    private TextToSpeech textToSpeech;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private RationCardDetailDialog rationCardDetailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_mobile_number_update_iris);

        initView();
    }



    private void initView() {
        try {

            setPopUpPage();
            ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
            updateDateTime();

            aadhaarNum = getIntent().getExtras().getString("customerUid");
            OTP = getIntent().getExtras().getString("OTP");
            mobileNum = getIntent().getExtras().getString("MobileNo");

            ((TextView) findViewById(R.id.txt_mobile_num)).setText(getResources().getText(R.string.MOBILE_NO) + " " + mobileNum);
            ((TextView) findViewById(R.id.txt_aadhaar_card_num)).setText(getResources().getText(R.string.AADHAAR_CARD) + aadhaarNum);

            preImageMember = (ImageView) findViewById(R.id.preImageMember);
            preImageDealer = (ImageView) findViewById(R.id.preImageDealer);


            imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
            imageViewBack.setOnClickListener(this);

            btSubmit = (Button) findViewById(R.id.btnIrisSubmit);
            btSubmit.setOnClickListener(this);

            Button btnScanWithFp = (Button) findViewById(R.id.btnScanWithFp);
            btnScanWithFp.setOnClickListener(this);

            btnScanWithFp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(TgMobileNumberUpdateWithIrisActivity.this, TgMobileNumUpdateDetailActivity.class);
                    i.putExtra("customerUid", aadhaarNum);
                    i.putExtra("OTP", OTP);
                    i.putExtra("MobileNo", mobileNum);
                    startActivity(i);
                    finish();
                }
            });

            btScan = (Button) findViewById(R.id.btnIrisScan);
            btScan.setOnClickListener(this);

            ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.mobile_number_update));

            ArrayList<String> finger = new ArrayList<String>();
            finger.add(getResources().getString(R.string.member_));
            finger.add(getResources().getString(R.string.DEALER));
            final Spinner spinnerIris = (Spinner) findViewById(R.id.fingerIris);
            ArrayAdapter<String> fingerAdapt = new ArrayAdapter<>(TgMobileNumberUpdateWithIrisActivity.this, android.R.layout.simple_spinner_item, finger);
            fingerAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerIris.setAdapter(fingerAdapt);
            spinnerIris.setPrompt(getString(R.string.selection));
            spinnerIris.setFocusable(true);
            spinnerIris.setFocusableInTouchMode(true);
            spinnerIris.requestFocus();
            spinnerIris.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedPosition = String.valueOf(position);
                    selectedIris = spinnerIris.getSelectedItem().toString();
                    Log.e(TAG, "spinnerFinger..." + spinnerIris);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });

            initApp();

            scan_iris= MediaPlayer.create(this, R.raw.scan_iris);
            beep = MediaPlayer.create(this, R.raw.censorbeep);

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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void openDevice() {
        IddkResult ret = new IddkResult();
        mCaptureMemberView.setImageBitmap(null);
        mCaptureDealerView.setImageBitmap(null);
        mCaptureViewLeft.setImageBitmap(null);

        mIsCameraReady = false;
        mIsGalleryLoaded = false;
        mCurrentStatus.setValue(IddkCaptureStatus.IDDK_IDLE);
        mIspreviewing = false;


        btScan.setEnabled(false);

        ArrayList<String> deviceDescs = new ArrayList<String>();
        ret = mApis.scanDevices(deviceDescs);
        if (ret.intValue() == IddkResult.IDDK_OK && deviceDescs.size() > 0) {
            updateListOfDevices(deviceDescs);

            ret = mApis.openDevice(deviceDescs.get(0), mDeviceHandle);
            if (ret.intValue() == IddkResult.IDDK_OK || ret.intValue() == IddkResult.IDDK_DEVICE_ALREADY_OPEN) {
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
                    handleError(ret);
                    return;
                }

                updateCurrentStatus(getString(R.string.device_connected));

                btScan.setEnabled(true);

                mIsJustError = false;

                mCurrentDeviceName = deviceDescs.get(0);
            } else {
                if (ret.getValue() == IddkResult.IDDK_DEVICE_ACCESS_DENIED) {
                    updateCurrentStatus(getString(R.string.device_access_denied));
                } else {
                    updateCurrentStatus("Open device failed. Scanning device ...");
                }
            }
        } else {
            updateListOfDevices(null);
            updateCurrentStatus(getString(R.string.device_not_connected));
        }
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgMobileNumberUpdateWithIrisActivity.this, TgMobileNumberUpdateActivity.class);
        startActivity(backIntent);
        finish();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.btnIrisScan:
                if (selectedIris == null || selectedIris.equalsIgnoreCase("") || selectedIris.equalsIgnoreCase("Select")) {
                    Toast.makeText(TgMobileNumberUpdateWithIrisActivity.this, R.string.Please_select_the_member_or_dealer, Toast.LENGTH_SHORT).show();
                } else {
                    if (mListOfDevices.getSelectedItem().toString() != null) {
                        if (mListOfDevices.getSelectedItem().toString() == mCurrentDeviceName) {
                            if(selectedPosition.equals("0")){
                                preImageMember.setVisibility(View.GONE);
                            }else{
                                preImageDealer.setVisibility(View.GONE);
                            }
                            startCamera(true);
                        } else {
                            mApis.closeDevice(mDeviceHandle);
                            setInitState();
                            IddkResult ret = mApis.openDevice(mListOfDevices.getSelectedItem().toString(), mDeviceHandle);
                            if (ret.intValue() == IddkResult.IDDK_OK || ret.intValue() == IddkResult.IDDK_DEVICE_ALREADY_OPEN) {
                                updateCurrentStatus(getString(R.string.device_connected));
                                btScan.setEnabled(true);
                                mIsJustError = false;
                                mCurrentDeviceName = mListOfDevices.getSelectedItem().toString();
                                startCamera(true);
                            } else {
                                if (ret.getValue() == IddkResult.IDDK_DEVICE_ACCESS_DENIED) {
                                    preImageMember.setVisibility(View.VISIBLE);
                                    preImageDealer.setVisibility(View.VISIBLE);
                                    updateCurrentStatus(getString(R.string.device_access_denied));
                                } else {
                                   // preImageDealer.setVisibility(View.VISIBLE);
                                    updateCurrentStatus("Open device failed. Scanning device ...");
                                }
                            }
                        }
                    }
                }

                break;
            case R.id.btnIrisSubmit:
                if (isCapturedDealer && isCapturedMember) {
                   new SendMobileNumberUpdate().execute();
                }

            default:
                break;

        }

    }

    private class CaptureTask extends AsyncTask<Object, Bitmap, Integer> {
        ImageView captureMemberView = null; //Right eye
        ImageView captureDealerView = null;
        ImageView captureViewLeft = null; //Left eye
        IddkResult iRet;
        boolean isBinocularDevice = false;

        public CaptureTask(View captureMemberView, View captureDealerView, View captureViewLeft) {
            this.captureMemberView = (ImageView) captureMemberView;
            this.captureDealerView = (ImageView) captureDealerView;
            this.captureViewLeft = (ImageView) captureViewLeft;

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

                if (selectedPosition.equals("0")) {
                    captureMemberView.setVisibility(View.VISIBLE);
                  /*  this.captureMemberView.getLayoutParams().width = 700;
                    this.captureMemberView.getLayoutParams().height = 300;*/

                } else {
                    captureDealerView.setVisibility(View.VISIBLE);
                    /*this.captureDealerView.getLayoutParams().width = 700;
                    this.captureDealerView.getLayoutParams().height =300;*/
                }


            }
        }

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
                        iRet = mApis.getCaptureStatus(mDeviceHandle, captureStatus);
                        mCurrentStatus.setValue(captureStatus.getValue());
                    }
                } else {
                    iRet = mApis.getCaptureStatus(mDeviceHandle, captureStatus);
                    mCurrentStatus.setValue(captureStatus.getValue());
                    DemoUtility.sleep(60);
                }

                if (iRet.intValue() == IddkResult.IDDK_OK) {
                    if (captureStatus.intValue() == IddkCaptureStatus.IDDK_CAPTURING) {
                        if (!eyeDetected) {
                            updateCurrentStatus(getString(R.string.eye_detected));
                            mMediaData.eyeDetectedPlayer.start();
                            eyeDetected = true;
                            mCurrentStatus.setValue(IddkCaptureStatus.IDDK_CAPTURING);
                        }
                    } else if (captureStatus.intValue() == IddkCaptureStatus.IDDK_COMPLETE) {
                        updateCurrentStatus(getString(R.string.capture_finished));
                        mMediaData.captureFinishedPlayer.start();
                        bRun = false;
                        mCurrentStatus.setValue(IddkCaptureStatus.IDDK_COMPLETE);
                    } else if (captureStatus.intValue() == IddkCaptureStatus.IDDK_ABORT) {
                        bRun = false;
                        mCurrentStatus.setValue(IddkCaptureStatus.IDDK_ABORT);
                    }
                } else {
                    bRun = false;
                }
            }

            mCaptureResult = iRet;
            if (mCurrentStatus.getValue() == IddkCaptureStatus.IDDK_COMPLETE) {
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


                    if(selectedPosition.equals("0")){
                        isodata = idkbuffer.getData();
                        isCapturedMember = true;
                    }else{
                        isodata1=idkbuffer.getData();
                        isCapturedDealer=true;
                    }

                  /*  runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.btnIrisSubmit).setEnabled(true);
                            findViewById(R.id.btnIrisSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));
                        }
                    });*/
                } else {
                    if(selectedPosition.equals("0")){
                        isCapturedMember = false;
                    }else{
                        isCapturedDealer=false;
                    }
                }


                ArrayList<IddkImage> monoBestImage = new ArrayList<IddkImage>();
                iRet = mApis.getResultImage(mDeviceHandle, new IddkImageKind(IddkImageKind.IDDK_IKIND_K1)
                        , new IddkImageFormat(IddkImageFormat.IDDK_IFORMAT_MONO_RAW),
                        (byte) 1, monoBestImage, maxEyeSubtypes);

                if ((!isBinocularDevice && iRet.intValue() == IddkResult.IDDK_OK) ||
                        (isBinocularDevice && (iRet.intValue() == IddkResult.IDDK_OK || iRet.intValue() == IddkResult.IDDK_SE_LEFT_FRAME_UNQUALIFIED || iRet.intValue() == IddkResult.IDDK_SE_RIGHT_FRAME_UNQUALIFIED))) {
                    Bitmap bestImage = null;
                    Bitmap bestImageRight = null;
                    Bitmap bestImageLeft = null;

                    bestImage = convertBitmap(monoBestImage.get(0).getImageData(), monoBestImage.get(0).getImageWidth(), monoBestImage.get(0).getImageHeight());
                    publishProgress(bestImage);


                    ArrayList<IddkIrisQuality> quality = new ArrayList<IddkIrisQuality>();
                    iRet = mApis.getResultQuality(mDeviceHandle, quality, maxEyeSubtypes);

                    if (mManiaConfig.th_qmscore_show)
                     //   updateCurrentStatus("Total Score = " + quality.get(0).getTotalScore() + ", Usable Area = " + quality.get(0).getUsableArea());


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
                         //   DemoUtility.SaveBin(outputDirStr.toString() + ".raw", monoBestImage.get(0).getImageData());

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

        private Bitmap convertBitmap(byte[] rawImage, int imageWidth, int imageHeight) {
            byte[] Bits = new byte[rawImage.length * 4]; //That's where the RGBA array goes.

            int j;
            for (j = 0; j < rawImage.length; j++) {
                Bits[j * 4] = (byte) (rawImage[j]);
                Bits[j * 4 + 1] = (byte) (rawImage[j]);
                Bits[j * 4 + 2] = (byte) (rawImage[j]);
                Bits[j * 4 + 3] = -1; //That's the alpha
            }

            mCurrentBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
            mCurrentBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));

            return mCurrentBitmap;
        }

        protected void onProgressUpdate(Bitmap... bm) {

            if (selectedPosition.equals("0")) {
                captureMemberView.setImageBitmap(bm[0]);
                if(isCapturedDealer && isCapturedMember){
                    findViewById(R.id.btnIrisSubmit).setEnabled(true);
                    findViewById(R.id.btnIrisSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));
                }
            } else {
                captureDealerView.setImageBitmap(bm[0]);
                if(isCapturedDealer && isCapturedMember){
                    findViewById(R.id.btnIrisSubmit).setEnabled(true);
                    findViewById(R.id.btnIrisSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));

                }
            }

        }

        protected void onPostExecute(Integer result) {
            Log.e("onPostExecute", "onPostExecute");
            IddkResult stopResult = stopCamera(false);
            if (iRet.getValue() != IddkResult.IDDK_OK && stopResult.getValue() != iRet.getValue()) {
                handleError(iRet);
            }

        }
    }

    private void initApp() {
        initGUI();

        mApis = Iddk2000Apis.getInstance(this);
        mDeviceHandle = new HIRICAMM();
        mCurrentStatus = new IddkCaptureStatus();
        mCaptureResult = new IddkResult();
        mManiaConfig = new DemoConfig();
        mCaptureInfo = new IddkCaptureInfo();

        IddkResult ret = new IddkResult();
        IddkConfig iddkConfig = new IddkConfig();
        iddkConfig.setCommStd(IddkCommStd.IDDK_COMM_USB);
        iddkConfig.setEnableLog(false);
        ret = Iddk2000Apis.setSdkConfig(iddkConfig);
        if (ret.getValue() != IddkResult.IDDK_OK) {
            showDialog("Warning", "Cannot configure the IriTech SDK. The application may not run properly.");
        }

        mUsbNotification = UsbNotification.getInstance(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, filter);

    }

    private void updateListOfDevices(ArrayList<String> listOfDevices) {
        ArrayAdapter<String> adapter = null;
        if (listOfDevices == null || listOfDevices.size() == 0) {
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

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                mMediaData.deviceDisconnected.start();

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

        mStatusTextView.setText("Device not found. Scanning device ...");
        mCaptureMemberView.setImageBitmap(null);
        mCaptureDealerView.setImageBitmap(null);
        mCaptureViewLeft.setImageBitmap(null);

        btScan.setEnabled(false);

    }

    private int setNativeConfig() {
        mTotalScore = 0;
        mUsableArea = 0;
        irisRegCurrentAction = 0;

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

        IddkDeviceConfig deviceConfig = new IddkDeviceConfig();
        IddkResult iRet = mApis.getDeviceConfig(mDeviceHandle, deviceConfig);
        boolean isShowImages = (iRet.getValue() == IddkResult.IDDK_OK) ? deviceConfig.isEnableStream() : true;
        if (isShowImages == false)
            showDialog("Warning", "Streamming function is disabled in the device !");
        mCaptureInfo.setShowStream(isShowImages);
        if (!isShowImages) {
            mCaptureMemberView.setImageBitmap(null);
            mCaptureDealerView.setImageBitmap(null);
            mCaptureViewLeft.setImageBitmap(null);
        }

        mIsCheckDedup = sharedPref.getBoolean("check_dedup_pref", true);

        return 0;
    }

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

            btScan.setEnabled(false);

            if (sound) mMediaData.moveEyeClosePlayer.start();
            mCurrentStatus.setValue(IddkCaptureStatus.IDDK_IDLE);

            if (setNativeConfig() < 0) {
                updateCurrentStatus("Failed to get current setting values.");
                return;
            }

            CaptureTask captureTask = new CaptureTask(mCaptureMemberView, mCaptureDealerView, mCaptureViewLeft);
            captureTask.execute(mApis, mCaptureResult, mCurrentStatus);

            mIspreviewing = true;
        }
    }

    private void initGUI() {
        mCaptureMemberView = (ImageView) findViewById(R.id.img_iris_member);

        mCaptureDealerView = (ImageView) findViewById(R.id.img_iris_dealer);

        mCaptureViewLeft = (ImageView) findViewById(R.id.capture_view_left);

        mStatusTextView = (TextView) findViewById(R.id.device_status);

        mMediaData = new MediaData(getApplicationContext());

        mListOfDevices = (Spinner) findViewById(R.id.list_of_devices_id);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
    }

    public void handleError(IddkResult error) {
        mIsCameraReady = false;
        mIsPermissionDenied = false;

        if ((error.getValue() == IddkResult.IDDK_DEVICE_IO_FAILED) ||
                (error.getValue() == IddkResult.IDDK_DEVICE_IO_DATA_INVALID) ||
                (error.getValue() == IddkResult.IDDK_DEVICE_IO_TIMEOUT)) {

            showDialog("Error", "The program cannot run properly due to connection problem." +
                    "We suggest to do the following actions:\n\t1. Unplug and plugin the device.\n\t2. Restart the application");

            updateCurrentStatus("Device connection failed.");
            btScan.setEnabled(false);
            mCaptureMemberView.setImageBitmap(null);
            mCaptureDealerView.setImageBitmap(null);

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

    private IddkResult stopCamera(boolean sound) {
        IddkResult iRet = new IddkResult();
        iRet.setValue(IddkResult.IDDK_OK);
        if (mIspreviewing) {

            btScan.setEnabled(true);

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

    @Override
    protected void onResume() {
        super.onResume();

        if (!mIsCameraReady && !mIsPermissionDenied)
            openDevice();
        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }

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

    private void updateCurrentStatus(final String newStatus) {
        mStatusTextView.post(new Runnable() {
            public void run() {
                mStatusTextView.setText(newStatus);
            }
        });
    }

    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            setInitState();
            openDevice();
        }
    };
    private class SendMobileNumberUpdate extends AsyncTask<String, GeneralResponse, GeneralResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgMobileNumberUpdateWithIrisActivity.this, getString(R.string.authenticatingFingerPrint));
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
                        AuthenticationSuccessDialog authenticationSuccessDialog = new AuthenticationSuccessDialog(TgMobileNumberUpdateWithIrisActivity.this, getString(R.string.authentication_success), "TgScanFingerPrintActivity");
                        authenticationSuccessDialog.setCanceledOnTouchOutside(false);
                        authenticationSuccessDialog.show();

                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception " + e.toString());

            }

        }
    }

    private GeneralResponse sendMobileNumber() {
        try {


            final String encodedBioMetricInfo_01 = Base64.encodeToString(isodata, Base64.DEFAULT);
            final String encodedBioMetricInfo_02 = Base64.encodeToString(isodata1, Base64.DEFAULT);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            authReqDate = sdf.parse(dateString);

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
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgMobileNumberUpdateWithIrisActivity.this,""+e.getMessage());
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

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgMobileNumberUpdateWithIrisActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
