package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 21/3/15.
 */

@Data
public class OpeningClosingBalanceDto {
    String name;

    Double quantity;

    double openingBalanceQuantity;

    double closingBalanceQuantity;

    Double cost;

}
