package com.omneagate.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSDealer;
import com.omneagate.DTO.FPSDealerDetails;
import com.omneagate.DTO.MenuDataDto;
import com.omneagate.DTO.VersionUpgradeDto;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.FpsMemberData;
import com.omneagate.Util.Mantra_Check;
import com.omneagate.Util.Mantra_intialization;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.BillISearchAdapterDate;
import com.omneagate.activity.dialog.ChangeUrlDialog;
import com.omneagate.activity.dialog.DateChangeDialog;
import com.omneagate.activity.dialog.DeviceIdDialog;
import com.omneagate.activity.dialog.LanguageSelectionDialog;
import com.omneagate.activity.dialog.MenuAdapter;
import com.omneagate.activity.dialog.OtpFailedDialog;
import com.omneagate.activity.dialog.TgBufferDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.activity.dialog.TgLoginFailureDialogue;
import com.omneagate.activity.dialog.TgNetworkGenericErrorDialog;
import com.omneagate.activity.dialog.TgTimeDifferenceDialog;
import com.omneagate.exception.FPSException;
import com.omneagate.receiver.ServerTimeSetReceiver;
import com.omneagate.service.HttpClientWrapper;
import com.omneagate.service.Mantra_service;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class
TgLoginActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private Button btSubmit, btExit;
    private ListPopupWindow popupWindow;
    private EditText edtFpsId;
    private String fpsId;
    private TgBufferDialog loginLoadingDailog;
    private final String TAG = TgLoginActivity.class.getCanonicalName();
    private String serverTime;
    private Mantra_intialization mantra_intial;
    public static String mac_id = "";
    private WifiManager manager;
    private BroadcastReceiver broadcastReceiver;
    Mantra_Check mantra_check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_login);

        getmantra_permission();
        getDeviceMacId();
        configureInitView();
        checkUserApk();

    }

    private void configureInitView() {
        updateDateTime();
        loginLoadingDailog = new TgBufferDialog(TgLoginActivity.this,getString(R.string.fetching_fps_details));
        btSubmit = (Button) findViewById(R.id.btSubmit);
        btExit = (Button) findViewById(R.id.btExit);
        networkConnection = new NetworkConnection(this);
        appState = (GlobalAppState) getApplication();
        httpConnection = new HttpClientWrapper();
        edtFpsId = (EditText) findViewById(R.id.login_fps);
        edtFpsId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edtFpsId.getText().toString().length() == 7){
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(edtFpsId.getWindowToken(), 0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkConnection network = new NetworkConnection(TgLoginActivity.this);
                if (network.isNetworkAvailable()) {

                    if (edtFpsId.getText().toString().trim().isEmpty()) {
                        Toast.makeText(TgLoginActivity.this, getResources().getString(R.string.fps_id_empty), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        fpsId = edtFpsId.getText().toString().trim();
                        LoginData.getInstance().setShopNo(fpsId);
                        Log.e(TAG, "<=====SHOP ID====>" + fpsId);

                        if (!isValiduserName(fpsId)) {
                            new TgLoginFailureDialogue(TgLoginActivity.this).show();
                        } else {
                            if (new Mantra_Check(TgLoginActivity.this).checkIsapk_installed()) {
                                String twoDigits = fpsId.substring(0, 2);
                                Log.e(TAG, "first Two digits " + twoDigits);
                                Log.e(TAG, "shopId : " + fpsId);
                                LoginData.getInstance().setTwoDigitsShopID(twoDigits);

                                new GetFpsMemberDetails().execute();
                            }
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }

                } else {
                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgLoginActivity.this, getString(R.string.noNetworkConnection));
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                    return;
                }


            }
        });
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TgLoginActivity.this);
                builder.setMessage(getResources().getString(R.string.exit_apllication))
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishAffinity();
                                //TgLoginActivity.this.finish();
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
    }


    public void showPopupMenu(View v) {
        List<MenuDataDto> menuDto = new ArrayList<>();

        menuDto.add(new MenuDataDto("Device Settings", R.drawable.icon_device_details, "\n" + "డివైస్ సెట్టింగులు"));
        menuDto.add(new MenuDataDto("Server Settings", R.drawable.icon_language, "\n" + "సర్వర్ సెట్టింగులు"));

//        menuDto.add(new MenuDataDto("Device Id :", R.drawable.icon_language, "Server Settings"));

        //  menuDto.add(new MenuDataDto("Printer", R.drawable.icon_printer, "मुद्रक"));
        popupWindow = new ListPopupWindow(this);
        ListAdapter adapter = new MenuAdapter(this, menuDto); // The view ids to map the data to
        popupWindow.setAnchorView(v);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(400); // note: don't use pixels, use a dimen resource
        popupWindow.setOnItemClickListener(this); // the callback for when a list item is selected
        popupWindow.show();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case CHECKVERSION:
                checkData(message);
                break;
            case ERROR_MSG:
                if (loginLoadingDailog != null)
                    loginLoadingDailog.dismiss();
                TgNetworkGenericErrorDialog tgGenericErrorDialog = new TgNetworkGenericErrorDialog(TgLoginActivity.this, "Unable to connect Auto Upgrade server. Connection Timedout.", true);
                tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                tgGenericErrorDialog.show();
                break;
            default:
                if (loginLoadingDailog != null)
                    loginLoadingDailog.dismiss();
                TgNetworkGenericErrorDialog tgGenericErrorDialog1 = new TgNetworkGenericErrorDialog(TgLoginActivity.this, "Unable to connect Auto Upgrade server. Connection Timedout.", true);
                tgGenericErrorDialog1.setCanceledOnTouchOutside(false);
                tgGenericErrorDialog1.show();
                break;
        }

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popupWindow.dismiss();
        MenuDataDto menuDataDto = (MenuDataDto) parent.getItemAtPosition(position);

        String selectedMenu = menuDataDto.getName();
        switch (selectedMenu) {

            case "Device Settings":
                Intent deviceIntent = new Intent(TgLoginActivity.this, TgDeviceSettingActivity.class);
                startActivity(deviceIntent);
                finish();
                break;
            case "Server Settings":
                Intent servcerIntent = new Intent(TgLoginActivity.this, TgServerSettingsActivity.class);
                startActivity(servcerIntent);
                finish();
                break;
        }
    }


    public String getFPSDealerDetailsReqRespTest() {
        Log.e(TAG, "<====Request Started=====>");
        Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
        inputMap.put("shopNo", fpsId);
        AndroidDeviceProperties props = new AndroidDeviceProperties(TgLoginActivity.this);
        inputMap.put("deviceId", props.getDeviceProperties().getSerialNumber());
     //inputMap.put("deviceId", "H2420161123060");
        inputMap.put("simSerialNo", "12121212121212");
        inputMap.put("eposVersion",  XMLUtil.NIC_BUILD_NUMBER);

     /*   String twoDigits = LoginData.getInstance().getTwoDigitsShopID();
        if (twoDigits.equals("16") || twoDigits.equals("15") || twoDigits.equals("33")) {
            inputMap.put("eposVersion", XMLUtil.NIC_BUILD_NUMBER);
        }else{
            inputMap.put("eposVersion", XMLUtil.NIC_BUILD_NUMBER);
        }*/


        inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");

        try {
            FPSDealerDetails fpsDealerDetails = XMLUtil.getFPDDetails(inputMap);
            if (fpsDealerDetails.getRespMsgCode().contains("0")) {
                startService(new Intent(this, Mantra_service.class));
                serverTime = fpsDealerDetails.getCurrDateTime();
                Date deviceTime = new Date();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date serverTime = df.parse(fpsDealerDetails.getCurrDateTime());

                SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date convertedServerTime=new Date(df1.format(serverTime));

                GlobalAppState.serverDate=convertedServerTime;

                Intent intent = new Intent(getApplicationContext(), ServerTimeSetReceiver.class);
                final PendingIntent pIntent = PendingIntent.getBroadcast(this, ServerTimeSetReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                long firstMillis = System.currentTimeMillis();
                AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 60000, pIntent);

                Log.e(TAG,"Device Time : "+deviceTime.getTime());
                Log.e(TAG,"Server Time : "+convertedServerTime.getTime());

                long duration  = deviceTime.getTime()- convertedServerTime.getTime();
                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
                Log.e(TAG,"Time Difference : "+diffInSeconds);

             //   if (diffInSeconds < 300 && diffInSeconds > -300) {
                    LoginData.getInstance().setShopNo(fpsId);
                    Log.e(TAG, "<====Response Transaction ID =====>" + fpsDealerDetails.toString());
                    LoginData.getInstance().setTransactionId(fpsDealerDetails.getTransactionID());
                    LoginData.getInstance().setDistCode(fpsDealerDetails.getDistCode());
                    Log.e(TAG, "<====current Month=====>" + fpsDealerDetails.getCurrMonth());
                    Log.e(TAG, "<====current Year=====>" + fpsDealerDetails.getCurrYear());
                    LoginData.getInstance().setCurrentMonth(fpsDealerDetails.getCurrMonth());
                    LoginData.getInstance().setCurrentYear(fpsDealerDetails.getCurrYear());
                    FpsMemberData.getInstance().setFpsDealerDetails(fpsDealerDetails);
                if(loginLoadingDailog !=null)
                    loginLoadingDailog.dismiss();
                Intent in = new Intent(TgLoginActivity.this, TgFpsMembersActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                finish();
                   /* if(Util.needAutoUpgrade) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                checkUserApk();
                            }
                        });

                    }else{

                        if(loginLoadingDailog !=null)
                        loginLoadingDailog.dismiss();
                        Intent in = new Intent(TgLoginActivity.this, TgFpsMembersActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                    }*/
               /* } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          displayTimedChangedDialog();
                        }
                    });


                }*/


            }
            return fpsDealerDetails.toString();
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(loginLoadingDailog !=null)
                        loginLoadingDailog.dismiss();
                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgLoginActivity.this, "" + e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return e.getMessage();
        } catch (SocketTimeoutException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(loginLoadingDailog !=null)
                        loginLoadingDailog.dismiss();
                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgLoginActivity.this, getString(R.string.connectionRefused));
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return e.getMessage();
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(loginLoadingDailog !=null)
                        loginLoadingDailog.dismiss();
                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgLoginActivity.this, getString(R.string.connectionRefused));
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            Log.e(TAG, " Exception FPSDealerDetails " + e.toString());
            return e.getMessage();
        }

    }
  private void displayTimedChangedDialog(){
      if(loginLoadingDailog !=null)
          loginLoadingDailog.dismiss();
      TgTimeDifferenceDialog tgTimeDifferenceDialog =new TgTimeDifferenceDialog(TgLoginActivity.this,serverTime);
      tgTimeDifferenceDialog.show();
  }
    public void checkUserApk() {
        try {
            if (networkConnection.isNetworkAvailable()) {
                loginLoadingDailog.setCanceledOnTouchOutside(false);
                loginLoadingDailog.show();
                VersionUpgradeDto version = new VersionUpgradeDto();
                String url = "/versionUpgrade/view";
                String checkVersion = new Gson().toJson(version);
                Log.e("Version", "" + checkVersion);
                StringEntity se = new StringEntity(checkVersion, HTTP.UTF_8);
                Util.LoggingQueue(this, "Device Register Version", "Checking version of apk in device");
                httpConnection.sendRequest(url, null, ServiceListenerType.CHECKVERSION,
                        SyncHandler, RequestType.POST, se, this);
            } else {
                TgNetworkGenericErrorDialog tgGenericErrorDialog = new TgNetworkGenericErrorDialog(TgLoginActivity.this, getString(R.string.noNetworkConnection),true);
                tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                tgGenericErrorDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Util.messageBar(this, getString(R.string.errorUpgrade));
        }
    }

    private void checkData(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e(TAG, "Login APK version response" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            VersionUpgradeDto versionUpgradeDto = gson.fromJson(response, VersionUpgradeDto.class);
            if (versionUpgradeDto == null || versionUpgradeDto.getVersion() == 0 || StringUtils.isEmpty(versionUpgradeDto.getLocation())) {
                //dismissProgress();
                Util.messageBar(this, getString(R.string.errorUpgrade));
            } else {
                if (versionUpgradeDto.getStatusCode() == 0) {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    if (versionUpgradeDto.getVersion() > pInfo.versionCode) {
                        //   dismissProgress();
                        if(loginLoadingDailog !=null)
                        loginLoadingDailog.dismiss();
                        Intent intent = new Intent(this, AutoUpgrationActivity.class);
                        intent.putExtra("downloadPath", versionUpgradeDto.getLocation());
                        intent.putExtra("newVersion", versionUpgradeDto.getVersion());
                        startActivity(intent);
                        finish();
                    } else {
                        if(loginLoadingDailog !=null)
                        loginLoadingDailog.dismiss();
                      /*  Intent in = new Intent(TgLoginActivity.this, TgFpsMembersActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();*/
                    }
                } else {
                    // dismissProgress();
                    if(loginLoadingDailog !=null)
                    loginLoadingDailog.dismiss();
                    Util.messageBar(this, getString(R.string.errorUpgrade));
                }
            }
        } catch (Exception e) {
            //  dismissProgress();
            if(loginLoadingDailog !=null)
            loginLoadingDailog.dismiss();
            Util.messageBar(this, getString(R.string.errorUpgrade));
        }
    }


    class GetFpsMemberDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginLoadingDailog.setCanceledOnTouchOutside(false);
            loginLoadingDailog.show();

        }

        protected String doInBackground(String... arg0) {
            return getFPSDealerDetailsReqRespTest();
        }

        protected void onPostExecute(String result) {
            //loginLoadingDailog.dismiss();
        }
    }

    private boolean isValiduserName(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }
    private void getmantra_permission() {
        mantra_intial = new Mantra_intialization(this);
    }

    private void getDeviceMacId() {
        mac_id = getSharedPreferences("FPS_POS", MODE_PRIVATE).getString(FPSDBConstants.KEY_WIFI_MAC_ID, "");
        if (mac_id.isEmpty()) {
            manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            mac_id = Util.recupAdresseMAC(manager);
            if (manager.isWifiEnabled()) {
                save_mac_id();
            } else {
                setup_broadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
                registerReceiver(broadcastReceiver, intentFilter);
                manager.setWifiEnabled(true);
            }
        }
    }
    private void setup_broadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                Log.e("wifi action", action);
                if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                    if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                        //do stuff
                        Log.e("wifi action", "connected");
                        save_mac_id();
                    } else {
                        // wifi connection was lost
                        Log.e("wifi action", "disconnected");
                    }
                }
            }
        };
    }

    private void save_mac_id() {
        mac_id = Util.recupAdresseMAC(manager);
        if (!mac_id.isEmpty())
            getSharedPreferences("FPS_POS", MODE_PRIVATE).edit().putString(FPSDBConstants.KEY_WIFI_MAC_ID, mac_id).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mantra_intial != null)
            mantra_intial.UnInitScanner();

        try {
            if (broadcastReceiver != null)
                unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
