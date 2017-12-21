package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 29/7/15.
 */
@Data
public class RollMenuDto {
    String name;
    int backgroundId;
    int imgId;
    String className;

    public RollMenuDto(String name, int imgId, int backgroundId, String className) {
        this.name = name;
        this.imgId = imgId;
        this.backgroundId = backgroundId;
        this.className = className;
    }
}
