package com.omneagate.DTO;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by user1 on 15-07-2016.
 */
@Data
public class POSOperatingHoursDto extends BaseDto implements Serializable{

    private static final long serialVersionUID = -6270128952317318830L;

    long id;

    long createdBy;
    long createdDate;

    long modifiedBy;
    long modifiedDate;

    String applicationType;
    long entityId;
    String day;
    String firstSessionOpeningTime;
    String firstSessionClosingTime;
    String secondSessionOpeningTime;
    String secondSessionClosingTime;

    long talukId;
    String entityGeneratedCode;
}
