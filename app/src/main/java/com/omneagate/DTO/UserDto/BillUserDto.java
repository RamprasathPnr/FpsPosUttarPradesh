package com.omneagate.DTO.UserDto;

import android.database.Cursor;

import com.omneagate.DTO.BaseDto;
import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

/**
 * This class is used to transfer Bill details
 *
 * @author user1
 */

@Data
public class BillUserDto extends BaseDto implements Serializable {

    long id;                        //AutoGenerated id

    long fpsId;                    //FPS id

    String billDate;                        //bill date

    long beneficiaryId;

    String createdby;                    //user who created the bill

    Double amount;                    //amount

    char mode;                        //Mode of identification. [R,Q,O]

    char channel;                    // Communication. Possible values [S ,G]

    Set<BillItemProductDto> billItemDto = new HashSet<BillItemProductDto>();

    String createdDate;

    long billRefId;

    long billLocalRefId;

    String billStatus;

    int billMonth;

    String ufc;

    String otpTime;

    long otpId;

    String transactionId;

    String rationCardNumber;

    String aRegisterNo;

    public BillUserDto() {

    }

    public BillUserDto(Cursor cur) {
       /* id = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_));*/

        billDate = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_DATE));

        billMonth = cur.getInt(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_TIME_MONTH));
        String modes = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_MODE));
        mode = modes.charAt(0);

        ufc = cur.getString(cur.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_UFC));

        String channels = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_CHANNEL));
        channel = channels.charAt(0);

        createdby = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_CREATED_BY));

     /*   createdDate = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_CREATED_DATE));*/

        billStatus = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_STATUS));

        transactionId = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_TRANSACTION_ID));

        fpsId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_FPS_ID));

        billLocalRefId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_REF_ID));

        amount = cur.getDouble(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_AMOUNT));

        beneficiaryId = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_BILL_BENEFICIARY));

        otpId = cur.getLong(cur.getColumnIndex("otpId"));

        ufc = cur.getString(cur.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_UFC));

        rationCardNumber = cur.getString(cur.getColumnIndex("old_ration_card_num"));

        aRegisterNo = cur.getString(cur.getColumnIndex("aRegister"));

        if (aRegisterNo == null || aRegisterNo.equals("-1")) {
            aRegisterNo = "";
        }

        otpTime = cur.getString(cur.getColumnIndex("otpTime"));

        // billItems =

    }


}
