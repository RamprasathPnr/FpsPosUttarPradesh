package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 11/5/15.
 */

@Data
public class SmsProviderDto extends BaseDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Primary Key column.
     */
    long id;

    /**
     * sms servicer provider name.
     */
    String providerName;

    /**
     * creater of sms provider
     */
    long createdBy;

    /**
     * sms provider status
     */
    boolean enabledStatus;


    /**
     * sms provider incoming number
     */
    String incomingNumber;

    /**
     * sms provider prefix key
     */
    String prefixKey;

    /**
     * sms provider preference
     */
    String preference;

    public SmsProviderDto() {

    }

    public SmsProviderDto(Cursor cursor) {
        providerName = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_SMS_PROVIDER_NAME));
        incomingNumber = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_SMS_PROVIDER_NUMBER));
        prefixKey = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_SMS_PROVIDER_PREFIX));

    }
}
