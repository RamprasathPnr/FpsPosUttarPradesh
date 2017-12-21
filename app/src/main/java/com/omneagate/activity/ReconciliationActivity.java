package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.ReconciliationPosStockDetailDto;
import com.omneagate.DTO.ReconciliationRequestDto;
import com.omneagate.DTO.ReconciliationStockDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.CompleteObservationDialog;
import com.omneagate.activity.dialog.ReconciliationAdapter;
import com.omneagate.activity.dialog.ReconciliationSuccessDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReconciliationActivity extends BaseActivity {

    int loadMore = 0;
    LoadMoreListView reconcillationListView;
    ProgressBar progressBarSpin;
    private List<ReconciliationStockDto> reconciliationStockDtoList1 = null;
    ReconciliationRequestDto reconciliationRequestDto;
    long timestamp;
    int retryCount = 3;
    String TAG = "ReconciliationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_reconciliation);
        timestamp = new Date().getTime();
        configureData();
    }

    private void configureData() {
        setUpPopUpPage();
        progressBarSpin = (ProgressBar) findViewById(R.id.progressBar1);
        TextView topTv = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(topTv, R.string.reconciliation);
        reconciliationRequestDto = new ReconciliationRequestDto();
        reconciliationStockDtoList1 = new ArrayList<>();

        reconcillationListView = (LoadMoreListView) findViewById(R.id.listView_reconciliation);
        new reconciliationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ReconciliationAdapter adapter = new ReconciliationAdapter(this, reconciliationStockDtoList1);
        reconcillationListView.setAdapter(adapter);


        final Button reconciliationBut = (Button) findViewById(R.id.reconciliationButton);
        reconciliationBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reconciliationRequestDto.setFpsId(SessionId.getInstance().getFpsId());
                reconciliationRequestDto.setTransactionId(String.valueOf(timestamp));
                List<ReconciliationPosStockDetailDto> reconciliationPosStockDetailDtoList = new ArrayList<ReconciliationPosStockDetailDto>();
                for (int i = 0; i < reconciliationStockDtoList1.size(); i++) {
                    Long id = reconciliationStockDtoList1.get(i).getProductId();
                    double qty = reconciliationStockDtoList1.get(i).getQuantity();
                    ReconciliationPosStockDetailDto reconciliationPosStockDetailDto = new ReconciliationPosStockDetailDto();
                    reconciliationPosStockDetailDto.setProductId(id);
                    reconciliationPosStockDetailDto.setPosQuantity(qty);
                    reconciliationPosStockDetailDtoList.add(reconciliationPosStockDetailDto);
                }
                reconciliationRequestDto.setPosStockDetailDtos(reconciliationPosStockDetailDtoList);
                submitReconciliation(reconciliationRequestDto);
            }
        });

        CheckBox termsCheck = (CheckBox) findViewById(R.id.terms);
        termsCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    enableReconciliationButton();
                } else {
                    disableReconciliationButton();
                }
            }
        });

        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Button closeBut = (Button) findViewById(R.id.closeButton);
        closeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    private class reconciliationTask extends AsyncTask<String, Void, List<ReconciliationStockDto>> {

        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        protected List<ReconciliationStockDto> doInBackground(final String... args) {
            List<ReconciliationStockDto> reconciliationStockDtoList = new ArrayList<ReconciliationStockDto>();
            reconciliationStockDtoList = FPSDBHelper.getInstance(ReconciliationActivity.this).getStockForReconciliation();
            return reconciliationStockDtoList;
        }

        protected void onPostExecute(final List<ReconciliationStockDto> reconciliationStockDtoList) {
            progressBarSpin.setVisibility(View.GONE);
            if (reconciliationStockDtoList.size() > 0) {
                reconciliationStockDtoList1.clear();
                reconciliationStockDtoList1.addAll(reconciliationStockDtoList);
                reconcillationListView.setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutNoRecords)).setVisibility(View.GONE);
                reconcillationListView.invalidate();
                reconcillationListView.onLoadMoreComplete();
            } else if (reconciliationStockDtoList.size() == 0 && reconciliationStockDtoList1.size() == 0) {
                reconcillationListView.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                reconcillationListView.onLoadMoreComplete();
            }
        }
    }

    private void submitReconciliation(ReconciliationRequestDto reconciliationRequestDto) {
        if (NetworkUtil.getConnectivityStatus(this) == 0 ) {
            Util.messageBar(this, getString(R.string.no_connectivity));
        } else {
            disableReconciliationButton();
            try {
                httpConnection = new HttpClientWrapper();
                String url = "/reconcile/fps/stock";
                String reconciliationReq = new Gson().toJson(reconciliationRequestDto);
                new insertReconciliationData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, reconciliationReq);
                StringEntity se = null;
                se = new StringEntity(reconciliationReq, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.RECONCILIATION, SyncHandler, RequestType.POST, se, this);
            }
            catch(Exception e) {}
        }
    }

    private class insertReconciliationData extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(final String... args) {
            FPSDBHelper.getInstance(ReconciliationActivity.this).insertReconciliationData(reconciliationRequestDto, args[0]);
            return true;
        }

        protected void onPostExecute(final Boolean unused) {}
    }

    private class updateReconciliationData extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(final String... args) {
            FPSDBHelper.getInstance(ReconciliationActivity.this).updateReconciliationData(reconciliationRequestDto, args[0]);
            return true;
        }

        protected void onPostExecute(final Boolean unused) {}
    }

    private class updateReconciliationErrorData extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(final String... args) {
            FPSDBHelper.getInstance(ReconciliationActivity.this).updateReconciliationErrorData(reconciliationRequestDto, args[0], args[1]);
            return true;
        }

        protected void onPostExecute(final Boolean unused) {}
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        dismissProgressBar();
        switch (what) {
            case ERROR_MSG:
                Toast.makeText(getBaseContext(), R.string.connectionRefused, Toast.LENGTH_LONG).show();
                retryCount--;
                if(retryCount > 0) {
                    enableReconciliationButton();
                }
                else {
                    Toast.makeText(getBaseContext(), R.string.retry_limit, Toast.LENGTH_LONG).show();
                }
                break;
            case RECONCILIATION:
                reconciliationResponse(message);
                break;
            default:
                break;
        }
    }

    private void reconciliationResponse(Bundle message) {
        String response = message.getString(FPSDBConstants.RESPONSE_DATA);
        if ((response != null) && (!response.contains("<html>"))) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto base = gson.fromJson(response, BaseDto.class);
            if (base.getStatusCode() == 0) {
                reconciliationRequestDto = gson.fromJson(response, ReconciliationRequestDto.class);
                new updateReconciliationData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response);
                ReconciliationSuccessDialog reconciliationSuccessDialog = new ReconciliationSuccessDialog(this);
                reconciliationSuccessDialog.show();
                reconciliationSuccessDialog.setCanceledOnTouchOutside(false);
            } else {
                String messageData = "";
                try {
                    messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(base.getStatusCode()));
                }
                catch(Exception e) {
                    messageData = "";
                }
                if (StringUtils.isEmpty(messageData))
                    messageData = getString(R.string.connectionRefused);
                Toast.makeText(ReconciliationActivity.this, messageData, Toast.LENGTH_SHORT).show();
                new updateReconciliationErrorData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response, messageData);
                retryCount--;
                if (retryCount > 0) {
                    enableReconciliationButton();
                } else {
                    Toast.makeText(getBaseContext(), R.string.retry_limit, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void disableReconciliationButton() {
        findViewById(R.id.reconciliationButton).setEnabled(false);
        findViewById(R.id.reconciliationButton).setBackgroundColor(Color.LTGRAY);
    }

    private void enableReconciliationButton() {
        findViewById(R.id.reconciliationButton).setEnabled(true);
        findViewById(R.id.reconciliationButton).setBackgroundColor(getResources().getColor(R.color.cpb_blue));
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(this, ReconciliationManualsyncActivity.class));
        finish();
    }

    private void dismissProgressBar() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressBar();
    }
}
