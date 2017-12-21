package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omneagate.DTO.EnumDTO.FpsStockInwardSelect;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdvancedStockConfirmActivity extends BaseActivity {

    TextView chellanIdTv;

    //Acknowledgement Date Textview
    TextView deliverdDateTv;

    // Batch no Textview
    TextView vehicleNoTv;

    // Godown Dto list
    List<GodownStockOutwardDto> fpsStockInwardDetailList;


    //adpter for spinner field
    ArrayAdapter<String> adapter;

    FpsStockInwardSelect fpsStockInward_Select;

    String fpsStockInward;

    GodownStockOutwardDto godownStockOutwardDtos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps_stock_inward_detail);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        fpsStockInward = getIntent().getExtras().getString("stockInwardList");
        fpsStockInward_Select = new Gson().fromJson(fpsStockInward, FpsStockInwardSelect.class);
        fpsStockInwardDetailList = fpsStockInward_Select.getFpsStockInwardconformList();
        chellanIdTv = (TextView) findViewById(R.id.tvChallan);
        deliverdDateTv = (TextView) findViewById(R.id.tvDeliverdDate);
        vehicleNoTv = (TextView) findViewById(R.id.tvVehicle);
        networkConnection = new NetworkConnection(this);
        openingPage();
        getMonthAndYear();

    }


    private void openingPage() {
        setUpPopUpPage();
        setTextView();
        Util.LoggingQueue(this, "Stock Inward detail activity", "Selected Inward:" + fpsStockInwardDetailList);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (fpsStockInwardDetailList != null) {
            for (GodownStockOutwardDto godownStockOutwardDto : fpsStockInwardDetailList) {
                godownStockOutwardDtos = godownStockOutwardDto;
                chellanIdTv.setText(godownStockOutwardDto.getReferenceNo());
            }
            String deliveredDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
            deliverdDateTv.setText(deliveredDate);
            if (godownStockOutwardDtos != null)
                godownStockOutwardDtos.setFpsAckDate(System.currentTimeMillis());
            configureData();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setTextView() {
        Util.LoggingQueue(this, "Stock Inward Details activity", "Main page Called");
        TextView topTv = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(((TextView) findViewById(R.id.tvChallanNoLabel)), R.string.reference_no);
        Util.setTamilText(((TextView) findViewById(R.id.tvDeliverLabel)), R.string.delivered_date);
        Util.setTamilText(((TextView) findViewById(R.id.tvVehicleLabel)), R.string.vehicle_no);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardDetailProductIdLabel)), R.string.commodity);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardDetailReceivedQuantityLabel)), R.string.recvd_qty);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardDetailAcknowledgeLabel)), R.string.ack);
        Util.setTamilText(((TextView) findViewById(R.id.monthspinnerlabel)), R.string.month);
        Util.setTamilText(((TextView) findViewById(R.id.btnfpsIDSubmit)), R.string.submit);
        Util.setTamilText(((TextView) findViewById(R.id.btnfpsIDCancel)), R.string.cancel);
        Util.setTamilText(topTv, R.string.inward_transit);
    }


    /*Data from server has been set inside this function*/
    private void configureData() {
        try {

            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_fps_stock_inward_detail);
            Log.i("Detail", fpsStockInwardDetailList.toString());
            fpsInwardLinearLayout.removeAllViews();
            for (int position = 0; position < fpsStockInwardDetailList.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, fpsStockInwardDetailList.get(position), position));

            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", "Inward:" + e.toString());
        }finally {
            findViewById(R.id.btnfpsIDCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            findViewById(R.id.btnfpsIDSubmit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSubmit();
                }
            });
        }
    }


    /*User entitlement view*/
    private View returnView(LayoutInflater entitle, GodownStockOutwardDto data, final int itemPosition) {
        View convertView = entitle.inflate(R.layout.adapter_fps_stock_inward_details, new LinearLayout(this),false);
        TextView productName = (TextView) convertView.findViewById(R.id.fpsInvardDetailProductId);
        TextView productUnit = (TextView) convertView.findViewById(R.id.fpsInvardDetailUnitId);
        getMonthAndYear();
        NoDefaultSpinner monthYear = (NoDefaultSpinner) convertView.findViewById(R.id.monthspinner);
        monthYear.setAdapter(adapter);
        monthYear.setSelection(0);
        data.setMonth(Calendar.getInstance().get(Calendar.MONTH));
        data.setYear(Calendar.getInstance().get(Calendar.YEAR));

        TextView receivedQuantity = (TextView) convertView.findViewById(R.id.fpsInvardDetailReceivedQuantity);
        CheckBox acknowledgeCbox = (CheckBox) convertView.findViewById(R.id.fpsInvardDetailAcknowledge);
        ProductDto product = FPSDBHelper.getInstance(this).getProductDetails(data.getProductId());
        Log.i("Product", product.toString());
        if (product != null) {
            productName.setText(product.getName());
            if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(product.getLocalProductUnit())) {
//                Util.setTamilText(productName, product.getLocalProductName());
                productName.setText(product.getLocalProductName());
            }

            productUnit.setText(product.getProductUnit());
            if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(product.getLocalProductUnit())) {
                Util.setTamilText(productUnit, product.getLocalProductUnit());
            }

        }
        acknowledgeCbox.setId(itemPosition);
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/

        String qty = Util.quantityRoundOffFormat(data.getQuantity());
        receivedQuantity.setText(qty);
            acknowledgeCbox.setEnabled(false);
            acknowledgeCbox.setChecked(true);
        if(!data.isCurrentMonth()){
            monthYear.setSelection(1);
        }
        monthYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position == 0) {
                    DateTime date = new DateTime();
                    fpsStockInwardDetailList.get(itemPosition).setMonth(date.getMonthOfYear());
                    fpsStockInwardDetailList.get(itemPosition).setYear(date.getYear());
                    fpsStockInwardDetailList.get(itemPosition).setCurrentMonth(true);
                } else {
                    DateTime date = new DateTime().plusMonths(1);
                    fpsStockInwardDetailList.get(itemPosition).setMonth(date.getMonthOfYear());
                    fpsStockInwardDetailList.get(itemPosition).setYear(date.getYear());
                    fpsStockInwardDetailList.get(itemPosition).setCurrentMonth(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
        return convertView;

    }

    // This method submit all data Fps stock outward data with received quantity
    public void onSubmit() {
        boolean checkedReceivedQuantity = false;
        for (int i = 0; i < fpsStockInwardDetailList.size(); i++) {
            checkedReceivedQuantity = ((CheckBox) findViewById(i)).isChecked();
            if (!checkedReceivedQuantity) {
                Util.messageBar(this, getString(R.string.ack_product));
                return;
            }
        }

        if (checkedReceivedQuantity) {
            Intent intent = new Intent(getApplicationContext(), StockInwardConfirmActivity.class);
            FpsStockInwardSelect fpsDataEntry = new FpsStockInwardSelect();
            fpsDataEntry.setFpsStockInwardconformList(fpsStockInwardDetailList);
            String fpsStockSelectList = new Gson().toJson(fpsDataEntry);
            intent.putExtra("stockInwardList", fpsStockSelectList);
            startActivity(intent);
            finish();
        }
    }

    // Cancel Button
    public void onCancel(View v) {
        onBackPressed();
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, FpsStockInwardActivity.class));
        Util.LoggingQueue(this, "Stock Inward activity", "Back pressed Called");
        finish();
    }

    private void getMonthAndYear() {
        DateTime date = new DateTime().plusMonths(1);
        DateTime dateNow = new DateTime();
        int nextMonthInt =date.getMonthOfYear();
        ArrayList<String> monthSpinner = new ArrayList<>();
        monthSpinner.add(String.valueOf(Html.fromHtml("<b>" + dateNow.getMonthOfYear() + "/" + dateNow.getYearOfCentury() + "</b>")));
        monthSpinner.add(String.valueOf( Html.fromHtml("<b>" + nextMonthInt + "/" + date.getYearOfCentury() + "</b>")));
        Log.e("arraylist", monthSpinner.toString());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }
}