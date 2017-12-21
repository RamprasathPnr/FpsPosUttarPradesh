package com.omneagate.DTO;

import lombok.Data;

@Data
public class DistrictDto {

    Long id;

    // district code
    String code;

    // district name
    String name;

    Long stateId;

}
