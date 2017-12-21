package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSCurrentClosingBalance;
import com.omneagate.DTO.FPSLastTransaction;
import com.omneagate.DTO.FPSRationCardDetails;

import com.omneagate.DTO.RCLastTransaction;
import com.omneagate.Util.FpsMemberData;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.exception.FPSException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TgSalesActivity extends BaseActivity implements View.OnClickListener {


    private TextView top_textView;
    private ImageView imageViewBack;
    public  EditText edtRationcardNumber;
    private Button bt_submit,btnBack;
    private Button bt_lastTransaction;
    private String rationCardNumber;
    private RationCardDetailDialog rationCardDetailDialog;
    private final String TAG = TgSalesActivity.class.getCanonicalName();
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private WebView webView;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_sales);
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.sales_top_heading));

        initView();
    }

    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        top_textView = (TextView) findViewById(R.id.top_textView);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        edtRationcardNumber = (EditText) findViewById(R.id.login_password);

        edtRationcardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(edtRationcardNumber.getText().toString().length() == 12){
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(edtRationcardNumber.getWindowToken(), 0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_lastTransaction = (Button) findViewById(R.id.bt_lastTransaction);
        bt_submit.setOnClickListener(this);
        bt_lastTransaction.setOnClickListener(this);
        imageViewBack.setOnClickListener(this);
        edtRationcardNumber.setText(XMLUtil.PREFIX_RCNO + LoginData.getInstance().getDistCode() + XMLUtil.POSTFIX_RCNO);

        networkConnection = new NetworkConnection(TgSalesActivity.this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        webView= (WebView)findViewById(R.id.webview);
        webView.getSettings();
        webView.setBackgroundColor(Color.parseColor("#EEEEEE"));


        try {
            if (LoginData.getInstance().getEposMessage() !=null && !LoginData.getInstance().getEposMessage().equalsIgnoreCase("NA")) {
                String summary = "<html><FONT color='#ff0000' size='5'><marquee behavior='scroll' direction='left' scrollamount='5'>"
                        + LoginData.getInstance().getEposMessage() + "</marquee></FONT></html>";
                webView.loadData(summary, "text/html", "utf-8");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_submit:

                if (networkConnection.isNetworkAvailable()) {
                    if (edtRationcardNumber.getText().toString().trim().isEmpty()) {
                        Toast.makeText(TgSalesActivity.this, R.string.please_enter_ration, Toast.LENGTH_SHORT).show();
                    }
                    LoginData.getInstance().setRcNoEntered(edtRationcardNumber.getText().toString().trim());
                    String LastSaleRCNumber = null;
                    SharedPreferences prefs = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE);
                    if (prefs != null) {
                        LastSaleRCNumber = prefs.getString("LastSaleRCNumber", null);
                    }

                    if (LastSaleRCNumber != null && Util.allowImmediateSale) {
                        if (edtRationcardNumber.getText().toString().trim().equals(LastSaleRCNumber)) {
                            // Toast.makeText(TgSalesActivity.this, R.string.enterdiff, Toast.LENGTH_SHORT).show();
                            TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgSalesActivity.this, getResources().getString(R.string.enterdiff));
                            tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                            tgGenericErrorDialog.show();
                        } else {
                            new GetFpsBeneficiaryDetails().execute();
                        }
                    } else {
                        new GetFpsBeneficiaryDetails().execute();
                    }
                } else {
                    displayNoInternetDailog();
                }

                break;
            case R.id.bt_lastTransaction:

                if (networkConnection.isNetworkAvailable()) {
                    if (edtRationcardNumber.getText().toString().trim().isEmpty()) {
                        Toast.makeText(TgSalesActivity.this, R.string.please_enter_ration, Toast.LENGTH_SHORT).show();
                    } else {
                        LoginData.getInstance().setRcNoEntered(edtRationcardNumber.getText().toString().trim());
                        new GetLastTransaction().execute();
                    }
                } else {
                    displayNoInternetDailog();
                }


                break;
            case R.id.imageViewBack:
                Intent imageViewBack = new Intent(TgSalesActivity.this, TgPaymentModeActivity.class);
                startActivity(imageViewBack);
                finish();
                break;

            case R.id.btnBack:
                Intent intentBack = new Intent(TgSalesActivity.this, TgPaymentModeActivity.class);
                startActivity(intentBack);
                finish();
                break;
            default:
                break;
        }
    }

    class GetFpsBeneficiaryDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgSalesActivity.this, getString(R.string.fpsMemberLoading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected String doInBackground(String... arg0) {
            return getFpsBeneficiaryDetails();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            rationCardDetailDialog.dismiss();
        }
    }


    private RCLastTransaction getLastTransactionDetails() {
        RCLastTransaction rcLastTransaction = new RCLastTransaction();
        try {
            GregorianCalendar gc = new GregorianCalendar();
         /*   int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);*/
            rationCardNumber = edtRationcardNumber.getText().toString();
            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
          //  inputMap.put("currYear", year);
          //  inputMap.put("currMonth", month);
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
            inputMap.put("rcNo", rationCardNumber);
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");
            rcLastTransaction = XMLUtil.getRCLastTransction(inputMap);
            Log.e("rcLastTransaction ::", "" + rcLastTransaction);
            if (rcLastTransaction.getRespMsgCode().contains("0")) {
                Intent last_tnx = new Intent(TgSalesActivity.this, TgViewLastTransactionActivity.class);
                last_tnx.putExtra("rcLastTransaction", rcLastTransaction);
                last_tnx.putExtra("rcNumber", rationCardNumber);
                last_tnx.putExtra("Fps_id", LoginData.getInstance().getShopNo());
                startActivity(last_tnx);
                finish();
            }

        } catch (final FPSException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgSalesActivity.this, "" + e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return rcLastTransaction;
    }


    class GetLastTransaction extends AsyncTask<String, String, RCLastTransaction> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            rationCardDetailDialog = new RationCardDetailDialog(TgSalesActivity.this, getString(R.string.Downloading));
            rationCardDetailDialog.setCanceledOnTouchOutside(false);
            rationCardDetailDialog.show();
        }

        protected RCLastTransaction doInBackground(String... arg0) {
            try {
                return getLastTransactionDetails();
            } catch (Exception e) {
                e.printStackTrace();
                return new RCLastTransaction();
            }
        }

        @Override
        protected void onPostExecute(RCLastTransaction rcLastTransaction) {
            super.onPostExecute(rcLastTransaction);
            rationCardDetailDialog.cancel();
//            setValues(rcLastTransaction);
        }
    }

    void displayNoInternetDailog() {
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgSalesActivity.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
    }

    private String getFpsBeneficiaryDetails() {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            int month = gc.get(Calendar.MONTH) + 1;
            int year = gc.get(Calendar.YEAR);

            Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
            inputMap.put("distCode", LoginData.getInstance().getDistCode());
            inputMap.put("shopNo", LoginData.getInstance().getShopNo());
            rationCardNumber = edtRationcardNumber.getText().toString();
            inputMap.put("rationCard", rationCardNumber);
           // inputMap.put("currYear", year);
           // inputMap.put("currMonth", month);
            inputMap.put("payType","" + LoginData.getInstance().getPayType());
            inputMap.put("currYear","" + LoginData.getInstance().getCurrentYear());
            inputMap.put("currMonth", "" + LoginData.getInstance().getCurrentMonth());
            Log.e(TAG, "<==== Transaction ID ====>" + LoginData.getInstance().getTransactionId());
            inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
            inputMap.put("password", "1159abbb8b6c0b8a8964210af6954b17");

            FPSRationCardDetails fpsRationCardDetails = XMLUtil.getRationCardDetails(inputMap);

            Log.e(TAG, "<===== FPS Beneficiary Member Response =====> " + fpsRationCardDetails);

            if (fpsRationCardDetails.getRespMsgCode().contains("0")) {
                Intent in = new Intent(TgSalesActivity.this, TgSalesEntryActivity.class);
                FpsMemberData.getInstance().setFpsRationCardDetails(fpsRationCardDetails);
                FpsMemberData.getInstance().setRcNo(rationCardNumber);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //  in.putExtra("FpsBeneficiarydetail", fpsRationCardDetails);
                //  in.putExtra("rationCardNumber",rationCardNumber);
                startActivity(in);
                finish();
            }

            return fpsRationCardDetails.toString();

        } catch (final FPSException e) {
            Log.e(TAG, "EXception msg" + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor = getSharedPreferences("FPS_TELANGANA", MODE_PRIVATE).edit();
                    editor.putString("LastSaleRCNumber", null);
                    editor.commit();

                    try {
                        if (LoginData.getInstance().getEposMessage() !=null && !LoginData.getInstance().getEposMessage().equalsIgnoreCase("NA")) {
                            String summary = "<html><FONT color='#ff0000' size='5'><marquee behavior='scroll' direction='left' scrollamount='5'>"
                                    + LoginData.getInstance().getEposMessage() + "</marquee></FONT></html>";
                            webView.loadData(summary, "text/html", "utf-8");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgSalesActivity.this, "" + e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public void onBackPressed() {
        Intent intentSale = new Intent(TgSalesActivity.this, TgPaymentModeActivity.class);
        startActivity(intentSale);
        finish();

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
    protected void onDestroy() {
        super.onDestroy();
        LoginData.getInstance().setRcNoEntered(null);
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
            Intent i = new Intent(TgSalesActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
