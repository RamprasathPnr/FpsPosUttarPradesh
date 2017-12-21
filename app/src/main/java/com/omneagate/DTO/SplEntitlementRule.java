package com.omneagate.DTO;

import android.database.Cursor;
import android.util.Log;

import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;

import lombok.Data;

/**
 * Created by user1 on 28/4/15.
 */
@Data
public class SplEntitlementRule {
    long id;

    long productId;

    ProductDto productDto;

    Long districtId;

    Long talukId;

    Long villageId;

    int cylinderCount;

    boolean taluk;

    boolean city;

    boolean municipality;

    boolean cityHeadQuarter;

    double quantity;

    long cardTypeId;

    boolean add;

    long lastUpdatedTime;

    long lastUpdatedBy;

    Boolean isDeleted;

    GroupDto groupDto;

    boolean hillyArea;
    boolean splArea;
    boolean townPanchayat;
    boolean villagePanchayat;

    public SplEntitlementRule() {

    }

    public SplEntitlementRule(Cursor cursor) {
        Log.e("SplEntitlementRule", "SplEntitlementRule");

        id = cursor.getLong(cursor
                .getColumnIndex(FPSDBHelper.KEY_ID));

        productId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_PRODUCT_ID));
/*
        districtId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_DISTRICT));*/


        Log.e("districtId", "before "+cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_DISTRICT)));

        if(!cursor.isNull(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_DISTRICT)))
        {

            districtId = cursor.getLong(cursor
                    .getColumnIndex(FPSDBConstants.KEY_SPECIAL_DISTRICT));


            Log.e("specialruledto","districtId if part = " +districtId);
        }else{
            Log.e("specialruledto","districtId else part"+districtId);
        }




       /* talukId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_TALUK));
*/


        Log.e("talukId", "Before"+cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_TALUK)));

        if(!cursor.isNull(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_TALUK)))
        {

            talukId = cursor.getLong(cursor
                    .getColumnIndex(FPSDBConstants.KEY_SPECIAL_TALUK));

            Log.e("specialruledto","talukId if part "+ talukId);
        }else{
            Log.e("specialruledto","talukId else part"+talukId);
        }



       /* villageId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_VILLAGE));*/

        Log.e("villageId", "Before"+cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_VILLAGE)));

        if(!cursor.isNull(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_VILLAGE)))
        {

            villageId = cursor.getLong(cursor
                    .getColumnIndex(FPSDBConstants.KEY_SPECIAL_VILLAGE));

            Log.e("specialruledto","villageId if part = "+ villageId);
        }else{
            Log.e("specialruledto","villageId else part"+villageId);
        }




        /*Log.e("villageId", ""+cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_VILLAGE)));*/

        cardTypeId = cursor.getLong(cursor
                .getColumnIndex(FPSDBConstants.KEY_RULES_CARD_TYPE_ID));

        cylinderCount = cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_CYLINDER));

        quantity = cursor.getDouble(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_QUANTITY));

        municipality = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_MUNICIPALITY)));

        add = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_ADD)));

        cityHeadQuarter = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_CITY_HEAD)));

        city = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_CITY)));

        taluk = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_TALUK)));

        hillyArea = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_HILLAREA)));
        splArea = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_SPLAREA)));
        townPanchayat = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_TOWNPANCHAYAT)));
        villagePanchayat = returnBoolean(cursor.getInt(cursor
                .getColumnIndex(FPSDBConstants.KEY_SPECIAL_IS_VILLAGE_PANCHAYAT)));
    }

    private boolean returnBoolean(int value) {
        return value == 1;

    }

}
