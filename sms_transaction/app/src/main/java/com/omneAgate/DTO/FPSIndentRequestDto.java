package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.Constants.FPSDBConstants;

import java.util.Set;

import lombok.Data;

@Data
public class FPSIndentRequestDto extends BaseDto {


    //Godown identifier
    long godownId;

    //FPS Identifier
    long fpsId;

  /*  //Product identifier
    long productId;

    //This value is calculated based on formula
    double quantity;*/

    //Approval status by the Taluk officer
    boolean talukOffiApproval;

    //Date of approval
    long dateOfApproval;

    //Quantity modified by taluk officer
    double modifiedQuantity;

    //Indent can be rejected or approved.
            /*	true - Approved.
                 false - Rejected.
			 	NULL - None
			 */
    boolean status;

    //Reason for indent rejection
    String reason;

    //Additional Notes from Taluk office if required
    String description;

    //Product Dto
    Set<FpsIntentReqProdDto> prodDtos;

    public FPSIndentRequestDto() {

    }

    public FPSIndentRequestDto(Cursor c) {

        godownId = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_FPS_INTENT_REQUEST_GODOWN_ID));
        fpsId = c.getLong(c.getColumnIndex(FPSDBConstants.KEY_FPS_INTENT_REQUEST_FPS_ID));


    }
}
