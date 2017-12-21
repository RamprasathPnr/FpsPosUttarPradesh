package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 18/11/15.
 */

@Data
public class BeneficiaryUpdateDto extends  BaseDto implements Serializable{

    long beneficiaryId;

    String mobileNumber;

//    String familyHeadAadharNumber;

    String aregisterNumber;

    String deviceNumber;

    AadharSeedingDto aadhaarSeedingDto;


}
