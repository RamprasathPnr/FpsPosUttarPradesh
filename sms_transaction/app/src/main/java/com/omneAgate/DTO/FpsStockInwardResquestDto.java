package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.util.Set;

import lombok.Data;

/**
 * Created by user1 on 14/3/15.
 */
@Data
public class FpsStockInwardResquestDto {
    //godown identifier
    long godownId;

    //FPS Identifier
    String fpsId;

    //Stock outward date
    long outwardDate;

    //Indicates whether the corresponding FPS has received the stock allotment
    boolean fpsAckStatus;

    //Date of acknowledgement from FPS
    long fpsAckDate;

    //Deliver details which is used for delivery to FPS. Delivery challan id from Deliver_challan table
    long deliveryChallanId;

    //Product identifier
    long productId;

    //Stock quantity
    Double quantity;

    Set<ChellanProductDto> productDto;


    public FpsStockInwardResquestDto() {

    }

    public FpsStockInwardResquestDto(Cursor cursor) {
        long godownId = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID));

    }


}
