package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

@Data
public class AllotmentMappingDto {

    Long id;                    //Autogenerated unique identifier

    double startRange;

    double allotedQuantity;

    double endRange;

    String allotment;

    long districtId;

    String description;            //Description about the card type

    long productId;

    public AllotmentMappingDto() {

    }

    public AllotmentMappingDto(Cursor cur) {
        id = cur.getLong(cur
                .getColumnIndex(FPSDBHelper.KEY_ID));

        startRange = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_MAP_START));

        endRange = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_MAP_END));

        allotment = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_MAP_ALLOT));

        productId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_MAP_PRODUCT_ID));

        districtId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_MAP_DISTRICT));

        allotedQuantity = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_MAP_ALLOT));
     /*   values.put(FPSDBConstants.KEY_ALLOTMENT_MAP_DISTRICT, allotmentMapping.getDistrictId());
        values.put(FPSDBConstants.KEY_ALLOTMENT_MAP_DESCRIPTION, allotmentMapping.getDescription());
        values.put(FPSDBConstants.KEY_ALLOTMENT_MAP_PRODUCT_ID, allotmentMapping.getProductId());*/
    }
}
