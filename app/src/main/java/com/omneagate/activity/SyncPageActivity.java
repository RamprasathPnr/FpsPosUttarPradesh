package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.EnumDTO.TableNames;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.FirstSynchReqDto;
import com.omneagate.DTO.FirstSynchResDto;
import com.omneagate.DTO.FistSyncInputDto;
import com.omneagate.DTO.FpsAllotmentRequestDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.OpeningStockHistoryDto;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.DTO.UserDto.StockCheckDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LocalDbBackup;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.SyncPageUpdate;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.FirstSyncFailureDialog;
import com.omneagate.activity.dialog.RetryDialog;
import com.omneagate.activity.dialog.RetryFailedDialog;
import com.omneagate.activity.dialog.RetryFailedMasterDataDialog;
import com.omneagate.activity.dialog.TableExceptionErrorDialog;

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
import java.net.HttpURLConnection;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;*/

public class SyncPageActivity extends BaseActivity {

    ScrollView loadScroll; //Scroll bar instance

    StringEntity stringEntity;   //StringEntity for sending data

    LinearLayout layout; // Layout for textView insert

    ProgressBar progressBar;  //ProgressBar for loading

    int retryCount = 0;   //user retry count

    List<FistSyncInputDto> firstSync;  //FistSync items

    int totalProgress = 6;  //Progressbar item add

    String serverUrl;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client2;

    // Boolean isTableExceptionFound = false;

    RetryDialog retryDialog;

    RetryFailedDialog retryFailedDialog;

    RetryFailedMasterDataDialog retryFailedMasterDataDialog;

    TableExceptionErrorDialog tableExceptionErrorDialog;

