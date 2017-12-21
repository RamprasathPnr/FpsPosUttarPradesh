package com.omneagate.DTO;

import lombok.Data;

/**
 * Created for FPSMigrationDto.
 */

@Data
public class FPSMigrationDto {

    long sourceFpsId;

    Long id;

    long beneficiaryId;

    /** Requesting Beneficiary information */
    BeneficiaryDto beneficiaryDto;

    /** Migrating FPS Store information */
    long targetFpsId;

    Boolean isMigrated;

    long createdDate;

    Long createdBy;

    String type;

    int month;

    int year;

    Long benefBlockedDate;

    Long benefMigratedDate;

}
