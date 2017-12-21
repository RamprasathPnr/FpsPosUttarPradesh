package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;

import lombok.Data;


/**
 * This class is used to transfer store (Fair price shop) details
 *
 * @author user1
 */
@Data
public class FpsStoreDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier of the store
     */
    long id;

    /**
     * Unique code to identify store
     */
    String code;

    /**
     * Flag to indicate if the store/ inactive
     */
    boolean active = true;

    /**
     * reference of the godown
     */
    GodownDto godown;

    /**
     * user id of the pos
     */
    long createdBy;

    /**
     * user id of the pos
     */
    long modifiedBy;

    /**
     * create date of the fps
     */
    long createDate;

    /**
     * modified date of the fps
     */
    long modifiedDate;

    /**
     * contact person name
     */
    String contactPerson;

    /**
     * contact mobile number
     */
    String phoneNumber;

    /**
     * Address
     */
    String addressLine1;

    /**
     * Address
     */
    String addressLine2;

    /**
     * Address
     */
    String addressLine3;

    /**
     * reference of the Device
     */
    DeviceDto device;


    public FpsStoreDto() {

    }

    public FpsStoreDto(Cursor c) {

        id = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_USERS_FPS_ID));
        int statusCode = c.getInt(c.getColumnIndex("isActive"));
        active = true;
        if (statusCode == 0) {
            active = false;
        }
        contactPerson = c.getString(c.getColumnIndex("contactPerson"));
        phoneNumber = c.getString(c.getColumnIndex("phoneNumber"));
        addressLine1 = c.getString(c.getColumnIndex("addressLine1"));
        addressLine2 = c.getString(c.getColumnIndex("addressLine2"));
        addressLine3 = c.getString(c.getColumnIndex("addressLine3"));
        code = c.getString(c.getColumnIndex("code"));


    }


}
