package com.omneagate.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.DTO.UpdateStockResponseDto;
import com.omneagate.activity.BillSearchActivity;
import com.omneagate.activity.CommonMethod;
import com.omneagate.activity.R;
import com.omneagate.activity.ReconciliationManualsyncActivity;
import com.omneagate.activity.dialog.CloseSaleDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created for BillSyncManually.
 */
public class BillSyncManually {
    List<BillDto> billList; //list of bills in offline
    Activity context;
    private String serverUrl = "";
    int billCount = 0;
    Dialog dialog;
    int sentCount = 0;
    String TAG = "BillSyncManually";

    public BillSyncManually(Activity context) {
        this.context = context;
    }

    public void billSync() {
        serverUrl = FPSDBHelper.getInstance(context).getMasterData("serverUrl");
        billList = new ArrayList<>();
        billList = FPSDBHelper.getInstance(context).getAllBillsForSync();
        billCount = billList.size();
        showDialog();
        billData();
    }

    private void showDialog() {
        dialog = new Dialog(context, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //here we set layout of progress dialog
        dialog.setContentView(R.layout.dialog_waiting);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        String textData = sentCount + " / " + billCount + " " + context.getString(R.string.billSyncing);
        ((TextView) dialog.findViewById(R.id.billText)).setText(textData);
        dialog.show();
    }

    private void billData() {
        billList.clear();
        billList = FPSDBHelper.getInstance(context).getAllBillsForSync();
        if (billList.size() > 0) {
            syncBillsToServer(billList.get(0));
            if (dialog != null && dialog.isShowing()) {
                String textData = sentCount + " / " + billCount + " " + context.getString(R.string.billSyncing);
                ((TextView) dialog.findViewById(R.id.billText)).setText(textData);
            }
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Log.e(TAG, "activity name..."+context.getLocalClassName());
            if (context.getLocalClassName().contains("ReconciliationManualsyncActivity")) {
                ReconciliationManualsyncActivity activity = ((ReconciliationManualsyncActivity) context);
                activity.setbtntext();
                activity.getunsync_bills();
            } else if (context.getLocalClassName().contains("TransactionCommodityActivity")) {
                new CloseSaleDialog(context).show();
            } else {
                context.startActivity(new Intent(context, BillSearchActivity.class));
                context.finish();
            }
        }
    }

    /**
     * Check bill size
     * <p/>
     * Async task to call bills
     */
    private void syncBillsToServer(BillDto bill) {
        Log.e("Bills from back", bill.toString());
        TransactionBaseDto base = new TransactionBaseDto();
        base.setTransactionType(TransactionTypes.SALE_QR_OTP_DISABLED);
//        bill.setMode('F');
//        bill.setChannel('G');
        UpdateStockRequestDto updateStock = new UpdateStockRequestDto();
        updateStock.setReferenceId(bill.getTransactionId());
        updateStock.setDeviceId(Settings.Secure.getString(
                context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
        updateStock.setUfc(bill.getUfc());
        updateStock.setBillDto(bill);
        base.setBaseDto(updateStock);
        base.setType("com.omneagate.rest.dto.QRRequestDto");
        new OfflineDataSyncTask().execute(base);
    }

    /**
     * return http POST method using parameters
     */
    private HttpResponse requestType(URI website, StringEntity entity) throws IOException {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 15000;
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        int timeoutSocket = 15000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpPost postRequest = new HttpPost();
        postRequest.setURI(website);
        postRequest.setHeader("Content-Type", "application/json");
        postRequest.setHeader("Store_type", "fps");
        postRequest.setHeader("Cookie", "JSESSIONID=" + SessionId.getInstance().getSessionId());
        postRequest.setHeader("Cookie", "SESSION=" + SessionId.getInstance().getSessionId());
        postRequest.setEntity(entity);
        return client.execute(postRequest);
    }

    private String getresponseData(HttpResponse response) {
        String strresponse = null;
        if (response != null && response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = null;
            StringBuffer sb = new StringBuffer("");
            try {
                in = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                strresponse = sb.toString();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                strresponse = null;
            }
        }
        return strresponse;
    }

    private void call_error_dialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        new CommonMethod().error_dialog(context.getResources().getString(R.string.connectionError),context);
    }

    /**
     * Async   task for connection heartbeat
     */
    private class OfflineDataSyncTask extends AsyncTask<TransactionBaseDto, String, String> {
        @Override
        protected String doInBackground(TransactionBaseDto... billData) {
            BufferedReader in = null;
            try {
                String url = serverUrl + "/transaction/process";
                URI website = new URI(url);
                Log.i("Bills", billData[0].toString());
                String bill = new Gson().toJson(billData[0]);
                StringEntity entity = new StringEntity(bill, HTTP.UTF_8);
                HttpResponse response = requestType(website, entity);
                return getresponseData(response);
            } catch (Exception e) {
                Util.LoggingQueue(context, "Error", "Network exception" + e.getMessage());
                Log.e("Error in connection: ", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                if (response != null) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    UpdateStockResponseDto updateStock = gson.fromJson(response, UpdateStockResponseDto.class);
                    if (updateStock != null && updateStock.getStatusCode() == 0 && updateStock.getBillDto() != null) {
                        FPSDBHelper.getInstance(context).billUpdate(updateStock.getBillDto());
                        Util.LoggingQueue(context, "Info", "Updating status of bill to status T for bill id " + updateStock.getBillDto().getTransactionId());
                        repeat_sync();
                    } else if (updateStock != null && updateStock.getStatusCode() == 5085) {
                        FPSDBHelper.getInstance(context).billUpdate(updateStock.getBillDto());
                        Util.LoggingQueue(context, "Info", "Updating status of bill to status T for bill id " + updateStock.getBillDto().getTransactionId());
                    } else {
                        Util.LoggingQueue(context, "Error", "Received null response ");
                    }
//                    billList.remove(0);
                } else
                    call_error_dialog();
            } catch (Exception e) {
                Log.e("Insert Error", e.toString(), e);
                call_error_dialog();
            }
        }

        private void repeat_sync() {
            sentCount += 1;
            billData();
        }
    }
}
