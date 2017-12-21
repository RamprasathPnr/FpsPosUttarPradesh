package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import lombok.Data;


@Data
public class BillInbetweenDto {


    Double quantity;                      // Quantity purchased

    Double cost;                          //Cost of the product

    String productName;

    String localProductName;

    String productUnit;

    String localProductUnit;

    public BillInbetweenDto() {

    }

    public BillInbetweenDto(Cursor cur) {

        quantity = cur.getDouble(cur
                .getColumnIndex("quantity"));

        cost = cur.getDouble(cur
                .getColumnIndex("totalCost"));

        productName = cur.getString(cur
                .getColumnIndex("name"));

        productUnit = cur.getString(cur
                .getColumnIndex("unit"));

        localProductUnit = cur.getString(cur
                .getColumnIndex("local_unit"));

        localProductName = cur.getString(cur
                .getColumnIndex("local_name"));


    }

}
