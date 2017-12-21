package com.omneagate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omneagate.service.AllocationService;
import com.omneagate.service.RegularSyncService;

public class RegularSyncAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 4;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, RegularSyncService.class);
        context.startService(i);
    }
}