package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.BiometricProcess;

public class BiometricService extends IntentService {

    String TAG = "BiometricService";

    public BiometricService() {
        super("BiometricService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"BiometricService started...");
        BiometricProcess biometricProcess = new BiometricProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) biometricProcess;
        bsAllocation.process(BiometricService.this);
    }
}
