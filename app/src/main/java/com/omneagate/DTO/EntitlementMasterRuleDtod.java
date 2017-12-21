package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

/**
 * Created by user1 on 28/4/15.
 */
@Data
public class EntitlementMasterRuleDtod {

    long id;

    long productId;

    long cardTypeId;


    boolean calcRequired;

    boolean personBased;

    boolean regionBased;

    boolean hasSpecialRule;

    double quantity;

    long lastUpdatedTime;

    long lastUpdatedBy;

    boolean minimumQty;

    boolean overridePrice;

    String name;                    //Name of the product

    String code;                    //Two digit product code

    boolean transmittedToPos;        //flag to indicate if this record needs to transmitted to POS if it is false

    boolean negativeIndicator;

    long modifiedDate;

    String modifiedby;

    long createdDate;

    String createdby;

    Double productPrice;

    String productUnit;

    String localProductName;

    String localProdName;

    String localProductUnit;

    String localProdUnit;

    long groupId;

    public EntitlementMasterRuleDtod(Cursor cursor) {
        id = cursor.getLong(cursor
                .getColumnIndex(FPSDBHelper.KEY_ID));

        productId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_PRODUCT_ID));

        calcRequired = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_IS_CALC)));

        quantity = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_QUANTITY));

        personBased = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_IS_PERSON)));

        regionBased = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_IS_REGION)));


        hasSpecialRule = returnBoolean(cursor.getInt(cursor
                .getColumnIndex("has_special_rule")));

        minimumQty = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_MINIMUM)));

        overridePrice = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_OVERRIDE)));

        productId = cursor.getLong(cursor
                .getColumnIndex("product_id"));
        groupId = cursor.getLong(cursor
                .getColumnIndex("group_id"));

        name = cursor.getString(cursor
                .getColumnIndex(FPSDBConstants.KEY_PRODUCT_NAME));

        productUnit = cursor.getString(cursor
                .getColumnIndex(FPSDBConstants.KEY_PRODUCT_UNIT));

        localProductName = cursor.getString(cursor
                .getColumnIndex("local_name"));


        localProductUnit = cursor.getString(cursor
                .getColumnIndex(FPSDBConstants.KEY_LPRODUCT_UNIT));

        productPrice = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_PRODUCT_PRICE));

    }

    private boolean returnBoolean(int value) {
        return value == 1;

    }
}
