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
    TalukDto taluk;

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
    long createdby;

    // userid
    long modifiedby;

    String contactPersonName;

    long categoryId;
//"fpsStore":{"statusCode":0,"id":1,"code":"1001","active":true,"godown":{"id":1,"godownCode":"101","name":"Godown_thirukuralar","taluk":{"id":1,"name":"taluk2","code":"1001","districtDto":{"statusCode":0,"id":1,"code":"1001","name":"Ariyaloor","stateDto":null,"status":true,"createdById":1,"createdDate":1426809600000,"modifiedById":null,"modifiedDate":null,"taluks":[]},"status":true,"createdById":1,"createdDate":1426809600000,"modifiedById":null,"modifiedDate":null,"villages":[]},"contactNumber":"2345678910","pincode":null,"address":null,"status":true,"createdDate":1426760532000,"modifiedDate":1426764576000,"createdById":1,"modifiedById":1,"contactPersonName":"Arun","categoryId":1},"createdBy":1,"modifiedBy":1,"createDate":1426760615000,"modifiedDate":1426764505000,"device":null}}}

    public GodownDto() {

    }

    public GodownDto(Cursor c) {
        long godownId = c.getLong(c.getColumnIndex(FPSDBHelper.KEY_ID));
    }


}