package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSCurrentClosingBalance;
import com.omneagate.DTO.GeneralResponse;
import com.omneagate.DTO.Product;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.RoKeroseneSucessDialog;
import com.omneagate.activity.dialog.RoSucessDialog;
import com.omneagate.activity.dialog.TgDownLoadStockDetail;
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

public class TgReceiveKeroseneGoodsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageViewBack;
    private NoDefaultSpinner monthSpinner;
    private NoDefaultSpinner yearSpinner;
    private Button btnSubmit,btnBack;
    private int i_month;
    private String strMonth, strYear;
    private TgDownLoadStockDetail roLoadingDeatils;
    private final String TAG = TgReceiveGoods.class.getCanonicalName();
    private EditText roNumber, keroseneQty;
    private RationCardDetailDialog rationCardDetailDialog;
    private Timer timer;
    private int string_len = 12;
    private boolean connecting = false;
    LogOutTimerTask logoutTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_receive_kerosene_goodsactivity);
        initView();
    }

    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        Usb_Printer.getinstance(TgReceiveKeroseneGoodsActivity.this).check_usb_permission();
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        roLoadingDeatils = new TgDownLoadStockDetail(TgReceiveKeroseneGoodsActivity.this);
        monthSpinner = (NoDefaultSpinner) findViewById(R.id.monthSpinner);
        yearSpinner = (NoDefaultSpinner) findViewById(R.id.yearSpinner);
        roNumber = (EditText) findViewById(R.id.ro_num);
        keroseneQty = (EditText) findViewById(R.id.edQty);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        ((TextView) findViewById(R.id.top_textView)).setText(R.string.receive_kerosene);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

      //  int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentMonth=calendar.get(Calendar.MONTH);

        String month[] = {getResources().getString(R.string.jan_t), getResources().getString(R.string.feb_t), getResources().getString(R.string.mar_t), getResources().getString(R.string.apr_t), getResources().getString(R.string.may_t),
                getResources().getString(R.string.jun_t), getResources().getString(R.string.jul_t), getResources().getString(R.string.aug_t), getResources().getString(R.string.sep_t), getResources().getString(R.string.oct_t),
                getResources().getString(R.string.nov_t), getResources().getString(R.string.dec_t),};
        networkConnection = new NetworkConnection(TgReceiveKeroseneGoodsActivity.this);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, month);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        monthSpinner.setAdapter(dataAdapter);

        monthSpinner.setSelection(currentMonth);


        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == -1) {
                    return;
                }
                strMonth = parent.getItemAtPosition(position).toString();


                switch (strMonth) {
                    case "JANUARY":
                        i_month = 1;
                        break;
                    case "FEBURARY":
                        i_month = 2;
                        break;
                    case "MARCH":
                        i_month = 3;
                        break;
                    case "APRIL":
                        i_month = 4;
                        break;
                    case "MAY":
                        i_month = 5;
                        break;
                    case "JUNE":
                        i_month = 6;
                        break;
                    case "JULY":
                        i_month = 7;
                        break;
                    case "AUGUST":
                        i_month = 8;
                        break;
                    case "SEPTEMBER":
                        i_month = 9;
                        break;
                    case "OCTOBER":
                        i_month = 10;
                        break;
                    case "NOVEMBER":
                        i_month = 11;
                        break;
                    case "DECEMBER":
                        i_month = 12;
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        List<String> Year = new ArrayList<String>();
        GregorianCalendar gc = new GregorianCalendar();
        int i_month = gc.get(Calendar.MONTH) + 1;
        int i_year = gc.get(Calendar.YEAR);
        if (i_month == 12) {
            Year.add("" + i_year);
            Year.add("" + (i_year + 1));
        } else {
            Year.add("" + i_year);
        }
       /* Year.add("2016");
        Year.add("2017");
        Year.add("2018");
*/



        /*String Year[] = {"2016", "2017", "2018"};*/

        String this_yr = String.valueOf(year);
        int position = 0;

        for (int i = 0; i < Year.size(); i++) {
            if (Year.get(i).toString().equals(this_yr)) {
                position = i;
            }

            break;
        }
        ArrayAdapter<String> dataAdapteryear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Year);
        dataAdapteryear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        yearSpinner.setAdapter(dataAdapteryear);
        yearSpinner.setSelection(position);


        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                strYear = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;

            case R.id.btnSubmit:
                if (strMonth == null) {
                    Toast.makeText(TgReceiveKeroseneGoodsActivity.this, getString(R.string.please_select_month), Toast.LENGTH_SHORT).show();
                    return;
                } else if (strYear == null) {
                    Toast.makeText(TgReceiveKeroseneGoodsActivity.this, getString(R.string.please_select_year), Toast.LENGTH_SHORT).show();
                    return;
                } else if (roNumber.getText().toString().trim().isEmpty()) {
                    Toast.makeText(TgReceiveKeroseneGoodsActivity.this, getString(R.string.please_enter_ro_number), Toast.LENGTH_SHORT).show();
                    return;
                } else if (keroseneQty.getText().toString().trim().isEmpty()) {
                    Toast.makeText(TgReceiveKeroseneGoodsActivity.this, getString(R.string.please_enter_qty), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (networkConnection.isNetworkAvailable()) {
                        new PostRoKeroseneDetails().execute();
                    } else {
                        displayNoInternetDailog();
                    }

                }

                break;

            case R.id.btnBack:
                onBackPressed();
                break;

            default:
                break;

        }

    }

    public void Print() {
        if (!connecting) {
            Usb_Printer.getinstance(TgReceiveKeroseneGoodsActivity.this).connectPrinter_new();
        } else {
            Toast.makeText(TgReceiveKeroseneGoodsActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
        }
    }

    void displayNoInternetDailog(){
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgReceiveKeroseneGoodsActivity.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
    }

    class PostRoKeroseneDetails extends AsyncTask<String, String, GeneralResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgReceiveKeroseneGoodsActivity.this, getString(R.string.logout_status));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected GeneralResponse doInBackground(String... arg0) {
            try {
                return postROKeroseneDetsils();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(GeneralResponse generalResponse) {
            super.onPostExecute(generalResponse);
            rationCardDetailDialog.cancel();


            if (generalResponse!=null && generalResponse.getRespMsgCode().contains("0")) {
                if (GlobalAppState.language.equalsIgnoreCase("te")) {
                    TgFPSReportsAllotmentActivity.fontsize = 18;
                    Usb_Printer.content = teluguPrintData(generalResponse);
                } else {
                    TgFPSReportsAllotmentActivity.fontsize = 19;
                    Usb_Printer.content = new_englishPrintData(generalResponse);
                }

                if (generalResponse.getRespMsgCode().contains("0")) {
                    btnSubmit.setVisibility(View.INVISIBLE);
                    RoKeroseneSucessDialog roSucessDialog = new RoKeroseneSucessDialog(TgReceiveKeroseneGoodsActivity.this);
                    roSucessDialog.setCanceledOnTouchOutside(false);
                    roSucessDialog.show();

                }
            }
        }


        public GeneralResponse postROKeroseneDetsils() {
            GeneralResponse generalResponse = new GeneralResponse();
            try {

                Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
                inputMap.put("distCode", LoginData.getInstance().getDistCode());
                inputMap.put("shopNo", LoginData.getInstance().getShopNo());
                inputMap.put("currYear", strYear);
                inputMap.put("currMonth", i_month);
                inputMap.put("roNo", roNumber.getText().toString().trim());
                inputMap.put("koilQty", keroseneQty.getText().toString().trim());
                inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
                inputMap.put("password", XMLUtil.PASSWORD);
                generalResponse = XMLUtil.postKeroseneRODetails(inputMap);


                return generalResponse;
            } catch (final FPSException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgReceiveKeroseneGoodsActivity.this, "" + e.getMessage());
                        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                        tgGenericErrorDialog.show();
                    }
                });
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }



        




        private String teluguPrintData(GeneralResponse generalResponse) {

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
            textData.append("<font size=\"5\">      FPS ప్రస్తుత ముగింపు నిలువ</font>" + "\n");
            textData.append("</pre>");
            String tx_dt = "తేదీ                 :  " + date.trim() + "\n" + "సమయం         :  " + time.trim() + "\n";
            textData.append(tx_dt.replaceAll(" ", "&nbsp;"));
            textData.append("----------------------------------------------------------------------" + "\n");
            String shop_id = "షాపు ID           :  " +  LoginData.getInstance().getShopNo()+ "\n";
            textData.append(shop_id.replaceAll(" ", "&nbsp;"));

            textData.append("----------------------------------------------------------------------" + "\n");
            String ro="కిరోసిన్  Ro            :" + "  " + roNumber.getText().toString() + "\n";
            textData.append(ro.replaceAll(" ", "&nbsp;"));
            String qty="కిరోసిన్  పరిమాణం  :" + "  " + keroseneQty.getText().toString() + "\n";
            textData.append(qty.replaceAll(" ", "&nbsp;"));

            textData.append("----------------------------------------------------------------------" + "\n");
            textData.append("</pre>");
            textData.append(getResources().getString(R.string.call_issue) + "\n");
            textData.append("\n");
            textData.append("\n");
            return textData.toString();
        }


        private String new_englishPrintData(GeneralResponse generalResponse) {

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
            textData.append("<font size=\"5\">       KEROSENE RECEIPT</font>" + "\n");

            textData.append(fixedlenght("Date") + date.trim() + "\n");
            textData.append(fixedlenght("Time") + time.trim() + "\n");
            textData.append("------------------------------------------------------------" + "\n");
            textData.append(fixedlenght("Shop Id") + LoginData.getInstance().getShopNo() + "\n");
            textData.append("------------------------------------------------------------" + "\n");
            textData.append("Kerosene Ro  : " + " " + roNumber.getText().toString() + "\n");
            textData.append("Kerosene Qty : " + " " + keroseneQty.getText().toString() + "\n");
            textData.append("------------------------------------------------------------" + "\n");
            textData.append("</pre>");
            textData.append(getResources().getString(R.string.call_issue) + "\n");
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




      /*  @Override
        public void onBackPressed() {
            Intent backIntent = new Intent(TgReceiveKeroseneGoodsActivity.this, TgDashBoardActivity.class);
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


        }

        @Override
        protected void onResume() {
            super.onResume();

            timer = new Timer();
            Log.i("Main", "Invoking logout timer");
            LogOutTimerTask logoutTimeTask = new LogOutTimerTask();
            timer.schedule(logoutTimeTask, 60000); //auto logout in 5 minutes

        }
*/



    }
    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }
    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgReceiveKeroseneGoodsActivity.this, TgDashBoardActivity.class);
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
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgReceiveKeroseneGoodsActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
