package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 8/5/15.
 */


@Data
public class OtpRegenerateDto extends  BaseDto{

    String rationCardNumber;

    String mobileNumber;

    String deviceNumber;

   // TransactionTypes transactionType;



}
