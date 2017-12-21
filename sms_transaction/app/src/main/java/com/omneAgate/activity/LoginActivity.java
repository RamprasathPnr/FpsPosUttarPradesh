package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.DeviceRegistrationDto;
import com.omneagate.DTO.DeviceRegistrationResponseDto;
import com.omneagate.DTO.EnumDTO.DeviceStatus;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.UserDetailDto;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.PurgeBill;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.ChangeUrlDialog;
import com.omneagate.activity.dialog.DeviceIdDialog;
import com.omneagate.activity.dialog.LanguageSelectionDialog;
import com.omneagate.activity.dialog.PurgeBillBItemDialog;
import com.omneagate.printer.BluetoothPairing;
import com.omneagate.service.ConnectionHeartBeat;
import com.omneagate.service.HttpClientWrapper;
import com.omneagate.service.LocalDBLogin;
import com.omneagate.service.OfflineInwardManager;
import com.omneagate.service.OfflineTransactionManager;
import com.omneagate.service.RemoteLoggingService;
import com.omneagate.service.UpdateDataService;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.List;

/**
 * Login activity  is used to login and also used to validate user
 */
public class LoginActivity extends BaseActivity {

    //User textBox for entering username and password
    private EditText userNameTextBox, passwordTextBox;

    PurgeBill purgeBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //size = ScreenSize.valueOf(getResources().getString(R.string.screensize));

        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        userNameTextBox = (EditText) findViewById(R.id.login_username);
        passwordTextBox = (EditText) findViewById(R.id.login_password);
        appState = (GlobalAppState) getApplication();
        httpConnection = new HttpClientWrapper();
        List<FpsStoreDto> fpsStore = FPSDBHelper.getInstance(this).retrieveDataStore();
        purgeBill = new PurgeBill();

    }

    //onclick event for login button
    public void userLogin(View view) {
        try {
            /*Keyboard disappearance*/
            InputMethodManager im = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);

            LoginDto loginCredentials = getUsernamePassword();
//            Login credentials null check
            if (loginCredentials == null) {
                return;
            }
           /* SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
            if (prefs.getBoolean("register", false)) {*/
            authenticateUser(loginCredentials);

        } catch (Exception e) {
            Log.e("LoginActivity_userLogin", e.toString(), e);
        }
    }


    /**
     * sending login details to server if network connection available
     *
     * @params loginDto
     */
    private void authenticateUser(LoginDto loginCredentials) {

        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/login/validateuser";
                loginCredentials.setDeviceId(Settings.Secure.getString(
                        getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                String login = new Gson().toJson(loginCredentials);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGIN_USER,
                        SyncHandler, RequestType.POST, se, this);
            } else {
                SharedPreferences pref = getSharedPreferences("FPS", MODE_PRIVATE);
                int days = pref.getInt("purgeBill", 30);
                purgeBill.purgeBillProcess(this, days);
                localLogin();
            }
        } catch (Exception e) {
            if (progressBar != null) {
                progressBar.show();
            }
            Log.e("LoginActivity_userLogin", e.toString(), e);
        }
    }

    /**
     * IF NO NETWORK AVAILABLE LOGIN WILL DONE USING LOCAL DATABASE
     */
    private void localLogin() {


        SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
        if (!prefs.getBoolean("approved", false)) {
            Util.messageBar(this, getString(R.string.noNetworkConnection));
            return;
        }
        progressBar = new CustomProgressDialog(this);
        progressBar.show();
        LoginResponseDto hashDbPassword = FPSDBHelper.getInstance(this).retrieveData(userNameTextBox.getText().toString());
        if (hashDbPassword == null) {
            Util.messageBar(this, getString(R.string.inCorrectUnamePword));
            if (progressBar != null) {
                progressBar.dismiss();
            }
            return;

        }
        String passwordUser = passwordTextBox.getText().toString();
        String hashPassword = hashDbPassword.getUserDetailDto().getPassword();
        if (StringUtils.isNotEmpty(hashPassword)) {
            if (hashDbPassword.getUserDetailDto().getFpsStore().isActive()) {
                appState.setFpsId(hashDbPassword.getUserDetailDto().getFpsStore().getId());
                LocalDBLogin localDBLogin = new LocalDBLogin(this, progressBar);
                localDBLogin.setLoginProcess(passwordUser, hashPassword);
            } else {
                Util.messageBar(this, "Store Inactive");
            }
        } else {
            Util.messageBar(this, getString(R.string.inCorrectUnamePword));

        }
    }

    /**
     * sending registration details to server if network connection available
     * else no network connection error message will appear
     *
     * @params loginDto
     */


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
                httpConnection.sendRequest(url, null, ServiceListenerType.DEVICE_REGISTER,
                        SyncHandler, RequestType.POST, se, this);
            } else {
                Util.messageBar(this, getString(R.string.connectionError));
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
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
        Util.setTamilText((TextView) findViewById(R.id.userNameLabel), R.string.userName);
        Util.setTamilText((TextView) findViewById(R.id.passwordLabel), R.string.loginPassword);
        Util.setTamilText((Button) findViewById(R.id.login_loginButton), R.string.loginButton);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    /*Concrete method*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case LOGIN_USER:
                userLoginResponse(message);
                break;
            case DEVICE_REGISTER:
                userRegisterResponse(message);
                break;
            default:
                if (progressBar != null)
                    progressBar.dismiss();
                SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
                if (!prefs.getBoolean("approved", false)) {
                    Util.messageBar(LoginActivity.this, getString(R.string.connectionRefused));
                } else {
                    localLogin();
                }
                break;
        }

    }

    /**
     * After login response received from server successfully in android
     *
     * @params bundle of message that received
     */
    private void userLoginResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            LoginResponseDto loginResponse = gson.fromJson(response, LoginResponseDto.class);
            if (loginResponse != null) {
                if (loginResponse.isAuthenticationStatus()) {
                    authenticationSuccess(loginResponse);
                } else {
                    SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
                    if (loginResponse.getDeviceStatus() == DeviceStatus.ACTIVE && loginResponse.isAuthenticationStatus()) {
                        authenticationSuccess(loginResponse);
                    } else if (loginResponse.getDeviceStatus() == DeviceStatus.UNASSOCIATED) {
                        if (!prefs.getBoolean("approved", false)) {
                            checkForRegistration(loginResponse.getUserDetailDto());
                        } else {
                            if (progressBar != null)
                                progressBar.dismiss();
                            Util.messageBar(this, getString(R.string.unassociated));
                        }
                    } else if (loginResponse.getDeviceStatus() == DeviceStatus.INACTIVE) {
                        if (progressBar != null)
                            progressBar.dismiss();
                        Util.messageBar(this, getString(R.string.deviceInvalid));
                    } else {
                        if (progressBar != null)
                            progressBar.dismiss();
                        Util.messageBar(this, getString(R.string.inCorrectUnamePword));
                    }

                }
            } else {
                if (progressBar != null)
                    progressBar.dismiss();
                Util.messageBar(this, "Service Error.Please Try again");
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    private void authenticationSuccess(LoginResponseDto loginResponse) {
        SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
        SessionId.getInstance().setSessionId(loginResponse.getSessionid());
        loginResponse.getUserDetailDto().setPassword(loginResponse.getUserDetailDto().getPassword());
        FPSDBHelper.getInstance(this).insertData(loginResponse);
        FPSDBHelper.getInstance(this).purgeStockHistoryDetails();
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("keyEncrypt", loginResponse.getKey());
        edit.putString("providerNum", loginResponse.getProviderNum());
        edit.putString("prefixKey", loginResponse.getPrefixKey());
        edit.commit();
        appState.setFpsId(loginResponse.getUserDetailDto().getFpsStore().getId());
        if (!prefs.getBoolean("approved", false)) {
            checkForRegistration(loginResponse.getUserDetailDto());
        } else {
            if (progressBar != null)
                progressBar.dismiss();
            loginSuccess();
        }
    }


    private void checkForRegistration(UserDetailDto userDetails) {
        if (StringUtils.equalsIgnoreCase(userDetails.getProfile(), "ADMIN")) {
            LoginDto loginCredentials = getUsernamePassword();
            if (loginCredentials == null) {
                return;
            }
            SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
            if (!prefs.getBoolean("register", false)) {
                registerDevice(loginCredentials);
            } else {
                if (progressBar != null)
                    progressBar.dismiss();
                startActivity(new Intent(this, RegistrationActivity.class));
                finish();
            }
        } else {
            if (progressBar != null)
                progressBar.dismiss();
            Util.messageBar(this, getString(R.string.inCorrectUnamePword));
        }

    }

    /**
     * After registration response received from server successfully in android
     *
     * @params bundle of message that received
     */
    private void userRegisterResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            DeviceRegistrationResponseDto deviceRegistrationResponse = gson.fromJson(response,
                    DeviceRegistrationResponseDto.class);
            if (progressBar != null)
                progressBar.dismiss();
            if (deviceRegistrationResponse.getStatusCode() == 0) {
                Util.storePreferenceRegister(this);
                startActivity(new Intent(this, RegistrationActivity.class));
                finish();
            } else if (deviceRegistrationResponse.getStatusCode() == 121) {
                Util.storePreferenceApproved(this);
                loginSuccess();
            } else {
                Util.messageBar(this, getString(R.string.inCorrectUnamePword));
                // FPSDBHelper.getInstance(this).retrieveLanguageTable(deviceRegistrationResponse.getStatusCode(), GlobalAppState.language).getDescription());
            }

        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    /**
     * After login success the user navigation to Sale activity page
     * And also start the connection heartBeat service
     */
    private void loginSuccess() {
        SharedPreferences pref = getSharedPreferences("FPS", MODE_PRIVATE);
        int days = pref.getInt("purgeBill", 30);
        new PurgeBill().purgeBillProcess(this, days);
        startService(new Intent(this, ConnectionHeartBeat.class));
        startService(new Intent(this, UpdateDataService.class));
        startService(new Intent(this, RemoteLoggingService.class));
        startService(new Intent(this, OfflineTransactionManager.class));
        startService(new Intent(this, OfflineInwardManager.class));
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }


    @Override
    public void onBackPressed() {

    }

    /**
     * Menu creation
     * Used to change language
     * *
     */
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.languagemenu, menu);//Menu Resource, Menu
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.languagemenu, menu);
        getLayoutInflater().setFactory(new LayoutInflater.Factory() {

            @Override
            public View onCreateView(String name, Context context,
                                     AttributeSet  attrs) {
                if (name.equalsIgnoreCase("TextView")) {
                    try {
                        LayoutInflater f = getLayoutInflater();
                        final View view = f.createView(name, null, attrs);
                        new Handler().post(new Runnable() {
                            public void run() {
                                TextView text = (TextView)view.findViewById(view.getId());
                                Util.setTamilText(text,text.getText().toString());
                            }
                        });
                        return view;
                    } catch (InflateException e) {
                    } catch (ClassNotFoundException e) {
                    }
                }
                return null;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.language:
                new LanguageSelectionDialog(this).show();
                return true;
            case R.id.publicUrl:
                new ChangeUrlDialog(this).show();
                return true;
            case R.id.deviceId:
                new DeviceIdDialog(this).show();
                return true;
            case R.id.purgeBill:
                new PurgeBillBItemDialog(this).show();
                return true;
            case R.id.bluetoothPair:
                BluetoothPairing pair = new BluetoothPairing(this);
                pair.bluetoothPair();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.changeLanguage(this, GlobalAppState.language);
        super.onSaveInstanceState(outState);
    }
}