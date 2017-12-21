package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class UpdateStockRequestDto extends BaseDto implements Serializable {


    String referenceId;                //auto generated referenceId

    String ufc;                    //beneficiary QRCode

    String deviceId;                //Device Id

    long fpsStoreId;                //Fps Store Id

    BillDto billDto;

    String refNumber;

    long otpId;

    String otp;

    String rmn;

    String otpUsedTime;

}
