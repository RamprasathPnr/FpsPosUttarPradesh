package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Util;
import com.omneagate.activity.RationCardSummaryReportActivity;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * Created by user1 on 26/6/15.
 */
@Data
public class UnitwiseSummaryDto {

    //RationCardSummaryReportActivity rcsra = new RationCardSummaryReportActivity();

    String cardType;
    String oneUnit;
    String oneHalfUnit;
    String twoUnit;
    String twoHalfUnit;
    String threeUnit;
    String total;
    String lCardType;


    public UnitwiseSummaryDto() {

    }

    public UnitwiseSummaryDto(Cursor cur) {
        cardType = cur.getString(cur.getColumnIndex("Descptn"));
        lCardType = cur.getString(cur.getColumnIndex("lDescptn"));

        oneUnit = cur.getString(cur.getColumnIndex("oneunit"));
        oneHalfUnit = cur.getString(cur.getColumnIndex("onehalfunit"));
        twoUnit = cur.getString(cur.getColumnIndex("twounit"));
        twoHalfUnit = cur.getString(cur.getColumnIndex("twohalhunit"));
        threeUnit = cur.getString(cur.getColumnIndex("threeaboveunit"));

        int one = Integer.parseInt(oneUnit);
        int oneHalf = Integer.parseInt(oneHalfUnit);
        int two = Integer.parseInt(twoUnit);
        int twoHalf = Integer.parseInt(twoHalfUnit);
        int three = Integer.parseInt(threeUnit);
        int totalVal = one + oneHalf + two + twoHalf + three;

        total = String.valueOf(totalVal);

        Util.oneTotal  = Util.oneTotal + one;
        Util.oneHalfTotal = Util.oneHalfTotal + oneHalf;
        Util.twoTotal = Util.twoTotal + two;
        Util.twoHalfTotal = Util.twoHalfTotal + twoHalf;
        Util.threeTotal = Util.threeTotal + three;
        Util.totalTotal = Util.totalTotal + totalVal;




    }

}
