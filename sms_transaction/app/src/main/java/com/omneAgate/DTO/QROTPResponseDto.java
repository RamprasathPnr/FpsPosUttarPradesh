package com.omneagate.DTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 13/3/15.
 */
@Data
public class QROTPResponseDto extends BaseDto implements Serializable {

    /**
     * Generated version number
     */
    private static final long serialVersionUID = 6912565505388630528L;

    /**
     * list of entitlement
     */
    List<Entitlement> entitlementList;

    /**
     * Unique generated referenceId
     */
    String referenceId;

    /**
     * Unique family code number
     */
    String ufc;

    /**
     * OTP Related information
     */
    OTPTransactionDto otpTransactionDto;

}