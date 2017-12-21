package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.HeartBeatProcess;

public class HeartBeatService extends IntentService {

    public HeartBeatService() {
        super("HeartBeatService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("HeartBeatService","started");

        HeartBeatProcess heartBeatClass = new HeartBeatProcess();
        BaseSchedulerService bs = (BaseSchedulerService) heartBeatClass;
        bs.process(HeartBeatService.this);
    }
}
