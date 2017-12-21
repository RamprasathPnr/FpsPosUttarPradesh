package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.WrongDeviceDateDialog;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FpsStockInwardDetailActivity extends BaseActivity {

    TextView chellanIdTv;

    //Acknowledgement Date Textview
    TextView deliveredDateTv;

    // Batch no Textview
    TextView vehicleNoTv;

    //CheckBox for Acknowledgement status
    boolean ackStatus = false;


    // Godown Dto list
    List<GodownStockOutwardDto> fpsStockInwardDetailList;

    // Godown Dto
    GodownStockOutwardDto godownStockOutwardDtos;

    String godownName = "";

    //adpter for spinner field
    ArrayAdapter<String> adapter;

    Boolean submitEnableorDisable;

    long timeOnClick = 0l;

    private String timeStamp;
    int position = 0;
    int lv_position = 0;

    WrongDeviceDateDialog wrongDeviceDateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps_stock_inward_detail);

        try {
            if (getIntent().getExtras().getString("lv_position") != null){
                Log.e("**FpsStockInwardDetailActivity", "**lv_position = != null > "+getIntent().getExtras().getString("lv_position"));
                lv_position = Integer.parseInt(getIntent().getExtras().getString("lv_position"));

            }else{
                Log.e("**FpsStockInwardDetailActivity", "**lv_position = null");

            }

        } catch (Exception e) {
            Log.e("**FpsStockInwardDetailActivity", "**lv_position = Exception");

        }

       // Log.e("**FpsStockInwardDetailActivity", "lv_position = "+lv_position);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        timeStamp = String.valueOf(System.currentTimeMillis());
        godownName = getIntent().getExtras().getString("godown");
        submitEnableorDisable = getIntent().getExtras().getBoolean("submitBoolean");
        Log.e("enable_disable","--->"+submitEnableorDisable+"");
        godownStockOutwardDtos = new Gson().fromJson(godownName, GodownStockOutwardDto.class);
        chellanIdTv = (TextView) findViewById(R.id.tvChallan);
        deliveredDateTv = (TextView) findViewById(R.id.tvDeliverdDate);
        vehicleNoTv = (TextView) findViewById(R.id.tvVehicle);
        networkConnection = new NetworkConnection(this);
        openingPage();
