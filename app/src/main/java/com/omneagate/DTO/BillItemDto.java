package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;

import lombok.Data;

/**
 * This class is used to transfer BillItem details
 *
 * @author user1
 */

@Data
public class BillItemDto implements Serializable {

    long Id;                              //Auto generated identifier

    Long billId;                          // bill identifier

    Long productId;                       // Product identifier

    Double quantity;                      // Quantity purchased

    Double cost;                          //Cost of the product

    long refId;

    String billItemDate;

    String transactionId;

    public BillItemDto() {

    }

    public BillItemDto(Cursor cur) {
        productId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID));

        quantity = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_QUANTITY));

        cost = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_COST));

       /* refId = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_BILL_REF));*/

        billItemDate = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_DATE));

        transactionId = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_TRANSACTION_ID));



    }

}	
