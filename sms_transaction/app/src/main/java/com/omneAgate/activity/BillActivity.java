package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;

import java.util.List;

/**
 * Created by user1 on 27/3/15.
 */
public class BillActivity extends BaseActivity {

    // list with Bill details to show in the listview
    List<BillDto> billList;

    //Load more ListView
    LoadMoreListView loadMore;

    //Billview adapter
    BillViewAdapter billViewAdapter;

    //Limit
    int limit = 15;

    //Count
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        billList = FPSDBHelper.getInstance(this).getAllBillDetailsMonth(limit, count);

        loadMore = (LoadMoreListView) findViewById(R.id.pullRefresh);
        billViewAdapter = new BillViewAdapter(this, billList);
        loadMore.setAdapter(billViewAdapter);
        loadMore.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            public void onLoadMore() {
                new LoadDataTask().execute();
            }
        });

        loadMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(BillActivity.this, BillDetailActivity.class);
               /* i.putExtra("billId", billList.get(position).getBillRefId());*/
                i.putExtra("billId", billList.get(position).getBillLocalRefId());
                startActivity(new Intent(i));
                finish();
            }
        });
    }

    private class LoadDataTask extends AsyncTask<Void, Void, List<BillDto>> {
        @Override
        protected List<BillDto> doInBackground(Void... params) {
            if (isCancelled()) {
                return null;
            }
            count++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            return FPSDBHelper.getInstance(BillActivity.this).getAllBillDetailsMonth(limit, count);
        }

        @Override
        protected void onPostExecute(List<BillDto> result) {
            billList.addAll(result);
            billViewAdapter.notifyDataSetChanged();
            loadMore.onLoadMoreComplete();
        }

        @Override
        protected void onCancelled() {
            loadMore.onLoadMoreComplete();
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }
}