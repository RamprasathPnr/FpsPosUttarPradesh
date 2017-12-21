package com.omneagate.DTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;


@Data
public class QRTransactionResposneDto implements Serializable {

    List<Entitlement> entitlementList;

    String referenceId;

    String beneficiaryName;

    String ufc;

    long benficiaryId;

    long fpsId;

    String mobileNumber;

    String addressline1;        //Address

    String addressline2;        //Address

    String addressline3;        //Address

    String addressline4;        //Address

    String addressline5;        //Address

    ErrorResponse errorResponse;

}
