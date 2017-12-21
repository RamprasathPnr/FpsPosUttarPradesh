package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class ValidateOtpDto extends  BaseDto implements Serializable {

    String userName;
    Long fpsId;
    String deviceId;
    String otp;

}
