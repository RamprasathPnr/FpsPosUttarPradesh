package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 5/3/15.
 */

@Data
public class BaseDto {

    int statusCode = 2000; // error code. 0 if success else unique error code value

    TransactionTypes transactionType; //Transactiontype

    String trackId;

    ApplicationType appType = ApplicationType.FPS;



}
