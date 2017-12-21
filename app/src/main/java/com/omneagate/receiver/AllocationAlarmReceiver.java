package com.omneagate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omneagate.service.AllocationService;

public class AllocationAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AllocationService.class);
        context.startService(i);
    }
}