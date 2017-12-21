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
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.BillISearchAdapterDate;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for show the bills
 */
public class BillActivity extends BaseActivity {

    List<BillDto> bills;

    String data, searchType;

    LoadMoreListView billSearch;

    int loadMore = 0;

    BillISearchAdapterDate adapter;

    ProgressBar progressBarSpin;

    long id;

    int lv_position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        data = getIntent().getExtras().getString("bills");
        searchType = getIntent().getExtras().getString("search");
        progressBarSpin = (ProgressBar) findViewById(R.id.progressBar1);

        try {
            if (getIntent().getExtras().getString("lv_position") != null)
                lv_position = Integer.parseInt(getIntent().getExtras().getString("lv_position"));

        } catch (Exception e) {

        }

        Log.e("BillActivity ", "lv_position = "+lv_position);

        configureData(data);
    }

    /*Data from server has been set inside this function*/
    private void configureData(String data) {
        try {
            setUpPopUpPage();
            Util.LoggingQueue(this, "BillActivity", "Setting up main page");
            Log.e("data", ""+data);
            Log.e("searchType", ""+searchType);
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.bills);
            final long count = FPSDBHelper.getInstance(this).getAllUnsyncBills();
            Util.setTamilText((TextView) findViewById(R.id.unSyncBillCount), getString(R.string.unsyncBills)+"  "+count);
            Util.setTamilText((TextView) findViewById(R.id.fpsInvardactionLabel), R.string.billDetailTxnBill);
            Util.setTamilText((TextView) findViewById(R.id.fpsInvardoutwardDateLabel), R.string.billDetailAmountLabel);
            Util.setTamilText((TextView) findViewById(R.id.fpsInvardoutwardGodownNameLabel), R.string.billDetailProductPrice);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.close);
            billSearch = (LoadMoreListView) findViewById(R.id.listView_fps_stock_inward);
            Util.setTamilText((TextView) findViewById(R.id.tvViewStockHistory), R.string.unsyncBills);
            id = 0l;
            BeneficiaryDto bene;
            if(count>0){
                findViewById(R.id.unsyncCount).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(com.omneagate.activity.BillActivity.this, UnsyncBillActivity.class));
                        finish();
                    }
                });
            }
            if (searchType.equalsIgnoreCase("cardNumber")) {
                bene = FPSDBHelper.getInstance(this).beneficiaryFromOldCard(data.toUpperCase());
            } else if (searchType.equalsIgnoreCase("mobileNo")) {
                bene = FPSDBHelper.getInstance(this).retrieveIdFromBeneficiary(data);
            } else if (searchType.equalsIgnoreCase("aRegister")) {
                bene = FPSDBHelper.getInstance(this).retrieveIdFromBeneficiaryReg(Integer.parseInt(data));
            } else {
                bene = FPSDBHelper.getInstance(this).beneficiaryDto(data);
            }
            if(bene!=null){
                String cardNumber = "";
                if(StringUtils.isNotEmpty(bene.getOldRationNumber())){
                    cardNumber = bene.getOldRationNumber().toUpperCase();
                }

                if(StringUtils.isNotEmpty(bene.getAregisterNum())){
                    cardNumber =cardNumber+" / " +bene.getAregisterNum();
                }
                ((TextView)findViewById(R.id.reg_date_search)).setText(cardNumber);
            }
            if(bene.getId()!=null){
                id  = bene.getId();
            }
            bills = new ArrayList<>();
            billSearch = (LoadMoreListView) findViewById(R.id.listView_fps_stock_inward);
            adapter = new BillISearchAdapterDate(this, bills);
            billSearch.setAdapter(adapter);
            billSearch.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loadMore++;
                    new SearchBillsByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
                }
            });
            billSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position < bills.size()) {
                        lv_position = position;
                        submitBill(bills.get(position).getTransactionId());
                    }
                }
            });
            new SearchBillsByDateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("BillActivity", e.toString(), e);
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
        Util.LoggingQueue(this, "Bill search List", "On back pressed Called");
        finish();
    }

    private class SearchBillsByDateTask extends AsyncTask<Long, Void, List<BillDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<BillDto> doInBackground(final Long... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BillActivity.this).getAllBillById(args[0],loadMore);
        }

        // can use UI thread here
        protected void onPostExecute(final List<BillDto> billDtos) {

            progressBarSpin.setVisibility(View.GONE);

            if (billDtos.size() > 0) {
                bills.addAll(billDtos);
                billSearch.setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutNoRecords)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                billSearch.invalidate();
                billSearch.onLoadMoreComplete();
                billSearch.post(new Runnable() {
                    @Override
                    public void run() {
                        billSearch.setSelectionFromTop(lv_position, 0);
                    }
                });
            } else if (billDtos.size() == 0 && bills.size() == 0) {
                billSearch.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                billSearch.onLoadMoreComplete();
               /* billSearch.post(new Runnable() {
                    @Override
                    public void run() {
                        billSearch.setSelectionFromTop(lv_position, 0);
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
            return FPSDBHelper.getInstance(com.omneagate.activity.BillActivity.this).getBillByTransactionId(args[0]);
        }

        // can use UI thread here
        protected void onPostExecute(final BillDto billDtos) {
            progressBarSpin.setVisibility(View.GONE);
            Intent intent = new Intent(com.omneagate.activity.BillActivity.this, BillDetailActivity.class);
            intent.putExtra("billData", new Gson().toJson(billDtos));
            Util.LoggingQueue(com.omneagate.activity.BillActivity.this, "Bill search List", "Selected bill:" + new Gson().toJson(billDtos));
            intent.putExtra("className", "billActivity");
            intent.putExtra("data", data);
            intent.putExtra("search", searchType);
            intent.putExtra("lv_position",""+ lv_position);

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