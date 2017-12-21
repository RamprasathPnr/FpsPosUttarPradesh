package com.omneagate.DTO;

import lombok.Data;

/**
 * Created StatisticsDto.
 */
@Data
public class StatisticsDto extends BaseDto {

    String deviceNum;

    int health;

    int scale;

    int level;

    int batteryLevel;

    int plugged;

    int status;

    String technology;

    int temperature;

    int voltage;

    boolean present;

    String latitude;

    String longtitude;

    int versionNum;

    int totalBillCountToday;

    int totalUnSyncBillCountToday;

    int beneficiaryCount;

    int registrationCount;

    int unSyncBillCount;

    long lastUpdatedTime;

    String versionName;

    long apkInstalledTime;

    String cpuUtilisation;

    String memoryUsed;

    String memoryRemaining;

    String totalMemory;

    String networkInfo;

    String hardDiskSizeFree;

    String userId;

    String simId;

    int unsyncInwardCount;

    int unsyncAdjustmentCount;

    int unsyncMigrationOut;

    int unsyncMigrationIn;

    int unsyncAdvanceStock;

    int entitlementMasterRulesCount;

    int personBasedRulesCount;

    int regionBasedRulesCount;

    int specialBasedRulesCount;

    int aadhaarSeedingCount;

    int beneficiaryMemberCount;

    int nfsaPosDataCount;

}
