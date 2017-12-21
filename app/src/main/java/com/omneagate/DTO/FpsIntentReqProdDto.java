package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import lombok.Data;

/**
 * Created by user1 on 23/3/15.
 */
@Data
public class FpsIntentReqProdDto {
    //Product Id
    long productId;

    // Product Name
    String name;

    //Product Quantity
    double quantity;


    public FpsIntentReqProdDto() {

    }

    public FpsIntentReqProdDto(Cursor c) {
        productId = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_FPS_INTENT_REQUEST_PRODUCT_ID));
        quantity = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_FPS_INTENT_REQUEST_QUANTITY));

    }


}
