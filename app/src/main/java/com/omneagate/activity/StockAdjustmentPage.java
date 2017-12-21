package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.AdjustmentDataAdapter;
import com.omneagate.activity.dialog.StockAdjustmentDialog;
import com.omneagate.activity.dialog.WrongDeviceDateDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockAdjustmentPage extends BaseActivity {

    int loadMore = 0;
    LoadMoreListView billByDate;
    AdjustmentDataAdapter adapter;
//    ProgressBar progressBarSpin;
    private List<POSStockAdjustmentDto> fpsStockAdjustmentList = null;
//    TextView noRecords;
    RelativeLayout noRecords;
    StockAdjustmentDialog stockAdjustmentDialog;
    WrongDeviceDateDialog wrongDeviceDateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.page_stock_adjustment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        configureData();
    }

    private void setTopTextView() {
//        progressBarSpin = (ProgressBar) findViewById(R.id.progressBar1);
        Util.LoggingQueue(this, "StockAdjustmentPage ", "Main page Called");
        TextView topTv = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(topTv, R.string.stock_adjust);
        Util.setTamilText(((TextView) findViewById(R.id.btnClose)), R.string.submit);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardDateLabel)), R.string.reference_no);
        Util.setTamilText(((TextView) findViewById(R.id.godownRefNoLabel)), R.string.godown_reference_no);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardactionLabel)), R.string.dispatch_date);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardGodownNameLabel)), R.string.commodity);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardoutwardLapsedTimeabel)), R.string.fpsInvardDetailQuantity);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardactionGodownStatusLabel)), R.string.adjustment_type);
        Util.setTamilText(((TextView) findViewById(R.id.tvViewStockHistory)), R.string.stock_adjustment_history_view);
//        noRecords = (TextView) findViewById(R.id.tvNoRecords);
        TextView tvViewStockHistory = (TextView) findViewById(R.id.tvViewStockHistory);
        tvViewStockHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.omneagate.activity.StockAdjustmentPage.this, FpsStockAdjustmentListActivity.class));
                finish();
            }
        });



        TextView submitButton = (TextView) findViewById(R.id.btnClose);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String maxBillDate = FPSDBHelper.getInstance(StockAdjustmentPage.this).getMaxBillDate();
                    if ((maxBillDate == null) || (maxBillDate.equalsIgnoreCase("")) || (maxBillDate.equalsIgnoreCase("null"))) {
                        processAdjustment();
                    } else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String currentDeviceDate = simpleDateFormat.format(new Date());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date d1 = sdf.parse(maxBillDate);
                        Date d2 = sdf.parse(currentDeviceDate);
                        if (d1.compareTo(d2) > 0) {
                            FPSDBHelper.getInstance(StockAdjustmentPage.this).insertInvalidDateException("fps_stock_adjustment", "Adjustment_Ack", Util.ackAdjustmentList.toString(), "MaxBillDate = " + maxBillDate + "\n" + "DeviceDate = "+currentDeviceDate);
                            wrongDeviceDateDialog = new WrongDeviceDateDialog(StockAdjustmentPage.this);
                            wrongDeviceDateDialog.show();
                        }
                        else {
                            processAdjustment();
                        }
                    }
                }
                catch(Exception e) {
                    processAdjustment();
                }
            }
        });
    }

    private void processAdjustment() {
        if (Util.ackAdjustmentList.size() == 0) {
            Util.messageBar(StockAdjustmentPage.this, getString(R.string.ack_alert));
        }
        else {
            findViewById(R.id.btnClose).setOnClickListener(null);
            findViewById(R.id.btnClose).setBackgroundColor(Color.LTGRAY);
            FPSDBHelper.getInstance(StockAdjustmentPage.this).stockAdjustmentData(Util.ackAdjustmentList);
            stockAdjustmentDialog = new StockAdjustmentDialog(StockAdjustmentPage.this);
            stockAdjustmentDialog.show();
        }
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            setUpPopUpPage();
            setTopTextView();

            noRecords = (RelativeLayout) findViewById(R.id.linearLayoutNoRecords);
            Util.ackAdjustmentList = new ArrayList<POSStockAdjustmentDto>();
            fpsStockAdjustmentList = new ArrayList<>();
            fpsStockAdjustmentList = FPSDBHelper.getInstance(com.omneagate.activity.StockAdjustmentPage.this).showFpsStockAdjustment(false, loadMore);
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_fps_stock_inward_detail);
            Log.i("Detail", fpsStockAdjustmentList.toString());
            fpsInwardLinearLayout.removeAllViews();
            for (int position = 0; position < fpsStockAdjustmentList.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(StockAdjustmentPage.this);
                fpsInwardLinearLayout.addView(returnView(lin, fpsStockAdjustmentList.get(position), position));
            }
            if(fpsStockAdjustmentList.size() == 0) {
                noRecords.setVisibility(View.VISIBLE);
                findViewById(R.id.btnClose).setOnClickListener(null);
                findViewById(R.id.btnClose).setBackgroundColor(Color.LTGRAY);
            }
            else {
                noRecords.setVisibility(View.INVISIBLE);
            }










