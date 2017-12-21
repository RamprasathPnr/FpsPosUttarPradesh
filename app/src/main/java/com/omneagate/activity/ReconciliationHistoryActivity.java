package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BackgroundServiceDto;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.ReconciliationRequestDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.InwardDataAdapterNew;
import com.omneagate.activity.dialog.ServiceHistoryAdapter;
import com.omneagate.activity.dialog.reconciliationHistoryAdapter;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReconciliationHistoryActivity extends BaseActivity {

    int loadMore = 0;
    LoadMoreListView reconciliationHistoryListView;
    reconciliationHistoryAdapter adapter;
    ProgressBar progressBarSpin;
    private List<ReconciliationRequestDto> reconciliationHistoryList = null;
    String TAG = "ReconciliationHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_reconciliation_history);
        configureData();
    }

    private void configureData() {
            setUpPopUpPage();
            progressBarSpin = (ProgressBar) findViewById(R.id.progressBar1);
            TextView topTv = (TextView) findViewById(R.id.top_textView);
            Util.setTamilText(topTv, R.string.reconciliation_history);
            reconciliationHistoryList = new ArrayList<>();
            reconciliationHistoryListView = (LoadMoreListView) findViewById(R.id.listView_reconciliation_history);
            new getReconciliationDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            adapter = new reconciliationHistoryAdapter(this, reconciliationHistoryList);
            reconciliationHistoryListView.setAdapter(adapter);
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
    }

    private void loadListViewData() {
        new getReconciliationDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        adapter.notifyDataSetChanged();
    }

    private class getReconciliationDataTask extends AsyncTask<String, Void, List<ReconciliationRequestDto>> {

        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        protected List<ReconciliationRequestDto> doInBackground(final String... args) {
            List<ReconciliationRequestDto> reconciliationRequestDtoList = new ArrayList<ReconciliationRequestDto>();
            reconciliationHistoryList.clear();
            reconciliationRequestDtoList = FPSDBHelper.getInstance(ReconciliationHistoryActivity.this).getReconciliationData();
            return reconciliationRequestDtoList;
        }

        protected void onPostExecute(final List<ReconciliationRequestDto> reconciliationRequestDtoList) {
            progressBarSpin.setVisibility(View.GONE);
            if (reconciliationRequestDtoList.size() > 0) {
                reconciliationHistoryList.addAll(reconciliationRequestDtoList);
                reconciliationHistoryListView.setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutNoRecords)).setVisibility(View.GONE);
                reconciliationHistoryListView.invalidate();
                reconciliationHistoryListView.onLoadMoreComplete();
            } else if (reconciliationRequestDtoList.size() == 0 && reconciliationHistoryList.size() == 0) {
                reconciliationHistoryListView.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                reconciliationHistoryListView.onLoadMoreComplete();
            }
        }
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(this, ReconciliationManualsyncActivity.class));
        finish();
    }

    public void getStatus(ReconciliationRequestDto reconciliationRequestDto) {
        if (NetworkUtil.getConnectivityStatus(this) == 0 ) {
            Util.messageBar(this, getString(R.string.no_connectivity));
        } else {
            try {
                httpConnection = new HttpClientWrapper();
                String url = "/reconcile/fetch/request";
                String reconciliationReq = new Gson().toJson(reconciliationRequestDto);
                StringEntity se = null;
                se = new StringEntity(reconciliationReq, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.RECONCILIATION_STATUS, SyncHandler, RequestType.POST, se, this);
            }
            catch(Exception e) {}
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        dismissProgressBar();
        switch (what) {
            case ERROR_MSG:
                Toast.makeText(getBaseContext(), R.string.connectionRefused, Toast.LENGTH_LONG).show();
                break;
            case RECONCILIATION_STATUS:
                reconciliationStatusResponse(message);
                break;
            default:
                break;
        }
    }

    private void reconciliationStatusResponse(Bundle message) {
        String response = message.getString(FPSDBConstants.RESPONSE_DATA);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ReconciliationRequestDto reconciliationRequestDto = gson.fromJson(response, ReconciliationRequestDto.class);
        if (reconciliationRequestDto.getStatusCode() == 0) {
                FPSDBHelper.getInstance(ReconciliationHistoryActivity.this).updateReconciliationStatus(reconciliationRequestDto, response);
                loadListViewData();
        }
        else {
            String messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(reconciliationRequestDto.getStatusCode()));
            if (StringUtils.isEmpty(messageData))
                messageData = getString(R.string.genericDatabaseError);
            Toast.makeText(ReconciliationHistoryActivity.this, messageData, Toast.LENGTH_SHORT).show();
        }
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
