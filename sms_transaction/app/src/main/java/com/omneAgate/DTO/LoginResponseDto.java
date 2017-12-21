package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.DTO.EnumDTO.DeviceStatus;

import java.io.Serializable;

import lombok.Data;

@Data
public class LoginResponseDto extends BaseDto implements Serializable {

    boolean authenticationStatus;   // Status of Authentication - true - authenticated  , false - not authenticated

    String sessionid;    //Session id only if authentication is successful.

    UserDetailDto userDetailDto; //user details

    DeviceStatus deviceStatus;

    String key;
    String providerNum;

    String prefixKey;


    public LoginResponseDto() {

    }

    public LoginResponseDto(Cursor cur) {
        userDetailDto = new UserDetailDto(cur);

    }
}
