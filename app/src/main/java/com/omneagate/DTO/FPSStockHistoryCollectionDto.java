package com.omneagate.DTO;

import lombok.Data;

/**
 * Created for FPSStockHistoryCollectionDto
 */

@Data
public class FPSStockHistoryCollectionDto {

    Long id;

    String deviceNum;
    /**FPS identifier*/
    long fpsId;

    /**product identifier*/
    ProductDto productDto;

    /**Stock quantity which is to be increased or decrease*/
    Double quantity;

    /**Previous quantity of the stock*/
    Double prevQuantity;

    /**Current quantity of the stock*/
    Double currQuantity;

    /**Created by of the stock*/
    long createdBy;

    /**Date of creation*/
    long createdDate;

    /**Operation Type*/
    String operation;

}
