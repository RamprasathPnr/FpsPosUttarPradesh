package com.omneagate.DTO;

import android.database.Cursor;

import java.util.List;

import lombok.Data;

@Data
public class ShopAndOthersDto extends BaseDto {
    Long id;
    String criteriaName;
    Long criteriaId;
    /**
     * Remarks of the inspector
     */
    private String remarks;
    /**
     * Inspection report id
     */
    private long reportId;
    /* Images in Base64 Format */
    private List<String> images;
    private List<byte[]> imageBytesList;
    private List<String> photoPathList;
    private Long clientReportId;
    private String status;

    public ShopAndOthersDto() {
    }

    public ShopAndOthersDto(Cursor cursor, String cType) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        clientReportId = cursor.getLong(cursor.getColumnIndex("client_reportId"));
        remarks = cursor.getString(cursor.getColumnIndex("remarks"));
        status = cursor.getString(cursor.getColumnIndex("status"));
        if (cType.equalsIgnoreCase("Others")) {
            criteriaId = 5l;
        } else {
            criteriaId = 4l;
        }
    }
}
