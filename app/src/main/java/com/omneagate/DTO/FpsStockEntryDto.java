package com.omneagate.DTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 10/8/15.
 */
@Data
public class FpsStockEntryDto extends BaseDto implements Serializable {

    List<FPSStockDto> openingStockList;
    ;

    long serialVersionUID = 7470317664068993864L;

    String deviceNumber;


}
