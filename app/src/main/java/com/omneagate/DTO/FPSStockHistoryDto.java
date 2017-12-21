package com.omneagate.DTO;

import android.database.Cursor;

import java.util.Date;

import lombok.Data;

@Data
public class FPSStockHistoryDto {

    //FPS identifier
    long FPSId;

    //product identifier
    long productId;

    //Stock quantity which is to be increased or decrease
    int quantity;

    //Previous quantity of the stock
    Double prevQuantity;

    //Current quantity of the stock
    Double currQuantity;

    //Date of creation
    Date createdDate;

    String action;

    public FPSStockHistoryDto() {

    }

    public FPSStockHistoryDto(Cursor cursor) {
        prevQuantity = cursor.getDouble(cursor.getColumnIndex("opening_balance"));
        currQuantity = cursor.getDouble(cursor.getColumnIndex("closing_balance"));
        productId = cursor.getLong(cursor.getColumnIndex("product_id"));
        action = cursor.getString(cursor.getColumnIndex("action"));
    }
}
