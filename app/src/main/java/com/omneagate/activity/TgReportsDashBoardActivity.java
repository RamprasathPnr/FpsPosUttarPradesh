package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.TgGenericErrorDialog;
import com.omneagate.printer.Usb_Printer;

import java.util.Timer;
import java.util.TimerTask;

public class TgReportsDashBoardActivity extends BaseActivity implements View.OnClickListener {
private LinearLayout fps_allotment;
private LinearLayout card_position;
private LinearLayout current_closing_balance;
private LinearLayout fps_sales;
private LinearLayout stock_inward;
private LinearLayout fps_sales_day_report;
private LinearLayout fps_closing_monthly;
private LinearLayout fps_portability_daywiseReport;
private LinearLayout fps_portability_monthwise;
    private ImageView imageViewBack;
    int mode;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private Button btnBack;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_reports_dash_board);
        initView();
    }
    private void initView() {
        try {
            setPopUpPage();
            ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
            updateDateTime();
            fps_allotment = (LinearLayout) findViewById(R.id.fps_allotment);
            card_position = (LinearLayout) findViewById(R.id.card_position);
            current_closing_balance = (LinearLayout) findViewById(R.id.current_closing_balance);
            fps_sales = (LinearLayout) findViewById(R.id.fps_sales);
            stock_inward = (LinearLayout) findViewById(R.id.stock_inward);
            fps_sales_day_report = (LinearLayout) findViewById(R.id.fps_sales_day_report);
            fps_closing_monthly = (LinearLayout) findViewById(R.id.fps_closing_monthly);
            fps_portability_daywiseReport=(LinearLayout)findViewById(R.id.fps_portability_daywiseReport);
            fps_portability_monthwise=(LinearLayout)findViewById(R.id.fps_portability_monthwise);

            fps_allotment.setOnClickListener(this);
            card_position.setOnClickListener(this);
            current_closing_balance.setOnClickListener(this);
            fps_sales.setOnClickListener(this);
            stock_inward.setOnClickListener(this);
            fps_sales_day_report.setOnClickListener(this);
            fps_closing_monthly.setOnClickListener(this);
            fps_portability_daywiseReport.setOnClickListener(this);
            fps_portability_monthwise.setOnClickListener(this);

            imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
            imageViewBack.setOnClickListener(this);

            btnBack = (Button) findViewById(R.id.btnBack);
            btnBack.setOnClickListener(this);

            networkConnection = new NetworkConnection(TgReportsDashBoardActivity.this);

            ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.reports));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fps_allotment:
                if (networkConnection.isNetworkAvailable()) {
                    Intent saleIntent = new Intent(TgReportsDashBoardActivity.this, TgFPSReportsAllotmentActivity.class);
                    startActivity(saleIntent);
                    finish();

                } else {
                    displayNoInternetDailog();
                }
                break;
            case R.id.card_position:

                if (networkConnection.isNetworkAvailable()) {
                    Intent submitIntent = new Intent(TgReportsDashBoardActivity.this, TgCardPositionReportActivity.class);

                    if (GlobalAppState.language.equalsIgnoreCase("te")) {
                        submitIntent.putExtra("mode", "నివేదికలు > కార్డులు స్థితి నివేదిక ");
                    } else {
                        submitIntent.putExtra("mode", "REPORTS > CARD POSITION");
                    }
                    startActivity(submitIntent);
                    finish();
                } else {
                    displayNoInternetDailog();
                }

                break;
            case R.id.current_closing_balance:
                if (networkConnection.isNetworkAvailable()) {
                    Intent reportIntent = new Intent(TgReportsDashBoardActivity.this, TgCurrentClosingBalanceReport.class);
                    if (GlobalAppState.language.equalsIgnoreCase("te")) {
                        reportIntent.putExtra("mode", "నివేదికలు > ప్రస్తుత ముగింపు బాకీ ");
                    } else {
                        reportIntent.putExtra("mode", "REPORTS > CURRENT CLOSING BALANCE");
                    }

                    startActivity(reportIntent);
                    finish();
                } else {
                    displayNoInternetDailog();
                }

                break;
            case R.id.fps_sales:
                if (networkConnection.isNetworkAvailable()) {
                    Intent fps_sales = new Intent(TgReportsDashBoardActivity.this, TgReportsSalesActivity.class);
                    if (GlobalAppState.language.equalsIgnoreCase("te")) {
                        fps_sales.putExtra("mode", "నివేదికలు > ఎఫ్ పి ఎస్ విక్రయాలు ");
                    } else {
                        fps_sales.putExtra("mode", "REPORTS > FPS SALE");
                    }
                    startActivity(fps_sales);
                    finish();
                } else {
                    displayNoInternetDailog();
                }

                break;
            case R.id.stock_inward:
                if (networkConnection.isNetworkAvailable()) {
                    Intent stock_inward = new Intent(TgReportsDashBoardActivity.this, TgFpsReportsStockInWardActivity.class);
                    if (GlobalAppState.language.equalsIgnoreCase("te")) {
                        stock_inward.putExtra("mode", "నివేదికలు > FPS రసీదు");
                    } else {
                        stock_inward.putExtra("mode", "REPORTS > FPS RECEIPTS");
                    }

                    startActivity(stock_inward);
                    finish();
                } else {
                    displayNoInternetDailog();
                }

                break;

            case R.id.fps_sales_day_report:
                if (networkConnection.isNetworkAvailable()) {
                    Intent fps_sales_day_report = new Intent(TgReportsDashBoardActivity.this, TgFpsReportsSalesDayActivity.class);
                    if (GlobalAppState.language.equalsIgnoreCase("te")) {
                        fps_sales_day_report.putExtra("mode", "నివేదికలు >cc ఎఫ్ పి ఎస్ విక్రయాలు");
                    } else {
                        fps_sales_day_report.putExtra("mode", "REPORTS > FPS SALES DAY REPORT");
                    }
                    startActivity(fps_sales_day_report);
                    finish();
                } else {
                    displayNoInternetDailog();
                }


                break;
            case R.id.fps_closing_monthly:
                if (networkConnection.isNetworkAvailable()) {
                    Intent fps_closing_monthly = new Intent(TgReportsDashBoardActivity.this,TgFpsMonthlyClosingReportActivity.class);
                    if (GlobalAppState.language.equalsIgnoreCase("te")) {
                        fps_closing_monthly.putExtra("mode","నివేదికలు > FPS ప్రతినెల ముగింపు నిలువ");
                    }else {
                        fps_closing_monthly.putExtra("mode","REPORTS > FPS MONTHLY CLOSING BALANCE");
                    }
                    startActivity(fps_closing_monthly);
                    finish();
                } else {
                    displayNoInternetDailog();
                }

                break;
            case R.id.fps_portability_monthwise:
                if (networkConnection.isNetworkAvailable()) {
                    Intent fps_portability_monthwise = new Intent(TgReportsDashBoardActivity.this,TgPortabilityMonthWiseReport.class);
                    if (GlobalAppState.language.equalsIgnoreCase("te")) {
                        fps_portability_monthwise.putExtra("mode","నివేదికలు > పోర్టబిలిటీ నెల నివేదిక");
                    }else {
                        fps_portability_monthwise.putExtra("mode","REPORTS > PORTABILITY MONTH REPORT");
                    }
                    startActivity(fps_portability_monthwise);
                    finish();
                } else {
                    displayNoInternetDailog();
                }

                break;
            case R.id.fps_portability_daywiseReport:
                if (networkConnection.isNetworkAvailable()) {
                    Intent fps_daywise_report = new Intent(TgReportsDashBoardActivity.this,TgPortabilityDayWiseReport.class);
                    if (GlobalAppState.language.equalsIgnoreCase("te")) {
                        fps_daywise_report.putExtra("mode","నివేదికలు > పోర్టబిలిటీ రోజుల నివేదిక");
                    }else {
                        fps_daywise_report.putExtra("mode","REPORTS > PORTABILITY DAY REPORT");
                    }
                    startActivity(fps_daywise_report);
                    finish();
                } else {
                    displayNoInternetDailog();
                }

                break;

            case R.id.imageViewBack:
                Intent imageViewBack = new Intent(TgReportsDashBoardActivity.this,TgDashBoardActivity.class);
                startActivity(imageViewBack);
                finish();
                break;
            case R.id.btnBack:
                Intent intentBack = new Intent(TgReportsDashBoardActivity.this,TgDashBoardActivity.class);
                startActivity(intentBack);
                finish();

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgReportsDashBoardActivity.this, TgDashBoardActivity.class);
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

    void displayNoInternetDailog(){
        TgGenericErrorDialog tgGenericErrorDialog = new TgGenericErrorDialog(TgReportsDashBoardActivity.this, getString(R.string.noNetworkConnection));
        tgGenericErrorDialog.setCanceledOnTouchOutside(false);
        tgGenericErrorDialog.show();
        return;
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
            Intent i = new Intent(TgReportsDashBoardActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
