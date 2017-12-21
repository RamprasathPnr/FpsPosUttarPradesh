package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 12/10/16.
 */
@Data
public class Reconciliation_Dto {
    Double amount;                    //amount
    String transactionId;
    String transactionDate;
    String billDate;
}
