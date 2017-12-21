package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.StockAdjustmentListAdapter;

import java.util.ArrayList;
import java.util.List;

public class FpsStockAdjustmentListActivity extends BaseActivity {

    int loadMore = 0;

    LoadMoreListView billByDate;

    StockAdjustmentListAdapter adapter;

    ProgressBar progressBarSpin;

    //List of GodownStockDto for fps stock inward
    private List<POSStockAdjustmentDto> fpsStockAdjustList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fps_stock_adjustment_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        configureData();
    }

    private void setTopTextView() {
        progressBarSpin = (ProgressBar) findViewById(R.id.progressBar1);
        Util.LoggingQueue(this, "Stock Adjustment List activity", "Main page Called");
        TextView topTv = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(topTv, R.string.stock_adjust_list_history);
        Util.setTamilText(((TextView) findViewById(R.id.btnClose)), R.string.close);
        Util.setTamilText(((TextView) findViewById(R.id.fpsStockAdjustListChallanNoLabel)), R.string.reference_no);
        Util.setTamilText(((TextView) findViewById(R.id.fpsStockAdjustDispatchDateLabel)), R.string.dispatch_date);
        Util.setTamilText(((TextView) findViewById(R.id.fpsStockAdjustListStatusLabel)), R.string.status);
        Util.setTamilText(((TextView) findViewById(R.id.fpsStockAdjustListSyncLabel)), R.string.sync);
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            setUpPopUpPage();
            setTopTextView();
            fpsStockAdjustList = new ArrayList<>();
            billByDate = (LoadMoreListView) findViewById(R.id.listView_fps_stock_adjust);

            adapter = new StockAdjustmentListAdapter(this, fpsStockAdjustList);
            billByDate.setAdapter(adapter);
            billByDate.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loadMore++;
                    new InwardByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            new InwardByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
           /* billByDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    submitInward(position);
                }
            });*/
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
    }

    private class InwardByDateTask extends AsyncTask<String, Void, List<POSStockAdjustmentDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<POSStockAdjustmentDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.FpsStockAdjustmentListActivity.this).allStockAdjustmentData();
        }

        // can use UI thread here
        protected void onPostExecute(final List<POSStockAdjustmentDto> billDtos) {
            progressBarSpin.setVisibility(View.GONE);

            if (billDtos.size() > 0) {
                fpsStockAdjustList.clear();
                fpsStockAdjustList.addAll(billDtos);
                billByDate.setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutNoRecords)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                billByDate.invalidate();
                billByDate.onLoadMoreComplete();
            } else if (billDtos.size() == 0 && fpsStockAdjustList.size() == 0) {
                billByDate.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                billByDate.onLoadMoreComplete();
            }
        }
    }


    public void onClose(View view) {
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        Util.LoggingQueue(this, "Stock Adjustment list activity", "Back Pressed Called");
//        startActivity(new Intent(this, StockManagementActivity.class));
        startActivity(new Intent(this, StockAdjustmentPage.class));
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
