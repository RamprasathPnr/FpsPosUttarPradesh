package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.EntitlementDTO;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.TransactionController.Transaction;
import com.omneagate.TransactionController.TransactionFactory;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.Util.BillUpdateToServer;
import com.omneagate.activity.dialog.PaymentModeDialog;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

//FPS user can view the summary of selection
public class SalesSummaryWithOutOTPActivity extends BaseActivity {

    //Response from server set in this variable to load data
    private QRTransactionResponseDto entitlementResponseDTO;
    /*List of item entitled and price is in this variable*/
    private List<EntitlementDTO> entitleList;

    String SaleType = "";
    final static int EZSWYPE_APP = 1212;
    int RESULT_FAILED=2;
    String TXN_TYPE_SALE = "sale"; //sale
    String TXN_TYPE_VOID = "void"; //void

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sales_submission);

        Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "onCreate called");


        networkConnection = new NetworkConnection(this);
        appState = (GlobalAppState) getApplication();
        entitlementResponseDTO = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto();
        try {

            /** 11-07-2016
             * MSFixes
             * Added to get type of sale entry via getExtras
             *
             */

            if (getIntent().getStringExtra("SaleType") != null) {
                SaleType = getIntent().getStringExtra("SaleType");
                Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "SaleType -> "+SaleType);

            }
        } catch (Exception e) {

        }
        setUpInitiailPage();
    }


    /*
*
* Initial Setup
*
* */
    private void setUpInitiailPage() {
        try {
            setUpPopUpPage();
            appState = (GlobalAppState) getApplication();
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sale_confirm_activity);
            Util.setTamilText((TextView) findViewById(R.id.commoditys), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.rate), R.string.rate);
            Util.setTamilText((TextView) findViewById(R.id.billDetailQuantity), R.string.billDetailQuantity);
            Util.setTamilText((TextView) findViewById(R.id.billDetailProductPrice), R.string.billDetailProductPrice);
            Util.setTamilText((TextView) findViewById(R.id.editEntitlement), R.string.edit);
            Util.setTamilText((TextView) findViewById(R.id.submitEntitlement), R.string.fpsIDSubmit);
            LinearLayout entitlementList = (LinearLayout) findViewById(R.id.entitlement_background);
            entitleList = entitlementResponseDTO.getEntitlementList();
            findViewById(R.id.imageViewBack).setVisibility(View.INVISIBLE);
            entitlementList.removeAllViews();
            int position = 0;
            for (EntitlementDTO entitled : entitleList) {
                if (entitled.getBought() > 0) {
                    LayoutInflater lin = LayoutInflater.from(this);
                    entitlementList.addView(returnView(lin, entitled, position));
                    position++;
                }
            }
            Util.setTamilText((TextView) findViewById(R.id.totalAmount), getString(R.string.total) + ": \u20B9" + setTotalAmount());
            Button editBill = (Button) findViewById(R.id.editEntitlement);
            editBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editBillSummary();
                }
            });
            findViewById(R.id.submitEntitlement).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PaymentModeDialog paymentModeDialog = new PaymentModeDialog(SalesSummaryWithOutOTPActivity.this);
                    paymentModeDialog.show();
                    //submitBill();
                }
            });
        } catch (Exception e) {
        }
    }


    /**
     * return User entitlement view
     *
     * @params entitle data
     */
    /**
     * return User entitlement view
     *
     * @param entitle data, position of entitle
     */
    private View returnView(LayoutInflater entitle, EntitlementDTO data, int position) {
        View convertView = entitle.inflate(R.layout.view_entitlement_data, null);
        TextView entitlementName = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView entitlementPurchased = (TextView) convertView.findViewById(R.id.entitlementPurchased);
        TextView entitlementRate = (TextView) convertView.findViewById(R.id.entitlementRate);
        TextView entitlementUnit = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView amountOfSelection = (TextView) convertView.findViewById(R.id.amountOfSelection);
        String productName = data.getProductName();
        if (GlobalAppState.language.equals("hi") && StringUtils.isNotEmpty(data.getLproductName()))
//            Util.setTamilText(entitlementName, data.getLproductName());
            entitlementName.setText(data.getLproductName());
        else
            entitlementName.setText(productName);

        if (GlobalAppState.language.equals("hi") && StringUtils.isNotEmpty(data.getProductUnit()))
            Util.setTamilText(entitlementUnit, data.getLproductUnit());
        else
            entitlementUnit.setText(data.getProductUnit());
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        String qty = Util.quantityRoundOffFormat(data.getBought());
        entitlementPurchased.setText(qty);
        /*NumberFormat numberFormat = new DecimalFormat("#0.00");
        numberFormat.setRoundingMode(RoundingMode.CEILING);*/
        String amt1 = Util.priceRoundOffFormat(data.getProductPrice());
        entitlementRate.setText(amt1);
        String amt2 = Util.priceRoundOffFormat(data.getTotalPrice());
        amountOfSelection.setText(amt2);
        amountOfSelection.setId(position);

        return convertView;

    }

    /**
     * This method is used to edit the summary
     * navigate to sales entry page
     */
    private void editBillSummary() {


        //  startActivity(new Intent(this, SalesEntryActivity.class));
        Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "Editing bill details ");


