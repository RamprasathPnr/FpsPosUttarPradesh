package com.omneagate.process;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.BatteryManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.HeartBeatDto;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.GPSService;
import com.omneagate.Util.LocationId;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.service.BaseSchedulerService;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;


public class HeartBeatProcess implements BaseSchedulerService, Serializable {

    HeartBeatDto heartBeat;
    TransactionBaseDto base;
    GPSService mGPSService;
    private int batteryLevel = 0;
    Context globalContext;
    static boolean register = false;
    BufferedReader in = null;
    private static long fpsId;
    String serverUrl = "";

    public void process(Context context) {
        globalContext = context;
        SharedPreferences mySharedPreferences = context.getSharedPreferences("FPS_POS", context.MODE_PRIVATE);
        serverUrl = mySharedPreferences.getString("server_url", "");
        /*if(serverUrl == null) {
            serverUrl = FPSDBHelper.getInstance(globalContext).getMasterData("serverUrl");
        }*/
        if (fpsId == 0)
            fpsId = getFpsId();
        // Check whether sessionId is empty or null
        if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
            startProcess();
        }
        else {
            getSessionAndRetry();
        }
    }

    private void startProcess() {
        registerBatteryReceiver();
        initializeValues();
        getCurrentLocation();
        getPackageInformation();
        boolean sessionInvalid = HeartBeatTask();
        if(sessionInvalid) {
            String sessionId = getSessionFromServer();
            SessionId.getInstance().setSessionId(sessionId);
            if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
                HeartBeatTask();
            }
        }
        unregisterBatteryReceiver();
    }

    private void initializeValues() {
        heartBeat = new HeartBeatDto();
        base = new TransactionBaseDto();
    }

    private long getFpsId() {
        long fpsId = 0;
        try {
            LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(globalContext).getFpsUserDetails();
            fpsId = loginResponseDto.getUserDetailDto().getFpsStore().getId();
        }
        catch(Exception e) {}
        return fpsId;
    }

    private void getCurrentLocation() {
        mGPSService = new GPSService(globalContext);
        mGPSService.getLocation();
        if (mGPSService.isLocationAvailable) {
            double latitude = mGPSService.getLatitude();
            double longitude = mGPSService.getLongitude();
            LocationId.getInstance().setLatitude(Util.latLngRoundOffFormat(latitude));
            LocationId.getInstance().setLongitude(Util.latLngRoundOffFormat(longitude));
            if (heartBeat != null) {
                String lat = String.valueOf(latitude);
                String lng = String.valueOf(longitude);
                if (StringUtils.isNotEmpty(lat) && StringUtils.isNotEmpty(lng)) {
                    heartBeat.setLatitude(lat);
                    heartBeat.setLongtitude(lng);
                }
            }
        }
    }

    private void getPackageInformation() {
        try {
            PackageInfo pInfo = globalContext.getPackageManager().getPackageInfo(globalContext.getPackageName(), 0);
            heartBeat.setVersionNum(pInfo.versionCode);
        } catch (Exception e) {}
    }

    public boolean HeartBeatTask() {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
                NetworkConnection network = new NetworkConnection(globalContext);
                if (network.isNetworkAvailable()) {
                    if(fpsId != 0) {
                        heartBeat.setBatteryLevel(batteryLevel);
                        String url = serverUrl + "/transaction/process";
                        URI website = new URI(url);
//                        heartBeat.setFpsId(SessionId.getInstance().getFpsId() + "");
                        heartBeat.setFpsId(String.valueOf(fpsId));
                        base.setBaseDto(heartBeat);
                        base.setType("com.omneagate.rest.dto.HeartBeatRequestDto");
                        base.setTransactionType(TransactionTypes.HEART_BEAT);
                        String heartBeatData = new Gson().toJson(base);
                        // inserting request into local db
                        primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(heartBeatData, "HeartBeatService");
                        StringEntity entity = new StringEntity(heartBeatData, HTTP.UTF_8);
                        HttpResponse response = requestType(website, entity);
                        String responseData = null;
                        if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                            StringBuffer sb = new StringBuffer("");
                            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                            String l;
                            String nl = System.getProperty("line.separator");
                            while ((l = in.readLine()) != null) {
                                sb.append(l + nl);
                            }
                            responseData = sb.toString();
                            in.close();
                            if ((responseData != null) && (!responseData.contains("<html>"))) {
                                GsonBuilder gsonBuilder = new GsonBuilder();
                                Gson gson = gsonBuilder.create();
                                HeartBeatDto heartBeatDto = gson.fromJson(responseData, HeartBeatDto.class);
                                String messageData = "";
                                String status = "";
                                if (heartBeatDto.getStatusCode() == 0) {
                                    status = "Success";
                                } else {
                                    status = "Failure";
                                    try {
                                        messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(heartBeatDto.getStatusCode()));
                                        if (messageData == null) {
                                            messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(4000));
                                        }
                                    } catch (Exception e) {
                                        messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(4000));
                                    }
                                }
                                // update response into local db
                                if (primaryId != -1) {
                                    FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(responseData, messageData, status, primaryId);
                                }
                            }
                        }
                        else if(response != null && response.getStatusLine() != null && ((response.getStatusLine().getStatusCode() == 403) || (response.getStatusLine().getStatusCode() == 401))) {
                            unauthorized = true;
                            // update response into local db
                            if (primaryId != -1) {
                                FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "Unauthorized", "Failure", primaryId);
                            }
                        }
                        else if (response != null && response.getStatusLine() != null) {
                            // update response into local db
                            if (primaryId != -1) {
                                FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", primaryId);
                            }
                        } else if (response == null) {
                            // update response into local db
                            if (primaryId != -1) {
                                FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "Response is null", "Failure", primaryId);
                            }
                        }
                    }
                    else if(fpsId == 0){
                        // inserting request into local db
                        primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "HeartBeatService");
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "FpsId unavailable", "Failure", primaryId);
                        }
                    }
                }
                else {
                    // inserting request into local db
                    primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "HeartBeatService");
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "Network unavailable", "Failure", primaryId);
                    }
                }
            } catch (Exception e) {
                // update response into local db
                if (primaryId != -1) {
                    FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", e.toString(), "Failure", primaryId);
                }
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {}
            }
        return unauthorized;
    }

    // Broadcast receiver for battery
    private final BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // Unregistering battery receiver
            if (register) {
                try {
                    context.unregisterReceiver(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                register = false;
            }

            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (currentLevel >= 0 && scale > 0) {
                batteryLevel = (currentLevel * 100) / scale;
            }
        }
    };

    private void registerBatteryReceiver() {
        // Registering battery receiver
        try {
            if (!register) {
                IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                globalContext.registerReceiver(batteryLevelReceiver, batteryLevelFilter);
                register = true;
            }
        } catch (Exception e) {}
    }

    private void unregisterBatteryReceiver() {
        // Unregistering battery receiver
        if (register) {
            try {
                globalContext.unregisterReceiver(batteryLevelReceiver);
            } catch (Exception e) {}
            register = false;
        }
    }

    private void getSessionAndRetry() {
        String sessionId = getSessionFromServer();
        SessionId.getInstance().setSessionId(sessionId);
        if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
            startProcess();
        }
    }

    private String getSessionFromServer() {
        long sessionPrimaryId = -1;
        String sessionId = "";
        NetworkConnection network = new NetworkConnection(globalContext);
        if (network.isNetworkAvailable()) {
            BufferedReader in = null;
            try {
                String url = serverUrl + "/login/user/internal/authenticate";
                URI website = new URI(url);
                LoginDto loginDto = setLoginDetails();
                if(loginDto != null) {
                    String loginDetails = new Gson().toJson(loginDto);
                    Log.e("HearBeatProcess", "loginDetails..."+loginDetails);
                    // inserting request into local db
                    sessionPrimaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(loginDetails, "Session_request_HeartBeatService");
                    StringEntity entity = new StringEntity(loginDetails, HTTP.UTF_8);
                    HttpResponse response = requestType(website, entity);
                    String responseData = null;
                    if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                        StringBuffer sb = new StringBuffer("");
                        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        String l;
                        String nl = System.getProperty("line.separator");
                        while ((l = in.readLine()) != null) {
                            sb.append(l + nl);
                        }
                        responseData = sb.toString();
                        in.close();
                        // update response into local db
                        if (sessionPrimaryId != -1) {
                            FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(responseData, "", "Success", sessionPrimaryId);
                        }
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        LoginResponseDto loginResponse = gson.fromJson(responseData, LoginResponseDto.class);
                        sessionId = loginResponse.getSessionid();
//                        SessionId.getInstance().setFpsId(loginResponse.getUserDetailDto().getFpsStore().getId());
                    }
                    else if(response != null && response.getStatusLine() != null) {
                        // update response into local db
                        if (sessionPrimaryId != -1) {
                            FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", sessionPrimaryId);
                        }
                    }
                    else if(response == null) {
                        // update response into local db
                        if (sessionPrimaryId != -1) {
                            FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "Response is null", "Failure", sessionPrimaryId);
                        }
                    }
                }
            } catch (Exception e) {
                // update response into local db
                if (sessionPrimaryId != -1) {
                    Log.e("HearBeatProcess", "loginDetails resp..."+e.toString());
                    FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", e.toString(), "Failure", sessionPrimaryId);
                }
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {}
                sessionId = "";
            }
        }
        return  sessionId;
    }

    private LoginDto setLoginDetails() {
        LoginDto loginDto = null;
        try {
            LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(globalContext).getFpsUserDetails();
            if ((loginResponseDto != null) && (loginResponseDto.getUserDetailDto().getUserId() != null) && (loginResponseDto.getUserDetailDto().getEncryptedPassword() != null)) {
                loginDto = new LoginDto();
                String userName = loginResponseDto.getUserDetailDto().getUserId();
                String password = Util.DecryptPassword(loginResponseDto.getUserDetailDto().getEncryptedPassword());
                String deviceNo = Util.deviceSerialNo;
                loginDto.setUserName(userName);
                loginDto.setPassword(password);
                loginDto.setDeviceId(deviceNo);
            }
            else {
                insertLoginCredentialException();
            }
        }
        catch(Exception e) {
            insertLoginCredentialException();
        }
        return loginDto;
    }

    private void insertLoginCredentialException() {
        long id = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "Session_request_HeartBeatService");
        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "LoginCredentials unavailable in local db", "Failure", id);
    }

    /*return http POST method using parameters*/
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {
        Util.LoggingQueue(globalContext, "ConnectionHeartBeat", "requestType() called ");
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 15000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 15000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpPost postRequest = new HttpPost();
        postRequest.setURI(website);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setHeader("Store_type", "fps");
        postRequest.setHeader("Cookie", "JSESSIONID=" + SessionId.getInstance().getSessionId());
        postRequest.setHeader("Cookie", "SESSION=" + SessionId.getInstance().getSessionId());
        postRequest.setEntity(entity);
        return client.execute(postRequest);
    }

}
