package com.omneagate.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.VersionUpgradeDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.Future;

//Auto Upgradation of apk file
public class AutoUpgrationActivity extends BaseActivity {

    //Downloading the progressbar
    private ProgressBar progressBar;

    // Download percentage
    private TextView tvUploadCount;

    //apk file
    private Future<File> downloadingFileFuture;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_auto_upgradation_version);
        appState = (GlobalAppState) getApplication();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        progressBar = (ProgressBar) findViewById(R.id.autoUpgradeprogressBar);
        progressBar.setVisibility(View.INVISIBLE);
        tvUploadCount = (TextView) findViewById(R.id.tvUploadCount);


        int ourVersion = versionNumber();
        getRequest(ourVersion);


        ((TextView) findViewById(R.id.tvDownloadTitle)).setVisibility(View.INVISIBLE);
        getRequest(versionNumber());

    }


    // This function returns version number
    private int versionNumber() {
        int versionCode = BuildConfig.VERSION_CODE;
        return versionCode;
    }


    // Request to the server
    private void getRequest(int versionNo) {
        try {
            if (networkConnection.isNetworkAvailable()) {
                VersionUpgradeDto versionUpgradeDtoRequest = new VersionUpgradeDto();
                versionUpgradeDtoRequest.setVersion(versionNo);
                String version = new Gson().toJson(versionUpgradeDtoRequest);
                StringEntity se = new StringEntity(version, HTTP.UTF_8);
                HttpClientWrapper httpConnection = new HttpClientWrapper();
                String url = "/versionUpgrade/view";
                httpConnection.sendRequest(url, null, ServiceListenerType.UPGRADE_RESPONSE, SyncHandler, RequestType.POST, se, this);
            } else {
                Util.messageBar(this, getString(R.string.noNetworkConnection));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }

    // After response received from server successfully in android
    private void getUpgradeResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("Ver", "Version Response" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Log.e("upgrade", response);

            final VersionUpgradeDto versionUpgradeDto = gson.fromJson(response, VersionUpgradeDto.class);
            int statusCode = versionUpgradeDto.getStatusCode();
            ((TextView) findViewById(R.id.tvDownloadTitle)).setVisibility(View.VISIBLE);
            if (statusCode == 0) {
                int version = versionUpgradeDto.getVersion();
                int ourVersion = versionNumber();
                if (version > ourVersion) {
                    Log.i("Location", versionUpgradeDto.getLocation());
                    String title = getResources().getString(R.string.app_name);
                    progressBar.setVisibility(View.VISIBLE);
                    final String path = Environment.getExternalStorageDirectory() + "/" + title + ".apk";
                    getFutureFile(versionUpgradeDto, path);
                } else {
                    Util.messageBar(this, getString(R.string.noUpdateAvailable));
                    Util.setTamilText((TextView) findViewById(R.id.tvDownloadTitle), getString(R.string.noUpdateAvailable));
                }

            } else {
                Util.messageBar(this, FPSDBHelper.getInstance(this).retrieveLanguageTable(versionUpgradeDto.getStatusCode(), GlobalAppState.language).getDescription());
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }


    }


    private void getFutureFile(VersionUpgradeDto versionUpgradeDto, String path)

    {

        downloadingFileFuture = Ion.with(AutoUpgrationActivity.this).load(versionUpgradeDto.getLocation())

                .progressBar(progressBar)

                .progressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {

                        double ratio = downloaded / (double) total;

                        DecimalFormat percentFormat = new DecimalFormat("#.#%");

                        tvUploadCount.setText("" + percentFormat.format(ratio));

                    }
                })

                .write(new File(path))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (e != null) {
                            Toast.makeText(AutoUpgrationActivity.this, "Error downloading file", Toast.LENGTH_LONG).show();

                            return;
                        }

                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        startActivity(i);
                        finish();


                    }

                });

    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case UPGRADE_RESPONSE:
                getUpgradeResponse(message);
                break;

            default:
                Log.e("Error", "Auto upgrade");
                break;
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }
}





