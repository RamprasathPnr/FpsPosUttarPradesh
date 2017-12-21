package com.omneagate.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.LoginDto;
import com.omneagate.DTO.LoginHistoryDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkUtil;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created for network change
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    String serverUrl = "";
    StringEntity stringEntity;
    Context context;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.e("NetworkChangeReceiver", "onReceive() called ");
        int status = NetworkUtil.getConnectivityStatus(context);


        if (status != 0) {


            Log.e("NetworkChangeReceiver", "onReceive() ,  network is present");

            if (GlobalAppState.localLogin) {


                Log.e("NetworkChangeReceiver", "onReceive() ,GlobalAppState localLogin Details = "+GlobalAppState.localLogin);

                this.context = context;
                try {



                    serverUrl = FPSDBHelper.getInstance(context).getMasterData("serverUrl");
                    LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(context).getUserDetails(SessionId.getInstance().getUserId());

                    Log.e("NetworkChangeReceiver", "onReceive() , LoginResponseDto = "+loginResponseDto);

                    LoginDto loginCredentials = new LoginDto();
                    loginCredentials.setUserName(loginResponseDto.getUserDetailDto().getUserId());
                    loginCredentials.setPassword(Util.DecryptPassword(loginResponseDto.getUserDetailDto().getEncryptedPassword()));
                    loginCredentials.setDeviceId(Settings.Secure.getString(
                            context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                    String login = new Gson().toJson(loginCredentials);

                    Log.e("NetworkChangeReceiver", "onReceive() , LoginResponseDto Gson  = "+login);

                    stringEntity = new StringEntity(login, HTTP.UTF_8);
                    new BackgroundSuccess().execute("");


                } catch (Exception e) {

                    Log.e("NetworkChangeReceiver", "onReceive() ,Exception = "+e);

                }
            }else{

                Log.e("NetworkChangeReceiver", "onReceive() , No GlobalAppState localLogin!!!!  ");


            }
        }else{
            Log.e("NetworkChangeReceiver", "onReceive() , No Network Sorry ");



        }
    }

    /*return http POST method using parameters*/
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {

        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 15000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
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

    /**
     * Async   task for Download Sync for data in table
     */
    private class BackgroundSuccess extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... f_url) {
            BufferedReader in = null;
            try {


                Log.e("NetworkChangeReceiver", "BackgroundSuccess called ");


                String url = serverUrl + "/login/validateuser";
                Log.e("NetworkChangeReceiver", "BackgroundSuccess called url = "+url);

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

                Log.e("NetworkChangeReceiver", "BackgroundSuccess called Response  = "+sb.toString());

                return sb.toString();
            } catch (Exception e) {

                Log.e("NetworkChangeReceiver", "BackgroundSuccess called Exception = "+e);

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
            if ((response != null) && (!response.contains("<html>")) && !response.contains("timestamp")) {
                try {

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    LoginResponseDto loginResponse = gson.fromJson(response, LoginResponseDto.class);

                    Log.e("NetworkChangeReceiver", "BackgroundSuccess called  LoginResponseDto  = "+loginResponse);

                    if(loginResponse.isAuthenticationStatus()) {

                        Log.e("NetworkChangeReceiver", "BackgroundSuccess Authendicated  = "+loginResponse.isAuthenticationStatus());

                        try {

                            SessionId.getInstance().setSessionId(loginResponse.getSessionid());
//                            context.startService(new Intent(context, ConnectionHeartBeat.class));
//                            context.startService(new Intent(context, OfflineCloseSaleManager.class));
//                            context.startService(new Intent(context, UpdateDataService.class));
//                            context.startService(new Intent(context, RemoteLoggingService.class));
//                            context.startService(new Intent(context, OfflineTransactionManager.class));
//                            context.startService(new Intent(context, OfflineInwardManager.class));
//                            context.startService(new Intent(context, StatisticsService.class));
//                            context.startService(new Intent(context, LoginHistoryService.class));
//                            context.startService(new Intent(context, SyncExcUpdateToServer.class));
//                            context.startService(new Intent(context, SyncProcessedAdvanceStock.class));
//                            context.startService(new Intent(context, OfflineReportAckService.class));
                            GlobalAppState.localLogin = false;
                            LoginHistoryDto history = new LoginHistoryDto();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                            history.setLoginTime(df.format(new Date()));
                            history.setLoginType("BACKGROUND_LOGIN");
                            if (loginResponse.getUserDetailDto() != null) {
                                history.setUserId(loginResponse.getUserDetailDto().getId());
                                if (loginResponse.getUserDetailDto().getFpsStore() != null)
                                    history.setFpsId(loginResponse.getUserDetailDto().getFpsStore().getId());
                            }


                            FPSDBHelper.getInstance(context).insertBackGroundLoginHistory(history);
                        }catch (Exception e){
                            Log.e("NetworkChangeReceiver", "BackgroundSuccess Authendicated Exception  = "+e);

                        }


                    }else{


                        Log.e("NetworkChangeReceiver", "BackgroundSuccess , Not Authendicated  Password Changed.Try to Relogin ");



                        Toast.makeText(context,"Password Changed.Try to Relogin",Toast.LENGTH_LONG).show();
                       /* Intent i = new Intent();
                        i.setClassName("com.omneagate.com.omneagate.com.omneagate.activity", "com.omneagate.activity.LoginActivity");
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);*/
                    }
                } catch (Exception e) {

                    Log.e("NetworkChangeReceiver", "BackgroundSuccess Response processing  Exception  = "+e);

                }
            }else{

                Log.e("NetworkChangeReceiver", "BackgroundSuccess   Response is null from server = ");


            }
        }
    }
}