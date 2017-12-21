package com.omneagate.DTO;

import lombok.Data;

@Data
public class OTPTransactionDto {

    long id;

    long FPSId;

    long createddate;

    String otp;

    String UFC;

    long Deviceid;

    long expirytime;

    long rmn;

    long posTime;
}  
