package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 5/3/15.
 */
@Data
public class DeviceRegistrationDto extends BaseDto {

    LoginDto loginDto;

    DeviceDetailsDto deviceDetailsDto;


}
