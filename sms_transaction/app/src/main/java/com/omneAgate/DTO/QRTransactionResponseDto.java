package com.omneagate.DTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class QRTransactionResponseDto extends BaseDto implements Serializable {

    List<EntitlementDTO> entitlementList;

    String referenceId;

    String ufc;

    long benficiaryId;

    long fpsId;

    String beneficiaryName;

    String mobileNumber;

    String addressline1;        //Address

    String addressline2;        //Address

    String addressline3;        //Address

    String addressline4;        //Address

    String addressline5;        //Address

    ErrorResponse errorResponse;

    char mode;

}