package com.omneagate.DTO;

import lombok.Data;

@Data
public class StockAllotmentDto {

    Long id;

    int productId;

    Double allotedQuantity;

    Integer month;

    Integer year;

    Double recivQuantity;

    public StockAllotmentDto() {

    }


}
