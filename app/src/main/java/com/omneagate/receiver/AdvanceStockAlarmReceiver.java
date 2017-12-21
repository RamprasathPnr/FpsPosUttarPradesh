package com.omneagate.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.omneagate.Util.Util;
import com.omneagate.service.AdjustmentService;
import com.omneagate.service.AdvanceStockService;
import com.omneagate.service.HeartBeatService;
import com.omneagate.service.InwardService;

public class AdvanceStockAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 7;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AdvanceStockService.class);
        context.startService(i);
    }
}