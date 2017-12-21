package com.omneagate.DTO;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class ReOrderLevelAlarmDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3540514651422545905L;

    long id;

    long FPSId;

    Date productId;

    long quantity;
}
