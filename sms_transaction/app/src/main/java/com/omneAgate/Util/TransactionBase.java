package com.omneagate.Util;

import com.omneagate.DTO.TransactionBaseDto;

import lombok.Getter;
import lombok.Setter;


/**
 * Singleton class for Response from  server for TransactionBase
 */
public class TransactionBase {
    private static TransactionBase mInstance = null;

    @Getter
    @Setter
    private TransactionBaseDto transactionBase;

    private TransactionBase() {
        transactionBase = new TransactionBaseDto();
    }

    public static TransactionBase getInstance() {
        if (mInstance == null) {
            mInstance = new TransactionBase();
        }
        return mInstance;
    }

}
