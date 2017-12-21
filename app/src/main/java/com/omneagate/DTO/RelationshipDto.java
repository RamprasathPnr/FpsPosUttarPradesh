package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;
import lombok.Data;



@Data
public class RelationshipDto extends BaseDto implements Serializable {

    long id;
    String name;
    long modifiedDate;
    long modifiedBy;
    long createdDate;
    long createdBy;
    String lname;
    Boolean status;

    public RelationshipDto() {

    }

    public RelationshipDto(Cursor cursor) {
        try {
            id = cursor.getLong(cursor.getColumnIndex("_id"));
            name = cursor.getString(cursor.getColumnIndex("name"));
            lname = cursor.getString(cursor.getColumnIndex("local_name"));
            modifiedDate = cursor.getLong(cursor.getColumnIndex("modified_date"));
            modifiedBy = cursor.getLong(cursor.getColumnIndex("modified_by"));
            createdDate = cursor.getLong(cursor.getColumnIndex("created_date"));
            createdBy = cursor.getLong(cursor.getColumnIndex("created_by"));
            if(cursor.getInt(cursor.getColumnIndex("status")) == 1) {
                status = true;
            }
            else {
                status = false;
            }
        }
        catch(Exception e) {

        }
    }

}
