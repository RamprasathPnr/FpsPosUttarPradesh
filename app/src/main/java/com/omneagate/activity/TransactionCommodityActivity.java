package com.omneagate.activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.CloseOfProductDto;
import com.omneagate.DTO.CloseSaleTransactionDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockHistoryDto;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.DTO.UserDto.StockCheckDto;
import com.omneagate.Util.BillSyncManually;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.StringDigesterString;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.CloseSaleDialog;
import com.omneagate.activity.dialog.CloseSalePasswordDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.jasypt.digest.StringDigester;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class TransactionCommodityActivity extends BaseActivity {

    CloseSaleTransactionDto closeSaleTransactionDto;

    List<CloseOfProductDto> closeSaleProduct;

    CloseSaleDialog closeSaleDialog;

    CloseSalePasswordDialog closeSalePasswordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_transaction_commodity);
        configureData();
    }


    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            setUpPopUpPage();
            closeSaleTransactionDto = new CloseSaleTransactionDto();
            closeSaleProduct = new ArrayList<>();
            Util.LoggingQueue(this, "Close sale activity", "Page stating up");
            /*SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
            String formattedDate = df.format(new Date());*/
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.close_sale);
            Util.setTamilText((TextView) findViewById(R.id.commodity), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.unit), R.string.unit);
            Util.setTamilText((TextView) findViewById(R.id.opening_stock), R.string.opening_stock);
            Util.setTamilText((TextView) findViewById(R.id.inward_qty), R.string.inward_qty);
            Util.setTamilText((TextView) findViewById(R.id.sale_qty), R.string.sale_qty);
            Util.setTamilText((TextView) findViewById(R.id.stock_adjustment), R.string.stock_adjust);
            Util.setTamilText((TextView) findViewById(R.id.current_stock), R.string.current_stock);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.close_sale);
            Util.setTamilText((TextView) findViewById(R.id.totAmount), R.string.totAmount);
            Util.setTamilText((TextView) findViewById(R.id.totBills), R.string.total_bill);
            String fpsCode = getResources().getString(R.string.fps_code)+" : "+ SessionId.getInstance().getFpsCode().toUpperCase();
            ((TextView) findViewById(R.id.fpsCode)).setText(fpsCode);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            ((TextView) findViewById(R.id.date_today)).setText(formattedDate);

            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            List<StockCheckDto> productDtoList = FPSDBHelper.getInstance(this).getAllProductStockDetails();
            Log.i("product Dto", productDtoList.toString());
            int count = FPSDBHelper.getInstance(this).totalBillsToday();
//            closeSaleTransactionDto.setDateOfTxn(new Date().getTime());

            closeSaleTransactionDto.setDateOfTxn(new Date().getTime());

            closeSaleTransactionDto.setNumofTrans(count);
            closeSaleTransactionDto.setDeviceId(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());

            SimpleDateFormat df2  = new SimpleDateFormat("ddMMyymmss", Locale.getDefault());
            String transactionId = df2.format(new Date());
            transactionId = SessionId.getInstance().getFpsId() + transactionId;

            closeSaleTransactionDto.setTransactionId(Long.parseLong(transactionId));
            Double sum_amount = FPSDBHelper.getInstance(this).totalAmountToday();

//            sum_amount = Double.parseDouble(Util.priceRoundOffFormat(sum_amount));
            ((TextView) findViewById(R.id.close_sale_total_bills)).setText(String.valueOf(count));
            /*NumberFormat format = new DecimalFormat("#0.00");
            format.setRoundingMode(RoundingMode.CEILING);*/
