package com.omneagate.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.StockReqBaseDto;
import com.omneagate.DTO.StockRequestDto;
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
public class OfflineInwardManager extends Service {

    List<StockRequestDto> stockRequestDtoList; //list of bills in offline
    long inwardKey;

    @Override
    public void onCreate() {
        super.onCreate();
        Thread notifyingThread = new Thread(null, serviceTask, "OfflineTransactionInward");
        notifyingThread.start();
        Util.LoggingQueue(OfflineInwardManager.this, "Info", "Service OfflineTransactionInward created ");
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
                    Util.LoggingQueue(OfflineInwardManager.this, "Info", "Starting up");
                    stockRequestDtoList = new ArrayList<StockRequestDto>();
                    stockRequestDtoList = FPSDBHelper.getInstance(OfflineInwardManager.this).getAllStockSync();
                    Log.e("DataSRequest", "bills:" + stockRequestDtoList.size());
                    Util.LoggingQueue(OfflineInwardManager.this, "Info", "Retrieved number of unsent StockRequest [" + stockRequestDtoList.size() + "]");

                    NetworkConnection network = new NetworkConnection(OfflineInwardManager.this);
                    if (network.isNetworkAvailable()) {
                        for (StockRequestDto stockRequest : stockRequestDtoList) {
                            int retryCount = FPSDBHelper.getInstance(OfflineInwardManager.this).readManualStockInward(stockRequest.getInwardKey());
                            if (retryCount > 0) {
                                retryCount++;
                                FPSDBHelper.getInstance(OfflineInwardManager.this).updateStockInward(stockRequest.getInwardKey(), retryCount);
                            } else if (retryCount == 20) {
                                FPSDBHelper.getInstance(OfflineInwardManager.this).updateStockInwardDeclined(stockRequest.getInwardKey());
                            }
                            syncBillsToServer(stockRequest);
                            Util.LoggingQueue(OfflineInwardManager.this, "Info", "Processing bill id " + stockRequest.getDate());
                        }
                    }
                    long timeout = Long.parseLong(getString(R.string.serviceTimeout));
                    Thread.sleep(timeout);
                    Util.LoggingQueue(OfflineInwardManager.this, "Info", "Waking up");
                } catch (Exception e) {
                    Util.LoggingQueue(OfflineInwardManager.this, "Error", e.getMessage());
                }
            }
        }

    };

    /**
     * Check bill size
     * <p/>
     * Async task to call bills
     */
    private void syncBillsToServer(StockRequestDto stockRequestDto) {
        StockReqBaseDto stockReqBaseDto = new StockReqBaseDto();
        stockReqBaseDto.setType("com.omneagate.rest.dto.StockRequestDto");
        stockReqBaseDto.setBaseDto(stockRequestDto);
        new OfflineDataSyncTask().execute(stockReqBaseDto);

    }

    //Async   task for connection offline Inward
    private class OfflineDataSyncTask extends AsyncTask<StockReqBaseDto, String, String> {

        @Override
        protected String doInBackground(StockReqBaseDto... billData) {
            BufferedReader in = null;
            try {
                String url = GlobalAppState.serverUrl + "/fpsStock/inward";
                URI website = new URI(url);
                Log.e("Url", url);
                Log.e("Stocks ", billData[0].toString());
                String stocks = new Gson().toJson(billData[0]);
                StringEntity entity = new StringEntity(stocks, HTTP.UTF_8);
                HttpResponse response = requestType(website, entity);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
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
                Util.LoggingQueue(OfflineInwardManager.this, "Error", "Network exception" + e.getMessage());
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
                StockRequestDto stockRequestDto = gson.fromJson(response, StockRequestDto.class);
                if (stockRequestDto != null && stockRequestDto.getStatusCode() == 0 && stockRequestDto.getInwardKey() != 0) {
                    FPSDBHelper.getInstance(OfflineInwardManager.this).updateStockInward(stockRequestDto.getInwardKey(), 0);
                    Util.LoggingQueue(OfflineInwardManager.this, "Info", "Updating status of Manual Inward to status T for inwardKey " + stockRequestDto.getInwardKey());

                } else {
                    Util.LoggingQueue(OfflineInwardManager.this, "Error", "Received null response ");
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
