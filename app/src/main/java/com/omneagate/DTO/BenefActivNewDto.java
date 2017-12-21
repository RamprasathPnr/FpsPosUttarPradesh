package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.SessionId;

import lombok.Data;

/**
 * Created for BenefActivNewDto
 */
@Data
public class BenefActivNewDto extends BaseDto {

    String deviceNum;

    String fpsId = ""+SessionId.getInstance().getFpsId();

    String rationCardNumber;

    String mobileNum;

    String otpEntryTime;

    String otp;

    String activationType;

    String requestedTime;

    String imageAddress;

    String aregisterNum;

//    String familyHeadAadharNumber;

    boolean isChecked = false;
    /**
     * number of cylinder
     */
    int numOfCylinder;

    String transactionId;

    boolean valueAdded = false;

    /**
     * number of adult
     */
    int numOfAdults;

    /**
     * number of child
     */
    int numOfChild;

    char cardType;

    String cardTypeDef;

    byte[] benefImage;

    String encryptedUfc;

    String channel = "POS";

    Long reqDate;

//    String rationCardType;

    AadharSeedingDto aadhaarSeedingDto;

    public BenefActivNewDto() {

    }


    public BenefActivNewDto(Cursor cursor) {

        rationCardNumber = cursor.getString(cursor
                .getColumnIndex(FPSDBConstants.KEY_REGISTRATION_CARD_NO));

        mobileNum = cursor.getString(cursor
                .getColumnIndex(FPSDBConstants.KEY_REGISTRATION_RMN));

        transactionId = cursor.getString(cursor
                .getColumnIndex("card_ref_id"));

        requestedTime = cursor.getString(cursor
                .getColumnIndex(FPSDBConstants.KEY_REGISTRATION_TIME));

        reqDate = cursor.getLong(cursor
                .getColumnIndex("reqTime"));

        /*channel = cursor.getString(cursor
                .getColumnIndex("channel"));*/

        channel = "POS";

        aregisterNum = cursor.getString(cursor
                .getColumnIndex("aRegister"));

        fpsId = ""+SessionId.getInstance().getFpsId();


    }
}