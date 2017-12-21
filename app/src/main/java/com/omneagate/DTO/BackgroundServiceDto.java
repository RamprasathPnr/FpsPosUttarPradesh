package com.omneagate.DTO;

import android.database.Cursor;
import com.omneagate.Util.FPSDBHelper;
import java.io.Serializable;
import lombok.Data;

@Data
public class BackgroundServiceDto implements Serializable {

    long id;

    String requestData;

    String responseData;

    long requestDateTime;

    long responseDateTime;

    String errorDescription;

    String serviceType;

    String status;

    public BackgroundServiceDto() {

    }

    public BackgroundServiceDto(Cursor cur) {

        id = cur.getLong(cur.getColumnIndex(FPSDBHelper.KEY_ID));

        requestData = cur.getString(cur.getColumnIndex("requestData"));

        responseData = cur.getString(cur.getColumnIndex("responseData"));

        requestDateTime = cur.getLong(cur.getColumnIndex("requestDateTime"));

        responseDateTime = cur.getLong(cur.getColumnIndex("responseDateTime"));

        errorDescription = cur.getString(cur.getColumnIndex("errorDescription"));

        serviceType = cur.getString(cur.getColumnIndex("serviceType"));

        status = cur.getString(cur.getColumnIndex("status"));

    }


}
