package com.omneagate.DTO;

import android.database.Cursor;

import lombok.Data;

/**
 * Created by user1 on 17/8/15.
 */
@Data
public class VersionDto {

    String createdTime;

    String updatedTime;

    String previousVersion;

    String currentVersion;

    String description;

    String  status;

    int referenceId;

    String state;


    public VersionDto(Cursor cursor) {



        createdTime = cursor.getString(cursor.getColumnIndex("created_date"));

        updatedTime = cursor.getString(cursor.getColumnIndex("execution_time"));

        previousVersion = cursor.getString(cursor.getColumnIndex("android_old_version"));

        currentVersion = cursor.getString(cursor.getColumnIndex("android_new_version"));

        description = cursor.getString(cursor.getColumnIndex("description"));

        status = cursor.getString(cursor.getColumnIndex("status"));

        state = cursor.getString(cursor .getColumnIndex("state"));


    }

    public VersionDto() {

    }
}
