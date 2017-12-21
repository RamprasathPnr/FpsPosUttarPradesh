package com.omneagate.DTO.EnumDTO;

/**
 * Created by user1 on 21/7/15.
 */
public enum UserRoles {

    SALES_ORDER_MENU(201, "Sale Order"),

    SALES_ORDER(202, "Sales Order"),

    RATION_CARD_BASED(203, "Ration Card Based"),

    RMN_BASED(204, "RMN Based"),

    QR_BASED(205, "QR Based"),

    CARD_ACTIVATION_MENU(206, "Card Activation Menu"),


    CARD_ACTIVATION_(207, "Card Activation"),

    RATION_CARD_ACTIVATION(208, "Ration Card Activation"),

    CARD_REGISTRATION(209, "FPS Card Registration"),

    CARD_ACTIVATION(210, "FPS Card Activation"),

    CARD_REG_REQ(211, "Card Registration Request"),

    STOCK_MGMT_MENU(212, "Stock Management Menu"),

    STOCK_MGMT(213, "Stock Management"),

    STOCK_INWARD(214, "Stock Inward"),

    STOCK_STATUS(215, "Stock Status"),

    TRANSACTION_MENU(216, "Transactions Menu"),

    TRANSACTION(217, "Transactions"),

    CLOSE_SALE_MENU(218, "Close Sales Menu"),

    CLOSE_SALE(219, "Close Sales"),

    OTHER_MENUS(220, "Other Menu"),

    OTHER_MENU(221, "Other Menu"),

    BENE_VIEW(222, "Benificiary View"),

    RESTORE_DB(223, "Restore DB"),

    RETRIEVE_DB(224, "Retrive DB");

    /**
     * The error code.
     */
    final private int id;

    /**
     * The error description.
     */
    final private String role;

    UserRoles(int id, String role) {
        this.id = id;
        this.role = role;
    }
}
