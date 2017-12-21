package com.omneagate.DTO;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class FpsAllocationCommodityDetailDto implements Serializable {
    /**
     * for serialization
     */
    private static final long serialVersionUID = 1L;
    /**
     * unique Id
     */
    Long id;
    /**
     * allotment for the fps
     */
    FpsStoreDto fpsStoreDto;
    GroupDto groupDto;
    Double entitlement;
    Double month_OB;
    Double allocationRequiredQty;
    Double allocatedQty;
    Double previousMonthOfftakeQty;
    Double advanceQty;
    Double currentQty;
    Double totalQty;
    /**
     * the allotment for the month
     */
    String month;
    /**
     * allotment for the year
     */
    Integer year;
    /**
     * created user
     */
    Long createdBy;
    /**
     * created on
     */
    Long createdDate;
    /**
     * modified by user
     */
    Long modifiedBy;
    /**
     * modified on
     */
    Long modifiedDate;
    Boolean isFinalized;
    Boolean isTalukFinalized;
    FpsAllocationKeroseneDetailDto fpsAllocationKeroseneDetailDto;
}
