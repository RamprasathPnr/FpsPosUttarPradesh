package com.omneagate.DTO;

import java.util.List;

import lombok.Data;

/**
 * persist the values of beneficiaryId and fpsId
 * for which passing the arguments of beneficiaryId and fpsId
 */
@Data
public class BeneficiaryAssociationDto {

    Long beneficiaryId;

    Long fpsId;

    String remarks;

    List<Long> beneficiaryIds;

}
