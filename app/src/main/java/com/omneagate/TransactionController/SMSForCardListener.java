package com.omneagate.TransactionController;

import com.omneagate.DTO.BenefActivNewDto;

/**
 * Created by user1 on 17/4/15.
 */
public interface SMSForCardListener {
    void smsCardReceived(BenefActivNewDto benefActivNewDto);
}
