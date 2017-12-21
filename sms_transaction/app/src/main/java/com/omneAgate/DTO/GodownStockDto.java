package com.omneagate.DTO;

import lombok.Data;

@Data
public class GodownStockDto {

    //Godown identifier
    long godownId;

    //product identifier
    long productId;

    //Stock quantity available
    Double quantity;

    //Unit of measurement from Master table
    String unit;

    //batch number
    long batchno;

}
