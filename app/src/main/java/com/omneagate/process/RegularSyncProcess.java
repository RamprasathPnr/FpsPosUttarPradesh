package com.omneagate.process;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.TableNames;
import com.omneagate.DTO.FirstSynchReqDto;
import com.omneagate.DTO.FirstSynchResDto;
import com.omneagate.DTO.FistSyncInputDto;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.StockAdjustAlertDialog;
import com.omneagate.activity.dialog.StockInwardAlertDialog;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegularSyncProcess implements BaseSchedulerService, Serializable {

    private Context globalContext;
    private static String deviceId, lastModifiedDate;
    StringEntity stringEntity;
    List<FistSyncInputDto> firstSync;
    String TAG = "RegularSyncProcess";
    String serverUrl = "";

    public void process(Context context) {
        Log.e(TAG,"RegularSyncProcess started");
        globalContext = context;
        SharedPreferences mySharedPreferences = context.getSharedPreferences("FPS_POS", context.MODE_PRIVATE);
        serverUrl = mySharedPreferences.getString("server_url", "");
        /*if (serverUrl == null)
            serverUrl = FPSDBHelper.getInstance(globalContext).getMasterData("serverUrl");*/
        if (deviceId == null)
            deviceId = Settings.Secure.getString(globalContext.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
//        if (lastModifiedDate == null)
            lastModifiedDate = FPSDBHelper.getInstance(globalContext).getMasterData("syncTime");
        // Check whether sessionId is empty or null
        if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
            startProcess();
        }
        else {
            getSessionAndRetry();
        }
    }

    // Send Request to the server
    public void startProcess() {
        boolean sessionInvalid = getTableNames();
        Log.e(TAG,"sessionInvalid "+sessionInvalid);
        if(sessionInvalid) {
            String sessionId = getSessionFromServer();
            SessionId.getInstance().setSessionId(sessionId);
            if((SessionId.getInstance().getSessionId() != null) && (!SessionId.getInstance().getSessionId().equalsIgnoreCase(""))) {
                getTableNames();
            }
        }
    }

    // Task for getTableNames
    private boolean getTableNames() {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
            Util.LoggingQueue(globalContext, TAG, "processor() called ");
            FirstSynchReqDto fpsRequest = new FirstSynchReqDto();
            fpsRequest.setDeviceNum(deviceId);
            fpsRequest.setLastSyncTime(lastModifiedDate);
            String requestData = new Gson().toJson(fpsRequest);
            Util.LoggingQueue(globalContext, TAG, "processor() updateData GSON Value ->" + requestData);
            // inserting request into local db
            primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(requestData, "RegularSyncService_GetTableNames");
            stringEntity = new StringEntity(requestData, HTTP.UTF_8);
            String url = serverUrl + "/transaction/getdetails";
//            Util.LoggingQueue(globalContext, TAG, "UpdateSyncTask url ->" + url);
            URI website = new URI(url);
            HttpResponse response = requestType(website, stringEntity);
            if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                StringBuffer sb = new StringBuffer("");
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                String resp = sb.toString();
                in.close();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                Log.e("Response get details", resp);
                Util.LoggingQueue(globalContext, TAG, "UpdateSyncTask response ->" + resp);
                if ((resp != null) && (!resp.contains("<html>"))) {
                    String messageData = "";
                    String status = "";
                    FirstSynchResDto fpsDataDto = gson.fromJson(resp, FirstSynchResDto.class);
                    int statusCode = fpsDataDto.getStatusCode();
                    if (statusCode == 0) {
                        status = "Success";
                        Util.LoggingQueue(globalContext, TAG, "UpdateSyncTask FirstSynchResDto Dto Value->" + fpsDataDto);
                        getTableNamesResponseData(fpsDataDto.getTableDetails());
                    }
                    else {
                        status = "Failure";
                        try {
                            messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(statusCode));
                            if (messageData == null) {
                                messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(4000));
                            }
                        } catch (Exception e) {
                            messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(4000));
                        }
                    }
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(resp, messageData, status, primaryId);
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
        catch(Exception e) {
            // update response into local db
            if (primaryId != -1) {
                FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", e.toString(), "Failure", primaryId);
            }
        }
        return unauthorized;
    }

    // After response received from server successfully in android
    private void getTableNamesResponseData(Map<String, Integer> tableDetails) {
        Util.LoggingQueue(globalContext, TAG, "processSyncResponseData() tableDetails -> " + tableDetails);
        firstSync = new ArrayList<>();
        if (tableDetails.containsKey("TABLE_USERDETAIL"))
            firstSync.add(getInputDTO("TABLE_USERDETAIL", TableNames.TABLE_USERDETAIL));
        if (tableDetails.containsKey("TABLE_CARDTYPE"))
            firstSync.add(getInputDTO("TABLE_CARDTYPE", TableNames.TABLE_CARDTYPE));
        if (tableDetails.containsKey("TABLE_PRODUCT"))
            firstSync.add(getInputDTO("TABLE_PRODUCT", TableNames.TABLE_PRODUCT));
        if (tableDetails.containsKey("TABLE_FPSSTOCK"))
            firstSync.add(getInputDTO("TABLE_FPSSTOCK", TableNames.TABLE_FPSSTOCK));
        if (tableDetails.containsKey("TABLE_BENEFICIARY"))
            firstSync.add(getInputDTO("TABLE_BENEFICIARY", TableNames.TABLE_BENEFICIARY));
        if (tableDetails.containsKey("TABLE_BENEFREGREQ"))
            firstSync.add(getInputDTO("TABLE_BENEFREGREQ", TableNames.TABLE_BENEFREGREQ));
        if (tableDetails.containsKey("TABLE_ENTITLEMENTMASTER"))
            firstSync.add(getInputDTO("TABLE_ENTITLEMENTMASTER", TableNames.TABLE_ENTITLEMENTMASTER));
        if (tableDetails.containsKey("TABLE_PERSONBASEDRULE"))
            firstSync.add(getInputDTO("TABLE_PERSONBASEDRULE", TableNames.TABLE_PERSONBASEDRULE));
        if (tableDetails.containsKey("TABLE_REGIONBASEDRULE"))
            firstSync.add(getInputDTO("TABLE_REGIONBASEDRULE", TableNames.TABLE_REGIONBASEDRULE));
        if (tableDetails.containsKey("TABLE_SPLRULES"))
            firstSync.add(getInputDTO("TABLE_SPLRULES", TableNames.TABLE_SPLRULES));
        if (tableDetails.containsKey("TABLE_PRICEOVERRIDE"))
            firstSync.add(getInputDTO("TABLE_PRICEOVERRIDE", TableNames.TABLE_PRICEOVERRIDE));
        if (tableDetails.containsKey("TABLE_PRODUCTGROUP"))
            firstSync.add(getInputDTO("TABLE_PRODUCTGROUP", TableNames.TABLE_PRODUCTGROUP));
        if (tableDetails.containsKey("TABLE_GODOWNSTKOUTWARD"))
            firstSync.add(getInputDTO("TABLE_GODOWNSTKOUTWARD", TableNames.TABLE_FPSSTOCKINWARD));
        if (tableDetails.containsKey("TABLE_STOCKADJUSTMENT"))
            firstSync.add(getInputDTO("TABLE_STOCKADJUSTMENT", TableNames.TABLE_STOCKADJUSTMENT));
        if (tableDetails.containsKey("TABLE_BILL"))
            firstSync.add(getInputDTO("TABLE_BILL", TableNames.TABLE_BILL));
        if (tableDetails.containsKey("TABLE_FPSMIGRATION"))
            firstSync.add(getInputDTO("TABLE_FPSMIGRATION", TableNames.TABLE_FPSMIGRATION));
        if (tableDetails.containsKey("TABLE_SERVICEPROVIDER"))
            firstSync.add(getInputDTO("TABLE_SERVICEPROVIDER", TableNames.TABLE_SERVICEPROVIDER));
        if (tableDetails.containsKey("TABLE_SMSPROVIDER"))
            firstSync.add(getInputDTO("TABLE_SMSPROVIDER", TableNames.TABLE_SMSPROVIDER));
        if (tableDetails.containsKey("TABLE_AADHAARSEEDING"))
            firstSync.add(getInputDTO("TABLE_AADHAARSEEDING", TableNames.TABLE_AADHAARSEEDING));
        if (tableDetails.containsKey("TABLE_DELETEDAADHAARSEEDING"))
            firstSync.add(getInputDTO("TABLE_DELETEDAADHAARSEEDING", TableNames.TABLE_DELETEDAADHAARSEEDING));
        if (tableDetails.containsKey("TABLE_BIFURCATIONHISTORY"))
            firstSync.add(getInputDTO("TABLE_BIFURCATIONHISTORY", TableNames.TABLE_BIFURCATIONHISTORY));
        if (tableDetails.containsKey("TABLE_POSOPERATINGHOURS"))
            firstSync.add(getInputDTO("TABLE_POSOPERATINGHOURS", TableNames.TABLE_POSOPERATINGHOURS));
        if (tableDetails.containsKey("TABLE_INSPECTIONCRITERIA"))
            firstSync.add(getInputDTO("TABLE_INSPECTIONCRITERIA", TableNames.TABLE_INSPECTIONCRITERIA));
        if (tableDetails.containsKey("TABLE_NFSAPOSDATA"))
            firstSync.add(getInputDTO("TABLE_NFSAPOSDATA", TableNames.TABLE_NFSAPOSDATA));
        if (firstSync.size() > 0) {
            FirstSynchReqDto fpsRequest = new FirstSynchReqDto();
            fpsRequest.setLastSyncTime(lastModifiedDate);
            fpsRequest.setDeviceNum(deviceId);
            fpsRequest.setTableName(firstSync.get(0).getTableName());
            getTableDatas(fpsRequest);
        } else {
            firstSyncSuccess();
        }
    }

    /**
     * Request for datas by giving name of table to server
     * input FirstSynchReqDto fpsRequest
     */
    private void getTableDatas(FirstSynchReqDto fpsRequest) {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
            Util.LoggingQueue(globalContext, TAG, "setTableSyncCall() FirstSynchReqDto -> " + fpsRequest);
            String requestData = new Gson().toJson(fpsRequest);
            Util.LoggingQueue(globalContext, "RegularSyncProcess Download Sync", "Req:" + requestData);
            stringEntity = new StringEntity(requestData, HTTP.UTF_8);
            // inserting request into local db
            primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(requestData, "RegularSyncService_GetTableDatas");
            String url = serverUrl + "/transaction/fpsData";
            Util.LoggingQueue(globalContext, TAG, "UpdateTablesTask url -> " + url);
            URI website = new URI(url);
            HttpResponse response = requestType(website, stringEntity);
            if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                StringBuffer sb = new StringBuffer("");
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                String resp = sb.toString();
                in.close();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                Log.e("Response get details", resp);
                if ((resp != null) && (!resp.contains("<html>"))) {
                    Util.LoggingQueue(globalContext, TAG, "UpdateTablesTask response -> " + resp);
                    if (StringUtils.isNotEmpty(resp)) {
                        FirstSynchResDto fpsDataDto = gson.fromJson(resp, FirstSynchResDto.class);
                        insertIntoDatabase(fpsDataDto);
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(resp, "", "Success", primaryId);
                        }
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
        } catch (Exception e) {
            // update response into local db
            if (primaryId != -1) {
                FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", e.toString(), "Failure", primaryId);
            }
        }
    }

    /**
     * Database insertion of received data
     */
    public void insertIntoDatabase(FirstSynchResDto firstSynchResDto) {
        try {
            Util.LoggingQueue(globalContext, TAG, "insertIntoDatabase() called FirstSynchResDto -> " + firstSynchResDto);
            FistSyncInputDto fistSyncInputDto = firstSync.get(0);
            switch (fistSyncInputDto.getTableNames()) {
                case TABLE_CARDTYPE:
                    FPSDBHelper.getInstance(globalContext).insertCardTypeData(firstSynchResDto.getCardtypeDto(), "RegularSync");
                    break;
                case TABLE_FPSSTOCK:
                    FPSDBHelper.getInstance(globalContext).insertFpsStockData(firstSynchResDto.getFpsStockDto(), "RegularSync");
                    break;
                case TABLE_PRODUCT:
                    FPSDBHelper.getInstance(globalContext).insertProductData(firstSynchResDto.getProductDto(), "RegularSync");
                    break;
                case TABLE_BILL:
                    FPSDBHelper.getInstance(globalContext).insertBillData(firstSynchResDto.getBillDto(), "RegularSync");
                    break;
                case TABLE_BENEFICIARY:
                    FPSDBHelper.getInstance(globalContext).insertBeneficiaryData(firstSynchResDto.getBeneficiaryDto(), "RegularSync");
                    break;
                case TABLE_BENEFREGREQ:
                    FPSDBHelper.getInstance(globalContext).insertRegistrationRequestData(firstSynchResDto.getBenefRegReqDto(), "RegularSync");
                    break;
                case TABLE_REGIONBASEDRULE:
                    FPSDBHelper.getInstance(globalContext).insertRegionRules(firstSynchResDto.getRegionBasedRulesDto(), "RegularSync");
                    break;
                case TABLE_PRICEOVERRIDE:
                    FPSDBHelper.getInstance(globalContext).insertProductPriceOverride(firstSynchResDto.getOverrideDto(), "RegularSync");
                    break;
                case TABLE_PRODUCTGROUP:
                    FPSDBHelper.getInstance(globalContext).insertProductGroup(firstSynchResDto.getProductGroupDtos(), "RegularSync");
                    break;
                case TABLE_FPSSTOCKINWARD:
                    FPSDBHelper.getInstance(globalContext).insertFpsStockInwardDetails(firstSynchResDto.getGodownStockOutwardDto(), "RegularSync");
//                    if (!LoginData.getInstance().getLoginData().getUserDetailDto().getProfile().equalsIgnoreCase("INSPECTION")) {
//                        new StockInwardAlertDialog(GlobalAppState.getInstance().getBaseContext()).show();
                        IntentFilter filter = new IntentFilter("inward_dialog");
                        try { globalContext.registerReceiver(receiver, filter); } catch(Exception e) {}
                        Intent intent1 = new Intent("inward_dialog");
                        globalContext.sendBroadcast(intent1);
//                    }
                    break;
                case TABLE_USERDETAIL:
                    FPSDBHelper.getInstance(globalContext).insertUserDetailData(firstSynchResDto.getUserdetailDto(), "RegularSync");
                    break;
                case TABLE_ENTITLEMENTMASTER:
                    FPSDBHelper.getInstance(globalContext).insertMasterRules(firstSynchResDto.getEntitlementMasterRulesDto(), "RegularSync");
                    break;
                case TABLE_FPSMIGRATION:
                    FPSDBHelper.getInstance(globalContext).insertMigrations(firstSynchResDto.getFpsMigrationDtos(), "RegularSync");
                    break;
                case TABLE_SPLRULES:
                    FPSDBHelper.getInstance(globalContext).insertSpecialRules(firstSynchResDto.getSplEntitlementRulesDto(), "RegularSync");
                    break;
                case TABLE_PERSONBASEDRULE:
                    FPSDBHelper.getInstance(globalContext).insertPersonRules(firstSynchResDto.getPersonBasedRulesDto(), "RegularSync");
                    break;
                case TABLE_SERVICEPROVIDER:
//                    FPSDBHelper.getInstance(globalContext).insertLpgProviderDetails(firstSynchResDto.getServiceProviderDto());
                    break;
                case TABLE_SMSPROVIDER:
                    FPSDBHelper.getInstance(globalContext).insertSmsProvider(firstSynchResDto.getSmsProviderDtos(), "RegularSync");
                    break;
                case TABLE_STOCKADJUSTMENT:
                    Log.e("Stock In Proc", firstSynchResDto.getStockAdjusmentDtos().toString());
                    FPSDBHelper.getInstance(globalContext).stockAdjustmentFirstSync(firstSynchResDto.getStockAdjusmentDtos(), "RegularSync");
//                    if (!LoginData.getInstance().getLoginData().getUserDetailDto().getProfile().equalsIgnoreCase("INSPECTION")) {
//                        new StockAdjustAlertDialog(GlobalAppState.getInstance().getBaseContext()).show();
                        IntentFilter filter2 = new IntentFilter("adjustment_dialog");
                        try { globalContext.registerReceiver(receiver, filter2); } catch(Exception e) {}
                        Intent intent2 = new Intent("adjustment_dialog");
                        globalContext.sendBroadcast(intent2);
//                    }
                    break;
                case TABLE_AADHAARSEEDING:
                    FPSDBHelper.getInstance(globalContext).beneficiaryMemberAadharSync(firstSynchResDto.getAadhaarDtos(), "Add", "RegularSync");
                    break;
                case TABLE_DELETEDAADHAARSEEDING:
                    FPSDBHelper.getInstance(globalContext).beneficiaryMemberAadharSync(firstSynchResDto.getAadhaarDtos(), "Delete", "RegularSync");
                    break;
                case TABLE_BIFURCATIONHISTORY:
                    FPSDBHelper.getInstance(globalContext).bifurcationSync(firstSynchResDto.getBifurcationHistoryDtos(), "RegularSync");
                    break;
                 case TABLE_POSOPERATINGHOURS:
                    FPSDBHelper.getInstance(globalContext).insertPosOperatingHoursData(firstSynchResDto.getPosOperatingHoursDtos(), "RegularSync");
                    break;
                case TABLE_INSPECTIONCRITERIA:
                    FPSDBHelper.getInstance(globalContext).insertSyncCriteria(firstSynchResDto.getInspectionCriteriaDtos(), "RegularSync");
                    break;
                case TABLE_NFSAPOSDATA:
                    FPSDBHelper.getInstance(globalContext).insert_nfsa_pos_data(firstSynchResDto.getNfsaPosDataDtos(), "RegularSync");
                    break;
                /*case TABLE_RELATIONSHIP:
                    FPSDBHelper.getInstance(globalContext).insertRelationshipData(firstSynchResDto.getRelationShipDto());*/
                default:
                    break;
            }
            afterDatabaseInsertion(firstSynchResDto);
        } catch (Exception e) {}
    }

    /*
    * After database insertion by user master this function called
    * */
    private void afterDatabaseInsertion(FirstSynchResDto firstSynchResDto) throws Exception {
        Util.LoggingQueue(globalContext, TAG, "afterDatabaseInsertion() called FirstSynchResDto -> " + firstSynchResDto);
        FirstSynchReqDto fpsRequest = new FirstSynchReqDto();
        fpsRequest.setDeviceNum(deviceId);
        fpsRequest.setLastSyncTime(lastModifiedDate);

        // Bifurcation process
        /*try {
            ArrayList<String> bifurcationBenefIdList = FPSDBHelper.getInstance(globalContext).getBifurcationBenefId();
            Util.LoggingQueue(globalContext, TAG, "ArrayList size of bifurcationBenefIdList = " + bifurcationBenefIdList.size());
            for (int i = 0; i < bifurcationBenefIdList.size(); i++) {
                try {
                    Util.LoggingQueue(globalContext, TAG, "Bifurcation ID = " +bifurcationBenefIdList.get(i));
                    int status = FPSDBHelper.getInstance(globalContext).bifurcationDeactivateBenef(bifurcationBenefIdList.get(i));
                    if (status == 1) {
                        Util.LoggingQueue(globalContext, TAG, "status == 1");
                        FPSDBHelper.getInstance(globalContext).updateBifurcationStatus(bifurcationBenefIdList.get(i));
                    } else {
                        Util.LoggingQueue(globalContext, TAG, "status == 0");
                    }
                } catch (Exception e) {
                    Util.LoggingQueue(globalContext, TAG, "Bifurcation Exception1 = " +e);
                }
            }
        } catch (Exception e) {
            Util.LoggingQueue(globalContext, "RegularSyncProcess  Bifurcation Exception2 = ", e.getMessage());
        }*/

        if (firstSynchResDto.isHasMore()) {
            fpsRequest.setTotalCount(firstSynchResDto.getTotalCount());
            fpsRequest.setTotalSentCount(firstSynchResDto.getTotalSentCount());
            fpsRequest.setCurrentCount(firstSynchResDto.getCurrentCount());
            fpsRequest.setTableName(firstSync.get(0).getTableName());
            getTableDatas(fpsRequest);
        } else {
            firstSync.remove(0);
            if (firstSync.size() > 0) {
                fpsRequest.setTableName(firstSync.get(0).getTableName());
                getTableDatas(fpsRequest);
            } else {
                firstSyncSuccess();
            }
        }
    }

    /**
     * After sync success this method will call
     */
    private void firstSyncSuccess() {
        boolean unauthorized = false;
        long primaryId = -1;
        try {
            Util.LoggingQueue(globalContext, TAG, "firstSyncSuccess() called  -> ");
            FirstSynchReqDto fpsRequest = new FirstSynchReqDto();
            fpsRequest.setDeviceNum(deviceId);
            String requestData = new Gson().toJson(fpsRequest);
            // inserting request into local db
            primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(requestData, "RegularSyncService_FirstSyncSuccess");
            stringEntity = new StringEntity(requestData, HTTP.UTF_8);
            Util.LoggingQueue(globalContext, TAG, "firstSyncSuccess() called  updateData -> " + requestData);
            String url = serverUrl + "/transaction/completesynch";
            Util.LoggingQueue(globalContext, TAG, "SyncSuccess called  url -> " + url);
            URI website = new URI(url);
            HttpResponse response = requestType(website, stringEntity);
            if (response != null && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = null;
                StringBuffer sb = new StringBuffer("");
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                String resp = sb.toString();
                in.close();
                if ((resp != null) && (!resp.contains("<html>"))) {
                    String messageData = "";
                    String status = "";
                    Util.LoggingQueue(globalContext, TAG, "SyncSuccess called  response -> " + resp);
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    FirstSynchResDto fpsDataDto = gson.fromJson(resp, FirstSynchResDto.class);
                    int statusCode = fpsDataDto.getStatusCode();
                    if (statusCode == 0 && fpsDataDto.getLastSyncTime() != null) {
                        status = "Success";
                        FPSDBHelper.getInstance(globalContext).updateMaserData("syncTime", fpsDataDto.getLastSyncTime());
                    }
                    else {
                        status = "Failure";
                        try {
                            messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(statusCode));
                            if (messageData == null) {
                                messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(4000));
                            }
                        } catch (Exception e) {
                            messageData = Util.messageSelection(FPSDBHelper.getInstance(globalContext).retrieveLanguageTable(4000));
                        }
                    }
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory(resp, messageData, status, primaryId);
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
        } catch (Exception e) {
            Log.e("Error in First sync", e.toString(), e);
            // update response into local db
            if (primaryId != -1) {
                FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", e.toString(), "Failure", primaryId);
            }
        }
    }

    /**
     * returns FistSyncInputDto of details of tables received from server
     */
    private FistSyncInputDto getInputDTO(String tableName, TableNames names) {
        Util.LoggingQueue(globalContext, TAG, "RegularSyncProcess getInputDTO() called  tableName -> " + tableName + "names -> " + names);
        FistSyncInputDto inputDto = new FistSyncInputDto();
        inputDto.setTableName(tableName);
        inputDto.setTableNames(names);
        inputDto.setDynamic(true);
        return inputDto;
    }

    /*return http POST method using parameters*/
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {
        Util.LoggingQueue(globalContext, TAG, "requestType() called   -> ");
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
//                Log.e(TAG, "url..." + url);
                URI website = new URI(url);
                LoginDto loginDto = setLoginDetails();
                if(loginDto != null) {
                    String loginDetails = new Gson().toJson(loginDto);
                    Log.e(TAG, "sessionRequestData..."+loginDetails);
                    // inserting request into local db
                    sessionPrimaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(loginDetails, "Session_request_RegularSyncService");
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
        long id = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory("", "Session_request_RegularSyncService");
        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("", "LoginCredentials unavailable in local db", "Failure", id);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("inward_dialog")) {
                    new StockInwardAlertDialog(globalContext).show();
                }
                else if (intent.getAction().equals("adjustment_dialog")) {
                    new StockAdjustAlertDialog(globalContext).show();
                }
            }
            catch(Exception e) {}

            try { globalContext.unregisterReceiver(receiver); }
            catch(Exception e) {}
        }
    };

}