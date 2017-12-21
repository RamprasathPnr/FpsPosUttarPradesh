package com.omneagate.DTO;


import lombok.Data;

@Data
public class AppfeatureDto {

    Long id;

    String name;

    String featureId;

    String featureName;

    Long parentId;
}
