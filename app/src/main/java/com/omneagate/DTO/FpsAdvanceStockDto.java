package com.omneagate.DTO;



import android.database.Cursor;
import android.util.Log;

import com.omneagate.Util.Constants.FPSDBConstants;

import org.bouncycastle.jce.provider.symmetric.Grain128;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.Data;

/**
 * Created by user1 on 2/9/15.
 */
@Data
public class FpsAdvanceStockDto extends BaseDto {


    long gowdownId;

    long fpsId;

    Long outwardDate;

    long productId;

//    Double quantity;

    String unit;

    long batchNo;

    long status;

    Long fpsDate;

    Double fpsQuantity;

//    int month;

//    int year;

    int retryCount;

    int retryTime;

    int challanId;




    long id;

    FpsStoreDto fpsStoreDto;

    ProductDto productDto;

    Double quantity;

    Long modifiedDate;

    Long modifiedBy;

    Long createdDate;

    Long createdBy;

    int month;

    int year;

    boolean addedToStock;

    Long transactionDate;

    GodownStockOutwardDto godownStockOutwardDto;

    String documentNumber;

    String godownName;
    String godownCode;

    Long processStatus;
    Long syncStatus;


    public FpsAdvanceStockDto(Cursor cursor) {

        fpsId = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID));
        productId = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID));
        fpsDate = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE));
        status = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS));
        fpsQuantity = cursor.getDouble(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY));
        month = cursor.getInt(cursor.getColumnIndex("month"));
        year = cursor.getInt(cursor.getColumnIndex("year"));
        unit = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT));
        challanId = cursor.getInt(cursor.getColumnIndex("delivery_challan_id"));
        godownName = cursor.getString(cursor.getColumnIndex("godown_name"));
        godownCode = cursor.getString(cursor.getColumnIndex("godown_code"));
        outwardDate = cursor.getLong(cursor.getColumnIndex("outward_date"));

        try {
            String dateStr = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE));
            Log.e("FpsadvancestockDto", "dateStr...." + dateStr);
            if (dateStr.contains("-")) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = dateFormat.parse(dateStr);
                    transactionDate = date.getTime();
                    Log.e("FpsadvancestockDto", "dateStr converted...." + dateStr);
                } catch (Exception e) {
                }
            } else {
                transactionDate = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE));
                Log.e("FpsadvancestockDto", "transactionDate...." + transactionDate);
            }
        }
        catch(Exception e) {}





        long godownId = cursor.getInt(cursor.getColumnIndex("_id"));
        String referenceNo = cursor.getString(cursor.getColumnIndex("referenceNo"));
        godownStockOutwardDto = new GodownStockOutwardDto();
        godownStockOutwardDto.setId(godownId);
        godownStockOutwardDto.setReferenceNo(referenceNo);
        quantity = cursor.getDouble(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY));
        processStatus = cursor.getLong(cursor.getColumnIndex("isAdded"));
        syncStatus = cursor.getLong(cursor.getColumnIndex("syncStatus"));

    }


    public FpsAdvanceStockDto() {

    }
}
