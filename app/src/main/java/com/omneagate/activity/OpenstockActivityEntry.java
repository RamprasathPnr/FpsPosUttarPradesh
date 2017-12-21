package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.FpsStockEntryDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.OpenStackDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created for sending opening stock to server
 */

public class OpenstockActivityEntry extends BaseActivity {

    RelativeLayout keyBoardCustom;
    KeyboardView keyView;
    FpsStockEntryDto entryListStock;
    OpenStackDialog openStackDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityopenstock);


        Util.LoggingQueue(this, "OpenstockActivityEntry", "onCreate() called");


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();
        String stockEntry = intent.getStringExtra("Dtolist");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        entryListStock = gson.fromJson(stockEntry, FpsStockEntryDto.class);
        configureData();
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {
            case OPEN_STOCK:
                openStockSubmitResponse(message);
                onClickUser();
                break;
            default:
                if (progressBar != null) {
                    progressBar.dismiss();
                }

                Util.messageBar(this, getString(R.string.connectionRefused));

                /*findViewById(R.id.btnClose).setOnClickListener(null);
                findViewById(R.id.cancel_button).setOnClickListener(null);
                findViewById(R.id.imageViewBack).setOnClickListener(null);*/
                onClickUser();
                break;
        }
    }

    private void configureData() {
        try {
            httpConnection = new HttpClientWrapper();
            setUpPopUpPageForAdmin();
            keyView = (KeyboardView) findViewById(R.id.customkeyboard);
            Util.LoggingQueue(this, "Stock Status activity", "Main page Called");
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.openingstockheading);
            Util.setTamilText((TextView) findViewById(R.id.commodity), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.current_stock), R.string.current_stock);
            Util.setTamilText((TextView) findViewById(R.id.opening_stock), R.string.opening_stock);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.submit);
            Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.cancel);
            findViewById(R.id.btnClose).setVisibility(View.VISIBLE);
            keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            onClickUser();
            int position = 0;
            transactionLayout.removeAllViews();
            for (FPSStockDto stocks : entryListStock.getOpeningStockList()) {
                LayoutInflater lin = LayoutInflater.from(this);
                transactionLayout.addView(returnView(lin, stocks, position));
                position++;
            }
        } catch (Exception e) {
            Log.e("StockCheckActivity", e.toString(), e);
        }
    }


    private void onClickUser(){
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                severSendStockEntry();
            }
        });
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

     private void onClickNull(){
        findViewById(R.id.btnClose).setOnClickListener(null);
        findViewById(R.id.cancel_button).setOnClickListener(null);
        findViewById(R.id.imageViewBack).setOnClickListener(null);
    }

    private View returnView(LayoutInflater entitle, FPSStockDto data, final int position) {
        View convertView = entitle.inflate(R.layout.adpter_openstock_ne, null);
        TextView productNameTv = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView unitTv = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView openingStockTv = (TextView) convertView.findViewById(R.id.entitlement_opening);
        TextView addOpenStockEt = (TextView) convertView.findViewById(R.id.amount_current);
        /*NumberFormat format = new DecimalFormat("#0.000");
        format.setRoundingMode(RoundingMode.CEILING);*/
        addOpenStockEt.setId(position);
        int maxLength = 9;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        addOpenStockEt.setFilters(fArray);
        String qty = Util.quantityRoundOffFormat(data.getQuantity());
        addOpenStockEt.setText(qty);
        addOpenStockEt.setTextColor(Color.GRAY);
        ProductDto product = FPSDBHelper.getInstance(this).getProductDetails(data.getProductId());
        String productName = product.getName();
        if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi") && product.getLocalProductName() != null) {
            productName = product.getLocalProductName();
        }

        String productUnit = product.getProductUnit();
        if (GlobalAppState.language.equalsIgnoreCase("hi") && product.getLocalProductUnit() != null) {
            productUnit = product.getLocalProductUnit();
        }
//        productNameTv.setText(unicodeToLocalLanguage(productName));
        productNameTv.setText(productName);
        unitTv.setText(unicodeToLocalLanguage(productUnit));
        String qty2 = Util.quantityRoundOffFormat(data.getQuantity());
        openingStockTv.setText(qty2);
        return convertView;
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, OpenStockActivity.class));
        Util.LoggingQueue(this, "openStock activity", "Back button pressed");
        finish();
    }


    private void openStockSubmitResponse(Bundle message) {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Util.LoggingQueue(this, "Stock entry response", "Activation resp:" + response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto base = gson.fromJson(response, BaseDto.class);
            Log.i("Response", response);
            if (base.getStatusCode() == 0) {
                FPSDBHelper.getInstance(getApplicationContext()).insertFpsStockDataAdmin(entryListStock.getOpeningStockList());
                openStackDialog = new OpenStackDialog(com.omneagate.activity.OpenstockActivityEntry.this);
                openStackDialog.show();
            } else {
                onClickUser();
                String messages = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(base.getStatusCode()));
                Util.messageBar(this, messages);
            }
        } catch (Exception e) {
            onClickUser();
            if (progressBar != null) {
                progressBar.dismiss();
            }
            Log.e("stockEntry", e.toString(), e);
            Util.LoggingQueue(this, "Stock entry to server", "Error:" + e.getStackTrace().toString());

        }


    }

    public void severSendStockEntry() {
        networkConnection = new NetworkConnection(this);
        if (networkConnection.isNetworkAvailable()) {
            onClickNull();
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.OpenstockActivityEntry.this);
                progressBar.setCancelable(false);
                progressBar.show();
                String url = "/fpsStock/openingstock";
                String jsonRequest = new Gson().toJson(entryListStock);
                Log.e("submit openstock...","values..."+jsonRequest);
                StringEntity se = new StringEntity(jsonRequest, HTTP.UTF_8);
                Log.e("entryList", jsonRequest);
                httpConnection.sendRequest(url, null, ServiceListenerType.OPEN_STOCK,
                        SyncHandler, RequestType.POST, se, com.omneagate.activity.OpenstockActivityEntry.this);
            } catch (Exception e) {
                Log.e("error excep", e.toString(), e);
            }
        } else {
            Util.messageBar(this, getString(R.string.noNetworkConnection));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }

        try {
            if ((openStackDialog != null) && openStackDialog.isShowing()) {
                openStackDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            openStackDialog = null;
        }
    }

}