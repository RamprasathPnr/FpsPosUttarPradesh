package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 18/11/15.
 */

@Data
public class AadharSeedingDto extends  BaseDto implements Serializable {

    long id;

    long aadhaarNum;

    long beneficiaryID;

    int serialNum;

    String channel = "FPS";

    long createdDate;



    String rationCardNumber;

    String uid;

    String name;

    Character gender;

    Long  yob;

    String co;

    String house;

    String street ;

    String lm;

    String loc;

    String vtc;

    String po;

    String dist;

    String subdist;

    String state;

    String pc;

    Long dob;

    long beneficiaryMemberID;

//    long relationId;

    String dateOfBirth;

    String scannedQRData;
}
