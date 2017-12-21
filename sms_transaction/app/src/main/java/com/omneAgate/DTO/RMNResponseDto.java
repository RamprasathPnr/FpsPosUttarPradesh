package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 17/3/15.
 */
@Data
public class RMNResponseDto extends BaseDto {

    String benefRMN;

    boolean activationStatus;

    OTPTransactionDto otpTransactionDto;

    String referenceNum;

}
