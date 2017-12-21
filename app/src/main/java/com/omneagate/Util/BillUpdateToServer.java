package com.omneagate.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.UpdateStockResponseDto;

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

            Util.LoggingQueue(context, "BillUpdateToServer", "sendBillToServer called");
            new BillUpdateToServerAsync().execute(bill);
        } else {
            Util.LoggingQueue(context, "BillUpdateToServer", "Network not available to send bill");
        }
    }

    //Async task to send bill to server
    class BillUpdateToServerAsync extends AsyncTask<TransactionBaseDto, String, String> {

        @Override
        protected String doInBackground(TransactionBaseDto... billData) {
            BufferedReader in = null;
            try {
                String serverUrl = FPSDBHelper.getInstance(context).getMasterData("serverUrl");
                String url = serverUrl + "/transaction/process";
                URI website = new URI(url);
                String bill = new Gson().toJson(billData[0]);
                StringEntity entity = new StringEntity(bill, HTTP.UTF_8);
                Util.LoggingQueue(context, "BillUpdateToServer", "Sending request url-> " + url);
                Util.LoggingQueue(context, "BillUpdateToServer", "Sending request bill dto details -> " + billData[0]);
                Util.LoggingQueue(context, "BillUpdateToServer", "Sending request bill json details -> " + bill);


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
               // Log.e("Response Bill:", responseData);
                Util.LoggingQueue(context, "BillUpdateToServer ", "Received response " + responseData);
                return responseData;
            } catch (Exception e) {
                Util.LoggingQueue(context, "BillUpdateToServer", "Network exception" + e.getMessage());
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
            int timeoutConnection = 15000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    timeoutConnection);
            int timeoutSocket = 15000;
            Log.i("entity", EntityUtils.toString(entity));
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


        @Override
        protected void onPostExecute(String response) {
            try {
                if ((response != null) && (!response.contains("<html>"))) {
                   // Log.e("BillUpdateToSer Result", response);
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    UpdateStockResponseDto updateStock = gson.fromJson(response, UpdateStockResponseDto.class);
                    Util.LoggingQueue(context, "BillUpdateToServer onPostExecute Response ->", response);
                    Util.LoggingQueue(context, "BillUpdateToServer onPostExecute updateStock ->", ""+updateStock);

                    if (updateStock != null && (updateStock.getStatusCode() == 0 || updateStock.getStatusCode() == 5085) && updateStock.getBillDto() != null) {
                        FPSDBHelper.getInstance(context).billUpdate(updateStock.getBillDto());
                    }else{
                        Log.e("onPostExecute()", "Error in updateStock : ->"+updateStock);
                    }
                }
            } catch (Exception e) {
                Log.e("Bill update to server", e.toString(), e);
            }

        }
    }
}