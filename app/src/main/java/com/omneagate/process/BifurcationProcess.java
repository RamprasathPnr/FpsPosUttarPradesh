package com.omneagate.process;

import android.content.Context;

import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.service.BaseSchedulerService;

import java.io.Serializable;
import java.util.ArrayList;


public class BifurcationProcess implements BaseSchedulerService, Serializable {

    Context globalContext;
    String TAG = "BifurcationProcess";

    // Bifurcation process
    public void process(Context context) {
        globalContext = context;
        long primaryId = -1;
        ArrayList<String> bifurcationBenefIdList = FPSDBHelper.getInstance(globalContext).getBifurcationBenefId();
        Util.LoggingQueue(globalContext, TAG, "ArrayList size of bifurcationBenefIdList = " + bifurcationBenefIdList.size());
            for (int i = 0; i < bifurcationBenefIdList.size(); i++) {
                try {
                    // inserting request into local db
                    primaryId = FPSDBHelper.getInstance(globalContext).insertBackgroundProcessHistory(bifurcationBenefIdList.get(i), "BifurcationService");
                    Util.LoggingQueue(globalContext, TAG, "Bifurcation ID = " + bifurcationBenefIdList.get(i));
                    FPSDBHelper.getInstance(globalContext).bifurcationDeactivateBenef(bifurcationBenefIdList.get(i));
                    FPSDBHelper.getInstance(globalContext).updateBifurcationStatus(bifurcationBenefIdList.get(i));
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("Beneficiary deactivated", "", "Success", primaryId);
                    }
                } catch (Exception e) {
                    Util.LoggingQueue(globalContext, TAG, "Bifurcation Exception1 = " + e);
                    // update response into local db
                    if (primaryId != -1) {
                        FPSDBHelper.getInstance(globalContext).updateBackgroundProcessHistory("Beneficiary not deactivated", e.toString(), "Failure", primaryId);
                    }
                }
            }
    }





}
