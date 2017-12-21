package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;

import com.omneagate.process.ServerTimeSetProcess;

/**
 * Created by root on 29/5/17.
 */
public class ServerTimeSetService extends IntentService {

    String TAG = "ServerTimeSetService";

    public ServerTimeSetService() {
        super("ServerTimeSetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ServerTimeSetProcess adjustmentProcess = new ServerTimeSetProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) adjustmentProcess;
        bsAllocation.process(ServerTimeSetService.this);
    }
}
