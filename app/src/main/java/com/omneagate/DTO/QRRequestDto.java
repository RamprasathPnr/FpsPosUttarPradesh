package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 18/5/15.
 */
@Data
public class QRRequestDto extends BaseDto implements Serializable {

    /**
     * generated long id
     */
    private static final long serialVersionUID = 735929554428693363L;

    /**
     * beneficiary unique family code
     */
    String ufc;

    /**
     * Device Id
     */
    String deviceId;

    /**
     * Fps Store Id
     */
    long fpsStoreId;

    /**
     * Bill related information
     */
    BillDto billDto;

    /**
     * Bill related information
     */
    String refNumber;

    String mobileNum;

    String otpId;

    /**
     * otp used time
     */
    String otpUsedTime;

}
