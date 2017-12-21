package com.omneagate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omneagate.service.ServerTimeSetService;

/**
 * Created by root on 29/5/17.
 */
public class ServerTimeSetReceiver  extends BroadcastReceiver {

    public static final int REQUEST_CODE = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, ServerTimeSetService.class);
        context.startService(i);
    }
}