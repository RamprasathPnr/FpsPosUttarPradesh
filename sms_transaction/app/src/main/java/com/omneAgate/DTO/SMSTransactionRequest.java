package com.omneagate.DTO;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SMSTransactionRequest implements Serializable {


    long id;

    long transactionid;

    Date createddate;

    String UFC;

    String rawdata;
}
