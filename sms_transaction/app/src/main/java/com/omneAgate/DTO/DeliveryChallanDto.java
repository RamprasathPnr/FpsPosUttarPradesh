package com.omneagate.DTO;

import java.util.Date;

import lombok.Data;

@Data
public class DeliveryChallanDto {

    //Vehicle number
    String vehicleNumber;

    //Driver Name
    String driverName;

    //Driver mobile number
    String driverMobileNumber;

    //created chellan date
    Date createdDate;
}
