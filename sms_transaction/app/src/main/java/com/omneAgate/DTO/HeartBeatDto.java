package com.omneagate.DTO;

import lombok.Data;

@Data
public class HeartBeatDto extends BaseDto {


    String fpsId; //Fair price shop identifier and same will be echoed back to FPS device from the server

    //    int health;
//
    int batteryLevel;
//
//    int plugged;
//
//    boolean present;
//
//    int status;
//
//    String technology;
//
//    int temperature;
//
//    int voltage;


}
