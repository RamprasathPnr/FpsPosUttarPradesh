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

    ProductDto productDto;

    CardTypeDto cardTypeDto;

    boolean calcRequired;

    boolean personBased;

    boolean regionBased;

    boolean hasSpecialRule;

    double quantity;

    long lastUpdatedTime;

    long lastUpdatedBy;

    public EntitlementMasterRule(Cursor cursor) {
        id = cursor.getLong(cursor
                .getColumnIndex(FPSDBHelper.KEY_ID));

        productId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_PRODUCT_ID));

        cardTypeId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_CARD_TYPE));

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

    }

    private boolean returnBoolean(int value) {
        if (value == 1)
            return true;
        return false;

    }
}
