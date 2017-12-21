package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AdjustmentProcess;
import com.omneagate.process.AllocationProcess;
import com.omneagate.process.InspectionReportProcess;
import com.omneagate.process.InwardProcess;

public class InspectionReportService extends IntentService {

    String TAG = "InspectionReportService";

    public InspectionReportService() {
        super("InspectionReportService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"InspectionReportService started...");
        InspectionReportProcess inspectionReportProcess = new InspectionReportProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) inspectionReportProcess;
        bsAllocation.process(InspectionReportService.this);
    }
}
