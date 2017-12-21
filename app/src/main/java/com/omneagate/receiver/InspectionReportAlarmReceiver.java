package com.omneagate.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.omneagate.Util.Util;
import com.omneagate.service.AdjustmentService;
import com.omneagate.service.HeartBeatService;
import com.omneagate.service.InspectionReportService;
import com.omneagate.service.InwardService;

public class InspectionReportAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 15;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, InspectionReportService.class);
        context.startService(i);
    }
}