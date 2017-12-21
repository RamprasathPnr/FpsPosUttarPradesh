package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FpsAdvanceStockDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created AdvanceStockActvity
 */
public class AdvanceStockActvity extends BaseActivity {
    TextView noRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advancestock);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setUpPopUpPage();
        initialPage();
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void initialPage() {
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.advanced_stock_title);
        Util.setTamilText((TextView) findViewById(R.id.month), getString(R.string.month));
        Util.setTamilText((TextView) findViewById(R.id.year), getString(R.string.year));
        Util.setTamilText((TextView) findViewById(R.id.product), getString(R.string.product));
        Util.setTamilText((TextView) findViewById(R.id.unit), getString(R.string.unit));
        Util.setTamilText((TextView) findViewById(R.id.quantity), getString(R.string.stock));
        /*Util.setTamilText((TextView) findViewById(R.id.process), getString(R.string.added));
        Util.setTamilText((TextView) findViewById(R.id.sync), getString(R.string.synced));*/
        Util.setTamilText((TextView) findViewById(R.id.snumber), getString(R.string.sno));
        Util.setTamilText((TextView) findViewById(R.id.challanId), getString(R.string.reference_no));
        Util.setTamilText((TextView) findViewById(R.id.dispatchDate), getString(R.string.dispatch_date));
        Util.setTamilText((TextView) findViewById(R.id.godownCode), getString(R.string.gowdown_code));
        Util.setTamilText((TextView) findViewById(R.id.btnClose), getString(R.string.close));
        noRecord = (TextView) findViewById(R.id.tvNoRecords);
        noRecord.setVisibility(View.GONE);

        new fpsAdvancedStockTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void AdvanceStockList(List<FpsAdvanceStockDto> stocklist) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            LayoutInflater lin = LayoutInflater.from(com.omneagate.activity.AdvanceStockActvity.this);
            Log.e("stockcount", "" + stocklist.size());
            int sno = 1;
            for (FpsAdvanceStockDto advanceStockdto : stocklist) {
                transactionLayout.addView(returnView(lin, sno, advanceStockdto));
                sno++;
            }
        } catch (Exception e) {
            Log.e("AdvanceStockListError", e.toString(), e);
        }
    }

    private View returnView(LayoutInflater entitle, int sno, FpsAdvanceStockDto advanceStockData) {
        Log.e("advance stock activity","advanceStockData.."+advanceStockData);
        View convertView = entitle.inflate(R.layout.adpter_advancestock, new LinearLayout(this),false);
        TextView snoTv = (TextView) convertView.findViewById(R.id.sno);
        TextView monthtv = (TextView) convertView.findViewById(R.id.monthtv);
        TextView yeartv = (TextView) convertView.findViewById(R.id.yeartv);
        TextView producttv = (TextView) convertView.findViewById(R.id.producttv);
        TextView challanTv = (TextView) convertView.findViewById(R.id.challanIdTv);
        TextView quantitytv = (TextView) convertView.findViewById(R.id.quantitytv);
        TextView productUnit = (TextView) convertView.findViewById(R.id.fpsInvardDetailUnitId);
        TextView dispatchDate = (TextView) convertView.findViewById(R.id.dispatchDateTv);
        TextView godownCode = (TextView) convertView.findViewById(R.id.godownCodeTv);
        TextView processTv = (TextView) convertView.findViewById(R.id.processTv);
        TextView syncTv = (TextView) convertView.findViewById(R.id.syncTv);

        String[] monthArray = getResources().getStringArray(R.array.month_list);
        List<String> myResArrayList = Arrays.asList(monthArray);
        Util.setTamilText(monthtv, myResArrayList.get(advanceStockData.getMonth() - 1));
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        snoTv.setText(String.valueOf(sno));
        // monthtv.setText("" + myResArrayList.get(month - 1));
        yeartv.setText(String.valueOf(advanceStockData.getYear()));

        String qty = Util.quantityRoundOffFormat(advanceStockData.getFpsQuantity());
        quantitytv.setText("" + qty);
        ProductDto productDetail = FPSDBHelper.getInstance(this).getProductDetails(advanceStockData.getProductId());
        Log.i("Product", productDetail.toString());
        if (productDetail != null) {
            producttv.setText(productDetail.getName());
            if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(productDetail.getLocalProductUnit())) {
//                Util.setTamilText(producttv, productDetail.getLocalProductName());
                producttv.setText(productDetail.getLocalProductName());
            }

            productUnit.setText(productDetail.getProductUnit());
            if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(productDetail.getLocalProductUnit())) {
                Util.setTamilText(productUnit, productDetail.getLocalProductUnit());
            }

        }
        challanTv.setText(String.valueOf(advanceStockData.getGodownStockOutwardDto().getReferenceNo()));
        godownCode.setText(String.valueOf(advanceStockData.getGodownCode()));


        try {
            DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long milliSeconds= advanceStockData.getOutwardDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
//            System.out.println(formatter.format(calendar.getTime()));
            String date = formatter2.format(calendar.getTime());
            Date dateVal = formatter2.parse(date);
//            createdDate = dateVal.getTime();
            dispatchDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(dateVal.getTime()));
        }
        catch(Exception e) {
            Log.e("AdvanceStockActivity","outward date exc..."+e);
//            createdDate = new Date().getTime();
        }

        if (advanceStockData.getProcessStatus() == 1) {
            processTv.setText("X");
            processTv.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else if (advanceStockData.getProcessStatus() == 0) {
            processTv.setText("\u2714");
            processTv.setTypeface(Typeface.DEFAULT);
        }

        if (advanceStockData.getSyncStatus() == 0) {
            syncTv.setText("X");
            processTv.setTypeface(Typeface.DEFAULT);
        }
        else if (advanceStockData.getSyncStatus() == 1) {
            syncTv.setText("\u2714");
        }


        return convertView;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StockManagementActivity.class));
        finish();
    }

    public void onClose(View view) {
        onBackPressed();

    }


    private class fpsAdvancedStockTask extends AsyncTask<String, Void, List<FpsAdvanceStockDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.AdvanceStockActvity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<FpsAdvanceStockDto> doInBackground(final String... args) {
            try {
                return FPSDBHelper.getInstance(com.omneagate.activity.AdvanceStockActvity.this).getAdvanceStockList();
            } catch (Exception e) {
                Log.e("adStock AsncError", e.toString(), e);
                return null;
            }

        }

        // can use UI thread here
        protected void onPostExecute(final List<FpsAdvanceStockDto> result) {
            try {
                Log.e("Db_advanceStockList", "" + result.toString());
                if (progressBar != null) {
                    progressBar.dismiss();
                }
                if (result.size() != 0) {
                    AdvanceStockList(result);
                } else {
//                    Util.messageBar(com.omneagate.activity.AdvanceStockActvity.this, getString(R.string.no_records));
                    noRecord.setVisibility(View.VISIBLE);
                }
            }
            catch(Exception e) {
//                Util.messageBar(com.omneagate.activity.AdvanceStockActvity.this, getString(R.string.no_records));
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                        noRecord.setVisibility(View.VISIBLE);
                    }
                }
                catch(Exception e1) {}
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }

}
