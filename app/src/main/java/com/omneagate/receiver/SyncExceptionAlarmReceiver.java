package com.omneagate.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.omneagate.Util.Util;
import com.omneagate.service.AdjustmentService;
import com.omneagate.service.HeartBeatService;
import com.omneagate.service.InwardService;
import com.omneagate.service.SyncExceptionService;

public class SyncExceptionAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SyncExceptionService.class);
        context.startService(i);
    }
}