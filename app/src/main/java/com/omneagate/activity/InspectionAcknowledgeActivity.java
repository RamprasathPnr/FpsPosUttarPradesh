package com.omneagate.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.InspectionReportStatus;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FindingCriteriaDto;
import com.omneagate.DTO.InspectionReportDto;
import com.omneagate.DTO.MenuDataDto;
import com.omneagate.DTO.StockInspectionDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.activity.dialog.CompleteObservationDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InspectionAcknowledgeActivity extends BaseActivity implements View.OnClickListener {

    private List<String> stackList;
    FindingCriteriaDto findingCriteriaDto;
    String TAG = "InspectionAcknowledgeActivity";
    InspectionFindingActivity inspectionFindingActivity;
    TextView mTvTitle, inspectionDateTv, shopCodeTv, inspectionByTv;
    ImageView backIcon;
    Button agreeButton, disagreeButton, appealButton;
    long clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_acknowledge);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findView();
        loadViewData();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void findView() {
        inspectionFindingActivity = new InspectionFindingActivity();
        mTvTitle = (TextView) findViewById(R.id.top_textView);
        backIcon = (ImageView) findViewById(R.id.imageViewBack);
        agreeButton = (Button) findViewById(R.id.btn_agree);
        disagreeButton = (Button) findViewById(R.id.btn_disagree);
        appealButton = (Button) findViewById(R.id.btn_prefer_appeal);
        inspectionDateTv = (TextView) findViewById(R.id.inspectionDate);
        shopCodeTv = (TextView) findViewById(R.id.shopCode);
        inspectionByTv = (TextView) findViewById(R.id.inspectionBy);
    }

    private void loadViewData() {
        setUpInspectionPopUpPage();
        clientId = getIntent().getLongExtra("clientId", 0);
        String date = "";
        long inspDate = getIntent().getLongExtra("inspectionDate", 0);
        String inspectorName = getIntent().getStringExtra("inspectorName");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            date = sdf.format(inspDate);
        }
        catch(Exception e) {}
        String fpsCode = SessionId.getInstance().getFpsCode();

        String shopCode = getResources().getString(R.string.shop_code);
        String insDate = getResources().getString(R.string.inspected_date);
        String insName = getResources().getString(R.string.inspected_by);

        inspectionDateTv.setText(date);
        shopCodeTv.setText(fpsCode);
        inspectionByTv.setText(inspectorName);

        mTvTitle.setText(getResources().getString(R.string.complted_inspection_report));
        backIcon.setOnClickListener(this);
        agreeButton.setOnClickListener(this);
        disagreeButton.setOnClickListener(this);
        appealButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                finish();
                break;
            case R.id.btn_agree:
                FPSDBHelper.getInstance(this).updateAckString(clientId, InspectionReportStatus.AGREE.toString());
                finishInspectionAcknowledge();
                break;
            case R.id.btn_disagree:
                FPSDBHelper.getInstance(this).updateAckString(clientId, InspectionReportStatus.DISAGREE.toString());
                finishInspectionAcknowledge();
                break;
            case R.id.btn_prefer_appeal:
                FPSDBHelper.getInstance(this).updateAckString(clientId, InspectionReportStatus.APPEAL.toString());
                finishInspectionAcknowledge();
                break;

        }
    }

    private void finishInspectionAcknowledge() {
        Intent myIntent = new Intent(getApplicationContext(), InspectionReviewListActivity.class);
        startActivity(myIntent);
        finish();
    }



    @Override
    public void onBackPressed() {
        Log.e(TAG,"backpressd...");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "on destroy called");
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }

}
