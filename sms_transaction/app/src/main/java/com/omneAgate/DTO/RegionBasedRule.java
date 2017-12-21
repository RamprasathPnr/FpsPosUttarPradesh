package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 28/4/15.
 */

@Data
public class RegionBasedRule {


    long id;

    long productId;

    int cylinderCount;

    boolean isTaluk;

    boolean isCity;

    boolean isMunicipality;

    boolean isCityHeadQuarter;

    double quantity;

    long lastUpdatedTime;

    long lastUpdatedBy;
}
