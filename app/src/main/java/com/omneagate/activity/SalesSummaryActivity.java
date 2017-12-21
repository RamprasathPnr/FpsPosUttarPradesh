package com.omneagate.activity;

import android.content.Intent;
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
import com.omneagate.activity.dialog.PaymentModeDialogWithOTP;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


//FPS user can view the summary of selection
public class SalesSummaryActivity extends BaseActivity {


    //Response from server set in this variable to load data
    private QRTransactionResponseDto entitlementResponseDTO;
    /*List of item entitled and price is in this variable*/
    private List<EntitlementDTO> entitleList;
    BillDto bill = new BillDto();
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
        Util.LoggingQueue(this, "SalesSummaryActivity", "onCreate called ");

        networkConnection = new NetworkConnection(this);
        appState = (GlobalAppState) getApplication();
        entitlementResponseDTO = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto();
        Util.LoggingQueue(this, "SalesSummaryActivity", "onCreate called entitlementResponseDTO = " + entitlementResponseDTO);



        try {


/** 11-07-2016
 * MSFixes
 * Added to get type of sale entry via getExtras
 *
 */


            if (getIntent().getStringExtra("SaleType") != null) {
                SaleType = getIntent().getStringExtra("SaleType");
                Util.LoggingQueue(this, "SalesSummaryActivity", "From Intent SaleType = " + SaleType);


            } else {

                Util.LoggingQueue(this, "SalesSummaryActivity", "No SaleType from Intent = " + SaleType);


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
        setUpPopUpPage();

        Util.LoggingQueue(this, "SalesSummaryActivity", "setUpInitiailPage() called ");

        appState = (GlobalAppState) getApplication();

        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sale_confirm_activity);
        Util.setTamilText((TextView) findViewById(R.id.commoditys), R.string.commodity);
        Util.setTamilText((TextView) findViewById(R.id.unit), R.string.unit);
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
        Util.setTamilText((TextView) findViewById(R.id.totalAmount), getString(R.string.total) + ": \u20B9 " + setTotalAmount());
//        ((TextView) findViewById(R.id.totalAmount)).setText(getString(R.string.total) + ": \u20B9 " + setTotalAmount());
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
                Log.e("Workingdfg","SalesSummaryActivity");
                PaymentModeDialogWithOTP paymentModeDialog = new PaymentModeDialogWithOTP(SalesSummaryActivity.this);
                paymentModeDialog.show();

            }
        });

    }


    /**
     * Used to get  the bill data from user
     * returns  UpdateStockRequestDto
     */
    private UpdateStockRequestDto setBillData() {
        Util.LoggingQueue(this, "SalesSummaryActivity", "setBillData() called ");

        UpdateStockRequestDto updateRequest = new UpdateStockRequestDto();
        QRTransactionResponseDto response = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto();
        updateRequest.setReferenceId(response.getReferenceId());
        bill = new BillDto();
        updateRequest.setUfc(response.getUfc());
        bill.setFpsId(response.getFpsId());
        bill.setBeneficiaryId(response.getBenficiaryId());
        bill.setCreatedby(SessionId.getInstance().getUserId() + "");
        bill.setAmount(Double.parseDouble(setTotalAmount()));

        Util.LoggingQueue(this, "SalesSummaryActivity", "setBillData() getTransactionType =  " + response.getTransactionType());


        /*if (response.getTransactionType() == TransactionTypes.SALE_QR_OTP_DISABLED) {
            bill.setMode('Q');
        }*/ /*else if (response.getTransactionType() == TransactionTypes.SALE_QR_OTP_AUTHENTICATION) {
            bill.setMode('U');
        } *//*if (response.getTransactionType() == TransactionTypes.SALE_HAVE_OTP_AUTHENTICATE) {
            bill.setMode('O');
        } else if (response.getTransactionType() == TransactionTypes.SALE_RMN_AUTHENTICATE) {
            bill.setMode('C');
        }*/
        /*else {
            bill.setMode('R');
        }*/

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
         *                                OTP Based Sales
         *                                ----------------------
         * Online Sale  -  C  |  No offline sales
         */

        NetworkConnection network = new NetworkConnection(this);
        if (SaleType.equalsIgnoreCase("QrCodeSale")) {
            if (network.isNetworkAvailable()) {
                bill.setChannel('G');
                bill.setMode('A');
            } else {
                bill.setChannel('G');
                bill.setMode('E');
            }
        }
        if (SaleType.equalsIgnoreCase("AadharQRSale")) {
            if (network.isNetworkAvailable()) {
                bill.setChannel('G');
                bill.setMode('H');
            }
        }
        if (SaleType.equalsIgnoreCase("OTPSale")) {
            if (network.isNetworkAvailable()) {
                bill.setMode(entitlementResponseDTO.getMode());
                Util.LoggingQueue(this, "SalesSummaryActivity", "entitlementResponseDTO.getMode()..." + entitlementResponseDTO.getMode());
                bill.setChannel('G');
            }
        }



        Util.LoggingQueue(this, "SalesSummaryActivity", "setBillData() SaleType =  " + SaleType);

        Util.LoggingQueue(this, "SalesSummaryActivity", "setBillData() getMode =  " + bill.getMode());


        bill.setUfc(response.getUfc());
        Date todayDate = new Date();
        SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        updateRequest.setOtpUsedTime(billDate.format(todayDate));
        bill.setBillDate(billDate.format(todayDate));
        bill.setTransactionId(Util.getTransactionId(this));
        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>();
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
        updateRequest.setOtpId(response.getOtpId());
        Util.LoggingQueue(this, "SalesSummaryActivity", "setBillData() Update bills: =  " + updateRequest);

        return updateRequest;
    }


    /**
     * Used to submit the bill to local database
     * if it inserted then sent to server
     */

    public void submitBill() {

        try {
            Util.LoggingQueue(this, "SalesSummaryActivity", "submitBill() called ");


            findViewById(R.id.submitEntitlement).setOnClickListener(null);

            UpdateStockRequestDto updateStock = setBillData();
            //  Util.LoggingQueue(this, "*submitBill()", "updateStock" + updateStock.toString());
            //new InsertBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, updateStock);

            if (SaleType.equalsIgnoreCase("OTPSale")) {
                Util.LoggingQueue(this, "SalesSummaryActivity", "submitBill() , Sale is OTPSale Type ");

/** 11-07-2016
 * MSFixes
 * Added to check net connection for submitting bill via OTP Based sales
 *
 */





                if (networkConnection.isNetworkAvailable()) {
                    Util.LoggingQueue(this, "SalesSummaryActivity", "submitBill() ,OTPSale InsertBillTask " + networkConnection.isNetworkAvailable());

                    new InsertBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, updateStock);

                } else {
                    Util.LoggingQueue(this, "SalesSummaryActivity", "submitBill() , OTPSale Type  ,No   Net Connection = " + networkConnection.isNetworkAvailable());


                    Util.messageBar(SalesSummaryActivity.this, getString(R.string.no_connectivity));
                }

            } else {
                Util.LoggingQueue(this, "SalesSummaryActivity", "submitBill() , Sale is Not OTPSale Type , SaleType  = " + SaleType);

                new InsertBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, updateStock);

            }


        } catch (Exception e) {
            Util.LoggingQueue(this, "SalesSummaryActivity", "submitBill() ,Exception " + e.toString());


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
                        Log.d("SalesSummaryActivity ", "Activity Returned : " +data.getStringExtra("result"));
                        submitBill();
                    }else if(resultCode==RESULT_CANCELED){
                        Log.d("SalesSummaryActivity ", "Activity Returned : " +data.getStringExtra("result"));
                        PaymentModeDialogWithOTP paymentModeDialog = new PaymentModeDialogWithOTP(SalesSummaryActivity.this);
                        paymentModeDialog.show();

                    }else if(resultCode==RESULT_FAILED){
                        Log.d("SalesSummaryActivity ", "Activity Returned : " +data.getStringExtra("result"));
                        PaymentModeDialogWithOTP paymentModeDialog = new PaymentModeDialogWithOTP(SalesSummaryActivity.this);
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

        Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "updateData() called UpdateStockRequestDto = " + updateStock);

        TransactionBaseDto base = TransactionBase.getInstance().getTransactionBase();

        Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "updateData() called getTransactionType = " + base.getTransactionType());

        if (base.getTransactionType() != null) {
            if (base.getTransactionType() == TransactionTypes.SALE_QR_OTP_AUTHENTICATION || base.getTransactionType() == TransactionTypes.SALE_RMN_AUTHENTICATE) {
//                updateStock.setRefNumber(appState.refId);
                base.setType("com.omneagate.rest.dto.QRRequestDto");
                bill.setMode('C');
                Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "SALE_QR_OTP_AUTHENTICATION || SALE_RMN_AUTHENTICATE");
            }
        }

        if (base.getTransactionType() != null) {
            if (base.getTransactionType() == TransactionTypes.SALE_HAVE_OTP_AUTHENTICATE) {
//                updateStock.setRefNumber(appState.refId);
                base.setType("com.omneagate.rest.dto.QRRequestDto");
                bill.setMode('O');
                Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "SALE_HAVE_OTP_AUTHENTICATE");
            }
        }

        base.setBaseDto(updateStock);

        String stock_validation = "" + FPSDBHelper.getInstance(SalesSummaryActivity.this).getMasterData("stock_validation");
        if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
            if (stock_validation.equalsIgnoreCase("1")) {
                updateStocks(updateStock);
            }
        }




        if (networkConnection.isNetworkAvailable() && SessionId.getInstance().getSessionId() != null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId())) {
            Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "Sending bills to server , bill = " + base.toString());

            BillUpdateToServer bill = new BillUpdateToServer(com.omneagate.activity.SalesSummaryActivity.this);
            bill.sendBillToServer(base);
        } else {
            Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "No net connection ");

            try {
                Transaction trans = TransactionFactory.getTransaction(0);
                trans.process(this, base, updateStock);
            }
            catch (Exception e){
                Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "updateData() Exception "+e);

            }
        }
        String updateStockString = updateStock.getBillDto().getTransactionId();

        Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "Moving to Success page "+updateStockString);

        Intent intent = new Intent(this, BillSuccessActivity.class);
        intent.putExtra("message", updateStockString);
        startActivity(intent);
        finish();
    }

    private void updateStocks(UpdateStockRequestDto updateStock) {

        Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "updateStocks() called UpdateStockRequestDto = " + updateStock);

        try {
            List<BillItemProductDto> billItems = new ArrayList<>(updateStock.getBillDto().getBillItemDto());
            List<FPSStockDto> fpsStockDto = new ArrayList<FPSStockDto>();
            for (BillItemProductDto bItems : billItems) {
                FPSStockDto fpsStockDto1 = FPSDBHelper.getInstance(this).getAllProductStockDetails(bItems.getProductId());
                if (fpsStockDto1 != null) {
                    double quantity = fpsStockDto1.getQuantity();
                    fpsStockDto1.setQuantity(quantity - bItems.getQuantity());
                    fpsStockDto.add(fpsStockDto1);
                    Util.LoggingQueue(this, "*Sales Summary", "Inserting inside database");
                    if (bItems.getQuantity() > 0)
                        FPSDBHelper.getInstance(this).insertStockHistory(quantity, fpsStockDto1.getQuantity(), "SALES", bItems.getQuantity(), bItems.getProductId());
                }
                else {
                    double quantity = 0.0;
                    fpsStockDto1.setQuantity(quantity - bItems.getQuantity());
                    fpsStockDto.add(fpsStockDto1);
                    Util.LoggingQueue(this, "*Sales Summary", "Inserting inside database");
                    if (bItems.getQuantity() > 0)
                        FPSDBHelper.getInstance(this).insertStockHistory(quantity, 0.0, "SALES", bItems.getQuantity(), bItems.getProductId());
                }
            }
            FPSDBHelper.getInstance(this).stockUpdate(fpsStockDto);
        }
        catch (Exception e){
            Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "updateStocks() Exception = " + e);

        }
    }

    /**
     * Find the total amount of selected product
     */
    private String setTotalAmount() {
        double totalValue = 0.0f;
        for (EntitlementDTO entitlement : entitleList) {
            totalValue = totalValue + entitlement.getTotalPrice();
        }
        /*NumberFormat numberFormat = new DecimalFormat("#0.00");
        numberFormat.setRoundingMode(RoundingMode.CEILING);*/
//        totalValue = Util.priceRoundOffFormat(totalValue);
        return Util.priceRoundOffFormat(totalValue);
    }

    /**
     * return User entitlement view
     *
     * @params entitle data, position of entitle
     */
    private View returnView(LayoutInflater entitle, EntitlementDTO data, int position) {
        View convertView = entitle.inflate(R.layout.view_entitlement_data, null);
        TextView entitlementName = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView entitlementPurchased = (TextView) convertView.findViewById(R.id.entitlementPurchased);
        TextView entitlementRate = (TextView) convertView.findViewById(R.id.entitlementRate);
        TextView entitlementUnit = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView amountOfSelection = (TextView) convertView.findViewById(R.id.amountOfSelection);
        String productName = data.getProductName();
        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(data.getLproductName()))
