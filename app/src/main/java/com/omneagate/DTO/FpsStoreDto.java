package com.omneagate.DTO;

import android.database.Cursor;
import android.util.Log;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;
import java.util.Date;

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

    FPSCategory fpsCategory;

    String generatedCode;

    FPSType fpsType;

    String entitlementClassification;

    String villageName;

    String villageCode;

    String talukName;

    String talukCode;

    String districtName;

    String districtCode;

    Long villageId;

    Long talukId;

    Long districtId;

    boolean remoteLogEnabled;

    String latitude;

    /**
     * longitude of the FPS-Store
     */
    String longitude;

    /**
     * The FPS-Store is geofencing or not
     */
    Boolean geofencing;

    String deviceSimNo;

    String agencyName, fpsTypeDef;

    String agencyCode, fpsCategoryType;

    String operationClosingTime;

    String operationOpeningTime;


    String createdDate;

    public FpsStoreDto() {

    }

    public FpsStoreDto(Cursor c) {

        try {
            Log.e("FpsStoreDto ", "FpsStoreDto");


            id = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_USERS_FPS_ID));
            int statusCode = c.getInt(c.getColumnIndex(FPSDBConstants.KEY_USERS_IS_ACTIVE));
            active = statusCode != 0;
            contactPerson = c.getString(c.getColumnIndex(FPSDBConstants.KEY_USERS_CONTACT_PERSON));
            phoneNumber = c.getString(c.getColumnIndex(FPSDBConstants.KEY_USERS_PHONE_NUMBER));
            addressLine1 = c.getString(c.getColumnIndex(FPSDBConstants.KEY_USERS_ADDRESS_LINE1));
            addressLine2 = c.getString(c.getColumnIndex(FPSDBConstants.KEY_USERS_ADDRESS_LINE2));
            addressLine3 = c.getString(c.getColumnIndex(FPSDBConstants.KEY_USERS_ADDRESS_LINE3));

            districtId = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_USERS_DISTRICT_ID));


/*
        Log.e("FpsStoreDto districtId ", "before = "+c.getLong(c
                .getColumnIndex(FPSDBConstants.KEY_USERS_DISTRICT_ID)));

        if(!c.isNull(c
                .getColumnIndex(FPSDBConstants.KEY_USERS_DISTRICT_ID)))
        {

            districtId = c.getLong(c
                    .getColumnIndex(FPSDBConstants.KEY_USERS_DISTRICT_ID));


            Log.e("FpsStoreDto","districtId if part = " +districtId);
        }else{
            Log.e("FpsStoreDto","districtId else part = "+districtId);
        }*/


            talukId = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_USERS_TALUK_ID));
/*

        Log.e("FpsStoreDto talukId", "Before = "+c.getLong(c
                .getColumnIndex(FPSDBConstants.KEY_USERS_TALUK_ID)));

        if(!c.isNull(c
                .getColumnIndex(FPSDBConstants.KEY_USERS_TALUK_ID)))
        {

            talukId = c.getLong(c
                    .getColumnIndex(FPSDBConstants.KEY_USERS_TALUK_ID));

            Log.e("FpsStoreDto","talukId if part = "+ talukId);
        }else{
            Log.e("FpsStoreDto","talukId else part = "+talukId);
        }
*/


            villageId = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_USERS_VILLAGE_ID));

  /*      Log.e("FpsStoreDto villageId", "Before = "+c.getLong(c
                .getColumnIndex(FPSDBConstants.KEY_USERS_VILLAGE_ID)));

        if(!c.isNull(c
                .getColumnIndex(FPSDBConstants.KEY_USERS_VILLAGE_ID)))
        {

            villageId = c.getLong(c
                    .getColumnIndex(FPSDBConstants.KEY_USERS_VILLAGE_ID));

            Log.e("FpsStoreDto","villageId if part = "+ villageId);
        }else{
            Log.e("FpsStoreDto","villageId else part = "+villageId);
        }
*/


            code = c.getString(c.getColumnIndex(FPSDBConstants.KEY_USERS_CODE));
            villageName = c.getString(c.getColumnIndex("village_name"));
            entitlementClassification = c.getString(c.getColumnIndex("entitlement_classification"));
            villageCode = c.getString(c.getColumnIndex("village_code"));
            talukName = c.getString(c.getColumnIndex("taluk_name"));
            fpsCategoryType = c.getString(c.getColumnIndex("fps_category"));
            fpsTypeDef = c.getString(c.getColumnIndex("fps_type"));
            districtName = c.getString(c.getColumnIndex("district_name"));
            deviceSimNo = c.getString(c.getColumnIndex("device_sim_no"));
            agencyName = c.getString(c.getColumnIndex("agency_name"));
            agencyCode = c.getString(c.getColumnIndex("agency_code"));
            generatedCode = c.getString(c.getColumnIndex("gen_code"));
            operationClosingTime = c.getString(c.getColumnIndex("operation_closing_time"));
            operationOpeningTime = c.getString(c.getColumnIndex("operation_opening_time"));

        }catch (Exception e){
            Log.e("FpsStoreDto ", "Exception = " +e);

        }
    }


}
