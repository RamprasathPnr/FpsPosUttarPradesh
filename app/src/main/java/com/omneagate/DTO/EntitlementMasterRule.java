package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

/**
 * Created by user1 on 28/4/15.
 */
@Data
public class EntitlementMasterRule {

    long id;

    long productId;

    long cardTypeId;
    long groupId;

    ProductDto productDto;

    CardTypeDto cardTypeDto;

    boolean calcRequired;

    boolean personBased;

    boolean regionBased;

    boolean hasSpecialRule;

    double quantity;

    long lastUpdatedTime;

    long lastUpdatedBy;

    boolean minimumQty;

    Boolean isDeleted;

    boolean overridePrice;

    GroupDto groupDto;

    public EntitlementMasterRule(Cursor cursor) {
        id = cursor.getLong(cursor
                .getColumnIndex(FPSDBHelper.KEY_ID));

        productId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_PRODUCT_ID));

        cardTypeId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_CARD_TYPE));

        groupId = cursor.getLong(cursor
                .getColumnIndex("groupId"));

        calcRequired = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_IS_CALC)));

        quantity = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_QUANTITY));

        personBased = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_IS_PERSON)));

        regionBased = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_IS_REGION)));


        hasSpecialRule = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_HAS_SPECIAL)));

        minimumQty = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_MINIMUM)));

        overridePrice = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_OVERRIDE)));

        isDeleted = returnBooleanDelete(cursor.getInt(cursor
                .getColumnIndex("isDeleted")));

    }

    private boolean returnBoolean(int value) {
        return value == 1;

    }

    private boolean returnBooleanDelete(int value) {
        if (value == 1) {
            return true;
        }
        return false;
    }
}
