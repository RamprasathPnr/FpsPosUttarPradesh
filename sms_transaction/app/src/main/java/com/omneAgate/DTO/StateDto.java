package com.omneagate.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class StateDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long id;

    String name; // the name of the state.

    String code; // the code of the state.
}
