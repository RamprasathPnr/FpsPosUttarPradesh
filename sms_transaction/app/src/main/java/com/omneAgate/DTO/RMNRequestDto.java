package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 16/3/15.
 */
@Data
public class RMNRequestDto extends BaseDto implements Serializable {

    /**
     * generated long id
     */
    private static final long serialVersionUID = -1999117623018939167L;

    /**
     * beneficiary QRCode
     */
    String mobileNumber;

    /**
     * Device Id
     */
    String deviceId;

    /**
     * Fps Store Id
     */
    private long fpsStoreId;

    /**
     * Bill related information
     */
    BillDto billDto;

    /**
     * Bill related information
     */
    String refNumber;

}
