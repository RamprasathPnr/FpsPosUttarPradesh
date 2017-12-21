package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.Util.AddressForBeneficiary;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.printer.PrintBillData;

import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * Created for unsync bills
 */
public class UnsyncBillDetailActivity extends BaseActivity {

    double totalCost = 0.0;

    BillDto billDto;

    UpdateStockRequestDto updateStockRequestDto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_bill_success);
        appState = (com.omneagate.activity.GlobalAppState) getApplication();
        String message = getIntent().getExtras().getString("billData");
        billDto = new Gson().fromJson(message, BillDto.class);
        Util.setTamilText((TextView) findViewById(R.id.summarySubmit), getString(R.string.sales));
        Util.setTamilText((TextView) findViewById(R.id.summaryEdit), getString(R.string.printBill));
        updateStockRequestDto = new UpdateStockRequestDto();
        findViewById(R.id.printing).setVisibility(View.INVISIBLE);
        setUpInitialPage();
    }


    /*
*
* Initial Setup
*
* */
    private void setUpInitialPage() {
        setUpPopUpPage();
        Util.LoggingQueue(this, "Bill search Details", "Setting up main page");
        appState = (com.omneagate.activity.GlobalAppState) getApplication();
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.bill_summary);
        Util.setTamilText((TextView) findViewById(R.id.commoditys), R.string.commodity);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDetailUfcLabel), R.string.billDetailTxnBill);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDetailAmountLabel), R.string.billDetailAmountLabel);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDateLabel), R.string.ration_card_number1);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDetailHeadOfTheFamilyLabel), R.string.billDetailFamilyHeadOfTheFamilyLabel);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDetailAddressLabel), R.string.billDetailAddressLabel);
        Util.setTamilText((TextView) findViewById(R.id.billDetailQuantity), R.string.billDetailQuantity);
        Util.setTamilText((TextView) findViewById(R.id.billDetailProductPrice), R.string.billDetailProductPrice);
        String myPairedDevice = FPSDBHelper.getInstance(this).getMasterData("printer");

        if (myPairedDevice != null) {
            findViewById(R.id.summaryEdit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.LoggingQueue(com.omneagate.activity.UnsyncBillDetailActivity.this, "Bill search Details", "Printer called");
                    PrintBillData print = new PrintBillData(com.omneagate.activity.UnsyncBillDetailActivity.this, updateStockRequestDto);
                    print.printBill();
                }
            });
        } else {
            findViewById(R.id.summaryEdit).setVisibility(View.INVISIBLE);
        }

        findViewById(R.id.summarySubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.LoggingQueue(com.omneagate.activity.UnsyncBillDetailActivity.this, "Bill search Details", "Continue button called");
                onBackPressed();
            }
        });
        submitBills();
    }

    private void submitBills() {
        try {
            TextView tvUfc = (TextView) findViewById(R.id.tvBillDetailUfc);
            TextView tvTotalAmount = (TextView) findViewById(R.id.tvBillDetailAmount);
            tvUfc.setText(billDto.getTransactionId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.getDefault());
            Date convertedDate = dateFormat.parse(billDto.getBillDate());
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            tvTotalAmount.setText(dateFormat.format(convertedDate));
            configureData(billDto);
            new SearchBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, billDto.getBeneficiaryId());
            Util.LoggingQueue(com.omneagate.activity.UnsyncBillDetailActivity.this, "Bill search Details", "Bills:" + billDto.toString());
        } catch (Exception e) {
            Util.LoggingQueue(this, "Bill search Details", "Error:" + e.toString());
            Log.e("Error",e.toString(),e);
        } finally {
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    Util.LoggingQueue(com.omneagate.activity.UnsyncBillDetailActivity.this, "Bill search Details", "Back pressed Calling");
                }
            });
        }
    }

    private String headOfFamily(Set<BeneficiaryMemberDto> beneficiaryMember) {
        String head = "";
        for (BeneficiaryMemberDto benef : beneficiaryMember) {
            if (benef.getRelName().equalsIgnoreCase("Family Head")) {
                if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(benef.getLocalName())) {
                    return benef.getLocalName();
                } else if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("en") && StringUtils.isNotEmpty(benef.getName())) {
                    return benef.getName();
                }
            }
        }
        return head;

    }


    /*Data from server has been set inside this function*/
    private void configureData(BillDto bills) {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_bill_detail);
            fpsInwardLinearLayout.removeAllViews();
            List<BillItemProductDto> billItems = FPSDBHelper.getInstance(this).getAllBillItems(bills.getTransactionId());
            Util.LoggingQueue(this, "Bill search Details", "Bill Items:" + billItems.toString());
            for (BillItemProductDto items : billItems) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, items));
            }
            TextView tvBillDetailTotal = (TextView) findViewById(R.id.tvBillDetailTotal);
            /*NumberFormat formatter = new DecimalFormat("#0.00");
            formatter.setRoundingMode(RoundingMode.CEILING);*/
