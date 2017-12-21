package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 10/3/15.
 */

@Data
public class TransactionBaseDto {

    BaseDto baseDto;        //BaseDto for all transaction related dto

    TransactionTypes transactionType; //TransactionType

    String type;
}