package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omneagate.DTO.BillInbetweenDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.BillInbetweenSearchAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created for Bill By DateActivity
 */
public class BillByFromToDateActivity extends BaseActivity {

    List<BillInbetweenDto> bills;

    int loadMore = 0;

    String data1, data2;

    LoadMoreListView billByDate;

    ProgressBar progressBarSpin;

    BillInbetweenSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_from_to_date);
        data1 = getIntent().getExtras().getString("fromBills");
        data2 = getIntent().getExtras().getString("toBills");
        progressBarSpin = (ProgressBar) findViewById(R.id.progressBar1);
        configureData();
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            setUpPopUpPage();
            Util.LoggingQueue(this, "BillByFromToDateActivity", "Setting up main page");
            long count = FPSDBHelper.getInstance(this).getAllUnsyncBills();
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.monthly_sales_summary);
//            Util.setTamilText((TextView) findViewById(R.id.unSyncBillCount), getString(R.string.unsyncBills) + "  " + count);
            Util.setTamilText((TextView) findViewById(R.id.comodityLabel), R.string.product);
            Util.setTamilText((TextView) findViewById(R.id.unit), R.string.unit);
            Util.setTamilText((TextView) findViewById(R.id.quantityLabel), R.string.sale_quantity);
            Util.setTamilText((TextView) findViewById(R.id.amountLabel), R.string.amount_sold);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.close);
            Util.setTamilText((TextView) findViewById(R.id.totalAmountLabel), R.string.total_amount);
            Util.setTamilText((TextView) findViewById(R.id.totalTransLabel), R.string.total_trans);
            int totalTransVal = FPSDBHelper.getInstance(this).getTotalTransFromTo(data1, data2);
            Util.setTamilText((TextView) findViewById(R.id.totalTransValueTv), String.valueOf(totalTransVal));
            Double totalAmountVal = FPSDBHelper.getInstance(this).getTotalAmountFromTo(data1, data2);
            /*NumberFormat formatter = new DecimalFormat("#0.00");
            formatter.setRoundingMode(RoundingMode.CEILING);*/
            String amt = Util.priceRoundOffFormat(totalAmountVal);
            Util.setTamilText((TextView) findViewById(R.id.totalAmountValueTv), amt);
            TextView summaryDates = (TextView) findViewById(R.id.summaryDatesTv);

            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");

            Date date1 = originalFormat.parse(data1);
            String fromDate = targetFormat.format(date1);

            Date date2 = originalFormat.parse(data2);
            String toDate = targetFormat.format(date2);

            summaryDates.setText(fromDate+" <-> "+toDate);
            /*Util.setTamilText((TextView) findViewById(R.id.tvViewStockHistory), R.string.unsyncBills);
            Util.setTamilText((TextView) findViewById(R.id.reg_date_search), data);*/


            bills = new ArrayList<>();
            /*if(count>0){
                findViewById(R.id.unsyncCount).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(com.omneagate.activity.BillByFromToDateActivity.this,UnsyncBillActivity.class));
                        finish();
                    }
                });
            }*/


            billByDate = (LoadMoreListView) findViewById(R.id.listView_fps_stock_inward);
            adapter = new BillInbetweenSearchAdapter(this, bills);
            billByDate.setAdapter(adapter);
            new SearchBillsByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data1, data2);
            billByDate.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loadMore++;
                    new SearchBillsByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data1, data2);
                }
            });
            billByDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    submitBill(bills.get(position).getTransactionId());
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
//        new SearchBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, transactionId);
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


    public void onClose(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MonthlySalesReportActivity.class));
        Util.LoggingQueue(this, "Bill search List", "Back pressed Called");
        finish();
    }

    private class SearchBillsByDateTask extends AsyncTask<String, Void, List<BillInbetweenDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<BillInbetweenDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BillByFromToDateActivity.this).getAllBillByFromToDate(args[0], args[1], loadMore);
        }

        // can use UI thread here
        protected void onPostExecute(final List<BillInbetweenDto> billDtos) {
            progressBarSpin.setVisibility(View.GONE);

            if (billDtos.size() > 0) {
               // Log.e("SearchBillsByDateTaskbillDtos", ""+billDtos);
                bills.clear();
                bills.addAll(billDtos);
                billByDate.setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutNoRecords)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                billByDate.invalidate();
                billByDate.onLoadMoreComplete();
            } else if (billDtos.size() == 0 && bills.size() == 0) {
                billByDate.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                billByDate.onLoadMoreComplete();
            }
        }
    }


    /*private class SearchBillTask extends AsyncTask<String, Void, BillDto> {

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
            startActivity(intent);
            finish();

        }
    }*/

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