package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.VersionDto;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import java.util.List;

/**
 * Created by user1 on 17/8/15.
 */
public class VersionUpgradeInfo extends BaseActivity {
    List<VersionDto> versionupgrade;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.versionupgrade_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        configureInitialPage();
    }

    private void configureInitialPage() {
        setUpPopUpPage();
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.version_upgrade);
        new fpsVersionUpgradeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, AdminActivity.class));
        finish();
    }

    private void versionInfo(List<VersionDto> version) {
        try {
            versionupgrade = version;
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            LayoutInflater lin = LayoutInflater.from(com.omneagate.activity.VersionUpgradeInfo.this);
            int sno = 1;
            for (VersionDto versiondto : version) {
                transactionLayout.addView(returnView(lin, sno, versiondto.getCurrentVersion(), versiondto.getPreviousVersion(), versiondto.getState(), versiondto.getStatus()));
                sno++;

            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }


    }

    private View returnView(LayoutInflater entitle, int sno, String newVersion, String oldVersion, String commands, String status) {
        View convertView = entitle.inflate(R.layout.adpter_versionupgrade, null);
        TextView snoTv = (TextView) convertView.findViewById(R.id.sno);
        TextView newVersionTv = (TextView) convertView.findViewById(R.id.newversion);
        TextView oldVersionTv = (TextView) convertView.findViewById(R.id.oldversion);
        TextView descriptionTv = (TextView) convertView.findViewById(R.id.description);
        TextView statusTv = (TextView) convertView.findViewById(R.id.status);
        Button viewMore = (Button) convertView.findViewById(R.id.viewmore);

        viewMore.setId(sno - 1);
        snoTv.setText("" + sno);
        newVersionTv.setText(newVersion);
        oldVersionTv.setText(oldVersion);
        descriptionTv.setText(status);
        statusTv.setText(commands);
        viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int itemIndex = view.getId();
                    Intent VersionDetailIntent;
                    String versionDetail = new Gson().toJson(versionupgrade.get(itemIndex));
                    VersionDetailIntent = new Intent(getApplicationContext(), VersionUpgradeDetail.class);
                    VersionDetailIntent.putExtra("versionDetail", versionDetail);
                    startActivity(VersionDetailIntent);
                } catch (Exception e) {
                    Log.e("Error", e.toString(), e);

                }
            }
        });
        return convertView;
    }

    private class fpsVersionUpgradeTask extends AsyncTask<String, Void, List<VersionDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.VersionUpgradeInfo.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<VersionDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.VersionUpgradeInfo.this).getVersionInfo();
        }

        // can use UI thread here
        protected void onPostExecute(final List<VersionDto> result) {
            Log.e("productDtoList", "" + result.toString());
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
            catch(Exception e) {}

            if (result.size() != 0) {
                versionInfo(result);
            } else {
                Util.messageBar(com.omneagate.activity.VersionUpgradeInfo.this, getString(R.string.no_records));
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }

}

