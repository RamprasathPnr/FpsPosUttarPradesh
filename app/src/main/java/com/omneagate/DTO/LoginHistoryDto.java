package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.SessionId;

import lombok.Data;

/**
 * Created by user1 on 27/7/15.
 */
@Data
public class LoginHistoryDto extends BaseDto {

    String loginTime;

    String loginType;

    long userId;

    String logoutTime;

    String logoutType;

    long fpsId;

    String transactionId;

    String deviceId;

    String sessionid;

    public LoginHistoryDto() {

    }

    public LoginHistoryDto(Cursor cur) {
        loginTime = cur.getString(cur.getColumnIndex("login_time"));
        loginType = cur.getString(cur.getColumnIndex("login_type"));
        logoutTime = cur.getString(cur.getColumnIndex("logout_time"));
        logoutType = cur.getString(cur.getColumnIndex("logout_type"));
        transactionId = cur.getString(cur.getColumnIndex("transaction_id"));
        fpsId = cur.getLong(cur.getColumnIndex("fps_id"));
        userId = cur.getLong(cur.getColumnIndex("user_id"));
        try {
            sessionid = SessionId.getInstance().getSessionId();
        }
        catch(Exception e) {

        }
    }
}
