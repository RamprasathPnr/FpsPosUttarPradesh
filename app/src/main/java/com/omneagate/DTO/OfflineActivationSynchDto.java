package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 9/7/15.
 */
@Data
public class OfflineActivationSynchDto extends BaseDto {

    String deviceNum;

    /**
     * old ration card number
     */
    String rationCardNumber;

    /**
     * product id
     */
    String mobNum;

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

    long activatedTime;

    String aregisterNum;

    List<BillDto> billDtos;

    BeneficiaryDto beneficairyDto;

    public OfflineActivationSynchDto() {

    }

    public OfflineActivationSynchDto(Cursor cursor) {
        rationCardNumber = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_OLD_RATION));
        aregisterNum = cursor.getString(cursor.getColumnIndex("aRegister"));
        mobNum = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MOBILE));
        numOfCylinder = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO));
        numOfChild = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_CHILD_NO));
        numOfAdults = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_ADULT_NO));
        activatedTime = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE));
        String cards = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID));
        cardType = cards.charAt(0);
    }
}