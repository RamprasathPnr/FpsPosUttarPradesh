package com.omneagate.DTO.UserDto;

import android.database.Cursor;

import lombok.Data;

/**
 * Created by user1 on 9/9/15.
 */

@Data
public class StockCheckDto {

    long productId;

    Double quantity;

    String name;

    String unit;

    String localUnit;

    String localName;

    Double sold;

    public StockCheckDto() {

    }

    public StockCheckDto(Cursor cursor) {

        productId = cursor.getLong(cursor.getColumnIndex("product_id"));

        quantity = cursor.getDouble(cursor.getColumnIndex("quantity"));

        sold = cursor.getDouble(cursor.getColumnIndex("sold"));

        name = cursor.getString(cursor.getColumnIndex("name"));

        unit = cursor.getString(cursor.getColumnIndex("unit"));

        localName = cursor.getString(cursor.getColumnIndex("local_name"));

        localUnit = cursor.getString(cursor.getColumnIndex("local_unit"));
    }
}
