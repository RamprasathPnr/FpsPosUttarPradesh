package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.activity.GlobalAppState;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 18/11/15.
 */

@Data
public class POSAadharAuthRequestDto extends  BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    String uid;
    byte[] fingerPrintData;
    long fpsId;
    long beneficiaryId;
    String authReponse;
    boolean authenticationStatus;
    long posRequestDate;
    long posResponseDate;
    long authRequestDate;
    long authResponseDate;

    public POSAadharAuthRequestDto(Cursor cur) {
        uid = cur.getString(cur.getColumnIndex("uid"));
        fpsId = cur.getInt(cur.getColumnIndex("fpsId"));
        beneficiaryId = cur.getInt(cur.getColumnIndex("benefId"));
        authReponse = cur.getString(cur.getColumnIndex("authResponse"));
        if((cur.getString(cur.getColumnIndex("authStatus"))).equalsIgnoreCase("true")) {
            authenticationStatus = true;
        }
        else {
            authenticationStatus = false;
        }
        posRequestDate = cur.getInt(cur.getColumnIndex("posReqDate"));
        posResponseDate = cur.getInt(cur.getColumnIndex("posRespDate"));
        authRequestDate = cur.getInt(cur.getColumnIndex("authReqDate"));
        authResponseDate = cur.getInt(cur.getColumnIndex("authRespDate"));
        fingerPrintData = cur.getBlob(cur.getColumnIndex("fingerPrintData"));
    }

    public POSAadharAuthRequestDto() {

    }
}
