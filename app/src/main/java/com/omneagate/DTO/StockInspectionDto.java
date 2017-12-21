package com.omneagate.DTO;

import android.database.Cursor;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class StockInspectionDto extends BaseDto implements Serializable {
    /** Auto generated value */
    private Long id;

    /** product Name */
    private long commodity;

    /**stock available in pos*/
    private Double posStock;

    /** Stock available in FPS */
    private Double actualStock;

    /** Variance of actual stock and pos stock*/
    private Double variance;

    /** Remarks of the inspector */
    private String remarks;

    /**  Inspection report id*/
    private long reportId;

    private List<String> images;

    //Byte format one Activity to another activity
    private List<byte[]> imageBytesList;

    private List<String> photoPathList;

    private Long clientReportId;

    String transactionId;

    String status;

    public StockInspectionDto()
    {

    }

    public StockInspectionDto(Cursor cursor)
    {
//		" client_reportId  INTEGER,"+" product_id  INTEGER,"+" stock_pos_system INTEGER,"+" actual_fps_system  INTEGER, "+"variance  INTEGER,"+" remarks VARCHAR(150)"
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        clientReportId = cursor.getLong(cursor.getColumnIndex("client_reportId"));
        commodity = cursor.getLong(cursor.getColumnIndex("product_id"));
        posStock= cursor.getDouble(cursor.getColumnIndex("stock_pos_system"));
        actualStock = cursor.getDouble(cursor.getColumnIndex("actual_fps_system"));
        variance = cursor.getDouble(cursor.getColumnIndex("variance"));
        remarks =  cursor.getString(cursor.getColumnIndex("remarks"));
        transactionId = cursor.getString((cursor.getColumnIndex("transaction_id")));
        status = cursor.getString((cursor.getColumnIndex("status")));
    }
}