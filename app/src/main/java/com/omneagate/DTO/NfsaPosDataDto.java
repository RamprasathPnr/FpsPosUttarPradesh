package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 31/10/16.
 */
@Data
public class NfsaPosDataDto {

    /**  */
    private static final long serialVersionUID = 6212583728895329621L;

    BeneficiaryDto beneficiaryDto;

    FpsStoreDto fpsStoreDto;

    String cardTypeName;

    String familyHeadName;

    String localFamilyHeadName;

    Boolean isDeleted;

    Boolean nfsaStatus;

    long createdDate;

    long modifiedDate;

}