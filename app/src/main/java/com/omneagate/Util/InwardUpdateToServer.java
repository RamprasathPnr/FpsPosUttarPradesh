package com.omneagate.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.StockReqBaseDto;
import com.omneagate.DTO.StockRequestDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;

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
public class InwardUpdateToServer {

    private final Context context; //Context for activity

    //    Constructor
    public InwardUpdateToServer(Context context) {
        this.context = context;
    }

    /*Bill sending to server*/
    public void sendBillToServer(String referenceNumber) {
        NetworkConnection network = new NetworkConnection(context);
        if (network.isNetworkAvailable()) {
            StockReqBaseDto stockReqBaseDto = new StockReqBaseDto();
            stockReqBaseDto.setType("com.omneagate.rest.dto.StockRequestDto");
            stockReqBaseDto.setBaseDto(FPSDBHelper.getInstance(context).getAllStockSync(referenceNumber));
            new BillUpdateToServerAsync().execute(stockReqBaseDto);
        } else {
            Util.LoggingQueue(context, "Error", "Network not available to send Stock");
        }
    }

    //Async task to send bill to server
    class BillUpdateToServerAsync extends AsyncTask<StockReqBaseDto, String, String> {

        @Override
        protected String doInBackground(StockReqBaseDto... billData) {
            BufferedReader in = null;
            try {
                String serverUrl = FPSDBHelper.getInstance(context).getMasterData("serverUrl");
                String url = serverUrl + "/fpsStock/inward";
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
                Log.e("Response Bill:", responseData);
                Util.LoggingQueue(context, "Info", "Received response " + responseData);
                return responseData;
            } catch (Exception e) {
                Util.LoggingQueue(context, "Error", "Network exception" + e.getMessage());
                Log.e("Error in bill update: ", e.toString());
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
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    StockRequestDto stockRequestDto = gson.fromJson(response, StockRequestDto.class);
                    if (stockRequestDto != null && stockRequestDto.getStatusCode() == 0 && stockRequestDto.getReferenceNo() != null) {
                        FPSDBHelper.getInstance(context).updateStockInward(stockRequestDto.getReferenceNo());
                        Util.LoggingQueue(context, "Info", "Updating status of Manual Inward to status T for inwardKey " + stockRequestDto.getInwardKey());
                    } else {
                        Util.LoggingQueue(context, "Error", "Received null response ");
                    }
                }

            } catch (Exception e) {
                Log.e("Bill update to server", e.toString(), e);
            }

        }
    }
}