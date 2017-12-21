package com.omneagate.DTO;

import java.util.Date;

import lombok.Data;

@Data
public class RemoteLogDto {

    long id;                        //Primary Key value

    String deviceId;                    //device id

    String logMessage;                //error message

    String errorType;                //type of error- could be FATAL, WARN

    Date createDate;
}	
