package com.omneagate.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.gson.Gson;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.ParseMessage;
import com.omneagate.Util.SMSMessageDto;
import com.omneagate.activity.SMSActivation.CardRegistration;

/**
 * Created by user1 on 30/3/15.
 */
public class IncomingSMSReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            final Bundle bundle = intent.getExtras();

            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < 1; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String message = currentMessage.getDisplayMessageBody();
                        Log.e("Error key", message);
                        if (message.contains("[H]") && message.contains("[D]")) {
                            ParseMessage parse = new ParseMessage();
                            String messageParsed = parse.parseData(message);
                            Gson gson = new Gson();
                            SMSMessageDto smsMessageDto = gson.fromJson(messageParsed, SMSMessageDto.class);
                            if (smsMessageDto.getTransactionType() == 100 || smsMessageDto.getTransactionType() == 102 || smsMessageDto.getTransactionType() == 104) {
                                BillDto bill = new BillDto();
                                bill.setTransactionId(smsMessageDto.getTransactionId());
                                bill.setId(Long.parseLong(smsMessageDto.getBillId()));
                                bill.setChannel('S');
                                bill.setBillRefId(Long.parseLong(smsMessageDto.getBillRef()));
                                FPSDBHelper.getInstance(context).billUpdate(bill);
                            } else if (smsMessageDto.getTransactionType() == 101 || smsMessageDto.getTransactionType() == 103) {
                                UpdateStockRequestDto stock = new UpdateStockRequestDto();
                                stock.setOtpId(Long.parseLong(smsMessageDto.getOtpId()));
                                stock.setOtp(smsMessageDto.getOtp());
                                stock.setRefNumber(smsMessageDto.getBillRef());
                                stock.setUfc(smsMessageDto.getTransactionId());
                                if (GlobalAppState.listener != null) {
                                    GlobalAppState.listener.smsReceived(stock);
                                }
                            }else if(smsMessageDto.getTransactionType()==203){
                                FPSDBHelper.getInstance(context).updateRegistration(smsMessageDto.getRationCardNumber(), "S", "Success");
                             }else if(smsMessageDto.getTransactionType()==204){
                                BenefActivNewDto benefActivNewDto = new BenefActivNewDto();
                                benefActivNewDto.setRationCardNumber(smsMessageDto.getRationCardNumber());
                                benefActivNewDto.setMobileNum(smsMessageDto.getMobileNumber());
                                if (GlobalAppState.smsListener != null) {
                                    GlobalAppState.smsListener.smsCardReceived(benefActivNewDto);
                                }}else if(smsMessageDto.getTransactionType()==205){
                                BenefActivNewDto benefActivNewDto = new BenefActivNewDto();
                                if (GlobalAppState.smsListener != null) {
                                    GlobalAppState.smsListener.smsCardReceived(benefActivNewDto);
                                }
                            }
                        } else {
                            message = message.replaceAll("[^a-zA-Z0-9]", "");
                            if (validCard(message)) {
                                String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                                CardRegistration cardRegistration = new CardRegistration(context);
                                cardRegistration.cardActivationRequest(message, phoneNumber);

                            }
                        }

                    } // end for loop

                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e, e);

            }
        }
    }

    private boolean validCard(String message) {
        boolean valid = false;
        try {
            if (message.length() <= 15) {
                /*String message1 = message.substring(0, 2);
                String message2 = message.substring(2, 3);
                String message3 = message.substring(3);
                if (StringUtils.isNumeric(message1) && StringUtils.isNumeric(message3) && StringUtils.isAlpha(message2)) {
                    valid = true;
                } else {
                    valid = false;
                }*/
                valid = true;
            }
        } catch (Exception e) {
            valid = false;
            Log.e("Error", e.toString(), e);
        }
        return valid;
    }
}