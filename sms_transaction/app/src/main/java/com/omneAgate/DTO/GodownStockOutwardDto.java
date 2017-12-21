package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.util.Set;

import lombok.Data;

@Data
public class GodownStockOutwardDto extends BaseDto {

    //godown identifier
    long godownId;

    //FPS Identifier
    long fpsId;

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
    boolean fpsAckStatus;

    //Date of acknowledgement from FPS
    long fpsAckDate;

    //The actual quantity received by the FPS
    Double fpsReceiQuantity;

    //user id
    long createdby;

    //Deliver details which is used for delivery to FPS. Delivery challan id from Deliver_challan table
    long deliveryChallanId;


    Set<ChellanProductDto> productDto;


    public GodownStockOutwardDto() {

    }

    public GodownStockOutwardDto(Cursor cursor) {

        godownId = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID));
        fpsId = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID));
        outwardDate = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE));
        productId = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID));

        quantity = cursor.getDouble(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY));
        unit = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT));
        batchno = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO));
        createdby = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY));

        fpsAckDate = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE));
        fpsReceiQuantity = cursor.getDouble(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY));
        deliveryChallanId = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID));

        if (cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS)) == 0) {
            fpsAckStatus = false;
        } else {
            fpsAckStatus = true;
        }


    }
}
