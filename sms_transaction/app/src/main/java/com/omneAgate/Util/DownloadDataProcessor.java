package com.omneagate.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.FpsDataDto;
import com.omneagate.DTO.FpsRequestDataDto;
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
 * Used to download master data
 */
public class DownloadDataProcessor {


    //Activity context
    private final Context context;

    StringEntity stringEntity;

    //Constructor
    public DownloadDataProcessor(Context context) {
        this.context = context;
    }


    // Send Request to the server
    public void processor() {
        try {
            SharedPreferences pref = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
            String lastModifiedDate = pref.getString("lastModified", null);
            Log.e("Resp", "Last Modified Date:" + lastModifiedDate);
            FpsRequestDataDto fpsRequest = new FpsRequestDataDto();
            fpsRequest.setDeviceid(GlobalAppState.deviceId);
            fpsRequest.setLastSyncDate(lastModifiedDate);
            String updateData = new Gson().toJson(fpsRequest);
            stringEntity = new StringEntity(updateData, HTTP.UTF_8);
            new UpdateSyncTask().execute("");

        } catch (Exception e) {
            Util.LoggingQueue(context, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }
    }

    // After response received from server successfully in android
    private void processSyncResponseData(String response) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            FpsDataDto fpsDataDto = gson.fromJson(response, FpsDataDto.class);
            int statusCode = fpsDataDto.getStatusCode();
            if (statusCode == 0) {
                Log.e("Resp", "Last Modified Date:" + fpsDataDto.getLastSyncTime());
                SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("lastModified", fpsDataDto.getLastSyncTime());
                editor.commit();
                FPSDBHelper.getInstance(context).setSyncData(fpsDataDto);
            }
        } catch (Exception e) {
            Util.LoggingQueue(context, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }


    //Async   task for Download Sync
    private class UpdateSyncTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... f_url) {
            BufferedReader in = null;
            try {
                String url = GlobalAppState.serverUrl + "/transaction/fpsData";
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
                String responseData = sb.toString();
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


        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                processSyncResponseData(response);
            }
        }
    }

    /*return http POST method using parameters*/
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {

        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 50000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
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


}