package com.omneagate.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.omneagate.DTO.LoggingDto;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Remote logging service class
 */
public class RemoteLoggingService extends Service {

    Timer remoteLogTimer;
    RemoteLogTimerTask remoteLogTimerTask;
    private GlobalAppState appState;//Application variable
    private String serverUrl = "";

    public void onCreate() {

        appState = (GlobalAppState) getApplication();
        serverUrl = FPSDBHelper.getInstance(this).getMasterData("serverUrl");

        remoteLogTimer = new Timer();
        remoteLogTimerTask = new RemoteLogTimerTask();
        Long timerWaitTime = 300l;
        remoteLogTimer.schedule(remoteLogTimerTask, 0, timerWaitTime);
    }

    @Override
    public void onDestroy() {
        try {
            if (remoteLogTimer != null) {
                remoteLogTimer.cancel();
                remoteLogTimer = null;
            }
        } catch (Exception e) {
            Log.e("Remote log ", "Error in Service", e);
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    class RemoteLogTimerTask extends TimerTask {

        @Override
        public void run() {
            while (!appState.queue.isEmpty()) {
                NetworkConnection network = new NetworkConnection(com.omneagate.service.RemoteLoggingService.this);
                if (network.isNetworkAvailable() && SessionId.getInstance().getSessionId().length() > 0)
                    try {
                        new RemoteLoggingAsync().execute(appState.queue.dequeue());
                    } catch (Exception e) {
                        Log.e("Exception", e.toString(), e);
                    }
            }
        }

    }

    //Remote logging service server async task
    class RemoteLoggingAsync extends AsyncTask<LoggingDto, String, String> {
        @Override
        protected String doInBackground(LoggingDto... logs) {
            BufferedReader in = null;
            try {
                String url = serverUrl + "/remoteLogging/addLog";
                URI website = new URI(url);
                String bill = new Gson().toJson(logs[0]);
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
                return responseData;
            } catch (Exception e) {
                Util.LoggingQueue(com.omneagate.service.RemoteLoggingService.this, "Error", "Network exception" + e.getMessage());
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                }
            }
            return null;
        }

        /*return http POST method using parameters*/
        private HttpResponse requestType(URI website, StringEntity entity) throws IOException {

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

        @Override
        protected void onPostExecute(String file_url) {

        }
    }


}