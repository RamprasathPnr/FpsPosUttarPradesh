package com.omneagate.DTO;

import java.util.Date;

import lombok.Data;

@Data
public class GodownStockHistoryDto {

    //Godown identifier
    long godownId;

    //Product identifier
    long productId;

    //Stock quantity available
    double quantity;

    //Previous stock quantity
    double prevQuantity;

    //Current stock quantity
    double currQuantity;

    //Date of creation
    Date createdate;

    //User id
    long createdby;

}
