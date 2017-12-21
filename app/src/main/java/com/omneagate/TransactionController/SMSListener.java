package com.omneagate.TransactionController;

import com.omneagate.DTO.UpdateStockRequestDto;

/**
 * Created by user1 on 17/4/15.
 */
public interface SMSListener {
    void smsReceived(UpdateStockRequestDto stockRequestDto);
}