//            totalCost = Util.priceRoundOffFormat(totalCost);
            tvBillDetailTotal.setText("\u20B9 " + Util.priceRoundOffFormat(totalCost));
            Util.setTamilText(tvBillDetailTotal, getString(R.string.total) + ":\u20B9 " + Util.priceRoundOffFormat(totalCost));
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("BillDetailActivity", e.toString(), e);
        }
    }


    /*User Bill Detail view*/
    private View returnView(LayoutInflater entitle, BillItemProductDto data) {
        View convertView = entitle.inflate(R.layout.adapter_bill_detail_activity, null);
        TextView entitlementName = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView entitlementPurchased = (TextView) convertView.findViewById(R.id.entitlementPurchased);
        TextView entitlementUnit = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView amountOfSelection = (TextView) convertView.findViewById(R.id.amountOfSelection);

        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        String unit = data.getProductUnit();
        entitlementUnit.setText(unit);
        if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(data.getLocalProductUnit())) {
            unit = data.getLocalProductUnit();
            Util.setTamilText(entitlementUnit, unit);
        }
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(data.getLocalProductName()))
//            Util.setTamilText(entitlementName, data.getLocalProductName());
            entitlementName.setText(data.getLocalProductName());
        else
            entitlementName.setText(data.getProductName());
        if (data.getQuantity() != null) {
            String qty1 = Util.quantityRoundOffFormat(data.getQuantity());
            entitlementPurchased.setText(qty1);
        }
        else {
            entitlementPurchased.setText("0.000");
        }
        /*formatter = new DecimalFormat("#0.00");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        double amountPerItem = data.getCost() * data.getQuantity();
        totalCost = totalCost + amountPerItem;
//        amountPerItem = Util.priceRoundOffFormat(amountPerItem);
        amountOfSelection.setText(Util.priceRoundOffFormat(amountPerItem));
        return convertView;

    }



    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, UnsyncBillActivity.class);
        startActivity(intent);
        finish();
    }

    private class SearchBillTask extends AsyncTask<Long, Void, BeneficiaryDto> {

        @Override
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.UnsyncBillDetailActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Progress bar", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected BeneficiaryDto doInBackground(final Long... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.UnsyncBillDetailActivity.this).retrieveBeneficiary(args[0]);
        }

        // can use UI thread here
        protected void onPostExecute(final BeneficiaryDto beneficiaryDto) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
            catch(Exception e) {}

            TextView tvBillDate = (TextView) findViewById(R.id.tvBillDetailDate);
            TextView tvAddress = (TextView) findViewById(R.id.tvBillDetailAddress);
            TextView tvHeadOfTheFamily = (TextView) findViewById(R.id.tvBillDetailHeadOfTheFamily);
            String aReg = "";
            if(StringUtils.isNotEmpty(beneficiaryDto.getAregisterNum())){
                aReg = " / "+beneficiaryDto.getAregisterNum();
            }
            tvBillDate.setText(beneficiaryDto.getOldRationNumber()+aReg);
            Set<BeneficiaryMemberDto> beneficiaryMembers = beneficiaryDto.getBenefMembersDto();
            List<BeneficiaryMemberDto> beneficiaryMember = new ArrayList<>(beneficiaryMembers);
            if (beneficiaryMember.size() > 0) {
                Util.setTamilText(tvAddress,  AddressForBeneficiary.addressForBeneficiary(beneficiaryMember.get(0)));
                Util.setTamilText(tvHeadOfTheFamily, headOfFamily(beneficiaryMembers));
            }
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
    }

}
