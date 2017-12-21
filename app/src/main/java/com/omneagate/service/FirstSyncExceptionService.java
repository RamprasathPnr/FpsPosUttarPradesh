package com.omneagate.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.POSSyncExceptionDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.LoginActivity;
import com.omneagate.activity.SyncPageActivity;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class FirstSyncExceptionService extends Service {

    List<POSSyncExceptionDto> posSyncExceptionDtoList;
    String localId;

    @Override
    public void onCreate() {
        super.onCreate();

        Util.LoggingQueue(this, "FirstSyncExceptionService", "onCreate() called");
        try {
            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "SyncExcTimerTask called , Started to send FirstSyncExceptionService message");
            posSyncExceptionDtoList = new ArrayList<>();
            posSyncExceptionDtoList = FPSDBHelper.getInstance(com.omneagate.service.FirstSyncExceptionService.this).getAllSyncExcData();
            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "SyncExcTimerTask , No of Execptions in PosSyn " + posSyncExceptionDtoList.size());
            NetworkConnection network = new NetworkConnection(com.omneagate.service.FirstSyncExceptionService.this);
            if (network.isNetworkAvailable()) {
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "SyncExcTimerTask , NetworkAvailable ");
                updateToServer();
            }
            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "SyncExcTimerTask , End of FirstSyncExceptionService message ");
        } catch (Exception e) {
            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "SyncExcTimerTask , syncExc UpdateToServer Error ");
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            Util.LoggingQueue(this, "FirstSyncExceptionService", "onDestroy() called");
        } catch (Exception e) {
            Util.LoggingQueue(this, "FirstSyncExceptionService", "onDestroy() Exception = " + e);
        }
    }

    /*return http POST method using parameters*/
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {
        Util.LoggingQueue(this, "FirstSyncExceptionService", "requestType() called");
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 15000;
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
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

    private void updateToServer() {
        try {
            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "updateToServer() called");
            new excUpdateToServerAsync().execute(posSyncExceptionDtoList.get(0));
        } catch (Exception e) {
            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "updateToServer() Exception = " + e);
        }
    }

    // Async task to send exc to server
    class excUpdateToServerAsync extends AsyncTask<POSSyncExceptionDto, String, String> {
        @Override
        protected String doInBackground(POSSyncExceptionDto... syncExcData) {
            BufferedReader in = null;
            try {
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync called");
                localId = "" + syncExcData[0].getLocalId();
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync localId = " + localId);
                String serverUrl = FPSDBHelper.getInstance(com.omneagate.service.FirstSyncExceptionService.this).getMasterData("serverUrl");
                String url = serverUrl + "/possyncexception/addPosSyncException";
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync url = " + url);
                URI website = new URI(url);
                String syncExcStr = new Gson().toJson(syncExcData[0]);
                StringEntity entity = new StringEntity(syncExcStr, HTTP.UTF_8);
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync Sending request exc = " + syncExcData[0]);
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
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync responseData = " + responseData);
                return responseData;
            } catch (Exception e) {
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync Exception = " + e);
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {}
            }
            return null;
        }

        /*return http GET,POST and PUT method using parameters*/
        private HttpResponse requestType(URI website, StringEntity entity) throws IOException {
            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "requestType() called ");
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 15000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
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
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync onPostExecute  response = " + response);
                if ((response != null) && (!response.contains("<html>"))) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    POSSyncExceptionDto posSyncExceptionDto = gson.fromJson(response, POSSyncExceptionDto.class);
                    if ((posSyncExceptionDto != null) && (posSyncExceptionDto.getStatusCode() == 0)) {
                        Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync onPostExecute  localId = " + localId);
                        FPSDBHelper.getInstance(com.omneagate.service.FirstSyncExceptionService.this).updateSyncExcData(posSyncExceptionDto, localId);
                        posSyncExceptionDtoList.remove(0);
                        if (posSyncExceptionDtoList.size() > 0) {
                            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync onPostExecute  Sending  = " + posSyncExceptionDtoList);
                            updateToServer();
                        } else {
                            Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync onPostExecute  completed............  = ");
                        }
                    }
                }
                FPSDBHelper.getInstance(FirstSyncExceptionService.this).deleteAllRecordsInAllTables();
                FPSDBHelper.getInstance(FirstSyncExceptionService.this).closeConnection();
                Intent intent = new Intent(FirstSyncExceptionService.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                FirstSyncExceptionService.this.stopSelf();
            } catch (Exception e) {
                Util.LoggingQueue(FirstSyncExceptionService.this, "FirstSyncExceptionService", "excUpdateToServerAsync onPostExecute  Exception  = " + e);
            }
        }
    }
}
