package com.omneagate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BackgroundServiceDto;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.EnumDTO.CommonStatuses;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.RoleFeature;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.FpsAdvanceStockDto;
import com.omneagate.DTO.POSOperatingHoursDto;
import com.omneagate.DTO.RoleFeatureDto;
import com.omneagate.DTO.RollMenuDto;
import com.omneagate.DTO.UpgradeDetailsDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.LogoutDialog;
import com.omneagate.activity.dialog.fpsRollViewAdpter;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ServiceHistoryDetailActivity extends BaseActivity {

    BackgroundServiceDto backgroundServiceDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_service_history_detail);
        try {
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.background_process_history);
            setUpPopUpPage();
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            backgroundServiceDto = (BackgroundServiceDto) getIntent().getSerializableExtra("backgroundServiceDto");
            initializeValues();
        }
        catch(Exception e) {}
    }

    private void initializeValues() {
        ((TextView) findViewById(R.id.requestTxt)).setText(backgroundServiceDto.getRequestData());
        ((TextView) findViewById(R.id.responseTxt)).setText(backgroundServiceDto.getResponseData());
        String requestDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(backgroundServiceDto.getRequestDateTime());
        ((TextView) findViewById(R.id.requestDateTxt)).setText(requestDate);
        String responseDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(backgroundServiceDto.getResponseDateTime());
        ((TextView) findViewById(R.id.responseDateTxt)).setText(responseDate);
        ((TextView) findViewById(R.id.errorDescriptionTxt)).setText(backgroundServiceDto.getErrorDescription());
        ((TextView) findViewById(R.id.serviceTypeTxt)).setText(backgroundServiceDto.getServiceType());
        ((TextView) findViewById(R.id.statusTxt)).setText(backgroundServiceDto.getStatus());
    }

    //Called when user press back button
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


}


