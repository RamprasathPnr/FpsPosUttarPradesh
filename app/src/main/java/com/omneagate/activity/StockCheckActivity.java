package com.omneagate.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockHistoryDto;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.DTO.UserDto.StockCheckDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class StockCheckActivity extends BaseActivity {

    String TAG = "StockCheckActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_stockcommodity);
        configureData();

    }


    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            setUpPopUpPage();
            Util.LoggingQueue(this, "Stock Status activity", "Main page Called");
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.stock_status_tv);
            Util.setTamilText((TextView) findViewById(R.id.commodity), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.unit), R.string.unit);
            Util.setTamilText((TextView) findViewById(R.id.opening_stock), R.string.opening_stock);
            Util.setTamilText((TextView) findViewById(R.id.inward_qty), R.string.inward_qty);
            Util.setTamilText((TextView) findViewById(R.id.sale_qty), R.string.sale_qty);
            Util.setTamilText((TextView) findViewById(R.id.stock_adjustment), R.string.stock_adjust);
            Util.setTamilText((TextView) findViewById(R.id.current_stock), R.string.current_stock);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.close);
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(com.omneagate.activity.StockCheckActivity.this, StockManagementActivity.class));
                    finish();
                }
            });
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            List<StockCheckDto> productDtoList = FPSDBHelper.getInstance(this).getAllProductStockDetails();
            transactionLayout.removeAllViews();
            for (StockCheckDto products : productDtoList) {
                LayoutInflater lin = LayoutInflater.from(this);
                transactionLayout.addView(returnView(lin, products));
            }

        } catch (Exception e) {
            Log.e("StockCheckActivity", e.toString(), e);
        }
    }


    private Double getProductInward(long productId) {
        Log.e(TAG, "getProductInward calling...");
//        BillItemDto productInwardToday = FPSDBHelper.getInstance(this).getAllInwardListToday(productId);
        BillItemDto productInwardToday = FPSDBHelper.getInstance(this).getAllInwardListTodayTwo(productId);
        return productInwardToday.getQuantity();
    }

    /**
     * User entitlement view
     */
    private View returnView(LayoutInflater entitle, StockCheckDto fpsStock) {
        View convertView = entitle.inflate(R.layout.adapter_commodity_stock, null);
        TextView productNameTv = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView unitTv = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView openingStockTv = (TextView) convertView.findViewById(R.id.entitlement_opening);
        TextView adjustmentStock = (TextView) convertView.findViewById(R.id.entitlement_adjustment);
        TextView saleQuantityTv = (TextView) convertView.findViewById(R.id.entitlement_sale);
        TextView currentStockTv = (TextView) convertView.findViewById(R.id.amount_current);
        TextView inwardQuantityTv = (TextView) convertView.findViewById(R.id.entitlement_inward_quantity);

        /*NumberFormat format = new DecimalFormat("#0.000");
        format.setRoundingMode(RoundingMode.CEILING);*/
        FPSStockHistoryDto fpsStockHistory = FPSDBHelper.getInstance(this).getAllProductStockHistoryDetails(fpsStock.getProductId());
        List<POSStockAdjustmentDto> fpsStockAdjustment = FPSDBHelper.getInstance(this).getStockAdjustment(fpsStock.getProductId());
        double adjustment = 0.0;
        if (fpsStockAdjustment.size() == 0) {
//            adjustment = Util.quantityRoundOffFormat(adjustment);
            adjustmentStock.setText(Util.quantityRoundOffFormat(adjustment));
        } else {
            adjustment = getAdjustedValue(fpsStockAdjustment);
//            adjustment = Util.quantityRoundOffFormat(adjustment);
            adjustmentStock.setText(Util.quantityRoundOffFormat(adjustment));
            if(adjustment<0){
                adjustmentStock.setTextColor(Color.RED);
            }
        }
        productNameTv.setText(fpsStock.getName());
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(fpsStock.getLocalName())) {
            productNameTv.setText(unicodeToLocalLanguage(fpsStock.getLocalName()));
        }
        unitTv.setText(fpsStock.getUnit());
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(fpsStock.getLocalUnit())) {
            unitTv.setText(unicodeToLocalLanguage(fpsStock.getLocalUnit()));
        }
        String qty1 = Util.quantityRoundOffFormat(fpsStockHistory.getCurrQuantity());
        openingStockTv.setText(qty1);

        String qty2 = Util.quantityRoundOffFormat(fpsStock.getQuantity());
        currentStockTv.setText(qty2);

        String qty3 = Util.quantityRoundOffFormat(fpsStock.getSold());
        saleQuantityTv.setText(qty3);

        double inward = getProductInward(fpsStock.getProductId());
//        inward = Util.quantityRoundOffFormat(inward);
        inwardQuantityTv.setText(Util.quantityRoundOffFormat(inward));
        if (fpsStock.getUnit().equalsIgnoreCase("kg")) {
            if (fpsStockHistory.getCurrQuantity() > 10000.0) {
                double openings = fpsStockHistory.getCurrQuantity() / 1000;
//                openings = Util.quantityRoundOffFormat(openings);
                openingStockTv.setText(Util.quantityRoundOffFormat(openings));
                Util.setTamilText(unitTv, getString(R.string.tons));

                double closings = fpsStock.getQuantity() / 1000;
//                closings = Util.quantityRoundOffFormat(closings);
                currentStockTv.setText(Util.quantityRoundOffFormat(closings));

                double sold = fpsStock.getSold() / 1000;
//                sold = Util.quantityRoundOffFormat(sold);
                saleQuantityTv.setText(Util.quantityRoundOffFormat(sold));

                double inwards = inward / 1000;
//                inwards = Util.quantityRoundOffFormat(inwards);
                inwardQuantityTv.setText(Util.quantityRoundOffFormat(inwards));


                double adjusted = adjustment / 1000;
//                adjusted = Util.quantityRoundOffFormat(adjusted);
                adjustmentStock.setText(Util.quantityRoundOffFormat(adjusted));
                if(adjustment<0){
                    adjustmentStock.setTextColor(Color.RED);
                }
            }
        }

        return convertView;

    }

    private double getAdjustedValue(List<POSStockAdjustmentDto> adjustment) {
        double quantity = 0.0;
        for (POSStockAdjustmentDto productValue : adjustment) {
            double productQuantity = productValue.getQuantity();
            if (productValue.getRequestType().equalsIgnoreCase("STOCK_DECREMENT")) {
                productQuantity = -1 * productQuantity;
            }
            quantity = quantity + productQuantity;
        }
        return quantity;
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StockManagementActivity.class));
        Util.LoggingQueue(this, "Stock Status activity", "Back pressed Called");
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
    }
}
