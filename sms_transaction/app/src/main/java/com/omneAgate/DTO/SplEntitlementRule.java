package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

/**
 * Created by user1 on 28/4/15.
 */
@Data
public class SplEntitlementRule {
    long id;

    long productId;

    ProductDto productDto;

    long districtId;

    long talukId;

    long villageId;

    int cylinderCount;

    boolean taluk;

    boolean city;

    boolean municipality;

    boolean cityHeadQuarter;

    double quantity;

    boolean add;

    long lastUpdatedTime;

    long lastUpdatedBy;

    public SplEntitlementRule(Cursor cursor) {

        id = cursor.getLong(cursor
                .getColumnIndex(FPSDBHelper.KEY_ID));

        productId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_PRODUCT_ID));

        districtId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_DISTRICT));

        talukId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_TALUK));

        villageId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_VILLAGE));

        cylinderCount = cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_CYLINDER));

        quantity = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_QUANTITY));

        municipality = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_ISMUNICIPALITY)));

        add = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_ISADD)));

        cityHeadQuarter = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_ISCITY_HEAD)));

        city = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_ISCITY)));

        taluk = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_ISTALUK)));
    }

    private boolean returnBoolean(int value) {
        if (value == 1)
            return true;
        return false;

    }

}
