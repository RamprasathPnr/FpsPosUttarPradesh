package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

/**
 * This class is used to transfer beneficiary details
 *
 * @author user1
 */

@Data
public class BeneficiaryDto implements Serializable {
    /**
     * serialVersion id of the serializable interface
     */
    private static final long serialVersionUID = 1L;


    Long id;                    //Unique id to identify beneficiary

    String encryptedUfc;                    // Unique family code

    /**
     * beneficiary belongs to the taluk_id
     */
    long talukId;

    /**
     * beneficiary belongs to the village_id
     */
    long villageId;

    /**
     * tin number
     */
    String tin;

    /**
     * beneficiary belongs to the stateid
     */
    long stateId;

    /**
     * beneficiary belongs to the district_id
     */
    long districtId;

    /**
     * Enumeration Block (Set of families attached with polling booth) derived from TIN of Head Of Family as in NPR
     */
    String block;

    /**
     * cardType of the beneficiary
     */
    String cardTypeId;

    /**
     * Old ration card detail
     */
    String oldRationCard;

    /**
     * reference of the fpsstore
     */
    long fpsId;


    /**
     * beneficiary mobile number
     */
    String mobileNumber;

    /**
     * pos user create date
     */
    long createdDate;

    /**
     * pos user create id
     */
    long createdBy;

    /**
     * pos user modified id
     */
    long modifiedBy;

    /**
     * pos user modified date
     */
    long modifiedDate;

    /**
     * beneficiary family card number
     */
    String familyCardNumber;

    /**
     * beneficiary family card number
     */
    boolean active;

    /**
     * collection of beneficiarymember
     */
    Set<BeneficiaryMemberDto> benefMembersDto;

    //  List<BeneficiaryMemberDto> familyMembers;

    String oldRationNumber;

    int numOfCylinder;

    int numOfAdults;

    int numOfChild;

    public BeneficiaryDto() {

    }

    public BeneficiaryDto(Cursor cur) {
        id = cur.getLong(cur
                .getColumnIndex(FPSDBHelper.KEY_ID));

        encryptedUfc = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_UFC));

        fpsId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_FPS_ID));

        tin = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_TIN));

        mobileNumber = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MOBILE));

        modifiedBy = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MODIFIED_BY));

        stateId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_STATE_ID));

        cardTypeId = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID));

        districtId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_DISTRICT_ID));

        talukId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_TALUK_ID));

        villageId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_VILLAGE_ID));

        int activeStatus = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_ACTIVE));

        active = false;
        if (activeStatus == 1) {
            active = true;
        }
        oldRationNumber = cur.getString(cur
                .getColumnIndex("oldRationNumber"));

        numOfCylinder = cur.getInt(cur
                .getColumnIndex("numOfCylinder"));


        numOfAdults = cur.getInt(cur
                .getColumnIndex("numOfAdults"));


        numOfChild = cur.getInt(cur
                .getColumnIndex("numOfChild"));


    }

}