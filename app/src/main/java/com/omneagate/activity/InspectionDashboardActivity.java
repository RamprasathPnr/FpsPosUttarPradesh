package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;

public class InspectionDashboardActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mLinearLayout, mLinearLayout2;
    private ImageView mIvBack;
    private TextView mTvTilte;
    String TAG = "InspectionDashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inspection_dashboard);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findView();
        loadViewData();
    }

    private void findView() {
        mTvTilte = (TextView) findViewById(R.id.top_textView);
        mTvTilte.setText(getResources().getString(R.string.dashboard));
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_new_inspection);
        mLinearLayout2 = (LinearLayout) findViewById(R.id.ll_inspection_list);
        mIvBack = (ImageView) findViewById(R.id.imageViewBack);
        mIvBack.setVisibility(View.GONE);
        mIvBack.setOnClickListener(this);
        mLinearLayout.setOnClickListener(this);
        mLinearLayout2.setOnClickListener(this);
    }

    private void loadViewData() {
        setUpInspectionPopUpPage();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.ll_new_inspection:
                Intent intent = new Intent(InspectionDashboardActivity.this, InspectionFindingActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ll_inspection_list:
                Intent intent2 = new Intent(InspectionDashboardActivity.this, InspectionListActivity.class);
                startActivity(intent2);
                finish();
                break;
        }
    }
}