//            String text = "\u20B9 " + format.format(sum_amount);
            String text = "\u20B9 " + Util.priceRoundOffFormat(sum_amount);
            ((TextView) findViewById(R.id.close_sale_total_amt)).setText(text);
            closeSaleTransactionDto.setTotalSaleCost(Double.parseDouble(Util.priceRoundOffFormat(sum_amount)));
            transactionLayout.removeAllViews();
            for (StockCheckDto products : productDtoList) {
                LayoutInflater lin = LayoutInflater.from(this);
                transactionLayout.addView(returnView(lin, products));
            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Close sale activity", "Error:" + e.toString());
            Log.e("TransactionCommodity", e.toString(), e);
        } finally {
            findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    syncPage();
                }
            });

            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }


    private void syncPage() {
        networkConnection = new NetworkConnection(this);

        findViewById(R.id.btnClose).setOnClickListener(null);
        findViewById(R.id.btnClose).setBackgroundColor(Color.LTGRAY);

        if (networkConnection.isNetworkAvailable() && SessionId.getInstance().getSessionId() != null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId())) {
            if (FPSDBHelper.getInstance(this).getAllBillsForSyncCheck() > 0l) {
                new BillSyncManually(this).billSync();
            } else {
                closeSaleDialog = new CloseSaleDialog(com.omneagate.activity.TransactionCommodityActivity.this);
                closeSaleDialog.show();
            }
        } else {
            closeSaleDialog = new CloseSaleDialog(com.omneagate.activity.TransactionCommodityActivity.this);
            closeSaleDialog.show();
        }

    }

    public void getUserPassword() {
        closeSalePasswordDialog = new CloseSalePasswordDialog(this);
        closeSalePasswordDialog.show();
    }

    public void passwordChecking(String password) {
        String passwordHash = FPSDBHelper.getInstance(this).getUserDetails(SessionId.getInstance().getUserId()).getUserDetailDto().getPassword();
        new LocalPasswordProcessCheck().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, password, passwordHash);
    }

    private boolean localDbPassword(String passwordUser, String passwordDbHash) {

        StringDigester stringDigester = StringDigesterString.getPasswordHash(this);

        return stringDigester.matches(passwordUser, passwordDbHash);
    }

    //Local login Process
    private class LocalPasswordProcessCheck extends AsyncTask<String, Void, Boolean> {


        /**
         * Local login Background Process
         * return true if user hash and dbhash equals else false
         */
        protected Boolean doInBackground(String... params) {
            try {
                return localDbPassword(params[0], params[1]);
            } catch (Exception e) {
                Log.e("loca lDb", "Interrupted", e);
                return false;

            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                if (progressBar != null) progressBar.dismiss();
            }
            catch(Exception e) {}

            if (result) {
                sendCloseSale();
            } else {
                Util.messageBar(com.omneagate.activity.TransactionCommodityActivity.this, getString(R.string.loginInvalidUserPassword));
            }
        }
    }

    private CloseOfProductDto getCloseSaleProduct(CloseOfProductDto closeOfProductDto, List<CloseOfProductDto> closeSale) {
        for (CloseOfProductDto close : closeSale) {
            if (close.getProductId() == closeOfProductDto.getProductId()) {
                closeOfProductDto.setTotalCost(close.getTotalCost());
                closeOfProductDto.setTotalQuantity(close.getTotalQuantity());
                closeOfProductDto.setOpeningStock(close.getOpeningStock());
                closeOfProductDto.setClosingStock(close.getClosingStock());
                closeOfProductDto.setInward(close.getInward());
            }
        }
        if (closeOfProductDto.getTotalCost() == null || StringUtils.isEmpty(closeOfProductDto.getTotalCost())) {
            closeOfProductDto.setTotalCost("0.00");
        }
        if (closeOfProductDto.getTotalQuantity() == null || StringUtils.isEmpty(closeOfProductDto.getTotalQuantity())) {
            closeOfProductDto.setTotalQuantity("0.000");
        }
        return closeOfProductDto;
    }

    private void sendCloseSale() {
        try {
            findViewById(R.id.btnClose).setOnClickListener(null);
            List<CloseOfProductDto> closeSale = FPSDBHelper.getInstance(this).getCloseSale();
            List<CloseOfProductDto> closeSaleUpdated = new ArrayList<>();
//            for (CloseOfProductDto closeOfProductDto : closeSaleProduct) {
            for (CloseOfProductDto closeOfProductDto : closeSale) {
                closeSaleUpdated.add(getCloseSaleProduct(closeOfProductDto, closeSale));
            }
            closeSaleTransactionDto.setCloseOfProductDtoList(new HashSet<>(closeSaleUpdated));
            if (NetworkUtil.getConnectivityStatus(this) == 0 || SessionId.getInstance().getSessionId().length() <= 0) {
                closeSaleTransactionDto.setIsServerAdded(1);
                FPSDBHelper.getInstance(this).insertIntoCloseSale(closeSaleTransactionDto);
//                FPSDBHelper.getInstance(this).insertCloseSaleProduct(closeSaleTransactionDto);
                logOutSuccess();
            } else {
                closeSaleTransactionDto.setIsServerAdded(2);
                FPSDBHelper.getInstance(this).insertIntoCloseSale(closeSaleTransactionDto);
//                FPSDBHelper.getInstance(this).insertCloseSaleProduct(closeSaleTransactionDto);
                httpConnection = new HttpClientWrapper();
                String url = "/closeofsale/save";
                String beneRegReq = new Gson().toJson(closeSaleTransactionDto);
                Log.e("Check", beneRegReq);
                StringEntity se = new StringEntity(beneRegReq, HTTP.UTF_8);
                Util.LoggingQueue(this, "Ration Card Registration", "Sending Benefeciary registration request to FPS server" + beneRegReq);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CLOSE_SALE,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }


    public void logOutSuccess() {
        SessionId.getInstance().setSessionId("");
        FPSDBHelper.getInstance(com.omneagate.activity.TransactionCommodityActivity.this).updateLoginHistory(SessionId.getInstance().getTransactionId(), "CLOSE_SALE_LOGOUT_OFFLINE");
        FPSDBHelper.getInstance(com.omneagate.activity.TransactionCommodityActivity.this).closeConnection();
        startActivity(new Intent(com.omneagate.activity.TransactionCommodityActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * User entitlement view
     */
    private View returnView(LayoutInflater entitle, StockCheckDto fpsStock) {
        View convertView = entitle.inflate(R.layout.adapter_commodity_stock, null);
        TextView productNameTv = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView unitTv = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView openingStockTv = (TextView) convertView.findViewById(R.id.entitlement_opening);
        TextView adjustmentStock = (TextView) convertView.findViewById(R.id.entitlement_adjustment);
        TextView saleQuantityTv = (TextView) convertView.findViewById(R.id.entitlement_sale);
        TextView currentStockTv = (TextView) convertView.findViewById(R.id.amount_current);
        TextView inwardQuantityTv = (TextView) convertView.findViewById(R.id.entitlement_inward_quantity);

        /*NumberFormat format = new DecimalFormat("#0.000");
        format.setRoundingMode(RoundingMode.CEILING);*/
        FPSStockHistoryDto fpsStockHistory = FPSDBHelper.getInstance(this).getAllProductStockHistoryDetails(fpsStock.getProductId());
        List<POSStockAdjustmentDto> fpsStockAdjustment = FPSDBHelper.getInstance(this).getStockAdjustment(fpsStock.getProductId());
        double adjustment = 0.0;
        if (fpsStockAdjustment.size() == 0) {
//            adjustment = Util.quantityRoundOffFormat(adjustment);
            adjustmentStock.setText(Util.quantityRoundOffFormat(adjustment));
        } else {
            adjustment = getAdjustedValue(fpsStockAdjustment);
//            adjustment = Util.quantityRoundOffFormat(adjustment);
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

        String qty1 = Util.quantityRoundOffFormat(fpsStockHistory.getCurrQuantity());
        openingStockTv.setText(qty1);

        String qty2 = Util.quantityRoundOffFormat(fpsStock.getQuantity());
        currentStockTv.setText(qty2);

        String qty3 = Util.quantityRoundOffFormat(fpsStock.getSold());
        saleQuantityTv.setText(qty3);

        double inward = getProductInward(fpsStock.getProductId());
//        inward = Util.quantityRoundOffFormat(inward);
        inwardQuantityTv.setText(Util.quantityRoundOffFormat(inward));
        if (fpsStock.getUnit().equalsIgnoreCase("kg")) {
            if (fpsStockHistory.getCurrQuantity() > 10000.0) {
                double openings = fpsStockHistory.getCurrQuantity() / 1000;
//                openings = Util.quantityRoundOffFormat(openings);
                openingStockTv.setText(Util.quantityRoundOffFormat(openings));
                Util.setTamilText(unitTv, getString(R.string.tons));

                double closings = fpsStock.getQuantity() / 1000;
//                closings = Util.quantityRoundOffFormat(closings);
                currentStockTv.setText(Util.quantityRoundOffFormat(closings));

                double sold = fpsStock.getSold() / 1000;
//                sold = Util.quantityRoundOffFormat(sold);
                saleQuantityTv.setText(Util.quantityRoundOffFormat(sold));

                double inwards = inward / 1000;
//                inwards = Util.quantityRoundOffFormat(inwards);
                inwardQuantityTv.setText(Util.quantityRoundOffFormat(inwards));


                double adjusted = adjustment / 1000;
//                adjusted = Util.quantityRoundOffFormat(adjusted);
                adjustmentStock.setText(Util.quantityRoundOffFormat(adjusted));
                if(adjustment<0){
                    adjustmentStock.setTextColor(Color.RED);
                }
            }
        }

        return convertView;

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

    private Double getProductInward(long productId) {
        BillItemDto productInwardToday = FPSDBHelper.getInstance(this).getAllInwardListTodayTwo(productId);
//        BillItemDto productInwardToday = FPSDBHelper.getInstance(this).getAllInwardListToday(productId);
        return productInwardToday.getQuantity();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        switch (what) {

           /* case ERROR_MSG:

                Util.messageBar(this, getString(R.string.connectionRefused));

                break;
            */
            case CLOSE_SALE:
                updateAndLogOut(message);
                break;
            default:
                logOutSuccess();
                break;
        }
    }

    private void updateAndLogOut(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            CloseSaleTransactionDto closeSaleTransactionDto = gson.fromJson(response, CloseSaleTransactionDto.class);
            if (closeSaleTransactionDto != null && (closeSaleTransactionDto.getStatusCode() == 0 || closeSaleTransactionDto.getStatusCode() == 7001)) {
                FPSDBHelper.getInstance(this).updateCloseDale(closeSaleTransactionDto);
            }
            else {
                Util.LoggingQueue(TransactionCommodityActivity.this, "Error", "Received null response ");
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        } finally {
            logOutSuccess();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        Util.LoggingQueue(this, "Close sale Activity", "Back press called");
        finish();
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

        try {
            if ((closeSaleDialog != null) && closeSaleDialog.isShowing()) {
                closeSaleDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            closeSaleDialog = null;
        }

        try {
            if ((closeSalePasswordDialog != null) && closeSalePasswordDialog.isShowing()) {
                closeSalePasswordDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            closeSalePasswordDialog = null;
        }
    }

}
