package com.omneagate.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.omneagate.Util.Util;
import com.omneagate.service.AdjustmentService;
import com.omneagate.service.HeartBeatService;
import com.omneagate.service.InspectionReportAckService;
import com.omneagate.service.InspectionReportService;
import com.omneagate.service.InwardService;

public class InspectionReportAckAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 16;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, InspectionReportAckService.class);
        context.startService(i);
    }
}