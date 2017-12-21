package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import org.joda.time.DateTime;
import org.joda.time.Years;

import java.io.Serializable;

import lombok.Data;

@Data
public class BeneficiaryMemberDto implements Serializable {

    /**
     * Generated serial version id
     */
    private static final long serialVersionUID = -5756801206768777282L;

    /**
     * auto generated
     */
    Long id;

    /**
     * reference of the beneficiary entity
     */
    BeneficiaryDto beneficiaryDto;

    /**
     * NPR reference number . TIN of the head of the family.
     */
    String tin;

    /**
     * UID as in Aadhaar Card
     */
    String uid;

    /**
     * E Aadhaar Number
     */
    String eid;

    /**
     * First name of the member
     */
    String name;

    /**
     * First name of the member
     */
    String localName;

    /**
     * First name of the member
     */
    String firstName;

    /**
     * Middle name of the member
     */
    String middleName;

    /**
     * Last name of the member
     */
    String lastName;

    /**
     * Relation to the head of the family.
     */
    String relName;

    /**
     * Relation to the head of the family.
     */
    String localRelName;

    char gender;

    /**
     * Date of birth
     */
    long dob;


    /**
     * 1(unmarried), 2(married)
     */
    char mstatusId;

    /**
     * Education name
     */
    String eduName;

    String localEduName;

    /**
     * occupation name
     */
    String occuName;

    /**
     * occupation name
     */
    String localOccuName;


    /**
     * 1(father name), 2(mother)
     */
    String fatherCode;

    /**
     * Father name
     */
    String fatherName;

    /**
     * Father name in local language
     */
    String localFatherName;

    /**
     * 2(mother)
     */
    String motherCode;

    /**
     * Mother Name
     */
    String motherName;

    String localMotherName;

    /**
     * Spouce code (1)
     */
    String spouseCode;

    /**
     * Spouce name
     */
    String spouseName;

    /**
     * Spouce name
     */
    String localSpouseName;

    /**
     * Permanent address
     */
    String permAddress;

    /**
     * Temporary address
     */
    String tempAddress;

    /**
     * User who created this record
     */
    String createdBy;

    /**
     * Date of creation
     */
    Long createdDate;

    /**
     * User who modified this record
     */
    String modifiedBy;

    /**
     * Date of modification
     */
    Long modifiedDate;


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
     * Address
     */
    String addressLine4;

    /**
     * Address
     */
    String addressLine5;

    /**
     * local language   Address
     */
    String localAddressLine1;

    /**
     * local language   Address
     */
    String localAddressLine2;

    /**
     * local language  Address
     */
    String localAddressLine3;

    /**
     * local language  Address
     */
    String localAddressLine4;

    /**
     * local language Address
     */
    String localAddressLine5;


    /**
     * pin code
     */
    String pincode;

    /**
     * it gives information of person staying period in present location
     */
    int durationInYear;

    /**
     * Address1
     */
    String paddressLine1;

    /**
     * Address2
     */
    String paddressLine2;

    /**
     * Address3
     */
    String paddressLine3;

    /**
     * Address4
     */
    String paddressLine4;

    /**
     * Address5
     */
    String paddressLine5;

    /**
     * Address1
     */
    String localPaddressLine1;

    /**
     * Address2
     */
    String localPaddressLine2;

    /**
     * Address3
     */
    String localPaddressLine3;

    /**
     * Address4
     */
    String localPaddressLine4;

    /**
     * Address5
     */
    String localPaddressLine5;

    /**
     * Pin Code
     */
    String ppincode;

    char residentid;

    /**
     * Nationality
     */
    String natname;

    /**
     * Data entry date
     */
    Long dateDataEntered;

    /**
     * True/false
     */
    boolean aliveStatus;
    /**
     * is adult checking
     */
    boolean isAdult;

    int currentAge;

    boolean isremoved;

    long removedDate;


    public BeneficiaryMemberDto() {

    }

    public BeneficiaryMemberDto(Cursor cur) {

        id = cur.getLong(cur
                .getColumnIndex(FPSDBHelper.KEY_ID));

        uid = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID));

        tin = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN));
        eid = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID));
        firstName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FIRST_NAME));
        middleName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MIDDLE_NAME));
        lastName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LAST_NAME));
        fatherName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NAME));
        motherName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NAME));

        String genderType = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER));

        gender = genderType.charAt(0);

        name = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAME));

        localName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME));

        String resident = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_RESIDENT_ID));

        if (resident != null && resident.length() > 0)
            residentid = resident.charAt(0);

        relName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_REL_NAME));

        gender = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID)).charAt(0);

        dob = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB));

        currentAge = Years.yearsBetween(new DateTime(dob), new DateTime()).getYears();

        mstatusId = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID)).charAt(0);

        eduName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME));

        occuName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME));

        fatherCode = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE));

        localFatherName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM));
        motherCode = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE));
        localMotherName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM));
        spouseCode = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE));
        localSpouseName = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM));


        natname = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME));
        addressLine1 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1));
        addressLine2 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2));
        addressLine3 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3));
        addressLine4 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4));

        addressLine5 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5));

        localAddressLine1 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1));
        localAddressLine2 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2));
        localAddressLine3 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3));
        localAddressLine4 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4));
        localAddressLine5 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5));

        pincode = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE));

        durationInYear = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR));

        paddressLine1 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1));
        paddressLine2 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2));
        paddressLine3 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3));
        paddressLine4 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4));
        paddressLine5 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5));
        localPaddressLine1 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1));
        localPaddressLine2 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2));
        localPaddressLine3 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3));
        localPaddressLine4 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4));
        localPaddressLine5 = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5));


        ppincode = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_PIN_CODE));
        int alive = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS));
        aliveStatus = alive == 0;

        int adult = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_IS_ADULT));
        isAdult = adult == 0;

        int removed = cur.getInt(cur
                .getColumnIndex("is_removed"));
        isremoved = removed == 0;

        removedDate = cur.getLong(cur
                .getColumnIndex("removed_date"));
    }
    /*@ManyToOne
    Beneficiary beneficiary;
	*/
}
