package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

/**
 * Created by user1 on 28/4/15.
 */

@Data
public class RegionBasedRule {


    long id;

    long productId;

    ProductDto productDto;

    int cylinderCount;

    boolean taluk;

    boolean city;

    boolean municipality;

    boolean cityHeadQuarter;

    double quantity;

    long lastUpdatedTime;

    long lastUpdatedBy;

    Boolean isDeleted;

    GroupDto groupDto;

    boolean hillyArea;
    boolean splArea;
    boolean townPanchayat;
    boolean villagePanchayat;
    public RegionBasedRule() {

    }

    public RegionBasedRule(Cursor cursor) {
        id = cursor.getLong(cursor
                .getColumnIndex(FPSDBHelper.KEY_ID));

        productId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_PRODUCT_ID));

        cylinderCount = cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_CYLINDER));

        taluk = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_TALUK)));

        hillyArea = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_HILLAREA)));
        splArea = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_SPLAREA)));
        townPanchayat = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_TOWNPANCHAYAT)));
        villagePanchayat = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_VILLAGEPANCHAYAT)));

        quantity = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_QUANTITY));
        city = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_CITY)));

        municipality = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_MUNICIPALITY)));


        cityHeadQuarter = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_HEAD)));


    }

    private boolean returnBoolean(int value) {
        return value == 1;

    }

}
