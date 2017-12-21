package com.omneagate.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.omneagate.DTO.HeartBeatDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
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


/**
 * Service for checking connection of server
 */
public class ConnectionHeartBeat extends Service implements LocationListener {

    private int batteryLevel = 0; //Battery level variable
    HeartBeatDto heartBeat;
    LocationManager locationManager;
    TransactionBaseDto base;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        heartBeat = new HeartBeatDto();
        base = new TransactionBaseDto();
        Thread notifyingThread = new Thread(null, serviceTask, "ConnectionHeartBeat");
        notifyingThread.start();
        Util.LoggingQueue(ConnectionHeartBeat.this, "Info", "Service  HeartBeat created");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*Connection device Thread*/
    private final Runnable serviceTask = new Runnable() {
        public void run() {
            Looper.prepare();
            while (true) {
                try {
                    Util.LoggingQueue(ConnectionHeartBeat.this, "Info", "Started to send HeartBeat message ");
                    if (SessionId.getInstance().getSessionId().length() > 0) {
                        Criteria criteria = new Criteria();
                        String provider = locationManager.getBestProvider(criteria, false);
                        Location location = locationManager.getLastKnownLocation(provider);
                        if (location != null) {
                            //TODO:
                        }
                        NetworkConnection network = new NetworkConnection(ConnectionHeartBeat.this);
                        if (network.isNetworkAvailable())
                            new ConnectionTestHearBeat().execute("");
                    } else {
                        Util.LoggingQueue(ConnectionHeartBeat.this, "Error", "Session is null or zero");
                    }
                    long timeout = Long.parseLong(getString(R.string.serviceTimeout));
                    Util.LoggingQueue(ConnectionHeartBeat.this, "Info", "End of HeartBeat message ");
                    Thread.sleep(timeout);
                    Util.LoggingQueue(ConnectionHeartBeat.this, "Info", "Waked up to send HeartBeat message ");
                } catch (Exception e) {
                    Util.LoggingQueue(ConnectionHeartBeat.this, "Error", e.getMessage());
                }
            }
        }

    };

    @Override
    public void onLocationChanged(Location location) {

        //txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    //Broadcast receiver for battery
    private final BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            context.unregisterReceiver(this);
            int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (currentLevel >= 0 && scale > 0) {
                batteryLevel = (currentLevel * 100) / scale;
                Log.e("Battry", currentLevel + ":" + scale + ":" + batteryLevel);
                if (batteryLevel <= Integer.parseInt(getString(R.string.thresholdLimit))) {
                    heartBeat.setBatteryLevel(batteryLevel);
                }
            }
        }
    };


    //Async   task for connection heartbeat
    private class ConnectionTestHearBeat extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... f_url) {
            BufferedReader in = null;
            try {
                String url = GlobalAppState.serverUrl + "/transaction/process";
                URI website = new URI(url);
                Log.e("Url", url);
                GlobalAppState appState = (GlobalAppState) getApplication();
                heartBeat.setFpsId(appState.getFpsId() + "");
                base.setBaseDto(heartBeat);
                base.setType("com.omneagate.rest.dto.HeartBeatRequestDto");
                base.setTransactionType(TransactionTypes.HEART_BEAT);
                Util.LoggingQueue(ConnectionHeartBeat.this, "Info", "Request message " + base);
                String heartBeatData = new Gson().toJson(base);
                StringEntity entity = new StringEntity(heartBeatData, HTTP.UTF_8);
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
                Util.LoggingQueue(ConnectionHeartBeat.this, "Info", "Response message " + responseData);
                return responseData;
            } catch (Exception e) {
                Util.LoggingQueue(ConnectionHeartBeat.this, "Error", "Network exception" + e.getMessage());
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


        // Once Music File is downloaded
        @Override
        protected void onPostExecute(String file_url) {

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
