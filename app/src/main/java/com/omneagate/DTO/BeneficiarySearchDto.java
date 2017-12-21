package com.omneagate.DTO;

import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * Created by user1 on 26/6/15.
 */
@Data
public class BeneficiarySearchDto {

    String cardNo;

    String mobileNo;

    String cardType, localCardType;

    int noOfChild;

    int noOfAdult;

    int noOfCylinder;

    boolean mobileAvail;

    boolean aadharAvail;

    int benefId;

    String ufc_code;

    public BeneficiarySearchDto() {

    }

    public BeneficiarySearchDto(Cursor cur) {

        noOfChild = cur.getInt(cur
                .getColumnIndex("num_of_child"));

        noOfCylinder = cur.getInt(cur
                .getColumnIndex("num_of_cylinder"));

        noOfAdult = cur.getInt(cur
                .getColumnIndex("num_of_adults"));

        cardNo = cur.getString(cur
                .getColumnIndex("old_ration_card_num"));

        mobileNo = cur.getString(cur
                .getColumnIndex("aRegister"));

        if (mobileNo == null || mobileNo.equals("-1")) {
            mobileNo = "";
        }

        mobileAvail = false;

        String mobileNo = cur.getString(cur
                .getColumnIndex("mobile"));

        if (StringUtils.isNotEmpty(mobileNo)) {
            mobileAvail = true;
        }

        aadharAvail = false;

        String aadharNo = cur.getString(cur
                .getColumnIndex("aadharNumber"));

        if (StringUtils.isNotEmpty(aadharNo)) {
            aadharAvail = true;
        }

        cardType = cur.getString(cur
                .getColumnIndex("description"));

        localCardType = cur.getString(cur
                .getColumnIndex("localDescription"));

        benefId = cur.getInt(cur
                .getColumnIndex("_id"));

        ufc_code = cur.getString(cur.getColumnIndex("ufc_code"));
    }
}
