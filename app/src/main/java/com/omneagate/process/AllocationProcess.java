package com.omneagate.process;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.FpsAllotmentRequestDto;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.service.BaseSchedulerService;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AllocationProcess implements BaseSchedulerService, Serializable {

    private Context globalContext;
    private static long fpsId;
    BufferedReader in = null;
    String TAG = "AllocationProcess";
    String serverUrl = "";

    public void process(Context context) {
        Log.e(TAG,"AllocationProcess started");
        globalContext = context;
        SharedPreferences mySharedPreferences = context.getSharedPreferences("FPS_POS", context.MODE_PRIVATE);
        serverUrl = mySharedPreferences.getString("server_url", "");
        /*if (serverUrl == null)
            serverUrl = FPSDBHelper.getInstance(globalContext).getMasterData("serverUrl");*/
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
        boolean sessionInvalid = AllocationTask();
        if(sessionInvalid) {
            String sessionId = getSessionFromServer();
            SessionId.getInstance().setSessionId(sessionId);
            if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
                AllocationTask();
            }
        }
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

    public boolean AllocationTask() {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
            NetworkConnection network = new NetworkConnection(globalContext);
            if (network.isNetworkAvailable()) {
                if(fpsId != 0) {
                    String url = serverUrl + "/fps/allotment/fetch";
                    URI website = new URI(url);
                    String requestData = getRequestData();
                    Log.e(TAG, "requestData..."+requestData);
                    // inserting request into local db
                    primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(requestData, "AllocationService");
                    StringEntity entity = new StringEntity(requestData, HTTP.UTF_8);
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
                        Log.e(TAG, "responseData..." + responseData);
                        if ((responseData != null) && (!responseData.contains("<html>"))) {
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            FpsAllotmentRequestDto fpsAllotmentRequestDto = gson.fromJson(responseData, FpsAllotmentRequestDto.class);
                            String messageData = "";
                            String status = "";
                            if (fpsAllotmentRequestDto.getStatusCode() == 0) {
                                status = "Success";
                                if(fpsAllotmentRequestDto.getFpsAllocationCommodityDetailDtos() != null) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                    String syncDate = dateFormat.format(fpsAllotmentRequestDto.getLastModifiedDate());
                                    FPSDBHelper.getInstance(globalContext).updateMaserData("allocationSyncTime", syncDate);
                                    FPSDBHelper.getInstance(globalContext).insert_stock_allocation(fpsAllotmentRequestDto.getFpsAllocationCommodityDetailDtos(), "AllocationService");
                                }
                            } else {
                                status = "Failure";
                                try {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(fpsAllotmentRequestDto.getStatusCode()));
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
                    primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "AllocationService");
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "FpsId unavailable", "Failure", primaryId);
                    }
                }
            }
            else {
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "AllocationService");
                // update response into local db
                if (primaryId != -1) {
                    FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "Network unavailable", "Failure", primaryId);
                }
            }
        } catch (Exception e) {
            Log.e(TAG,"exc..."+e.toString());
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

    private String getRequestData() {
        FpsAllotmentRequestDto fpsRequest = new FpsAllotmentRequestDto();
        Calendar calendar = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM");
        fpsRequest.setMonth(formatter.format(calendar.getTime()).toUpperCase());
        fpsRequest.setYear(calendar.get(Calendar.YEAR));
        fpsRequest.setFinalizedStatus(true);
        fpsRequest.setFpsId(fpsId);
        try {
            String lastModifiedDate = FPSDBHelper.getInstance(globalContext).getMasterData("allocationSyncTime");
            fpsRequest.setLastSyncTime(lastModifiedDate);
        }
        catch(Exception e) {}
        String reqData = new Gson().toJson(fpsRequest);
        return reqData;
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
                Log.e(TAG, "url..." + url);
                URI website = new URI(url);
                LoginDto loginDto = setLoginDetails();
                if(loginDto != null) {
                    String loginDetails = new Gson().toJson(loginDto);
                    Log.e(TAG, "sessionRequestData..."+loginDetails);
                    // inserting request into local db
                    sessionPrimaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(loginDetails, "Session_request_AllocationService");
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
                        Log.e(TAG, "sessionResponseData..." + responseData);
                        // update response into local db
                        if (sessionPrimaryId != -1) {
                            FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(responseData, "", "Success", sessionPrimaryId);
                        }
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        LoginResponseDto loginResponse = gson.fromJson(responseData, LoginResponseDto.class);
                        sessionId = loginResponse.getSessionid();
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
                Log.e(TAG, "Exception..."+e.toString());
                // update response into local db
                if (sessionPrimaryId != -1) {
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
        long id = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "Session_request_AllocationService");
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