package com.omneagate.DTO;

import android.database.Cursor;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

/**
 * store the closesale transaction
 */

@Data
public class CloseSaleTransactionDto extends BaseDto implements Serializable {

    /**
     * serial version id of serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * auto generated
     */
    long id;


    /**
     * FPS_id of the fps store
     */
    long fpsStoreId;

    /**
     * create date of the pos
     */
    long createdDate;

    /**
     * number of transaction in pos
     */
    long numofTrans;

    /**
     * Date of transaction in pos
     */
    long dateOfTxn;

    String deviceId;

    long transactionId;
    ;

    /**
     * CloseSaleTransactionDto identifier
     */
    Set<CloseOfProductDto> closeOfProductDtoList;

    Long createdBy;

    Double totalSaleCost;

    int isServerAdded;

    public CloseSaleTransactionDto(){

    }

    public  CloseSaleTransactionDto(Cursor cur){
        numofTrans = cur.getInt(cur.getColumnIndex("numofTrans"));
        transactionId = cur.getLong(cur.getColumnIndex("transactionId"));
        totalSaleCost = cur.getDouble(cur.getColumnIndex("totalSaleCost"));
        dateOfTxn = cur.getLong(cur.getColumnIndex("dateOfTxn"));
    }
}
