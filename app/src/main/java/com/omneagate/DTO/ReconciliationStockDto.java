package com.omneagate.DTO;

import android.database.Cursor;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class ReconciliationStockDto extends  BaseDto implements Serializable {

    Long productId;

    Double quantity;

    String name;

    String lName;

    String unit;

    String lUnit;

    public ReconciliationStockDto() {

    }

    public ReconciliationStockDto(Cursor cur) {
        productId = cur.getLong(cur.getColumnIndex("product_id"));
        quantity = cur.getDouble(cur.getColumnIndex("quantity"));
        name = cur.getString(cur.getColumnIndex("name"));
        lName = cur.getString(cur.getColumnIndex("local_name"));
        unit = cur.getString(cur.getColumnIndex("unit"));
        lUnit = cur.getString(cur.getColumnIndex("local_unit"));
    }

}
