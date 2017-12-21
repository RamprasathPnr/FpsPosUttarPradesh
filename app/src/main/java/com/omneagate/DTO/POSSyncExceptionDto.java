package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.SessionId;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 18/11/15.
 */

@Data
public class POSSyncExceptionDto extends  BaseDto implements Serializable {

    Long fpsId = SessionId.getInstance().getFpsId();

    int localId;

    String syncMode;

    String tableName;

    String action;

    long recordId;

    long lastSyncTime;

    String rawData;

    String errorDescription;

    public POSSyncExceptionDto() {

    }

    public POSSyncExceptionDto(Cursor cur) {
        localId = cur.getInt(cur.getColumnIndex("_id"));
        syncMode = cur.getString(cur.getColumnIndex("syncMode"));
        tableName = cur.getString(cur.getColumnIndex("tableName"));
        action = cur.getString(cur.getColumnIndex("action"));
        recordId = Long.valueOf(cur.getString(cur.getColumnIndex("recordId")));
        lastSyncTime = Long.valueOf(cur.getString(cur.getColumnIndex("lastSyncTime")));
        rawData = cur.getString(cur.getColumnIndex("rawData"));
        errorDescription = cur.getString(cur.getColumnIndex("errorDescription"));
    }

}
