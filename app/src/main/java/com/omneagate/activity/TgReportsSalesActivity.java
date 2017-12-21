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


import com.omneagate.Adapter.SalesAdapter;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSIssueReport;
import com.omneagate.DTO.Product;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;
import com.omneagate.printer.Usb_Printer;

import java.net.SocketTimeoutException;
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

/**
 * Created by user on 27/2/17.
 */
public class TgReportsSalesActivity extends BaseActivity implements View.OnClickListener {
    String message;
        private ImageView imageViewBack;
    private RationCardDetailDialog rationCardDetailDialog;
    private Map<String, Object> inputMap;
    private int string_len = 12;
    private List<Product> fpsSalesProductList;
    private boolean connecting = false;
    private Button btnPrintAllotment,btnBack;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private boolean isDataFound=false;
    private List<Product> tempProductList;
    private RecyclerView recyclerView;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps_report_sales);
        message = getIntent().getStringExtra("mode");
        Usb_Printer.getinstance(TgReportsSalesActivity.this).check_usb_permission();
        initView();
        recyclerView = (RecyclerView) findViewById(R.id.fps_sales_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TgReportsSalesActivity.this));
        new GetFpsIssueDetails().execute();
    }


    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(TgReportsSalesActivity.this);
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
                Intent backIntent = new Intent(TgReportsSalesActivity.this, TgReportsDashBoardActivity.class);
                startActivity(backIntent);
                finish();
                break;

            case R.id.btnPrintAllotment:
                if(!isDataFound){
                    Toast.makeText(TgReportsSalesActivity.this,getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!connecting) {
                    Usb_Printer.getinstance(TgReportsSalesActivity.this).connectPrinter_new();
                } else {
                    Toast.makeText(TgReportsSalesActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnBack:
                Intent backIntent1 = new Intent(TgReportsSalesActivity.this, TgReportsDashBoardActivity.class);
                startActivity(backIntent1);
                finish();
                break;

            default:
                break;

        }

    }


    class GetFpsIssueDetails extends AsyncTask<String, String, FPSIssueReport> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgReportsSalesActivity.this, getString(R.string.Downloading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected FPSIssueReport doInBackground(String... arg0) {
            try {
                return getIssueReport();
            } catch (Exception e) {
                e.printStackTrace();
                return new FPSIssueReport();
            }
        }

        @Override
        protected void onPostExecute(FPSIssueReport fpsIssueReportResponseDto) {
            super.onPostExecute(fpsIssueReportResponseDto);
            rationCardDetailDialog.cancel();
            setValues(fpsIssueReportResponseDto);
        }
    }


    private void setValues(FPSIssueReport fpsIssueReportResponseDto) {


        fpsSalesProductList = fpsIssueReportResponseDto.getProductList();

        for (Product product : fpsSalesProductList) {
            if ((product.getIssuedQuantity() != null) && (product.getIssuedQuantity() > 0.0)) {
                 tempProductList.add(product);
            }

        }
        SalesAdapter salesAdapter = new SalesAdapter(TgReportsSalesActivity.this, tempProductList);
        recyclerView.setAdapter(salesAdapter);

        TgFPSReportsAllotmentActivity.fontsize = 21;
        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            Usb_Printer.content = teluguPrintData();
        } else {
            Usb_Printer.content = new_englishPrintData();
        }


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
        textData.append("<font size=\"5\">     ఆహార & పౌరసరఫరాల విభాగం \n        తెలంగాణ ప్రభుత్వము </font>" + "\n");
        textData.append("<font size=\"5\">        FPS జారీ నివేదిక</font>" + "\n");
        textData.append("</pre>");

//        textData.append("<font size=\"5\">            స్వీకరణపై</font>" + "\n");
//        textData.append("<font size=\"5\">   ఆహార & పౌరసరఫరాల విభాగం \n      తెలంగాణ ప్రభుత్వము </font>" + "\n");
//        textData.append("<font size=\"5\">       FPS జారీ నివేదిక</font>" + "\n");

        String tx_dt = "తేదీ           :  " + date.trim() + "\n" + "సమయం   :  " + time.trim() + "\n";
        textData.append(tx_dt.replaceAll(" ", "&nbsp;"));
        textData.append("------------------------------------------------------------" + "\n");
        String shop_id = "షాపు ID     :  " + LoginData.getInstance().getShopNo() + "\n";
        textData.append(shop_id.replaceAll(" ", "&nbsp;"));
        textData.append("------------------------------------------------------------" + "\n");

        textData.append("<pre>");
        textData.append(fixedlenghth("వస్తువు", 6) + "          " + "  పరిమాణం" + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        for (Product product : tempProductList) {
            String productName = product.getDisplayName();
            String quantity = String.format("" + product.getIssuedQuantity());
            if (quantity.length() == 3) {
                quantity = "    " + quantity;
            } else if (quantity.length() == 4) {
                quantity = "   " + quantity;
            } else if(quantity.length() == 5) {
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
        textData.append("<font size=\"5\">       FPS ISSUE REPORT</font>" + "\n");

        textData.append(fixedlenght("Date") + date.trim() + "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop Id") + LoginData.getInstance().getShopNo() + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        textData.append(fixedlenghth("Item", 6) + "           " + "Qty" +"\n");
        textData.append("------------------------------------------------------------" + "\n");

        for (Product product : tempProductList) {
            String productName = product.getDisplayName();
            String quantity = String.format("" + product.getIssuedQuantity());
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


    private FPSIssueReport getIssueReport() {
        FPSIssueReport fpsIssueReport = new FPSIssueReport();
        try {

            GregorianCalendar gc = new GregorianCalendar();
         /*   int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);*/

         //   Calendar mCalendar = Calendar.getInstance();
           // String month_txt = mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());


            Date currentData;
            if(Util.needInternalClock && GlobalAppState.serverDate !=null){
                currentData=GlobalAppState.serverDate;
            }else{
                currentData = new Date();
            }

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(currentData);
            System.out.println("formattedDate => " + formattedDate);

            ((TextView) findViewById(R.id.tvMonth)).setText("" + LoginData.getInstance().getCurrentMonth());
            ((TextView) findViewById(R.id.tvYear)).setText("" + LoginData.getInstance().getCurrentYear());

            inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode",LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            inputMap.put("currDate", formattedDate);
            //inputMap.put("currMonth", month);
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
           // inputMap.put("currYear", year);
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            fpsIssueReport = XMLUtil.getFPSIssuesReport(inputMap);
            if(fpsIssueReport.getRespMsgCode().equals("0")){
                isDataFound=true;
            }else{
                isDataFound=false;
            }
            return fpsIssueReport;
        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgReportsSalesActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return fpsIssueReport;
        }catch (final SocketTimeoutException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgReportsSalesActivity.this,"Server Connection Error");
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return fpsIssueReport;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new FPSIssueReport();
        }

    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgReportsSalesActivity.this, TgReportsDashBoardActivity.class);
        startActivity(backIntent);
        finish();
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
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

            Intent i = new Intent(TgReportsSalesActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}