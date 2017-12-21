package com.omneagate.Util;

import com.omneagate.DTO.Product;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.RCAuthResponse;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * Singleton class for Response from  server for qrTransactionResponse
 */
public class EntitlementResponse {
    private static com.omneagate.Util.EntitlementResponse mInstance = null;

    @Getter
    @Setter
    private QRTransactionResponseDto qrcodeTransactionResponseDto;

    private EntitlementResponse() {
        qrcodeTransactionResponseDto = new QRTransactionResponseDto();
    }

    @Getter
    @Setter
    private List<Product> itemEntitlementList;

    @Getter
    @Setter
    private RCAuthResponse rcAuthResponse;

    public static com.omneagate.Util.EntitlementResponse getInstance() {
        if (mInstance == null) {
            mInstance = new com.omneagate.Util.EntitlementResponse();
        }
        return mInstance;
    }

    public void clear()
    {
        mInstance = null;
    }

}
