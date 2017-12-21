package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.VersionDto;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created for version upgrade
 */
public class VersionUpgradeDetail extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_upgrade);
        try {
            Intent intent = getIntent();
            VersionDto versionDetail;
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String versionString = intent.getStringExtra("versionDetail");
            versionDetail = gson.fromJson(versionString, VersionDto.class);
            viewVersionInfo(versionDetail);
        } catch (Exception e) {
            Log.e("error", e.toString(), e);
        } finally {
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    }

    //Version information details
    private void viewVersionInfo(VersionDto versionDetail) {
        setUpPopUpPage();
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.version_upgrade);
        if (StringUtils.isNotEmpty(versionDetail.getCurrentVersion())) {
            ((TextView) findViewById(R.id.current_version)).setText(versionDetail.getCurrentVersion());
        }
        if (StringUtils.isNotEmpty(versionDetail.getPreviousVersion())) {
            ((TextView) findViewById(R.id.old_version)).setText(versionDetail.getPreviousVersion());
        }
        if (!versionDetail.getState().equals(null)) {
            ((TextView) findViewById(R.id.upgrade_status)).setText(versionDetail.getStatus());
        }
        if (StringUtils.isNotEmpty(versionDetail.getDescription())) {
            ((TextView) findViewById(R.id.description)).setText(versionDetail.getDescription());
        }
        if (StringUtils.isNotEmpty(versionDetail.getCreatedTime())) {
            ((TextView) findViewById(R.id.created_time)).setText(versionDetail.getCreatedTime());
        }
        if (StringUtils.isNotEmpty(versionDetail.getUpdatedTime())) {
            ((TextView) findViewById(R.id.updated_time)).setText(versionDetail.getUpdatedTime());
        }
        if (StringUtils.isNotEmpty(versionDetail.getStatus())) {
            ((TextView) findViewById(R.id.status)).setText(versionDetail.getState());
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, VersionUpgradeInfo.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
    }

}
