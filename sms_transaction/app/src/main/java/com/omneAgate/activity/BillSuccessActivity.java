package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.printer.PrintBillData;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillSuccessActivity extends BaseActivity {

    UpdateStockRequestDto updateStockRequestDto;


    List<ProductDto> products;

    double totalCost = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_bill_success);
        appState = (GlobalAppState) getApplication();
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        String message = getIntent().getStringExtra("message");
        updateStockRequestDto = new Gson().fromJson(message, UpdateStockRequestDto.class);
        httpConnection = new HttpClientWrapper();
        Util.setTamilText((TextView) findViewById(R.id.summarySubmit), getString(R.string.sales));
        Util.setTamilText((TextView) findViewById(R.id.summaryEdit), getString(R.string.printBill));
        submitBills();
        SharedPreferences pref = getSharedPreferences("FPSPOS", Context.MODE_PRIVATE);
        String myPairedDevice = pref.getString("printer", "");
        ((TextView) findViewById(R.id.tvBillDetailProductPriceLabel)).setText(getString(R.string.billDetailProductPrice) + "(" + getString(R.string.rs) + ")");
        if (myPairedDevice.length() > 0) {
            ((Button) findViewById(R.id.summaryEdit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrintBillData print = new PrintBillData(BillSuccessActivity.this, updateStockRequestDto);
                    print.printBill();
                }
            });
        } else {
            ((Button) findViewById(R.id.summaryEdit)).setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.summarySubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BillSuccessActivity.this, SaleActivity.class));
                finish();
            }
        });
    }

    private void submitBills() {
        TextView tvUfc = (TextView) findViewById(R.id.tvBillDetailUfc);
        TextView tvBillDate = (TextView) findViewById(R.id.tvBillDetailDate);
        TextView tvAddress = (TextView) findViewById(R.id.tvBillDetailAddress);
        TextView tvHeadOfTheFamily = (TextView) findViewById(R.id.tvBillDetailHeadOfTheFamily);
        TextView tvTotalAmount = (TextView) findViewById(R.id.tvBillDetailAmount);

        BillDto bills = updateStockRequestDto.getBillDto();
        BeneficiaryDto beneficiaryDto = FPSDBHelper.getInstance(this).retrieveBeneficiary(bills.getBeneficiaryId());
      /* Util.setTamilText(tvAddress, getAddress(beneficiaryDto.getFamilyMembers().get(0)));*/
        String head = "";
       /* if (GlobalAppState.language.equals("ta")) {
            head = beneficiaryDto.getFamilyMembers().get(0).getLname();
        } else {
            head = beneficiaryDto.getFamilyMembers().get(0).getName();
        }*/
        tvHeadOfTheFamily.setText(head);
        /*Util.setTamilText(tvHeadOfTheFamily, head);*/
        String ufc = beneficiaryDto.getOldRationNumber();//Util.DecryptedBeneficiary(this, beneficiaryDto.getEncryptedUfc());
        tvBillDate.setText(ufc);
        tvUfc.setText(bills.getTransactionId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(bills.getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault());
        tvTotalAmount.setText(dateFormat.format(convertedDate));
        configureData(bills);
    }

    /*Data from server has been set inside this function*/
    private void configureData(BillDto bills) {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_bill_detail);
            fpsInwardLinearLayout.removeAllViews();
            products = FPSDBHelper.getInstance(this).getAllProductDetails();
            List<BillItemDto> billItems = new ArrayList<>(bills.getBillItemDto());
            for (int position = 0; position < billItems.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, billItems.get(position), position));
            }
            TextView tvBillDetailTotal = (TextView) findViewById(R.id.tvBillDetailTotal);
            NumberFormat formatter = new DecimalFormat("#0.00");
            tvBillDetailTotal.setText(formatter.format(totalCost));
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }
    }


    /*User Bill Detail view*/
    private View returnView(LayoutInflater entitle, BillItemDto data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_bill_detail_activity, null);

        TextView serialNo = (TextView) convertView.findViewById(R.id.tvBillDetailSerailNo);
        TextView productName = (TextView) convertView.findViewById(R.id.tvBillDetailProductName);
        TextView quantity = (TextView) convertView.findViewById(R.id.tvBillDetailQuantity);
        TextView price = (TextView) convertView.findViewById(R.id.tvBillDetailPrice);

        serialNo.setText("" + (position + 1));
        Log.e("ID", "" + data.getProductId());
        ProductDto product = getProduct(data.getProductId());
        NumberFormat formatter = new DecimalFormat("#0.000");
        String unit = product.getProductUnit();
        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(product.getLocalProductUnit())) {
            unit = product.getLocalProductUnit();
        }
        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(product.getLocalProductName()))
            Util.setTamilText(productName, product.getLocalProductName());
        else
            productName.setText(product.getName());

        if (data.getQuantity() != null)
            quantity.setText(formatter.format(data.getQuantity()) + " (" + unit + ")");
        else
            quantity.setText("0.000" + " (" + unit + ")");
        formatter = new DecimalFormat("#0.00");
        double amountPerItem = data.getCost() * data.getQuantity();
        totalCost = totalCost + amountPerItem;
        price.setText(formatter.format(amountPerItem));
        return convertView;

    }

    private ProductDto getProduct(long productId) {
        for (ProductDto productDto : products) {
            if (productDto.getId() == productId) {
                return productDto;
            }
        }
        return new ProductDto();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void printUserBill() {
        PrintBillData print = new PrintBillData(this, updateStockRequestDto);
        print.printBill();

    }


}
