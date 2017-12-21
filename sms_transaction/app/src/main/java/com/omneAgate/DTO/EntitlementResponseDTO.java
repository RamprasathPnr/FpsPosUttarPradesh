package com.omneagate.DTO;

import java.util.List;

import lombok.Data;

/**
 * Response received from server as entitlement
 */
@Data
public class EntitlementResponseDTO {
    String referenceId;
    List<EntitlementDTO> entitlementList;
    String beneficiaryName;
    String mobileNumber;
    String address;
    String addressline1;
    String addressline2;
    String addressline3;
    String addressline4;
    String addressline5;

}
