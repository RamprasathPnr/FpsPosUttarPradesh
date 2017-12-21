package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import lombok.Data;

/**
 * Created by user1 on 6/3/15.
 */
@Data
public class MessageDto extends BaseDto {

    long id;


    String languageId;


    long languageCode;


    String description;


    long createdBy;


    long modifiedBY;


    long createDate;


    long modifiedDate;


    public MessageDto() {

    }

    public MessageDto(Cursor cur) {

        languageCode = cur.getLong(cur
                .getColumnIndex(FPSDBConstants.KEY_LANGUAGE_CODE));
        languageId = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_LANGUAGE_ID));

        description = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_LANGUAGE_MESSAGE));
    }

}
