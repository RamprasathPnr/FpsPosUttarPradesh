package com.omneagate.DTO;

import lombok.Data;

/**
 * Used to set entitlement
 */
@Data
public class EntitlementDTO implements Comparable<com.omneagate.DTO.EntitlementDTO> {

    double entitledQuantity;

    long productId;

    double currentQuantity;

    String productName;

    String lproductName;

    double productPrice;

    String productUnit;

    String lproductUnit;

    double purchasedQuantity;

    double totalPrice;

    double bought;

    double history;

    long groupId;

    double nfsa_purchasedQuantity;

    @Override
    public int compareTo(com.omneagate.DTO.EntitlementDTO o) {
        Long first = this.getProductId();
        Long second = o.getProductId();
        return first.compareTo(second);
    }
}
