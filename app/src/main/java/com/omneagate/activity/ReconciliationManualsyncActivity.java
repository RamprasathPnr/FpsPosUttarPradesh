package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.Util.BillSyncManually;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.AdjustmentSyncManually;
import com.omneagate.Util.InwardSyncManually;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReconciliationManualsyncActivity extends BaseActivity implements View.OnClickListener {

    public Button btn_bills;
    Button btn_inwards;
    Button btn_adjust;
    Button btn_continue;
    Button btn_sync;
    Button btn_reconciliation_history;
    private TextView emptyview, mTvTitle;
    private ImageView mIvBack;
    private int billcount, inwardcount, adjustcount;
    private int btn_focus;
    NetworkConnection network = new NetworkConnection(this);
    private LinearLayout linear_header;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_reconciliation_manualsync);
        setupview();
        setbtntext();
        setUpPopUpPage();
        btn_focus = R.id.btn_unsync_bills;
        setbtn_focus(btn_bills);
        getunsync_bills();
        btn_bills.setOnClickListener(this);
        btn_inwards.setOnClickListener(this);
        btn_adjust.setOnClickListener(this);

    }

    private void setupview() {
        mTvTitle = (TextView) findViewById(R.id.top_textView);
        mTvTitle.setText(getResources().getString(R.string.reconciliation));
        mIvBack = (ImageView) findViewById(R.id.imageViewBack);
        mIvBack.setOnClickListener(this);
        btn_bills = (Button) findViewById(R.id.btn_unsync_bills);
        btn_inwards = (Button) findViewById(R.id.btn_unsync_inward);
        btn_adjust = (Button) findViewById(R.id.btn_unsync_adjust);
        btn_continue = (Button) findViewById(R.id.btn_reconcil_continue);
        btn_reconciliation_history = (Button) findViewById(R.id.btn_reconcil_history);
        btn_sync = (Button) findViewById(R.id.btn_reconcil_sync);
        emptyview = (TextView) findViewById(R.id.empty_view);
        linear_header = (LinearLayout) findViewById(R.id.linear_header);
    }

    private void setbtn_focus(Button btn) {
        Button[] button = {btn_bills, btn_inwards, btn_adjust};
        for (int i = 0; i < button.length; i++) {
            if (button[i].getId() == btn.getId()) {
                button[i].setTextColor(getResources().getColor(R.color.white));
                button[i].setBackground(getResources().getDrawable(R.drawable.buttoncornerradius));
            } else {
                button[i].setTextColor(getResources().getColor(R.color.transitButtonColor));
                button[i].setBackground(null);
            }
        }
    }

    public void setbtntext() {
        billcount = FPSDBHelper.getInstance(this).getBillUnSyncCount();
        inwardcount = FPSDBHelper.getInstance(this).getInwardUnSyncCount();
        adjustcount = FPSDBHelper.getInstance(this).getAdjustmentUnSyncCount();
        btn_bills.setText(getResources().getString(R.string.unsyncBills) + " (" + billcount + ")");
        btn_inwards.setText(getResources().getString(R.string.unsyncedInward) + " (" + inwardcount + ")");
        btn_adjust.setText(getResources().getString(R.string.unsyncAdjustment) + " (" + adjustcount + ")");
        if (billcount == 0 && inwardcount == 0 && adjustcount == 0) {
            btn_continue.setBackgroundColor(getResources().getColor(R.color.cpb_blue));
            btn_continue.setOnClickListener(this);
        }
        btn_reconciliation_history.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_unsync_bills || v.getId() == R.id.btn_unsync_inward || v.getId() == R.id.btn_unsync_adjust) {
            btn_bills.setBackground(null);
            btn_inwards.setBackground(null);
            btn_adjust.setBackground(null);
            btn_focus = v.getId();
            setbtn_focus((Button) v);
        }
        switch (v.getId()) {
            case R.id.btn_unsync_bills:
                getunsync_bills();
                break;
            case R.id.btn_unsync_inward:
                getunsync_Inwards();
                break;
            case R.id.btn_unsync_adjust:
                getunsync_Adjust();
                break;
            case R.id.btn_reconcil_sync:
                if (network.isNetworkAvailable())
                    sync_now();
                else
                    Toast.makeText(this, getResources().getString(R.string.noNetworkConnection), Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.btn_reconcil_continue:
                navigateToReconciliation();
                break;
            case R.id.btn_reconcil_history:
                navigateToReconciliationHistory();
                break;
        }
    }

    private void navigateToReconciliation() {
        startActivity(new Intent(this, ReconciliationActivity.class));
//        finish();
    }

    private void navigateToReconciliationHistory() {
        startActivity(new Intent(this, ReconciliationHistoryActivity.class));
//        finish();
    }

    private void sync_now() {
        switch (btn_focus) {
            case R.id.btn_unsync_bills:
                new BillSyncManually(this).billSync();
                break;
            case R.id.btn_unsync_inward:
                new InwardSyncManually(this).inwardSync();
                break;
            case R.id.btn_unsync_adjust:
                new AdjustmentSyncManually(this).adjustSync();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void getunsync_Adjust() {
        List<POSStockAdjustmentDto> dto_list = FPSDBHelper.getInstance(this).stockAdjustmentDataToServer();
        loadAdjustTableValues(dto_list);
    }

    public void getunsync_bills() {
        List<BillDto> dto_list = FPSDBHelper.getInstance(this).getAllBillsForSync();
        loadBillTableValues(dto_list);
    }

    private void showemptyview(String text) {
        emptyview.setVisibility(View.VISIBLE);
        emptyview.setText(text);
        btn_sync.setBackgroundColor(getResources().getColor(R.color.lightgray));
        btn_sync.setOnClickListener(null);
    }

    public void getunsync_Inwards() {
        List<GodownStockOutwardDto> dto_list = FPSDBHelper.getInstance(this).getAllInwardSync();
        loadInwardTableValues(dto_list);
    }

    private void loadBillTableValues(List<BillDto> value) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout);
            transactionLayout.removeAllViews();
            LayoutInflater lin = LayoutInflater.from(this);
            int sno = 1;
            linear_header.removeAllViews();
            LayoutInflater header = LayoutInflater.from(this);
            View convertView = header.inflate(R.layout.adapter_reconciliation_bill_header, new LinearLayout(this), false);
            linear_header.addView(convertView);
            if (value.size() > 0) {
                setSyncbtn();
                emptyview.setVisibility(View.GONE);
                for (int j = value.size() - 1; j >= 0; j--) {
                    transactionLayout.addView(returnBillView(lin, sno, value.get(j)));
                    sno++;
                }
            } else
                showemptyview(getResources().getString(R.string.no_bills));
        } catch (Exception e) {
            Log.e("OtherInspectionActivity", "loadTableValues exc..." + e);
        }
    }

    private void setSyncbtn() {
        btn_sync.setBackground(getResources().getDrawable(R.drawable.buttoncornerradius_green));
        btn_sync.setOnClickListener(this);
    }

    private View returnBillView(LayoutInflater entitle, final int sno, final BillDto dto) {
        View convertView = entitle.inflate(R.layout.adapter_reconciliation_bill, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView trans_id = (TextView) convertView.findViewById(R.id.txt_transaction_id);
        TextView trans_date = (TextView) convertView.findViewById(R.id.txt_transaction_date);
        TextView trans_amount = (TextView) convertView.findViewById(R.id.txt_transaction_amount);
        mTvSno.setText("" + sno);
        trans_id.setText("" + dto.getTransactionId());
        trans_date.setText("" + dto.getBillDate());
        trans_amount.setText("" + dto.getAmount());
        return convertView;
    }

    private void loadInwardTableValues(List<GodownStockOutwardDto> value) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout);
            transactionLayout.removeAllViews();
            LayoutInflater lin = LayoutInflater.from(this);
            int sno = 1;
            linear_header.removeAllViews();
            LayoutInflater header = LayoutInflater.from(this);
            View convertView = header.inflate(R.layout.adapter_reconciliation_inward_header, new LinearLayout(this), false);
            linear_header.addView(convertView);
            if (value.size() > 0) {
                setSyncbtn();
                emptyview.setVisibility(View.GONE);
                for (int j = value.size() - 1; j >= 0; j--) {
                    transactionLayout.addView(returnInwardView(lin, sno, value.get(j)));
                    sno++;
                }
            } else
                showemptyview(getResources().getString(R.string.no_inwards));
        } catch (Exception e) {
        }
    }

    private View returnInwardView(LayoutInflater entitle, final int sno, final GodownStockOutwardDto dto) {
        View convertView = entitle.inflate(R.layout.adapter_reconciliation_inward, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView reference_no = (TextView) convertView.findViewById(R.id.txt_reference_no);
        TextView dispatch_date = (TextView) convertView.findViewById(R.id.txt_dispatch_date);
        TextView godown_code = (TextView) convertView.findViewById(R.id.txt_godowncode);
        TextView elapsed_time = (TextView) convertView.findViewById(R.id.txt_elapsed_time);
        mTvSno.setText("" + sno);
        reference_no.setText("" + dto.getReferenceNo());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedOutwardDate = df.format(dto.getOutwardDate());
        dispatch_date.setText("" + formattedOutwardDate);
        godown_code.setText("" + dto.getGodownCode());
        String lapsedString = lapsedTimeAndDay(dto.getOutwardDate());
        elapsed_time.setText("" + lapsedString);
        return convertView;
    }

    private void loadAdjustTableValues(List<POSStockAdjustmentDto> value) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout);
            transactionLayout.removeAllViews();
            LayoutInflater lin = LayoutInflater.from(this);
            int sno = 1;
            linear_header.removeAllViews();
            LayoutInflater header = LayoutInflater.from(this);
            View convertView = header.inflate(R.layout.adapter_reconciliation_adjust_header, new LinearLayout(this), false);
            linear_header.addView(convertView);
            if (value.size() > 0) {
                setSyncbtn();
                emptyview.setVisibility(View.GONE);
                for (int j = value.size() - 1; j >= 0; j--) {
                    transactionLayout.addView(returnAdjustView(lin, sno, value.get(j)));
                    sno++;
                }
            } else
                showemptyview(getResources().getString(R.string.no_adjust));
        } catch (Exception e) {
        }
    }

    private View returnAdjustView(LayoutInflater entitle, final int sno, final POSStockAdjustmentDto dto) {
        View convertView = entitle.inflate(R.layout.adapter_reconciliation_adjust, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView reference_no = (TextView) convertView.findViewById(R.id.txt_reference_no);
        TextView dispatch_date = (TextView) convertView.findViewById(R.id.txt_dispatch_date);
        TextView godown_code = (TextView) convertView.findViewById(R.id.txt_godowncode);
        TextView commodity = (TextView) convertView.findViewById(R.id.txt_commodity);
        TextView quantity = (TextView) convertView.findViewById(R.id.txt_quantity);
        TextView adjust_type = (TextView) convertView.findViewById(R.id.txt_adjust_type);
        mTvSno.setText("" + sno);
        reference_no.setText("" + dto.getReferenceNumber());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedOutwardDate = df.format(dto.getCreatedDate());
        dispatch_date.setText(formattedOutwardDate);
        if((dto.getGodownStockOutwardReferenceNumber() != null) && (!dto.getGodownStockOutwardReferenceNumber().equalsIgnoreCase("null"))) {
            godown_code.setText("" + dto.getGodownStockOutwardReferenceNumber());
        }
        if (dto.getProductId() != 0)
            commodity.setText("" + FPSDBHelper.getInstance(this).getProductName(dto.getProductId()));
        quantity.setText("" + dto.getQuantity());
        if(dto.getRequestType().equalsIgnoreCase("STOCK_INCREMENT")) {
            adjust_type.setText(getResources().getString(R.string.stock_increment));
        }
        else if(dto.getRequestType().equalsIgnoreCase("STOCK_DECREMENT")) {
            adjust_type.setText(getResources().getString(R.string.stock_decrement));
        }

        return convertView;
    }

    private String lapsedTimeAndDay(long outwardDateAndTime) {
        long difference = System.currentTimeMillis() - outwardDateAndTime;
        if (difference < 0) {
            difference = 1;
        }
        long differenceInMins = difference / 60000;
        long minutes = differenceInMins % 60;
        differenceInMins /= 60;
        long hours = differenceInMins % 24;
        differenceInMins /= 24;
        long days = differenceInMins;
        String dateTimeData = "";
        if (days > 0) {
            if (days > 1) {
                dateTimeData = days + " " + getString(R.string.days) + " ";
            } else {
                dateTimeData = days + " " + getString(R.string.day) + " ";
            }
            if (hours > 0) {
                if (hours > 1) {
                    dateTimeData = dateTimeData + hours + " " + getString(R.string.hrs) + " ";
                } else {
                    dateTimeData = dateTimeData + hours + " " + getString(R.string.hr) + " ";
                }
            } else {
                if (minutes > 1) {
                    dateTimeData = dateTimeData + minutes + " " + getString(R.string.mins);
                } else {
                    dateTimeData = dateTimeData + minutes + " " + getString(R.string.min);
                }
            }
        } else if (hours > 0) {
            if (hours > 1) {
                dateTimeData = dateTimeData + hours + " " + getString(R.string.hrs) + " ";
            } else {
                dateTimeData = dateTimeData + hours + " " + getString(R.string.hr) + " ";
            }
            if (minutes > 0) {
                if (minutes > 1) {
                    dateTimeData = dateTimeData + minutes + " " + getString(R.string.mins);
                } else {
                    dateTimeData = dateTimeData + minutes + " " + getString(R.string.min);
                }
            }
        } else {
            if (minutes > 1) {
                dateTimeData = dateTimeData + minutes + " " + getString(R.string.mins);
            } else {
                dateTimeData = dateTimeData + minutes + " " + getString(R.string.min);
            }
        }
        return dateTimeData;
    }
}