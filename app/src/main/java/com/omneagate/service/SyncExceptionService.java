package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.SyncExceptionProcess;

public class SyncExceptionService extends IntentService {

    String TAG = "SyncExceptionService";

    public SyncExceptionService() {
        super("SyncExceptionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"SyncExceptionService started...");
        SyncExceptionProcess syncExceptionProcess = new SyncExceptionProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) syncExceptionProcess;
        bsAllocation.process(SyncExceptionService.this);
    }
}
