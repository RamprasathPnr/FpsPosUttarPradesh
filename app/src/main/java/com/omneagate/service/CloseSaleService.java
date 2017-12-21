package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.CloseSaleProcess;

public class CloseSaleService extends IntentService {

    String TAG = "CloseSaleService";

    public CloseSaleService() {
        super("CloseSaleService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"CloseSaleService started...");
        CloseSaleProcess closeSaleProcess = new CloseSaleProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) closeSaleProcess;
        bsAllocation.process(CloseSaleService.this);
    }
}
