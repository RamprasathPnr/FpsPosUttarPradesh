package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.omneagate.process.AdjustmentProcess;
import com.omneagate.process.AllocationProcess;
import com.omneagate.process.CardInspectionProcess;
import com.omneagate.process.InspectionReportProcess;
import com.omneagate.process.InwardProcess;
import com.omneagate.process.OtherInspectionProcess;
import com.omneagate.process.ShopInspectionProcess;
import com.omneagate.process.StockInspectionProcess;
import com.omneagate.process.WeighmentInspectionProcess;

public class InspectionCriteriaService extends IntentService {

    String TAG = "InspectionCriteriaService";

    public InspectionCriteriaService() {
        super("InspectionCriteriaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG,"InspectionCriteriaService started...");

        CardInspectionProcess cardInspectionProcess = new CardInspectionProcess();
        BaseSchedulerService bsAllocationCard = (BaseSchedulerService) cardInspectionProcess;
        bsAllocationCard.process(InspectionCriteriaService.this);

        StockInspectionProcess stockInspectionProcess = new StockInspectionProcess();
        BaseSchedulerService bsAllocationStock = (BaseSchedulerService) stockInspectionProcess;
        bsAllocationStock.process(InspectionCriteriaService.this);

        WeighmentInspectionProcess weighmentInspectionProcess = new WeighmentInspectionProcess();
        BaseSchedulerService bsAllocationWeighment = (BaseSchedulerService) weighmentInspectionProcess;
        bsAllocationWeighment.process(InspectionCriteriaService.this);

        ShopInspectionProcess shopInspectionProcess = new ShopInspectionProcess();
        BaseSchedulerService bsAllocationShop = (BaseSchedulerService) shopInspectionProcess;
        bsAllocationShop.process(InspectionCriteriaService.this);

        OtherInspectionProcess otherInspectionProcess = new OtherInspectionProcess();
        BaseSchedulerService bsAllocationOther = (BaseSchedulerService) otherInspectionProcess;
        bsAllocationOther.process(InspectionCriteriaService.this);
    }
}
