package com.omneagate.Util;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by user1 on 13/4/15.
 */
public class ParseMessage {
    public ParseMessage() {

    }

    public String parseData(String message) {
        SMSMessageDto smsMessage = new SMSMessageDto();
        try {
            String headerString = StringUtils.substringBetween(message, "[H]", "[H]");
            String footerString = StringUtils.substringBetween(message, "[D]", "[D]");
            String[] header = StringUtils.split(headerString, "\\|");
            Log.e("data", headerString);
            int statusCode = Integer.parseInt(header[0]);
            if (statusCode == 0) {
                smsMessage.setStatusCode(statusCode);
                int transType = Integer.parseInt(header[1]);
                smsMessage.setTransactionType(transType);
                if (transType == 203 || transType == 204 || transType == 205) {
                    smsMessage.setMobileNumber(header[3]);
                    smsMessage.setRationCardNumber(footerString);
                } else {
                    String otpId = header[2];
                    smsMessage.setOtpId(otpId);
                    String oTP = header[3];
                    smsMessage.setOtp(oTP);
                    String billRef = header[4];
                    smsMessage.setBillRef(billRef);
                    String billId = header[5];
                    smsMessage.setBillId(billId);
                    if (header.length > 6) {
                        String transId = header[6];
                        smsMessage.setTransactionId(transId);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
        Gson gson = new Gson();
        return gson.toJson(smsMessage);

    }
}
