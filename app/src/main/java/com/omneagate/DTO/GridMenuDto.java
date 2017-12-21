package com.omneagate.DTO;

import lombok.Data;

@Data
public class GridMenuDto {
    String name;
    int imgId;
    String className;

    public GridMenuDto(String name, int imgId, String className) {
        this.name = name;
        this.imgId = imgId;
        this.className = className;
    }
}
