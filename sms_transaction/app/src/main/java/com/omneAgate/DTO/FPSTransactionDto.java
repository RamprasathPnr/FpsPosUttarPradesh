package com.omneagate.DTO;

import java.util.Date;

import lombok.Data;

@Data
public class FPSTransactionDto {
    long id;

    String referenceNumber;

    long fpsId;

    Date createdDate;

    String rawdataRequest;

    char transactionType;

    String rawdataResponse;

    char channel;

    String qrCode;

    String rmn;
}
