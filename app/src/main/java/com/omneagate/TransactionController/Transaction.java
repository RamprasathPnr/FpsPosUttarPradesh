package com.omneagate.TransactionController;

import android.content.Context;

import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.UpdateStockRequestDto;

/**
 * Created by user1 on 8/4/15.
 */
public interface Transaction {

    boolean process(Context context, TransactionBaseDto transaction, UpdateStockRequestDto updateStock);

}

