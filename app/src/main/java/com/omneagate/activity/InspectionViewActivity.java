package com.omneagate.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.CardInspectionDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FindingCriteriaDto;
import com.omneagate.DTO.InspectionVewDto;
import com.omneagate.DTO.ShopAndOthersDto;
import com.omneagate.DTO.StockInspectionDto;
import com.omneagate.DTO.WeighmentInspectionDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.omneagate.activity.R.id.linear_header;

public class InspectionViewActivity extends BaseActivity implements View.OnClickListener {
    FindingCriteriaDto findingCriteriaDto;
    String TAG = "InspectionViewActivity";
    InspectionFindingActivity inspectionFindingActivity;
    TextView mTvTitle;
    ImageView backIcon;
    Button cancelButton;
    private int criteriaNo;
    private LinearLayout linear_headerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        criteriaNo = getIntent().getIntExtra("Criteria_no", 5);
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
        cancelButton = (Button) findViewById(R.id.btn_cancel);
        linear_headerview = (LinearLayout) findViewById(linear_header);
    }

    private void loadViewData() {
        setUpInspectionPopUpPage();
        mTvTitle.setText(getResources().getString(R.string.inspection_finding));
        mTvTitle.setText(getResources().getString(R.string.inspection_finding));
        List<InspectionVewDto> inspectionVewDtolist = null;
        if (criteriaNo == 0) {
            inspectionVewDtolist = setDto(Util.findingCriteriaDto.getStockInspection());
            loadTableValues(inspectionVewDtolist);
        } else if (criteriaNo == 1) {
            inspectionVewDtolist = setDto(Util.findingCriteriaDto.getCardInspection());
            loadcardTableValues(inspectionVewDtolist);
        } else if (criteriaNo == 2) {
            inspectionVewDtolist = setDto(Util.findingCriteriaDto.getWeighmentInspection());
            loadweightTableValues(inspectionVewDtolist);
        } else if (criteriaNo == 3) {
            inspectionVewDtolist = setDto(Util.findingCriteriaDto.getShopInpsection());
            loadotherTableValues(inspectionVewDtolist);
        } else if (criteriaNo == 4) {
            inspectionVewDtolist = setDto(Util.findingCriteriaDto.getOtherInsection());
            loadotherTableValues(inspectionVewDtolist);
        }
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private List<InspectionVewDto> setDto(List dtolist) {
        List<InspectionVewDto> list = new ArrayList<>();
        for (int i = 0; i < dtolist.size(); i++) {
            InspectionVewDto inspectionviewdto = new InspectionVewDto();
            if (criteriaNo == 0) {
                StockInspectionDto dto = (StockInspectionDto) dtolist.get(i);
                inspectionviewdto.setActualStock(dto.getActualStock());
                inspectionviewdto.setCommodity(dto.getCommodity());
                inspectionviewdto.setPosStock(dto.getPosStock());
                inspectionviewdto.setRemarks(dto.getRemarks());
                inspectionviewdto.setVariance(dto.getVariance());
            } else if (criteriaNo == 1) {
                CardInspectionDto dto = (CardInspectionDto) dtolist.get(i);
                inspectionviewdto.setActualStock(dto.getCommodityIssuedasperCard());
                inspectionviewdto.setCommodity(dto.getCommodity());
                inspectionviewdto.setPosStock(dto.getCommodityIssuedasperPos());
                inspectionviewdto.setRemarks(dto.getRemarks());
                inspectionviewdto.setVariance(dto.getVariance());
                inspectionviewdto.setCardnumber(dto.getCardNumber());
            } else if (criteriaNo == 2) {
                WeighmentInspectionDto dto = (WeighmentInspectionDto) dtolist.get(i);
                inspectionviewdto.setActualStock(dto.getObservedQuantity());
                inspectionviewdto.setCommodity(dto.getCommodity());
                inspectionviewdto.setPosStock(dto.getSoldQuantity());
                inspectionviewdto.setRemarks(dto.getRemarks());
                inspectionviewdto.setVariance(dto.getVariance());
                inspectionviewdto.setCardnumber(dto.getCardNo());
                inspectionviewdto.setBillnumber(dto.getBillNumber());
            } else if (criteriaNo == 3) {
                ShopAndOthersDto dto = (ShopAndOthersDto) dtolist.get(i);
//            inspectionviewdto.setActualStock(dto.getActualStock());
//            inspectionviewdto.setCommodity(dto.getCommodity());
//            inspectionviewdto.setPosStock(dto.getPosStock());
                inspectionviewdto.setRemarks(dto.getRemarks());
//            inspectionviewdto.setVariance(dto.getVariance());
            } else if (criteriaNo == 4) {
                ShopAndOthersDto dto = (ShopAndOthersDto) dtolist.get(i);
//            inspectionviewdto.setActualStock(dto.getActualStock());
//            inspectionviewdto.setCommodity(dto.getCommodity());
//            inspectionviewdto.setPosStock(dto.getPosStock());
                inspectionviewdto.setRemarks(dto.getRemarks());
//            inspectionviewdto.setVariance(dto.getVariance());
            }
            list.add(inspectionviewdto);
        }
        return list;
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

    private void loadTableValues(List<InspectionVewDto> InspectionDtoList) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            LayoutInflater lin = LayoutInflater.from(InspectionViewActivity.this);
            int sno = 1;
            set_header(R.layout.header_stockinspectionview);
            for (InspectionVewDto inspectionVewDto : InspectionDtoList) {
                transactionLayout.addView(returnView(lin, sno, inspectionVewDto));
                sno++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTableValues exc..." + e);
        }
    }

    private View returnView(LayoutInflater entitle, int sno, InspectionVewDto inspectionVewDto) {
        View convertView = entitle.inflate(R.layout.adapter_inspection_view, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvCommodity = (TextView) convertView.findViewById(R.id.txt_commodity);
        TextView mTvSystemStock = (TextView) convertView.findViewById(R.id.txt_system_stock);
        TextView mTvPhysicalStock = (TextView) convertView.findViewById(R.id.txt_physical_stock);
        TextView mTvVariance = (TextView) convertView.findViewById(R.id.txt_variance);
        TextView mTvRemarks = (TextView) convertView.findViewById(R.id.txt_remarks);
        mTvSno.setText("" + sno);
        if (inspectionVewDto.getCommodity() != 0) {
            String prodName = FPSDBHelper.getInstance(this).getProductName(inspectionVewDto.getCommodity());
            mTvCommodity.setText(prodName);
        } else {
            mTvCommodity.setText(" - ");
        }
        mTvSystemStock.setText("" + checknull(inspectionVewDto.getPosStock()));
        mTvPhysicalStock.setText("" + checknull(inspectionVewDto.getActualStock()));
        mTvVariance.setText("" + checknull(inspectionVewDto.getVariance()));
        mTvRemarks.setText("" + inspectionVewDto.getRemarks());
        return convertView;
    }

    private void loadcardTableValues(List<InspectionVewDto> InspectionDtoList) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            LayoutInflater lin = LayoutInflater.from(InspectionViewActivity.this);
            int sno = 1;
            set_header(R.layout.header_cardinspectionview);
            for (InspectionVewDto inspectionVewDto : InspectionDtoList) {
                transactionLayout.addView(returncardView(lin, sno, inspectionVewDto));
                sno++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTableValues exc..." + e);
        }
    }

    private void set_header(int view) {
        linear_headerview.removeAllViews();
        LayoutInflater header = LayoutInflater.from(this);
        View convertView = header.inflate(view, new LinearLayout(this), false);
        linear_headerview.addView(convertView);
    }

    private View returncardView(LayoutInflater entitle, int sno, InspectionVewDto inspectionVewDto) {
        View convertView = entitle.inflate(R.layout.adapter_cardinspectionview, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvCommodity = (TextView) convertView.findViewById(R.id.txt_commodity);
        TextView mTvSystemStock = (TextView) convertView.findViewById(R.id.txt_system_stock);
        TextView mTvPhysicalStock = (TextView) convertView.findViewById(R.id.txt_physical_stock);
        TextView mTvVariance = (TextView) convertView.findViewById(R.id.txt_variance);
        TextView mTvRemarks = (TextView) convertView.findViewById(R.id.txt_remarks);

        TextView mTvcardnumber = (TextView) convertView.findViewById(R.id.cardnumber);
        mTvcardnumber.setText(inspectionVewDto.getCardnumber());
        mTvSno.setText("" + sno);
        if (inspectionVewDto.getCommodity() != 0) {
            String prodName = FPSDBHelper.getInstance(this).getProductName(inspectionVewDto.getCommodity());
            mTvCommodity.setText(prodName);
        } else {
            mTvCommodity.setText(" - ");
        }
        mTvSystemStock.setText("" + checknull(inspectionVewDto.getPosStock()));
        mTvPhysicalStock.setText("" + checknull(inspectionVewDto.getActualStock()));
        mTvVariance.setText("" + checknull(inspectionVewDto.getVariance()));
        mTvRemarks.setText("" + inspectionVewDto.getRemarks());
        return convertView;
    }

    private void loadweightTableValues(List<InspectionVewDto> InspectionDtoList) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            LayoutInflater lin = LayoutInflater.from(InspectionViewActivity.this);
            int sno = 1;
            set_header(R.layout.header_weightinspectionview);
            for (InspectionVewDto inspectionVewDto : InspectionDtoList) {
                transactionLayout.addView(returnweightView(lin, sno, inspectionVewDto));
                sno++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTableValues exc..." + e);
        }
    }

    private View returnweightView(LayoutInflater entitle, int sno, InspectionVewDto inspectionVewDto) {
        View convertView = entitle.inflate(R.layout.adapter_weightinspectionview, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvbillnumber = (TextView) convertView.findViewById(R.id.billnumber);
        TextView mTvcardnumber = (TextView) convertView.findViewById(R.id.cardnumber);
        TextView mTvCommodity = (TextView) convertView.findViewById(R.id.txt_commodity);
        TextView mTvSystemStock = (TextView) convertView.findViewById(R.id.txt_system_stock);
        TextView mTvPhysicalStock = (TextView) convertView.findViewById(R.id.txt_physical_stock);
        TextView mTvVariance = (TextView) convertView.findViewById(R.id.txt_variance);
        TextView mTvRemarks = (TextView) convertView.findViewById(R.id.txt_remarks);
        mTvSno.setText("" + sno);
        mTvbillnumber.setText(inspectionVewDto.getBillnumber());
        mTvcardnumber.setText(inspectionVewDto.getCardnumber());
        if (inspectionVewDto.getCommodity() != 0) {
            String prodName = FPSDBHelper.getInstance(this).getProductName(inspectionVewDto.getCommodity());
            mTvCommodity.setText(prodName);
        } else {
            mTvCommodity.setText(" - ");
        }
        mTvSystemStock.setText("" + checknull(inspectionVewDto.getPosStock()));
        mTvPhysicalStock.setText("" + checknull(inspectionVewDto.getActualStock()));
        mTvVariance.setText("" + checknull(inspectionVewDto.getVariance()));
        mTvRemarks.setText("" + inspectionVewDto.getRemarks());
        return convertView;
    }

    private void loadotherTableValues(List<InspectionVewDto> InspectionDtoList) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            LayoutInflater lin = LayoutInflater.from(InspectionViewActivity.this);
            int sno = 1;
            set_header(R.layout.header_shop_other_inspectionview);
            for (InspectionVewDto inspectionVewDto : InspectionDtoList) {
                transactionLayout.addView(returnotherView(lin, sno, inspectionVewDto));
                sno++;
            }
        } catch (Exception e) {
            Log.e(TAG, "loadTableValues exc..." + e);
        }
    }

    private View returnotherView(LayoutInflater entitle, int sno, InspectionVewDto inspectionVewDto) {
        View convertView = entitle.inflate(R.layout.adapter_shop_other_inspectionview, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvshopcode = (TextView) convertView.findViewById(R.id.txt_shopcode);
        TextView mTvRemarks = (TextView) convertView.findViewById(R.id.txt_remarks);
        mTvSno.setText("" + sno);
        mTvshopcode.setText(inspectionFindingActivity.shop_code);
        mTvRemarks.setText("" + inspectionVewDto.getRemarks());
        return convertView;
    }

    private String checknull(Double text) {
        if (text == null)
            return " - ";
        else
        return String.format("%.3f",text);
//            return Double.toString(text);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "backpressd...");
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