//        getMonthAndYear();

    }


    private void openingPage() {
        setUpPopUpPage();
        setTextView();



        if (godownStockOutwardDtos.isFpsAckStatus()) {
            ackStatus = true;
            setDisableWidgets();
        } else {
            dismissDialog();

        }
        fpsStockInwardDetailList = FPSDBHelper.getInstance(this).showFpsStockInvardDetail(godownStockOutwardDtos.getReferenceNo(), ackStatus);
        Util.LoggingQueue(this, "Stock Inward detail activity", "Selected Inward:" + fpsStockInwardDetailList.toString());
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
                if (StringUtils.isNotEmpty(godownStockOutwardDto.getVehicleN0()))
                    vehicleNoTv.setText(godownStockOutwardDto.getVehicleN0().toUpperCase());
                if(godownStockOutwardDto.getOutwardDate()!=0){
                    deliveredDateTv.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(godownStockOutwardDto.getOutwardDate()));
                }else{
                    deliveredDateTv.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()));
                }
            }

            if (godownStockOutwardDtos != null)
                godownStockOutwardDtos.setFpsAckDate(System.currentTimeMillis());
            configureData();
        }


    }



    private void setDisableWidgets() {
        findViewById(R.id.btnfpsIDCancel).setVisibility(View.INVISIBLE);
        Util.setTamilText(((TextView) findViewById(R.id.btnfpsIDSubmit)), R.string.close);
        findViewById(R.id.btnfpsIDSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setTextView() {
        Util.LoggingQueue(this, "Stock Inward Details activity", "Main page Called");
        TextView topTv = (TextView) findViewById(R.id.top_textView);
        Util.setTamilText(((TextView) findViewById(R.id.tvChallanNoLabel)), R.string.reference_no);
        Util.setTamilText(((TextView) findViewById(R.id.unitLabel)), R.string.unit);
        Util.setTamilText(((TextView) findViewById(R.id.tvDeliverLabel)), R.string.doc_date);
        Util.setTamilText(((TextView) findViewById(R.id.tvVehicleLabel)), R.string.vehicle_no);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardDetailProductIdLabel)), R.string.commodity);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardDetailReceivedQuantityLabel)), R.string.recvd_qty);
        Util.setTamilText(((TextView) findViewById(R.id.fpsInvardDetailAcknowledgeLabel)), R.string.ack);
        Util.setTamilText(((TextView) findViewById(R.id.monthspinnerlabel)), R.string.month);
        Util.setTamilText(((TextView) findViewById(R.id.btnfpsIDSubmit)), R.string.submit);
        Util.setTamilText(((TextView) findViewById(R.id.btnfpsIDCancel)), R.string.cancel);
        Util.setTamilText(topTv, R.string.inward_transit);
        if(!submitEnableorDisable)
        {
           findViewById(R.id.btnfpsIDSubmit).setVisibility(View.INVISIBLE);
           Util.setTamilText(topTv, R.string.stock_inward_history);
        }
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
        }
    }


    /*User entitlement view*/
    private View returnView(LayoutInflater entitle, GodownStockOutwardDto data, final int itemPosition) {
        View convertView = entitle.inflate(R.layout.adapter_fps_stock_inward_details, null);
        TextView productName = (TextView) convertView.findViewById(R.id.fpsInvardDetailProductId);
        TextView productUnit = (TextView) convertView.findViewById(R.id.fpsInvardDetailUnitId);
        TextView monthYear = (TextView) convertView.findViewById(R.id.monthspinner);
//        getMonthAndYear();
//        NoDefaultSpinner monthYear = (NoDefaultSpinner) convertView.findViewById(R.id.monthspinner);
//        monthYear.setAdapter(adapter);
//        monthYear.setSelection(0);

        TextView receivedQuantity = (TextView) convertView.findViewById(R.id.fpsInvardDetailReceivedQuantity);
        CheckBox acknowledgeCbox = (CheckBox) convertView.findViewById(R.id.fpsInvardDetailAcknowledge);
        ProductDto product = FPSDBHelper.getInstance(this).getProductDetails(data.getProductId());
        Log.e("Product",data.toString());
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


            monthYear.setText(data.getMonth() + "/" + data.getYear());

            /*ArrayList<String> monthSpinner = new ArrayList<>();
            monthSpinner.add(String.valueOf(Html.fromHtml("<b>" + data.getMonth() + "/" + data.getYear() + "</b>")));
//            monthSpinner.add(String.valueOf( Html.fromHtml("<b>" + nextMonthInt + "/" + date.getYearOfCentury() + "</b>")));
            Log.e("arraylist", monthSpinner.toString());
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            monthYear.setAdapter(adapter);
            monthYear.setSelection(0);*/

        }
        acknowledgeCbox.setId(itemPosition);


        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        String qty = Util.quantityRoundOffFormat(data.getQuantity());
        receivedQuantity.setText(qty);
        if (ackStatus) {
            acknowledgeCbox.setEnabled(false);
            acknowledgeCbox.setChecked(true);

            /*DateTime date = new DateTime();
            if(date.getMonthOfYear() == data.getMonth()){
                monthYear.setSelection(0);
            }else{
                monthYear.setSelection(1);
            }
            monthYear.setEnabled(false);*/

        } else {
            acknowledgeCbox.setChecked(false);
        }

        /*monthYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                DateTime date = new DateTime().plusMonths(1);
                if (position == 0) {
                    fpsStockInwardDetailList.get(itemPosition).setMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
                    fpsStockInwardDetailList.get(itemPosition).setYear(Calendar.getInstance().get(Calendar.YEAR));
                    fpsStockInwardDetailList.get(itemPosition).setCurrentMonth(true);
                } else {
                    fpsStockInwardDetailList.get(itemPosition).setMonth(date.getMonthOfYear());
                    fpsStockInwardDetailList.get(itemPosition).setYear(date.getYear());
                    fpsStockInwardDetailList.get(itemPosition).setCurrentMonth(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });*/
        return convertView;

    }

    // This method submit all data Fps stock outward data with received quantity
    private void onSubmit() {
        if (SystemClock.elapsedRealtime() - timeOnClick < 4000) {
            return;
        }
        timeOnClick = SystemClock.elapsedRealtime();
        /*findViewById(R.id.btnfpsIDCancel).setEnabled(false);
        findViewById(R.id.btnfpsIDSubmit).setEnabled(false);
        findViewById(R.id.btnfpsIDCancel).setClickable(false);
        findViewById(R.id.btnfpsIDSubmit).setClickable(false);
        findViewById(R.id.btnfpsIDCancel).setOnClickListener(null);
        findViewById(R.id.btnfpsIDSubmit).setOnClickListener(null);*/
        boolean checkedReceivedQuantity = false;
        if (fpsStockInwardDetailList != null) {
            for (int i = 0; i < fpsStockInwardDetailList.size(); i++) {
                checkedReceivedQuantity = ((CheckBox) findViewById(i)).isChecked();
                if (!checkedReceivedQuantity) {
                    Util.messageBar(com.omneagate.activity.FpsStockInwardDetailActivity.this, getString(R.string.ack_product));
                    dismissDialog();
                    return;
                }
            }
        }

        findViewById(R.id.btnfpsIDCancel).setEnabled(false);
        findViewById(R.id.btnfpsIDSubmit).setEnabled(false);
        findViewById(R.id.btnfpsIDCancel).setClickable(false);
        findViewById(R.id.btnfpsIDSubmit).setClickable(false);
        findViewById(R.id.btnfpsIDCancel).setOnClickListener(null);
        findViewById(R.id.btnfpsIDSubmit).setOnClickListener(null);
        findViewById(R.id.btnfpsIDSubmit).setBackgroundColor(Color.LTGRAY);

        if (checkedReceivedQuantity) {
            Intent intent = new Intent(getApplicationContext(), StockInwardConfirmActivity.class);
            FpsStockInwardSelect fpsDataEntry = new FpsStockInwardSelect();
            fpsDataEntry.setFpsStockInwardconformList(fpsStockInwardDetailList);
            String fpsStockSelectList = new Gson().toJson(fpsDataEntry);
            intent.putExtra("stockInwardList", fpsStockSelectList);
            intent.putExtra("godownName", godownName);
            startActivity(intent);
            finish();
        }
    }

    // Cancel Button
    public void onCancel(View v) {
        onBackPressed();
    }


    private void dismissDialog() {
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.dismiss();
        }
        findViewById(R.id.btnfpsIDCancel).setEnabled(true);
        findViewById(R.id.btnfpsIDSubmit).setEnabled(true);
        findViewById(R.id.btnfpsIDCancel).setClickable(true);
        findViewById(R.id.btnfpsIDSubmit).setClickable(true);
        findViewById(R.id.btnfpsIDCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.btnfpsIDSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            try {
                String maxBillDate = FPSDBHelper.getInstance(FpsStockInwardDetailActivity.this).getMaxBillDate();
                if ((maxBillDate == null) || (maxBillDate.equalsIgnoreCase("")) || (maxBillDate.equalsIgnoreCase("null"))) {
                    onSubmit();
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String currentDeviceDate = simpleDateFormat.format(new Date());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d1 = sdf.parse(maxBillDate);
                    Date d2 = sdf.parse(currentDeviceDate);
                    if (d1.compareTo(d2) > 0) {
                        FPSDBHelper.getInstance(FpsStockInwardDetailActivity.this).insertInvalidDateException("stock_inward", "Inward_Ack", fpsStockInwardDetailList.toString(), "MaxBillDate = " + maxBillDate + "\n"+"DeviceDate = "+currentDeviceDate);
                        wrongDeviceDateDialog = new WrongDeviceDateDialog(FpsStockInwardDetailActivity.this);
                        wrongDeviceDateDialog.show();
                    }
                    else {
                        onSubmit();
                    }
                }
            }
            catch(Exception e) {
                onSubmit();
            }
            }
        });
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


    @Override
    public void onBackPressed() {
        if(!submitEnableorDisable)
        {
            startActivity(new Intent(this, FpsStockInwardReceivedActivity.class));
            Util.LoggingQueue(this, "Stock Inward activity", "Back pressed Called");
            finish();
        }
        else {

            Intent intent = new Intent(this, FpsStockInwardActivity.class);
            intent.putExtra("lv_position", ""+lv_position);
            startActivity(intent);

            //startActivity(new Intent(this, FpsStockInwardActivity.class));
            Util.LoggingQueue(this, "Stock Inward activity", "Back pressed Called = "+ lv_position);
            finish();
        }
    }

    /*private void getMonthAndYear() {
        DateTime date = new DateTime().plusMonths(1);
        DateTime dateNow = new DateTime();
        int nextMonthInt =date.getMonthOfYear();
        ArrayList<String> monthSpinner = new ArrayList<>();
        monthSpinner.add(String.valueOf(Html.fromHtml("<b>" + dateNow.getMonthOfYear() + "/" + dateNow.getYearOfCentury() + "</b>")));
        monthSpinner.add(String.valueOf( Html.fromHtml("<b>" + nextMonthInt + "/" + date.getYearOfCentury() + "</b>")));
        Log.e("arraylist", monthSpinner.toString());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }*/

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