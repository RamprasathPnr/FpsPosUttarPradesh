package com.omneagate.DTO;

import lombok.Getter;
import lombok.Setter;

public enum TransactionTypes {


    SALE_QR_OTP_DISABLED(100),
    SALE_QR_OTP_GENERATE(101),
    SALE_QR_OTP_AUTHENTICATION(102),
    SALE_RMN_GENERATE(103),
    SALE_RMN_AUTHENTICATE(104),
    SALE_OLD_CARD(105),
    BENEFICIARY_RMN_VALIDATION(200),
    BENEFICIARY_UPDATION(201),
    BENEFICIARY_ACTIVATION(202),
    BENEFICIARY_REGREQUEST_NEW(203),
    BENEFICIARY_VALIDATION_NEW(204),
    BENEFICIARY_ACTIVATION_NEW(205),
    HEART_BEAT(300);

    @Getter
    @Setter
    private int txnType;

    private TransactionTypes(int type) {

        txnType = type;
    }
}
