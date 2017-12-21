package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class VersionUpgradeDto extends BaseDto implements Serializable {


    private static final long serialVersionUID = 1L;

    long id;

    /**
     * upgrade version
     */

    int version;

    /**
     * released date
     */

    long releaseDate;

    /**
     * server location
     */

    String location;

}
