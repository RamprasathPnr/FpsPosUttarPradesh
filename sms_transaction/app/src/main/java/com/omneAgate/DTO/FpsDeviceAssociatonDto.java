package com.omneagate.DTO;

import java.util.Date;

import lombok.Data;

@Data
public class FpsDeviceAssociatonDto {

    long id;                //Unique id primary key

    long fpsId;

    String oldDeviceId;

    String newDeviceId;

    Date createdDate;
}
