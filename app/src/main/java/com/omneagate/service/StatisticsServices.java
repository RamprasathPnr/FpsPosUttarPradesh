package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;

import com.omneagate.process.BiometricProcess;
import com.omneagate.process.StatisticsProcess;

public class StatisticsServices extends IntentService {

    public StatisticsServices() {
        super("StatisticsServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Statistics task
        StatisticsProcess statClass = new StatisticsProcess();
        BaseSchedulerService bsStats = (BaseSchedulerService) statClass;
        bsStats.process(StatisticsServices.this);

        // Biometric sync task
        BiometricProcess biometricClass = new BiometricProcess();
        BaseSchedulerService bsBiometric = (BaseSchedulerService) biometricClass;
        bsBiometric.process(StatisticsServices.this);
    }
}
