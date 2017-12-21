package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.DeviceStatusRequest;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

public class RegistrationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        appState = (GlobalAppState) getApplication();
        httpConnection = new HttpClientWrapper();
        Util.setTamilText((TextView) findViewById(R.id.textRegistration), R.string.deviceRegistration);
        ((Button) findViewById(R.id.registrationButton)).setText(R.string.status);
    }

    /*Concrete method*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case DEVICE_STATUS:
                setStatusCheck(message);
                break;
            default:
                Util.messageBar(RegistrationActivity.this, getString(R.string.serviceNotAvailable));
                break;
        }

    }

    private void setStatusCheck(Bundle message) {
        String response = message.getString(FPSDBConstants.RESPONSE_DATA);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        DeviceStatusRequest deviceRegistrationResponse = gson.fromJson(response,
                DeviceStatusRequest.class);
        if (deviceRegistrationResponse.isActive()) {
            Util.storePreferenceApproved(this);
            /*startService(new Intent(this, ConnectionHeartBeat.class));
            startService(new Intent(this, UpdateDataService.class));
            startService(new Intent(this, RemoteLoggingService.class));
            startService(new Intent(this, OfflineTransactionManager.class));*/
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Util.messageBar(RegistrationActivity.this, getString(R.string.deviceRegistration));
        }
    }

    //onclick event for login button
    public void registrationStatus(View view) {
        try {
            AndroidDeviceProperties deviceProperties = new AndroidDeviceProperties(this);
            DeviceStatusRequest deviceRegister = new DeviceStatusRequest();
            deviceRegister.setDeviceNumber(deviceProperties.getDeviceProperties().getSerialNumber());
            String device = new Gson().toJson(deviceRegister);
            StringEntity se = new StringEntity(device, HTTP.UTF_8);
            String url = "/device/getStatus";
            httpConnection.sendRequest(url, null, ServiceListenerType.DEVICE_STATUS,
                    SyncHandler, RequestType.POST, se, this);
        } catch (Exception e) {
            Log.e("LoginActivity_userLogin", e.toString(), e);
        }
    }

}
