package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FindingCriteriaDto;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginHistoryDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.MenuDataDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.ChangeUrlDialog;
import com.omneagate.activity.dialog.LanguageSelectionDialog;
import com.omneagate.activity.dialog.MenuAdapter;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InspectionLoginActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListPopupWindow popupWindow;
    private EditText userNameTextBox;
    private EditText passwordTextBox;
    LoginResponseDto loginResponse;
    LoginDto loginCredentials;
    String TAG = "InspectionLoginActivity";

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
        switch (what) {
            case LOGIN_USER:
                userLoginResponse(message);
                break;
            default:
                dismissProgress();
                Log.e(TAG, "default...");
                Toast.makeText(InspectionLoginActivity.this, getResources().getString(R.string.connectionRefused), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        findView();
        Util.findingCriteriaDto = new FindingCriteriaDto();
    }

    private void findView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        userNameTextBox = (EditText) findViewById(R.id.login_username);
        passwordTextBox = (EditText) findViewById(R.id.login_password);
        String url = FPSDBHelper.getInstance(this).getMasterData("serverUrl");
//        if (url != null && url.equals("http://192.168.1.53:9097")) {
//            userNameTextBox.setText("inspection");
//            passwordTextBox.setText("alluser");
//        }
//        try {
//            String deviceid = new AndroidDeviceProperties(this).getDeviceProperties().getSerialNumber();
//            if(deviceid.equals("8C8004C0CCD7525F")||deviceid.equals("F6C3D49ECFEAEE76")) {
//                userNameTextBox.setText("inspection");
//                passwordTextBox.setText("alluser");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void officerLogin(View view) {
        try {
            // Checking fps store status
            try {
                String userName = FPSDBHelper.getInstance(InspectionLoginActivity.this).retrieveFpsUserName();
                LoginResponseDto hashDbPassword = FPSDBHelper.getInstance(InspectionLoginActivity.this).retrieveData(userName);
                if (!hashDbPassword.getUserDetailDto().getFpsStore().isActive()) {
                    dismissProgress();
                    Toast.makeText(InspectionLoginActivity.this, R.string.storeInactive, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
            }
            loginCredentials = getUsernamePassword();
            if (loginCredentials == null) {
                return;
            }
            authenticateOfficer(loginCredentials);
            /*startActivity(new Intent(InspectionLoginActivity.this, InspectionOTPActivity.class));
            finish();*/
        } catch (Exception e) {
            Log.e(TAG, "officerLogin exc..." + e);
        }
    }

    private void authenticateOfficer(LoginDto loginCredentials) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCanceledOnTouchOutside(false);
            if (networkConnection.isNetworkAvailable()) {
                loginCredentials.setDeviceId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                String url = "/login/inspectionuser";
                Log.e(TAG, "loginCredentials...." + loginCredentials);
                String login = new Gson().toJson(loginCredentials);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.LOGIN_USER, SyncHandler, RequestType.POST, se, this);
            } else {
                Toast.makeText(InspectionLoginActivity.this, R.string.noNetworkConnection, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            dismissProgress();
            Log.e(TAG, "authenticateOfficer exc..." + e);
        }
    }

    private void userLoginResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("Report Resp", response);
            if (response.contains("Unauthorized")) {
                Toast.makeText(InspectionLoginActivity.this, "Your Session is closed", Toast.LENGTH_SHORT).show();
                /*startActivity(new Intent(this,LoginActivity.class));
                finish();*/
            } else {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                loginResponse = gson.fromJson(response, LoginResponseDto.class);
                int statusCode = loginResponse.getStatusCode();
                String messageData = "";
                if (statusCode == 0) {
                    LoginData.getInstance().setLoginData(loginResponse);
                    SessionId.getInstance().setSessionId(loginResponse.getSessionid());
                    SessionId.getInstance().setUserName(loginResponse.getUserDetailDto().getUsername());
                    insertLoginHistoryDetails();
                    FPSDBHelper.getInstance(this).insertLoginUserData(loginResponse, passwordTextBox.getText().toString().trim());
                    FPSDBHelper.getInstance(this).setLastLoginTime(loginResponse.getUserDetailDto().getId());
                    Intent intent = new Intent(InspectionLoginActivity.this, InspectionOTPActivity.class);
                    String loginDetails = new Gson().toJson(loginCredentials);
                    intent.putExtra("LoginCredentials", loginDetails);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(statusCode));
                        if (StringUtils.isEmpty(messageData))
                            messageData = getString(R.string.internalError);
                    } catch (Exception e) {
                        messageData = getString(R.string.internalError);
                    }
                    Toast.makeText(InspectionLoginActivity.this, messageData, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
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

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }

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

    public void showPopupMenu(View v) {
        List<MenuDataDto> menuDto = new ArrayList<>();
        menuDto.add(new MenuDataDto("FPS Login", R.drawable.icon_main_login, "FPS लॉग इन करें"));
        popupWindow = new ListPopupWindow(this);
        ListAdapter adapter = new MenuAdapter(this, menuDto); // The view ids to map the data to
        popupWindow.setAnchorView(v);
        popupWindow.setAdapter(adapter);
        popupWindow.setWidth(400); // note: don't use pixels, use a dimen resource
        popupWindow.setOnItemClickListener(this); // the callback for when a list item is selected
        popupWindow.show();
        Util.LoggingQueue(this, "Login Entry", "Inside Popup Window");
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTamilHeader((TextView) findViewById(R.id.login_actionbar), R.string.headerAllPageEnglish);
        setTamil(((TextView) findViewById(R.id.login_actionbarTamil)), R.string.headerAllPage);
        Util.setTamilText(((TextView) findViewById(R.id.appName)), getString(R.string.fpsposapplication));
        removeInspectionServices();
    }

    private void startLoginActivity() {
        startActivity(new Intent(InspectionLoginActivity.this, LoginActivity.class));
        finish();
    }

    private void removeInspectionServices() {
//        stopService(new Intent(this, OfflineCriteriaService.class));
//        stopService(new Intent(this, OfflineReportService.class));
//        stopService(new Intent(this, OfflineReportAckService.class));
//        stopService(new Intent(this, UpdateDataService.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popupWindow.dismiss();
        switch (position) {
            case 0:
                startLoginActivity();
                break;
            case 1:
                new LanguageSelectionDialog(this).show();
                break;
            case 2:
                new ChangeUrlDialog(this).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "on destroy called");
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }
}
