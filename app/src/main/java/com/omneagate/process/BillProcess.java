package com.omneagate.process;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.DTO.UpdateStockResponseDto;
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
import java.util.ArrayList;
import java.util.List;


public class BillProcess implements BaseSchedulerService, Serializable {

    Context globalContext;
    BufferedReader in = null;
    List<BillDto> billList;
    String TAG = "BillProcess";
    String serverUrl = "";

    public void process(Context context) {
        globalContext = context;
        SharedPreferences mySharedPreferences = context.getSharedPreferences("FPS_POS", context.MODE_PRIVATE);
        serverUrl = mySharedPreferences.getString("server_url", "");
        /*if(serverUrl == null) {
            serverUrl = FPSDBHelper.getInstance(globalContext).getMasterData("serverUrl");
        }*/
        // Check whether sessionId is empty or null
        if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
            startProcess();
        }
        else {
            getSessionAndRetry();
        }
    }

    private void startProcess() {
        boolean sessionInvalid = BillTask();
        if(sessionInvalid) {
            String sessionId = getSessionFromServer();
            SessionId.getInstance().setSessionId(sessionId);
            if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
                BillTask();
            }
        }
    }

    public boolean BillTask() {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
            billList = new ArrayList<>();
            billList = FPSDBHelper.getInstance(globalContext).getAllBillsForSync();
            NetworkConnection network = new NetworkConnection(globalContext);
            if (network.isNetworkAvailable()) {
                for (BillDto bill : billList) {
                    Log.e(TAG, "Bills from back "+bill.toString());
                    TransactionBaseDto base = new TransactionBaseDto();
                    base.setTransactionType(TransactionTypes.SALE_QR_OTP_DISABLED);
                    UpdateStockRequestDto updateStock = new UpdateStockRequestDto();
                    updateStock.setReferenceId(bill.getTransactionId());
                    updateStock.setDeviceId(Settings.Secure.getString(globalContext.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                    updateStock.setUfc(bill.getUfc());
                    updateStock.setBillDto(bill);
                    base.setBaseDto(updateStock);
                    base.setType("com.omneagate.rest.dto.QRRequestDto");
                    String url = serverUrl + "/transaction/process";
                    URI website = new URI(url);
                    Log.i(TAG, "Bills offline sync..." + base.toString());
                    String billData = new Gson().toJson(base);
                    Log.e(TAG, "bill json......"+billData);
                    // inserting request into local db
                    primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(billData, "BillService");
                    StringEntity entity = new StringEntity(billData, HTTP.UTF_8);
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
                            UpdateStockResponseDto updateStock1 = gson.fromJson(responseData, UpdateStockResponseDto.class);
                            String messageData = "";
                            String status = "";
                            Log.e(TAG, "response:" + updateStock1.toString());
                            if (updateStock1 != null && updateStock1.getStatusCode() == 0 && updateStock1.getBillDto() != null) {
                                status = "Success";
                                FPSDBHelper.getInstance(globalContext).billUpdate(updateStock1.getBillDto());
                                Util.LoggingQueue(globalContext, TAG, "Updating status of bill to status T for bill id " + updateStock1.getBillDto().getTransactionId());
                            } else if (updateStock1 != null && updateStock1.getStatusCode() == 5085) {
                                status = "Success";
                                FPSDBHelper.getInstance(globalContext).billUpdate(updateStock1.getBillDto());
                                Util.LoggingQueue(globalContext, TAG, "Updating status of bill to status T for bill id " + updateStock1.getBillDto().getTransactionId());
                            } else {
                                Util.LoggingQueue(globalContext, TAG, "Received null response ");
                                status = "Failure";
                                try {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(updateStock1.getStatusCode()));
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
            }
            else {
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "BillService");
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
                    Log.e(TAG, "loginDetails..."+loginDetails);
                    // inserting request into local db
                    sessionPrimaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(loginDetails, "Session_request_BillService");
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
                    Log.e(TAG, "loginDetails resp..."+e.toString());
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
        long id = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "Session_request_BillService");
        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "LoginCredentials unavailable in local db", "Failure", id);
    }

    /*return http POST method using parameters*/
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {
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
