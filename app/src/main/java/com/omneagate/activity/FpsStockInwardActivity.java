package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.InwardDataAdapterNew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FpsStockInwardActivity extends BaseActivity {

    int loadMore = 0;

    LoadMoreListView billByDate;

    InwardDataAdapterNew adapter;

    ProgressBar progressBarSpin;
    int lv_position = 0;

    //List of GodownStockDto for fps stock inward
    private List<GodownStockOutwardDto> fpsStockInwardList = null;

//    private String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fps_stock_inward);
//        Toast.makeText(getBaseContext(), "timestamp.."+System.(),Toast.LENGTH_SHORT).show();
//        timeStamp = String.valueOf(System.currentTimeMillis());
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        Util.LoggingQueue(this, "Stock Inward activity", "Main page Called"+lv_position);
        TextView topTv = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(topTv, R.string.inward_register);
        Util.setTamilText(((TextView) findViewById(R.id.btnClose)), R.string.close);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardDateLabel)), R.string.reference_no);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardactionLabel)), R.string.dispatch_date);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardGodownNameLabel)), R.string.gowdown_code);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardLapsedTimeabel)), R.string.lapsed_time);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardactionGodownStatusLabel)), R.string.status);
        Util.setTamilText(((TextView) findViewById(R.id.tvViewStockHistory)), R.string.stock_history_view);
        TextView tvViewStockHistory = (TextView) findViewById(R.id.tvViewStockHistory);

        tvViewStockHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.omneagate.activity.FpsStockInwardActivity.this, FpsStockInwardReceivedActivity.class));
                finish();
            }
        });




    }
    private void submitInward(int position) {

        Log.e("submitInward()", "lv_position = "+lv_position);
        Intent intent = new Intent(this, FpsStockInwardDetailActivity.class);
        intent.putExtra("godown", new Gson().toJson(fpsStockInwardList.get(position)));
        intent.putExtra("submitBoolean", true);
        intent.putExtra("lv_position", ""+lv_position);

        startActivity(intent);
        finish();
    }


    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            setUpPopUpPage();
            setTopTextView();
            fpsStockInwardList = new ArrayList<>();
            billByDate = (LoadMoreListView) findViewById(R.id.listView_fps_stock_inward);

            /*Collections.sort(fpsStockInwardList, new Comparator<GodownStockOutwardDto>(){
                public int compare(GodownStockOutwardDto o1, GodownStockOutwardDto o2){
                    return Long.valueOf(o1.getOutwardDate() - o2.getOutwardDate()).intValue();
                }
            });*/



            adapter = new InwardDataAdapterNew(this, fpsStockInwardList);
            billByDate.setAdapter(adapter);
            billByDate.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loadMore++;
                    new InwardByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            new InwardByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            billByDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position < fpsStockInwardList.size()) {
                        lv_position = position;
                        submitInward(position);
                    }
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
    }

    private class InwardByDateTask extends AsyncTask<String, Void, List<GodownStockOutwardDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<GodownStockOutwardDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.FpsStockInwardActivity.this).showFpsStockInvard(false, loadMore);
        }

        // can use UI thread here
        protected void onPostExecute(final List<GodownStockOutwardDto> billDtos) {
            progressBarSpin.setVisibility(View.GONE);

            if (billDtos.size() > 0) {
                fpsStockInwardList.addAll(billDtos);
                billByDate.setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutNoRecords)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                billByDate.invalidate();
                billByDate.onLoadMoreComplete();
                billByDate.post(new Runnable() {
                    @Override
                    public void run() {
                        billByDate.setSelectionFromTop(lv_position, 0);
                    }
                });
            } else if (billDtos.size() == 0 && fpsStockInwardList.size() == 0) {
                billByDate.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                billByDate.onLoadMoreComplete();
                /*billByDate.post(new Runnable() {
                    @Override
                    public void run() {
                        billByDate.setSelectionFromTop(lv_position, 0);
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
        startActivity(new Intent(this, StockManagementActivity.class));
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
