package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

@Data
public class AgeGroupDto {

    Long id;

    String name;

    long productId;

    int fromRange;

    int toRange;

    double quantity;

    public AgeGroupDto() {

    }

    public AgeGroupDto(Cursor cur) {

        id = cur.getLong(cur
                .getColumnIndex(FPSDBHelper.KEY_ID));

        name = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_AGE_NAME));

        productId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_AGE_PRODUCT_ID));

        fromRange = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_AGE_FROM));
        toRange = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_AGE_TO));
        quantity = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_AGE_QUANTITY));


    }
}
