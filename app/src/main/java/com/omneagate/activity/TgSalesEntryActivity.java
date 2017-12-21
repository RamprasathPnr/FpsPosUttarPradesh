package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.Adapter.RationDetailAdapter;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSDealer;
import com.omneagate.DTO.FPSRationCard;
import com.omneagate.DTO.FPSRationCardDetails;
import com.omneagate.Util.FpsMemberData;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.RationCardDetailDialog;

import java.util.Timer;
import java.util.TimerTask;

public class TgSalesEntryActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageViewBack;
    private LinearLayout best_finger_detection_layout;
    private LinearLayout scan_finger_print_layout;
    private LinearLayout scan_iris_layout;
    private FPSRationCardDetails fpsRationDetails;
    private LoadMoreListView listViewBeneficairy;
    private RationDetailAdapter adapter;
    private final String TAG = TgSalesEntryActivity.class.getCanonicalName();
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;
    private Button btnBack;


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_sales_entry);
        initView();
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.sales_top_heading));
    }





    private void initView() {
        setPopUpPage();
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        updateDateTime();
        fpsRationDetails = new FPSRationCardDetails();

        fpsRationDetails= FpsMemberData.getInstance().getFpsRationCardDetails();
       // fpsRationDetails = (FPSRationCardDetails) getIntent().getSerializableExtra("FpsBeneficiarydetail");
       // String rationCardNumber = getIntent().getStringExtra("rationCardNumber");
        String rationCardNumber =FpsMemberData.getInstance().getRcNo();

        ((TextView) findViewById(R.id.ration_number)).setText(getString(R.string.rc_number)+" "+rationCardNumber);

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);

        best_finger_detection_layout = (LinearLayout) findViewById(R.id.best_finger_detection_layout);
        best_finger_detection_layout.setOnClickListener(this);

        scan_finger_print_layout = (LinearLayout) findViewById(R.id.scan_finger_print_layout);
        scan_finger_print_layout.setOnClickListener(this);

        scan_iris_layout = (LinearLayout) findViewById(R.id.scan_iris_layout);
        scan_iris_layout.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        listViewBeneficairy = (LoadMoreListView) findViewById(R.id.listView_fps_member_detail);

        Log.e(TAG, "<====Total Beneficiary Members Size====>" + fpsRationDetails.getRationCardList());

        adapter = new RationDetailAdapter(this, fpsRationDetails.getRationCardList());
        listViewBeneficairy.setAdapter(adapter);

        listViewBeneficairy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (FPSRationCard fpsRationCard : fpsRationDetails.getRationCardList()) {
                    fpsRationCard.setSelectedItem(false);
                }

                fpsRationDetails.getRationCardList().get(position).setSelectedItem(true);

             /*   adapter = new RationDetailAdapter(TgSalesEntryActivity.this, fpsRationDetails.getRationCardList());
                listViewBeneficairy.setAdapter(adapter);*/
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;

            case R.id.best_finger_detection_layout:

                moveToNext("TgTenFingerRegistrationActivity");

                break;

            case R.id.scan_finger_print_layout:
                moveToNext("SalesFingerPrintActivity");
                break;
            case R.id.scan_iris_layout:
                moveToNext("SalesIrisActivity");
                break;

            case R.id.btnBack:
                onBackPressed();
                break;

            default:
                break;
        }
    }

    private void moveToNext(String activityName) {
        try {
            boolean memberSelected = false;
            int position = 0;

            for (int i = 0; i < fpsRationDetails.getRationCardList().size(); i++) {

                FPSRationCard fpsRationCard = fpsRationDetails.getRationCardList().get(i);

                if (fpsRationCard.isSelectedItem()) {
                    memberSelected = true;
                    position = i;
                }

            }
            if (memberSelected) {
                if (activityName.equalsIgnoreCase("SalesFingerPrintActivity")) {
                    Intent in = new Intent(TgSalesEntryActivity.this, TgSalesFingerPrintActivity.class);
                    in.putExtra("memberName", fpsRationDetails.getRationCardList().get(position).getMemberName());
                    in.putExtra("uid", fpsRationDetails.getRationCardList().get(position).getUidNo());
                    in.putExtra("rationCardNo", fpsRationDetails.getRationCardList().get(position).getRationCardNumber());
                    in.putExtra("memberId", fpsRationDetails.getRationCardList().get(position).getMemberId());
                    startActivity(in);
                    finish();
                } else if (activityName.equalsIgnoreCase("TgTenFingerRegistrationActivity")) {
                    Intent backIntent = new Intent(TgSalesEntryActivity.this, TgTenFingerRegistrationActivity.class);
                    backIntent.putExtra("ActivityName", "TgSalesEntryActivity");
                    backIntent.putExtra("AadharNo", fpsRationDetails.getRationCardList().get(position).getUidNo());
                    backIntent.putExtra("memberName", fpsRationDetails.getRationCardList().get(position).getMemberName());
                    backIntent.putExtra("uid", fpsRationDetails.getRationCardList().get(position).getUidNo());
                    backIntent.putExtra("rationCardNo", fpsRationDetails.getRationCardList().get(position).getRationCardNumber());
                    backIntent.putExtra("memberId", fpsRationDetails.getRationCardList().get(position).getMemberId());
                    startActivity(backIntent);
                    finish();
                } else {
                    Intent in = new Intent(TgSalesEntryActivity.this, TgSalesIrisScanActivity.class);
                    in.putExtra("memberName", fpsRationDetails.getRationCardList().get(position).getMemberName());
                    in.putExtra("uid", fpsRationDetails.getRationCardList().get(position).getUidNo());
                    in.putExtra("rationCardNo", fpsRationDetails.getRationCardList().get(position).getRationCardNumber());
                    in.putExtra("memberId", fpsRationDetails.getRationCardList().get(position).getMemberId());
                    startActivity(in);
                    finish();
                }

            } else {
                Toast.makeText(TgSalesEntryActivity.this, getString(R.string.please_bene), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(TgSalesEntryActivity.this, TgSalesActivity.class);
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

        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
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
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgSalesEntryActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
