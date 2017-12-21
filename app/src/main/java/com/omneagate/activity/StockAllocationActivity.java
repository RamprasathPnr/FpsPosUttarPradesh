package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FpsAllocationCommodityDetailDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StockAllocationActivity extends BaseActivity implements View.OnClickListener {
    private TextView emptyview;
    Spinner txt_month_year;
    String TAG = "StockAllocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_stock_allocation);
        setupview();
        Calendar c = Calendar.getInstance();
        DateFormat formatter = new SimpleDateFormat("MMM");
        int year = c.get(Calendar.YEAR);
        List<FpsAllocationCommodityDetailDto> dtolist = FPSDBHelper.getInstance(this).get_stock_allocation_details(formatter.format(c.getTime()).toUpperCase(), year);
        loadTableValues(dtolist);
    }

    private void setupview() {
        setUpPopUpPage();
        TextView mTvTitle = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(mTvTitle, R.string.stock_allocation_title);
        ImageView mIvBack = (ImageView) findViewById(R.id.imageViewBack);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Button btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        emptyview = (TextView) findViewById(R.id.empty_view);
//        btn_sync = (Button) findViewById(R.id.btn_sync);

        txt_month_year = (Spinner) findViewById(R.id.txt_month_year);
        ArrayList<String> monthYear = getMonthYear();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthYear);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txt_month_year.setAdapter(dataAdapter);
        txt_month_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                String monthStr[] = item.split(" / ");
                List<FpsAllocationCommodityDetailDto> dtolist = FPSDBHelper.getInstance(StockAllocationActivity.this).get_stock_allocation_details(monthStr[0].toUpperCase(), Integer.valueOf(monthStr[1]));
                loadTableValues(dtolist);


//                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayList<String> getMonthYear() {
        Calendar cal1 =  Calendar.getInstance();
        cal1.add(Calendar.MONTH, -2);
        String previousBeforeMonth  = new SimpleDateFormat("MMM").format(cal1.getTime());
        String previousBeforeYear  = new SimpleDateFormat("yyyy").format(cal1.getTime());

        Calendar cal2 =  Calendar.getInstance();
        cal2.add(Calendar.MONTH, -1);
        String previousMonth  = new SimpleDateFormat("MMM").format(cal2.getTime());
        String previousYear  = new SimpleDateFormat("yyyy").format(cal2.getTime());

        Calendar cal3 = Calendar.getInstance();
        String currentMonth  = new SimpleDateFormat("MMM").format(cal3.getTime());
        String currentYear  = new SimpleDateFormat("yyyy").format(cal3.getTime());

        ArrayList<String> monthYearList = new ArrayList<>();
        monthYearList.add(currentMonth+" / "+currentYear);
        monthYearList.add(previousMonth+" / "+previousYear);
        monthYearList.add(previousBeforeMonth+" / "+previousBeforeYear);

        return monthYearList;
    }


    private void loadTableValues(List<FpsAllocationCommodityDetailDto> value) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout);
            transactionLayout.removeAllViews();
            LayoutInflater lin = LayoutInflater.from(this);
            int sno = 1;
            if (value.size() > 0) {
                for (int j = value.size() - 1; j >= 0; j--) {
                    transactionLayout.addView(returnView(lin, sno, value.get(j)));
                    sno++;
                }
                emptyview.setVisibility(View.GONE);
            } else
                showemptyview(getResources().getString(R.string.no_data));
        } catch (Exception e) {
            Log.e("OtherInspectionActivity", "loadTableValues exc..." + e);
        }
    }

    private View returnView(LayoutInflater entitle, final int sno, final FpsAllocationCommodityDetailDto dto) {
        View convertView = entitle.inflate(R.layout.adapter_stock_allocation, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView txt_commodity = (TextView) convertView.findViewById(R.id.txt_commodity);
        TextView txt_allocated_qty = (TextView) convertView.findViewById(R.id.txt_allocated_qty);
        TextView txt_uom = (TextView) convertView.findViewById(R.id.txt_uom);
        mTvSno.setText("" + sno);
        txt_commodity.setText("" + dto.getGroupDto().getGroupName());
        txt_allocated_qty.setText("" + dto.getAllocatedQty());
//        List<ProductDto> list = dto.getGroupDto().getProductDto();
//        if (list != null && list.size() > 0) {
            if (GlobalAppState.language.equals("ta"))
                txt_uom.setText("" +dto.getGroupDto().getLocalUnit());
            else
                txt_uom.setText("" + dto.getGroupDto().getUnit());
//        } else {
//            txt_uom.setText("-");
//        }
        return convertView;
    }

    private void showemptyview(String text) {
        emptyview.setVisibility(View.VISIBLE);
        emptyview.setText(text);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StockManagementActivity.class));
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }
}
