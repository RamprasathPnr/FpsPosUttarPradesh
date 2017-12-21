package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;

import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.io.USBPrinting;
import com.omneagate.Adapter.AllotmentAdapter;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSAllotment;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TgFPSReportsAllotmentActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imageViewBack;
    private Map<String, Object> inputMap;
    private RationCardDetailDialog rationCardDetailDialog;
    private Button btnPrintAllotment,btnBack;
    private boolean connecting = false, printing = false;
    static USBPrinting mUsb = new USBPrinting();
    public static ExecutorService es = Executors.newScheduledThreadPool(5);
    private String TAG = "TgFPSReportsAllotmentActivity";
    UsbManager manager;
    ProgressBar printProgress;
    public static int fontsize = 20;
    public static boolean destroyed = true;
    private int string_len = 12;
    List<Product> fpsAllotmentProductList;
    private boolean isDataFound=false;

    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private List<Product> tempProductList;
    private RecyclerView recyclerView;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_fpsreports_allotment);
        Usb_Printer.getinstance(TgFPSReportsAllotmentActivity.this).check_usb_permission();
        initView();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TgFPSReportsAllotmentActivity.this));
        new GetFpsAllotmentDetails().execute();
    }

    class GetFpsAllotmentDetails extends AsyncTask<String, String, FPSAllotment> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgFPSReportsAllotmentActivity.this, getString(R.string.Downloading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected FPSAllotment doInBackground(String... arg0) {
            try {
                return getAllotmentReport();
            } catch (Exception e) {
                e.printStackTrace();
                return new FPSAllotment();
            }
        }

        @Override
        protected void onPostExecute(FPSAllotment s) {
            super.onPostExecute(s);
            rationCardDetailDialog.cancel();
            setValues(s);
        }
    }

    private void setValues(FPSAllotment responseAllotmentDto) {

        fpsAllotmentProductList = responseAllotmentDto.getFpsAllotmentProductList();
        for (Product product : fpsAllotmentProductList) {
            if ((product.getProductAllotment() != null) && (product.getProductAllotment() > 0.0)) {
                tempProductList.add(product);
            }

        }
        AllotmentAdapter allotmentAdapter = new AllotmentAdapter(TgFPSReportsAllotmentActivity.this, tempProductList);
        recyclerView.setAdapter(allotmentAdapter);



        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            fontsize = 18;
            Usb_Printer.content = teluguPrintData();
        } else {
            fontsize = 21;
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
        textData.append("<font size=\"5\">     FPS ALLOTMENT REPORT</font>" + "\n");

        textData.append(fixedlenght("Date") + date.trim() + "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop Id") + LoginData.getInstance().getShopNo() + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        textData.append(fixedlenghth("Item", 6) + "   " + "Allot.qty" + "  " + "Price" + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        for (Product product : tempProductList) {

            String productName = product.getDisplayName();
            String quantity = String.format("" + product.getProductAllotment());
            String prize = String.format("%.2f", product.getProductPrice());
            if (prize.length() == 4) {
                prize = " " + prize;
            }

            if (quantity.length() == 3) {
                quantity = "   " + quantity;
            } else if (quantity.length() == 4) {
                quantity = "  " + quantity;
            } else if (quantity.length() == 5) {
                quantity = " " + quantity;
            }
            textData.append(fixedlenghth(productName, 6) + "   " + quantity + "     " + prize + "\n");
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
        textData.append("<font size=\"5\">      FPS కేటాయింపు నివేదిక </font>" + "\n");
        textData.append("</pre>");

        String tx_dt = "తేదీ           :  " + date.trim() + "\n" + "సమయం   :  " + time.trim() + "\n";
        textData.append(tx_dt.replaceAll(" ", "&nbsp;"));
        textData.append("----------------------------------------------------------------------" + "\n");

        String shop_id = "షాపు ID     :  " + LoginData.getInstance().getShopNo() + "\n";
        textData.append(shop_id.replaceAll(" ", "&nbsp;"));
        textData.append("--------------------------------------------------------------------------" + "\n");

        textData.append("<pre>");
        textData.append(fixedlenghth("వస్తువు", 6) + "   " + "కేటాయింపు పరిమాణం " + "  " + "ధర" + "\n");
        textData.append("--------------------------------------------------------------------" + "\n");

        for (Product product : tempProductList) {

            String productName = product.getDisplayName();
            String quantity = "" + product.getProductAllotment();
            String prize = String.format("%.2f", product.getProductPrice());
            if (prize.length() == 4) {
                prize = " " + prize;
            }

            if (quantity.length() == 3) {
                quantity = "   " + quantity;
            } else if (quantity.length() == 4) {
                quantity = "  " + quantity;
            } else if (quantity.length() == 5) {
                quantity = " " + quantity;
            }
            textData.append(fixedlenghth(productName, 6) + "       " + quantity + "     " + prize + "\n");
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

    private FPSAllotment getAllotmentReport() {
        FPSAllotment fpsAllotment = new FPSAllotment();
        try {

            GregorianCalendar gc = new GregorianCalendar();
          /*  int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);*/

            Calendar mCalendar = Calendar.getInstance();
            String month_txt = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

            ((TextView) findViewById(R.id.tvMonth)).setText("" + LoginData.getInstance().getCurrentMonth());
            ((TextView) findViewById(R.id.tvYear)).setText("" + LoginData.getInstance().getCurrentYear());

            inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode",LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            //   inputMap.put("currMonth", month);
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
            // inputMap.put("currYear", year);
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            fpsAllotment = XMLUtil.getFPSAllotmentRport(inputMap);

            if(fpsAllotment.getRespMsgCode().equals("0")){
                isDataFound=true;
            }else{
                isDataFound=false;
            }
            return fpsAllotment;
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgFPSReportsAllotmentActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return fpsAllotment;
        } catch (Exception e) {
            e.printStackTrace();
            return new FPSAllotment();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                Intent backIntent = new Intent(TgFPSReportsAllotmentActivity.this, TgReportsDashBoardActivity.class);
                startActivity(backIntent);
                finish();
                break;

            case R.id.btnPrintAllotment:
                if(!isDataFound){
                    Toast.makeText(TgFPSReportsAllotmentActivity.this,getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!connecting) {
                    Usb_Printer.getinstance(TgFPSReportsAllotmentActivity.this).connectPrinter_new();
                } else {
                    Toast.makeText(TgFPSReportsAllotmentActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnBack:
                Intent backIntent1 = new Intent(TgFPSReportsAllotmentActivity.this, TgReportsDashBoardActivity.class);
                startActivity(backIntent1);
                finish();
                break;


            default:
                break;

        }

    }

    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            ((TextView) findViewById(R.id.top_textView)).setText("నివేదికలు > FPS కేటాయింపు నివేదిక");

        } else {
            ((TextView) findViewById(R.id.top_textView)).setText("REPORTS > FPS ALLOTMENT");
        }

        btnPrintAllotment = (Button) findViewById(R.id.btnPrintAllotment);
        btnPrintAllotment.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        printProgress = (ProgressBar) findViewById(R.id.printerProgress);
        tempProductList = new ArrayList<Product>();
    }


    public void show_print_button(boolean status) {
        if (status) {
            btnPrintAllotment.setVisibility(View.VISIBLE);
            printProgress.setVisibility(View.GONE);
        } else {
            btnPrintAllotment.setVisibility(View.INVISIBLE);
            printProgress.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgFPSReportsAllotmentActivity.this, TgReportsDashBoardActivity.class);
        startActivity(backIntent);
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
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
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime);

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            Intent i = new Intent(TgFPSReportsAllotmentActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}