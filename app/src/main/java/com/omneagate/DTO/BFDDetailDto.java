package com.omneagate.DTO;

import android.database.Cursor;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 18/11/15.
 */

@Data
public class BFDDetailDto extends  BaseDto implements Serializable {

    long id;

    BeneficiaryDto beneficiary;

//    BeneficiaryMemberDto beneficiaryMember;

    ProxyDetailDto proxyDetail;

    String bestFinger01;

    String bestFinger02;

    String bestFinger03;

    String bestFinger04;

    String bestFinger05;

    String bestFinger06;

    String bestFinger07;

    String bestFinger08;

    String bestFinger09;

    String bestFinger10;

    long createdDate;

    long createdBy;

    long modifiedDate;

    long modifiedBy;

    Boolean status;

    String syncStatus;

    public BFDDetailDto(Cursor cur) {

        beneficiary = new BeneficiaryDto();
        long benefId = cur.getLong(cur.getColumnIndex("benef_id"));
        beneficiary.setId(benefId);

        /*beneficiaryMember = new BeneficiaryMemberDto();
        long benefMemberId = cur.getLong(cur.getColumnIndex("benef_member_id"));
        beneficiaryMember.setId(benefMemberId);*/

        proxyDetail = new ProxyDetailDto();
        long proxyId = cur.getLong(cur.getColumnIndex("proxy_id"));
        proxyDetail.setId(proxyId);

        bestFinger01 = cur.getString(cur.getColumnIndex("finger_01"));
        bestFinger02 = cur.getString(cur.getColumnIndex("finger_02"));
        bestFinger03 = cur.getString(cur.getColumnIndex("finger_03"));
        bestFinger04 = cur.getString(cur.getColumnIndex("finger_04"));
        bestFinger05 = cur.getString(cur.getColumnIndex("finger_05"));
        bestFinger06 = cur.getString(cur.getColumnIndex("finger_06"));
        bestFinger07 = cur.getString(cur.getColumnIndex("finger_07"));
        bestFinger08 = cur.getString(cur.getColumnIndex("finger_08"));
        bestFinger09 = cur.getString(cur.getColumnIndex("finger_09"));
        bestFinger10 = cur.getString(cur.getColumnIndex("finger_10"));
        createdDate = cur.getLong(cur.getColumnIndex("created_date"));
        createdBy = cur.getLong(cur.getColumnIndex("created_by"));
        modifiedDate = cur.getLong(cur.getColumnIndex("modified_date"));
        modifiedBy = cur.getLong(cur.getColumnIndex("modified_by"));
        if (cur.getInt(cur.getColumnIndex("status")) == 1) {
            status = true;
        }
        else {
            status = false;
        }
        syncStatus = cur.getString(cur.getColumnIndex("sync_status"));
    }

    public BFDDetailDto() {

    }

}
