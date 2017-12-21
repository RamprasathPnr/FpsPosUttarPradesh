package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSDealer;
import com.omneagate.DTO.FPSDealerDetails;
import com.omneagate.DTO.FPSROAllocation;
import com.omneagate.DTO.Product;
import com.omneagate.DTO.UserDto.MonthDto;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.TgDownLoadStockDetail;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.activity.dialog.TgLoginFailureDialogue;
import com.omneagate.exception.FPSException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TgReceiveGoods extends BaseActivity implements View.OnClickListener {
    private ImageView imageViewBack;
    private TextView monthSpinnerLayout;
    private TextView fingerHeadingTv;
    private Spinner monthSpinner, yearSpinner;
    String strMonth, strYear;
    TgDownLoadStockDetail roLoadingDeatils;
    int i_month;
    private Button btnSubmit,btnBack;
    private Timer timer;
    private final String TAG =TgReceiveGoods.class.getCanonicalName();
    private LogOutTimerTask logoutTimeTask;


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_receive_goods);
        roLoadingDeatils = new TgDownLoadStockDetail(TgReceiveGoods.this);

        initView();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                Intent backIntent = new Intent(TgReceiveGoods.this, TgDashBoardActivity.class);
                startActivity(backIntent);
                finish();
                break;

            case R.id.btnSubmit:
                if(strMonth ==null){
                    Toast.makeText(TgReceiveGoods.this,getString(R.string.please_select_month),Toast.LENGTH_SHORT).show();

                }else if(strYear ==null){
                    Toast.makeText(TgReceiveGoods.this,getString(R.string.please_select_year),Toast.LENGTH_SHORT).show();

                }
                else{

                    if (networkConnection.isNetworkAvailable()) {
                        new GetRoDetails().execute();
                    } else {
                        displayNoInternetDailog();
                    }

                }

                break;

            case R.id.btnBack:
                Intent backIntent1 = new Intent(TgReceiveGoods.this, TgDashBoardActivity.class);
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

        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);

        monthSpinnerLayout = (TextView) findViewById(R.id.selectmonth);
        monthSpinnerLayout.setOnClickListener(this);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        ((TextView) findViewById(R.id.top_textView)).setText(R.string.receive_goods);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        final List<String> month = new ArrayList<String>();
        networkConnection = new NetworkConnection(TgReceiveGoods.this);
        //  month.add(getResources().getString(R.string.Select_Month));

      /*  month.add(new MonthDto(0,"January"));
        month.add(new MonthDto(1,"February"));
        month.add(new MonthDto(2,"March"));
        month.add(new MonthDto(3,"April"));
        month.add(new MonthDto(4,"May"));
        month.add(new MonthDto(5,"June"));
        month.add(new MonthDto(5,"July"));
        month.add(new MonthDto(6,"September"));
        month.add(new MonthDto(7,"October"));
        month.add(new MonthDto(8,"November"));
        month.add(new MonthDto(9,"December"));
        month.add(new MonthDto(9,"December"));*/


        month.add("January");
        month.add("February");
        month.add("March");
        month.add("April");
        month.add("May");
        month.add("June");
        month.add("July");
        month.add("August");
        month.add("September");
        month.add("October");
        month.add("November");
        month.add("December");


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


                switch (strMonth){
                    case "January":
                        i_month=1;
                        break;
                    case "February":
                        i_month=2;
                        break;
                    case "March":
                        i_month=3;
                        break;
                    case "April":
                        i_month=4;
                        break;
                    case "May":
                        i_month=5;
                        break;
                    case "June":
                        i_month=6;
                        break;
                    case "July":
                        i_month=7;
                        break;
                    case "August":
                        i_month=8;
                        break;
                    case "September":
                        i_month=9;
                        break;
                    case "October":
                        i_month=10;
                        break;
                    case "November":
                        i_month=11;
                        break;
                    case "December":
                        i_month=12;
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

            GregorianCalendar gc = new GregorianCalendar();
            int i_month = gc.get(Calendar.MONTH) + 1;
            int i_year = gc.get(Calendar.YEAR);

        List<String> Year = new ArrayList<String>();
        if (i_month == 12) {
            Year.add("" + i_year);
            Year.add("" + (i_year + 1));
        } else {
            Year.add("" + i_year);
        }
      /*  Year.add("2016");
        Year.add("2017");
        Year.add("2018");*/

        String this_yr = String.valueOf(year);
        int position=0;

        for (int i = 0; i < Year.size(); i++) {
            if(Year.get(i).toString().equals(this_yr)){
                position=i;
            }

            break;
        }

        ArrayAdapter<String> dataAdapteryear = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Year);

        // Drop down layout style - list view with radio button
        dataAdapteryear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
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
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime);
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
    public void onBackPressed() {
        Intent backIntent = new Intent(TgReceiveGoods.this, TgDashBoardActivity.class);
        startActivity(backIntent);
        finish();
    }

    void displayNoInternetDailog(){
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgReceiveGoods.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
    }

    public String getRODetsilsDetails() {

        Map<String, Object> inputMap = new LinkedHashMap<String, Object>();
        inputMap.put("distCode", LoginData.getInstance().getDistCode());
        inputMap.put("shopNo", LoginData.getInstance().getShopNo());
        inputMap.put("currYear", strYear);
        inputMap.put("currMonth", i_month);
        inputMap.put("transactionId", LoginData.getInstance().getTransactionId());
        inputMap.put("password", XMLUtil.PASSWORD);

        try {
            FPSROAllocation fpsroaallocationDetails = XMLUtil.getRODetails(inputMap);
            List<Product> productList = fpsroaallocationDetails.getFpsProductList();
            Log.e(TAG,"Total Product Size : "+productList.size());

            if (fpsroaallocationDetails !=null && fpsroaallocationDetails.getRespMsgCode().contains("0")) {
                Intent i = new Intent(TgReceiveGoods.this, TgReceivegoodsComodityList.class);
                i.putExtra("roDetails",fpsroaallocationDetails);
                i.putExtra("year", strYear);
                i.putExtra("month", ""+i_month);
                startActivity(i);
                finish();
            }


            return fpsroaallocationDetails.toString();
        }catch (final FPSException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TgGenericErrorDialog tgGenericErrorDialog =new TgGenericErrorDialog(TgReceiveGoods.this,""+e.getMessage());
                    tgGenericErrorDialog.setCanceledOnTouchOutside(false);
                    tgGenericErrorDialog.show();
                }
            });
            return null;
        }
        catch (Exception e) {
            Log.e("Exception", "FPSDealerDetails " + e.toString());
            return null;
        }

    }


    class GetRoDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            roLoadingDeatils.setCanceledOnTouchOutside(false);
            roLoadingDeatils.show();
        }

        protected String doInBackground(String... arg0) {
            return getRODetsilsDetails();
        }

        protected void onPostExecute(String result) {

            roLoadingDeatils.dismiss();
        }
    }

    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {
            Intent i = new Intent(TgReceiveGoods.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
