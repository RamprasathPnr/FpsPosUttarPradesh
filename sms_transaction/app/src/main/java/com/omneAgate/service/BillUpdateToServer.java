package com.omneagate.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.UpdateStockResponseDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Used to send bill to server in background
 */
public class BillUpdateToServer {

    private final Context context; //Context for activity

    //    Constructor
    public BillUpdateToServer(Context context) {
        this.context = context;
    }

    /*Bill sending to server*/
    public void sendBillToServer(TransactionBaseDto bill) {
        NetworkConnection network = new NetworkConnection(context);
        if (network.isNetworkAvailable()) {
            new BillUpdateToServerAsync().execute(bill);

        } else {
            Util.LoggingQueue(context, "Error", "Network not available to send bill");
        }
    }

    //Async task to send bill to server
    class BillUpdateToServerAsync extends AsyncTask<TransactionBaseDto, String, String> {

        @Override
        protected String doInBackground(TransactionBaseDto... billData) {
            BufferedReader in = null;
            try {
                String url = GlobalAppState.serverUrl + "/transaction/process";
                URI website = new URI(url);
                String bill = new Gson().toJson(billData[0]);
                StringEntity entity = new StringEntity(bill, HTTP.UTF_8);
                Util.LoggingQueue(context, "Info", "Sending request bill" + billData[0]);
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
                Util.LoggingQueue(context, "Info", "Received response " + responseData);
                return responseData;
            } catch (Exception e) {
                Util.LoggingQueue(context, "Error", "Network exception" + e.getMessage());
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

        /*return http GET,POST and PUT method using parameters*/
        private HttpResponse requestType(URI website, StringEntity entity) throws IOException {

            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 50000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 50000;
            Log.e("entity", EntityUtils.toString(entity));
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
        protected void onPostExecute(String response) {
            try {
                if (response != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    UpdateStockResponseDto updateStock = gson.fromJson(response, UpdateStockResponseDto.class);
                    Log.e("Response", updateStock + "");
                    if (updateStock != null && updateStock.getStatusCode() == 0 && updateStock.getBillDto() != null) {
                        FPSDBHelper.getInstance(context).billUpdate(updateStock.getBillDto());
                    }
                }
            } catch (Exception e) {
                Log.e("Error", e.toString(), e);
            }

        }
    }
}