package com.omneagate.DTO;

import java.util.Date;

import lombok.Data;

/**
 * Created by user1 on 4/4/16.
 */

@Data
public class LogoutDto extends BaseDto {

    String sessionId;
    String logoutStatus;
    String logoutTime;

}
