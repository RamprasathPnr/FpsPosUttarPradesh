package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.DTO.EnumDTO.DeviceStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class ConfigurationResponseDto extends BaseDto implements Serializable {

    HashMap<String,String> posGlobalConfigMap;

    public ConfigurationResponseDto() {

    }

    /*public ConfigurationResponseDto(Cursor cur) {
        userDetailDto = new UserDetailDto(cur);
    }*/
}
