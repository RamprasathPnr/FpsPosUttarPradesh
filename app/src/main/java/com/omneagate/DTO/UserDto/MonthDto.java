package com.omneagate.DTO.UserDto;

import lombok.Data;

/**
 * Created by root on 10/3/17.
 */
@Data
public class MonthDto  {
    private int code;
    private String month;

    public MonthDto(int code ,String month){
        this.code=code;
        this.month=month;

    }
}
