package com.omneagate.activity;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.ActivityBusinessClass.LoginCheck;
import com.omneagate.DTO.AppfeatureDto;
import com.omneagate.DTO.ConfigurationResponseDto;
import com.omneagate.DTO.DeviceRegistrationDto;
import com.omneagate.DTO.DeviceRegistrationResponseDto;
import com.omneagate.DTO.EnumDTO.DeviceStatus;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginHistoryDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.MenuDataDto;
import com.omneagate.DTO.UserDetailDto;
import com.omneagate.DTO.VersionUpgradeDto;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.InsertIntoDatabase;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.SyncPageUpdate;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.BluetoothDialog;
import com.omneagate.activity.dialog.ChangeUrlDialog;
import com.omneagate.activity.dialog.DateChangeDialog;
import com.omneagate.activity.dialog.DeviceIdDialog;
import com.omneagate.activity.dialog.GpsAlertDialog;
import com.omneagate.activity.dialog.LanguageSelectionDialog;
import com.omneagate.activity.dialog.MenuAdapter;
import com.omneagate.activity.dialog.PurgeBillBItemDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Login activity  is used to login and also used to validate user
 */
public class LoginActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    //Popup window for menu
    ListPopupWindow popupWindow;
    LoginResponseDto loginResponse;
    ConfigurationResponseDto configurationResponse;
    List<BluetoothDevice> mDeviceList;
    BluetoothAdapter mBluetoothAdapter;
    //User textBox for entering username and password
    private EditText userNameTextBox, passwordTextBox;
    //    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    BluetoothDialog bluetoothDialog;
    ChangeUrlDialog changeUrlDialog;
    DateChangeDialog dateChangeDialog;
    DeviceIdDialog deviceIdDialog;
    GpsAlertDialog gpsAlertDialog;
    LanguageSelectionDialog languageSelectionDialog;
    PurgeBillBItemDialog purgeBillBItemDialog;
    String TAG = "LoginActivity";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    mBluetoothAdapter.startDiscovery();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<>();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                progressBar.dismiss();
                if (mDeviceList == null) {
                    mDeviceList = new ArrayList<>();
                }
                if (mDeviceList.size() > 0) {
                    bluetoothDialog = new BluetoothDialog(com.omneagate.activity.LoginActivity.this, mDeviceList);
                    bluetoothDialog.show();
                } else {
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.no_records));
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                if (mDeviceList == null) {
                    mDeviceList = new ArrayList<>();
                }
                mDeviceList.add((BluetoothDevice) intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            networkConnection = new NetworkConnection(this);
            userNameTextBox = (EditText) findViewById(R.id.login_username);
            passwordTextBox = (EditText) findViewById(R.id.login_password);
            try {
                String deviceid = new AndroidDeviceProperties(this).getDeviceProperties().getSerialNumber();
                if (deviceid.equals("U2420161101447")) {
                    userNameTextBox.setText("mohanfps");
                    passwordTextBox.setText("test123");
                } else if (deviceid.equals("F6C3D49ECFEAEE76")) {
                    userNameTextBox.setText("arunp");
                    passwordTextBox.setText("alluser");
                }else if(deviceid.equals("U2420161102205")){
                    userNameTextBox.setText("bharath");
                    passwordTextBox.setText("alluser");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            appState = (GlobalAppState) getApplication();
            httpConnection = new HttpClientWrapper();
            checkErrorCode();
            Util.LoggingQueue(this, "Login Entry", "Inside Login Page");
        } catch (Exception e) {
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    public void bluetoothRegister(BluetoothDevice device) {
        try {
            unregisterReceiver(mReceiver);
            SharedPreferences mySharedPreferences = getSharedPreferences("FPS_POS",
                    MODE_PRIVATE);
            String json = new Gson().toJson(device);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("bluetoothDevices", json);
            editor.putString("printeraddress", device.getAddress());
            editor.apply();
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    private void checkErrorCode() {
        SharedPreferences mySharedPreferences = getSharedPreferences("FPS_POS",
                MODE_PRIVATE);
        if (!mySharedPreferences.getBoolean("isAdjusted", false)) {
            Log.e("Already Updated", "Already Updated isAdjusted");
            FPSDBHelper.getInstance(this).updateStockInwardNew();
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putBoolean("isAdjusted", true);
            editor.apply();
        }
        if (FPSDBHelper.getInstance(this).getAllErrorMessages() > 0) {
            Log.e("Already Updated", "Already Updated");
        } else {
            InsertIntoDatabase database = new InsertIntoDatabase(this);
            database.updateDatabase();
        }
        if (FPSDBHelper.getInstance(this).getErrorMessages() > 0) {
            Log.e("Already Updated", "Already Updated");
        } else {
            InsertIntoDatabase database = new InsertIntoDatabase(this);
            database.updateDatabaseNew();
        }
        if (FPSDBHelper.getInstance(this).getErrorMessages2() > 0) {
            Log.e("Already Updated", "Already Updated");
        } else {
            InsertIntoDatabase database = new InsertIntoDatabase(this);
            database.updateDatabaseNew2();
        }
        if (FPSDBHelper.getInstance(this).getErrorMessages3() > 0) {
            Log.e("Already Updated", "Already Updated");
        } else {
            InsertIntoDatabase database = new InsertIntoDatabase(this);
            database.updateDatabaseNew3();
        }
        if (FPSDBHelper.getInstance(this).getErrorMessages4() > 0) {
            Log.e("Already Updated", "Already Updated");
        } else {
            InsertIntoDatabase database = new InsertIntoDatabase(this);
            database.updateDatabaseNew4();
        }
        if (FPSDBHelper.getInstance(this).getErrorMessages5() > 0) {
            Log.e("Already Updated", "Already Updated");
        } else {
            InsertIntoDatabase database = new InsertIntoDatabase(this);
            database.updateDatabaseNew5();
        }
        if (FPSDBHelper.getInstance(this).getErrorMessages6() > 0) {
            Log.e("Already Updated", "Already Updated");
        } else {
            InsertIntoDatabase database = new InsertIntoDatabase(this);
            database.updateDatabaseNew6();
        }
    }

    private void printerConfiguration() {
        progressBar = new CustomProgressDialog(this);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
        mBluetoothAdapter.startDiscovery();
    }

    /**
     * Menu creation
     * Used to change language
     * *
     */
    public void showPopupMenu(View v) {
        List<MenuDataDto> menuDto = new ArrayList<>();
        SharedPreferences mySharedPreferences = getSharedPreferences("FPS_POS", MODE_PRIVATE);
        if (mySharedPreferences.getBoolean("sync_complete", false)) {
            menuDto.add(new MenuDataDto("Officer Login", R.drawable.officer_login, "अधिकारी लॉगिन"));
        }
        menuDto.add(new MenuDataDto("Language", R.drawable.icon_language, "भाषा"));
        menuDto.add(new MenuDataDto("Change URL", R.drawable.icon_server, "परिवर्तनउरल"));
        menuDto.add(new MenuDataDto("Device Details", R.drawable.icon_device_details, "डिवाइस के विवरण"));
        menuDto.add(new MenuDataDto("Printer", R.drawable.icon_printer, "मुद्रक"));
        popupWindow = new ListPopupWindow(this);
        ListAdapter adapter = new MenuAdapter(this, menuDto); // The view ids to map the data to
        popupWindow.setAnchorView(v);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(400); // note: don't use pixels, use a dimen resource
        popupWindow.setOnItemClickListener(this); // the callback for when a list item is selected
        popupWindow.show();
        Util.LoggingQueue(this, "Login Entry", "Inside Popup Window");
    }

    private void startOfficerLogin() {
        startActivity(new Intent(LoginActivity.this, InspectionLoginActivity.class));
        finish();
    }

    /**
     * Menu item click listener
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popupWindow.dismiss();
        Log.e(TAG, "menu item..." + parent.getItemAtPosition(position).toString());
        MenuDataDto menuDataDto = (MenuDataDto) parent.getItemAtPosition(position);

        String selectedMenu = menuDataDto.getName();
        switch (selectedMenu) {
            case "Officer Login":
                startOfficerLogin();
                break;
            case "Language":
                languageSelectionDialog = new LanguageSelectionDialog(this);
                languageSelectionDialog.show();
                break;
            case "Change URL":
                changeUrlDialog = new ChangeUrlDialog(this);
                changeUrlDialog.show();
                break;
            case "Printer":
                printerConfiguration();
                break;
            case "Device Details":
                deviceIdDialog = new DeviceIdDialog(this);
                deviceIdDialog.show();
                break;

        }
    }

    //onclick event for login button
    public void userLogin(View view) {
        try {
                /*Keyboard disappearance*/
            InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
            LoginDto loginCredentials = getUsernamePassword();
            if (loginCredentials == null) {
                return;
            }
            authenticateUser(loginCredentials);
        } catch (Exception e) {
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    public void turnGPSOn() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 1);
    }

    /**
     * sending login details to server if network connection available
     *
     * @param loginCredentials for user
     */
    private void authenticateUser(LoginDto loginCredentials) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/login/validateuser";
                Log.e("LoginActivity", "request online..." + loginCredentials);
                loginCredentials.setDeviceId(Settings.Secure.getString(
                        getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                String login = new Gson().toJson(loginCredentials);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGIN_USER,
                        SyncHandler, RequestType.POST, se, this);
            } else {
                Log.e("LoginActivity", "offline login...");
                progressBar = new CustomProgressDialog(this);
                progressBar.setCanceledOnTouchOutside(false);
                SharedPreferences mySharedPreferences = getSharedPreferences("FPS_POS",
                        MODE_PRIVATE);
                if (mySharedPreferences.getBoolean("sync_complete", false)) {
                    LoginCheck loginLocal = new LoginCheck(this, progressBar);
                    loginLocal.localLogin(loginCredentials);
                } else {
                    dismissProgress();
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.noNetworkConnection));
                }
            }
        } catch (Exception e) {
            dismissProgress();
            Util.LoggingQueue(this, "Login Request Error", e.toString());
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void getConfiguration() {
        try {
            Log.e("LoginActivity", "getConfiguration.........");
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                String url = "/posglobalconfig/getAll";
                String configReqParam = "{\n" +
                        "    \"posAppType\":\"FPS\"\n" +
                        "}";
                StringEntity se = new StringEntity(configReqParam, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CONFIGURATION,
                        SyncHandler, RequestType.POST, se, this);
            } else {
            }
        } catch (Exception e) {
            dismissProgress();
            Util.LoggingQueue(this, "configuration Request Error", e.toString());
            Log.e("LoginActivity", e.toString(), e);
        }
    }


    private void registerDevice(LoginDto loginCredentials) {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/device/registerDevice";
                AndroidDeviceProperties deviceProperties = new AndroidDeviceProperties(this);
                DeviceRegistrationDto deviceRegister = new DeviceRegistrationDto();
                deviceRegister.setLoginDto(loginCredentials);
                deviceRegister.setDeviceDetailsDto(deviceProperties.getDeviceProperties());
                String login = new Gson().toJson(deviceRegister);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                Util.LoggingQueue(this, "Device Register request", login);
                httpConnection.sendRequest(url, null, ServiceListenerType.DEVICE_REGISTER,
                        SyncHandler, RequestType.POST, se, this);
            } else {
                Util.messageBar(this, getString(R.string.connectionError));
            }
        } catch (Exception e) {
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    //return login DTO if it is valid else null
    private LoginDto getUsernamePassword() {
        LoginDto loginCredentials = new LoginDto();
        String userName = userNameTextBox.getText().toString();
        String password = passwordTextBox.getText().toString();
        //Username field empty
        if (StringUtils.isEmpty(userName)) {
            Util.messageBar(this, getString(R.string.loginUserNameEmpty));
            return null;
        }
        //password field empty
        if (StringUtils.isEmpty(password)) {
            Util.messageBar(this, getString(R.string.loginPasswordEmpty));
            return null;
        }
        loginCredentials.setUserName(userName);
        loginCredentials.setPassword(password);
        return loginCredentials;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.setTamilText((Button) findViewById(R.id.login_loginButton), R.string.loginButton);
        GlobalAppState.localLogin = false;
        setTamilHeader((TextView) findViewById(R.id.login_actionbar), R.string.headerAllPageEnglish);
        setTamil(((TextView) findViewById(R.id.login_actionbarTamil)), R.string.headerAllPage);
        Util.setTamilText(((TextView) findViewById(R.id.appName)), getString(R.string.fpsposapplication));
        removeAllServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /*Concrete method*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        Util.LoggingQueue(LoginActivity.this, "LoginActivity ",
                "processMessage() called " +
                        " Type -> " + what);
        Util.LoggingQueue(LoginActivity.this, "LoginActivity ",
                "processMessage() called message -> " + message + " Type -> " + what);
        switch (what) {
            case LOGIN_USER:
                Log.e("LOGIN_USER", "" + message);
                userLoginResponse(message);
                break;
            case DEVICE_REGISTER:
                userRegisterResponse(message);
                break;
            case CHECKVERSION:
                checkData(message);
                break;
            case CONFIGURATION:
                getConfigurationResponse(message);
                break;
            case ERROR_MSG:
                Toast.makeText(getBaseContext(), R.string.connectionRefused, Toast.LENGTH_LONG).show();
                break;
            default:
                dismissProgress();
                SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
                if (!prefs.getBoolean("approved", false)) {
                    dismissProgress();
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.connectionRefused));
                } else {
                    SharedPreferences mySharedPreferences = getSharedPreferences("FPS_POS",
                            MODE_PRIVATE);
                    if (mySharedPreferences.getBoolean("sync_complete", false)) {
                        LoginCheck loginLocal = new LoginCheck(this, progressBar);
                        loginLocal.localLogin(getUsernamePassword());
                    } else {
                        dismissProgress();
                        Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.connectionRefused));
                    }
                }
                break;
        }
        dismissProgress();
    }

    private void checkData(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Util.LoggingQueue(this, "Login APK version response", response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            VersionUpgradeDto versionUpgradeDto = gson.fromJson(response, VersionUpgradeDto.class);
            if (versionUpgradeDto == null || versionUpgradeDto.getVersion() == 0 || StringUtils.isEmpty(versionUpgradeDto.getLocation())) {
                dismissProgress();
                Util.messageBar(this, getString(R.string.errorUpgrade));
            } else {
                if (versionUpgradeDto.getStatusCode() == 0) {
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    if (versionUpgradeDto.getVersion() > pInfo.versionCode) {
                        dismissProgress();
                        Intent intent = new Intent(this, AutoUpgrationActivity.class);
                        intent.putExtra("downloadPath", versionUpgradeDto.getLocation());
                        intent.putExtra("newVersion", versionUpgradeDto.getVersion());
                        startActivity(intent);
                        finish();
                    } else {
                        authenticationSuccess();
                    }
                } else {
                    dismissProgress();
                    Util.messageBar(this, getString(R.string.errorUpgrade));
                }
            }
        } catch (Exception e) {
            dismissProgress();
            Util.LoggingQueue(this, "Login Request Error", e.toString());
            Util.messageBar(this, getString(R.string.errorUpgrade));
            Log.e("LoginActivity", e.toString(), e);
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

    /**
     * After login response received from server successfully in android
     *
     * @param message bundle that received
     */
    private void userLoginResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("LoginActivity", "Login Request response:" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            loginResponse = gson.fromJson(response, LoginResponseDto.class);

            LoginData.getInstance().setLoginData(loginResponse);
            if (loginResponse != null) {
                if (loginResponse.getStatusCode() == 500) {
                    SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
                    if (!prefs.getBoolean("approved", false)) {
                        dismissProgress();
                        Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.connectionRefused));
                    } else {
                        SharedPreferences mySharedPreferences = getSharedPreferences("FPS_POS",
                                MODE_PRIVATE);
                        if (mySharedPreferences.getBoolean("sync_complete", false)) {
                            LoginCheck loginLocal = new LoginCheck(this, progressBar);
                            loginLocal.localLogin(getUsernamePassword());
                        } else {
                            dismissProgress();
                            Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.connectionRefused));
                        }
                    }
                } else {

                    try {
                        if (loginResponse.getDeviceStatus() == DeviceStatus.ACTIVE) {
                            Util.LoggingQueue(this, "DeviceStatus is ACTIVE", "Update DB ");
                            FPSDBHelper.getInstance(this).updateMaserData("status", "ACTIVE");
                        }
                        if (loginResponse.getDeviceStatus() == DeviceStatus.UNASSOCIATED) {
                            Util.LoggingQueue(this, "DeviceStatus is ACTIVE", "Update DB ");
                            FPSDBHelper.getInstance(this).updateMaserData("status", "UNASSOCIATED");
                        }
                    } catch (Exception e) {
                    }
                    if (loginResponse.isAuthenticationStatus()) {
                        checkUserApk();
                    } else {
                        checkDeviceStatus();
                    }
//                      checkDeviceStatus();
                }
            } else {
                dismissProgress();
                Util.messageBar(this, getString(R.string.serviceNotAvailable));
            }
        } catch (Exception e) {

            dismissProgress();
            SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
            if (!prefs.getBoolean("approved", false)) {
                dismissProgress();
                Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.connectionRefused));
            } else {
                SharedPreferences mySharedPreferences = getSharedPreferences("FPS_POS",
                        MODE_PRIVATE);
                if (mySharedPreferences.getBoolean("sync_complete", false)) {
                    LoginCheck loginLocal = new LoginCheck(this, progressBar);
                    loginLocal.localLogin(getUsernamePassword());
                } else {
                    dismissProgress();
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.connectionRefused));
                }
            }
        }
    }

    private void getConfigurationResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e(TAG, "config response..." + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            configurationResponse = gson.fromJson(response, ConfigurationResponseDto.class);
            if (configurationResponse.getStatusCode() == 0) {
                HashMap<String, String> configMap = configurationResponse.getPosGlobalConfigMap();
                String purgeDays = configMap.get("purgeDays");

                String ageLimit = configMap.get("BENEFICIARY_CHILD_MEMBER_AGE_LIMIT");
                String geoFencing = configMap.get("POS_GEO_FENCING_DISTANCE_RANGE");
                Util.LoggingQueue(LoginActivity.this, "LoginActivity ",
                        "getConfigurationResponse()  Bundle BENEFICIARY_CHILD_MEMBER_AGE_LIMIT = " + ageLimit);
                Util.LoggingQueue(LoginActivity.this, "LoginActivity ",
                        "getConfigurationResponse()  Bundle POS_GEO_FENCING_DISTANCE_RANGE = " + geoFencing);
                FPSDBHelper.getInstance(this).updateMaserData("purgeBill", purgeDays);
                FPSDBHelper.getInstance(this).insertMaserData("ageLimit", ageLimit);
                FPSDBHelper.getInstance(this).insertMaserData("geoFencing", geoFencing);
                String heartBeatPurgeDays = configMap.get("POS_HEARTBEAT_PURGE_DAYS");
                Log.e(TAG, "heartBeatPurgeDays..." + heartBeatPurgeDays);
                FPSDBHelper.getInstance(this).insertMaserData("POS_HEARTBEAT_PURGE_DAYS", heartBeatPurgeDays);
                String UID_AUTH_TerminalId = configMap.get("UID_AUTH_TerminalId");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_TerminalId", UID_AUTH_TerminalId);
                String UID_AUTH_Fdc = configMap.get("UID_AUTH_Fdc");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_Fdc", UID_AUTH_Fdc);
                String UID_AUTH_Lov = configMap.get("UID_AUTH_Lov");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_Lov", UID_AUTH_Lov);
                String UID_AUTH_PublicIp = configMap.get("UID_AUTH_PublicIp");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_PublicIp", UID_AUTH_PublicIp);
                String UID_AUTH_Udc = configMap.get("UID_AUTH_Udc");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_Udc", UID_AUTH_Udc);
                String UID_AUTH_securityToken = configMap.get("UID_AUTH_securityToken");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_securityToken", UID_AUTH_securityToken);
                String UID_AUTH_clientId = configMap.get("UID_AUTH_clientId");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_clientId", UID_AUTH_clientId);
                String UID_AUTH_uriString = configMap.get("UID_AUTH_uriString");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_uriString", UID_AUTH_uriString);
                String UID_AUTH_certificateName = configMap.get("UID_AUTH_certificateName");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_certificateName", UID_AUTH_certificateName);
                String uidMandatory = configMap.get("FPS_CARD_REGISTRATION_UID_MANDATORY");
                FPSDBHelper.getInstance(this).insertMaserData("UID_MANDATORY", uidMandatory);
                String POS_RECONCILIATION_ENABLE_STATUS = configMap.get("POS_RECONCILIATION_ENABLE_STATUS");
                FPSDBHelper.getInstance(this).insertMaserData("POS_RECONCILIATION_ENABLE_STATUS", POS_RECONCILIATION_ENABLE_STATUS);
                String UID_AUTH_bfd_uriString = configMap.get("UID_AUTH_bfd_uriString");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_bfd_uriString", UID_AUTH_bfd_uriString);
                String UID_AUTH_bfd_securityToken = configMap.get("UID_AUTH_bfd_securityToken");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH__bfd_securityToken", UID_AUTH_bfd_securityToken);
                String UID_AUTH_bfd_certificateName = configMap.get("UID_AUTH_certificateName");
                FPSDBHelper.getInstance(this).insertMaserData("UID_AUTH_bfd_certificateName", UID_AUTH_bfd_certificateName);
                String stock_validation = configMap.get("stockValidationEnabled");
                FPSDBHelper.getInstance(this).insertMaserData("stock_validation", stock_validation);

            } else if (loginResponse.getStatusCode() == 12025) {
            }
        } catch (Exception e) {
            dismissProgress();
            Util.LoggingQueue(this, "configuration Request Error", e.toString());
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void checkDeviceStatus() {
        SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
        Log.e("status", "Inside device status");
        if (loginResponse.getDeviceStatus() == DeviceStatus.ACTIVE && loginResponse.isAuthenticationStatus()) {
            Util.LoggingQueue(this, "checkDeviceStatus()", "Active and Authendicated");
            FPSDBHelper.getInstance(this).updateMaserData("status", "ACTIVE");
            authenticationSuccess();
        } else if (loginResponse.getDeviceStatus() == DeviceStatus.UNASSOCIATED) {
            Util.LoggingQueue(this, "checkDeviceStatus()", "UNASSOCIATED");
            if (!prefs.getBoolean("approved", false)) {
                Util.LoggingQueue(this, "checkDeviceStatus()", "not approved");
                checkForRegistration(loginResponse.getUserDetailDto());
            } else {
                Util.LoggingQueue(this, "checkDeviceStatus()", " approved");
                dismissProgress();
                Util.messageBar(this, getString(R.string.unassociated));
                try {
                    if (!LoginData.getInstance().getLoginData().getUserDetailDto().getProfile().equalsIgnoreCase("ADMIN")) {
                        Log.e("LoginActivity", "!ADMIN" + loginResponse.getStatusCode());
                        FPSDBHelper.getInstance(this).updateMaserData("status", "UNASSOCIATED");
                        Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.unassociated));
                    }
                } catch (Exception e) {
                }
            }
        } else if (loginResponse.getDeviceStatus() == DeviceStatus.INACTIVE) {
            if (loginResponse.getStatusCode() == 1000) {
                dismissProgress();
                Util.messageBar(this, getString(R.string.inCorrectUnamePword));
            } else if (loginResponse.getStatusCode() == 1002) {
                dismissProgress();
                Util.messageBar(this, getString(R.string.inCorrectUnamePword));
            } else if (loginResponse.getStatusCode() == 5004 || loginResponse.getStatusCode() == 5024 || loginResponse.getStatusCode() == 5055) {
                dismissProgress();
                try {
                    Log.e("LoginActivity", "!ADMIN" + loginResponse.getStatusCode());
                    FPSDBHelper.getInstance(this).updateMaserData("status", "INACTIVE");
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.deviceInvalid));
                } catch (Exception e) {
                }
            } else if (loginResponse.getStatusCode() == 5057) {
                dismissProgress();
                Log.e("LoginActivity", "UNASSOCIATED" + loginResponse.getStatusCode());
                try {
                    Log.e("LoginActivity", "!ADMIN" + loginResponse.getStatusCode());
                    FPSDBHelper.getInstance(this).updateMaserData("status", "UNASSOCIATED");
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.unassociated));
                } catch (Exception e) {
                }
            } else if (loginResponse.getStatusCode() == 32000 || loginResponse.getStatusCode() == 5025) {
                dismissProgress();
                try {
                    FPSDBHelper.getInstance(this).updateShopActiveStatusDetails(userNameTextBox.getText().toString());
                    Log.e(TAG, "a............");
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.storeInactive));
                } catch (Exception e) {
                }
            } else if (loginResponse.getStatusCode() == 5095) {
                dismissProgress();
                try {
                    FPSDBHelper.getInstance(this).updateUserActiveStatusDetails(userNameTextBox.getText().toString());

                    String messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(loginResponse.getStatusCode()));
                    if (messageData == null) {
                        messageData = getString(R.string.internalError);
                    }
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, messageData);
                } catch (Exception e) {
                }
            } else {
                dismissProgress();
                String messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(loginResponse.getStatusCode()));
                if (messageData == null) {
                    messageData = getString(R.string.internalError);
                }
                Toast.makeText(LoginActivity.this, messageData, Toast.LENGTH_SHORT).show();
            }
        } else {
            dismissProgress();
            Util.messageBar(this, getString(R.string.inCorrectUnamePword));
        }
    }

    private void checkUserApk() {
        try {
            if (networkConnection.isNetworkAvailable()) {
                SessionId.getInstance().setSessionId(loginResponse.getSessionid());
                VersionUpgradeDto version = new VersionUpgradeDto();
                String url = "/versionUpgrade/view";
                String checkVersion = new Gson().toJson(version);
                Log.e("Version", checkVersion);
                StringEntity se = new StringEntity(checkVersion, HTTP.UTF_8);
                Util.LoggingQueue(this, "Device Register Version", "Checking version of apk in device");
                httpConnection.sendRequest(url, null, ServiceListenerType.CHECKVERSION,
                        SyncHandler, RequestType.POST, se, this);
            } else {
                dismissProgress();
                Util.messageBar(this, getString(R.string.connectionError));
            }
        } catch (Exception e) {
            dismissProgress();
            Util.messageBar(this, getString(R.string.inCorrectUnamePword));
        }
    }

    /**
     * After successful login of user
     * Userdetails will be stored in Singleton class
     */
    private void authenticationSuccess() {
        try {
            //Util.LoggingQueue(this, "Login response", loginResponse.toString());
            Util.LoggingQueue(LoginActivity.this, "LoginActivity", "authenticationSuccess() called");
            SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
            SessionId.getInstance().setSessionId(loginResponse.getSessionid());
            SessionId.getInstance().setUserId(loginResponse.getUserDetailDto().getId());
            SessionId.getInstance().setLocalpasword(passwordTextBox.getText().toString().trim());
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isActive", loginResponse.getGlobalConfigs().get(0).isActive());
            editor.apply();
            String lastLoginTime = FPSDBHelper.getInstance(this).getLastLoginTime(loginResponse.getUserDetailDto().getId());
            if (StringUtils.isNotEmpty(lastLoginTime)) {
                SessionId.getInstance().setLastLoginTime(new Date(Long.parseLong(lastLoginTime)));
            } else {
                SessionId.getInstance().setLastLoginTime(new Date());
            }
            SessionId.getInstance().setLoginTime(new Date());
            if (loginResponse.getUserDetailDto().getFpsStore() != null) {
                Util.LoggingQueue(LoginActivity.this, "LoginActivity", "authenticationSuccess() IF getFpsStore is Present");
                GlobalAppState.isLoggingEnabled = loginResponse.getUserDetailDto().getFpsStore().isRemoteLogEnabled();
                SessionId.getInstance().setFpsId(loginResponse.getUserDetailDto().getFpsStore().getId());
                SessionId.getInstance().setFpsCode(loginResponse.getUserDetailDto().getFpsStore().getGeneratedCode());
            } else {
                Util.LoggingQueue(LoginActivity.this, "LoginActivity", "authenticationSuccess() ELSe");
                loginResponse.getUserDetailDto().setFpsStore(new FpsStoreDto());
            }
            SessionId.getInstance().setUserName(loginResponse.getUserDetailDto().getUserId());
            LoginData.getInstance().setLoginData(loginResponse);
            loginResponse.getUserDetailDto().setPassword(loginResponse.getUserDetailDto().getPassword());
            FPSDBHelper.getInstance(this).insertLoginUserData(loginResponse, passwordTextBox.getText().toString().trim());
            FPSDBHelper.getInstance(this).setLastLoginTime(loginResponse.getUserDetailDto().getId());
//            FPSDBHelper.getInstance(this).purgeBill();
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("keyEncrypt", loginResponse.getKey());
            edit.putString("longitude", loginResponse.getUserDetailDto().getFpsStore().getLongitude());
            edit.putString("latitude", loginResponse.getUserDetailDto().getFpsStore().getLatitude());
            if (loginResponse.getUserDetailDto().getFpsStore().getGeofencing() != null)
                edit.putBoolean("fencing", loginResponse.getUserDetailDto().getFpsStore().getGeofencing());
            edit.apply();
            Util.LoggingQueue(this, "Bluetooth", "Bluetooth pairing");
            if (!prefs.getBoolean("approved", false)) {
                checkForRegistration(loginResponse.getUserDetailDto());
                Util.LoggingQueue(this, "Login response", "Not Approved");
            } else {
                checkTime();
            }
            getConfiguration();
        } catch (Exception e) {
            Util.LoggingQueue(this, "Log in success error", e.toString());
            Log.e("LoginActivity", e.toString(), e);
        } finally {
            dismissProgress();
        }
    }

    /**
     * Used to check whether device is registered or not
     *
     * @param userDetails from server
     */
    private void checkForRegistration(UserDetailDto userDetails) {
        if (userDetails != null && StringUtils.equalsIgnoreCase(userDetails.getProfile(), "ADMIN")) {
            LoginDto loginCredentials = getUsernamePassword();
            if (loginCredentials == null) {
                return;
            }
            SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
            if (!prefs.getBoolean("register", false)) {
                Util.LoggingQueue(this, "Device Register", "Device Registration process started");
                registerDevice(loginCredentials);
            } else {
                dismissProgress();
                Util.LoggingQueue(this, "Device Register", "Device Registration already done");
                startActivity(new Intent(this, RegistrationActivity.class));
                finish();
            }
        } else {
            dismissProgress();
            Util.LoggingQueue(this, "Device Register", "Insufficient Credentials");
            Util.messageBar(this, getString(R.string.inCorrectUserCredential));
        }
    }

    /**
     * After registration response received from server successfully in android
     *
     * @param message bundle that received
     */
    private void userRegisterResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Util.LoggingQueue(this, "Device Register response", response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            DeviceRegistrationResponseDto deviceRegistrationResponse = gson.fromJson(response,
                    DeviceRegistrationResponseDto.class);
            dismissProgress();
            if (deviceRegistrationResponse.getStatusCode() == 0) {
                Util.storePreferenceRegister(this);
                startActivity(new Intent(this, RegistrationActivity.class));
                finish();
            } else if (deviceRegistrationResponse.getStatusCode() == 5055) {
                Util.LoggingQueue(this, "Device Register", "Device already registered");
                Util.storePreferenceApproved(this);
                checkTime();
            } else {
                Util.messageBar(this, getString(R.string.inCorrectUnamePword));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Registered error", e.toString());
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void checkTime() {
        long diff = new Date().getTime() - new Date(loginResponse.getServerTime()).getTime();//as given
        Util.LoggingQueue(this,"Time check","server Time"+loginResponse.getServerTime());
        Util.LoggingQueue(this, "Time check", "Check user time and server time");
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        Util.LoggingQueue(this, "Time check", "Time difference:" + seconds);
        if (seconds < 300 && seconds > -300) {
            loginSuccess();
        } else {
            dismissProgress();
            dateChangeDialog = new DateChangeDialog(this, loginResponse.getServerTime());
            dateChangeDialog.show();
        }
    }

    /**
     * After login success the user navigation to Sale activity page
     * And also start the connection heartBeat service
     */
    private void loginSuccess() {
        Util.storePreferenceApproved(this);
        Util.LoggingQueue(this, "Logged in", "User login success");
        SharedPreferences mySharedPreferences = getSharedPreferences("FPS_POS",
                MODE_PRIVATE);
        LoginData.getInstance().setLoginData(loginResponse);
        Log.e("Role Features", loginResponse.getRoleFeatures().toString());
        insertRoleFeatures(loginResponse.getRoleFeatures());
        if (mySharedPreferences.getBoolean("sync_complete", false)) {
            navigationToAdmin();
        } else {
            insertLoginHistoryDetails();
            dismissProgress();
            String lastModifiedDate = FPSDBHelper.getInstance(this).getMasterData("syncTime");
            if (StringUtils.isNotEmpty(lastModifiedDate)) {
                SyncPageUpdate syncPage = new SyncPageUpdate(this);
                syncPage.setSync();
                navigationToAdmin();
            } else {
                if (loginResponse.getUserDetailDto().getProfile().equalsIgnoreCase("ADMIN")) {
                        startActivity(new Intent(this, SyncPageActivity.class));
                        finish();
                } else {
                    Util.messageBar(this, getString(R.string.inCorrectUserCredential));
                    return;
                }
            }
        }
    }

    private long getFpsId() {
        long fpsId = 0;
        try {
            LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(LoginActivity.this).getFpsUserDetails();
            fpsId = loginResponseDto.getUserDetailDto().getFpsStore().getId();
        }
        catch(Exception e) {}
        return fpsId;
    }

    private void navigationToAdmin() {
        if (loginResponse.getUserDetailDto().getProfile().equalsIgnoreCase("ADMIN")) {
            Util.LoggingQueue(this, "Logged in", "Admin login");
            insertLoginHistoryDetails();
            dismissProgress();
            startActivity(new Intent(this, AdminActivity.class));
            finish();
        } else {
            moveToSales();
        }
    }

    private void insertRoleFeatures(Set<AppfeatureDto> roleFeatures) {
        FPSDBHelper.getInstance(this).updateRoles(loginResponse.getUserDetailDto().getId());
        FPSDBHelper.getInstance(this).insertRoles(loginResponse.getUserDetailDto().getId(), roleFeatures);
    }

    private void insertLoginHistoryDetails() {
        LoginHistoryDto loginHistoryDto = new LoginHistoryDto();
        if (loginResponse.getUserDetailDto().getFpsStore() != null)
            loginHistoryDto.setFpsId(loginResponse.getUserDetailDto().getFpsStore().getId());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        loginHistoryDto.setLoginTime(df.format(new Date()));
        loginHistoryDto.setLoginType("ONLINE_LOGIN");
        loginHistoryDto.setUserId(loginResponse.getUserDetailDto().getId());
        df = new SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault());
        String transactionId = df.format(new Date());
        loginHistoryDto.setTransactionId(transactionId);
        SessionId.getInstance().setTransactionId(transactionId);
        FPSDBHelper.getInstance(this).insertLoginHistory(loginHistoryDto);
    }

    private void moveToSales() {
        try {

            if (!checkLocationDetails()) {
                return;
            }
            insertLoginHistoryDetails();
            dismissProgress();
            Util.LoggingQueue(this, "Logged in", "User login.All services started");

            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!enabled) {
                gpsAlertDialog = new GpsAlertDialog(this);
                gpsAlertDialog.show();
            } else {

                try {
                    if (!loginResponse.getUserDetailDto().getActive()) {
                        dismissProgress();
                        Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.userInactive));
                        return;
                    }
                } catch (Exception e) {
                    dismissProgress();
                    Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.userInactive));
                    return;
                }

                try {
                    if (!loginResponse.getUserDetailDto().getProfile().equalsIgnoreCase("ADMIN")) {
                        if (!loginResponse.getUserDetailDto().getFpsStore().isActive()) {
                            dismissProgress();
                            Log.e(TAG, "c............");
                            Util.messageBar(com.omneagate.activity.LoginActivity.this, getString(R.string.storeInactive));
                            return;
                        }
                    }
                } catch (Exception e) {
                }
                SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
                Log.e("moveToSales ", "Check  device status ");
                try {
                    if (loginResponse.getDeviceStatus() == DeviceStatus.UNASSOCIATED) {
                        Util.LoggingQueue(this, "Device Association", "Device Not associated");
                        dismissProgress();
                        Util.messageBar(this, getString(R.string.unassociated));
                        return;
                    } else if (loginResponse.getDeviceStatus() == DeviceStatus.INACTIVE) {
                        dismissProgress();
                        if (loginResponse.getStatusCode() == 5095) {
                            Util.messageBar(this, Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(5095)));
                        } else if (loginResponse.getStatusCode() == 1000) {
                            Util.messageBar(this, getString(R.string.inCorrectUnamePword));
                        } else if (loginResponse.getStatusCode() == 1002) {
                            Util.messageBar(this, getString(R.string.inCorrectUnamePword));
                        } else {
                            Util.LoggingQueue(this, "Device Inactive", "Device is Inactive");
                            Util.messageBar(this, getString(R.string.deviceInvalid));
                        }
                        return;
                    }
                } catch (Exception e) {
                    Util.LoggingQueue(this, "Check  device status", "Ex = " + e);
                }

                startActivity(new Intent(this, SaleActivity.class));
                finish();
            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Logged in error", e.toString());
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Used to stop all services running currently
     */
    private void removeAllServices() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("LoginActivity", "on destroy called");
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
            if ((bluetoothDialog != null) && bluetoothDialog.isShowing()) {
                bluetoothDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            bluetoothDialog = null;
        }
        try {
            if ((changeUrlDialog != null) && changeUrlDialog.isShowing()) {
                changeUrlDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            changeUrlDialog = null;
        }
        try {
            if ((dateChangeDialog != null) && dateChangeDialog.isShowing()) {
                dateChangeDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            dateChangeDialog = null;
        }
        try {
            if ((deviceIdDialog != null) && deviceIdDialog.isShowing()) {
                deviceIdDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            deviceIdDialog = null;
        }
        try {
            if ((gpsAlertDialog != null) && gpsAlertDialog.isShowing()) {
                gpsAlertDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            gpsAlertDialog = null;
        }
        try {
            if ((languageSelectionDialog != null) && languageSelectionDialog.isShowing()) {
                languageSelectionDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            languageSelectionDialog = null;
        }
        try {
            if ((purgeBillBItemDialog != null) && purgeBillBItemDialog.isShowing()) {
                purgeBillBItemDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            purgeBillBItemDialog = null;
        }
    }
}