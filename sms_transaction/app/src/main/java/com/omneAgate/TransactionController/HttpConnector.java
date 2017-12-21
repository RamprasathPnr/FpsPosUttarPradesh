package com.omneagate.TransactionController;

import android.content.Context;

import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.UpdateStockRequestDto;

/**
 * Created by Rajesh on 4/8/2015.
 */
public class HttpConnector implements Transaction {
    @Override
    public boolean process(Context context, TransactionBaseDto transaction, UpdateStockRequestDto updateStock) {
        return false;
    }
}
