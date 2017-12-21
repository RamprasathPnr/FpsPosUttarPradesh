package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Typeface;
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

public class OapAnpActivationSuccessActivity extends BaseActivity {

    BenefActivNewDto benefData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_oap_anp_activation_success);

        Util.LoggingQueue(OapAnpActivationSuccessActivity.this, "OapAnpActivationSuccessActivity", "onCreate() called  ");


        appState = (GlobalAppState) getApplication();
        String message = getIntent().getStringExtra("data");
        benefData = new Gson().fromJson(message, BenefActivNewDto.class);



        Util.LoggingQueue(OapAnpActivationSuccessActivity.this, "OapAnpActivationSuccessActivity", "onCreate() called benefData =   " +benefData);


        try{
            String phNumberStatus = getIntent().getStringExtra("phNumberStatus");

            if(phNumberStatus.equalsIgnoreCase("exists") ){
                Toast.makeText(this, getString(R.string.already_saved_ph_no ), Toast.LENGTH_LONG).show();

            }

        }catch (Exception e){
            Util.LoggingQueue(OapAnpActivationSuccessActivity.this, "OapAnpActivationSuccessActivity", "onCreate() Exception =   " +e);

        }

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

//            setOapAnpText((TextView) findViewById(R.id.top_textView), R.string.oap_anp_card_registration);
            ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.oap_anp_card_registration_confirm));
            if(GlobalAppState.language.equalsIgnoreCase("hi")) {
                ((TextView) findViewById(R.id.top_textView)).setTextSize(30);
                ((TextView) findViewById(R.id.top_textView)).setTypeface(Typeface.DEFAULT_BOLD);
            }
            ((TextView) findViewById(R.id.textViewMessage)).setText(R.string.oap_anp_card_activation_success);
            Util.setTamilText((TextView) findViewById(R.id.commoditys), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.billDetailQuantity), R.string.quantity);

            Util.setTamilText((TextView) findViewById(R.id.aRegisterNoLabel), R.string.aRegisterNo);
            Util.setTamilText((TextView) findViewById(R.id.rationCardNoLabel), R.string.ration_card_number);
            Util.setTamilText((TextView) findViewById(R.id.rationCardTypeLabel), R.string.cardCap);
            Util.setTamilText((TextView) findViewById(R.id.mobileNoLabel), R.string.mobile_no);
            /*int childCount = benefData.getNumOfChild();
            int adultCount = benefData.getNumOfAdults();
            int cylinderCount = benefData.getNumOfCylinder();
            ((TextView) findViewById(R.id.childCount)).setText(String.valueOf(childCount));
            ((TextView) findViewById(R.id.adultCount)).setText(String.valueOf(adultCount));
            ((TextView) findViewById(R.id.cylinderCount)).setText(String.valueOf(cylinderCount));*/
            ((TextView) findViewById(R.id.aRegNoValue)).setText(benefData.getAregisterNum());
            String rationCardNo = benefData.getRationCardNumber().toUpperCase();
            String numOne =  rationCardNo.substring(0, 2);
            String numTwo =  rationCardNo.substring(2,3);
            String numThree =  rationCardNo.substring(3);
            rationCardNo = numOne+"/"+numTwo+"/"+numThree;

            ((TextView) findViewById(R.id.rationCardNoValue)).setText(rationCardNo);
            ((TextView) findViewById(R.id.rationCardTypeValue)).setText(benefData.getCardTypeDef());
            ((TextView) findViewById(R.id.mobileNoValue)).setText(benefData.getMobileNum());


            findViewById(R.id.imageViewBack).setVisibility(View.INVISIBLE);
            BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
            Util.LoggingQueue(this, "Entitlement", "Calculating entitlement");
            QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(benefData.getRationCardNumber());
            List<EntitlementDTO> entitled = qrCodeResponseReceived.getEntitlementList();
            findViewById(R.id.summarySubmit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.LoggingQueue(com.omneagate.activity.OapAnpActivationSuccessActivity.this, "Sales Summary", "Continue button called");
                    startActivity(new Intent(com.omneagate.activity.OapAnpActivationSuccessActivity.this, CardActivationActivity.class));
                    finish();
                }
            });
            if (entitled != null) {
                Log.d("Success page....","yes entitlement");
                findViewById(R.id.withComodity).setVisibility(View.VISIBLE);
                (findViewById(R.id.noComodity)).setVisibility(View.GONE);
                configureData(entitled);
            } else {
                Log.d("Success page....","no entitlement");
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
            Log.d("Success page....", "size entitlement"+entitlements.size());
            for (int position = 0; position < entitlements.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, entitlements.get(position), position));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
//            Log.e("ActivationSuccess", e.toString(), e);
        }
    }


    /*User Bill Detail view*/
    private View returnView(LayoutInflater entitle, EntitlementDTO data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_card_detail_activity, new LinearLayout(this),false);

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
            Util.setTamilText(entitlementUnit, String.valueOf(amountPerItem) + " " + data.getLproductUnit());
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


        Util.LoggingQueue(OapAnpActivationSuccessActivity.this, "OapAnpActivationSuccessActivity", "onBackPressed() called  moving to CardActivationActivity");

        startActivity(new Intent(this, CardActivationActivity.class));
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
