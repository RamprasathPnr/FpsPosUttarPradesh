package com.omneagate.DTO.UserDto;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import lombok.Data;

/**
 * Created by user1 on 3/9/15.
 */
@Data
public class BillItemProductDto {


    Long productId;                       // Product identifier

    Double quantity;                      // Quantity purchased

    Double cost;                          //Cost of the product

    String transactionId;

    String productName;

    String localProductName;

    String productUnit;

    String localProductUnit;

    public BillItemProductDto() {

    }

    public BillItemProductDto(Cursor cur) {
        productId = cur.getLong(cur
                .getColumnIndex("product_id"));

        quantity = cur.getDouble(cur
                .getColumnIndex("quantity"));

        cost = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_COST));

        productName = cur.getString(cur
                .getColumnIndex("name"));

        productUnit = cur.getString(cur
                .getColumnIndex("unit"));

        localProductUnit = cur.getString(cur
                .getColumnIndex("local_unit"));

        localProductName = cur.getString(cur
                .getColumnIndex("local_name"));

        transactionId = cur.getString(cur
                .getColumnIndex("transaction_id"));

    }

}
