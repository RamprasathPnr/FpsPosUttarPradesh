package com.omneagate.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.omneagate.Util.Util;
import com.omneagate.service.AdjustmentService;
import com.omneagate.service.HeartBeatService;
import com.omneagate.service.InwardService;
import com.omneagate.service.LoginService;
import com.omneagate.service.RemoteLogService;

public class RemoteLogReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 11;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, RemoteLogService.class);
        context.startService(i);
    }
}