package com.omneagate.Util;

import com.omneagate.DTO.LoginResponseDto;

import lombok.Getter;
import lombok.Setter;

/**
 * SingleTon class for maintain the sessionId
 */
public class LoginData {
    private static com.omneagate.Util.LoginData mInstance = null;

    @Getter
    @Setter
    private LoginResponseDto loginData;


    @Getter
    @Setter
    private long fpsId;

    @Getter
    @Setter
    private String shopNo;

    @Getter
    @Setter
    private String fpsUsername;

    @Getter
    @Setter
    private String fpsUserUid;

    @Getter
    @Setter
    private String transactionId;
    @Getter
    @Setter
    private String distCode;

    @Getter
    @Setter
    private String memberId;

    @Getter
    @Setter
    private String uid;
    @Getter
    @Setter
    private String memberName;

    @Getter
    @Setter
    private String rationCardNo;

    @Getter
    @Setter
    private String dealerType;

    @Getter
    @Setter
    private String cashMode;

    @Getter
    @Setter
    private int payType;

    @Getter
    @Setter
    private String currentMonth;

    @Getter
    @Setter
    private String currentYear;

    @Getter
    @Setter
    private String TwoDigitsShopID;

    @Getter
    @Setter
    private String responseTime;
    @Getter
    @Setter
    private String weighingScale;

    @Getter
    @Setter
    private String eposMessage;

    @Getter
    @Setter
    private String rcNoEntered;



    private LoginData() {
        loginData = new LoginResponseDto();
    }

    public static synchronized com.omneagate.Util.LoginData getInstance() {
        if (mInstance == null) {
            mInstance = new com.omneagate.Util.LoginData();
        }
        return mInstance;
    }

}
