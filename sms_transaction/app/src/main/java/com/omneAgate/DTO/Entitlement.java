package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

/**
 * Entitlement for user
 */
@Data
public class Entitlement implements Serializable {
    long id; //id for the entitlement

    String productName; //name of the product

    double productPrice;  //price for the product of 1 kg or 1 lit or 1 quantity

    double entitledQuantity; // entitlement available

    double currentQuantity;        //Total amount of value user bought

    double totalAmount;  // Total amount user bought

    String unit;

    long productId;

}
