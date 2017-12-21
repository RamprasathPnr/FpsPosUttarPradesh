package com.omneagate.Util;

import lombok.Getter;
import lombok.Setter;

/**
 * SingleTon class for maintain the sessionId
 */
public class SessionId {
    private static SessionId mInstance = null;

    @Getter
    @Setter
    private String sessionId;

    private SessionId() {
        sessionId = "";
    }

    public static SessionId getInstance() {
        if (mInstance == null) {
            mInstance = new SessionId();
        }
        return mInstance;
    }

}
