package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

@Data
public class GodownDto {

    // Godown id
    long id;
    // Godown code
    String godownCode;

    // Name of the godown
    String name;

    // District Name
    String district;

    // Taluk name
    Object taluk;

    // Godwon contact number
    String contactNumber;

    // Pincode information
    String pincode;

    // Address
    String address;

    // Active/Inactive
    boolean status;

    // Date of creation
    long createdDate;

    // Date of modification
    long modifiedDate;

    // user id
    long createdById;

    // userid
    long modifiedById;

    String contactPersonName;

    long categoryId;


    public GodownDto() {

    }

    public GodownDto(Cursor c) {
        long godownId = c.getLong(c.getColumnIndex(FPSDBHelper.KEY_ID));
    }


}