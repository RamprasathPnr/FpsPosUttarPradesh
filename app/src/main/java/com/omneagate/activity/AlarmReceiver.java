package com.omneagate.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.ParseMessage;
import com.omneagate.Util.SMSMessageDto;
import com.omneagate.Util.Util;

/**
 * Created for Alarm Receiver
 */
public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        /*String days = FPSDBHelper.getInstance(context).getMasterData("purgeBill");
        int purgeDays = Integer.parseInt(days);
        Log.e("purging days...........", "" + purgeDays);
        Util.LoggingQueue(context, "purging days received in android.....", ""+purgeDays);
        if(purgeDays > 0) {
            FPSDBHelper.getInstance(context).purge(purgeDays+1);
        }*/
    }
}