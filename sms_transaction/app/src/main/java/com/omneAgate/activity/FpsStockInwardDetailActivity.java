package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.ChellanProductDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.StockInwardDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FpsStockInwardDetailActivity extends BaseActivity {

    // Chellan Id Textview
    TextView tvChellanId;

    //Acknowledgement Date Textview
    TextView tvAckDate;

    // Batch no Textview
    TextView tvBatchNo;

    //CheckBox for Acknowledgement status
    CheckBox cBAckStatus;


    // Godown Dto list
    List<GodownStockOutwardDto> fpsStockInwardDetailList;

    // Chellan Product Dto
    Set<ChellanProductDto> chellanProductDtoSet;

    // Godown Dto
    GodownStockOutwardDto godownStockOutwardDtos;

    //Chellan ID
    long chellanId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps_stock_inward_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        chellanId = getIntent().getExtras().getLong("cid");
        tvChellanId = (TextView) findViewById(R.id.tvFIDChellanId);
        tvAckDate = (TextView) findViewById(R.id.tvFIDAckDate);
        tvBatchNo = (TextView) findViewById(R.id.tvFIDFIDBatchNo);
        createPage();

    }

    private void createPage() {
        Util.setTamilText((TextView) findViewById(R.id.tvFIDChellanIdLabel), getString(R.string.chellanId));
        Util.setTamilText((TextView) findViewById(R.id.fpsIDcheckBoxStatus), getString(R.string.ackStatus));
        Util.setTamilText((TextView) findViewById(R.id.tvFIDAckDateLabel), getString(R.string.ackDate));
        Util.setTamilText((TextView) findViewById(R.id.tvFIDBatchNoLabel), getString(R.string.batchNumber));
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardDetailProductLabel), getString(R.string.product));
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardDetailQuantityLabel), getString(R.string.quantity));
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardDetailReceivedQuantityLabel), getString(R.string.receivedQuantity));
        Util.setTamilText((TextView) findViewById(R.id.btnfpsIDSubmit), getString(R.string.submit));
        Util.setTamilText((TextView) findViewById(R.id.btnfpsIDCancel), getString(R.string.cancel));
        cBAckStatus = (CheckBox) findViewById(R.id.fpsIDcheckBoxStatus);

        cBAckStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                godownStockOutwardDtos.setFpsAckStatus(isChecked);
                if (isChecked) {
                    ((Button) findViewById(R.id.btnfpsIDSubmit)).setVisibility(View.VISIBLE);
                } else {
                    ((Button) findViewById(R.id.btnfpsIDSubmit)).setVisibility(View.GONE);
                }
            }
        });

        fpsStockInwardDetailList = FPSDBHelper.getInstance(this).showFpsStockInvardDetail(chellanId);
        for (GodownStockOutwardDto godownStockOutwardDto : fpsStockInwardDetailList) {
            godownStockOutwardDtos = new GodownStockOutwardDto();
            godownStockOutwardDtos = godownStockOutwardDto;

            tvChellanId.setText(":  " + godownStockOutwardDto.getDeliveryChallanId());
            tvBatchNo.setText(":  " + godownStockOutwardDto.getBatchno());
            chellanProductDtoSet = godownStockOutwardDto.getProductDto();
        }

        tvAckDate.setText(":  " + new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime()));

        godownStockOutwardDtos.setFpsAckDate(System.currentTimeMillis());
        configureData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, FpsStockInwardActivity.class));
        finish();
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_fps_stock_inward_detail);
            Log.i("Detail", fpsStockInwardDetailList.toString());
            fpsInwardLinearLayout.removeAllViews();

            List<ChellanProductDto> list = new ArrayList<ChellanProductDto>(chellanProductDtoSet);

            for (int position = 0; position < list.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, list.get(position), position));

            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        }
    }

    /*User entitlement view*/
    private View returnView(LayoutInflater entitle, ChellanProductDto data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_fps_stock_inward_details, null);

        TextView productName = (TextView) convertView.findViewById(R.id.tvProductName);
        TextView quantity = (TextView) convertView.findViewById(R.id.tvQuantity);
        EditText receivedQuantity = (EditText) convertView.findViewById(R.id.tvReceivedQuantity);
        ProductDto product = FPSDBHelper.getInstance(this).getProductDetails(data.getProductId());
        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(product.getLocalProductName()))
            Util.setTamilText(productName, product.getLocalProductName());
        else
            productName.setText(product.getName());
        quantity.setText("" + data.getQuantity());
        receivedQuantity.setId(position);
        receivedQuantity.setText("" + data.getQuantity());

        return convertView;

    }


    // This method submit all data Fps stock outward data with received quantity

    public void onSubmit(View v) {
        List<ChellanProductDto> list = new ArrayList<ChellanProductDto>(chellanProductDtoSet);
        Set<ChellanProductDto> setChellanProductDto = new HashSet<ChellanProductDto>();
        boolean valueEntered = false;
        for (int i = 0; i < list.size(); i++) {
            String receivedQuantity = ((EditText) findViewById(i)).getText().toString().trim();

            ChellanProductDto chellanProductDto = new ChellanProductDto();

            chellanProductDto.setProductId(list.get(i).getProductId());
            Double userEnteredQuantity = 0.0;
            if (receivedQuantity.length() > 0) {
                userEnteredQuantity = Double.parseDouble(receivedQuantity);
            }
            if (userEnteredQuantity > 0.0) {
                valueEntered = true;
            }
            if (userEnteredQuantity > list.get(i).getQuantity()) {
                Util.messageBar(this, getString(R.string.receivedQuantityHigh));
                return;
            }
            chellanProductDto.setQuantity(list.get(i).getQuantity());
            chellanProductDto.setReceiProQuantity(userEnteredQuantity);
            setChellanProductDto.add(chellanProductDto);

        }
        if (valueEntered) {
            godownStockOutwardDtos.setProductDto(setChellanProductDto);
            getRequest();
        } else {
            Util.messageBar(this, getString(R.string.itemHigh));
        }


    }

    // Cancel Button
    public void onCancel(View v) {
        startActivity(new Intent(this, FpsStockInwardActivity.class));
        finish();

    }


    //Reguest to Server

    public void getRequest() {
        try {
            String inwardAck = new Gson().toJson(godownStockOutwardDtos);
            Log.e("Inward", inwardAck);
            StringEntity se = new StringEntity(inwardAck, HTTP.UTF_8);
            httpConnection = new HttpClientWrapper();
            String url = "/fpsStock/update";
            httpConnection.sendRequest(url, null, ServiceListenerType.FPS_INTENT_REQUEST, SyncHandler, RequestType.POST, se, this);

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case FPS_INTENT_REQUEST:
                getStockResponse(message);
                break;

            default:
                Log.e("Error", "Fps Stock Entry");
                break;
        }
    }

    // After response received from server successfully in android
    public void getStockResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("Response", response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            GodownStockOutwardDto godownStockOutwardDto = gson.fromJson(response, GodownStockOutwardDto.class);
            int statusCode = godownStockOutwardDto.getStatusCode();

            if (statusCode == 0) {
                FPSDBHelper.getInstance(this).updateReceivedQuantity(godownStockOutwardDtos);
                StockInwardDialog stockInwardDialog = new StockInwardDialog(this);
                stockInwardDialog.show();
            } else {
                Util.messageBar(this, FPSDBHelper.getInstance(this).retrieveLanguageTable(godownStockOutwardDto.getStatusCode(), GlobalAppState.language).getDescription());
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }


}