//            Util.setTamilText(entitlementName, data.getLproductName());
            entitlementName.setText(data.getLproductName());
        else
            entitlementName.setText(productName);

        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(data.getProductUnit()))
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
        Util.LoggingQueue(this, "*Sales Summary", "Editing bill summary");
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

            Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "InsertBillTask() , onPreExecute() called ");

            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.SalesSummaryActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("*Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final UpdateStockRequestDto... args) {
            updateStock = args[0];
            Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "InsertBillTask() , doInBackground() updateStock =  " + updateStock);
            return FPSDBHelper.getInstance(com.omneagate.activity.SalesSummaryActivity.this).insertBill(args[0].getBillDto());
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean success) {


            Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "InsertBillTask() , onPostExecute() updateStock =  ");

            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e) {
            }


            try {
                if (success) {
                    Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "InsertBillTask() , onPostExecute() Succesfully inserted in DB   ");

                    updateData(updateStock);
                } else {

                    Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "InsertBillTask() , onPostExecute() Not inserted in DB   ");

                    Util.messageBar(com.omneagate.activity.SalesSummaryActivity.this, getString(R.string.internalError));
                    return;
                }
            } catch (Exception e) {
                Util.LoggingQueue(SalesSummaryActivity.this, "SalesSummaryActivity", "InsertBillTask() , Exception =   " + e.toString());


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
