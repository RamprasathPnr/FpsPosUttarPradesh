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

import com.google.gson.Gson;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.BillISearchAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for Bill By DateActivity
 */
public class BillByDateActivity extends BaseActivity {

    List<BillDto> bills;

    int loadMore = 0;

    String data;

    LoadMoreListView billByDate;

    ProgressBar progressBarSpin;

    BillISearchAdapter adapter;

    int lv_position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_date);
        data = getIntent().getExtras().getString("bills");
        progressBarSpin = (ProgressBar) findViewById(R.id.progressBar1);
        try {
            if (getIntent().getExtras().getString("lv_position") != null)
                lv_position = Integer.parseInt(getIntent().getExtras().getString("lv_position"));

        } catch (Exception e) {

        }

       // Log.e("BillByDateActivity ", "lv_position = "+lv_position);

        configureData();
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            setUpPopUpPage();
            Util.LoggingQueue(this, "BillByDateActivity", "Setting up main page");
            long count = FPSDBHelper.getInstance(this).getAllUnsyncBills();
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.bills);
            Util.setTamilText((TextView) findViewById(R.id.unSyncBillCount), getString(R.string.unsyncBills) + "  " + count);
            Util.setTamilText((TextView) findViewById(R.id.fpsInvardactionLabel), R.string.billDetailTxnBill);
            Util.setTamilText((TextView) findViewById(R.id.fpsInvardoutwardDateLabel), R.string.ration_card_number1);
            Util.setTamilText((TextView) findViewById(R.id.fpsInvardoutwardGodownNameLabel), R.string.billDetailProductPrice);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.close);
            Util.setTamilText((TextView) findViewById(R.id.tvViewStockHistory), R.string.unsyncBills);
            Util.setTamilText((TextView) findViewById(R.id.reg_date_search), data);


            bills = new ArrayList<>();
            if(count>0){
                findViewById(R.id.unsyncCount).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(com.omneagate.activity.BillByDateActivity.this,UnsyncBillActivity.class));
                        finish();
                    }
                });
            }
            billByDate = (LoadMoreListView) findViewById(R.id.listView_fps_stock_inward);
            adapter = new BillISearchAdapter(this, bills);
            billByDate.setAdapter(adapter);
            new SearchBillsByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
            billByDate.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loadMore++;
                    new SearchBillsByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
                }
            });
            billByDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    lv_position = position;
                    submitBill(bills.get(position).getTransactionId());
                }
            });
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        } finally {
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void submitBill(String transactionId) {
        new SearchBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, transactionId);
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


    public void onClose(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, BillSearchActivity.class));
        Util.LoggingQueue(this, "Bill search List", "Back pressed Called");
        finish();
    }

    private class SearchBillsByDateTask extends AsyncTask<String, Void, List<BillDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<BillDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BillByDateActivity.this).getAllBillByDate(args[0],loadMore);
        }

        // can use UI thread here
        protected void onPostExecute(final List<BillDto> billDtos) {
            progressBarSpin.setVisibility(View.GONE);

            if (billDtos.size() > 0) {
                bills.addAll(billDtos);
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
            } else if (billDtos.size() == 0 && bills.size() == 0) {
                billByDate.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                billByDate.onLoadMoreComplete();
               /* billByDate.post(new Runnable() {
                    @Override
                    public void run() {
                        billByDate.setSelectionFromTop(lv_position, 0);
                    }
                });*/
            }
        }
    }


    private class SearchBillTask extends AsyncTask<String, Void, BillDto> {

        // can use UI thread here
        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        // automatically done on worker thread (separate from UI thread)
        protected BillDto doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BillByDateActivity.this).getBillByTransactionId(args[0]);
        }

        // can use UI thread here
        protected void onPostExecute(final BillDto billDtos) {
            progressBarSpin.setVisibility(View.GONE);
            Intent intent = new Intent(com.omneagate.activity.BillByDateActivity.this, BillDetailActivity.class);
            intent.putExtra("billData", new Gson().toJson(billDtos));
            Util.LoggingQueue(com.omneagate.activity.BillByDateActivity.this, "Bill search List", "Selected bill:" + new Gson().toJson(billDtos));
            intent.putExtra("className", "billDateActivity");
            intent.putExtra("data", data);
            intent.putExtra("search", "date");
            intent.putExtra("lv_position", ""+lv_position);

            startActivity(intent);
            finish();

        }
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