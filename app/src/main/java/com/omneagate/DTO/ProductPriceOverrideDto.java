package com.omneagate.DTO;

import lombok.Data;

@Data
public class ProductPriceOverrideDto {

    Long id;

    long cardTypeId;

    long productId;

    double percentage;

    String cardType;

    /**
     * user created date of pos
     */
    long createdDate;

    /**
     * user id of pos
     */
    Long createdBy;

    /**
     * user created date of pos
     */
    long modifiedDate;

    /**
     * user id of pos
     */
    Long modifiedBy;

    Boolean isDeleted;
}
