package com.omneagate.DTO;

import lombok.Data;

/**
 * Used to send QRcode details as input to server
 */
@Data
public class QRCodeDetails {

    String qrCode;//123456",

    String deviceId;

}
