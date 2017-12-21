package com.omneagate.DTO;

import lombok.Data;

/**
 * Created by user1 on 11/5/15.
 */

@Data
public class BenefCardImageDto extends BaseDto {

    String rationNumber;

    byte[] cardImage;

    String address;

}
