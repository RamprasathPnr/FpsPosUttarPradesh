package com.omneagate.DTO.EnumDTO;

import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.GodownStockOutwardDto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * Created by user1 on 7/9/15.
 */
@Data
public class FpsStockInwardSelect extends BaseDto implements Serializable {

    List<GodownStockOutwardDto> FpsStockInwardconformList;

    int year;

    int Month;
}
