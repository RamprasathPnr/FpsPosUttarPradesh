package com.omneagate.Util;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * SingleTon class for maintain the sessionId
 */
public class TransactionMessage {
    private static TransactionMessage mInstance = null;

    @Getter
    @Setter
    private Map<String, String> transactionMessage;

    private TransactionMessage() {
        transactionMessage = new HashMap<String, String>();
    }

    public static TransactionMessage getInstance() {
        if (mInstance == null) {
            mInstance = new TransactionMessage();
        }
        return mInstance;
    }

}
