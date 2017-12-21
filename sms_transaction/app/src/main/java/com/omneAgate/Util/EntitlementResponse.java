package com.omneagate.Util;

import com.omneagate.DTO.QRTransactionResponseDto;

import lombok.Getter;
import lombok.Setter;


/**
 * Singleton class for Response from  server for qrTransactionResponse
 */
public class EntitlementResponse {
    private static EntitlementResponse mInstance = null;

    @Getter
    @Setter
    private QRTransactionResponseDto qrcodeTransactionResponseDto;

    private EntitlementResponse() {
        qrcodeTransactionResponseDto = new QRTransactionResponseDto();
    }

    public static EntitlementResponse getInstance() {
        if (mInstance == null) {
            mInstance = new EntitlementResponse();
        }
        return mInstance;
    }

}
