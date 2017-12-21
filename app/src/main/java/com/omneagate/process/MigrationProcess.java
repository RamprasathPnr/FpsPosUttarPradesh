package com.omneagate.process;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BenefMigrationDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.UserDto.MigrationOutDTO;
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
import java.util.List;


public class MigrationProcess implements BaseSchedulerService, Serializable {

    Context context;
    List<MigrationOutDTO> migrationOut;
    BufferedReader in = null;
    String TAG = "MigrationProcess";
    String serverUrl = "";

    // Migration out data will be processed first
    public void process(Context cont) {
        context = cont;
        SharedPreferences mySharedPreferences = context.getSharedPreferences("FPS_POS", context.MODE_PRIVATE);
        serverUrl = mySharedPreferences.getString("server_url", "");
        /*if(serverUrl == null) {
            serverUrl = FPSDBHelper.getInstance(context).getMasterData("serverUrl");
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
        migrationOut = FPSDBHelper.getInstance(context).getMigrationOut();
        if (migrationOut.size() > 0) {
            boolean sessionInvalid = MigrationOutTask(migrationOut.get(0));
            if(sessionInvalid) {
                String sessionId = getSessionFromServer();
                SessionId.getInstance().setSessionId(sessionId);
                if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
                    MigrationOutTask(migrationOut.get(0));
                }
            }
        } else {
            migrationOutValues();
        }
    }

    private void migrationOutValues() {
        Log.e("Migration Out",migrationOut.toString());
        if (migrationOut.size() > 0) {
            Log.e("Migration Out","Inside Migration out");
            MigrationOutTask(migrationOut.get(0));
        } else {
            migrationOut = FPSDBHelper.getInstance(context).getMigrationIn();
            Log.e("Migration In",migrationOut.toString());
            migrationInData();
        }
    }

    private void migrationInData() {
        Log.e("Migration In Ins",migrationOut.toString());
        if (migrationOut.size() > 0) {
            MigrationInTask(migrationOut.get(0));
        }
    }

    private boolean MigrationOutTask(MigrationOutDTO migrationOutDTO) {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
            NetworkConnection network = new NetworkConnection(context);
            if (network.isNetworkAvailable()) {
                MigrationOutDTO migrate = migrationOutDTO;
                FPSDBHelper.getInstance(context).updateBeneficiary(migrate);
                String url = serverUrl + "/benefmigration/deactivate";
                Log.e(TAG, "migration deactivate url..." + url);
                URI website = new URI(url);
                BenefMigrationDto migrationDto = new BenefMigrationDto();
                migrationDto.setBenefMigrationReqId(migrate.getId());
                String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                migrationDto.setDeviceId(deviceId);
                migrationDto.setBenificiaryId(migrate.getBeneficiaryId());
                String benefDeactivateData = new Gson().toJson(migrationDto);
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(context).insertBackgroundProcessHistory(benefDeactivateData, "MigrationOutService");
                StringEntity entity = new StringEntity(benefDeactivateData, HTTP.UTF_8);
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
                        BenefMigrationDto fpsDataDto = gson.fromJson(responseData, BenefMigrationDto.class);
                        String messageData = "";
                        String status = "";
                        if (fpsDataDto.getStatusCode() == 0 || fpsDataDto.getStatusCode() == 6001 || fpsDataDto.getStatusCode() == 6003) {
                            status = "Success";
                            FPSDBHelper.getInstance(context).updateMigrationOut(migrationOut.get(0));
                        }
                        else {
                            status = "Failure";
                            try {
                                messageData = Util.messageSelection(FPSDBHelper.getInstance(context).retrieveLanguageTable(fpsDataDto.getStatusCode()));
                                if (messageData == null) {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(context).retrieveLanguageTable(4000));
                                }
                            } catch (Exception e) {
                                messageData = Util.messageSelection(FPSDBHelper.getInstance(context).retrieveLanguageTable(4000));
                            }
                        }

                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(context).updateBackgroundProcessHistory(responseData, messageData, status, primaryId);
                        }
                    }
                }
                else if(response != null && response.getStatusLine() != null && ((response.getStatusLine().getStatusCode() == 403) || (response.getStatusLine().getStatusCode() == 401))) {
                    unauthorized = true;
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", "Unauthorized", "Failure", primaryId);
                    }
                }
                else if(response != null && response.getStatusLine() != null) {
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(context).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", primaryId);
                    }
                }
                else if(response == null) {
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", "Response is null", "Failure", primaryId);
                    }
                }
            }
            else {
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(context).insertBackgroundProcessHistory("", "MigrationOutService");
                // update response into local db
                if (primaryId != -1) {
                    FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", "Network unavailable", "Failure", primaryId);
                }
            }
        } catch (Exception e) {
            // update response into local db
            if (primaryId != -1) {
                FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", e.toString(), "Failure", primaryId);
            }
            try {
                if (in != null)
                    in.close();
            } catch (Exception e1) {}
        }
        finally {
            migrationOut.remove(0);
            migrationOutValues();
        }
        return unauthorized;
    }

    private boolean MigrationInTask(MigrationOutDTO migrationOutDTO) {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
            NetworkConnection network = new NetworkConnection(context);
            if (network.isNetworkAvailable()) {
                MigrationOutDTO migrate = migrationOutDTO;
                BeneficiaryDto beneficiaryDto = FPSDBHelper.getInstance(context).retrieveBeneficiaryIn(migrate.getBeneficiaryId());
                FPSDBHelper.getInstance(context).insertBeneficiaryNew(beneficiaryDto);
                String url = serverUrl + "/benefmigration/activate";
                URI website = new URI(url);
                BenefMigrationDto migrationDto = new BenefMigrationDto();
                migrationDto.setBenefMigrationReqId(migrate.getId());
                String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
                migrationDto.setDeviceId(deviceId);
                migrationDto.setBenificiaryId(migrate.getBeneficiaryId());
                String benefActivateData = new Gson().toJson(migrationDto);
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(context).insertBackgroundProcessHistory(benefActivateData, "MigrationInService");
                StringEntity entity = new StringEntity(benefActivateData, HTTP.UTF_8);
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
                        BenefMigrationDto fpsDataDto = gson.fromJson(responseData, BenefMigrationDto.class);
                        String messageData = "";
                        String status = "";
                        if (fpsDataDto.getStatusCode() == 0 || fpsDataDto.getStatusCode() == 6001 || fpsDataDto.getStatusCode() == 5036) {
                            status = "Success";
                            Log.e(TAG, "migration activate final result..." + responseData);
                            FPSDBHelper.getInstance(context).updateMigrationIn(migrationOut.get(0));
                        }
                        else {
                            status = "Failure";
                            try {
                                messageData = Util.messageSelection(FPSDBHelper.getInstance(context).retrieveLanguageTable(fpsDataDto.getStatusCode()));
                                if (messageData == null) {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(context).retrieveLanguageTable(4000));
                                }
                            } catch (Exception e) {
                                messageData = Util.messageSelection(FPSDBHelper.getInstance(context).retrieveLanguageTable(4000));
                            }
                        }

                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(context).updateBackgroundProcessHistory(responseData, messageData, status, primaryId);
                        }
                    }
                }
                else if(response != null && response.getStatusLine() != null && ((response.getStatusLine().getStatusCode() == 403) || (response.getStatusLine().getStatusCode() == 401))) {
                    unauthorized = true;
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", "Unauthorized", "Failure", primaryId);
                    }
                }
                else if(response != null && response.getStatusLine() != null) {
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(context).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", primaryId);
                    }
                }
                else if(response == null) {
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", "Response is null", "Failure", primaryId);
                    }
                }
            }
            else {
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(context).insertBackgroundProcessHistory("", "MigrationInService");
                // update response into local db
                if (primaryId != -1) {
                    FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", "Network unavailable", "Failure", primaryId);
                }
            }
        } catch (Exception e) {
            // update response into local db
            if (primaryId != -1) {
                FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", e.toString(), "Failure", primaryId);
            }
            try {
                if (in != null)
                    in.close();
            } catch (Exception e1) {}
        }
        finally {
            migrationOut.remove(0);
            migrationInData();
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
        NetworkConnection network = new NetworkConnection(context);
        if (network.isNetworkAvailable()) {
            try {
                String url = serverUrl + "/login/user/internal/authenticate";
                URI website = new URI(url);
                LoginDto loginDto = setLoginDetails();
                if(loginDto != null) {
                    String loginDetails = new Gson().toJson(loginDto);
                    // inserting request into local db
                    sessionPrimaryId = FPSDBHelper.getInstance(context).insertBackgroundProcessHistory(loginDetails, "Session_request_MigrationService");
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
                            FPSDBHelper.getInstance(context).updateBackgroundProcessHistory(responseData, "", "Success", sessionPrimaryId);
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
                            FPSDBHelper.getInstance(context).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", sessionPrimaryId);
                        }
                    }
                    else if(response == null) {
                        // update response into local db
                        if (sessionPrimaryId != -1) {
                            FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", "Response is null", "Failure", sessionPrimaryId);
                        }
                    }
                }
            } catch (Exception e) {
                // update response into local db
                if (sessionPrimaryId != -1) {
                    FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", e.toString(), "Failure", sessionPrimaryId);
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
            LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(context).getFpsUserDetails();
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
        long id = FPSDBHelper.getInstance(context).insertBackgroundProcessHistory("", "Session_request_MigrationService");
        FPSDBHelper.getInstance(context).updateBackgroundProcessHistory("", "LoginCredentials unavailable in local db", "Failure", id);
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
