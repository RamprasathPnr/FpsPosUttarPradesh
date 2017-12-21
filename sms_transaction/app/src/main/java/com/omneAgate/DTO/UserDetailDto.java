package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;

import lombok.Data;

/**
 * This class is used to transfer UserDetails
 *
 * @author user1
 */
@Data
public class UserDetailDto implements Serializable {

    Long id;                //Unique id to identify the user

    String userId;

    String username;        // Users name

    String password;        //Users password is hashed and stored.

    String profile;            // an enum values for FPS_USER, ADMIN, SUPER_ADMIN

    Long createdDate;        // Date of creation

    Long modifiedDate;      // Date of modification

    Long createdBy;            // used to identify created user

    Long modifiedBy;            // used to identify created user

    FpsStoreDto fpsStore;

    public UserDetailDto(Cursor cur) {
        username = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_USERS_NAME));
        password = cur.getString(cur
                .getColumnIndex(FPSDBConstants.KEY_USERS_PASS_HASH));
        fpsStore = new FpsStoreDto(cur);
    }
    // FPS id
}
