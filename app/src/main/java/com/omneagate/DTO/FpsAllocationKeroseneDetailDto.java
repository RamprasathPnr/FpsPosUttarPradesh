package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class FpsAllocationKeroseneDetailDto implements Serializable {
    /**
     * for serialization
     */
    private static final long serialVersionUID = 1L;
    Long id;
    Double ioc;
    Double bpcl;
    Double hpcl;
}
