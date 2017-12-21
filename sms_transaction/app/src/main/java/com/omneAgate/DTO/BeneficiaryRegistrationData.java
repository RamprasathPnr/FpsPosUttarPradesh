package com.omneagate.DTO;

import android.database.Cursor;

import lombok.Data;

/**
 * Created by user1 on 4/5/15.
 */

@Data
public class BeneficiaryRegistrationData extends BaseDto {
    long id;
    String oldRationCardNum;
    String mobNum;
    long fpsId;
    String channel;
    int numOfCylinder, numOfAdults, numOfChild;
    String cardType;

    public BeneficiaryRegistrationData() {

    }

    public BeneficiaryRegistrationData(Cursor cursor) {


    }
}
