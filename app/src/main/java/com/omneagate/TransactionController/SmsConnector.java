package com.omneagate.TransactionController;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.gson.Gson;
import com.omneagate.DTO.SmsProviderDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TransactionMessage;
import com.omneagate.Util.Util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rajesh on 4/8/2015.
 */
public class SmsConnector implements Transaction {

    UpdateStockRequestDto updateStockRequest;
    Context context;
    String mobileNumber, prefixKey;

    @Override
    public boolean process(Context context, TransactionBaseDto transaction, UpdateStockRequestDto updateStock) {
        updateStockRequest = updateStock;
        this.context = context;

        SmsProviderDto smsProvider = FPSDBHelper.getInstance(context).getSmsProvider();
        mobileNumber = smsProvider.getIncomingNumber();
        prefixKey = smsProvider.getPrefixKey();
      /*  SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        mobileNumber = prefs.getString("providerNum","09248006202");
        prefixKey = prefs.getString("prefixKey","");*/
        String message = getSmsMessage(transaction);
        boolean messageStatus = sendSMS(message);
        if (messageStatus) {
            Gson gson = new Gson();
            String updateBill = gson.toJson(updateStockRequest);
            if (updateStock.getBillDto() != null)
                TransactionMessage.getInstance().getTransactionMessage().put(updateStock.getBillDto().getTransactionId(), updateBill);
            else {
                if (updateStock.getUfc() != null)
                    TransactionMessage.getInstance().getTransactionMessage().put(updateStock.getUfc(), updateBill);

            }
        }
        return messageStatus;
    }

    private boolean sendSMS(String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(mobileNumber, null, message, null, null);
            Log.i("Message", message);
            return true;
        } catch (Exception e) {
            Util.LoggingQueue(context, "SMS ERROR", e.toString());
            Log.e("SMS Exception", e.toString(), e);
            return false;
        }
    }

    private String getSmsMessage(TransactionBaseDto transaction) {
        if (prefixKey == null) {
            prefixKey = "";
        }
        String data = prefixKey + " ";
        data = data + "[H]";
        data = data + transaction.getTransactionType().getTxnType() + "|";
        String transactionId = "";

        if (updateStockRequest.getBillDto() != null) {
            transactionId = updateStockRequest.getBillDto().getTransactionId();
        }
        data = data + transactionId + "|";
        if (updateStockRequest.getRefNumber() == null) {
            updateStockRequest.setRefNumber("");
        }
        data = data + updateStockRequest.getRefNumber() + "|";
        data = data + updateStockRequest.getUfc() + "|";
        data = data + "1|";
        if (updateStockRequest.getRmn() != null)
            data = data + updateStockRequest.getRmn() + "|";
        else
            data = data + getRMN() + "|";
        data = data + SessionId.getInstance().getFpsId() + "|";
        if (updateStockRequest.getBillDto() != null) {
            /*NumberFormat numberFormat = new DecimalFormat("#0.00");
            numberFormat.setRoundingMode(RoundingMode.CEILING);*/
            String amt1 = Util.priceRoundOffFormat(updateStockRequest.getBillDto().getAmount());
            data = data + amt1 + "|";
        } else {
            data = data + "0|";
        }
        data = data + updateStockRequest.getOtpId() + "|";
        SimpleDateFormat toDate = new SimpleDateFormat("hhmmss", Locale.getDefault());
        data = data + toDate.format(new Date()) + "[H]";
        data = data + "[D]";
        if (updateStockRequest.getBillDto() != null) {
            List<BillItemProductDto> billItems = new ArrayList<>(updateStockRequest.getBillDto().getBillItemDto());
            for (BillItemProductDto billItem : billItems) {
                data = data + billItem.getProductId() + ",";
                /*NumberFormat decimalFormat = new DecimalFormat("#0.000");
                decimalFormat.setRoundingMode(RoundingMode.CEILING);*/
                String qty1 = Util.quantityRoundOffFormat(billItem.getQuantity());
                data = data + qty1 + "|";
            }
        }
        data = data + "[D]";
        return data;
    }

    private String getRMN() {
        try {
            String rmn = FPSDBHelper.getInstance(context).beneficiaryDto(updateStockRequest.getUfc()).getMobileNumber();
            if (rmn == null) {
                rmn = "";
            }
            return rmn;
        } catch (Exception e) {
            return "";
        }

    }
}
