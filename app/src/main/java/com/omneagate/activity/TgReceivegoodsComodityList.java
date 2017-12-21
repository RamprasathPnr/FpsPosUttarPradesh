package com.omneagate.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.Adapter.ReceiveGoodsAdapter;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSCurrentClosingBalance;
import com.omneagate.DTO.FPSROAllocation;
import com.omneagate.DTO.GeneralResponse;
import com.omneagate.DTO.Product;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NonScrollListView;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.RoSucessDialog;
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

public class TgReceivegoodsComodityList extends BaseActivity implements View.OnClickListener{
    List<Product> product;
    private ImageView imageViewBack;
    String month,year;
    private EditText monthedittext,yearedittext,date_edit_text,ro_trans_edit_text;
    Calendar c = Calendar.getInstance();
    String server_format="";
    NonScrollListView productsearch;
    int loadMore = 0;
    TextView btnUpdateStock,btnExit,btnBack;
    ReceiveGoodsAdapter adapter;
    RationCardDetailDialog rationCardDetailDialog;
    FPSROAllocation fpsroAllocation;
    private int string_len = 12;
    private List<Product> tempProductList;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private boolean connecting = false;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_receivegoods_comodity_list);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                Intent backIntent = new Intent(TgReceivegoodsComodityList.this, TgReceiveGoods.class);
                startActivity(backIntent);
                finish();
                break;

            case R.id.btnUpdateStock:
                try {
                    if (networkConnection.isNetworkAvailable()) {
                        for(int i=0;i<tempProductList.size();i++){
                            View view=productsearch.getChildAt(i);
                            EditText editText=(EditText)view.findViewById(R.id.received_stock);
                            String valueEntered=editText.getText().toString();
                            Log.e("Receieve Goods", "value entered : " + valueEntered);
                            double qty = Double.parseDouble(valueEntered);
                            tempProductList.get(i).setReceivedQuantity(qty);
                            for(int j=0;j<fpsroAllocation.getFpsProductList().size();j++){
                                if(tempProductList.get(i).getCode().equals(fpsroAllocation.getFpsProductList().get(j).getCode())){
                                    fpsroAllocation.getFpsProductList().get(j).setReceivedQuantity(qty);
                                }

                            }
                        }

                        new PostRoDeatils().execute();
                    } else {
                        displayNoInternetDailog();
                    }

                }catch (NumberFormatException e){
                    e.printStackTrace();
                 Toast.makeText(TgReceivegoodsComodityList.this,getString(R.string.enter_receive),Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btnExit:
                try {
                    Intent exitIntent = new Intent(TgReceivegoodsComodityList.this, TgReceiveGoods.class);
                    startActivity(exitIntent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btnBack:
                try {
                    Intent exitIntent = new Intent(TgReceivegoodsComodityList.this, TgReceiveGoods.class);
                    startActivity(exitIntent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;

        }

    }
    private void initView() {
        Usb_Printer.getinstance(TgReceivegoodsComodityList.this).check_usb_permission();
        networkConnection = new NetworkConnection(TgReceivegoodsComodityList.this);
        setPopUpPage();
        updateDateTime();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        fpsroAllocation=new FPSROAllocation();
        //if(getIntent().getExtras().containsKey("roDetails")){
            fpsroAllocation = (FPSROAllocation) getIntent().getSerializableExtra("roDetails");
      //  }

        month=getIntent().getExtras().getString("month");
        year=getIntent().getExtras().getString("year");
        monthedittext = (EditText) findViewById(R.id.edt_month);
        monthedittext.setText(month);


        yearedittext = (EditText) findViewById(R.id.edt_year);
        yearedittext.setText(year);

        date_edit_text=(EditText) findViewById(R.id.edt_date);
        date_edit_text.setText(fpsroAllocation.getTransDate());
        date_edit_text.setKeyListener(null);

        ro_trans_edit_text=(EditText)findViewById(R.id.edt_ro);
        ro_trans_edit_text.setText(fpsroAllocation.getTransRoNo());

        btnUpdateStock=(TextView)findViewById(R.id.btnUpdateStock);
        btnUpdateStock.setOnClickListener(this);

        btnExit=(TextView)findViewById(R.id.btnExit);
        btnExit.setOnClickListener(this);

        btnBack=(TextView)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        ((TextView) findViewById(R.id.top_textView)).setText(R.string.receive_goods);

        productsearch = (NonScrollListView) findViewById(R.id.productlist);

        tempProductList=new ArrayList<>();

        for(int i=0;i<fpsroAllocation.getFpsProductList().size();i++){
            if(fpsroAllocation.getFpsProductList().get(i).getIssuedQuantity() >0){
              tempProductList.add(fpsroAllocation.getFpsProductList().get(i));
            }
        }
       Log.e("Receieve Goods", "list size : " + tempProductList);
        adapter = new ReceiveGoodsAdapter(this, tempProductList);
        productsearch.setAdapter(adapter);

    }
    public void PrintData() {

        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            TgFPSReportsAllotmentActivity.fontsize = 18;
            Usb_Printer.content = teluguPrintData(tempProductList);
        } else {
            TgFPSReportsAllotmentActivity.fontsize = 20;
            Usb_Printer.content = new_englishPrintData(tempProductList);
        }
    }

    public void Print() {
        if (!connecting) {
            Usb_Printer.getinstance(TgReceivegoodsComodityList.this).connectPrinter_new();
        } else {
            Toast.makeText(TgReceivegoodsComodityList.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
        }
    }
    void displayNoInternetDailog(){
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgReceivegoodsComodityList.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
    }

    @Override
    public void onBackPressed() {
        Intent backIntent =new Intent(TgReceivegoodsComodityList.this,TgReceiveGoods.class);
        startActivity(backIntent);
        finish();

    }

    private class PostRoDeatils extends AsyncTask<String, GeneralResponse, GeneralResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgReceivegoodsComodityList.this, getString(R.string.loading_please_wait));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();

        }

        @Override
        protected GeneralResponse doInBackground(String... params) {

            return PostRcSale();
        }

        @Override
        protected void onPostExecute(GeneralResponse generalResponse) {
            super.onPostExecute(generalResponse);
            rationCardDetailDialog.cancel();
            Log.e("ScanFingerPrintActivity","dealerAuthResponse :"+generalResponse);

            if(generalResponse !=null && generalResponse.getRespMsgCode().equals("0")){
                btnUpdateStock.setVisibility(View.GONE);
                btnExit.setVisibility(View.VISIBLE);
                PrintData();
                RoSucessDialog roSucessDialog =new RoSucessDialog(TgReceivegoodsComodityList.this);
                roSucessDialog.setCanceledOnTouchOutside(false);
                roSucessDialog.show();

            }

        }
    }
    private String new_englishPrintData(List<Product> productList) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date currentData = new Date();
        String date = dateFormat.format(currentData);
        dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = dateFormat.format(currentData);

        StringBuilder textData = new StringBuilder();
        textData.append("<pre>");
        textData.append("<font size=\"5\">            RECEIPT</font>" + "\n");
        textData.append("<font size=\"5\">  Food & Civil Supplies Dept \n       Govt of Telangana</font>" + "\n");
        textData.append("<font size=\"5\">       RECEIVE GOODS</font>" + "\n");

        textData.append(fixedlenght("Date") + date.trim() + "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop Id") + LoginData.getInstance().getShopNo() + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        textData.append(fixedlenghth("Item", 6) + "    " + " Ro Qty" +"  "+"Recv Qty"+"\n");
        textData.append("------------------------------------------------------------" + "\n");

        for (Product product : productList) {
            String productName = product.getDisplayName();
            String roQuantity = String.format("" + product.getIssuedQuantity().intValue());
            String recvQuantity=String.format("" + product.getReceivedQuantity().intValue());
            if (roQuantity.length() == 3) {
                roQuantity = "  " + roQuantity;
            } else if (roQuantity.length() == 4) {
                roQuantity = " " + roQuantity;
            }

            if (recvQuantity.length() == 1) {
                recvQuantity = "   " + recvQuantity;
            } else if (recvQuantity.length() == 2) {
                recvQuantity = "   " + recvQuantity;
            } else if (recvQuantity.length() == 3) {
                recvQuantity = "  " + recvQuantity;
            } else if (recvQuantity.length() == 4) {
                recvQuantity = " " + recvQuantity;
            }
            textData.append(fixedlenghth(productName, 6) + "     " + roQuantity+ "   "+recvQuantity+ "\n");
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
    private String teluguPrintData(List<Product> productList) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date currentData = new Date();
        String date = dateFormat.format(currentData);
        dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String time = dateFormat.format(currentData);

        StringBuilder textData = new StringBuilder();


        textData.append("<pre>");
        textData.append("<font size=\"5\">            స్వీకరణపై</font>" + "\n");
        textData.append("<font size=\"5\">    ఆహార & పౌరసరఫరాల విభాగం \n        తెలంగాణ ప్రభుత్వము </font>" + "\n");
        textData.append("<font size=\"5\">      సరకులు స్వీకరించండి </font>" + "\n");
        textData.append("</pre>");

        String tx_dt = "తేదీ                   :  " + date.trim() + "\n" + "సమయం           :  " + time.trim() + "\n";
        textData.append(tx_dt.replaceAll(" ", "&nbsp;"));
        textData.append("----------------------------------------------------------------------" + "\n");

        String shop_id = "షాపు ID             :  " + LoginData.getInstance().getShopNo() + "\n";
        textData.append(shop_id.replaceAll(" ", "&nbsp;"));
        textData.append("--------------------------------------------------------------------------" + "\n");

        textData.append("<pre>");
        textData.append(fixedlenghth("వస్తువు", 6) + "     " + "R O పరిమాణమo" + "  " + "స్వీకర. సరకు" + "\n");
        textData.append("--------------------------------------------------------------------" + "\n");

        for (Product product : productList) {

            String productName = product.getDisplayName();
            String roQuantity = "" + product.getIssuedQuantity().intValue();
            String recvQuantity = "" + product.getReceivedQuantity().intValue();

            if (roQuantity.length() == 3) {
                roQuantity = "  " + roQuantity;
            } else if (roQuantity.length() == 4) {
                roQuantity = " " + roQuantity;
            }

            if (recvQuantity.length() == 1) {
                recvQuantity = "   " + recvQuantity;
            } else if (recvQuantity.length() == 2) {
                recvQuantity = "   " + recvQuantity;
            } else if (recvQuantity.length() == 3) {
                recvQuantity = "  " + recvQuantity;
            } else if (recvQuantity.length() == 4) {
                recvQuantity = " " + recvQuantity;
            }

            textData.append(fixedlenghth(productName, 6) + "       " + roQuantity + "       " + recvQuantity + "\n");
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
    private GeneralResponse PostRcSale(){
        GeneralResponse generalResponse;
        try{
            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();

            GregorianCalendar gc = new GregorianCalendar();

         /*   int month=gc.get(Calendar.MONTH)+1;
            int year =gc.get(Calendar.YEAR);*/

            inputMap.put("distCode",LoginData.getInstance().getDistCode());
            inputMap.put("shopNo",LoginData.getInstance().getShopNo());
            inputMap.put("roNo", fpsroAllocation.getTransRoNo());
            inputMap.put("currYear", year);
            inputMap.put("currMonth", month);
            inputMap.put("totalAmt","0");
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");

              generalResponse= XMLUtil.postRODetails(inputMap,fpsroAllocation.getFpsProductList());
        }catch (final FPSException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgReceivegoodsComodityList.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return null;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return generalResponse;
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
    protected void onPause() {
        super.onPause();

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
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgReceivegoodsComodityList.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
