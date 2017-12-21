package com.omneagate.Util;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.CommonMethod;
import com.omneagate.activity.R;
import com.omneagate.activity.ReconciliationManualsyncActivity;

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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 15/10/16.
 */
public class AdjustmentSyncManually {
    List<POSStockAdjustmentDto> adjustmentList; //list of bills in offline
    Activity context;
    private String serverUrl = "";
    int adjustCount = 0;
    Dialog dialog;
    int sentCount = 1;

    public AdjustmentSyncManually(Activity context) {
        this.context = context;
    }

    public void adjustSync() {
        serverUrl = FPSDBHelper.getInstance(context).getMasterData("serverUrl");
        adjustmentList = new ArrayList<>();
        adjustmentList = FPSDBHelper.getInstance(context).stockAdjustmentDataToServer();
        adjustCount = adjustmentList.size();
        showDialog();
        adjustData();
    }

    private void showDialog() {
        dialog = new Dialog(context, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //here we set layout of progress dialog
        dialog.setContentView(R.layout.dialog_waiting);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        String textData = sentCount + " / " + adjustCount + " " + context.getString(R.string.adjustSyncing);
        ((TextView) dialog.findViewById(R.id.billText)).setText(textData);
        dialog.show();
    }

    private void call_error_dialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        new CommonMethod().error_dialog(context.getResources().getString(R.string.connectionError),context);

    }

    private void adjustData() {
        adjustmentList.clear();
        adjustmentList = FPSDBHelper.getInstance(context).stockAdjustmentDataToServer();
        if (adjustmentList.size() > 0) {
            syncBillsToServer(adjustmentList.get(0));
            if (dialog != null && dialog.isShowing()) {
                String textData = sentCount + " / " + adjustCount + " " + context.getString(R.string.adjustSyncing);
                ((TextView) dialog.findViewById(R.id.billText)).setText(textData);
            }
        } else {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ReconciliationManualsyncActivity activity = ((ReconciliationManualsyncActivity) context);
            activity.setbtntext();
            activity.getunsync_Adjust();
//            if (context.getLocalClassName().contains("TransactionCommodityActivity")) {
//                new CloseSaleDialog(context).show();
//            } else {
//                /*context.startActivity(new Intent(context, BillSearchActivity.class));
//                context.finish();*/
//            }
        }
    }

    /**
     * Check bill size
     * <p/>
     * Async task to call bills
     */
    private void syncBillsToServer(POSStockAdjustmentDto adjust) {
        Log.e("adjust from back", adjust.toString());
        new OfflineDataSyncTask().execute(adjust);
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

    /**
     * Async   task for connection heartbeat
     */
    private class OfflineDataSyncTask extends AsyncTask<POSStockAdjustmentDto, String, String> {
        @Override
        protected String doInBackground(POSStockAdjustmentDto... adjustData) {
            BufferedReader in = null;
            try {
                String url = serverUrl + "/posstockadjustment/fps/acknowledge";
                URI website = new URI(url);
                Log.i("Bills", adjustData[0].toString());
                String bill = new Gson().toJson(adjustData[0]);
                StringEntity entity = new StringEntity(bill, HTTP.UTF_8);
                HttpResponse response = requestType(website, entity);
                in = new BufferedReader(new InputStreamReader(response
                        .getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String l;
                String nl = System.getProperty("line.separator");
                while ((l = in.readLine()) != null) {
                    sb.append(l + nl);
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                Util.LoggingQueue(context, "Error", "Network exception" + e.getMessage());
                Log.e("Error in connection: ", e.toString());
                try {
                    if (in != null)
                        in.close();
                } catch (Exception e1) {
                    // Intentional swallow of exception
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                if ((response != null) && (!response.contains("<html>"))) {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    POSStockAdjustmentDto posData = gson.fromJson(response, POSStockAdjustmentDto.class);
                    if (posData != null && (posData.getStatusCode() == 0 || posData.getStatusCode() == 12008 || posData.getStatusCode() == 12009)) {
                        FPSDBHelper.getInstance(context).adjustmentUpdate(posData);
                        sentCount++;
                        adjustData();
                    } else {
                        Util.LoggingQueue(context, "Error", "Received null response ");
                        call_error_dialog();
                    }
                }
            } catch (Exception e) {
                Log.e("Insert Error", e.toString(), e);
                call_error_dialog();
            }
        }
    }
}