//            billByDate = (LoadMoreListView) findViewById(R.id.listView_fps_stock_adjustment);

//            Util.ackAdjustmentList.clear();
            /*adapter = new AdjustmentDataAdapter(this, fpsStockAdjustmentList);
            billByDate.setAdapter(adapter);
            billByDate.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    loadMore++;
                    new AdjustmentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            new AdjustmentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            billByDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private View returnView(LayoutInflater entitle, final POSStockAdjustmentDto data, final int itemPosition) {
        View convertView = entitle.inflate(R.layout.stock_adjustment_adapter, null);
        TextView sNo = (TextView) convertView.findViewById(R.id.tvSerialNo);
        TextView referenceNo = (TextView) convertView.findViewById(R.id.tvDeliveryChellanId);
        TextView godownReferenceNo = (TextView) convertView.findViewById(R.id.godownOutwardRefNo);
        TextView dispatchDate = (TextView) convertView.findViewById(R.id.tvOutwardDate);
        TextView commodity = (TextView) convertView.findViewById(R.id.tvGodownName);
        TextView quantity = (TextView) convertView.findViewById(R.id.tvLapsedTime);
        TextView adjustmentType = (TextView) convertView.findViewById(R.id.btnStatus);
        CheckBox acknowledgeCbox = (CheckBox) convertView.findViewById(R.id.fpsAdjustmentAcknowledge);


        String productName = FPSDBHelper.getInstance(StockAdjustmentPage.this).getProductName(data.getProductId());
        sNo.setText(String.valueOf(itemPosition + 1));
        referenceNo.setText(String.valueOf(data.getReferenceNumber()));
        godownReferenceNo.setText(data.getGodownStockOutwardReferenceNumber());
        Log.e("Stock adjustment page","created date"+data.getCreatedDate());
        dispatchDate.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(data.getCreatedDate()));
        commodity.setText(productName);

            /** 11-07-2016
             * MSFixes
             * Added to fix quantity to round off to 3 digits after decimal
             *
             */

        String qty_str = ""+Util.quantityRoundOffFormat(data.getQuantity());
        quantity.setText(qty_str);

        if(data.getRequestType().equalsIgnoreCase("STOCK_INCREMENT")) {
            Util.setTamilText(adjustmentType, R.string.stock_increment);
        }
        else if(data.getRequestType().equalsIgnoreCase("STOCK_DECREMENT")) {
            Util.setTamilText(adjustmentType, R.string.stock_decrement);
        }
        acknowledgeCbox.setId(itemPosition);
        acknowledgeCbox.setChecked(false);

        acknowledgeCbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if (isChecked) {
//                        Toast.makeText(StockAdjustmentPage.this, "checked.."+itemPosition, Toast.LENGTH_LONG).show();
                      if (!Util.ackAdjustmentList.contains(data)) {
                          Util.ackAdjustmentList.add(data);
                      }
                  } else {
//                        Toast.makeText(StockAdjustmentPage.this, "unchecked.."+itemPosition, Toast.LENGTH_LONG).show();
                      if (Util.ackAdjustmentList.contains(data)) {
                          Util.ackAdjustmentList.remove(data);
                      }
                  }
              }
          }
        );

        return convertView;
    }












    /*private class AdjustmentTask extends AsyncTask<String, Void, List<POSStockAdjustmentDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            progressBarSpin.setVisibility(View.VISIBLE);
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<POSStockAdjustmentDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.StockAdjustmentPage.this).showFpsStockAdjustment(false, loadMore);
        }

        // can use UI thread here
        protected void onPostExecute(final List<POSStockAdjustmentDto> billDtos) {
            progressBarSpin.setVisibility(View.GONE);
            if (billDtos.size() > 0) {
                fpsStockAdjustmentList.addAll(billDtos);






                billByDate.setVisibility(View.VISIBLE);
                (findViewById(R.id.linearLayoutNoRecords)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                billByDate.invalidate();
                billByDate.onLoadMoreComplete();
            } else if (billDtos.size() == 0 && fpsStockAdjustmentList.size() == 0) {
                billByDate.setVisibility(View.GONE);
                findViewById(R.id.linearLayoutNoRecords).setVisibility(View.VISIBLE);
            } else {
                billByDate.onLoadMoreComplete();
            }
        }
    }*/


    public void onClose(View view) {
        onBackPressed();

    }

    @Override
    public void onBackPressed() {
        Util.LoggingQueue(this, "Stock adjustment activity", "Back Pressed Called");
        startActivity(new Intent(this, StockManagementActivity.class));
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if ((stockAdjustmentDialog != null) && stockAdjustmentDialog.isShowing()) {
                stockAdjustmentDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            stockAdjustmentDialog = null;
        }

        try {
            if ((wrongDeviceDateDialog != null) && wrongDeviceDateDialog.isShowing()) {
                wrongDeviceDateDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            wrongDeviceDateDialog = null;
        }
    }

}
