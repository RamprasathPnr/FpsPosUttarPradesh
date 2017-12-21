package com.omneagate.DTO;

import com.omneagate.DTO.EnumDTO.StockTransactionType;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class StockRequestDto extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    public static class ProductList {

        @Getter
        @Setter
        Double quantity;

        @Getter
        @Setter
        Long id;

        @Getter
        @Setter
        Double recvQuantity;

        @Getter
        @Setter
        String adjustment;
    }


    StockTransactionType type;

    Long fpsId;

    Long godownId;

    List<ProductList> productLists;

    String deliveryChallanId;

    String batchNo;

    long date;

    String createdBy;

    long inwardKey;

    String unit;

}
