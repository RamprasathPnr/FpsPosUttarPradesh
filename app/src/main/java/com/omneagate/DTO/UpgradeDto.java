package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 7/7/15.
 */
@Data
public class UpgradeDto {


    long createdTime;

    /**
     * created time
     */
    long updatedTime;

    /**
     * device number
     */
    String deviceNum;

    /**
     * beneficiary table count
     */
    int beneficiaryCount;

    /**
     * bill table count
     */
    int billCount;

    /**
     * product table count
     */
    int productCount;

    /**
     * card type table count
     */
    int cardTypeCount;

    /**
     * fps stock count
     */
    int fpsStockCount;
    int beneficiaryUnsyncCount;
    int billUnsyncCount;

    /**
     * previous version
     */
    int previousVersion;

    /**
     * current version
     */
    int currentVersion;
}