/** 11-07-2016
 * MSFixes
 * Added to navigate back to respective sale type page
 *
 */

        Intent intent = new Intent(this, SalesEntryActivity.class);
        intent.putExtra("SaleType", "" + SaleType);
        startActivity(intent);

        finish();
    }


    /**
     * Used to get  the bill data from user
     * returns  UpdateStockRequestDto
     */
    private UpdateStockRequestDto setBillData() {
        Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "setBillData()");
        UpdateStockRequestDto updateRequest = new UpdateStockRequestDto();
        QRTransactionResponseDto response = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto();
        Log.e("getReferenceId", "" + response.getReferenceId());
        updateRequest.setReferenceId(response.getReferenceId());
        BillDto bill = new BillDto();
        bill.setFpsId(response.getFpsId());
        bill.setBeneficiaryId(response.getBenficiaryId());
        bill.setCreatedby(SessionId.getInstance().getUserId() + "");
        bill.setAmount(Double.parseDouble(setTotalAmount()));
        bill.setUfc(response.getUfc());


        //bill.setBillStatus();

        NetworkConnection network = new NetworkConnection(this);
        Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "isNetworkAvailable() > " + network.isNetworkAvailable());

        Boolean isNetworkConnected = false;
        if (SessionId.getInstance().getSessionId()!= null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId()) && networkConnection.isNetworkAvailable()) {
            isNetworkConnected = true;
        } else {
            isNetworkConnected = false;
        }
        if (isNetworkConnected) {
            bill.setChannel('G');
            bill.setMode('D');
        } else {
            bill.setChannel('G');
            bill.setMode('F');
        }


        /**  08/07/2016
         * MSFixes
         * SaleType defines Mode while inserting into FPSDB.db
         *                                Ration Card Based Sales
         *                                ----------------------
         * Online Sale  -  D  |  Offline Sale  -  F
         *                                     OR Based Sales
         *                                ----------------------
         * Online Sale  -  A  |  Offline Sale  -  E
         *                         Aadhar card Biometric Card Based Sales
         *                                ----------------------
         * Online Sale  -  G  |  No Offline Sale
         *                                Aadhar Card QR Based Sales
         *                                ----------------------
         * Online Sale  -  H  |  No Offline Sale
         *
         * No Offline Mode for Aadhar card Biometric based sale
         */

        if (SaleType.equalsIgnoreCase("AadharQRSale")) {
            if (isNetworkConnected) {
                bill.setMode('H');
                bill.setChannel('G');
            } else {
                bill.setMode('I');
                bill.setChannel('G');
            }
        } else if (SaleType.equalsIgnoreCase("QrCodeSale")) {
            if (isNetworkConnected) {
                bill.setChannel('G');
                bill.setMode('A');
            } else {
                bill.setChannel('G');
                bill.setMode('E');
            }

        } else if (SaleType.equalsIgnoreCase("AadharCardSale")) {
            bill.setMode('G');
            bill.setChannel('G');
        } else if (SaleType.equalsIgnoreCase("RationCardSale")) {
            if (isNetworkConnected) {
                bill.setChannel('G');
                bill.setMode('D');
            } else {
                bill.setChannel('G');
                bill.setMode('F');
            }

        }
        /*if (SaleType.equalsIgnoreCase("OTPSale")) {
            if (network.isNetworkAvailable()) {
                bill.setChannel('G');
                bill.setMode('C');
            }

        }*/

        if (SaleType.equalsIgnoreCase("QrCodeSale")) {
            if (isNetworkConnected) {
                bill.setChannel('G');
                bill.setMode('A');
            } else {
                bill.setChannel('G');
                bill.setMode('E');
            }

        }

        Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "isNetworkConnected ->" + isNetworkConnected + " SaleType ->"+SaleType);

        Date todayDate = new Date();
        SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        bill.setBillDate(billDate.format(todayDate));
        bill.setTransactionId(Util.getTransactionId(this));
        List<BillItemProductDto> billItems = new ArrayList<>();
        Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "entitlement list..." + EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().toString());
        for (EntitlementDTO entitleSelection : EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList()) {
            if (entitleSelection.getBought() > 0) {
                BillItemProductDto billItem = new BillItemProductDto();
                billItem.setProductId(entitleSelection.getProductId());
                Double amt2 = Double.parseDouble(Util.priceRoundOffFormat(entitleSelection.getProductPrice()));
                billItem.setCost(amt2);
                billItem.setQuantity(entitleSelection.getBought());
                billItems.add(billItem);
            }
        }
        AndroidDeviceProperties device = new AndroidDeviceProperties(this);
        updateRequest.setDeviceId(device.getDeviceProperties().getSerialNumber());
        updateRequest.setUfc(response.getUfc());
        bill.setBillItemDto(new HashSet<>(billItems));
        bill.setTotalBillItemCount(new HashSet<>(billItems).size());
        updateRequest.setBillDto(bill);


        return updateRequest;
    }


    /**
     * Used to submit the bill to local database
     * if it inserted then sent to server
     */

    public void submitBill() {
        findViewById(R.id.submitEntitlement).setOnClickListener(null);
        findViewById(R.id.submitEntitlement).setBackgroundColor(Color.LTGRAY);
        UpdateStockRequestDto updateStock = setBillData();
        Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "submitBill() ->" + updateStock.toString());


        /** 11-07-2016
         * MSFixes
         * Added to check net connection for submitting bill via Aadhar QR Based sales
         *
         */


        if (SaleType.equalsIgnoreCase("AadharQRSale")) {
//            if (networkConnection.isNetworkAvailable()) {
            new InsertBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, updateStock);
