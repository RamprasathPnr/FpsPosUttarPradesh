package com.omneagate.DTO;

import android.database.Cursor;

import lombok.Data;

@Data
public class Godown_Stock_InwardDto extends BaseDto {
/*
    //Godown identifier
    long godownId;


    //Master data received from admin
    String receivedFrom;

    //stock received date
    Date receivedDate;

    //product identifier
    String productId;

    //Stock quantity
    Double quantity;

    //Unit of measurement from Master table
    String unit;

    //Internal batch number auto generated
    long batchno;

    //Location identifier
    String storageLocation;

    //created Date & time
    Date createdDate;

    //created Date & time
    long createdby;*/


    long godownId;

    //FPS Identifier
    String fpsId;

    //Stock outward date
    long outwardDate;

    //Product identifier
    long productId;

    //Stock quantity
    Double quantity;

    //Unit of measurement from Master table
    String unit;

    //Internal batch number auto generated
    long batchno;

    //Indicates whether the corresponding FPS has received the stock allotment
    boolean FPSAckStatus;

    //Date of acknowledgement from FPS
    long FPSAckDate;

    //The actual quantity received by the FPS
    Double fpsReceiQuantity;

    //user id
    long createdby;

    //Deliver details which is used for delivery to FPS. Delivery challan id from Deliver_challan table
    long deliveryChallanId;

    public Godown_Stock_InwardDto() {

    }

    public Godown_Stock_InwardDto(Cursor cursor) {


    }


}
