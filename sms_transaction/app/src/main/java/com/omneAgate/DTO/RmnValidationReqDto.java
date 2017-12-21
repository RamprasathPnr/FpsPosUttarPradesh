package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 17/3/15.
 */
@Data
public class RmnValidationReqDto extends BaseDto {

    /**
     * Unique QR code
     */
    String ufc;

    /**
     * old ration number
     */
    String oldCardNumber;

    /**
     * card Type of beneficiary
     */
    char cardType;


    /**
     * cylinder of beneficiary
     */
    int noOfCylinder;


    /**
     * mobileNumber of beneficiary
     */
    String mobileNumber;


    /**
     * mobileNumber of beneficiary
     */
    String refNumber;


    /**
     * OTP details
     */
    OTPTransactionDto otpTransactionDto;


    /**
     * Device number
     */
    String deviceNumber;

}
