package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import lombok.Data;

/**
 * Created by user1 on 29/4/15.
 */
@Data
public class BenefActivNewDto extends BaseDto {

    String deviceNum;

    String fpsId;

    String rationCardNumber;

    String mobileNum;

    String otpEntryTime;

    String otp;

    String activationType;
    /**
     * number of cylinder
     */
    int numOfCylinder;

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


    public BenefActivNewDto() {

    }

    public BenefActivNewDto(Cursor cursor) {

        rationCardNumber = cursor.getString(cursor
                .getColumnIndex(FPSDBConstants.KEY_REGISTRATION_CARD_NO));

        mobileNum = cursor.getString(cursor
                .getColumnIndex(FPSDBConstants.KEY_REGISTRATION_RMN));
    }
}