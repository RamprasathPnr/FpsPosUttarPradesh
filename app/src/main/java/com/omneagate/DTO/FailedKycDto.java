package com.omneagate.DTO;

import android.database.Cursor;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 18/11/15.
 */

@Data
public class FailedKycDto extends  BaseDto implements Serializable {

    long id;

    byte[] fingerPrintData;

    String aadharNumber;

    String beneficiaryId;

    public FailedKycDto(Cursor cur) {
        id = cur.getLong(cur.getColumnIndex("_id"));
        fingerPrintData = cur.getBlob(cur.getColumnIndex("fingerPrintData"));
        aadharNumber = cur.getString(cur.getColumnIndex("aadharNumber"));
        beneficiaryId = cur.getString(cur.getColumnIndex("benefId"));
    }

    public FailedKycDto() {}
}
