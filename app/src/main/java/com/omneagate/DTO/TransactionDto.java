package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import lombok.Data;

/**
 * Created by user1 on 9/3/15.
 */

@Data
public class TransactionDto {

    /* This column uniquely identifies the district */
    Long id;

    /**
     * Unique Key Possible transaction types
     */

    long txnType;

    /* Boolean  false - Disabled true - Enabled. */

    boolean status;

    /**
     * Transaction name
     */

    String description;

   /* User id who updated this ticket */

    long createdBy;

    /* User Created date */

    long createdDate;

    /* User Created date */

    long lastModifiedTime;


    public TransactionDto() {

    }

    public TransactionDto(Cursor cursor) {
        txnType = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_TRANSACTION_TYPE_TXN_TYPE));

    }
}
