package com.omneagate.TransactionController;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.gson.Gson;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.SmsProviderDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.TransactionMessage;
import com.omneagate.Util.Util;

/**
 * Created by Rajesh on 4/8/2015.
 */
public class SmsRegistrationConnector implements CardRegistration {

    final String singleSpace = " ";
    BenefActivNewDto benefActivNewDto;
    Context context;
    String mobileNumber, prefixKey;

    @Override
    public boolean process(Context context, TransactionBaseDto transaction, BenefActivNewDto benefActivNewDto) {
        this.benefActivNewDto = benefActivNewDto;
        this.context = context;
        SmsProviderDto smsProvider = FPSDBHelper.getInstance(context).getSmsProvider();
        mobileNumber = smsProvider.getIncomingNumber();
        prefixKey = smsProvider.getPrefixKey();
/*        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        mobileNumber = prefs.getString("providerNum","09248006202");
        prefixKey = prefs.getString("prefixKey","");*/
        String message = getSmsMessage(transaction);
        boolean messageStatus = sendSMS(message);
        if (messageStatus) {
            Gson gson = new Gson();
            String updateBill = gson.toJson(benefActivNewDto);
            if (benefActivNewDto.getRationCardNumber() != null)
                TransactionMessage.getInstance().getTransactionMessage().put(benefActivNewDto.getRationCardNumber(), updateBill);
        }
        return messageStatus;
    }

    private boolean sendSMS(String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobileNumber, null, message, null, null);
            Log.i("Message & Mobile ", message + "  " + mobileNumber);
            return true;
        } catch (Exception e) {
            Util.LoggingQueue(context, "SMS ERROR", e.toString());
            Log.e("Registration Error", e.toString(), e);
            return false;
        }
    }

    private String getSmsMessage(TransactionBaseDto transaction) {

        if (prefixKey == null) {
            prefixKey = "";
        }
        String data = prefixKey + singleSpace;
        data = data + "[H]";
        data = data + transaction.getTransactionType().getTxnType() + "|";
        data = data + benefActivNewDto.getDeviceNum() + "|";
        if (benefActivNewDto.getMobileNum() == null) {
            benefActivNewDto.setMobileNum("");
        }
        if (benefActivNewDto.getTransactionId() == null) {
            benefActivNewDto.setTransactionId("");
        }
        if (benefActivNewDto.getOtpEntryTime() == null) {
            benefActivNewDto.setOtpEntryTime("");
        }
        if (benefActivNewDto.getOtp() == null) {
            benefActivNewDto.setOtp("");
        }
        data = data + benefActivNewDto.getOtpEntryTime() + "|";
        data = data + benefActivNewDto.getOtp() + "|" + benefActivNewDto.getMobileNum() + "|" + benefActivNewDto.getTransactionId() + "|[H][D]";
        data = data + benefActivNewDto.getRationCardNumber() + "|";

        if (benefActivNewDto.getEncryptedUfc() == null) {
            benefActivNewDto.setEncryptedUfc("");
        }
        if (benefActivNewDto.getCardTypeDef() == null) {
            data = data + "|";
        } else {
            data = data + benefActivNewDto.getCardTypeDef() + "|";
        }
        data = data + benefActivNewDto.getEncryptedUfc() + "|";
        data = data + benefActivNewDto.getNumOfCylinder() + "|";
        data = data + benefActivNewDto.getNumOfAdults() + "|";
        data = data + benefActivNewDto.getNumOfChild() + "|[D]";
        return data;
    }

}
