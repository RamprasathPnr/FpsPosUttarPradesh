package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ShopAndOthersDto;
import com.omneagate.Util.InspectionConstants;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.InspectionRemoveDialog;
import com.omneagate.activity.dialog.UnsavedStockInspectionDialog;

import java.util.List;

public class ShopOpenCloseActivity extends BaseActivity
        implements View.OnClickListener {
    private TextView mTvTitle;
    private ImageView mIvBack;
    private Button submitBtn, addBtn, mBtCancel;
    private EditText remarks;
    private TextView noCommodity;
    private UnsavedStockInspectionDialog unsavedStockInspectionDialog;
    private InspectionRemoveDialog inspectionRemoveDialog;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_open_close);
        findView();
        setUpInspectionPopUpPage();
        loadOtherInspectionList();
    }

    private void findView() {
        mTvTitle = (TextView) findViewById(R.id.top_textView);
        mTvTitle.setText(getResources().getString(R.string.title_shop_open_close));
        mIvBack = (ImageView) findViewById(R.id.imageViewBack);
        mIvBack.setOnClickListener(this);
        noCommodity = (TextView) findViewById(R.id.noCommodity);
        remarks = (EditText) findViewById(R.id.edt_remark);
        mBtCancel = (Button) findViewById(R.id.btn_cancel);
        submitBtn = (Button) findViewById(R.id.btn_submit);
        addBtn = (Button) findViewById(R.id.btn_add_another);
        submitBtn.setOnClickListener(this);
        mBtCancel.setOnClickListener(this);
        addBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_add_another:
                add_shopinspection();
                break;
            case R.id.btn_submit:
                if (!remarks.getText().toString().trim().isEmpty()) {
                    boolean added = add_shopinspection();
                    if (added) {
                        callfindingactivity();
                    }
                } else {
                    callfindingactivity();
                }
                break;
        }
    }

    private void callfindingactivity() {
        Intent intent = new Intent(this, InspectionFindingActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean add_shopinspection() {
        if (remarks.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(ShopOpenCloseActivity.this, R.string.enter_remarks, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            ShopAndOthersDto shopAndOthersDto = new ShopAndOthersDto();
            shopAndOthersDto.setRemarks(remarks.getText().toString());
            shopAndOthersDto.setCriteriaName(InspectionConstants.shopinspection);
//            FindingCriteriaDto findingCriteria = new FindingCriteriaDto();
//            findingCriteria.setCriteria(InspectionConstants.shopinspection);
//            findingCriteria.setShopInpsection(shopAndOthersDto);
            Util.findingCriteriaDto.getShopInpsection().add(shopAndOthersDto);
            loadOtherInspectionList();
            remarks.setText("");
            return true;
        }
    }

    private void loadOtherInspectionList() {
        if (Util.findingCriteriaDto.getShopInpsection().size() > 0) {
            noCommodity.setVisibility(View.GONE);
        } else {
            noCommodity.setVisibility(View.VISIBLE);
        }
        loadTableValues(Util.findingCriteriaDto.getShopInpsection());
    }

    private void loadTableValues(List<ShopAndOthersDto> value) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            transactionLayout.removeAllViews();
            LayoutInflater lin = LayoutInflater.from(ShopOpenCloseActivity.this);
            int sno = 1;
            for (int j = value.size() - 1; j >= 0; j--) {
                transactionLayout.addView(returnView(lin, sno, value.get(j), j));
                sno++;
            }
        } catch (Exception e) {
            Log.e("ShopOpenCloseActivity", "loadTableValues exc..." + e);
        }
    }

    private View returnView(LayoutInflater entitle, final int sno, final ShopAndOthersDto shopAndOthersDto, final int position) {
        View convertView = entitle.inflate(R.layout.adapter_stock_inspection, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvType = (TextView) convertView.findViewById(R.id.txt_type);
        ImageView remove = (ImageView) convertView.findViewById(R.id.img_remove);
//        ImageView viewRecord = (ImageView) convertView.findViewById(R.id.img_explore);
        mTvSno.setText("" + sno);
//        final String productName = FPSDBHelper.getInstance(this).getProductName(findingCriteriaDto.getWeighmentInspection().getCommodity());
        mTvType.setText(shopAndOthersDto.getRemarks());
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inspectionRemoveDialog = new InspectionRemoveDialog(com.omneagate.activity.ShopOpenCloseActivity.this, position, "","ShopOpenCloseActivity");
                inspectionRemoveDialog.show();
            }
        });
        /*viewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockInspectionViewDialog = new StockInspectionViewDialog(com.omneagate.activity.WeighmentInspectionActivity.this, WeighInspectionDto, productId);
                stockInspectionViewDialog.show();
                WeighInspectionDtoList.remove(sno - 1);
                InspectionReportDto inspectionReportDto = FPSDBHelper.getInstance(WeighmentInspectionActivity.this).getLastInsertedReportClientId();
                editedWeighInspectionDto.setClientReportId(inspectionReportDto.getClientId());
                WeighInspectionDtoList.add(editedWeighInspectionDto);
                editedWeighInspectionDto = null;
                loadOtherInspectionList();
            }
        });*/
        return convertView;
    }
    @Override
    public void onBackPressed() {

        if (!remarks.getText().toString().equalsIgnoreCase("")) {
            call_unsaveddialog();
        }  else {
            callfindingactivity();
        }
    }


    private void call_unsaveddialog() {
        unsavedStockInspectionDialog = new UnsavedStockInspectionDialog(com.omneagate.activity.ShopOpenCloseActivity.this, getResources().getString(R.string.unsavedOtherInspection));
        unsavedStockInspectionDialog.show();
        unsavedStockInspectionDialog.setCanceledOnTouchOutside(false);
    }
}
