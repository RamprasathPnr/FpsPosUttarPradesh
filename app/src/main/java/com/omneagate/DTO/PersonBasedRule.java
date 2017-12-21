package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

/**
 * Created by user1 on 28/4/15.
 */

@Data
public class PersonBasedRule {

    long id;

    long productId;
    long groupId;

    double min;

    ProductDto productDto;

    double max;

    double perChild;

    double perAdult;

    long lastUpdatedDate;

    long lastUpdatedBy;

    Boolean isDeleted;

    GroupDto groupDto;

    CardTypeDto cardTypeDto;

    long cardTypeId;


    public PersonBasedRule() {

    }

    public PersonBasedRule(Cursor cursor) {

        id = cursor.getLong(cursor
                .getColumnIndex(FPSDBHelper.KEY_ID));

        productId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_PRODUCT_ID));

        cardTypeId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_CARD_TYPE_ID));

        groupId = cursor.getLong(cursor
                .getColumnIndex("groupId"));

        min = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_MIN));

        max = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_MAX));

        perChild = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_CHILD));

        perAdult = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_PERSON_ADULT));

        isDeleted = returnBoolean(cursor.getInt(cursor
                .getColumnIndex("isDeleted")));





    }

    private boolean returnBoolean(int value) {
        if (value == 1) {
            return true;
        }
        return false;
    }
}
