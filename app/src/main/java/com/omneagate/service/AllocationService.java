package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AllocationProcess;
import com.omneagate.process.BifurcationProcess;

public class AllocationService extends IntentService {

    String TAG = "AllocationService";

    public AllocationService() {
        super("AllocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"sAllocationService started...");
        AllocationProcess gsd = new AllocationProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) gsd;
        bsAllocation.process(AllocationService.this);

        // Bifurcation task
        BifurcationProcess bifurcationClass = new BifurcationProcess();
        BaseSchedulerService bsBifurcation = (BaseSchedulerService) bifurcationClass;
        bsBifurcation.process(AllocationService.this);
    }
}
