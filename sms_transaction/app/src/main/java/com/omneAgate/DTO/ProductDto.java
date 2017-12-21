package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import java.io.Serializable;

import lombok.Data;

/**
 * This class is used to transfer Product related details
 *
 * @author user1
 */

@Data
public class ProductDto implements Serializable {

    Long id;

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


    public ProductDto() {

    }

    public ProductDto(Cursor cur) {

        id = cur.getLong(cur
                .getColumnIndex(FPSDBHelper.KEY_ID));

        name = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_PRODUCT_NAME));
        localProductName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_LPRODUCT_NAME));

        code = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_PRODUCT_CODE));

        productUnit = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_PRODUCT_UNIT));
        localProductUnit = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_LPRODUCT_UNIT));

        productPrice = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_PRODUCT_PRICE));


    }

}
