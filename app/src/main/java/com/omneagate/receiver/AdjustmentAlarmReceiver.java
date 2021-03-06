package com.omneagate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omneagate.service.AdjustmentService;

public class AdjustmentAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 6;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AdjustmentService.class);
        context.startService(i);
    }
}