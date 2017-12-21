package com.omneagate.TransactionController;

import android.content.Context;

import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.TransactionBaseDto;

/**
 * Created by user1 on 8/4/15.
 */
public interface CardRegistration {

    boolean process(Context context, TransactionBaseDto transaction, BenefActivNewDto benefActivNewDto);

}

