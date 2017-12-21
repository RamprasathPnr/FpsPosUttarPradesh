package com.omneagate.DTO;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * This class is used to transfer fpsIndent information
 */

@Data
public class FpsIndentDto implements Serializable {
    long Id;            //Unique Identifier

    long indentId;        //Indent identifier

    long FPSId;        ////FPS id to identify shop

    boolean productid;    //Product identifier

    Date createdate;    //Created date and time
}
