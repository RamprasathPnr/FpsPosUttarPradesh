package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class VillageDto implements Serializable {

    Long id;        //Unique id

    String name;    //Name of the village

    String code;    //two digit village code

    Long talukId;
}
