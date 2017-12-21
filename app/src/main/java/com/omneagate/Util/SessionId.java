package com.omneagate.Util;

import java.util.Date;

import lombok.Data;

/**
 * SingleTon class for maintain the sessionId
 */
@Data
public class SessionId {

    private static com.omneagate.Util.SessionId mInstance = null;


    private String sessionId;

    private String localpasword;


    private long userId;


    private String transactionId;


    private String userName;


    private boolean qrOTPEnabled = false;

     private String fpsCode;

    private long fpsId;

    private Date loginTime;

    private Date lastLoginTime;



    private SessionId() {
        sessionId = "";
        localpasword = "";
        userName = "";
        fpsCode = "";
        userId = 0l;
        fpsId = 0l;
        transactionId = "";
        loginTime = new Date();
        lastLoginTime = new Date();
    }

    public static synchronized com.omneagate.Util.SessionId getInstance() {
        if (mInstance == null) {
            mInstance = new com.omneagate.Util.SessionId();
        }
        return mInstance;
    }

}
