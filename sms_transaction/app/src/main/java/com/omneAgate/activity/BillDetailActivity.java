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

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.printer.PrintBillData;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by user1 on 27/3/15.
 */
public class BillDetailActivity extends BaseActivity {


    // Ufc
    TextView tvUfc;

    //Bill Date
    TextView tvBillDate;

    //Address
    TextView tvAddress;

    //Head of the Family
    TextView tvHeadOfTheFamily;

    //Total amount
    TextView tvTotalAmount;

    //Total ledger amount
    TextView tvBillDetailTotal;

    List<ProductDto> products;

    long billId;

    double totalCost = 0.0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bill_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();

        tvUfc = (TextView) findViewById(R.id.tvBillDetailUfc);
        tvBillDate = (TextView) findViewById(R.id.tvBillDetailDate);
        tvAddress = (TextView) findViewById(R.id.tvBillDetailAddress);
        tvHeadOfTheFamily = (TextView) findViewById(R.id.tvBillDetailHeadOfTheFamily);
        tvTotalAmount = (TextView) findViewById(R.id.tvBillDetailAmount);
        tvBillDetailTotal = (TextView) findViewById(R.id.tvBillDetailTotal);
        billId = getIntent().getExtras().getLong("billId");
        Util.setTamilText((TextView) findViewById(R.id.summarySubmit), getString(R.string.sales));
        Util.setTamilText((TextView) findViewById(R.id.summaryEdit), getString(R.string.printBill));
        setBeneficiaryDetails();
    }


    private void setBeneficiaryDetails() {
        BillDto bills = FPSDBHelper.getInstance(this).getBill(billId);
        BeneficiaryDto beneficiaryDto = FPSDBHelper.getInstance(this).retrieveBeneficiary(bills.getBeneficiaryId());
/*        if (beneficiaryDto.getFamilyMembers().size() > 0)
            tvAddress.setText(getAddress(beneficiaryDto.getFamilyMembers().get(0)));
        String head = "";
        if (GlobalAppState.language.equals("ta")) {
            head = beneficiaryDto.getFamilyMembers().get(0).getLname();
        } else {
            head = beneficiaryDto.getFamilyMembers().get(0).getName();
        }
        tvHeadOfTheFamily.setText(head);*/
        /*Util.setTamilText(tvHeadOfTheFamily, head);*/
        String ufc = Util.DecryptedBeneficiary(this, beneficiaryDto.getEncryptedUfc());
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
        final UpdateStockRequestDto updateStockRequestDto = new UpdateStockRequestDto();
        updateStockRequestDto.setBillDto(bills);
        SharedPreferences pref = getSharedPreferences("FPSPOS", Context.MODE_PRIVATE);
        String myPairedDevice = pref.getString("printer", "");
        if (myPairedDevice.length() > 0) {
            ((Button) findViewById(R.id.summaryEdit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrintBillData print = new PrintBillData(BillDetailActivity.this, updateStockRequestDto);
                    print.printBill();
                }
            });
        } else {
            ((Button) findViewById(R.id.summaryEdit)).setVisibility(View.GONE);
        }
        ((Button) findViewById(R.id.summarySubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BillDetailActivity.this, BillActivity.class));
                finish();
            }
        });
        ((TextView) findViewById(R.id.tvBillDetailProductPriceLabel)).setText(getString(R.string.billDetailProductPrice) + "(" + getString(R.string.rs) + ")");

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * returns the address of beneficiary
     *
     * @params response received from server or from local database
     */
    private String getAddress(BeneficiaryMemberDto head) {
        String address = "";
        String address1, address2, address3, address4, address5;
        if (GlobalAppState.language.equals("ta")) {
            address1 = head.getLaddressLine1();
            address2 = head.getLaddressLine2();
            address3 = head.getLaddressLine3();
            address4 = head.getLaddressLine4();
            address5 = head.getLaddressLine5();
        } else {
            address1 = head.getAddressLine1();
            address2 = head.getAddressLine2();
            address3 = head.getAddressLine3();
            address4 = head.getAddressLine4();
            address5 = head.getAddressLine5();
        }
        if (StringUtils.isNotEmpty(address1)) {
            address = address1;
        }
        if (StringUtils.isNotEmpty(address2)) {
            address = address + " , " + address2;
        }

        if (StringUtils.isNotEmpty(address3)) {
            address = address + " , " + address3;
        }

        if (StringUtils.isNotEmpty(address4)) {
            address = address + " , " + address4;
        }

        if (StringUtils.isNotEmpty(address5)) {
            address = address + " , " + address5;
        }
        return address;
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, BillActivity.class));
        finish();
    }
}
