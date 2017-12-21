package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 30/9/16.
 */
@Data
public class InspectionVewDto {
    private long commodity;

    /**stock available in pos*/
    private Double posStock;

    /** Stock available in FPS */
    private Double actualStock;

    /** Variance of actual stock and pos stock*/
    private Double variance;

    /** Remarks of the inspector */
    private String remarks;

    private String billnumber;
    private String cardnumber;
}
