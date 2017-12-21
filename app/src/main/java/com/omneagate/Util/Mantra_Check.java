package com.omneagate.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.omneagate.activity.R;

import java.io.File;

/**
 * Created by root on 30/11/17.
 */
public class Mantra_Check {

    private final Activity activity;
    private String TAG = "Mantra check";
   /* private String rd_service_download_path = "https://download.mantratecapp.com/staticdownload/MantraRDService.apk";
    private String client_download_path = "https://download.mantratecapp.com/staticdownload/MMC_Custom.apk";
   */

     private String rd_service_download_path = "http://52.66.76.172:9201/apk/mantra/MantraRDService.apk";
     private String client_download_path = "http://52.66.76.172:9201/apk/mantra/MMC_Custom.apk";

    final String rd_path = Environment.getExternalStorageDirectory() + "/POS/MantraRDService.apk";
    final String client_path = Environment.getExternalStorageDirectory() + "/POS/MMC_Custom.apk";
    private ProgressDialog mProgressDialog;
    NetworkConnection networkConnection;

    public Mantra_Check(Activity context) {
        this.activity = context;
        networkConnection = new NetworkConnection(context);
        String Mantra_RD = FPSDBHelper.getInstance(context).getMasterData("Mantra_RD");
        if (Mantra_RD != null)
            rd_service_download_path = Mantra_RD;

        String Mantra_Client = FPSDBHelper.getInstance(context).getMasterData("Mantra_Client");
        if (Mantra_Client != null)
            client_download_path = Mantra_Client;
    }

    public boolean checkIsapk_installed() {

        try {
            PackageInfo pinfo1 = activity.getPackageManager().getPackageInfo("com.mantra.clientmanagement", 0);
            String verName1 = pinfo1.versionName;
            int verCode1 = pinfo1.versionCode;
            Log.e(TAG, "Client Management app Name  " + pinfo1.applicationInfo.loadLabel(activity.getPackageManager()).toString());
            Log.e(TAG, "Client Management VersionCode" + verCode1);
            Log.e(TAG, "Client Management VersionName" + verName1);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, activity.getString(R.string.no_client_app), Toast.LENGTH_SHORT).show();
            if (networkConnection.isNetworkAvailable())
                download_apk(client_path, client_download_path, activity.getString(R.string.downloading_msg_1));
            else
                Toast.makeText(activity, activity.getString(R.string.noNetworkConnection), Toast.LENGTH_SHORT).show();

            return false;
        }

        try {
            PackageInfo pinfo = activity.getPackageManager().getPackageInfo("com.mantra.rdservice", 0);
            String verName = pinfo.versionName;
            int verCode = pinfo.versionCode;
            Log.e(TAG, "RD Sample app Name  " + pinfo.applicationInfo.loadLabel(activity.getPackageManager()).toString());
            Log.e(TAG, "RD Sample VersionCode  " + verCode);
            Log.e(TAG, "RD Sample VersionName " + verName);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, activity.getString(R.string.no_rd_app), Toast.LENGTH_SHORT).show();
            if (networkConnection.isNetworkAvailable())
                download_apk(rd_path, rd_service_download_path, activity.getString(R.string.downloading_msg_2));
            else
                Toast.makeText(activity, activity.getString(R.string.noNetworkConnection), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void download_apk(String filepath, String downloadPath, String msg) {
        show_progressbar(msg);
        checkIs_fileExist();
        getFutureFile(filepath, downloadPath);
    }

    private void checkIs_fileExist() {
        File file = new File(Environment.getExternalStorageDirectory(), "POS");
        if (!file.exists()) {
            file.mkdir();
        }
    }


    private void getFutureFile(String filepath, String downloadPath) {

        Ion.with(activity).load(downloadPath)
                .progressBar(null)
                .progressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        mProgressDialog.setMax((int) total / 1024);
                        mProgressDialog.setProgress((int) downloaded / 1024);
//                        double ratio = downloaded / (double) total;
//                        Log.e("ratio ",""+ratio);
//                        double percentage = ratio * 100;
//                        Log.e("percentage ",""+percentage);

//                        DecimalFormat percentFormat = new DecimalFormat("#.#%");
//                        DecimalFormat percentFormat = new DecimalFormat("#");
//                        Double.parseDouble(percentFormat.format(ratio);
//                        progressbar1.setProgress((int)ratio);
//                        progressbar1.incrementProgressBy(1);
//                        String what = percentFormat.format(ratio);
//                        mProgressDialog.incrementProgressBy((int) percentage);
//                        mProgressDialog.setProgressNumberFormat(Double.toString(ratio));
                    }
                })
                .write(new File(filepath))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        if (e == null) {
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                            activity.startActivity(i);
                        }else{
                            Toast.makeText(activity, "Downloading interrupted. Please try again", Toast.LENGTH_LONG).show();
                        }
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    }

                });
    }

    private void show_progressbar(String msg) {
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

}
