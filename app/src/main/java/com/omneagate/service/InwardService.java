package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AllocationProcess;
import com.omneagate.process.InwardProcess;

public class InwardService extends IntentService {

    String TAG = "InwardService";

    public InwardService() {
        super("InwardService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"InwardService started...");
        InwardProcess inwardProcess = new InwardProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) inwardProcess;
        bsAllocation.process(InwardService.this);
    }
}
