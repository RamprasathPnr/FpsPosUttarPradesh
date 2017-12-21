package com.omneagate.DTO;

import lombok.Data;

@Data
public class FpsRequestDataDto {


    String deviceid; //MAC id


/*    String billDate; // if null means, send all last 30 days data

    String commodityDate; // if null means ,send all commodity data

    String beneficiaryDate; // if null means , send all beneficiary data

    String allotmentDate; // if null means , send all beneficiary data

    String transactionDate; // if null means,send transaction type table data

    char type; //'G','W','Y'

    long allotmentid;

    int fromRange, toRange;

    Date fpsstoreDate;

    Date fpsStockInDate;

    boolean fpsakstatus;*/

    String lastSyncDate;


}
