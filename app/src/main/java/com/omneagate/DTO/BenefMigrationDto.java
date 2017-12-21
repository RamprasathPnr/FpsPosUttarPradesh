package com.omneagate.DTO;

import lombok.Data;

/**
 * Created for BenefMigrationDto
 */

@Data
public class BenefMigrationDto extends BaseDto {


    String deviceId;

    long benificiaryId;

    long benefMigrationReqId;

    BeneficiaryDto beneficiaryDto;

}
