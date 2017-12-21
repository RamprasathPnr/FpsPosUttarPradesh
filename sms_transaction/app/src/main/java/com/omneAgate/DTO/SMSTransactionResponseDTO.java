package com.omneagate.DTO;


import java.util.Date;

import lombok.Data;

@Data
public class SMSTransactionResponseDTO {

    long id;

    long FPSId;

    Date date;
    /* @Getter @Setter
      public BlobType rawdata; */

    long transactionid;

    String UFC;

    long SMSRequestId;

    Date createddate;

}
