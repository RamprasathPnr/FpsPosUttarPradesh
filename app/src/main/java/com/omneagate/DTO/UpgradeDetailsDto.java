package com.omneagate.DTO;

import com.omneagate.DTO.EnumDTO.CommonStatuses;

import lombok.Data;

@Data
public class UpgradeDetailsDto extends BaseDto {
    /**
     * created time
     */
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
     * beneficiary table count
     */
    int beneficiaryUnsyncCount;

    /**
     * bill table count
     */
    int billCount;

    /**
     * bill table count
     */
    int billUnsyncCount;

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

    /**
     * previous version
     */
    int previousVersion;

    /**
     * current version
     */
    int currentVersion;

    String referenceNumber;

    /**
     * Staus for update
     */
    CommonStatuses status;
}