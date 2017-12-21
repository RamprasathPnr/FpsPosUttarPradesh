package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AdjustmentProcess;
import com.omneagate.process.AllocationProcess;
import com.omneagate.process.InwardProcess;
import com.omneagate.process.LoginProcess;

public class LoginService extends IntentService {

    String TAG = "LoginService";

    public LoginService() {
        super("LoginService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"LoginService started...");
        LoginProcess loginProcess = new LoginProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) loginProcess;
        bsAllocation.process(LoginService.this);
    }
}
