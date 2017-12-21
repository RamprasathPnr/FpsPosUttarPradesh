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


import com.omneagate.DTO.BackgroundServiceDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.InwardDataAdapterNew;
import com.omneagate.activity.dialog.ServiceHistoryAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BackgroundServiceHistoryActivity extends BaseActivity {

    int loadMore = 0;
    LoadMoreListView serviceHistoryListView;
    ServiceHistoryAdapter adapter;
    ProgressBar progressBarSpin;
    int lv_position = 0;
    private List<BackgroundServiceDto> backgroundServiceHistoryList = null;
    String selectedService = "";
    String TAG = "BackgroundServiceHistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_background_service_history);
        configureData();
    }

    private void setTopTextView() {

        try {
            if (getIntent().getExtras().getString("lv_position") != null)
                lv_position = Integer.parseInt(getIntent().getExtras().getString("lv_position"));

        } catch (Exception e) {

        }

        Log.e("FpsStockInwardActivity ", "lv_position = "+lv_position);


        progressBarSpin = (ProgressBar) findViewById(R.id.progressBar1);
        Util.LoggingQueue(this, "Stock Inward activity", "Main page Called" + lv_position);
        TextView topTv = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(topTv, R.string.background_process_history);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardDateLabel)), R.string.req_data);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardactionLabel)), R.string.resp_data);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardGodownNameLabel)), R.string.req_date_time);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardLapsedTimeabel)), R.string.resp_date_time);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardactionGodownStatusLabel)), R.string.status);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardactionGodownViewLabel)), R.string.action);
    }

    private void configureData() {
        try {
            setUpPopUpPage();
            setTopTextView();
            backgroundServiceHistoryList = new ArrayList<>();
            serviceHistoryListView = (LoadMoreListView) findViewById(R.id.listView_background_service_history);
            adapter = new ServiceHistoryAdapter(this, backgroundServiceHistoryList);
            serviceHistoryListView.setAdapter(adapter);
            serviceHistoryListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loadMore++;
//                    new historyByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            new historyByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            serviceHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        } catch (Exception e) {
            Util.LoggingQueue(this, "Stock Inward activity", "Error:" + e.toString());
            Log.e("Error", e.toString(), e);
        } finally {
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

        }

        final Spinner mSpinnerServiceType = (Spinner) findViewById(R.id.serviceTypeSpinner);
        List<String> serviceList = new ArrayList<String>();
        serviceList.add("HeartBeatService");
        serviceList.add("StatisticsService");
        serviceList.add("AllocationService");
        serviceList.add("RegularSyncService");
        serviceList.add("InwardService");
        serviceList.add("AdjustmentService");
        serviceList.add("AdvanceStockService");
        serviceList.add("BillService");
        serviceList.add("CloseSaleService");
        serviceList.add("LoginService");
        serviceList.add("RemoteLogService");
        serviceList.add("SyncExceptionService");
        serviceList.add("BifurcationService");
        serviceList.add("MigrationOutService");
        serviceList.add("MigrationInService");
        serviceList.add("BiometricService");
        serviceList.add("InspectionReportService");
        serviceList.add("InspectionReportAckService");
        serviceList.add("CardInspectionService");
        serviceList.add("StockInspectionService");
        serviceList.add("WeighmentInspectionService");
        serviceList.add("ShopInspectionService");
        serviceList.add("OtherInspectionService");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerServiceType.setAdapter(adapter);
        mSpinnerServiceType.setPrompt(getString(R.string.selection));
        mSpinnerServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedService = mSpinnerServiceType.getSelectedItem().toString();
                new historyByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private class historyByDateTask extends AsyncTask<String, Void, List<BackgroundServiceDto>> {
        
        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        protected List<BackgroundServiceDto> doInBackground(final String... args) {
            List<BackgroundServiceDto> backgroundServiceDtoList = new ArrayList<BackgroundServiceDto>();
            backgroundServiceHistoryList.clear();
            if(selectedService.equalsIgnoreCase("HeartBeatService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getHeartBeatServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("StatisticsService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getStatisticsServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("AllocationService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getAllocationServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("RegularSyncService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getRegularSyncServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("InwardService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getInwardServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("AdjustmentService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getAdjustmentServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("AdvanceStockService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getAdvanceStockServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("BillService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getBillServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("CloseSaleService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getCloseSaleServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("LoginService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getLoginServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("RemoteLogService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getRemoteLogServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("SyncExceptionService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getSyncExceptionServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("BifurcationService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getBifurcationServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("MigrationOutService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getMigrationOutServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("MigrationInService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getMigrationInServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("BiometricService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getBiometricServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("InspectionReportService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getInspectionReportServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("InspectionReportAckService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getInspectionReportAckServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("CardInspectionService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getCardInspectionServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("StockInspectionService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getStockInspectionServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("WeighmentInspectionService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getWeighmentInspectionServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("ShopInspectionService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getShopInspectionServiceHistoryList();
            }
            else if(selectedService.equalsIgnoreCase("OtherInspectionService")) {
                backgroundServiceDtoList = FPSDBHelper.getInstance(BackgroundServiceHistoryActivity.this).getOtherInspectionServiceHistoryList();
            }


            Log.e(TAG,"backgroundServiceDtoList size..."+backgroundServiceDtoList.size());
            return backgroundServiceDtoList;
        }

        protected void onPostExecute(final List<BackgroundServiceDto> backgroundServiceDtoList) {
            progressBarSpin.setVisibility(View.GONE);
            if (backgroundServiceDtoList.size() > 0) {
                backgroundServiceHistoryList.addAll(backgroundServiceDtoList);
                serviceHistoryListView.setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutNoRecords)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                serviceHistoryListView.invalidate();
                serviceHistoryListView.onLoadMoreComplete();
                /*serviceHistoryListView.post(new Runnable() {
                    @Override
                    public void run() {
                        serviceHistoryListView.setSelectionFromTop(lv_position, 0);
                    }
                });*/
            } else if (backgroundServiceDtoList.size() == 0 && backgroundServiceHistoryList.size() == 0) {
                serviceHistoryListView.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                serviceHistoryListView.onLoadMoreComplete();
                /*serviceHistoryListView.post(new Runnable() {
                    @Override
                    public void run() {
                        serviceHistoryListView.setSelectionFromTop(lv_position, 0);
                    }
                });*/
            }
        }
    }


    public void onClose(View view) {
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        Util.LoggingQueue(this, "Stock Inward activity", "Back Pressed Called");
        startActivity(new Intent(this, BeneficiaryMenuActivity.class));
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

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
