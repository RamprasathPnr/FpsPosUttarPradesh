package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AdvanceStockProcess;

public class AdvanceStockService extends IntentService {

    String TAG = "AdvanceStockService";

    public AdvanceStockService() {
        super("AdvanceStockService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"AdvanceStockService started...");
        AdvanceStockProcess advanceStockProcess = new AdvanceStockProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) advanceStockProcess;
        bsAllocation.process(AdvanceStockService.this);
    }
}
