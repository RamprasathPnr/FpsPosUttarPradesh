package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class CloseOfProductDto implements Serializable {
    private static final long serialVersionUID = 1L;

    long id;

    long closeId;

    long productid;

    String totalQuantity;
}
