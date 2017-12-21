package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 17/6/15.
 */
@Data
public class GlobalConfigsDTO extends BaseDto implements Serializable {
    long id;
    String configName;
    String desc;
    boolean active;
}
