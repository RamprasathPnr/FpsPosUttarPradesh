package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 28/7/15.
 */
@Data
public class RoleFeatureDto extends BaseDto implements Serializable {

    Integer userId;
    String rollName;
    Integer featureId;
    Integer parentId;
    Integer rollType;
    List<com.omneagate.DTO.RoleFeatureDto> rollfeature_dto;

      public RoleFeatureDto(){

      }
    public RoleFeatureDto(Cursor cur) {

        userId = cur.getColumnIndex(FPSDBConstants.KEY_ROLE_USERID);
        rollName = cur.getString(cur.getColumnIndex(FPSDBConstants.KEY_ROLE_NAME));
        featureId = cur.getColumnIndex(FPSDBConstants.KEY_ROLE_FEATUREID);
        parentId = cur.getColumnIndex(FPSDBConstants.KEY_ROLE_PARENTID);
        rollType = cur.getColumnIndex(FPSDBConstants.KEY_ROLE_TYPE);

    }


}
