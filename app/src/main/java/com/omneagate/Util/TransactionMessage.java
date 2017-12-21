package com.omneagate.Util;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * SingleTon class for maintain the sessionId
 */
public class TransactionMessage {
    private static com.omneagate.Util.TransactionMessage mInstance = null;

    @Getter
    @Setter
    private Map<String, String> transactionMessage;

    private TransactionMessage() {
        transactionMessage = new HashMap<String, String>();
    }

    public static com.omneagate.Util.TransactionMessage getInstance() {
        if (mInstance == null) {
            mInstance = new com.omneagate.Util.TransactionMessage();
        }
        return mInstance;
    }

}
