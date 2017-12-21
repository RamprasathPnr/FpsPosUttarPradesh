package com.omneagate.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.omneagate.Util.Util;
import com.omneagate.service.AdjustmentService;
import com.omneagate.service.HeartBeatService;
import com.omneagate.service.InwardService;
import com.omneagate.service.MigrationService;
import com.omneagate.service.SyncExceptionService;

public class MigrationAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 13;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MigrationService.class);
        context.startService(i);
    }
}