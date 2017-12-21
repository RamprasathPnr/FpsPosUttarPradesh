package com.omneagate.DTO;

import lombok.Data;

/**
 * This class represents device associate request
 *
 * @author user1
 */

@Data
public class DeviceAssociateRequestDto {

    long fpsId;            //FPSStore id

    String deviceId;        //Device to be associated

}
