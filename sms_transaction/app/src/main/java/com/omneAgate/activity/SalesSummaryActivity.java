package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EntitlementDTO;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.TransactionController.Transaction;
import com.omneagate.TransactionController.TransactionFactory;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.service.BillUpdateToServer;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

//FPS user can view the summary of selection
public class SalesSummaryActivity extends BaseActivity {

    //Summary of selected product
    private List<EntitlementDTO> entitlementSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sales_submission);
        appState = (GlobalAppState) getApplication();
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        Util.setTamilText((TextView) findViewById(R.id.entitlementName), R.string.product);
        String rate = getString(R.string.price) + "(" + getString(R.string.rs) + ")";
        Util.setTamilText((TextView) findViewById(R.id.priceLabel), rate);
        Util.setTamilText((TextView) findViewById(R.id.entitlementQty), R.string.availed);
        Util.setTamilText((TextView) findViewById(R.id.entitlementAmt), R.string.total);
        Util.setTamilText((TextView) findViewById(R.id.textView2), R.string.totalAmount);
        Util.setTamilText((TextView) findViewById(R.id.summarySubmit), R.string.submit);
        Util.setTamilText((TextView) findViewById(R.id.summaryEdit), R.string.edit);
        Util.setTamilText((TextView) findViewById(R.id.textView), R.string.numberOfProducts);
    }


    @Override
    protected void onStart() {
        super.onStart();
        showSelectionSummary();

    }

    //Orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.messageBar(this, GlobalAppState.language);
        super.onSaveInstanceState(outState);
    }

    /**
     * Entitlement selection summary from the fps user
     * on click listener for buttons submit and edit
     */
    private void showSelectionSummary() {
        try {
            entitlementSelected = new ArrayList<EntitlementDTO>();
            for (EntitlementDTO entitleSelection : EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList()) {
                if (entitleSelection.getBought() > 0) {
                    entitlementSelected.add(entitleSelection);
                }
            }
            LinearLayout entitlementSummaryList = (LinearLayout) findViewById(R.id.listView_summary);
            entitlementSummaryList.removeAllViews();
            for (EntitlementDTO entitleSummary : entitlementSelected) {
                LayoutInflater lin = LayoutInflater.from(this);
                entitlementSummaryList.addView(returnView(lin, entitleSummary));
            }
            ((TextView) findViewById(R.id.totalNumberOfProduct)).setText(entitlementSelected.size() + "");
            ((TextView) findViewById(R.id.totalAmount)).setText(setTotalAmount());

            Button submit = (Button) findViewById(R.id.summarySubmit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitBill();
                }
            });
            Button editBill = (Button) findViewById(R.id.summaryEdit);
            editBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editBillSummary();
                }
            });
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        }
    }

    /**
     * return User entitlement view
     *
     * @params entitle data
     */
    private View returnView(LayoutInflater entitle, EntitlementDTO data) {
        View convertView = entitle.inflate(R.layout.adapter_entitlementsummary, null);
        TextView entitlementName = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView entitlementPrice = (TextView) convertView.findViewById(R.id.entitlementPrice);
        TextView entitlementEntitled = (TextView) convertView.findViewById(R.id.entitlementSelected);
        TextView entitlementTotalAmount = (TextView) convertView.findViewById(R.id.entitlementTotalAmount);
        NumberFormat numberFormat = new DecimalFormat("#0.00");
        String productName = data.getProductName();
        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(data.getLproductName())) {
            Util.setTamilText(entitlementName, data.getLproductName() + "(" + data.getLproductUnit() + ")");
        } else
            entitlementName.setText(productName + "(" + data.getProductUnit() + ")");
        entitlementPrice.setText(numberFormat.format(data.getProductPrice()));
        NumberFormat unitFormat = new DecimalFormat("#0.000");
        entitlementEntitled.setText(unitFormat.format(data.getBought()));
        entitlementTotalAmount.setText(numberFormat.format(data.getTotalPrice()));
        return convertView;

    }

    /**
     * This method is used to edit the summary
     * navigate to sales entry page
     */
    private void editBillSummary() {
        startActivity(new Intent(this, SalesEntryActivity.class));
        finish();
    }


    /**
     * Used to get  the bill data from user
     * returns  UpdateStockRequestDto
     */
    private UpdateStockRequestDto setBillData() {
        UpdateStockRequestDto updateRequest = new UpdateStockRequestDto();
        QRTransactionResponseDto response = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto();

        updateRequest.setReferenceId(response.getReferenceId());
        BillDto bill = new BillDto();
        updateRequest.setUfc(response.getUfc());
        bill.setFpsId(response.getFpsId());
        bill.setBeneficiaryId(response.getBenficiaryId());
        bill.setCreatedby("1");
        bill.setAmount(Double.parseDouble(setTotalAmount()));
        if (response.getTransactionType() == TransactionTypes.SALE_QR_OTP_DISABLED) {
            bill.setMode('Q');
        } else if (response.getTransactionType() == TransactionTypes.SALE_QR_OTP_AUTHENTICATION) {
            bill.setMode('O');
        } else {
            bill.setMode('R');
        }
        bill.setUfc(response.getUfc());
        bill.setChannel('G');
        Date todayDate = new Date();
        SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        bill.setBillDate(billDate.format(todayDate));
        BillDto lastBill = FPSDBHelper.getInstance(this).lastGenBill();
        SimpleDateFormat toDate = new SimpleDateFormat("ddMMyy", Locale.getDefault());
        String transactionId = toDate.format(todayDate);
        if (lastBill == null) {
            bill.setTransactionId(transactionId + "001");
        } else {
            bill.setTransactionId(transactionId + returnNextValue(lastBill.getTransactionId()));
        }
        List<BillItemDto> billItems = new ArrayList<BillItemDto>();
        for (EntitlementDTO entitleSelection : EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList()) {
            if (entitleSelection.getBought() > 0) {
                BillItemDto billItem = new BillItemDto();
                billItem.setProductId(entitleSelection.getProductId());
                billItem.setCost(entitleSelection.getProductPrice());
                billItem.setQuantity(entitleSelection.getBought());
                billItems.add(billItem);
            }
        }
        AndroidDeviceProperties device = new AndroidDeviceProperties(this);
        updateRequest.setDeviceId(device.getDeviceProperties().getSerialNumber());
        updateRequest.setUfc(response.getUfc());
        bill.setBillItemDto(new HashSet<>(billItems));
        updateRequest.setBillDto(bill);
        return updateRequest;
    }

    /**
     * return long sequence number
     *
     * @paramlast inserted string value
     */
    private String returnNextValue(String lastValue) {
        try {
            String data = StringUtils.substring(lastValue, 6);
            Long nextData = 001l;
            SimpleDateFormat toDate = new SimpleDateFormat("dd", Locale.getDefault());
            String transactionId = toDate.format(new Date());
            if (StringUtils.substring(lastValue, 0, 2).equals(transactionId)) {
                nextData = Long.parseLong(data) + 1;
            }
            DecimalFormat formatter = new DecimalFormat("000");
            return formatter.format(nextData);
        } catch (Exception e) {
            Log.e("Last", e.toString(), e);
            return "001";
        }

    }

    /**
     * Used to submit the bill to local database
     * if it inserted then sent to server
     */

    private void submitBill() {
        ((Button) findViewById(R.id.summaryEdit)).setOnClickListener(null);
        ((Button) findViewById(R.id.summarySubmit)).setOnClickListener(null);
        TransactionBaseDto base = TransactionBase.getInstance().getTransactionBase();
        UpdateStockRequestDto updateStock = setBillData();
        updateStock.setRefNumber(appState.refId);
        base.setBaseDto(updateStock);

        if (FPSDBHelper.getInstance(this).insertBill(updateStock.getBillDto())) {
            List<BillItemDto> billItems = new ArrayList<BillItemDto>(updateStock.getBillDto().getBillItemDto());
            List<FPSStockDto> fpsStockDto = new ArrayList<FPSStockDto>();
            for (BillItemDto bItems : billItems) {
                FPSStockDto fpsStockDto1 = FPSDBHelper.getInstance(this).getAllProductStockDetails(bItems.getProductId());
                double quantity = fpsStockDto1.getQuantity();
                fpsStockDto1.setQuantity(quantity - bItems.getQuantity());
                fpsStockDto.add(fpsStockDto1);
                if (bItems.getQuantity() > 0)
                    FPSDBHelper.getInstance(this).insertStockHistory(quantity, fpsStockDto1.getQuantity(), "SALES", bItems.getQuantity(), bItems.getProductId());
            }
            FPSDBHelper.getInstance(this).stockUpdate(fpsStockDto);
            if (networkConnection.isNetworkAvailable()) {
                if (SessionId.getInstance().getSessionId().length() > 0) {
                    GlobalAppState.transactionType = 1;
                    BillUpdateToServer bill = new BillUpdateToServer(this);
                    bill.sendBillToServer(base);
                } else {
                    GlobalAppState.transactionType = 0;
                    Transaction trans = TransactionFactory.getTransaction(0);
                    trans.process(this, base, updateStock);
                }
            } else {
                GlobalAppState.transactionType = 0;
                Transaction trans = TransactionFactory.getTransaction(0);
                trans.process(this, base, updateStock);
            }
           /* Transaction trans = TransactionFactory.getTransaction(0);
            trans.process(this, base, updateStock);*/
          /* BillUpdateToServer bill = new BillUpdateToServer(this);
            bill.sendBillToServer(base);*/

            String updateStockString = new Gson().toJson(updateStock);
            Intent intent = new Intent(this, BillSuccessActivity.class);
            intent.putExtra("message", updateStockString);
            startActivity(intent);
            finish();
        } else {
            Util.messageBar(this, "Internal Error");
        }

    }


    /**
     * Find the total amount of selected product
     */
    private String setTotalAmount() {
        double totalValue = 0.0f;
        for (EntitlementDTO entitlement : entitlementSelected) {
            totalValue = totalValue + entitlement.getTotalPrice();
        }
        NumberFormat numberFormat = new DecimalFormat("#0.00");
        return numberFormat.format(totalValue);
    }


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
            address = address + ", " + address2;
        }

        if (StringUtils.isNotEmpty(address3)) {
            address = address + ", " + address3;
        }

        if (StringUtils.isNotEmpty(address4)) {
            address = address + ", " + address4;
        }

        if (StringUtils.isNotEmpty(address5)) {
            address = address + ", " + address5;
        }
        return address;
    }


    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
    }


    @Override
    public void onBackPressed() {

    }
}
