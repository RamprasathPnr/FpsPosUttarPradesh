package com.omneagate.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.io.USBPrinting;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSCardPosition;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;
import com.omneagate.printer.Usb_Printer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TgCardPositionReportActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageViewBack;
    String message;
    private RationCardDetailDialog rationCardDetailDialog;
    private Map<String, Object> inputMap;
    private Button btnPrintAllotment,btnBack;
    private boolean connecting = false;
    private String TAG = "TgCardPositionReportActivity";
    private int string_len = 12;
    private Timer timer;
    private boolean isDataFound=false;
    private LogOutTimerTask logoutTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_card_position_report);
        message = getIntent().getStringExtra("mode");
        initView();
        Usb_Printer.getinstance(TgCardPositionReportActivity.this).check_usb_permission();
        new GetFpsAllotmentDetails().execute();
    }


    class GetFpsAllotmentDetails extends AsyncTask<String, String, FPSCardPosition> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgCardPositionReportActivity.this, getString(R.string.Downloading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected FPSCardPosition doInBackground(String... arg0) {
            try {
                return getCardPosition();
            } catch (Exception e) {
                e.printStackTrace();
                return new FPSCardPosition();
            }
        }

        @Override
        protected void onPostExecute(FPSCardPosition cardPositionResponseDto) {
            super.onPostExecute(cardPositionResponseDto);
            rationCardDetailDialog.cancel();
            setValues(cardPositionResponseDto);
        }
    }

    private void setValues(FPSCardPosition cardPositionResponseDto) {
        ((TextView)findViewById(R.id.aap_card)).setText(cardPositionResponseDto.getRcAap());
        ((TextView)findViewById(R.id.afsc_card)).setText(cardPositionResponseDto.getRcAfsc());
        ((TextView)findViewById(R.id.fsc_card)).setText(cardPositionResponseDto.getRcFsc());

        ((TextView)findViewById(R.id.aap_units)).setText(cardPositionResponseDto.getRcAapUnits());
        ((TextView)findViewById(R.id.afsc_units)).setText(cardPositionResponseDto.getRcAfscUnits());
        ((TextView)findViewById(R.id.fsc_units)).setText(cardPositionResponseDto.getRcFscUnits());

        TgFPSReportsAllotmentActivity.fontsize = 21;
        if (GlobalAppState.language.equalsIgnoreCase("te")) {
            Usb_Printer.content = teluguPrintData(cardPositionResponseDto);
        }else {
            Usb_Printer.content = new_englishPrintData(cardPositionResponseDto);
        }

    }


    private String new_englishPrintData(FPSCardPosition cardPositionResponseDto) {

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
        textData.append("<font size=\"5\">     CARD POSITION REPORT</font>" + "\n");

        textData.append(fixedlenght("Date") + date.trim()+ "\n");
        textData.append(fixedlenght("Time") + time.trim() + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append(fixedlenght("Shop Id") + LoginData.getInstance().getShopNo() + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        textData.append(fixedlenghth("Item", 6) + "   " + "Cards" + "    " + "Units" + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        String typeAap;
        if(cardPositionResponseDto.getRcAap() !=null){
            typeAap= cardPositionResponseDto.getRcAap();
        }else{
            typeAap= "";
        }

        String typeAfsc;
        if(cardPositionResponseDto.getRcAfsc() !=null){
            typeAfsc= cardPositionResponseDto.getRcAfsc();
        }else{
            typeAfsc= "";
        }

        String typeFsc;
        if(cardPositionResponseDto.getRcFsc() !=null){
            typeFsc= cardPositionResponseDto.getRcFsc();
        }else{
            typeFsc= "";
        }


        String unitAap;

        if(cardPositionResponseDto.getRcAapUnits() !=null){
            unitAap= cardPositionResponseDto.getRcAapUnits();
        }else{
            unitAap= "";
        }

        String unitAfsc = cardPositionResponseDto.getRcAfscUnits();
        if(cardPositionResponseDto.getRcAfscUnits() !=null){
            unitAfsc= cardPositionResponseDto.getRcAfscUnits();
        }else{
            unitAfsc= "";
        }

        String unitFSc = cardPositionResponseDto.getRcFscUnits();
        if(cardPositionResponseDto.getRcFscUnits() !=null){
            unitFSc= cardPositionResponseDto.getRcFscUnits();
        }else{
            unitFSc= "";
        }

        if (typeAap.length() == 1) {
            typeAap = "   " + typeAap;
        } else if (typeAap.length() == 2) {
            typeAap = "  " + typeAap;
        } else if (typeAap.length() == 3) {
            typeAap = " " + typeAap;
        }

        if (typeAfsc.length() == 1) {
            typeAfsc = "   " + typeAfsc;
        } else if (typeAfsc.length() == 2) {
            typeAfsc = "  " + typeAfsc;
        } else if (typeAfsc.length() == 3) {
            typeAfsc = " " + typeAfsc;
        }

        if (typeFsc.length() == 1) {
            typeFsc = "   " + typeFsc;
        } else if (typeFsc.length() == 2) {
            typeFsc = "  " + typeFsc;
        } else if (typeFsc.length() == 3) {
            typeFsc = " " + typeFsc;
        }

        if (unitAap.length() == 1) {
            unitAap = "   " + unitAap;
        } else if (unitAap.length() == 2) {
            unitAap = "  " + unitAap;
        } else if (unitAap.length() == 3) {
            unitAap = " " + unitAap;
        }

        if (unitAfsc.length() == 1) {
            unitAfsc = "   " + unitAfsc;
        } else if (unitAfsc.length() == 2) {
            unitAfsc = "  " + unitAfsc;
        } else if (unitAfsc.length() == 3) {
            unitAfsc = " " + unitAfsc;
        }

        if (unitFSc.length() == 1) {
            unitFSc = "   " + unitFSc;
        } else if (unitFSc.length() == 2) {
            unitFSc = "  " + unitFSc;
        } else if (unitFSc.length() == 3) {
            unitFSc = " " + unitFSc;
        }


        textData.append(fixedlenghth("RCAAP", 6) + "   " + typeAap + "  " + unitAap + "\n");
        textData.append(fixedlenghth("RCAFSC", 6) + "   " + typeAfsc + "  " + unitAfsc + "\n");
        textData.append(fixedlenghth("RCFSC", 6) + "   " + typeFsc + "  " + unitFSc + "\n");

        textData.append("------------------------------------------------------------" + "\n");
        textData.append("</pre>");
        textData.append(getResources().getString(R.string.call_issue) + "\n");

        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");


        return  textData.toString();
    }

    private String teluguPrintData(FPSCardPosition cardPositionResponseDto) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        Date currentData;
        if(Util.needInternalClock &&  GlobalAppState.serverDate !=null){
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
        textData.append("<font size=\"5\">        కార్డులు స్థితి నివేదిక </font>" + "\n");
        textData.append("</pre>");

        String tx_dt ="తేదీ           :  " + date.trim() + "\n" + "సమయం   :  " + time.trim()+ "\n";
        textData.append(tx_dt.replaceAll(" ","&nbsp;"));
        textData.append("------------------------------------------------------------" + "\n");
        String shop_id ="షాపు ID     :  " + LoginData.getInstance().getShopNo() + "\n";
        textData.append(shop_id.replaceAll(" ","&nbsp;"));
        textData.append("------------------------------------------------------------" + "\n");

        textData.append("<pre>");
        textData.append(fixedlenghth("వస్తువు", 6) + "   " + "  కార్డులు" + "  " + " యూనిట్లు" + "\n");
        textData.append("------------------------------------------------------------" + "\n");

        String typeAap;
        if(cardPositionResponseDto.getRcAap() !=null){
            typeAap= cardPositionResponseDto.getRcAap();
        }else{
            typeAap= "";
        }

        String typeAfsc;
        if(cardPositionResponseDto.getRcAfsc() !=null){
            typeAfsc= cardPositionResponseDto.getRcAfsc();
        }else{
            typeAfsc= "";
        }

        String typeFsc;
        if(cardPositionResponseDto.getRcFsc() !=null){
            typeFsc= cardPositionResponseDto.getRcFsc();
        }else{
            typeFsc= "";
        }


        String unitAap;

        if(cardPositionResponseDto.getRcAapUnits() !=null){
            unitAap= cardPositionResponseDto.getRcAapUnits();
        }else{
            unitAap= "";
        }

        String unitAfsc = cardPositionResponseDto.getRcAfscUnits();
        if(cardPositionResponseDto.getRcAfscUnits() !=null){
            unitAfsc= cardPositionResponseDto.getRcAfscUnits();
        }else{
            unitAfsc= "";
        }

        String unitFSc = cardPositionResponseDto.getRcFscUnits();
        if(cardPositionResponseDto.getRcFscUnits() !=null){
            unitFSc= cardPositionResponseDto.getRcFscUnits();
        }else{
            unitFSc= "";
        }

        if (typeAap.length() == 1) {
            typeAap = "   " + typeAap;
        } else if (typeAap.length() == 2) {
            typeAap = "  " + typeAap;
        } else if (typeAap.length() == 3) {
            typeAap = " " + typeAap;
        }

        if (typeAfsc.length() == 1) {
            typeAfsc = "   " + typeAfsc;
        } else if (typeAfsc.length() == 2) {
            typeAfsc = "  " + typeAfsc;
        } else if (typeAfsc.length() == 3) {
            typeAfsc = " " + typeAfsc;
        }

        if (typeFsc.length() == 1) {
            typeFsc = "   " + typeFsc;
        } else if (typeFsc.length() == 2) {
            typeFsc = "  " + typeFsc;
        } else if (typeFsc.length() == 3) {
            typeFsc = " " + typeFsc;
        }

        if (unitAap.length() == 1) {
            unitAap = "   " + unitAap;
        } else if (unitAap.length() == 2) {
            unitAap = "  " + unitAap;
        } else if (unitAap.length() == 3) {
            unitAap = " " + unitAap;
        }

        if (unitAfsc.length() == 1) {
            unitAfsc = "   " + unitAfsc;
        } else if (unitAfsc.length() == 2) {
            unitAfsc = "  " + unitAfsc;
        } else if (unitAfsc.length() == 3) {
            unitAfsc = " " + unitAfsc;
        }

        if (unitFSc.length() == 1) {
            unitFSc = "   " + unitFSc;
        } else if (unitFSc.length() == 2) {
            unitFSc = "  " + unitFSc;
        } else if (unitFSc.length() == 3) {
            unitFSc = " " + unitFSc;
        }


        textData.append(fixedlenghth("RCAAP", 6) + "   " + typeAap + "  " + unitAap + "\n");
        textData.append(fixedlenghth("RCAFSC", 6) + "   " + typeAfsc + "  " + unitAfsc + "\n");
        textData.append(fixedlenghth("RCFSC", 6) + "   " + typeFsc + "  " + unitFSc + "\n");
        textData.append("------------------------------------------------------------" + "\n");
        textData.append("</pre>");
        textData.append(getResources().getString(R.string.call_issue) + "\n");

        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");


        return  textData.toString();
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

    private FPSCardPosition getCardPosition() {
        FPSCardPosition fpsCardPosition = new FPSCardPosition();
        try {
            GregorianCalendar gc = new GregorianCalendar();
          /*  int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);*/
            inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode",LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
           // inputMap.put("currYear", year);
          //  inputMap.put("currMonth", month);
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");

            fpsCardPosition = XMLUtil.getFPSCardPosition(inputMap);
            if(fpsCardPosition.getRespMsgCode().equals("0")){
                isDataFound=true;
            }else{
                isDataFound=false;
            }

            return fpsCardPosition;
        }catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgCardPositionReportActivity.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return fpsCardPosition;
        } catch (Exception e) {
            e.printStackTrace();
            return new FPSCardPosition();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;

            case R.id.btnPrintCards:
               if(!isDataFound){
                   Toast.makeText(TgCardPositionReportActivity.this,getString(R.string.data_not_found), Toast.LENGTH_SHORT).show();
                   return;
               }

                if (!connecting) {
                    Usb_Printer.getinstance(TgCardPositionReportActivity.this).connectPrinter_new();
                } else {
                    Toast.makeText(TgCardPositionReportActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnBack:
                onBackPressed();
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
        ((TextView) findViewById(R.id.top_textView)).setText(message);
        btnPrintAllotment = (Button) findViewById(R.id.btnPrintCards);
        btnPrintAllotment.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgCardPositionReportActivity.this, TgReportsDashBoardActivity.class);
        startActivity(backIntent);
        finish();
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

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


            Intent i = new Intent(TgCardPositionReportActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
