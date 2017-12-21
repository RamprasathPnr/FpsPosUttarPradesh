package com.omneagate.DTO;


import java.io.Serializable;

import lombok.Data;

@Data
public class TalukDto implements Serializable {

    Long Id;        //Unique id

    String name;    //Name of the taluk

    String code;    //two digit taluk code

    Long districtId;

    Object villages;

}
