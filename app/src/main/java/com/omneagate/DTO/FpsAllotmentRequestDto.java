package com.omneagate.DTO;

import java.util.Set;

import lombok.Data;

/**
 * Created by user1 on 17/10/16.
 */
@Data
public class FpsAllotmentRequestDto extends BaseDto {
    Set<FpsAllocationCommodityDetailDto> fpsAllocationCommodityDetailDtos;
    Long fpsId;
    String lastSyncTime;
    Integer year;
    String month;
    boolean finalizedStatus;
    Long lastModifiedDate;
}
