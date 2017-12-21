package com.omneagate.DTO;

import android.database.Cursor;
import android.util.Log;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import lombok.Data;

@Data
public class GodownStockOutwardDto extends BaseDto {

    String cancelStatus;

    Long modifiedBy;

    long modifiedDate;

    Long id;
    /**
     * godown identifier
     */
    long godownId;

    String godownName;

    /**
     * FPS Identifier
     */
    long fpsId;

    /**
     * Stock outward date
     */
    long outwardDate;

    /**
     * Product identifier
     */
    long productId;

    /**
     * Stock quantity
     */
    Double quantity;

    /**
     * Unit of measurement from Master table
     */
    String unit;

    /**
     * Internal batch number auto generated
     */
    long batchno;

    /**
     * Indicates whether the corresponding FPS has received the stock allotment
     */
    boolean fpsAckStatus;

    /**
     * Date of acknowledgement from FPS
     */
    Long fpsAckDate;

    /**
     * The actual quantity received by the FPS
     */
    Double fpsReceiQuantity;

    /**
     * user id
     */
    long createdby;

    String vehicleN0;

    String driverName;

    String driverMobileNumber;

    String inwardType;

    String transportName;
    /**
     * Delivery challan id from Deliver_challan table
     */
    Long deliveryChallanId;


    String godownCode;

    /**
     * collection allotment data
     */
    Set<ChellanProductDto> productDto;

    String referenceNo;

    int month;

    int year;

    boolean currentMonth = true;

    public GodownStockOutwardDto() {

    }

    public GodownStockOutwardDto(Cursor cursor) {
      try {
          godownId = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID));
          fpsId = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID));
          outwardDate = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE));
          productId = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID));

          quantity = cursor.getDouble(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY));
          unit = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT));
          godownName = cursor.getString(cursor.getColumnIndex("godown_name"));
          godownCode = cursor.getString(cursor.getColumnIndex("godown_code"));
          referenceNo = cursor.getString(cursor.getColumnIndex("referenceNo"));
          batchno = cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO));
          createdby = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY));
          String value = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE));
          fpsAckDate = 0l;
          if(value!=null){
              SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
              Date date = sdf.parse(value);
              fpsAckDate = date.getTime();
          }
          fpsReceiQuantity = cursor.getDouble(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY));
          deliveryChallanId = cursor.getLong(cursor.getColumnIndex("challanId"));
          fpsAckStatus = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS)) != 0;
          month = cursor.getInt(cursor.getColumnIndex("month"));
          year = cursor.getInt(cursor.getColumnIndex("year"));
          id = cursor.getLong(cursor.getColumnIndex("_id"));
          vehicleN0 = cursor.getString(cursor.getColumnIndex("vehicleN0"));
          driverName = cursor.getString(cursor.getColumnIndex("driverName"));
          transportName = cursor.getString(cursor.getColumnIndex("transportName"));
          driverMobileNumber = cursor.getString(cursor.getColumnIndex("driverMobileNumber"));
      }catch (Exception e){
          Log.e("Excep",e.toString(),e);
      }
    }
}
