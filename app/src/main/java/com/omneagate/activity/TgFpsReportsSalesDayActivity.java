package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.Adapter.SalesDayAdapter;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSIssueDaywiseReport;
import com.omneagate.DTO.Product;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;
import com.omneagate.printer.Usb_Printer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TgFpsReportsSalesDayActivity extends BaseActivity implements View.OnClickListener{
    private ImageView imageViewBack;
    private Button btnPrintAllotment,btnBack;
    private boolean connecting = false;
    private String message;
    private RationCardDetailDialog rationCardDetailDialog;
    private int string_len = 12;
    private Map<String, Object> inputMap;
    List<Product> fpsSalesDayProductList;
    List<Product> tempProductList;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private boolean isDataFound=false;
    private RecyclerView recyclerView;
    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpsreportssales_day);
        message = getIntent().getStringExtra("mode");
        Usb_Printer.getinstance(TgFpsReportsSalesDayActivity.this).check_usb_permission();
        initView();
        recyclerView = (RecyclerView) findViewById(R.id.fps_sales_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TgFpsReportsSalesDayActivity.this));
        new GetFpsSaleDailyReport().execute();
        tempProductList = new ArrayList<Product>();
    }


    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(TgFpsReportsSalesDayActivity.this);
        ((TextView) findViewById(R.id.top_textView)).setText(message);

        btnPrintAllotment = (Button) findViewById(R.id.btnPrintAllotment);
        btnPrintAllotment.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        Calendar mCalendar = Calendar.getInstance();

        Date currentData;
        if(Util.needInternalClock && GlobalAppState.serverDate !=null){
            currentData=GlobalAppState.serverDate;
        }else{
            currentData = new Date();
        }

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(currentData);
        System.out.println("formattedDate => " + formattedDate);

        ((TextView) findViewById(R.id.tvMonth)).setText(formattedDate);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                Intent backIntent = new Intent(TgFpsReportsSalesDayActivity.this,TgReportsDashBoardActivity.class);
                startActivity(backIntent);
                finish();
                break;

            case R.id.btnPrintAllotment:
                if(!isDataFound){
                    Toast.makeText(TgFpsReportsSalesDayActivity.this,getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!connecting) {
                    Usb_Printer.getinstance(TgFpsReportsSalesDayActivity.this).connectPrinter_new();
                } else {
                    Toast.makeText(TgFpsReportsSalesDayActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnBack:
                Intent backIntent1 = new Intent(TgFpsReportsSalesDayActivity.this,TgReportsDashBoardActivity.class);
                startActivity(backIntent1);
                finish();
                break;

            default:
                break;

        }

    }
    class GetFpsSaleDailyReport extends AsyncTask<String, String, FPSIssueDaywiseReport> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgFpsReportsSalesDayActivity.this, getString(R.string.Downloading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected FPSIssueDaywiseReport doInBackground(String... arg0) {
            try {
                return getDailySaleReport();
            } catch (Exception e) {
                e.printStackTrace();
                return new FPSIssueDaywiseReport();
            }
        }

        @Override
        protected void onPostExecute(FPSIssueDaywiseReport fpsIssueDaywiseReportResponseDto) {
            super.onPostExecute(fpsIssueDaywiseReportResponseDto);
            rationCardDetailDialog.cancel();
            setValues(fpsIssueDaywiseReportResponseDto);
        }
    }


    private void setValues(FPSIssueDaywiseReport fpsIssueDaywiseReportResponseDto) {

        fpsSalesDayProductList = fpsIssueDaywiseReportResponseDto.getProductList();
        for (Product product : fpsSalesDayProductList) {
            if ((product.getIssuedQuantity() != null) && (product.getIssuedQuantity() > 0.0)) {
                tempProductList.add(product);
            }

        }


        SalesDayAdapter salesDayAdapter = new SalesDayAdapter(TgFpsReportsSalesDayActivity.this, tempProductList);
        recyclerView.setAdapter(salesDayAdapter);

        TgFPSReportsAllotmentActivity.fontsize = 21;

        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            Usb_Printer.content = teluguPrintData();
        }else {
            Usb_Printer.content = new_englishPrintData();
        }


    }


    private String new_englishPrintData() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date currentData;
        if(Util.needInternalClock && GlobalAppState.serverDate !=null){
            currentData=GlobalAppState.serverDate;
        }else{
            currentData = new Date();
        }
        String date = dateFormat.format(currentData);
        dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.getDefault());
        String time = dateFormat.format(currentData);

        StringBuilder textData = new StringBuilder();
        textData.append("<pre>");
        textData.append("<font size=\"5\">            RECEIPT</font>" + "\n");
        textData.append("<font size=\"5\">  Food & Civil Supplies Dept \n       Govt of Telangana</font>" + "\n");
        textData.append("<font size=\"5\">   FPS DAY SALES REPORT</font>" + "\n");

        textData.append(fixedlenght("Date") + date.trim() + "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop Id") + LoginData.getInstance().getShopNo() + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        textData.append(fixedlenghth("Item", 6) + "             " + "Qty" +"\n");
        textData.append("------------------------------------------------------------" + "\n");

        for (Product product : tempProductList) {
            String productName = product.getDisplayName();
            String quantity = String.format("" + product.getIssuedQuantity());
            if (quantity.length() == 3) {
                quantity = "    " + quantity;
            } else if (quantity.length() == 4) {
                quantity = "   " + quantity;
            }
            textData.append(fixedlenghth(productName, 6) + "         " + quantity +"\n");
        }

        textData.append("------------------------------------------------------------" + "\n");
        textData.append("</pre>");
        textData.append(getResources().getString(R.string.call_issue) + "\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");


        return textData.toString();
    }


    private String teluguPrintData() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date currentData;
        if(Util.needInternalClock && GlobalAppState.serverDate !=null){
            currentData=GlobalAppState.serverDate;
        }else{
            currentData = new Date();
        }
        String date = dateFormat.format(currentData);
        dateFormat = new SimpleDateFormat("kk:mm:ss", Locale.getDefault());
        String time = dateFormat.format(currentData);

        StringBuilder textData = new StringBuilder();
        textData.append("<pre>");
        textData.append("<font size=\"5\">            స్వీకరణపై</font>" + "\n");
        textData.append("<font size=\"5\">   ఆహార & పౌరసరఫరాల విభాగం \n      తెలంగాణ ప్రభుత్వము </font>" + "\n");
        textData.append("<font size=\"5\">   FPS రోజు అమ్మకాలు నివేదిక</font>" + "\n");
        textData.append("</pre>");

        String tx_dt = "తేదీ           :  " + date.trim() + "\n" + "సమయం   :  " + time.trim() + "\n";
        textData.append(tx_dt.replaceAll(" ", "&nbsp;"));
        textData.append("------------------------------------------------------------" + "\n");

        String shop_id = "షాపు ID     :  " + LoginData.getInstance().getShopNo() + "\n";
        textData.append(shop_id.replaceAll(" ", "&nbsp;"));
        textData.append("------------------------------------------------------------" + "\n");

        textData.append("<pre>");
        textData.append(fixedlenghth("వస్తువు", 6) + "           " + "పరిమాణం" +"\n");
        textData.append("------------------------------------------------------------" + "\n");

        for (Product product : tempProductList) {
            String productName = product.getDisplayName();
            String quantity = String.format("" + product.getIssuedQuantity());
            if (quantity.length() == 3) {
                quantity = "    " + quantity;
            } else if (quantity.length() == 4) {
                quantity = "   " + quantity;
            }
            textData.append(fixedlenghth(productName, 6) + "       " + quantity +"\n");
        }

        textData.append("------------------------------------------------------------" + "\n");
        textData.append("</pre>");
        textData.append(getResources().getString(R.string.call_issue) + "\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");


        return textData.toString();
    }

    private String fixedlenght(String text) {

        text = text.trim();
        if (text.length() < string_len) {

            while (text.length() < string_len) {
                text = text + " ";
            }
            text = text.substring(0, string_len - 1) + " : ";
        }
        Log.e("text", text);
        return text/*.replaceAll(" ", "&nbsp;")*/;
    }

    private String fixedlenghth(String text, int length) {

        text = text.trim();
        if (text.length() < length) {
            while (text.length() < length) {
                text = text + " ";
            }
        } else {
            text = text.substring(0, length);
        }
        return text;
    }


    private FPSIssueDaywiseReport getDailySaleReport() {
        FPSIssueDaywiseReport fpsIssueDaywiseReport = new FPSIssueDaywiseReport();
        try {
            GregorianCalendar gc = new GregorianCalendar();
         /*   int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);*/

            Calendar mCalendar = Calendar.getInstance();
            String month_txt = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(mCalendar.getTime());
            System.out.println("formattedDate => " + formattedDate);

//            ((TextView) findViewById(R.id.tvMonth)).setText(formattedDate);
//            ((TextView) findViewById(R.id.tvYear)).setText("" + year);

            inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
//            inputMap.put("currDate", formattedDate);
           // inputMap.put("currYear", year);
         //   inputMap.put("currMonth", month);
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            fpsIssueDaywiseReport = XMLUtil.getFPSDaywiseIssueReport(inputMap);
            if(fpsIssueDaywiseReport.getRespMsgCode().equals("0")){
                isDataFound=true;
            }else{
                isDataFound=false;
            }
            return fpsIssueDaywiseReport;
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgFpsReportsSalesDayActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return fpsIssueDaywiseReport;
        }catch (Exception e) {
            e.printStackTrace();
            return new FPSIssueDaywiseReport();
        }

    }

    @Override
    public void onBackPressed() {
        Intent backIntent =new Intent(TgFpsReportsSalesDayActivity.this,TgReportsDashBoardActivity.class);
        startActivity(backIntent);
        finish();
    }
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgFpsReportsSalesDayActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}