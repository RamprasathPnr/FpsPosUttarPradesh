package com.omneagate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.InsertIntoDatabase;
import com.omneagate.Util.Util;


//SplashActivity initial activity of this App

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
        String languageCode = prefs.getString("lCode", "ta");
        String serverUrl = prefs.getString("serverUrl", "http://192.168.1.33:9097");
        Util.changeLanguage(this, languageCode);
        GlobalAppState.language = languageCode;
        GlobalAppState.serverUrl = serverUrl;
        AndroidDeviceProperties device = new AndroidDeviceProperties(this);
        GlobalAppState.deviceId = device.getDeviceProperties().getSerialNumber();

        actionBarCreation();

        if (!prefs.getBoolean("register", false)) {
            InsertIntoDatabase db = new InsertIntoDatabase(this);
            db.insertIntoDatabase();
        }

    }

    /*
    *  Database helper called as writable database
    *  Starting services for android application
    *  */
    @Override
    protected void onStart() {
        super.onStart();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("On Stop", "Login On Stop");
    }


    /*Concrete method*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {

    }
}