    FirstSyncFailureDialog firstSyncFailureDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_page);
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "onCreate() called ");
        Util.setTamilText((TextView) findViewById(R.id.downloadCompleted), R.string.downloading_files);
        // ((TextView) findViewById(R.id.downloadCompleted)).setText(""+R.string.downloading_files);

        Util.setTamilText((TextView) findViewById(R.id.syncIndicator), R.string.sync_page_msg);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        layout = (LinearLayout) findViewById(R.id.info);
        loadScroll = (ScrollView) findViewById(R.id.scrollData);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        findViewById(R.id.syncContinue).setVisibility(View.INVISIBLE);
        firstTimeSyncDetails();
        // Util.LoggingQueue(this, "Sync Page", "Starting up Sync");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Send request to server
     * <p/>
     * for getting table details
     */
    public void firstTimeSyncDetails() {
        try {

            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstTimeSyncDetails() called ");

            FirstSynchReqDto fpsRequest = new FirstSynchReqDto();
            String deviceId = Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
            fpsRequest.setDeviceNum(deviceId);
            serverUrl = FPSDBHelper.getInstance(this).getMasterData("serverUrl");
            String updateData = new Gson().toJson(fpsRequest);
            //  Util.LoggingQueue(this, "Sync Page", "First Request:" + updateData);
            //  Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstTimeSyncDetails() updateData GSON value ->"+updateData);

            stringEntity = new StringEntity(updateData, HTTP.UTF_8);
            new UpdateSyncTask().execute("");
        } catch (Exception e) {
            // Util.LoggingQueue(this, "Sync Page", "Error in sync:" + e.toString());
            // Log.e("SyncPageActivity", e.toString(), e);
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstTimeSyncDetails() called Exception " + e);

        }
    }


    /**
     * After response received from server successfully in android
     * Table details fetched in MAP
     * if tableDetails is empty or null user need to retry
     */
    private void processSyncResponseData(String response) {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "processSyncResponseData() called response ->" + response);
            //     Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "processSyncResponseData() called ");

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            //    Log.e("responseData", response.toString());
            FirstSynchResDto fpsDataDto = gson.fromJson(response, FirstSynchResDto.class);
            int statusCode = fpsDataDto.getStatusCode();
            if (statusCode == 0) {
                if ((fpsDataDto.getTableDetails() == null || fpsDataDto.getTableDetails().isEmpty())) {
                    errorInSync();
                    return;
                }
                syncTableDetails(fpsDataDto.getTableDetails());
            }
            else if (statusCode == 5057) {
                String errorMsg = getString(R.string.unassociated)+". "+getString(R.string.contact_helpdesk);
                firstSyncFailureDialog = new FirstSyncFailureDialog(this, errorMsg);
                firstSyncFailureDialog.show();
            }
        } catch (Exception e) {
            // Util.LoggingQueue(this, "Error", e.toString());
            // Log.e("SyncPageActivity", e.toString(), e);
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "processSyncResponseData() called Exception " + e);
            errorInSync();
            return;
        }
    }

    /**
     * Used to get number of items in server table
     * TableDetails map is input
     * If masters count 0 user should retry
     */
    private void syncTableDetails(Map<String, Integer> tableDetails) {
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "syncTableDetails() called tableDetails ->" + tableDetails);

        firstSync = new ArrayList<>();
        List<String> masterDataEmpty = new ArrayList<>();

        int count = tableDetails.get("TABLE_CARDTYPE");
        if (count == 0) {
            masterDataEmpty.add("Card Type");
        }
        firstSync.add(getInputDTO("TABLE_CARDTYPE", count, "Table Card type downloading", "Card type downloaded with", TableNames.TABLE_CARDTYPE));

        count = tableDetails.get("TABLE_PRODUCT");
        if (count == 0) {
            masterDataEmpty.add("Product");
        }
        firstSync.add(getInputDTO("TABLE_PRODUCT", count, "Table Product downloading", "Product downloaded with", TableNames.TABLE_PRODUCT));

        count = tableDetails.get("TABLE_FPSSTOCK");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_FPSSTOCK", count, "Table Stock downloading", "Stock downloaded with", TableNames.TABLE_FPSSTOCK));

        count = tableDetails.get("TABLE_SMSPROVIDER");
        if (count == 0) {
            masterDataEmpty.add("SMS provider");
        }
        firstSync.add(getInputDTO("TABLE_SMSPROVIDER", count, "Table SMS Provider downloading", "SMS Provider downloaded with", TableNames.TABLE_SMSPROVIDER));

        count = tableDetails.get("TABLE_SPLRULES");
        if (count == 0) {
            masterDataEmpty.add("Special rules");
        }
        firstSync.add(getInputDTO("TABLE_SPLRULES", count, "Table Special Rules downloading", "Special rules downloaded with", TableNames.TABLE_SPLRULES));

        count = tableDetails.get("TABLE_ENTITLEMENTMASTER");
        if (count == 0) {
            masterDataEmpty.add("Entitlement master");
        }
        firstSync.add(getInputDTO("TABLE_ENTITLEMENTMASTER", count, "Table Entitlement Rules downloading", "Entitlement Rules  downloaded with", TableNames.TABLE_ENTITLEMENTMASTER));

        count = tableDetails.get("TABLE_PERSONBASEDRULE");
        if (count == 0) {
            masterDataEmpty.add("Person based rule");
        }
        firstSync.add(getInputDTO("TABLE_PERSONBASEDRULE", count, "Table Person Based Rules downloading", "Person Based Rules downloaded with", TableNames.TABLE_PERSONBASEDRULE));

        count = tableDetails.get("TABLE_REGIONBASEDRULE");
        if (count == 0) {
            masterDataEmpty.add("region based rules");
        }

        if (masterDataEmpty.size() > 0) {
            retryFailedMasterDataDialog = new RetryFailedMasterDataDialog(this, masterDataEmpty);
            retryFailedMasterDataDialog.show();
            return;
        }

        firstSync.add(getInputDTO("TABLE_REGIONBASEDRULE", count, "Table Region Based Rules downloading", "Region Based Rules downloaded with", TableNames.TABLE_REGIONBASEDRULE));

        count = tableDetails.get("TABLE_FPSSTORE");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_FPSSTORE", count, "Table Store downloading", "Store downloaded with", TableNames.TABLE_FPSSTORE));

        count = tableDetails.get("TABLE_PRODUCTGROUP");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_PRODUCTGROUP", count, "Table Product Group downloading", "Store downloaded with ", TableNames.TABLE_PRODUCTGROUP));

        count = tableDetails.get("TABLE_PRICEOVERRIDE");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_PRICEOVERRIDE", count, "Table Product Group downloading", "Store downloaded with ", TableNames.TABLE_PRICEOVERRIDE));

        count = tableDetails.get("TABLE_BENEFICIARY");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_BENEFICIARY", count, "Table Beneficiary downloading", "Beneficiary downloaded with", TableNames.TABLE_BENEFICIARY));

        count = tableDetails.get("TABLE_GODOWNSTKOUTWARD");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_GODOWNSTKOUTWARD", count, "Table Stock Inward downloading", "Stock Inward downloaded with", TableNames.TABLE_FPSSTOCKINWARD));

        count = tableDetails.get("TABLE_BILL");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_BILL", count, "Table Bill downloading", "Bill downloaded with", TableNames.TABLE_BILL));

        count = tableDetails.get("TABLE_BENEFREGREQ");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_BENEFREGREQ", count, "Table Beneficiary Reg request downloading", "Beneficiary Reg downloaded with", TableNames.TABLE_BENEFREGREQ));

        count = tableDetails.get("TABLE_SERVICEPROVIDER");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_SERVICEPROVIDER", count, "Table Service Provider downloading", "Service Provider downloaded with", TableNames.TABLE_SERVICEPROVIDER));

        count = tableDetails.get("TABLE_FPSMIGRATION");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_FPSMIGRATION", count, "Table Migration data downloading", "Migration data downloaded with", TableNames.TABLE_FPSMIGRATION));

        count = tableDetails.get("TABLE_USERDETAIL");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_USERDETAIL", count, "Table User details downloading", "User details downloaded with", TableNames.TABLE_USERDETAIL));

        count = tableDetails.get("TABLE_STOCKADJUSTMENT");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_STOCKADJUSTMENT", count, "Table Stock Adjustment details downloading", "Stock Adjustment details downloaded with", TableNames.TABLE_STOCKADJUSTMENT));

        count = tableDetails.get("TABLE_AADHAARSEEDING");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_AADHAARSEEDING", count, "Table Aadhaar Seeding downloading", "Aadhaar Seeding data downloaded with", TableNames.TABLE_AADHAARSEEDING));
        totalProgress = 100 / firstSync.size();

        count = tableDetails.get("TABLE_FPSSTOCKADVANCE");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_FPSSTOCKADVANCE", count, "Table Fps stock advance downloading", "Fps stock advance data downloaded with", TableNames.TABLE_FPSSTOCKADVANCE));
        totalProgress = 100 / firstSync.size();

        count = tableDetails.get("TABLE_CLOSESALETRANSACTION");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_CLOSESALETRANSACTION", count, "Table Close sale transaction downloading", "Close sale transaction data downloaded with", TableNames.TABLE_CLOSESALETRANSACTION));
        totalProgress = 100 / firstSync.size();

        count = tableDetails.get("TABLE_PROXYDETAIL");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_PROXYDETAIL", count, "Table Proxy detail transaction downloading", "Proxy detail transaction data downloaded with", TableNames.TABLE_PROXYDETAIL));
        totalProgress = 100 / firstSync.size();

        count = tableDetails.get("TABLE_BFDDETAIL");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_BFDDETAIL", count, "Table Bfd detail transaction downloading", "Bfd detail transaction data downloaded with", TableNames.TABLE_BFDDETAIL));
        totalProgress = 100 / firstSync.size();

        /*count = tableDetails.get("TABLE_RELATIONSHIP");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_RELATIONSHIP", count, "Table Relationship downloading", "Relationship data downloaded with", TableNames.TABLE_RELATIONSHIP));
        totalProgress = 100 / firstSync.size();*/

 /* 15-07-2016
    * Added new table CREATE_TABLE_POSOPERATINGHOURS
    */
        count = tableDetails.get("TABLE_POSOPERATINGHOURS");
        if (count > 0)
            firstSync.add(getInputDTO("TABLE_POSOPERATINGHOURS", count, "Table Close sale transaction downloading", "Close sale transaction data downloaded with", TableNames.TABLE_POSOPERATINGHOURS));
        totalProgress = 100 / firstSync.size();

        if (tableDetails.get("TABLE_INSPECTIONCRITERIA") != null)
        {
            count = tableDetails.get("TABLE_INSPECTIONCRITERIA");
            if (count > 0) {
                firstSync.add(getInputDTO("TABLE_INSPECTIONCRITERIA", count, "Table Inspection Criteria downloading", "Inspection Criteria downloaded with", TableNames.TABLE_INSPECTIONCRITERIA));
            }
        }

       if (tableDetails.get("TABLE_NFSAPOSDATA") != null)
        {
            count = tableDetails.get("TABLE_NFSAPOSDATA");
            if (count > 0) {
                firstSync.add(getInputDTO("TABLE_NFSAPOSDATA", count, "Table _NFSAPOSDATA downloading", "_NFSAPOS_table downloaded with", TableNames.TABLE_NFSAPOSDATA));
            }
        }

        FirstSynchReqDto fpsRequest = new FirstSynchReqDto();
        String deviceId = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
        fpsRequest.setDeviceNum(deviceId);
        fpsRequest.setTableName(firstSync.get(0).getTableName());
        setTableSyncCall(fpsRequest);
        setTextStrings(firstSync.get(0).getTextToDisplay() + "....");




        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "syncTableDetails() finished  FirstSynchReqDto ->" + fpsRequest);

    }

    /**
     * Request for datas by giving name of table to server
     * <p/>
     * input FirstSynchReqDto fpsRequest
     */

    private void setTableSyncCall(FirstSynchReqDto fpsRequest) {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "setTableSyncCall() called FirstSynchReqDto ->" + fpsRequest);

            String updateData = new Gson().toJson(fpsRequest);
            // Log.e("input data", updateData);
            // Util.LoggingQueue(this, "Sync Page", "Table Wise Sync called:" + updateData);
            stringEntity = new StringEntity(updateData, HTTP.UTF_8);
            new UpdateTablesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
        } catch (Exception e) {
            //Log.e("SyncPageActivity", e.toString(), e);
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "setTableSyncCall() called Exception ->" + e);

        }
    }

    /**
     * returns FistSyncInputDto of details of tables received from server
     */
    private FistSyncInputDto getInputDTO(String tableName, int count, String textToDisplay, String endText, TableNames names) {
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getInputDTO() called tableName ->" + tableName);
        //      Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getInputDTO() called count ->"+count);
        //    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getInputDTO() called textToDisplay ->"+textToDisplay);
        //  Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getInputDTO() called endText ->"+endText);
        //Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getInputDTO() called names ->"+names);

        FistSyncInputDto inputDto = new FistSyncInputDto();
        inputDto.setTableName(tableName);
        inputDto.setCount(count);
        inputDto.setTableNames(names);
        inputDto.setTextToDisplay(textToDisplay);
        inputDto.setEndTextToDisplay(endText);
        inputDto.setDynamic(true);
        return inputDto;
    }

    /**
     * After sync success this method will call
     */
    private void firstSyncSuccess() {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstSyncSuccess() called  ->");

            FirstSynchReqDto fpsRequest = new FirstSynchReqDto();
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
            fpsRequest.setDeviceNum(deviceId);
            String updateData = new Gson().toJson(fpsRequest);
            //Util.LoggingQueue(this, "Sync Page", "Sync Success req:" + updateData);
//            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstSyncSuccess() called  updateData ->" +updateData);

            stringEntity = new StringEntity(updateData, HTTP.UTF_8);
            new SyncSuccess().execute("");

            new firstAllocationCall().execute("");
            new secondAllocationCall().execute("");
            new thirdAllocationCall().execute("");



        } catch (Exception e) {
            //  Util.LoggingQueue(this, "SyncPageActivity Error", e.toString());
            // Log.e("SyncPageActivity", e.toString(), e);
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstSyncSuccess() called Exception " + e);

        }
    }

    private void firstSyncSuccessResponse(String response) {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstSyncSuccessResponse() called  response ->" + response);

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            FirstSynchResDto fpsDataDto = gson.fromJson(response, FirstSynchResDto.class);
            int statusCode = fpsDataDto.getStatusCode();
            if (statusCode == 0) {
                if ((fpsDataDto.getLastSyncTime() == null)) {
                    errorInSync();
                    return;
                }
                progressBar.setProgress(100);
                FPSDBHelper.getInstance(SyncPageActivity.this).updateMaserData("syncTime", fpsDataDto.getLastSyncTime());

               /* ((TextView) findViewById(R.id.downloadCompleted)).setText(R.string.download_completed);
                ((TextView) findViewById(R.id.syncIndicator)).setText(R.string.sync_page_completed);*/
                // ((TextView) findViewById(R.id.downloadCompleted)).setText(""+R.string.download_completed);

                Util.setTamilText((TextView) findViewById(R.id.downloadCompleted), R.string.download_completed);
                Util.setTamilText((TextView) findViewById(R.id.syncIndicator), R.string.sync_page_completed);


                /*Button continueButton = (Button) findViewById(R.id.syncContinue);
                continueButton.setVisibility(View.VISIBLE);
                continueButton.setBackgroundColor(Color.parseColor("#00b7be"));
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        syncSuccessCompletion();
                    }
                });*/
            }
        } catch (Exception e) {
            //  Util.LoggingQueue(this, "Sync Page", "Error in sync:" + e.toString());
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstSyncSuccessResponse() called Exception " + e);

            errorInSync();
            return;
        }
    }

    private void syncSuccessCompletion() {
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "syncSuccessCompletion() called  ->");

        SyncPageUpdate syncPage = new SyncPageUpdate(this);
        syncPage.setSync();
        if (LoginData.getInstance().getLoginData().getUserDetailDto().getProfile().equalsIgnoreCase("ADMIN")) {
            startActivity(new Intent(this, AdminActivity.class));
            finish();
        } else {
//            startService(new Intent(this, ConnectionHeartBeat.class));
//            startService(new Intent(this, UpdateDataService.class));
//            startService(new Intent(this, RemoteLoggingService.class));
//            startService(new Intent(this, OfflineTransactionManager.class));
//            startService(new Intent(this, OfflineInwardManager.class));
//            startService(new Intent(this, OfflineImageManager.class));
//            startService(new Intent(this, SyncExcUpdateToServer.class));
//            startService(new Intent(this, SyncProcessedAdvanceStock.class));
//            startService(new Intent(this, OfflineReportAckService.class));

            /*int count = FPSDBHelper.getInstance(SyncPageActivity.this).getAllMissedProductCount();
            if (count == 1) {
                startActivity(new Intent(this, MissedOpenStockActivity.class));
            } else if (count == 0) {
                startActivity(new Intent(this, SaleActivity.class));
            }*/

            finish();
        }
    }


    /**
     * Progress bar setting in activity
     */
    private void setDownloadedProgress() {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "setDownloadedProgress() called  ->");

            int progress = progressBar.getProgress();
            progress = progress + totalProgress;
            progressBar.setProgress(progress);
        } catch (Exception e) {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "setDownloadedProgress() called Exception " + e);

        }
    }

    /**
     * Response received from server
     */
    private void setTableResponse(String response) {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "setTableResponse() called  response ->" + response);

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            FirstSynchResDto fpsDataDto = gson.fromJson(response, FirstSynchResDto.class);
            if (fpsDataDto.getStatusCode() == 0) {
                insertIntoDatabase(fpsDataDto);
            } else {
                errorInSync();
                return;
            }
        } catch (Exception e) {
            //    Util.LoggingQueue(this, "Sync Page", "setTableResponse Exception..."+e);
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "setTableResponse() called Exception " + e);

            errorInSync();
            return;
        }
    }

    /**
     * Scrolling of received String
     */
    private void setTextStrings(String syncString) {
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "setTextStrings() called  syncString ->" + syncString);

        TextView tv = new TextView(SyncPageActivity.this);
        tv.setText(syncString);
        tv.setTextColor(Color.parseColor("#5B5B5B"));
        tv.setTextSize(22);
        layout.addView(tv);
        loadScroll.post(new Runnable() {
            @Override
            public void run() {
                loadScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    /**
     * Database insertion of received data
     */
    private void insertIntoDatabase(FirstSynchResDto firstSynchResDto) {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "insertIntoDatabase() called  ");
            FistSyncInputDto fistSyncInputDto = firstSync.get(0);
            setTextStrings(firstSync.get(0).getEndTextToDisplay() + " items " + firstSynchResDto.getTotalSentCount() + "....");
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "insertIntoDatabase() called  getTableNames ->" + fistSyncInputDto.getTableNames());
            boolean isExceptionThrown;
            switch (fistSyncInputDto.getTableNames()) {

                case TABLE_CARDTYPE:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertCardTypeData(firstSynchResDto.getCardtypeDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_FPSSTOCK:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertFpsStockData(firstSynchResDto.getFpsStockDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_PRODUCT:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertProductData(firstSynchResDto.getProductDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_BILL:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertBillData(firstSynchResDto.getBillDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_BENEFICIARY:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertBeneficiaryData(firstSynchResDto.getBeneficiaryDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_BENEFREGREQ:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertRegistrationRequestData(firstSynchResDto.getBenefRegReqDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_REGIONBASEDRULE:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertRegionRules(firstSynchResDto.getRegionBasedRulesDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_PRICEOVERRIDE:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertProductPriceOverride(firstSynchResDto.getOverrideDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_PRODUCTGROUP:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertProductGroup(firstSynchResDto.getProductGroupDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_FPSSTOCKINWARD:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertFpsStockInwardDetails(firstSynchResDto.getGodownStockOutwardDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_USERDETAIL:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertUserDetailData(firstSynchResDto.getUserdetailDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_ENTITLEMENTMASTER:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertMasterRules(firstSynchResDto.getEntitlementMasterRulesDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_SPLRULES:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertSpecialRules(firstSynchResDto.getSplEntitlementRulesDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_PERSONBASEDRULE:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertPersonRules(firstSynchResDto.getPersonBasedRulesDto(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_FPSMIGRATION:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertMigrations(firstSynchResDto.getFpsMigrationDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_STOCKADJUSTMENT:
                    isExceptionThrown = FPSDBHelper.getInstance(this).stockAdjustmentFirstSync(firstSynchResDto.getStockAdjusmentDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_SERVICEPROVIDER:
//                FPSDBHelper.getInstance(this).insertLpgProviderDetails(firstSynchResDto.getServiceProviderDto());
                    break;

                case TABLE_SMSPROVIDER:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertSmsProvider(firstSynchResDto.getSmsProviderDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_AADHAARSEEDING:
                    isExceptionThrown = FPSDBHelper.getInstance(this).beneficiaryMemberAadharSync(firstSynchResDto.getAadhaarDtos(), "Add", "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_FPSSTOCKADVANCE:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertAdvanceFpsStockSync(firstSynchResDto.getFpsAdvanceStockDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_CLOSESALETRANSACTION:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertIntoCloseSaleSync(firstSynchResDto.getCloseSaleTransactionDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_POSOPERATINGHOURS:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertPosOperatingHoursData(firstSynchResDto.getPosOperatingHoursDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_INSPECTIONCRITERIA:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insertSyncCriteria(firstSynchResDto.getInspectionCriteriaDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                case TABLE_NFSAPOSDATA:
                    isExceptionThrown = FPSDBHelper.getInstance(this).insert_nfsa_pos_data(firstSynchResDto.getNfsaPosDataDtos(), "FirstSync");
                    if (!isExceptionThrown) {
                        backupDB();
                        exceptionInSync();
                        return;
                    }
                    break;

                /*case TABLE_RELATIONSHIP:
                FPSDBHelper.getInstance(this).insertRelationshipData(firstSynchResDto.getRelationShipDto());
                break;*/

                default:
                    break;
            }
            afterDatabaseInsertion(firstSynchResDto);
        } catch (Exception e) {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "insertIntoDatabase() called  Exception ->" + e);

        }
    }

    private void backupDB() {
        LocalDbBackup localDbBackup = new LocalDbBackup(SyncPageActivity.this);
        localDbBackup.backupDb(true, "", FPSDBHelper.DATABASE_NAME, "");
    }


    /*
    * After database insertion by user master this function called
    * */
    private void afterDatabaseInsertion(FirstSynchResDto firstSynchResDto) {

        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "afterDatabaseInsertion() called  FirstSynchResDto ->" + firstSynchResDto);

        FirstSynchReqDto fpsRequest = new FirstSynchReqDto();
        String deviceId = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
        fpsRequest.setDeviceNum(deviceId);
        if (firstSynchResDto.isHasMore()) {
            fpsRequest.setTotalCount(firstSynchResDto.getTotalCount());
            fpsRequest.setTotalSentCount(firstSynchResDto.getTotalSentCount());
            fpsRequest.setCurrentCount(firstSynchResDto.getCurrentCount());
            fpsRequest.setTableName(firstSync.get(0).getTableName());
            setTableSyncCall(fpsRequest);
        } else {
            firstSync.remove(0);
            setDownloadedProgress();
            if (firstSync.size() > 0) {
                fpsRequest.setTableName(firstSync.get(0).getTableName());
                setTextStrings(firstSync.get(0).getTextToDisplay() + "....");
                setTableSyncCall(fpsRequest);
            } else {
//                getOpeningStock();
                getOpeningStockInLocal();
                firstSyncSuccess();
            }
        }
    }


    private void getOpeningStock() {
        try {
            String deviceId = Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase();
            OpeningStockHistoryDto open = new OpeningStockHistoryDto();
            open.setDeviceNum(deviceId);
            String updateData = new Gson().toJson(open);
            Util.LoggingQueue(this, "Sync Page", "Sync Success req:" + updateData);
            stringEntity = new StringEntity(updateData, HTTP.UTF_8);
            new getOpeningStockTask().execute("");
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    private void getOpeningStockInLocal() {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getOpeningStockInLocal() called   ->");

            List<StockCheckDto> stockCheckDtoList = FPSDBHelper.getInstance(this).getAllProductStockDetails();
            for (int position = 0; position < stockCheckDtoList.size(); position++) {

                long productId = stockCheckDtoList.get(position).getProductId();

                // Get current stock
                FPSStockDto stockList = FPSDBHelper.getInstance(this).getAllProductStockDetails(productId);
                double currentStock = 0.0;
                if(stockList != null) {
                    currentStock = stockList.getQuantity();
                }

                // Get today inward
                BillItemDto productInwardToday = FPSDBHelper.getInstance(this).getAllInwardListTodayTwo(productId);
                double inward = productInwardToday.getQuantity();

                // Get today adjustment
                List<POSStockAdjustmentDto> fpsStockAdjustment = FPSDBHelper.getInstance(this).getStockAdjustment(productId);
                double adjustment = 0.0;
                if (fpsStockAdjustment.size() == 0) {
                } else {
                    adjustment = getAdjustedValue(fpsStockAdjustment);
                }
                adjustment = -(adjustment);

                double soldOut = stockCheckDtoList.get(position).getSold();

                double openingStock = (currentStock - inward + adjustment + soldOut);

                FPSDBHelper.getInstance(this).insertStockHistory(openingStock, FPSDBHelper.getInstance(this).getClosingStock(productId), "INITIAL STOCK", 0.0, productId);

            }
        } catch (Exception e) {
            //   Log.e("Error",e.toString(),e);
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getOpeningStockInLocal() called Exception " + e);

        }
    }

    private double getAdjustedValue(List<POSStockAdjustmentDto> adjustment) {
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getAdjustedValue() called   ->");

        double quantity = 0.0;
        for (POSStockAdjustmentDto productValue : adjustment) {
            double productQuantity = productValue.getQuantity();
            if (productValue.getRequestType().equalsIgnoreCase("STOCK_DECREMENT")) {
                productQuantity = -1 * productQuantity;
            }
            quantity = quantity + productQuantity;
        }
        return quantity;
    }


    /**
     * user logout
     */
    public void logOut() {
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "logOut() called   ->");
        FPSDBHelper.getInstance(SyncPageActivity.this).deleteAllRecordsInAllTables();
        FPSDBHelper.getInstance(this).closeConnection();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void errorInSync() {
        layout.removeAllViews();
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "errorInSync() called   ->");
        Util.LoggingQueue(this, "Sync Page", "Error in sync");
        progressBar.setProgress(0);
        retryCount++;
        if (retryCount >= 3) {
            retryFailedDialog = new RetryFailedDialog(this);
            retryFailedDialog.show();
        } else {
            retryDialog = new RetryDialog(this, retryCount);
            retryDialog.show();
        }
    }


    public void exceptionInSync() {
        layout.removeAllViews();
        Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "exceptionInSync() called   ->");
        Util.LoggingQueue(this, "Sync Page", "Error in sync");
        progressBar.setProgress(0);
        tableExceptionErrorDialog = new TableExceptionErrorDialog(this);
        tableExceptionErrorDialog.show();
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

    /**
     * Concrete method
     */
    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SyncPage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.omneagate.activity/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        /*Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SyncPage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.omneagate.activity/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();*/
    }

    /**
     * Async   task for Download Sync for table details
     */
    private class UpdateSyncTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... f_url) {
            BufferedReader in = null;
            try {
                String url = serverUrl + "/firstsynch/getdetails";
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateSyncTask url ->" + url);

                URI website = new URI(url);
                HttpResponse response = requestType(website, stringEntity);
                in = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                // Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "Error", "Network exception" + e.getMessage());
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateSyncTask called Exception " + e);

                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateSyncTask >> Exception " + e);

                    // Intentional swallow of exception
                }
            }


            return null;
        }


        @Override
        protected void onPostExecute(String response) {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateSyncTask response ->" + response);
            if ((response != null) && (!response.contains("<html>"))) {
//                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateSyncTask response ->" + response);
                processSyncResponseData(response);
            } else {
                errorInSync();
                return;
            }
        }
    }

    /**
     * Async   task for Download Sync for table details
     */
    private class getOpeningStockTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... f_url) {
            BufferedReader in = null;
            try {
                String url = serverUrl + "/firstsynch/getlastdayclosingstock";
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getOpeningStockTask url ->" + url);

                URI website = new URI(url);
                HttpResponse response = requestType(website, stringEntity);
                in = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getOpeningStockTask called Exception " + e);

                // Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "SyncPageActivity Error", "Network exception" + e.getMessage());
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getOpeningStockTask >> Exception " + e);

                    // Intentional swallow of exception
                }
            }


            return null;
        }


        @Override
        protected void onPostExecute(String response) {
            if ((response != null) && (!response.contains("<html>"))) {
                //Log.e(" last close sale Response", response);
                //Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "Sync Page", "First sync resp:" + response);
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "getOpeningStockTask response ->" + response);

                openStockHistory(response);
            } else {
                errorInSync();
                return;
            }
        }
    }


    private void openStockHistory(String result) {
        try {
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "openStockHistory() called  result ->" + result);

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            FirstSynchResDto fpsDataDto = gson.fromJson(result, FirstSynchResDto.class);
            int statusCode = fpsDataDto.getStatusCode();
            if (statusCode == 0) {
                FPSDBHelper.getInstance(this).insertProductHistory(fpsDataDto.getFpsstockHistoryDtoCollection());
            }
        } catch (Exception e) {
            //  Log.e("openStockHistory Error",e.toString(),e);
            Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "openStockHistory called Exception " + e);

        } finally {
            firstSyncSuccess();
        }

    }

    /**
     * Async   task for Download Sync for data in table
     */
    private class UpdateTablesTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... f_url) {
            BufferedReader in = null;
            try {
                String url = serverUrl + "/firstsynch/gettabledetails";
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateTablesTask called  url ->" + url);

                URI website = new URI(url);
                HttpResponse response = requestType(website, stringEntity);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String l;
                String nl = System.getProperty("line.separator");

                //    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateTablesTask called  stringEntity ->" +stringEntity);
                //     Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateTablesTask called  response.getEntity().getContent()) ->" +response.getEntity().getContent());


                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                // Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "Error", "Network exception" + e.getMessage());
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateTablesTask called Exception " + e);

                //  Log.e("SyncPageActivity", e.toString(), e);
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateTablesTask >> Exception " + e);

                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String response) {
            if ((response != null) && (!response.contains("<html>"))) {
                //   Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "Sync Page", "Table Wise Sync resp" + response);
                //   Log.e("Response", response);
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "UpdateTablesTask called  response ->" + response);

                setTableResponse(response);
            } else {
                errorInSync();
                return;
            }
        }
    }

    /**
     * Async   task for Download Sync for data in table
     */
    private class SyncSuccess extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... f_url) {
            BufferedReader in = null;
            try {
                String url = serverUrl + "/firstsynch/completesynch";
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "SyncSuccess called  url ->" + url);

                URI website = new URI(url);
                HttpResponse response = requestType(website, stringEntity);
                in = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                // Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "Error", "Network exception" + e.toString());
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "SyncSuccess called Exception " + e);

                Log.e("SyncPageActivity", e.toString(), e);
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "SyncSuccess >> Exception " + e);

                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String response) {
            if ((response != null) && (!response.contains("<html>"))) {
                //   Log.i("Response", response);
                //  Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "Sync Page", "Sync Success Resp:" + response);
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "SyncSuccess called  response ->" + response);

                firstSyncSuccessResponse(response);
            }
        }
    }

    private class firstAllocationCall extends AsyncTask<String, String, String> {
        BufferedReader in = null;
        long primaryId = -1;

        @Override
        protected String doInBackground(String... f_url) {
            try {
                String url = serverUrl + "/fps/allotment/fetch";
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstAllocationCall called  url ->" + url);
                URI website = new URI(url);
                String requestData = getRequestData1();
                Log.e("SyncPageActivity", "firstAllocationCall requestData..." + requestData);
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(SyncPageActivity.this).insertBackgroundProcessHistory(requestData, "firstAllocationCall");
                StringEntity entity = new StringEntity(requestData, HTTP.UTF_8);
                HttpResponse response = requestType(website, entity);
                try {
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
                        Log.e("SyncPageActivity", "firstAllocationCall responseData..." + responseData);
                        if ((responseData != null) && (!responseData.contains("<html>"))) {
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            FpsAllotmentRequestDto fpsAllotmentRequestDto = gson.fromJson(responseData, FpsAllotmentRequestDto.class);
                            String messageData = "";
                            String status = "";
                            if (fpsAllotmentRequestDto.getStatusCode() == 0) {
                                status = "Success";
                                if (fpsAllotmentRequestDto.getFpsAllocationCommodityDetailDtos() != null) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                    String syncDate = dateFormat.format(fpsAllotmentRequestDto.getLastModifiedDate());
                                    FPSDBHelper.getInstance(SyncPageActivity.this).updateMaserData("allocationSyncTime", syncDate);
                                    FPSDBHelper.getInstance(SyncPageActivity.this).insert_stock_allocation(fpsAllotmentRequestDto.getFpsAllocationCommodityDetailDtos(), "FirstSync");
                                }
                            } else {
                                status = "Failure";
                                try {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(fpsAllotmentRequestDto.getStatusCode()));
                                    if (messageData == null) {
                                        messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(4000));
                                    }
                                } catch (Exception e) {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(4000));
                                }
                            }
                            // update response into local db
                            if (primaryId != -1) {
                                FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory(responseData, messageData, status, primaryId);
                            }
                        }
                    } else if (response != null && response.getStatusLine() != null && ((response.getStatusLine().getStatusCode() == 403) || (response.getStatusLine().getStatusCode() == 401))) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory("", "Unauthorized", "Failure", primaryId);
                        }
                    } else if (response != null && response.getStatusLine() != null) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", primaryId);
                        }
                    } else if (response == null) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory("", "Response is null", "Failure", primaryId);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Syncpage","allocation exception.."+e);
                }

                return "";
            } catch (Exception e) {
                Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "firstAllocationCall", "Network exception" + e.toString());
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstAllocationCall called Exception " + e);
                Log.e("SyncPageActivity", e.toString(), e);
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "firstAllocationCall >> Exception " + e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
        }

    }


    private String getRequestData1() {
        FpsAllotmentRequestDto fpsRequest = new FpsAllotmentRequestDto();
        Calendar cal =  Calendar.getInstance();
        cal.add(Calendar.MONTH ,-2);
        String previousBeforeMonth  = new SimpleDateFormat("MMM").format(cal.getTime());
        String previousBeforeYear  = new SimpleDateFormat("yyyy").format(cal.getTime());
        fpsRequest.setMonth(previousBeforeMonth.toUpperCase());
        fpsRequest.setYear(Integer.valueOf(previousBeforeYear));
        fpsRequest.setFinalizedStatus(true);
        fpsRequest.setFpsId(getFpsId());
        try {
            String syncTime  = new SimpleDateFormat("yyyy-MM-").format(cal.getTime());
            fpsRequest.setLastSyncTime(syncTime+"01 00:00:00.000");
        }
        catch(Exception e) {}
        String reqData = new Gson().toJson(fpsRequest);
        return reqData;
    }

    private class secondAllocationCall extends AsyncTask<String, String, String> {
        BufferedReader in = null;
        long primaryId = -1;

        @Override
        protected String doInBackground(String... f_url) {
            try {
                String url = serverUrl + "/fps/allotment/fetch";
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "secondAllocationCall called  url ->" + url);
                URI website = new URI(url);
                String requestData = getRequestData2();
                Log.e("SyncPageActivity", "secondAllocationCall requestData..." + requestData);
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(SyncPageActivity.this).insertBackgroundProcessHistory(requestData, "secondAllocationCall");
                StringEntity entity = new StringEntity(requestData, HTTP.UTF_8);
                HttpResponse response = requestType(website, entity);
                try {
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
                        Log.e("SyncPageActivity", "secondAllocationCall responseData..." + responseData);
                        if ((responseData != null) && (!responseData.contains("<html>"))) {
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            FpsAllotmentRequestDto fpsAllotmentRequestDto = gson.fromJson(responseData, FpsAllotmentRequestDto.class);
                            String messageData = "";
                            String status = "";
                            if (fpsAllotmentRequestDto.getStatusCode() == 0) {
                                status = "Success";
                                if (fpsAllotmentRequestDto.getFpsAllocationCommodityDetailDtos() != null) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                    String syncDate = dateFormat.format(fpsAllotmentRequestDto.getLastModifiedDate());
                                    FPSDBHelper.getInstance(SyncPageActivity.this).updateMaserData("allocationSyncTime", syncDate);
                                    FPSDBHelper.getInstance(SyncPageActivity.this).insert_stock_allocation(fpsAllotmentRequestDto.getFpsAllocationCommodityDetailDtos(), "FirstSync");
                                }
                            } else {
                                status = "Failure";
                                try {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(fpsAllotmentRequestDto.getStatusCode()));
                                    if (messageData == null) {
                                        messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(4000));
                                    }
                                } catch (Exception e) {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(4000));
                                }
                            }
                            // update response into local db
                            if (primaryId != -1) {
                                FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory(responseData, messageData, status, primaryId);
                            }
                        }
                    } else if (response != null && response.getStatusLine() != null && ((response.getStatusLine().getStatusCode() == 403) || (response.getStatusLine().getStatusCode() == 401))) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory("", "Unauthorized", "Failure", primaryId);
                        }
                    } else if (response != null && response.getStatusLine() != null) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", primaryId);
                        }
                    } else if (response == null) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory("", "Response is null", "Failure", primaryId);
                        }
                    }
                } catch (Exception e) {
                }
                return "";
            } catch (Exception e) {
                Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "secondAllocationCall", "Network exception" + e.toString());
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "secondAllocationCall called Exception " + e);
                Log.e("SyncPageActivity", e.toString(), e);
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "secondAllocationCall >> Exception " + e);
                }
            }
            return null;
        }

        protected void onPostExecute(HttpResponse response) {

        }
    }

    private String getRequestData2() {
        FpsAllotmentRequestDto fpsRequest = new FpsAllotmentRequestDto();
        Calendar cal =  Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String previousMonth  = new SimpleDateFormat("MMM").format(cal.getTime());
        String previousYear  = new SimpleDateFormat("yyyy").format(cal.getTime());
        fpsRequest.setMonth(previousMonth.toUpperCase());
        fpsRequest.setYear(Integer.valueOf(previousYear));
        fpsRequest.setFinalizedStatus(true);
        fpsRequest.setFpsId(getFpsId());
        try {
            String syncTime  = new SimpleDateFormat("yyyy-MM-").format(cal.getTime());
            fpsRequest.setLastSyncTime(syncTime+"01 00:00:00.000");
        }
        catch(Exception e) {}
        String reqData = new Gson().toJson(fpsRequest);
        return reqData;
    }

    private class thirdAllocationCall extends AsyncTask<String, String, String> {
        BufferedReader in = null;
        long primaryId = -1;

        @Override
        protected String doInBackground(String... f_url) {
            try {
                String url = serverUrl + "/fps/allotment/fetch";
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "thirdAllocationCall called  url ->" + url);
                URI website = new URI(url);
                String requestData = getRequestData3();
                Log.e("SyncPageActivity", "thirdAllocationCall requestData..." + requestData);
                // inserting request into local db
                primaryId = FPSDBHelper.getInstance(SyncPageActivity.this).insertBackgroundProcessHistory(requestData, "thirdAllocationCall");
                StringEntity entity = new StringEntity(requestData, HTTP.UTF_8);
                HttpResponse response = requestType(website, entity);
                try {
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
                        Log.e("SyncPageActivity", "thirdAllocationCall responseData..." + responseData);
                        if ((responseData != null) && (!responseData.contains("<html>"))) {
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            Gson gson = gsonBuilder.create();
                            FpsAllotmentRequestDto fpsAllotmentRequestDto = gson.fromJson(responseData, FpsAllotmentRequestDto.class);
                            String messageData = "";
                            String status = "";
                            if (fpsAllotmentRequestDto.getStatusCode() == 0) {
                                status = "Success";
                                if (fpsAllotmentRequestDto.getFpsAllocationCommodityDetailDtos() != null) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                                    String syncDate = dateFormat.format(fpsAllotmentRequestDto.getLastModifiedDate());
                                    FPSDBHelper.getInstance(SyncPageActivity.this).updateMaserData("allocationSyncTime", syncDate);
                                    FPSDBHelper.getInstance(SyncPageActivity.this).insert_stock_allocation(fpsAllotmentRequestDto.getFpsAllocationCommodityDetailDtos(), "FirstSync");
                                }
                            } else {
                                status = "Failure";
                                try {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(fpsAllotmentRequestDto.getStatusCode()));
                                    if (messageData == null) {
                                        messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(4000));
                                    }
                                } catch (Exception e) {
                                    messageData = Util.messageSelection(FPSDBHelper.getInstance(SyncPageActivity.this).retrieveLanguageTable(4000));
                                }
                            }
                            // update response into local db
                            if (primaryId != -1) {
                                FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory(responseData, messageData, status, primaryId);
                            }
                        }
                    } else if (response != null && response.getStatusLine() != null && ((response.getStatusLine().getStatusCode() == 403) || (response.getStatusLine().getStatusCode() == 401))) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory("", "Unauthorized", "Failure", primaryId);
                        }
                    } else if (response != null && response.getStatusLine() != null) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory(String.valueOf(response.getStatusLine().getStatusCode()), "Service unavailable", "Failure", primaryId);
                        }
                    } else if (response == null) {
                        // update response into local db
                        if (primaryId != -1) {
                            FPSDBHelper.getInstance(SyncPageActivity.this).updateBackgroundProcessHistory("", "Response is null", "Failure", primaryId);
                        }
                    }
                } catch (Exception e) {
                }
                return "";
            } catch (Exception e) {
                Util.LoggingQueue(com.omneagate.activity.SyncPageActivity.this, "thirdAllocationCall", "Network exception" + e.toString());
                Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "thirdAllocationCall called Exception " + e);
                Log.e("SyncPageActivity", e.toString(), e);
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                    Util.LoggingQueue(SyncPageActivity.this, "SyncPageActivity ", "thirdAllocationCall >> Exception " + e);
                }
            }
            return null;
        }

        protected void onPostExecute(String response) {
            Button continueButton = (Button) findViewById(R.id.syncContinue);
            continueButton.setVisibility(View.VISIBLE);
            continueButton.setBackgroundColor(Color.parseColor("#00b7be"));
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    syncSuccessCompletion();
                }
            });
        }
    }

    private String getRequestData3() {
        FpsAllotmentRequestDto fpsRequest = new FpsAllotmentRequestDto();
        Calendar calendar = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM");
        fpsRequest.setMonth(formatter.format(calendar.getTime()).toUpperCase());
        fpsRequest.setYear(calendar.get(Calendar.YEAR));
        fpsRequest.setFinalizedStatus(true);
        fpsRequest.setFpsId(getFpsId());
        try {
            String syncTime  = new SimpleDateFormat("yyyy-MM-").format(calendar.getTime());
            fpsRequest.setLastSyncTime(syncTime+"01 00:00:00.000");
        }
        catch(Exception e) {}
        String reqData = new Gson().toJson(fpsRequest);
        return reqData;
    }

    private long getFpsId() {
        long fpsId = 0;
        try {
            LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(SyncPageActivity.this).getFpsUserDetails();
            fpsId = loginResponseDto.getUserDetailDto().getFpsStore().getId();
        }
        catch(Exception e) {}
        return fpsId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if ((retryDialog != null) && retryDialog.isShowing()) {
                retryDialog.dismiss();
            }
        } catch (final Exception e) {}
        finally {
            retryDialog = null;
        }

        try {
            if ((retryFailedDialog != null) && retryFailedDialog.isShowing()) {
                retryFailedDialog.dismiss();
            }
        } catch (final Exception e) {}
        finally {
            retryFailedDialog = null;
        }

        try {
            if ((retryFailedMasterDataDialog != null) && retryFailedMasterDataDialog.isShowing()) {
                retryFailedMasterDataDialog.dismiss();
            }
        } catch (final Exception e) {}
        finally {
            retryFailedMasterDataDialog = null;
        }

        try {
            if ((tableExceptionErrorDialog != null) && tableExceptionErrorDialog.isShowing()) {
                tableExceptionErrorDialog.dismiss();
            }
        } catch (final Exception e) {}
        finally {
            tableExceptionErrorDialog = null;
        }

        try {
            if ((firstSyncFailureDialog != null) && firstSyncFailureDialog.isShowing()) {
                firstSyncFailureDialog.dismiss();
            }
        } catch (final Exception e) {}
        finally {
            firstSyncFailureDialog = null;
        }
    }

}
