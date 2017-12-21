package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 25/5/15.
 */
@Data
public class MenuDataDto {
    String name;
    String lName;
    int id;

    public MenuDataDto(String name, int id, String lName) {
        this.name = name;
        this.id = id;
        this.lName = lName;
    }
}
