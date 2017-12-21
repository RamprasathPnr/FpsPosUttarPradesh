package com.omneagate.DTO;

import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * Created by user1 on 26/6/15.
 */
@Data
public class RationcardSummaryDto {

    String cardType;
    String lCardType;
    String noOfCards;

    public RationcardSummaryDto() {

    }

    public RationcardSummaryDto(Cursor cur) {
        cardType = cur.getString(cur.getColumnIndex("description"));
        lCardType = cur.getString(cur.getColumnIndex("localDescription"));
        noOfCards = cur.getString(cur.getColumnIndex("typeCount"));
    }

}
