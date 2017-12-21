package com.omneagate.DTO;

import lombok.Data;

/**
 * Created for BifurcationHistoryDto
 */

@Data
public class BifurcationHistoryDto {

    private static final long serialVersionUID = 1L;

    long id;

    FpsStoreDto oldFpsStore;

    FpsStoreDto newFpsStore;

    BeneficiaryDto beneficiary;

    long createdById;

    long createdDate;

}
