package com.omneagate.DTO;

import android.database.Cursor;

import java.util.List;

import lombok.Data;

@Data
public class WeighmentInspectionDto extends BaseDto {

    /** Auto Generated Value */
    private Long id;

    /** Bill number */
    private String billNumber;

    /** Family Card Number */
    private String cardNo;

   /** Commodity need to be checked for the Family Card number */
    private Long commodity;


    /** Sold Quantity of the Commodity to the Card Holder */
    private Double soldQuantity;

    /** Sold Quantity of the Commodity to the Card Holder */
    private Double observedQuantity;

    /** Remarks of the Inspector */
    private String remarks;

    /** Variance of the Commodity between pos and Family card */
    private Double variance;

    private Long reportId;

    /* Images in Base64 Format -Sending Format*/
    private List<String> images;

    //Byte format one Activity to another activity
    private List<byte[]> imageBytesList;

    private List<String> photoPathList;


    private Long clientReportId;

    private String status;

    private Long fpsId;


    public WeighmentInspectionDto(){

    }

    public WeighmentInspectionDto(Cursor cursor){

        id = cursor.getLong(cursor.getColumnIndex("_id"));

        clientReportId = cursor.getLong(cursor.getColumnIndex("client_reportId"));

        billNumber = cursor.getString(cursor.getColumnIndex("bill_number"));

        commodity = cursor.getLong(cursor.getColumnIndex("product_id"));

        cardNo= cursor.getString(cursor.getColumnIndex("card_number"));

        fpsId = cursor.getLong(cursor.getColumnIndex("fpsId"));

        soldQuantity = cursor.getDouble(cursor.getColumnIndex("sold_quantity"));

        observedQuantity = cursor.getDouble(cursor.getColumnIndex("observed_quantity"));

        variance =  cursor.getDouble(cursor.getColumnIndex("variance"));

        remarks = cursor.getString(cursor.getColumnIndex("remarks"));

        status = cursor.getString(cursor.getColumnIndex("status"));

    }

}
