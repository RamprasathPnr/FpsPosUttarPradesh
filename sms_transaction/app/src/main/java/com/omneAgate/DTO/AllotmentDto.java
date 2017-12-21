package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

@Data
public class AllotmentDto {

    Long id;

    long productId;

    String cardtypeId;

    double allotmentLimit;

    boolean calcAllotment;

    long districtId;

    String createdBy;

    String modifiedBY;

    long createDate;

    long modifiedDate;

    public AllotmentDto() {

    }

    public AllotmentDto(Cursor cur) {
        id = cur.getLong(cur
                .getColumnIndex(FPSDBHelper.KEY_ID));

        productId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_PRODUCT_ID));

        allotmentLimit = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_LIMIT));

        districtId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_DISTRICT));

        cardtypeId = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_CARD_TYPE));

        int calculateAllot = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_CALC_ALLOTMENT_ALLOTMENT));

        calcAllotment = false;
        if (calculateAllot == 0) {
            calcAllotment = true;
        }

    }
}
