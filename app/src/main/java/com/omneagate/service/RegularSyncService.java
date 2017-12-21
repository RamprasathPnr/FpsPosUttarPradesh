package com.omneagate.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.dialog.StockInwardAlertDialog;
import com.omneagate.process.RegularSyncProcess;

public class RegularSyncService extends IntentService {

    String TAG = "RegularSyncService";

    public RegularSyncService() {
        super("RegularSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "RegularSyncService started...");
        RegularSyncProcess rsp = new RegularSyncProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) rsp;
        bsAllocation.process(RegularSyncService.this);
    }

}
