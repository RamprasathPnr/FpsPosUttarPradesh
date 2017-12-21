package com.omneagate.DTO;

import android.database.Cursor;
import android.util.Log;

import com.omneagate.Util.FPSDBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.Data;

@Data
public class POSStockAdjustmentDto extends BaseDto {
    /**
     * auto generated
     */

    Long id;

    /**
     * FPS identifier
     */
    long fpsId;

    /**
     * product	 identifier
     */
    long productId;

    /**
     * Reorder Level quantity for a product
     */
    Double quantity;

    /**
     * when created
     */
    long createdDate;

    /**
     * who created
     */
    long createdBy;

    long lastUpdatedTime;

    long lastUpdatedBy;

    Boolean posAckStatus;

    Long posAckDate;

    String requestType;

    String godownStockOutwardReferenceNumber;

    String referenceNumber;


    public POSStockAdjustmentDto() {

    }

    public POSStockAdjustmentDto(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(FPSDBHelper.KEY_ID));
        productId = cursor.getLong(cursor.getColumnIndex("product_id"));
        quantity = cursor.getDouble(cursor.getColumnIndex("quantity"));
        requestType = cursor.getString(cursor.getColumnIndex("requestType"));
        String dateAck = cursor.getString(cursor.getColumnIndex("dateOfAck"));
        String isAcked = cursor.getString(cursor.getColumnIndex("isAdjusted"));
        try {
            if (isAcked != null&& isAcked.equalsIgnoreCase("1")) {
                posAckStatus = true;
            }else{
                posAckStatus = false;
            }
            if (dateAck != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse(dateAck);
                posAckDate = date.getTime();
            }
        } catch (Exception e) {
            posAckDate = new Date().getTime();
        }

        try {
            String createdDateStr = cursor.getString(cursor.getColumnIndex("createdDate"));
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long milliSeconds= Long.parseLong(createdDateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
//            System.out.println(formatter.format(calendar.getTime()));
            String date = formatter.format(calendar.getTime());
            Date dateVal = formatter.parse(date);
            createdDate = dateVal.getTime();
        }
        catch(Exception e) {
            Log.e("PosStockAdjustmentDto","created date exc..."+e);
//            createdDate = new Date().getTime();
        }

        referenceNumber = cursor.getString(cursor.getColumnIndex("referenceNo"));
        godownStockOutwardReferenceNumber = cursor.getString(cursor.getColumnIndex("godownReferenceNo"));
    }

}
