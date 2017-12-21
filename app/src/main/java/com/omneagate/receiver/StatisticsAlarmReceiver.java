package com.omneagate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omneagate.service.StatisticsServices;

public class StatisticsAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, StatisticsServices.class);
        context.startService(i);
    }
}