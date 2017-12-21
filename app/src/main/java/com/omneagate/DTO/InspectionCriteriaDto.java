package com.omneagate.DTO;

import android.database.Cursor;

import lombok.Data;

/**
 * Created by user1 on 5/2/16.
 */
@Data
public class InspectionCriteriaDto extends BaseDto {

    Long id;

    String criteria;

    public InspectionCriteriaDto() {

    }


    public InspectionCriteriaDto(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("criteria_id"));
        criteria = cursor.getString(cursor.getColumnIndex("criteria"));

    }

}
