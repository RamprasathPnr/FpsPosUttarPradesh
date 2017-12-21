package com.omneagate.DTO;

import android.database.Cursor;
import android.util.Log;

import com.omneagate.DTO.EnumDTO.DeviceStatus;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class LoginResponseDto extends BaseDto implements Serializable {

    boolean authenticationStatus;   // Status of Authentication - true - authenticated  , false - not authenticated

    String sessionid;    //Session id only if authentication is successful.

    UserDetailDto userDetailDto; //user details

    DeviceStatus deviceStatus;

    String key;

    long serverTime;

    String timezone;

    long loginDetailsId;

    List<GlobalConfigsDTO> globalConfigs;

    Set<AppfeatureDto> roleFeatures;

    public LoginResponseDto() {

    }

    public LoginResponseDto(Cursor cur) {

        Log.e("LoginResponseDto ", "LoginResponseDto");

        userDetailDto = new UserDetailDto(cur);
    }
}
