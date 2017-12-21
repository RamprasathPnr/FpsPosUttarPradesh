package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AdjustmentProcess;

public class AdjustmentService extends IntentService {

    String TAG = "AdjustmentService";

    public AdjustmentService() {
        super("AdjustmentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"AdjustmentService started...");
        AdjustmentProcess adjustmentProcess = new AdjustmentProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) adjustmentProcess;
        bsAllocation.process(AdjustmentService.this);
    }
}
