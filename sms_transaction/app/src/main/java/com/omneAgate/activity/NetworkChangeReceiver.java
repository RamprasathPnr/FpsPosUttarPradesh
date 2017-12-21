package com.omneagate.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.omneagate.Util.NetworkUtil;

/**
 * Created by user1 on 30/3/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatus(context);
        if (status != 0) {

        }
    }
}