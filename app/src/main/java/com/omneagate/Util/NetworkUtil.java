package com.omneagate.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by user1 on 30/3/15.
 */
public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {

        Log.e("NetworkUtil", "getConnectivityStatus() called " );

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {

            Log.e("NetworkUtil", "getConnectivityStatus() Network Type = "+ activeNetwork.getType() );

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {

                Log.e("NetworkUtil", "getConnectivityStatus() Network Type is wifi " );

                return TYPE_WIFI;
            }

           else  if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                Log.e("NetworkUtil", "getConnectivityStatus() Network Type is Mobile " );

                return TYPE_MOBILE;

            }
        }else{
            Log.e("NetworkUtil", "getConnectivityStatus() NetworkInfo is  null " );

        }

        return TYPE_NOT_CONNECTED;
    }
}