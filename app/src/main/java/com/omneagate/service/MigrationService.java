package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.MigrationProcess;

public class MigrationService extends IntentService {

    String TAG = "MigrationService";

    public MigrationService() {
        super("MigrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"MigrationService started...");
        MigrationProcess migrationProcess = new MigrationProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) migrationProcess;
        bsAllocation.process(MigrationService.this);
    }
}
