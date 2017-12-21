package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;

import lombok.Data;

/**
 * store the CloseOfProductDto
 */
@Data
public class CloseOfProductDto extends BaseDto implements Serializable {
    /**
     * serial version id of Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * auto generated id
     */
    long id;

    /**
     * closeId generated id
     */
    long closeId;

    /**
     * productid of the product
     */
    long productId;

    /**
     * quantity of the product
     */
    String totalQuantity;

    /**
     * total Cost
     */
    String totalCost;

    double openingStock;

    double closingStock;

    double inward;


    public CloseOfProductDto() {

    }

    public CloseOfProductDto(Cursor cur) {

        productId = cur.getLong(cur.getColumnIndex("productId"));
        inward = cur.getDouble(cur.getColumnIndex("inward"));
        openingStock = cur.getDouble(cur.getColumnIndex(FPSDBConstants.KEY_STOCK_HISTORY_OPEN_BALANCE));
        closingStock = cur.getDouble(cur.getColumnIndex(FPSDBConstants.KEY_STOCK_HISTORY_CLOSE_BALANCE));
        totalCost = cur.getString(cur.getColumnIndex("totalCost"));
        totalQuantity = cur.getString(cur.getColumnIndex("totalQuantity"));

    }
}
