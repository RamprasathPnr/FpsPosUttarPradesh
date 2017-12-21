package com.omneagate.process;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.StatisticsDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LocationId;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StatisticsProcess implements BaseSchedulerService, Serializable {

    StatisticsDto statisticsDto;
    int scale, health, level, plugged, status, temperature, voltage;
    String technology;
    boolean present;
    private int batteryLevel = 0;
    Context globalContext;
    static boolean register = false;
    BufferedReader in = null;
    String serverUrl = "";

    @Override
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
        registerBatteryReceiver();
        initializeValues();
        boolean sessionInvalid = StatisticsTask();
        if(sessionInvalid) {
            String sessionId = getSessionFromServer();
            SessionId.getInstance().setSessionId(sessionId);
            if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
                StatisticsTask();
            }
        }
        unregisterBatteryReceiver();
    }

    private void initializeValues() {
        statisticsDto = new StatisticsDto();
        try {
            PackageInfo pInfo = globalContext.getPackageManager().getPackageInfo(globalContext.getPackageName(), 0);
            statisticsDto.setVersionNum(pInfo.versionCode);
            statisticsDto.setApkInstalledTime(pInfo.firstInstallTime);
            statisticsDto.setLastUpdatedTime(pInfo.lastUpdateTime);
            statisticsDto.setVersionName(pInfo.versionName);
            ConnectivityManager cm = (ConnectivityManager) globalContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            statisticsDto.setNetworkInfo(cm.getActiveNetworkInfo().getTypeName());
            long totalFreeMemory = getAvailableInternalMemorySize() + getAvailableExternalMemorySize();
            statisticsDto.setHardDiskSizeFree(formatSize(totalFreeMemory));
            statisticsDto.setUserId(String.valueOf(SessionId.getInstance().getUserId()));
            TelephonyManager telephonyManager = (TelephonyManager) globalContext.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                statisticsDto.setSimId(telephonyManager.getSimSerialNumber());
            } catch (Exception e) {}
        } catch (Exception e) {}
    }

    private boolean StatisticsTask() {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
                NetworkConnection network = new NetworkConnection(globalContext);
                if (network.isNetworkAvailable()) {
                    statisticsDto.setBatteryLevel(batteryLevel);
                    statisticsDto.setLatitude(LocationId.getInstance().getLatitude());
                    statisticsDto.setLongtitude(LocationId.getInstance().getLongitude());
                    statisticsDto = getAllStatisticsData();
                        String url = serverUrl + "/pos/addstatistics";
                        URI website = new URI(url);
                        String statsData = new Gson().toJson(statisticsDto);
                        Log.e("StatisticsProcess",""+ statsData);
                        // inserting request into local db
                        primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(statsData, "StatisticsService");
                        StringEntity entity = new StringEntity(statsData, HTTP.UTF_8);
                        HttpResponse response = requestType(website, entity);
//                        String responseData = getresponseData(response);
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
                                StatisticsDto statisticsDto = gson.fromJson(responseData, StatisticsDto.class);
                                String messageData = "";
                                String status = "";
                                if (statisticsDto.getStatusCode() == 0) {
                                    status = "Success";
                                } else {
                                    status = "Failure";
                                    try {
                                        messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(statisticsDto.getStatusCode()));
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
                        else if(response != null && response.getStatusLine() != null) {
                            // update response into local db
                            if (primaryId != -1) {
                                FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", primaryId);
                            }
                        }
                        else if(response == null) {
                            // update response into local db
                            if (primaryId != -1) {
                                FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "Response is null", "Failure", primaryId);
                            }
                        }
            }
            else {
                    // inserting request into local db
                    primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "StatisticsService");
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

    private boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    private long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    private long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getAvailableBlocksLong();
            long availableBlocks = stat.getBlockSizeLong();
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    private String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    private StatisticsDto getAllStatisticsData() {
        try {
            statisticsDto.setDeviceNum((Settings.Secure.getString(globalContext.getContentResolver(), Settings.Secure.ANDROID_ID)).toUpperCase());
            statisticsDto.setScale(scale);
            statisticsDto.setHealth(health);
            statisticsDto.setLevel(level);
            statisticsDto.setPlugged(plugged);
            statisticsDto.setStatus(status);
            statisticsDto.setTemperature(temperature);
            statisticsDto.setVoltage(voltage);
            statisticsDto.setTechnology(technology);
            statisticsDto.setPresent(present);
            statisticsDto.setBeneficiaryCount(FPSDBHelper.getInstance(globalContext).getBeneficiaryCount());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            statisticsDto.setTotalUnSyncBillCountToday(FPSDBHelper.getInstance(globalContext).getAllBillCount(false));
            statisticsDto.setTotalBillCountToday(FPSDBHelper.getInstance(globalContext).getAllBillCount(true));
            statisticsDto.setRegistrationCount(FPSDBHelper.getInstance(globalContext).getBeneficiaryUnSyncCount());
            statisticsDto.setUnSyncBillCount(FPSDBHelper.getInstance(globalContext).getBillUnSyncCount());
            statisticsDto.setUnsyncInwardCount(FPSDBHelper.getInstance(globalContext).getInwardUnSyncCount());
            statisticsDto.setUnsyncAdjustmentCount(FPSDBHelper.getInstance(globalContext).getAdjustmentUnSyncCount());
            statisticsDto.setUnsyncMigrationIn(FPSDBHelper.getInstance(globalContext).getMigrationInUnSyncCount());
            statisticsDto.setUnsyncMigrationOut(FPSDBHelper.getInstance(globalContext).getMigrationOutUnSyncCount());
            statisticsDto.setUnsyncAdvanceStock(FPSDBHelper.getInstance(globalContext).getAdvanceStockUnSyncCount());
            statisticsDto.setCpuUtilisation(String.valueOf(readUsage()));
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) globalContext.getSystemService(globalContext.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long availableMegs = mi.availMem / 1048576L;
            long totalMegs = mi.totalMem / 1048576L;
            statisticsDto.setMemoryRemaining(String.valueOf(availableMegs));
            statisticsDto.setTotalMemory(String.valueOf(totalMegs));
            statisticsDto.setMemoryUsed(String.valueOf(totalMegs - availableMegs));
            try { statisticsDto.setEntitlementMasterRulesCount(FPSDBHelper.getInstance(globalContext).getEntitlementRulesCount()); } catch(Exception e) {}
            try { statisticsDto.setPersonBasedRulesCount(FPSDBHelper.getInstance(globalContext).getPersonRulesCount()); } catch(Exception e) {}
            try { statisticsDto.setRegionBasedRulesCount(FPSDBHelper.getInstance(globalContext).getRegionRulesCount()); } catch(Exception e) {}
            try { statisticsDto.setSpecialBasedRulesCount(FPSDBHelper.getInstance(globalContext).getSpecialRulesCount()); } catch(Exception e) {}
//            try { statisticsDto.setAadhaarSeedingCount(FPSDBHelper.getInstance(globalContext).getMembersAadhaarTableCount()); } catch(Exception e) {}
            try { statisticsDto.setBeneficiaryMemberCount(FPSDBHelper.getInstance(globalContext).getBeneficiaryMemberTableCount()); } catch(Exception e) {}
            try { statisticsDto.setNfsaPosDataCount(FPSDBHelper.getInstance(globalContext).getNfscaTableCount()); } catch(Exception e) {}
        } catch (Exception e) {}
        return statisticsDto;
    }

    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" ");
            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            try {
                Thread.sleep(360);
            } catch (Exception e) {}
            reader.seek(0);
            load = reader.readLine();
            reader.close();
            toks = load.split(" ");
            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    //Broadcast receiver for battery
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
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
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
            try {
                String url = serverUrl + "/login/user/internal/authenticate";
                URI website = new URI(url);
                LoginDto loginDto = setLoginDetails();
                if(loginDto != null) {
                    String loginDetails = new Gson().toJson(loginDto);
                    // inserting request into local db
                    sessionPrimaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(loginDetails, "Session_request_StatisticsService");
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
        long id = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "Session_request_StatisticsService");
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
