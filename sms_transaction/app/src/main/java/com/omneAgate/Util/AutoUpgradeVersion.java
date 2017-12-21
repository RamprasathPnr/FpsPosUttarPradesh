package com.omneagate.Util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.VersionUpgradeDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.activity.BuildConfig;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.service.HttpClientWrapper;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.File;

/**
 * Created by user1 on 17/3/15.
 */
public class AutoUpgradeVersion {

    //Activity context
    public Activity context;

    ProgressBar progressBar;

    TextView mTvuploadCount;

    //Constructor
    public AutoUpgradeVersion(Activity context) {
        this.context = context;
    }

    // Auto upgrade process
    public void autoUpgradeProcess() {
        progressBar = (ProgressBar) context.findViewById(R.id.autoUpgradeprogressBar);
        mTvuploadCount = (TextView) context.findViewById(R.id.tvUploadCount);
        int ourVersion = versionNumber();
        getRequest(ourVersion);
    }

    // This function returns version number
    private int versionNumber() {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        Log.i("Ver", "Version no" + versionCode + " version Name" + versionName);
        return versionCode;
    }


    private void getRequest(int versionNo) {
        try {

            VersionUpgradeDto versionUpgradeDtoRequest = new VersionUpgradeDto();
            versionUpgradeDtoRequest.setVersion(versionNo);
            Log.i("Request", versionUpgradeDtoRequest.toString());
            String version = new Gson().toJson(versionUpgradeDtoRequest);
            StringEntity se = new StringEntity(version, HTTP.UTF_8);
            HttpClientWrapper httpConnection = new HttpClientWrapper();
            String url = "/versionUpgrade/view";
            httpConnection.sendRequest(url, null, ServiceListenerType.UPGRADE_RESPONSE, SyncHandler, RequestType.POST, se, context);


        } catch (Exception e) {
            Util.LoggingQueue(context, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }


    // After response received from server successfully in android
    public void getUpgradeResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);

            Log.i("Ver", "Version Response" + response);

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Log.e("GOUP", response);

            VersionUpgradeDto versionUpgradeDto = gson.fromJson(response, VersionUpgradeDto.class);
            int statusCode = versionUpgradeDto.getStatusCode();

            if (statusCode == 0) {

                int version = versionUpgradeDto.getVersion();
                int ourVersion = versionNumber();

                if (version > ourVersion) {
                    String title = context.getResources().getString(R.string.app_name);
                    final String path = Environment.getExternalStorageDirectory() + "/" + title + ".apk";
                    Ion.with(context).load("http://192.168.1.53:9095/webapps/fps/FPS.apk")

                            .progressBar(progressBar)

                            .progress(new ProgressCallback() {
                                @Override
                                public void onProgress(long downloaded, long total) {

                                    mTvuploadCount.setText("" + downloaded + " / " + total);
                                    System.out.println("" + downloaded + " / " + total);


                                }
                            })
                            .write(new File(path))
                            .setCallback(new FutureCallback<File>() {
                                @Override
                                public void onCompleted(Exception e, File file) {

                                    Intent i = new Intent();
                                    i.setAction(Intent.ACTION_VIEW);
                                    i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                                    Log.d("Lofting", "About to install new .apk");
                                    context.startActivity(i);
                                }
                            });


                    Log.e("update", "update our version");
                } else {
                    Log.e("dont", "update our version");
                }

            } else

            {
                Util.messageBar(context, FPSDBHelper.getInstance(context).retrieveLanguageTable(versionUpgradeDto.getStatusCode(), GlobalAppState.language).getDescription());
            }
        } catch (Exception e) {
            Util.LoggingQueue(context, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }


    /*Handler used to get response from server*/
    private final Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ServiceListenerType type = (ServiceListenerType) msg.obj;
            switch (type) {
                case UPGRADE_RESPONSE:
                    getUpgradeResponse(msg.getData());
                    break;

                default:
                    Log.e("Error", "Auto Upgradation");
                    break;
            }
        }

    };

}
