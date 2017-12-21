package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 16/3/15.
 */
@Data
public class ChellanProductDto {

    //Product identifier
    long productId;

    // Product Name
    String name;

    //Stock quantity
    Double quantity;

    // Received Quantity entry after  Chellan received physically .
    double receiProQuantity;

    double openingQuantity;

    int month;

    int year;

    long serverId;
    long ackDate;

    long recordId;

}

