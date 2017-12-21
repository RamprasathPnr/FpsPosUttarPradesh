package com.omneagate.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;

import com.google.gson.Gson;
import com.omneagate.DTO.LoggingDto;
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

/**
 * Remote logging service class
 */
public class RemoteLoggingService extends Service {

    private GlobalAppState appState;//Application variable

    public void onCreate() {

        appState = (GlobalAppState) getApplication();
        Thread notifyingThread = new Thread(null, remoteTask, "ServerService");
        notifyingThread.start();
    }

    //Thread for remote logging
    private final Runnable remoteTask = new Runnable() {
        public void run() {
            Looper.prepare();
            while (true) {
                try {
                    while (!appState.queue.isEmpty()) {
                        NetworkConnection network = new NetworkConnection(RemoteLoggingService.this);
                        if (network.isNetworkAvailable() && SessionId.getInstance().getSessionId().length() > 0)
                            new RemoteLoggingAsync().execute(appState.queue.dequeue());
                    }
                    long timeout = 300;
                    Thread.sleep(timeout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //Remote logging service server async task
    class RemoteLoggingAsync extends AsyncTask<LoggingDto, String, String> {
        @Override
        protected String doInBackground(LoggingDto... logs) {
            BufferedReader in = null;
            try {
                String url = GlobalAppState.serverUrl + "/remoteLogging/addLog";
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
                Util.LoggingQueue(RemoteLoggingService.this, "Error", "Network exception" + e.getMessage());
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
        protected void onPostExecute(String file_url) {

        }
    }
}