package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by user1 on 7/10/15.
 */

@Data
public class OpeningStockHistoryDto extends BaseDto implements Serializable{

    String deviceNum;
}
