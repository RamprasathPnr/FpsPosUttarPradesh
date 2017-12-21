package com.omneagate.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionCommodityActivity extends BaseActivity {

    private TextView mTvProduct;
    private TextView mTvOpenBalance;
    private TextView mTvSoldQuantity;
    private TextView mTvClosingBalance;
    private TextView mTvAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_commodity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        appState = (GlobalAppState) getApplication();
        actionBarCreation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        configureData();
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {

            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_transactionCommodity);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = df2.format(c.getTime());
            List<ProductDto> productDtoList = FPSDBHelper.getInstance(this).getAllProductDetails();
            List<BillItemDto> billItemsListToday = FPSDBHelper.getInstance(this).getAllBillItemsListToday(date);
            transactionLayout.removeAllViews();
            for (ProductDto products : productDtoList) {
                LayoutInflater lin = LayoutInflater.from(this);
                FPSStockDto fpsStock = FPSDBHelper.getInstance(this).getAllProductStockDetails(products.getId());
                BillItemDto billItems = getProductSellOrNot(billItemsListToday, products.getId());
                if (billItems != null) {
                    transactionLayout.addView(returnView(lin, products, fpsStock, billItems.getQuantity()));
                } else if (fpsStock.getQuantity() > 0) {
                    transactionLayout.addView(returnView(lin, products, fpsStock, 0.0));
                }

            }


        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        }
    }

    private BillItemDto getProductSellOrNot(List<BillItemDto> billItemsListToday, long productId) {
        for (BillItemDto billItems : billItemsListToday) {
            if (productId == billItems.getProductId()) {
                return billItems;
            }
        }

        return null;
    }

    //User Commodity Transaction Open Balance and Close Balance
    private View returnView(LayoutInflater entitle, ProductDto data, FPSStockDto fpsStock, double sale) {
        View convertView = entitle.inflate(R.layout.adapter_commodity_transaction, null);

        TextView product = (TextView) convertView.findViewById(R.id.tvProductName);
        TextView closingBalance = (TextView) convertView.findViewById(R.id.tvClosingBalance);
        TextView openingBalance = (TextView) convertView.findViewById(R.id.tvOpeningBalance);

        product.setText(data.getName());
        if (GlobalAppState.language.equals("ta")) {
            product.setText(data.getLocalProductName());
        }
        NumberFormat unitFormat = new DecimalFormat("#0.000");
        openingBalance.setText(unitFormat.format(fpsStock.getQuantity() + sale));
        closingBalance.setText(unitFormat.format(fpsStock.getQuantity()));

        return convertView;

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


}
