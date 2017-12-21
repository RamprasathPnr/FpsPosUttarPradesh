package com.omneagate.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Created by root on 27/7/17.
 */
@Data
public class FPSPortablityDayWiseReport {

    private String respMessage;
    private String respMsgCode;
    private String transDate;
    private Double totalAmt;
    private String transRoNo;

    private List<Product> productList = null;

    public FPSPortablityDayWiseReport() {
        this.productList = new ArrayList<Product>();

    }
}
