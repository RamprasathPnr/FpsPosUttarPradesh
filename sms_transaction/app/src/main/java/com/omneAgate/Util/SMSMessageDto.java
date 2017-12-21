package com.omneagate.Util;

import lombok.Data;

/**
 * Created by user1 on 13/4/15.
 */
@Data
public class SMSMessageDto {

    int statusCode;
    int transactionType;
    String otpId;
    String otp;
    String billRef;
    String billId;
    String transactionId;
    String mobileNumber;
    String rationCardNumber;

}
