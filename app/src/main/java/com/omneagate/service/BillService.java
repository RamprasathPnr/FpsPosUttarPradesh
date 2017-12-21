package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AdjustmentProcess;
import com.omneagate.process.AllocationProcess;
import com.omneagate.process.BillProcess;
import com.omneagate.process.InwardProcess;

public class BillService extends IntentService {

    String TAG = "BillService";

    public BillService() {
        super("BillService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"BillService started...");
        BillProcess billProcess = new BillProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) billProcess;
        bsAllocation.process(BillService.this);
    }
}
