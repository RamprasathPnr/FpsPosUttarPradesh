package com.omneagate.DTO;

import java.util.List;

import lombok.Data;

@Data
public class GroupDto {
    /**
     * auto generated
     */
    Long id;

    /**
     * Name of the group
     */
    String groupName;

    /**
     * collection of product
     */
    List<ProductDto> productDto;

    /**
     * status flag
     */
    Boolean deleted;

    String unit;

    String localUnit;
}
