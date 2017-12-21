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

    String referenceNo;

    String godownName;

    String godownCode;

    public static class ProductList extends BaseDto {

        @Getter
        @Setter
        Double quantity;

        @Getter
        @Setter
        Long id;

        @Getter
        @Setter
        Long serverId;

        @Getter
        @Setter
        Double recvQuantity;

        @Getter
        @Setter
        String adjustment;

        @Getter
        @Setter
        int adjustmentItem;

        @Getter
        @Setter
        int month;

        @Getter
        @Setter
        int year;

        @Getter
        @Setter
        long fpsAckDate;

        @Getter
        @Setter
        StockTransactionType type;


    }

}
