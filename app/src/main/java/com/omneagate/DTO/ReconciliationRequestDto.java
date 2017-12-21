package com.omneagate.DTO;

import android.database.Cursor;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class ReconciliationRequestDto extends  BaseDto implements Serializable {

    Long id;

    Long createdDate;

    Long modifiedDate;

    String applicationType;

    Long fpsId;

    String transactionId;

    List<ReconciliationPosStockDetailDto> posStockDetailDtos;

    String status;

    Integer errorCode;

    String errorDesc;

    Long villageId;

    Long talukId;

    Long districtId;

    Long requestDateTime;

    Long responseDateTime;

    public ReconciliationRequestDto() {

    }

    public ReconciliationRequestDto(Cursor cur) {
        requestDateTime = cur.getLong(cur.getColumnIndex("requestDateTime"));
        responseDateTime = cur.getLong(cur.getColumnIndex("responseDateTime"));
        transactionId = cur.getString(cur.getColumnIndex("reconciliationId"));
        status = cur.getString(cur.getColumnIndex("status"));
        id = cur.getLong(cur.getColumnIndex("serverId"));
    }

}
