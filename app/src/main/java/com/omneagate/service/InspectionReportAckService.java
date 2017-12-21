package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AdjustmentProcess;
import com.omneagate.process.AllocationProcess;
import com.omneagate.process.InspectionReportAckProcess;
import com.omneagate.process.InspectionReportProcess;
import com.omneagate.process.InwardProcess;

public class InspectionReportAckService extends IntentService {

    String TAG = "InspectionReportAckService";

    public InspectionReportAckService() {
        super("InspectionReportAckService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"InspectionReportAckService started...");
        InspectionReportAckProcess inspectionReportAckProcess = new InspectionReportAckProcess();
        BaseSchedulerService bsAllocation = (BaseSchedulerService) inspectionReportAckProcess;
        bsAllocation.process(InspectionReportAckService.this);
    }
}
