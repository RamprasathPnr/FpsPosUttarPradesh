package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EntitlementDTO;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.Util.BeneficiarySalesTransaction;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ActivationSuccessActivity extends BaseActivity {

    BenefActivNewDto benefData;
    String registeredMobileNoInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_card_activation_success);
        Util.LoggingQueue(this, "ActivationSuccessActivity ", "onCreate called");

        appState = (GlobalAppState) getApplication();
        String message = getIntent().getStringExtra("data");
        try {
            if (getIntent().getStringExtra("registeredMobileNoInfo") != null) {
                registeredMobileNoInfo = getIntent().getStringExtra("registeredMobileNoInfo");
                Util.LoggingQueue(this, "ActivationSuccessActivity ", "registeredMobileNoInfo ->" + registeredMobileNoInfo);

                if (registeredMobileNoInfo.equalsIgnoreCase("")){
                    // registeredMobileNoInfo = "";
                }else if (registeredMobileNoInfo.equalsIgnoreCase("exists")){
                    //registeredMobileNoInfo = "exists";
                    Toast.makeText(this, getString(R.string.already_saved_ph_no ), Toast.LENGTH_LONG).show();

                } else if (registeredMobileNoInfo.equalsIgnoreCase("success")){
                    // registeredMobileNoInfo = "success";
                }

            }else
                Util.LoggingQueue(this, "ActivationSuccessActivity ", "No registeredMobileNoInfo ->" + registeredMobileNoInfo);



        } catch (Exception e) {

        }


        benefData = new Gson().fromJson(message, BenefActivNewDto.class);
        Util.setTamilText((TextView) findViewById(R.id.summarySubmit), getString(R.string.sales));
        setUpInitialPage();


    }


    /*
*
* Initial Setup
*
* */
    private void setUpInitialPage() {
        try {
            appState = (GlobalAppState) getApplication();
            setUpPopUpPage();

            ((TextView) findViewById(R.id.tvBillDetailAmount)).setText(benefData.getRationCardNumber().toUpperCase());
            ((TextView) findViewById(R.id.tvBillDetailDate)).setText(benefData.getCardTypeDef().toUpperCase());
            Util.setTamilText((TextView) findViewById(R.id.textViewMessage), R.string.card_activation_success);
            Util.setTamilText((TextView) findViewById(R.id.commoditys), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.tvBillDetailAmountLabel), R.string.normal_ration_card_number);
            Util.setTamilText((TextView) findViewById(R.id.tvAregLabel), R.string.aRegisterNo);
            Util.setTamilText((TextView) findViewById(R.id.tvBillDateLabel), R.string.normal_cardCap);
            Util.setTamilText((TextView) findViewById(R.id.billDetailQuantity), R.string.billDetailQuantity);
            int childCount = benefData.getNumOfChild();
            int adultCount = benefData.getNumOfAdults();
            int cylinderCount = benefData.getNumOfCylinder();
            ((TextView) findViewById(R.id.childCount)).setText(String.valueOf(childCount));
            ((TextView) findViewById(R.id.adultCount)).setText(String.valueOf(adultCount));
            ((TextView) findViewById(R.id.cylinderCount)).setText(String.valueOf(cylinderCount));
            ((TextView) findViewById(R.id.tvAregNo)).setText(benefData.getAregisterNum());
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.card_activation);
            findViewById(R.id.imageViewBack).setVisibility(View.INVISIBLE);
            BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
            QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(benefData.getRationCardNumber());
            List<EntitlementDTO> entitled = qrCodeResponseReceived.getEntitlementList();
            findViewById(R.id.summarySubmit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Util.LoggingQueue(com.omneagate.activity.ActivationSuccessActivity.this, "ActivationSuccessActivity", "Continue button called");
                    startActivity(new Intent(com.omneagate.activity.ActivationSuccessActivity.this, CardActivationActivity.class));
                    finish();
                }
            });
            if (entitled != null) {
                findViewById(R.id.withComodity).setVisibility(View.VISIBLE);
                (findViewById(R.id.noComodity)).setVisibility(View.GONE);
                configureData(entitled);
            } else {

                (findViewById(R.id.withComodity)).setVisibility(View.GONE);
                (findViewById(R.id.noComodity)).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            (findViewById(R.id.withComodity)).setVisibility(View.GONE);
            (findViewById(R.id.noComodity)).setVisibility(View.VISIBLE);
        }

    }


    /*Data from server has been set inside this function*/
    private void configureData(List<EntitlementDTO> entitlements) {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_bill_detail);
            fpsInwardLinearLayout.removeAllViews();
            for (int position = 0; position < entitlements.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, entitlements.get(position), position));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "ActivationSuccessActivity Exception", e.toString());
        }
    }


    /*User Bill Detail view*/
    private View returnView(LayoutInflater entitle, EntitlementDTO data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_card_detail_activity, new LinearLayout(this), false);

        LinearLayout commodityBackground = (LinearLayout) convertView.findViewById(R.id.commodityBackground);
        int[] subColor = getResources().getIntArray(R.array.mainColor);
        commodityBackground.setBackgroundColor(subColor[position % 2]);
        TextView entitlementName = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView entitlementUnit = (TextView) convertView.findViewById(R.id.entitlementPurchased);
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/

        double amountPerItem = data.getEntitledQuantity();
//        amountPerItem = Util.quantityRoundOffFormat(amountPerItem);
        entitlementUnit.setText(Util.quantityRoundOffFormat(amountPerItem) + " " + data.getProductUnit());
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(data.getLproductUnit())) {
            Util.setTamilText(entitlementUnit, amountPerItem + " " + data.getLproductUnit());
        }
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(data.getLproductName()))
//            Util.setTamilText(entitlementName, data.getLproductName());
            entitlementName.setText(data.getLproductName());
        else
            entitlementName.setText(data.getProductName());

        return convertView;

    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


    @Override
    public void onBackPressed() {
        Util.LoggingQueue(this, "Sales Summary", "Back pressed");
        startActivity(new Intent(this, CardActivationActivity.class));
        finish();
    }
}
