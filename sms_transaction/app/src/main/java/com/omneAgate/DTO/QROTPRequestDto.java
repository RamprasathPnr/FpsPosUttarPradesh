package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class QROTPRequestDto extends BaseDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8213689061500871089L;

    /**
     * qrcode of the beneficiary
     */
    String ufc;

    /**
     * deviceId of the fps store
     */
    String deviceId;

    /**
     * fps store id
     */
    long fpsStoreId;                //Fps Store Id

    /**
     * deviceId of the qrtransaction
     */
    BillDto billDto;                //Bill related information

    /**
     * registered mobile number of the beneficiary
     */
    long rmn;

    /**
     * Reference number for the transaction
     */
    String referenceNumber;

    OTPTransactionDto otpTransactionDto;


}