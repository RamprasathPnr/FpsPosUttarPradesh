package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.activity.GlobalAppState;
import com.omneagate.process.AdjustmentProcess;
import com.omneagate.process.AllocationProcess;
import com.omneagate.process.InwardProcess;
import com.omneagate.process.LoginProcess;
import com.omneagate.process.RemoteLogProcess;

public class RemoteLogService extends IntentService {

    String TAG = "RemoteLogService";

    public RemoteLogService() {
        super("RemoteLogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"RemoteLogService started...");
        GlobalAppState appState = (GlobalAppState) getApplication();
        RemoteLogProcess remoteLogProcess = new RemoteLogProcess();
//        BaseSchedulerService bsAllocation = (BaseSchedulerService) remoteLogProcess;
        remoteLogProcess.process(RemoteLogService.this, appState);
    }
}
