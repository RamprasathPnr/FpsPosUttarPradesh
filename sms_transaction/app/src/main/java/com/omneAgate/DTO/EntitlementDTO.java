package com.omneagate.DTO;

import lombok.Data;

/**
 * Used to set entitlement
 */
@Data
public class EntitlementDTO {

    double entitledQuantity;

    long productId;

    double currentQuantity;

    String productName;

    String lproductName;

    double productPrice;

    String productUnit;

    String lproductUnit;

    double totalPrice;

    double bought;

    double history;

}
