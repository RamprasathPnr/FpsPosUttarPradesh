package com.omneagate.DTO;

import java.util.Date;

import lombok.Data;

@Data
public class CloseSaleTransactionDto {

    long id;

    long FPS_id;

    Date createddate;

    long notxns;

    Date dateOfTxn;
}
