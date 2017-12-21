package com.omneagate.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.omneagate.Util.Util;
import com.omneagate.service.HeartBeatService;

public class HeartBeatAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, HeartBeatService.class);
        context.startService(i);
    }
}