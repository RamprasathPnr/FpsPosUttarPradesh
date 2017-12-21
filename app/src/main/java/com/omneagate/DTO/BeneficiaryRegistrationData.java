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

    long requestedTime;

    String channel;

    int numOfCylinder;

    int numOfAdults;

    int numOfChild;

    String cardType;

    String aregisterNum;

    String transactionId;

    public BeneficiaryRegistrationData() {

    }

    public BeneficiaryRegistrationData(Cursor cursor) {


    }
}
