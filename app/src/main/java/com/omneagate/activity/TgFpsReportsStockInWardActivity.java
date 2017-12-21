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

import com.omneagate.Adapter.ReceiptAdapter;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSReceiptReport;
import com.omneagate.DTO.Product;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;
import com.omneagate.printer.Usb_Printer;

import java.text.DateFormat;
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

public class TgFpsReportsStockInWardActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageViewBack;
    private Button btnPrintAllotment,btnBack;
    private boolean connecting = false;
    private String message;
    private RationCardDetailDialog rationCardDetailDialog;
    private int string_len = 12;
    private Map<String, Object> inputMap;
    List<Product> fpsReceiptProductList;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private final String TAG=TgFpsReportsStockInWardActivity.class.getCanonicalName();
    private boolean isDataFound=false;
    private List<Product> tempProductList;
    private RecyclerView recyclerView;


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpsreports_stock_in_ward);
        message = getIntent().getStringExtra("mode");
        Usb_Printer.getinstance(TgFpsReportsStockInWardActivity.this).check_usb_permission();
        initView();
        recyclerView = (RecyclerView) findViewById(R.id.fps_sales_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TgFpsReportsStockInWardActivity.this));
        new GetFpsInWardDetails().execute();
    }


    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(TgFpsReportsStockInWardActivity.this);
        ((TextView) findViewById(R.id.top_textView)).setText(message);
        btnPrintAllotment = (Button) findViewById(R.id.btnPrintAllotment);
        btnPrintAllotment.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        tempProductList = new ArrayList<Product>();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                Intent backIntent = new Intent(TgFpsReportsStockInWardActivity.this, TgReportsDashBoardActivity.class);
                startActivity(backIntent);
                finish();
                break;

            case R.id.btnPrintAllotment:
                if(!isDataFound){
                    Toast.makeText(TgFpsReportsStockInWardActivity.this,getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!connecting) {
                    Usb_Printer.getinstance(TgFpsReportsStockInWardActivity.this).connectPrinter_new();
                } else {
                    Toast.makeText(TgFpsReportsStockInWardActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnBack:
                Intent backIntent1 = new Intent(TgFpsReportsStockInWardActivity.this, TgReportsDashBoardActivity.class);
                startActivity(backIntent1);
                finish();
                break;

            default:
                break;

        }

    }

    class GetFpsInWardDetails extends AsyncTask<String, String, FPSReceiptReport> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgFpsReportsStockInWardActivity.this, getString(R.string.Downloading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected FPSReceiptReport doInBackground(String... arg0) {
            try {
                return getInWardReport();
            } catch (Exception e) {
                e.printStackTrace();
                return new FPSReceiptReport();
            }
        }

        @Override
        protected void onPostExecute(FPSReceiptReport fpsReceiptReportResponseDto) {
            super.onPostExecute(fpsReceiptReportResponseDto);
            rationCardDetailDialog.cancel();
            setValues(fpsReceiptReportResponseDto);
        }
    }


    private void setValues(FPSReceiptReport fpsReceiptReportResponseDto) {


        fpsReceiptProductList = fpsReceiptReportResponseDto.getProductList();
        for (Product product : fpsReceiptProductList) {
            if ((product.getReceivedQuantity() != null) && (product.getReceivedQuantity() > 0.0)) {
                tempProductList.add(product);
            }

        }

        ReceiptAdapter receiptAdapter = new ReceiptAdapter(TgFpsReportsStockInWardActivity.this, tempProductList);
        recyclerView.setAdapter(receiptAdapter);

         /*   transaction_id= fpsReceiptReportResponseDto.getTransactionId();
        l = Long.parseLong(transaction_id);
        Log.e("check log",""+l);

        System.out.println(transaction_id + "\n" + l);

        Date date = new Date(l);
         formatter = new SimpleDateFormat("EEE hh:mm a  MMM dd - yyyy");
        Log.e("check log",""+formatter.format(date));
        System.out.println(formatter.format(date));*/

        TgFPSReportsAllotmentActivity.fontsize = 21;

        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            Usb_Printer.content = teluguPrintData();
        } else {
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
        //textData.append("<font size=\"5\">       STOCK INWARD</font>" + "\n");
        textData.append("<font size=\"5\">       FPS RECEIPTS</font>" + "\n");
        textData.append(fixedlenght("Date") + date.trim() + "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop Id") + LoginData.getInstance().getShopNo() + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        textData.append(fixedlenghth("Item", 6) + "           " + "Qty" +"\n");
        textData.append("------------------------------------------------------------" + "\n");

        for (Product product : tempProductList) {
            String productName = product.getDisplayName();
            String quantity = String.format("" + product.getReceivedQuantity());
            if (quantity.length() == 3) {
                quantity = "    " + quantity;
            } else if (quantity.length() == 4) {
                quantity = "   " + quantity;
            }else if(quantity.length() == 5) {
                quantity = "  " + quantity;
            }
            else if(quantity.length() ==6){
                quantity = " " + quantity;
            }
            else{
                Log.e(TAG,"Total quantity : "+quantity);
                Log.e(TAG,"Total length : "+quantity.length());
            }
            textData.append(fixedlenghth(productName, 6) + "       " + quantity + "\n");
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
        textData.append("<font size=\"5\">    ఆహార & పౌరసరఫరాల విభాగం \n        తెలంగాణ ప్రభుత్వము </font>" + "\n");
        //textData.append("<font size=\"5\">            స్టాక్ లోపలి</font>" + "\n");
        textData.append("<font size=\"5\">           FPS రసీదు</font>" + "\n");
        textData.append("</pre>");

//        textData.append("<pre>");
//        textData.append("<font size=\"5\">            స్వీకరణపై</font>" + "\n");
//        textData.append("<font size=\"5\">   ఆహార & పౌరసరఫరాల విభాగం \n      తెలంగాణ ప్రభుత్వము </font>" + "\n");
//        textData.append("<font size=\"5\">         FPS రసీదు</font>" + "\n");

        String tx_dt = "తేదీ           :  " + date.trim() + "\n" + "సమయం   :  " + time.trim() + "\n";
        textData.append(tx_dt.replaceAll(" ", "&nbsp;"));
        textData.append("------------------------------------------------------------" + "\n");
        String shop_id = "షాపు ID     :  " + LoginData.getInstance().getShopNo() + "\n";
        textData.append(shop_id.replaceAll(" ", "&nbsp;"));
        textData.append("------------------------------------------------------------" + "\n");

        textData.append("<pre>");

        textData.append(fixedlenghth("వస్తువు", 6) + "       " + "   పరిమాణం" + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        for (Product product : tempProductList) {
            String productName = product.getDisplayName();
            String quantity = String.format("" + product.getReceivedQuantity());
            if (quantity.length() == 3) {
                quantity = "    " + quantity;
            } else if (quantity.length() == 4) {
                quantity = "   " + quantity;
            }else if(quantity.length() == 5) {
                quantity = "  " + quantity;
            }
            else if(quantity.length() ==6){
                quantity = " " + quantity;
            }
            textData.append(fixedlenghth(productName, 6) + "       " + quantity + "\n");
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


    private FPSReceiptReport getInWardReport() {
        FPSReceiptReport fpsReceiptReport = new FPSReceiptReport();
        try {


            GregorianCalendar gc = new GregorianCalendar();

            Calendar mCalendar = Calendar.getInstance();
            String month_txt = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

            ((TextView) findViewById(R.id.tvMonth)).setText("" + LoginData.getInstance().getCurrentMonth());
            ((TextView) findViewById(R.id.tvYear)).setText("" + LoginData.getInstance().getCurrentYear());

            inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());

            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());

            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            fpsReceiptReport = XMLUtil.getFPSReceiptReport(inputMap);
            if(fpsReceiptReport.getRespMsgCode().equals("0")){
                isDataFound=true;
            }else{
                isDataFound=false;
            }
            return fpsReceiptReport;
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgFpsReportsStockInWardActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return fpsReceiptReport;
        } catch (Exception e) {
            e.printStackTrace();
            return new FPSReceiptReport();
        }

    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgFpsReportsStockInWardActivity.this, TgReportsDashBoardActivity.class);
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
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime);

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgFpsReportsStockInWardActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}