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
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.DTO.UserDto.StockCheckDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockCheckSummaryActivity extends BaseActivity {

    String date1, date2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_stockcommodity_summary);


        Util.LoggingQueue(StockCheckSummaryActivity.this, "StockCheckSummaryActivity", "onCreate() called");


        date1 = getIntent().getExtras().getString("fromDate");
        date2 = getIntent().getExtras().getString("toDate");
        configureData();

    }


    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            setUpPopUpPage();
           // Util.LoggingQueue(this, "Stock Status summary activity", "Main page Called");
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.monthly_inventory_summary);
            Util.setTamilText((TextView) findViewById(R.id.commodity), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.unit), R.string.unit);
            Util.setTamilText((TextView) findViewById(R.id.opening_stock), R.string.opening_stock);
            Util.setTamilText((TextView) findViewById(R.id.inward_qty), R.string.inward_qty);
            Util.setTamilText((TextView) findViewById(R.id.sale_qty), R.string.sale_qty);
            Util.setTamilText((TextView) findViewById(R.id.stock_adjustment), R.string.stock_adjust);
            Util.setTamilText((TextView) findViewById(R.id.current_stock), R.string.closingBalance);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.close);
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
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
            List<StockCheckDto> productDtoList = FPSDBHelper.getInstance(this).getAllProductStockDetailsTwo(date1, date2);
            transactionLayout.removeAllViews();
            for (StockCheckDto products : productDtoList) {
                LayoutInflater lin = LayoutInflater.from(this);
                transactionLayout.addView(returnView(lin, products));
            }

            TextView summaryDates = (TextView) findViewById(R.id.summaryDatesTv);
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");

            Date d1 = originalFormat.parse(date1);
            String fromDate = targetFormat.format(d1);

            Date d2 = originalFormat.parse(date2);
            String toDate = targetFormat.format(d2);
            summaryDates.setText(fromDate+" <-> "+toDate);

        } catch (Exception e) {
            Log.e("StockCheckSummary", e.toString(), e);
        }
    }



    /**
     * User entitlement view
     */
    private View returnView(LayoutInflater entitle, StockCheckDto fpsStock) {
        Util.LoggingQueue(StockCheckSummaryActivity.this, "StockCheckSummaryActivity", "returnView() StockCheckDto "+fpsStock);

        View convertView = entitle.inflate(R.layout.adapter_commodity_stock, null);
        TextView productNameTv = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView unitTv = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView openingStockTv = (TextView) convertView.findViewById(R.id.entitlement_opening);
        TextView adjustmentStock = (TextView) convertView.findViewById(R.id.entitlement_adjustment);
        TextView saleQuantityTv = (TextView) convertView.findViewById(R.id.entitlement_sale);
        TextView currentStockTv = (TextView) convertView.findViewById(R.id.amount_current);
        TextView inwardQuantityTv = (TextView) convertView.findViewById(R.id.entitlement_inward_quantity);

        List<POSStockAdjustmentDto> fpsStockAdjustment = FPSDBHelper.getInstance(this).getSelectedDateStockAdjustment(fpsStock.getProductId(), date1, date2);
        double adjustment = 0.0;
        if (fpsStockAdjustment.size() == 0) {
            adjustmentStock.setText(Util.quantityRoundOffFormat(adjustment));
        } else {
            adjustment = getAdjustedValue(fpsStockAdjustment);
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

        String qty3 = Util.quantityRoundOffFormat(fpsStock.getSold());
        saleQuantityTv.setText(qty3);

        double inward = getProductInwardTwo(fpsStock.getProductId(), date1, date2);
        inwardQuantityTv.setText(Util.quantityRoundOffFormat(inward));

        // Calculating opening stock for from date
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = df.format(new Date());
        String sourceDate = date1;
        double openingStock = 0.0;
        List<StockCheckDto> productDtoList = FPSDBHelper.getInstance(this).getAllProductStockDetailsTwo(sourceDate, currentDate);
        for (StockCheckDto products : productDtoList) {
            if(fpsStock.getProductId() == products.getProductId()) {
                // Inward
                double inward2 = getProductInwardTwo(fpsStock.getProductId(), sourceDate, currentDate);
                // Adjustment
                List<POSStockAdjustmentDto> fpsStockAdjustment2 = FPSDBHelper.getInstance(this).getSelectedDateStockAdjustment(fpsStock.getProductId(), sourceDate, currentDate);
                double adjustment2 = 0.0;
                if (fpsStockAdjustment2.size() == 0) {
                } else {
                    adjustment2 = getAdjustedValue(fpsStockAdjustment2);
                }
                // Sold
                double sold2 = products.getSold();
                adjustment2 = -(adjustment2);
                openingStock = (fpsStock.getQuantity() - inward2 + adjustment2 + sold2);
            }
        }
        String qty4 = Util.quantityRoundOffFormat(openingStock);
        openingStockTv.setText(qty4);

        // Calculating closing stock for to date
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate2 = df2.format(new Date());
        String sourceDate2 = "";
        try {
            Date dt = df.parse(date2);
            DateTime dtOrg = new DateTime(dt);
            DateTime dtPlusOne = dtOrg.plusDays(1);
            Date dt2 = dtPlusOne.toDate();
            sourceDate2 = df.format(dt2);
        }
        catch(Exception e) {}
        double closingStock = 0.0;
        List<StockCheckDto> productDtoList2 = FPSDBHelper.getInstance(this).getAllProductStockDetailsTwo(sourceDate2, currentDate2);
        for (StockCheckDto products : productDtoList2) {
            if(fpsStock.getProductId() == products.getProductId()) {
                // Inward
                double inward2 = getProductInwardTwo(fpsStock.getProductId(), sourceDate2, currentDate2);
                // Adjustment
                List<POSStockAdjustmentDto> fpsStockAdjustment2 = FPSDBHelper.getInstance(this).getSelectedDateStockAdjustment(fpsStock.getProductId(), sourceDate2, currentDate2);
                double adjustment2 = 0.0;
                if (fpsStockAdjustment2.size() == 0) {
                } else {
                    adjustment2 = getAdjustedValue(fpsStockAdjustment2);
                }
                // Sold
                double sold2 = products.getSold();
                adjustment2 = -(adjustment2);
                closingStock = (fpsStock.getQuantity() - inward2 + adjustment2 + sold2);
            }
        }
        String qty5 = Util.quantityRoundOffFormat(closingStock);
        currentStockTv.setText(qty5);


        if (fpsStock.getUnit().equalsIgnoreCase("kg")) {
            if (openingStock > 10000.0) {
                double openings = openingStock / 1000;
                openingStockTv.setText(Util.quantityRoundOffFormat(openings));
                Util.setTamilText(unitTv, getString(R.string.tons));

                double closings = closingStock / 1000;
                currentStockTv.setText(Util.quantityRoundOffFormat(closings));

                double sold = fpsStock.getSold() / 1000;
                saleQuantityTv.setText(Util.quantityRoundOffFormat(sold));

                double inwards = inward / 1000;
                inwardQuantityTv.setText(Util.quantityRoundOffFormat(inwards));

                double adjusted = adjustment / 1000;
                adjustmentStock.setText(Util.quantityRoundOffFormat(adjusted));
                if(adjustment<0){
                    adjustmentStock.setTextColor(Color.RED);
                }
            }
        }

        return convertView;
    }

    private Double getProductInwardTwo(long productId, String fromDate, String toDate) {
        BillItemDto productInwardToday = FPSDBHelper.getInstance(this).getSelectedDateInwardList(productId, fromDate, toDate);
        return productInwardToday.getQuantity();
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
        startActivity(new Intent(this, MonthlyInventoryReportActivity.class));
        Util.LoggingQueue(this, "Stock Status summary activity", "Back pressed Called");
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