//            } else {
//                Util.messageBar(SalesSummaryWithOutOTPActivity.this, getString(R.string.no_connectivity));
//            }
        } else {
            new InsertBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, updateStock);
        }


    }



    public void submitCardPayment(){
        Intent intent = new Intent("com.ezswypelocal.mpos.home.SaleActivity");
        intent.putExtra("userName","1212121212");
        intent.putExtra("password","Ez@12345");
        intent.putExtra("transactionType",TXN_TYPE_SALE);
        intent.putExtra("amount","1");
        startActivityForResult(intent,EZSWYPE_APP);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EZSWYPE_APP:
                try{
                    if(resultCode==RESULT_OK){
                        Log.d("SalesSummaryWithOutOTPActivity", "Activity Returned : " +data.getStringExtra("result"));
                        submitBill();
                    }else if(resultCode==RESULT_CANCELED){
                        Log.d("SalesSummaryWithOutOTPActivity", "Activity Returned : " +data.getStringExtra("result"));
                        PaymentModeDialog paymentModeDialog = new PaymentModeDialog(SalesSummaryWithOutOTPActivity.this);
                        paymentModeDialog.show();

                    }else if(resultCode==RESULT_FAILED){
                        Log.d("SalesSummaryWithOutOTPActivity", "Activity Returned : " +data.getStringExtra("result"));
                        PaymentModeDialog paymentModeDialog = new PaymentModeDialog(SalesSummaryWithOutOTPActivity.this);
                        paymentModeDialog.show();
                    }
                }catch (NullPointerException e){
                    Log.e("Error",e.toString());
                }
                break;
            default:
                break;
        }
    }


    private void updateData(UpdateStockRequestDto updateStock) {
        TransactionBaseDto base = TransactionBase.getInstance().getTransactionBase();
        Log.e("updateData getType()", "" + base.getType());
        Log.e("updateData getTransactionType()", "" + base.getTransactionType());
        Log.e("updateData SaleType -=", "" + SaleType);

        if (base.getTransactionType() == TransactionTypes.SALE_QR_OTP_AUTHENTICATION || base.getTransactionType() == TransactionTypes.SALE_RMN_AUTHENTICATE) {
            updateStock.setRefNumber(appState.refId);
        }
        base.setBaseDto(updateStock);
        String stock_validation = "" + FPSDBHelper.getInstance(SalesSummaryWithOutOTPActivity.this).getMasterData("stock_validation");
        if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
            if (stock_validation.equalsIgnoreCase("1")) {
                updateStocks(updateStock);
            }
        }
        if (networkConnection.isNetworkAvailable() && SessionId.getInstance().getSessionId() != null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId())) {
            Util.LoggingQueue(com.omneagate.activity.SalesSummaryWithOutOTPActivity.this, "SalesSummaryWithOutOTPActivity", "Sending to server . Bill Details -> " + base.toString());
            /** 08/07/2016
             *  After successfull insertion of bill in FPSDB , bill is inserted into server
             */
            BillUpdateToServer bill = new BillUpdateToServer(SalesSummaryWithOutOTPActivity.this);
            bill.sendBillToServer(base);
        } else {
            Transaction trans = TransactionFactory.getTransaction(0);

            Util.LoggingQueue(com.omneagate.activity.SalesSummaryWithOutOTPActivity.this, "SalesSummaryWithOutOTPActivity", "trans => " + trans + "base => " + base +"updateStock = " + updateStock);

            trans.process(com.omneagate.activity.SalesSummaryWithOutOTPActivity.this, base, updateStock);
        }
        String updateStockString = updateStock.getBillDto().getTransactionId();
        Util.LoggingQueue(com.omneagate.activity.SalesSummaryWithOutOTPActivity.this, "SalesSummaryWithOutOTPActivity", "Moving to Success page updateStockString = " + updateStockString);
        Intent intent = new Intent(com.omneagate.activity.SalesSummaryWithOutOTPActivity.this, BillSuccessActivity.class);
        intent.putExtra("message", updateStockString);
        startActivity(intent);
        finish();
    }

    private void updateStocks(UpdateStockRequestDto updateStock) {
        List<BillItemProductDto> billItems = new ArrayList<>(updateStock.getBillDto().getBillItemDto());
        List<FPSStockDto> fpsStockDto = new ArrayList<>();
        for (BillItemProductDto bItems : billItems) {
            FPSStockDto fpsStockDto1 = FPSDBHelper.getInstance(this).getAllProductStockDetails(bItems.getProductId());
            if(fpsStockDto1 != null) {
                double quantity = fpsStockDto1.getQuantity();
                fpsStockDto1.setQuantity(quantity - bItems.getQuantity());
                fpsStockDto.add(fpsStockDto1);
                Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "Inserting inside stock history");
                if (bItems.getQuantity() > 0)
                    FPSDBHelper.getInstance(this).insertStockHistory(quantity, fpsStockDto1.getQuantity(), "SALES", bItems.getQuantity(), bItems.getProductId());
            }
            else {
                double quantity = 0.0;
                fpsStockDto1.setQuantity(quantity - bItems.getQuantity());
                fpsStockDto.add(fpsStockDto1);
                Util.LoggingQueue(this, "SalesSummaryWithOutOTPActivity", "Inserting inside stock history");
                if (bItems.getQuantity() > 0)
                    FPSDBHelper.getInstance(this).insertStockHistory(quantity, 0.0, "SALES", bItems.getQuantity(), bItems.getProductId());
            }
        }
        String stock_validation = "" + FPSDBHelper.getInstance(SalesSummaryWithOutOTPActivity.this).getMasterData("stock_validation");
        if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
            if (stock_validation.equalsIgnoreCase("1")) {
                FPSDBHelper.getInstance(this).stockUpdate(fpsStockDto);
            }
        }

    }

    /**
     * Find the total amount of selected product
     */
    private String setTotalAmount() {
        double totalValue = 0.0f;
        for (EntitlementDTO entitlement : entitleList) {
            String pr = Util.priceRoundOffFormat(entitlement.getTotalPrice());
            totalValue = totalValue + Double.parseDouble(pr);
        }
        return Util.priceRoundOffFormat(totalValue);
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
    }

    @Override
    public void onBackPressed() {

    }

    private class InsertBillTask extends AsyncTask<UpdateStockRequestDto, Void, Boolean> {

        UpdateStockRequestDto updateStock;

        @Override
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.SalesSummaryWithOutOTPActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("SalesSummaryWithOutOTPActivity", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final UpdateStockRequestDto... args) {
            updateStock = args[0];
            Log.e("InsertBillTask()", "getBillDto() > "+args[0].getBillDto());
            Log.e("InsertBillTask()", "updateStock > "+updateStock);



            return FPSDBHelper.getInstance(com.omneagate.activity.SalesSummaryWithOutOTPActivity.this).insertBill(args[0].getBillDto());
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean success) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e) {
            }

            if (success) {
                updateData(updateStock);
            } else {
                Util.messageBar(com.omneagate.activity.SalesSummaryWithOutOTPActivity.this, getString(R.string.connectionError));
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
