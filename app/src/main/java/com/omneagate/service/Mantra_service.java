package com.omneagate.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.MantraDeviceDetailsDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

/**
 * Created by user1 on 30/11/17.
 */
public class Mantra_service extends IntentService {

    String TAG = "Mantra_service";
    private NetworkConnection networkConnection;
    public HttpClientWrapper httpConnection;
    Handler SyncHandler;
    public Mantra_service() {
        super("Mantra_service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        SyncHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ServiceListenerType type = (ServiceListenerType) msg.obj;
                switch (type) {
                    case MANTRA_DEVICE_DETAILS:
                        get_mantra_response(msg.getData());
                        break;
                }
            }

        };
        get_mantra_details();
    }


    private void get_mantra_details() {
        try {
            if (networkConnection.isNetworkAvailable()) {
                String url = "/mantradevice/create/update";
                MantraDeviceDetailsDto mantraDto = new MantraDeviceDetailsDto();
                mantraDto.setDeviceDetails(this);
                String details = new Gson().toJson(mantraDto);
                StringEntity se = new StringEntity(details, HTTP.UTF_8);
//                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.MANTRA_DEVICE_DETAILS,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "configuration Request Error", e.toString());
            Log.e("LoginActivity", e.toString(), e);
        }
    }

    private void get_mantra_response(Bundle message) {
        String response = message.getString(FPSDBConstants.RESPONSE_DATA);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        BaseDto baseDto = gson.fromJson(response, BaseDto.class);
        if (baseDto.getStatusCode() == 0) {

        }
    }
}

