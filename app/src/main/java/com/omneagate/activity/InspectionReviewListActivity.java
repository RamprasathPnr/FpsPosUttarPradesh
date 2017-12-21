package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
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
import com.omneagate.activity.dialog.CompleteObservationDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class InspectionReviewListActivity extends BaseActivity implements View.OnClickListener {

    private List<String> stackList;
    FindingCriteriaDto findingCriteriaDto;
    String TAG = "InspectionViewActivity";
    InspectionFindingActivity inspectionFindingActivity;
    TextView mTvTitle, totalNo;
    ImageView backIcon;
    List<InspectionReportDto> inspectionReportDtoList;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_review_list);
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
        totalNo = (TextView) findViewById(R.id.txt_total_no);
        backIcon = (ImageView) findViewById(R.id.imageViewBack);
        cancelButton = (Button) findViewById(R.id.btn_cancel);
    }

    private void loadViewData() {
        setUpPopUpPage();
        mTvTitle.setText(getResources().getString(R.string.complted_inspection_report));
        String criteriaStr = getIntent().getStringExtra("Criteria");
        inspectionReportDtoList = FPSDBHelper.getInstance(InspectionReviewListActivity.this).getAllReports();
        totalNo.setText(" : "+inspectionReportDtoList.size()+" "+getResources().getString(R.string.inspection));
        loadTableValues(inspectionReportDtoList);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.btn_cancel:
                finish();
                break;
            case R.id.imageViewBack:
                finish();
                break;*/
        }
    }

    private void loadTableValues(List<InspectionReportDto> inspectionReportDtos) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            LayoutInflater lin = LayoutInflater.from(InspectionReviewListActivity.this);
            int sno = 1;
            for (int i=inspectionReportDtos.size()-1;i>=0;i--) {
                transactionLayout.addView(returnView(lin, sno, inspectionReportDtos.get(i)));
                sno++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTableValues exc..."+e);
        }
    }

    private View returnView(LayoutInflater entitle, int sno, final InspectionReportDto inspectionReportDto) {
        View convertView = entitle.inflate(R.layout.adapter_inspection_review_list, new LinearLayout(this), false);
        LinearLayout parentLay = (LinearLayout) findViewById(R.id.linearLayoutTitleAdapter);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvDateTime = (TextView) convertView.findViewById(R.id.txt_date_time);
        TextView mTvInspectedBy = (TextView) convertView.findViewById(R.id.txt_inspected_by);
        TextView mTvOverallStatus = (TextView) convertView.findViewById(R.id.txt_overall_status);
        TextView mTvFpsAckStatus = (TextView) convertView.findViewById(R.id.txt_fps_ack_status);
        ImageView mTvView = (ImageView) convertView.findViewById(R.id.img_explore);

        Log.e(TAG,"status..."+sno+" , "+inspectionReportDto.getStatus());
        /*if(inspectionReportDto.getStatus().equalsIgnoreCase("N")) {
            mTvSno.setTextColor(Color.parseColor("#F08080"));
        }*/

        mTvSno.setText("" + sno);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String date = sdf.format(inspectionReportDto.getDateOfInspection());
//            Date createdDate = sdf.parse(date);
//            String inspDate = sdf.format(date);
            mTvDateTime.setText(date);
        }
        catch(Exception e) {}
        mTvInspectedBy.setText("" +inspectionReportDto.getInspectorName());
        String overallStatusStr;
        if(inspectionReportDto.getOverAllStatus()) {
            overallStatusStr = getResources().getString(R.string.ok);
        }
        else {
            overallStatusStr = getResources().getString(R.string.not_ok);
        }
        String ackStatusStr = "";
        if(inspectionReportDto.getFpsAckStatus().equalsIgnoreCase(InspectionReportStatus.PENDING.toString())) {
            ackStatusStr = getResources().getString(R.string.inspection_pending);
        }
        else if(inspectionReportDto.getFpsAckStatus().equalsIgnoreCase(InspectionReportStatus.AGREE.toString())) {
            ackStatusStr = getResources().getString(R.string.inspection_agree);
        }
        else if(inspectionReportDto.getFpsAckStatus().equalsIgnoreCase(InspectionReportStatus.DISAGREE.toString())) {
            ackStatusStr = getResources().getString(R.string.inspection_disagree);
        }
        else if(inspectionReportDto.getFpsAckStatus().equalsIgnoreCase(InspectionReportStatus.APPEAL.toString())) {
            ackStatusStr = getResources().getString(R.string.inspection_prefer_appeal);
        }

        mTvOverallStatus.setText(overallStatusStr);
        mTvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(InspectionReviewListActivity.this, InspectionReviewListViewActivity.class);
                    intent.putExtra("InspectionReportDto", new Gson().toJson(inspectionReportDto));
                    startActivity(intent);
                    finish();
            }
        });
        mTvFpsAckStatus.setText(ackStatusStr);
        return convertView;
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG,"backpressd...");
        Intent intent = new Intent(InspectionReviewListActivity.this, BeneficiaryMenuActivity.class);
        startActivity(intent);
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
