package com.omneagate.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSLogout;
import com.omneagate.DTO.LogoutDto;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.GPSService;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.GeoFencingDialog;
import com.omneagate.activity.dialog.InspectionLogoutDialog;
import com.omneagate.activity.dialog.LogoutDialog;
import com.omneagate.activity.dialog.TgBufferDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.activity.dialog.UnackAdjustmentDialog;
import com.omneagate.activity.dialog.UnackInwardDialog;
import com.omneagate.exception.FPSException;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


/**
 * BaseActivity is the base class for all activities
 */
public abstract class BaseActivity extends Activity {

    //Network connectivity
    NetworkConnection networkConnection;

    //HttpConnection service
    public HttpClientWrapper httpConnection;

    //Global application context for this application
    com.omneagate.activity.GlobalAppState appState;

    //Progressbar for waiting
    CustomProgressDialog progressBar;

    GeoFencingDialog geoFencingDialog;

    LogoutDialog logout;

//    InspectionLogoutDialog inspectionLogoutDialog;

    PopupWindow popupMessage;          //User menu popup

    View layoutOfPopup;                //Popup window view

    ImageView imageViewUserProfile;    //User profile imageview
    TgBufferDialog loginLoadingDailog;
    /*Handler used to get response from server*/
    protected final Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ServiceListenerType type = (ServiceListenerType) msg.obj;
            switch (type) {
                case LOGIN_USER:
                    processMessage(msg.getData(), ServiceListenerType.LOGIN_USER);
                    break;
                case CLOSE_SALE:
                    processMessage(msg.getData(), ServiceListenerType.CLOSE_SALE);
                    break;
                case LOGOUT_USER:
                    processMessage(msg.getData(), ServiceListenerType.LOGOUT_USER);
                    break;
                case FPS_INTENT_REQUEST:
                    processMessage(msg.getData(), ServiceListenerType.FPS_INTENT_REQUEST);
                    break;
                case CARD_OTP_REGENERATE:
                    processMessage(msg.getData(), ServiceListenerType.CARD_OTP_REGENERATE);
                    break;
                case QR_CODE:
                    processMessage(msg.getData(), ServiceListenerType.QR_CODE);
                    break;
                case CARD_ACTIVATION:
                    processMessage(msg.getData(), ServiceListenerType.CARD_ACTIVATION);
                    break;
                case CHECKVERSION:
                    processMessage(msg.getData(), ServiceListenerType.CHECKVERSION);
                    break;
                case CARD_REGISTRATION:
                    processMessage(msg.getData(), ServiceListenerType.CARD_REGISTRATION);
                    break;
                case DEVICE_STATUS:
                    processMessage(msg.getData(), ServiceListenerType.DEVICE_STATUS);
                    break;
                case UPGRADE_RESPONSE:
                    processMessage(msg.getData(), ServiceListenerType.UPGRADE_RESPONSE);
                    break;
                case BENEFICIARY_UPDATION:
                    processMessage(msg.getData(), ServiceListenerType.BENEFICIARY_UPDATION);
                    break;
                case SEND_BILL:
                    processMessage(msg.getData(), ServiceListenerType.SEND_BILL);
                    break;
                case DEVICE_REGISTER:
                    processMessage(msg.getData(), ServiceListenerType.DEVICE_REGISTER);
                    break;
                case CARD_OTP:
                    processMessage(msg.getData(), ServiceListenerType.CARD_OTP);
                    break;
                case OPEN_STOCK:
                    processMessage(msg.getData(), ServiceListenerType.OPEN_STOCK);
                    break;
                case CONFIGURATION:
                    processMessage(msg.getData(), ServiceListenerType.CONFIGURATION);
                    break;
                case PROXY_REGISTRATION:
                    processMessage(msg.getData(), ServiceListenerType.PROXY_REGISTRATION);
                    break;
                case BFD_REGISTRATION:
                    processMessage(msg.getData(), ServiceListenerType.BFD_REGISTRATION);
                    break;
                case UNSCHEDULED_REPORT:
                    processMessage(msg.getData(), ServiceListenerType.UNSCHEDULED_REPORT);
                    break;
                case RESEND_OTP:
                    processMessage(msg.getData(), ServiceListenerType.RESEND_OTP);
                    break;
                case RECONCILIATION:
                    processMessage(msg.getData(), ServiceListenerType.RECONCILIATION);
                    break;
                case RECONCILIATION_STATUS:
                    processMessage(msg.getData(), ServiceListenerType.RECONCILIATION_STATUS);
                    break;
                default:
                    com.omneagate.activity.GlobalAppState.localLogin = true;
                    processMessage(msg.getData(), ServiceListenerType.ERROR_MSG);
                    break;
            }
        }

    };
    public static BaseActivity globalContext;


    /*
     * abstract method for all activity
     * */
    protected abstract void processMessage(Bundle message, ServiceListenerType what);

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi")) {
           /* Typeface tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini, Typeface.BOLD);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, getString(id)));*/
            textName.setText(getString(id));
        } else {
            textName.setText(getString(id));
        }
    }

    public void setOapAnpText(TextView textName, int id) {
        if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi")) {
            Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/akshar.ttf");
            textName.setTypeface(tf);
            textName.setText(getString(id));


        } else {
            textName.setText(getString(id));
        }
    }

    public void updateDateTime(){
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

    }
    private void updateTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        Date currentData;
        if(Util.needInternalClock && GlobalAppState.serverDate !=null){
            currentData=GlobalAppState.serverDate;
        }else{
            currentData = new Date();
        }
        //GregorianCalendar gc = new GregorianCalendar();
        String dateString = sdf.format(currentData);
        final PackageManager packageManager = BaseActivity.this.getPackageManager();

        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(BaseActivity.this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = packageInfo.versionName;

        if(GlobalAppState.serverDate !=null){
            ((TextView)findViewById(R.id.login_bottom1)).setText("ePOS Version " + versionName + "   " + dateString);
        }else{
            ((TextView)findViewById(R.id.login_bottom1)).setText("ePOS Version " + versionName + "   ");
        }

    }
    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilHeader(TextView textName, int id) {
       /* Typeface tfBamini = Typeface.createFromAsset(getAssets(), "Impact.ttf");
        textName.setTypeface(tfBamini);*/
        textName.setText(getString(id));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String languageCode = FPSDBHelper.getInstance(this).getMasterData("language");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (languageCode == null) {
            languageCode = "hi";
        }
        Util.changeLanguage(this, languageCode);
        com.omneagate.activity.GlobalAppState.language = languageCode;
        globalContext = this;
    }


    public boolean checkLocationDetails() {

        String geoFencingStr = FPSDBHelper.getInstance(this).getMasterData("geoFencing");
        Util.LoggingQueue(BaseActivity.this, "BaseActivity ", " checkLocationDetails()  geoFencingStr  ->" +geoFencingStr);
        Float maxGeoFencingRange  = 2f;
        if(geoFencingStr != null && StringUtils.isNotEmpty(geoFencingStr)){
            maxGeoFencingRange = Float.parseFloat(geoFencingStr);
            Util.LoggingQueue(BaseActivity.this, "BaseActivity ", " checkLocationDetails()  maxGeoFencingRange  ->" +maxGeoFencingRange);
        }
        boolean isInsideLimit = true;
        GPSService mGPSService = new GPSService(this);
        Location locationB = mGPSService.getLocation();
       // Location locationB = null;
        SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
        boolean fencing = prefs.getBoolean("fencing", false);

        Util.LoggingQueue(BaseActivity.this, "BaseActivity ", "  checkLocationDetails()  getSharedPreferences is fencing enabled ->" + fencing);

        if (fencing) {
            String longitude = prefs.getString("longitude", "");
            String latitude = prefs.getString("latitude", "");

            Util.LoggingQueue(BaseActivity.this, "BaseActivity ", "  checkLocationDetails()  getSharedPreferences  longitude  ->" +longitude);
            Util.LoggingQueue(BaseActivity.this, "BaseActivity ", "  checkLocationDetails()   getSharedPreferences latitude  ->" +latitude);


            if (StringUtils.isNotEmpty(latitude) && StringUtils.isNotEmpty(longitude)
                    && latitude != null && longitude != null ) {
                Location locationA = new Location("point A");
                locationA.setLatitude(Float.parseFloat(latitude));
                locationA.setLongitude(Float.parseFloat(longitude));
                if (locationB != null) {
                    float distance = locationA.distanceTo(locationB);
                    /** 15-07-2016
                     *  Changed Gps Distance range to Global Config vale
                     */
                try
                {
                    Util.LoggingQueue(BaseActivity.this, "BaseActivity ", " checkLocationDetails()  distance  ->" +distance);
                    Util.LoggingQueue(BaseActivity.this, "BaseActivity ", " checkLocationDetails()  maxGeoFencingRange  ->" +maxGeoFencingRange);
                    //if (distance > 200f) {
                    if (distance > maxGeoFencingRange) {
                        geoFencingDialog = new GeoFencingDialog(this);
                        geoFencingDialog.show();
                        isInsideLimit = false;
                    }
                    Log.e("distance", distance + ":::::" + distance);
                }catch (Exception e){
                    Util.LoggingQueue(BaseActivity.this, "BaseActivity ", " checkLocationDetails()   Exception ->" +e.toString());
                }
                }
            }
        }
        return isInsideLimit;
    }

    /**
     * Tamil text textView typeface
     * input  textView name and text string input
     */
    public void setTamilText(TextView textName, String text) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            /*Typeface tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, text));*/
            textName.setText(text);
        } else {
            textName.setText(text);
        }
    }

    public void setTamilTextButton(Button textName, String text) {
      //  Log.e("BaseActivity", "Util.setTamilTextButton passing text");

        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            /*Typeface tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, text));*/
            textName.setText(text);
        } else {
            textName.setText(text);
        }
    }
    public String unicodeToLocalLanguage(String keyString) {
        String unicodeString = null;
        try {
            unicodeString = new String(keyString.getBytes(), "UTF8");
        } catch (Exception e) {
            Log.e("Exception while UTF", keyString);
        }
        return unicodeString;
    }

    public void setPopUpPage(){
        try{
            layoutOfPopup = LayoutInflater.from(this).inflate(R.layout.tg_popup, new LinearLayout(this),false);
            popupMessage = new PopupWindow(layoutOfPopup, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            popupMessage.setContentView(layoutOfPopup);
            popupMessage.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            imageViewUserProfile = (ImageView) findViewById(R.id.imageViewUserProfile);
            imageViewUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.showAsDropDown(imageViewUserProfile, 0, 0);

                }
            });
            popupMessage.setOutsideTouchable(true);
            ((TextView) layoutOfPopup.findViewById(R.id.device_id)).setText(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            ((TextView) layoutOfPopup.findViewById(R.id.fps_id)).setText(LoginData.getInstance().getShopNo());

            layoutOfPopup.findViewById(R.id.homeLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent i=new Intent(BaseActivity.this,TgLoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            });

            layoutOfPopup.findViewById(R.id.logoutLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setMessage(R.string.log)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new GetLogout().execute();

                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();


                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setUpPopUpPage() {
        try {
            layoutOfPopup = LayoutInflater.from(this).inflate(R.layout.popup_user_image, new LinearLayout(this),false);
            popupMessage = new PopupWindow(layoutOfPopup, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            popupMessage.setContentView(layoutOfPopup);
           // Util.LoggingQueue(this, "Popup BaseActivty", "setUpPopUpPage");
            if (StringUtils.isNotEmpty(SessionId.getInstance().getUserName()))
                ((TextView) layoutOfPopup.findViewById(R.id.popup_userName)).setText(SessionId.getInstance().getUserName().toUpperCase());
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.welcome_view)), R.string.welcome_view);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.last_login)), R.string.last_login);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.logout)), R.string.logout);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.fps_profile_view)), R.string.fps_profile);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.store_details)), R.string.fps_code);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
            if (SessionId.getInstance().getLastLoginTime() != null)
                ((TextView) layoutOfPopup.findViewById(R.id.popup_last_login)).setText(formatter.format(SessionId.getInstance().getLastLoginTime()).toUpperCase());
            popupMessage.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (SessionId.getInstance().getFpsCode() != null)
                ((TextView) layoutOfPopup.findViewById(R.id.popup_store_details)).setText(SessionId.getInstance().getFpsCode().toUpperCase());
            if (getLocalClassName().toString().contains("ProfileActivity")) {
                layoutOfPopup.findViewById(R.id.fps_profile).setVisibility(View.GONE);
            }
            String POS_RECONCILIATION_ENABLE_STATUS = FPSDBHelper.getInstance(this).getMasterData("POS_RECONCILIATION_ENABLE_STATUS");
            if((POS_RECONCILIATION_ENABLE_STATUS != null) && (StringUtils.isNotEmpty(POS_RECONCILIATION_ENABLE_STATUS.trim())) && (!POS_RECONCILIATION_ENABLE_STATUS.equalsIgnoreCase("null"))) {
                if(POS_RECONCILIATION_ENABLE_STATUS.equalsIgnoreCase("true")) {
                    layoutOfPopup.findViewById(R.id.reconciliationLayout).setVisibility(View.VISIBLE);
                }
                else if(POS_RECONCILIATION_ENABLE_STATUS.equalsIgnoreCase("false")) {
                    layoutOfPopup.findViewById(R.id.reconciliationLayout).setVisibility(View.GONE);
                }
            }
            else {
                layoutOfPopup.findViewById(R.id.reconciliationLayout).setVisibility(View.VISIBLE);
            }
            if (getLocalClassName().toString().contains("ReconciliationActivity") || getLocalClassName().toString().contains("ReconciliationHistoryActivity") || getLocalClassName().toString().contains("ReconciliationManualsyncActivity")) {
                layoutOfPopup.findViewById(R.id.reconciliationLayout).setVisibility(View.GONE);
            }
            popupMessage.setOutsideTouchable(true);

            imageViewUserProfile = (ImageView) findViewById(R.id.imageViewUserProfile);
            imageViewUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.showAsDropDown(imageViewUserProfile, 0, 0);

                }
            });
            layoutOfPopup.findViewById(R.id.fps_profile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.dismiss();
                    profilePage();
                }
            });
            layoutOfPopup.findViewById(R.id.logout_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.dismiss();
                    userLogoutResponse();
                }
            });
            layoutOfPopup.findViewById(R.id.reconciliationLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.dismiss();
                    int unackAdjustment = FPSDBHelper.getInstance(BaseActivity.this).getUnacknowledgedAdjustment();
                    int unackInward = FPSDBHelper.getInstance(BaseActivity.this).getUnacknowledgedInward();
                    if(unackAdjustment > 0) {
                        UnackAdjustmentDialog unackAdjustmentDialog = new UnackAdjustmentDialog(BaseActivity.this, unackAdjustment);
                        unackAdjustmentDialog.show();
                    }
                    else if(unackInward > 0) {
                        UnackInwardDialog unackInwardDialog = new UnackInwardDialog(BaseActivity.this, unackInward);
                        unackInwardDialog.show();
                    }
                    else {
                        reconciliationPage();
                    }
                }
            });
            View viewOnline = findViewById(R.id.onLineOffline);
            TextView textViewOnline = (TextView) findViewById(R.id.textOnline);
            networkConnection = new NetworkConnection(this);
            if (SessionId.getInstance().getSessionId()!= null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId()) && networkConnection.isNetworkAvailable()) {
                viewOnline.setBackgroundResource(R.drawable.rounded_circle_green);
                textViewOnline.setTextColor(Color.parseColor("#038203"));
                Util.setTamilText(textViewOnline, R.string.onlineText);
            } else {
                viewOnline.setBackgroundResource(R.drawable.rounded_circle_red);
                textViewOnline.setTextColor(Color.parseColor("#FFFF0000"));
                Util.setTamilText(textViewOnline, R.string.offlineText);
            }
        } catch (Exception e) {
            Log.e("BaseActivity", e.toString(), e);
        }
    }

    public void setUpInspectionPopUpPage() {
        try {
            layoutOfPopup = LayoutInflater.from(this).inflate(R.layout.popup_inspection, new LinearLayout(this),false);
            popupMessage = new PopupWindow(layoutOfPopup, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            popupMessage.setContentView(layoutOfPopup);
            // Util.LoggingQueue(this, "Popup BaseActivty", "setUpPopUpPage");
            if (StringUtils.isNotEmpty(SessionId.getInstance().getUserName()))
                ((TextView) layoutOfPopup.findViewById(R.id.popup_userName)).setText(SessionId.getInstance().getUserName().toUpperCase());
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.welcome_view)), R.string.welcome_view);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.last_login)), R.string.last_login);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.logout)), R.string.logout);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.fps_profile_view)), R.string.fps_profile);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.store_details)), R.string.fps_code);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
            if (SessionId.getInstance().getLastLoginTime() != null)
                ((TextView) layoutOfPopup.findViewById(R.id.popup_last_login)).setText(formatter.format(SessionId.getInstance().getLastLoginTime()).toUpperCase());
            popupMessage.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (SessionId.getInstance().getFpsCode() != null)
                ((TextView) layoutOfPopup.findViewById(R.id.popup_store_details)).setText(SessionId.getInstance().getFpsCode().toUpperCase());
            if (getLocalClassName().toString().contains("ProfileActivity")) {
                layoutOfPopup.findViewById(R.id.fps_profile).setVisibility(View.GONE);
            }
            popupMessage.setOutsideTouchable(true);

            imageViewUserProfile = (ImageView) findViewById(R.id.imageViewUserProfile);
            imageViewUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.showAsDropDown(imageViewUserProfile, 0, 0);

                }
            });
            layoutOfPopup.findViewById(R.id.fps_profile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.dismiss();
                    profilePage();
                }
            });
            layoutOfPopup.findViewById(R.id.logout_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.dismiss();
                    inspectionLogoutResponse();
//                    userLogoutResponse();
                }
            });
            View viewOnline = findViewById(R.id.onLineOffline);
            TextView textViewOnline = (TextView) findViewById(R.id.textOnline);
            networkConnection = new NetworkConnection(this);
            if (SessionId.getInstance().getSessionId()!= null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId()) && networkConnection.isNetworkAvailable()) {
                viewOnline.setBackgroundResource(R.drawable.rounded_circle_green);
                textViewOnline.setTextColor(Color.parseColor("#038203"));
                Util.setTamilText(textViewOnline, R.string.onlineText);
            } else {
                viewOnline.setBackgroundResource(R.drawable.rounded_circle_red);
                textViewOnline.setTextColor(Color.parseColor("#FFFF0000"));
                Util.setTamilText(textViewOnline, R.string.offlineText);
            }
        } catch (Exception e) {
            Log.e("BaseActivity", e.toString(), e);
        }
    }

    public void setUpPopUpPageForAdmin() {
        try {
            layoutOfPopup = LayoutInflater.from(this).inflate(R.layout.popup_admin_image, new LinearLayout(this),false);
            popupMessage = new PopupWindow(layoutOfPopup, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            popupMessage.setContentView(layoutOfPopup);
            Util.LoggingQueue(this, "Popup", "Pop up called");
            if (StringUtils.isNotEmpty(SessionId.getInstance().getUserName()))
                ((TextView) layoutOfPopup.findViewById(R.id.popup_userName)).setText(SessionId.getInstance().getUserName().toUpperCase());
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.welcome_view)), R.string.welcome_view);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.last_login)), R.string.last_login);
            Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.logout)), R.string.logout);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
            if (SessionId.getInstance().getLastLoginTime() != null)
                ((TextView) layoutOfPopup.findViewById(R.id.popup_last_login)).setText(formatter.format(SessionId.getInstance().getLastLoginTime()).toUpperCase());
            popupMessage.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupMessage.setOutsideTouchable(true);

            try {
                Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.store_details)), R.string.fps_code);
                String fpsCode = FPSDBHelper.getInstance(this).getFpsCode("FPS");
                if (fpsCode.equalsIgnoreCase("")) {
                    Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.popup_store_details)), R.string.fpsCodeStatus);
                } else {
                    ((TextView) layoutOfPopup.findViewById(R.id.popup_store_details)).setText(fpsCode);
                }
            }
            catch(Exception e) {
                Log.e("Base activity","fps code selectQuery exception..."+e);
                Util.setTamilText(((TextView) layoutOfPopup.findViewById(R.id.popup_store_details)), R.string.fpsCodeStatus);
            }

            imageViewUserProfile = (ImageView) findViewById(R.id.imageViewUserProfile);
            imageViewUserProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.showAsDropDown(imageViewUserProfile, 0, 0);

                }
            });
            layoutOfPopup.findViewById(R.id.logout_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMessage.dismiss();
                    userLogoutResponse();
                }
            });
            View viewOnline = findViewById(R.id.onLineOffline);
            TextView textViewOnline = (TextView) findViewById(R.id.textOnline);
            networkConnection = new NetworkConnection(this);
            if (SessionId.getInstance().getSessionId() != null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId()) && networkConnection.isNetworkAvailable()) {
                viewOnline.setBackgroundResource(R.drawable.rounded_circle_green);
                textViewOnline.setTextColor(Color.parseColor("#038203"));
                Util.setTamilText(textViewOnline, R.string.onlineText);
            } else {
                viewOnline.setBackgroundResource(R.drawable.rounded_circle_red);
                textViewOnline.setTextColor(Color.parseColor("#FFFF0000"));
                Util.setTamilText(textViewOnline, R.string.offlineText);
            }
        } catch (Exception e) {
            Log.e("BaseActivity", e.toString(), e);
        }
    }

    private void profilePage() {
        startActivity(new Intent(this, ProfileActivity.class));
        Util.LoggingQueue(this, "Pro activity", "calling for profile activity");
//        finish();
    }

    private void reconciliationPage() {
        startActivity(new Intent(this, ReconciliationManualsyncActivity.class));
    }

    //After user give logout this method will call dialog
    private void userLogoutResponse() {
        logout = new LogoutDialog(this);
        Util.LoggingQueue(this, "Logout", "Logout dialog appearence");
        logout.show();
    }

    private void inspectionLogoutResponse() {
        InspectionLogoutDialog inspectionLogout = new InspectionLogoutDialog(this);
        Util.LoggingQueue(this, "Logout", "Inspection Logout dialog appearence");
        inspectionLogout.show();
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamil(TextView textName, int id) {
        /*Typeface tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
        textName.setTypeface(tfBamini);
        textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, getString(id)));*/
        textName.setText(getString(id));
    }

   //Logout request from user success and send to server
    public void logOutSuccess() {

        Util.LoggingQueue(this, "Logout", "Calling for logout");
        networkConnection = new NetworkConnection(this);
        String logoutString = "OFFLINE_LOGOUT";
        if (networkConnection.isNetworkAvailable()) {
            try {
//                String url = "/login/logmeout";
                String url = "/login/logout";
                Util.LoggingQueue(this, "Logout", "Calling for logout url.."+url);
                logoutString = "ONLINE_LOGOUT";
                httpConnection = new HttpClientWrapper();
                LogoutDto logoutDto = new LogoutDto();
                logoutDto.setSessionId(SessionId.getInstance().getSessionId());
                logoutDto.setLogoutStatus(logoutString);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String dateStr = df.format(new Date());

//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                Date date = df.parse(dateStr);
                logoutDto.setLogoutTime(dateStr);
                String logout = new Gson().toJson(logoutDto);
                Log.e("base activity","logout..."+logout);
                StringEntity se = new StringEntity(logout, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGOUT_USER,
                        SyncHandler, RequestType.POST, se, this);
            }
            catch(Exception e) {
                Util.LoggingQueue(this, "Logout", "Calling for logout exc.."+e);
            }
        }
        FPSDBHelper.getInstance(this).updateLoginHistory(SessionId.getInstance().getTransactionId(), logoutString);
        SessionId.getInstance().setSessionId("");
        FPSDBHelper.getInstance(this).closeConnection();
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }

    public void inspectionLogOutSuccess() {
        Util.LoggingQueue(this, "Logout", "Calling for inspectionLogOutSuccess");
        networkConnection = new NetworkConnection(this);
        String logoutString = "OFFLINE_LOGOUT";
        if (networkConnection.isNetworkAvailable()) {
            try {
//                String url = "/login/logmeout";
                String url = "/login/logout";
                Util.LoggingQueue(this, "Logout", "Calling for logout url.."+url);
                logoutString = "ONLINE_LOGOUT";
                httpConnection = new HttpClientWrapper();
                LogoutDto logoutDto = new LogoutDto();
                logoutDto.setSessionId(SessionId.getInstance().getSessionId());
                logoutDto.setLogoutStatus(logoutString);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String dateStr = df.format(new Date());

//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                Date date = df.parse(dateStr);
                logoutDto.setLogoutTime(dateStr);
                String logout = new Gson().toJson(logoutDto);
                Log.e("base activity","logout..."+logout);
                StringEntity se = new StringEntity(logout, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGOUT_USER,
                        SyncHandler, RequestType.POST, se, this);
            }
            catch(Exception e) {
                Util.LoggingQueue(this, "Logout", "Calling for logout exc.."+e);
            }
        }
        FPSDBHelper.getInstance(this).updateLoginHistory(SessionId.getInstance().getTransactionId(), logoutString);
        SessionId.getInstance().setSessionId("");
        FPSDBHelper.getInstance(this).closeConnection();
        startActivity(new Intent(this, InspectionLoginActivity.class));
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("BaseActivity", "ondestroy called");
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }

        try {
            if (popupMessage != null) {
                popupMessage.dismiss();
            }
        } catch (Exception e) {
        }

        try {
            if ((geoFencingDialog != null) && geoFencingDialog.isShowing()) {
                geoFencingDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            geoFencingDialog = null;
        }

        try {
            if ((logout != null) && logout.isShowing()) {
                logout.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            logout = null;
        }
    }
    class GetLogout extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginLoadingDailog = new TgBufferDialog(BaseActivity.this,getString(R.string.logout_status));
            loginLoadingDailog.setCanceledOnTouchOutside(false);
            loginLoadingDailog.show();

        }

        protected String doInBackground(String... arg0) {
            return getLogoutTest();
        }

        protected void onPostExecute(String result) {
            loginLoadingDailog.dismiss();


        }
    }

    public String getLogoutTest() {

        Map<String, Object> inputMap = new LinkedHashMap<String, Object>();

        inputMap.put("distCode", LoginData.getInstance().getDistCode());
        inputMap.put("shopNo", LoginData.getInstance().getShopNo());

        inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
        inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");

        try {
            FPSLogout fpslogout = XMLUtil.doFPSLogout(inputMap);
            if (fpslogout.getRespMsgCode().contains("0")) {
                Intent i = new Intent(BaseActivity.this, TgLoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();

            }
            return fpslogout.toString();
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loginLoadingDailog.dismiss();
                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(BaseActivity.this, ""+e.getMessage(), true);
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return e.getMessage();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loginLoadingDailog.dismiss();
                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(BaseActivity.this, getString(R.string.connectionRefused), true);
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return e.getMessage();
        }

    }
}
