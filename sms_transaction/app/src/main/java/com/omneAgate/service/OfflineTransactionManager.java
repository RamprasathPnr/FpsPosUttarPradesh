package com.omneagate.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.DTO.UpdateStockResponseDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * Offline data sync to server service
 */
public class OfflineTransactionManager extends Service {

    List<BillDto> billList; //list of bills in offline

    @Override
    public void onCreate() {
        super.onCreate();
        Thread notifyingThread = new Thread(null, serviceTask, "OfflineTransaction");
        notifyingThread.start();
        Log.e("check", "Inside Bills");
        Util.LoggingQueue(OfflineTransactionManager.this, "Info", "Service OfflineTransactionManager created ");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*Offline sync bill Thread*/
    private final Runnable serviceTask = new Runnable() {
        public void run() {
            Looper.prepare();
            while (true) {
                try {
                    Util.LoggingQueue(OfflineTransactionManager.this, "Info", "Starting up");
                    billList = new ArrayList<BillDto>();
                    billList = FPSDBHelper.getInstance(OfflineTransactionManager.this).getAllBillsForSync();
                    Log.e("Data", "bills:" + billList.size());
                    Util.LoggingQueue(OfflineTransactionManager.this, "Info", "Retrieved number of unsent bills [" + billList.size() + "]");
                    NetworkConnection network = new NetworkConnection(OfflineTransactionManager.this);
                    if (network.isNetworkAvailable()) {
                        for (BillDto bill : billList) {
                            Util.LoggingQueue(OfflineTransactionManager.this, "Info", "Processing bill id" + bill.getTransactionId());
                            syncBillsToServer(bill);
                        }
                    }
                    long timeout = Long.parseLong(getString(R.string.serviceTimeout));
                    Thread.sleep(timeout);
                    Util.LoggingQueue(OfflineTransactionManager.this, "Info", "Waking up");
                } catch (Exception e) {
                    Util.LoggingQueue(OfflineTransactionManager.this, "Error", e.getMessage());
                }
            }
        }

    };

    /**
     * Check bill size
     * <p/>
     * Async task to call bills
     */
    private void syncBillsToServer(BillDto bill) {

        TransactionBaseDto base = new TransactionBaseDto();
        base.setTransactionType(TransactionTypes.SALE_QR_OTP_DISABLED);
        bill.setMode('O');
        bill.setChannel('G');
        UpdateStockRequestDto updateStock = new UpdateStockRequestDto();
        updateStock.setReferenceId(bill.getTransactionId());
        updateStock.setDeviceId(Settings.Secure.getString(
                OfflineTransactionManager.this.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
        updateStock.setUfc(bill.getUfc());
        updateStock.setBillDto(bill);
        base.setBaseDto(updateStock);
        base.setType("com.omneagate.rest.dto.QRRequestDto");
        new OfflineDataSyncTask().execute(base);

    }

    //Async   task for connection heartbeat
    private class OfflineDataSyncTask extends AsyncTask<TransactionBaseDto, String, String> {

        @Override
        protected String doInBackground(TransactionBaseDto... billData) {
            BufferedReader in = null;
            try {
                String url = GlobalAppState.serverUrl + "/transaction/process";
                URI website = new URI(url);
                Log.e("Url", url);
                Log.e("Bills", billData[0].toString());
                String bill = new Gson().toJson(billData[0]);
                StringEntity entity = new StringEntity(bill, HTTP.UTF_8);
                HttpResponse response = requestType(website, entity);
                in = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                in.close();
                String responseData = sb.toString();
                Log.e("Response", responseData);
                return responseData;
            } catch (Exception e) {
                Util.LoggingQueue(OfflineTransactionManager.this, "Error", "Network exception" + e.getMessage());
                Log.e("Error: ", e.toString());
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                UpdateStockResponseDto updateStock = gson.fromJson(response, UpdateStockResponseDto.class);
                if (updateStock != null && updateStock.getStatusCode() == 0 && updateStock.getBillDto() != null) {
                    FPSDBHelper.getInstance(OfflineTransactionManager.this).billUpdate(updateStock.getBillDto());
                    Util.LoggingQueue(OfflineTransactionManager.this, "Info", "Updating status of bill to status T for bill id " + updateStock.getBillDto().getTransactionId());
                } else {
                    Util.LoggingQueue(OfflineTransactionManager.this, "Error", "Received null response ");
                }
            }
        }
    }

    /*return http POST method using parameters*/
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {

        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 50000;
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        int timeoutSocket = 50000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpPost postRequest = new HttpPost();
        postRequest.setURI(website);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setHeader("Cookie", "JSESSIONID=" + SessionId.getInstance().getSessionId());
        postRequest.setEntity(entity);
        return client.execute(postRequest);
    }

    @Override
    public void onDestroy() {

    }
}
