package com.omneagate.DTO;

import android.database.Cursor;

import org.bouncycastle.jce.provider.symmetric.Grain128;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReconciliationPosStockDetailDto extends BaseDto implements Serializable {

    Long id;

    Long reconciliationRequestId;

    Long productId;

    Double posQuantity;

    Double serverQuantity;

    Double differenceQuantity;

    Long productGroupId;

    Long villageId;

    Long talukId;

    Long districtId;

    String stockProcess;

    Integer errorCode;

    String errorDesc;

    public ReconciliationPosStockDetailDto() {

    }

    public ReconciliationPosStockDetailDto(Cursor cur) {
        /*cardType = cur.getString(cur.getColumnIndex("description"));
        lCardType = cur.getString(cur.getColumnIndex("localDescription"));
        noOfCards = cur.getString(cur.getColumnIndex("typeCount"));*/
    }

}
