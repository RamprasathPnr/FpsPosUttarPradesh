package com.omneagate.DTO;

import android.database.Cursor;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 18/11/15.
 */

@Data
public class ProxyDetailDto extends  BaseDto implements Serializable {

    long id;

    long serverId;

    BeneficiaryDto beneficiary;

    BeneficiaryMemberDto beneficiaryMember;

    String name;

    String uid;

    String mobile;

    long dob;

    long createdDate;

    long createdBy;

    long modifiedDate;

    long modifiedBy;

    String requestStatus;

    String syncStatus;

    public ProxyDetailDto(Cursor cur) {
        id = cur.getLong(cur.getColumnIndex("_id"));
        serverId = cur.getLong(cur.getColumnIndex("server_id"));

        beneficiary = new BeneficiaryDto();
        Long benefId = cur.getLong(cur.getColumnIndex("benef_id"));
        if(benefId != 0) {
            beneficiary.setId(benefId);
        }

        beneficiaryMember = new BeneficiaryMemberDto();
        Long benefMemberId = cur.getLong(cur.getColumnIndex("benef_member_id"));
        if(benefMemberId != 0) {
            beneficiaryMember.setId(benefMemberId);
        }

        name = cur.getString(cur.getColumnIndex("name"));
        uid = cur.getString(cur.getColumnIndex("uid"));
        mobile = cur.getString(cur.getColumnIndex("mobile"));
        dob = cur.getLong(cur.getColumnIndex("dob"));
        createdDate = cur.getLong(cur.getColumnIndex("created_date"));
        createdBy = cur.getLong(cur.getColumnIndex("created_by"));
        modifiedDate = cur.getLong(cur.getColumnIndex("modified_date"));
        modifiedBy = cur.getLong(cur.getColumnIndex("modified_by"));
        requestStatus = cur.getString(cur.getColumnIndex("approval_status"));
        syncStatus = cur.getString(cur.getColumnIndex("sync_status"));
    }

    public ProxyDetailDto() {

    }
}
