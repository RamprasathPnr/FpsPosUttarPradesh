package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.CardInspectionDto;
import com.omneagate.DTO.EnumDTO.InspectionReportStatus;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FindingCriteriaDto;
import com.omneagate.DTO.InspectionReportDto;
import com.omneagate.DTO.ShopAndOthersDto;
import com.omneagate.DTO.StockInspectionDto;
import com.omneagate.DTO.WeighmentInspectionDto;
import com.omneagate.Util.FPSDBHelper;

import java.text.SimpleDateFormat;
import java.util.List;

public class InspectionReviewListViewActivity extends BaseActivity implements View.OnClickListener {
    private List<String> stackList;
    FindingCriteriaDto findingCriteriaDto;
    String TAG = "InspectionViewListActivity";
    InspectionFindingActivity inspectionFindingActivity;
    TextView mTvTitle, inspectionDate, inspectionBy, shopCodeTv, fineAmountTv, overallRemarksTv, overallStatusTv, ackTxt;
    ImageView backIcon;
    long clientId, inspDate;
    String inspectorName, shopCode, overallStatus, overallComment;
    double fineAmount;
    Button agreeButton, disagreeButton, appealButton;
    RelativeLayout ackButLay;
    Button cancelButton, stockButton, cardButton, weightButton, shopButton, otherButton;
    View convertView;
    private LinearLayout transactionLayout;
    List<StockInspectionDto> stockdtolist;
    List<CardInspectionDto> carddtolist;
    List<WeighmentInspectionDto> weightdtolist;
    List<ShopAndOthersDto> shopdtolist, otherdtolist;
    private TextView emptyview;
    LayoutInflater lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_review_list_view);
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
        cancelButton = (Button) findViewById(R.id.btn_cancel);
        inspectionDate = (TextView) findViewById(R.id.inspectionDate);
        inspectionBy = (TextView) findViewById(R.id.inspectionBy);
        shopCodeTv = (TextView) findViewById(R.id.shopCode);
        fineAmountTv = (TextView) findViewById(R.id.fineAmount);
        overallRemarksTv = (TextView) findViewById(R.id.overallRemarks);
        overallRemarksTv.setMovementMethod(new ScrollingMovementMethod());
        overallStatusTv = (TextView) findViewById(R.id.overallStatus);
        agreeButton = (Button) findViewById(R.id.btn_agree);
        disagreeButton = (Button) findViewById(R.id.btn_disagree);
        appealButton = (Button) findViewById(R.id.btn_prefer_appeal);
        ackTxt = (TextView) findViewById(R.id.ackText);
        ackButLay = (RelativeLayout) findViewById(R.id.ackButLayout);
        transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
        stockButton = (Button) findViewById(R.id.stockButton);
        cardButton = (Button) findViewById(R.id.cardButton);
        weightButton = (Button) findViewById(R.id.weightButton);
        shopButton = (Button) findViewById(R.id.shopButton);
        otherButton = (Button) findViewById(R.id.otherButton);
        stockButton.setOnClickListener(this);
        cardButton.setOnClickListener(this);
        weightButton.setOnClickListener(this);
        shopButton.setOnClickListener(this);
        otherButton.setOnClickListener(this);
    }

    private void loadViewData() {
        setUpPopUpPage();
        lin = LayoutInflater.from(this);
        setemptyview();
        String inspectionReportDtoStr = getIntent().getStringExtra("InspectionReportDto");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        InspectionReportDto inspectionReportDto = gson.fromJson(inspectionReportDtoStr, InspectionReportDto.class);
        clientId = inspectionReportDto.getClientId();
        inspDate = inspectionReportDto.getDateOfInspection();
        inspectorName = inspectionReportDto.getInspectorName();
        String fpsAckStatus = inspectionReportDto.getFpsAckStatus();
        if (!fpsAckStatus.equalsIgnoreCase("pending")) {
            ackTxt.setVisibility(View.INVISIBLE);
            ackButLay.setVisibility(View.INVISIBLE);
        }
        shopCode = FPSDBHelper.getInstance(this).getFpsCode("FPS");
//        shopCode = SessionId.getInstance().getFpsCode();
        fineAmount = inspectionReportDto.getFineAmount();
        if (inspectionReportDto.getOverAllStatus()) {
            overallStatus = getResources().getString(R.string.ok);
        } else {
            overallStatus = getResources().getString(R.string.not_ok);
        }
        overallComment = inspectionReportDto.getOverAllComments();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String date = sdf.format(inspDate);
            inspectionDate.setText(":  " + date);
        } catch (Exception e) {
        }
        inspectionBy.setText(":  " + inspectorName);
        shopCodeTv.setText(":  " + shopCode);
        fineAmountTv.setText(":  ₹ " + String.format("%.0f", fineAmount));
        overallRemarksTv.setText(overallComment);
        overallStatusTv.setText(":  " + overallStatus);
        mTvTitle.setText(getResources().getString(R.string.complted_inspection_report));
        stockdtolist = FPSDBHelper.getInstance(this).getAllStockImagesView(clientId);
        carddtolist = FPSDBHelper.getInstance(this).getAllCardVerificationImages(clientId);
        weightdtolist = FPSDBHelper.getInstance(this).getAllWeighmentImages(clientId);
        shopdtolist = FPSDBHelper.getInstance(this).getAllShopImages(clientId);
        otherdtolist = FPSDBHelper.getInstance(this).getAllOthersImages(clientId);
        setbtn_focus(stockButton);
        cancelButton.setOnClickListener(this);
        if (stockdtolist != null && stockdtolist.size() > 0)
            loadTableValues(stockdtolist);
        else
            emptyview();    
        backIcon.setOnClickListener(this);
        agreeButton.setOnClickListener(this);
        disagreeButton.setOnClickListener(this);
        appealButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.btn_ack:
                String fpsAckStatus = getIntent().getStringExtra("fpsAckStatus");
                if (fpsAckStatus.equalsIgnoreCase("pending")) {
                    Intent intent = new Intent(InspectionReviewListViewActivity.this, InspectionAcknowledgeActivity.class);
                    intent.putExtra("clientId", clientId);
                    intent.putExtra("inspectionDate", inspDate);
                    intent.putExtra("inspectorName", inspectorName);
                    startActivity(intent);
                }
                break;
            case R.id.btn_agree:
                FPSDBHelper.getInstance(this).updateAckString(clientId, InspectionReportStatus.AGREE.toString());
                onBackPressed();
                break;
            case R.id.btn_disagree:
                FPSDBHelper.getInstance(this).updateAckString(clientId, InspectionReportStatus.DISAGREE.toString());
                onBackPressed();
                break;
            case R.id.btn_prefer_appeal:
                FPSDBHelper.getInstance(this).updateAckString(clientId, InspectionReportStatus.APPEAL.toString());
                onBackPressed();
                break;
            default:
                tabclick((Button) v);
                break;
        }
    }

    private void setbtn_focus(Button btn) {
        Button[] button = {stockButton, cardButton, weightButton, shopButton, otherButton};
        for (int i = 0; i < button.length; i++) {
            if (button[i].getId() == btn.getId()) {
                button[i].setTextColor(getResources().getColor(R.color.white));
                button[i].setBackground(getResources().getDrawable(R.drawable.buttoncornerradius));
            } else {
                button[i].setTextColor(getResources().getColor(R.color.black));
                button[i].setBackground(getResources().getDrawable(R.drawable.buttoncornerradius_lightgrey));
            }
        }
    }

    private void tabclick(View v) {
        setbtn_focus((Button) v);
        switch (v.getId()) {
            case R.id.stockButton:
                if (stockdtolist != null && stockdtolist.size() > 0)
                    loadTableValues(stockdtolist);
                else
                    emptyview();
                break;
            case R.id.cardButton:
                if (carddtolist != null && carddtolist.size() > 0)
                    loadcardTableValues(carddtolist);
                else
                    emptyview();
                break;
            case R.id.weightButton:
                if (weightdtolist != null && weightdtolist.size() > 0)
                    loadweightTableValues(weightdtolist);
                else
                    emptyview();
                break;
            case R.id.shopButton:
                if (shopdtolist != null && shopdtolist.size() > 0)
                    loadotherTableValues(shopdtolist);
                else
                    emptyview();
                break;
            case R.id.otherButton:
                if (otherdtolist != null && otherdtolist.size() > 0)
                    loadotherTableValues(otherdtolist);
                else
                    emptyview();
                break;
            default:
                finish();
                break;
        }
    }

    private void loadTableValues(List<StockInspectionDto> stockInspectionDtoList) {
        transactionLayout.removeAllViews();
        transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
        lin = LayoutInflater.from(InspectionReviewListViewActivity.this);

        for (int position = stockInspectionDtoList.size() - 1; position >= 0; position--) {
            transactionLayout.addView(returnView(lin, stockInspectionDtoList.get(position)));

        }
    }

    private View returnView(LayoutInflater entitle, StockInspectionDto stockInspectionDto) {
        View convertView = entitle.inflate(R.layout.adapter_inspection_list_view, new LinearLayout(this), false);
        TextView mTvCommodity = (TextView) convertView.findViewById(R.id.txt_commodity);
        TextView mTvSystemStock = (TextView) convertView.findViewById(R.id.txt_system_stock);
        TextView mTvPhysicalStock = (TextView) convertView.findViewById(R.id.txt_physical_stock);
        TextView mTvVariance = (TextView) convertView.findViewById(R.id.txt_variance);
        TextView mTvRemarks = (TextView) convertView.findViewById(R.id.txt_remarks);
        String prodName = FPSDBHelper.getInstance(this).getProductName(stockInspectionDto.getCommodity());
        mTvCommodity.setText(" : " +prodName);
        mTvSystemStock.setText(" : " + stockInspectionDto.getPosStock());
        mTvPhysicalStock.setText(" : " + stockInspectionDto.getActualStock());
        mTvVariance.setText(" : " + stockInspectionDto.getVariance());
        mTvRemarks.setText(" : " +stockInspectionDto.getRemarks());
        return convertView;
    }

    private void loadcardTableValues(List<CardInspectionDto> dtolist) {
        transactionLayout.removeAllViews();
        for (int position = dtolist.size() - 1; position >= 0; position--) {
            transactionLayout.addView(returncardView(lin, dtolist.get(position)));
        }
    }

    private View returncardView(LayoutInflater entitle, CardInspectionDto dto) {
        convertView = entitle.inflate(R.layout.adapter_inspection_list_view, new LinearLayout(this), false);
        ((TextView) convertView.findViewById(R.id.txt_system_stock_label)).setText(getResources().getString(R.string.quantityinpos));
        ((TextView) convertView.findViewById(R.id.txt_physical_stock_label)).setText(getResources().getString(R.string.issued_qty));
        TextView mTvCommodity = (TextView) convertView.findViewById(R.id.txt_commodity);
        TextView mTvSystemStock = (TextView) convertView.findViewById(R.id.txt_system_stock);
        TextView mTvPhysicalStock = (TextView) convertView.findViewById(R.id.txt_physical_stock);
        TextView mTvVariance = (TextView) convertView.findViewById(R.id.txt_variance);
        TextView mTvRemarks = (TextView) convertView.findViewById(R.id.txt_remarks);
        String prodName = FPSDBHelper.getInstance(this).getProductName(dto.getCommodity());
        TextView card_label = (TextView) convertView.findViewById(R.id.txt_card_number_label);
        TextView card_number = (TextView) convertView.findViewById(R.id.txt_card_number);
        card_label.setVisibility(View.VISIBLE);
        card_number.setVisibility(View.VISIBLE);
        card_number.setText(" : " + dto.getCardNumber());
        mTvCommodity.setText(" : " +prodName);
        mTvSystemStock.setText(" : " + dto.getCommodityIssuedasperPos());
        mTvPhysicalStock.setText(" : " + dto.getCommodityIssuedasperCard());
        mTvVariance.setText(" : " + dto.getVariance());
        mTvRemarks.setText(" : " +dto.getRemarks());
        return convertView;
    }

    private void loadweightTableValues(List<WeighmentInspectionDto> dtolist) {
        transactionLayout.removeAllViews();
        for (int position = dtolist.size() - 1; position >= 0; position--) {
            transactionLayout.addView(returnweightView(lin, dtolist.get(position)));
        }
    }

    private View returnweightView(LayoutInflater entitle, WeighmentInspectionDto dto) {
        convertView = entitle.inflate(R.layout.adapter_inspection_list_view, new LinearLayout(this), false);
        ((TextView) convertView.findViewById(R.id.txt_system_stock_label)).setText(getResources().getString(R.string.qtyinbill));
        TextView mTvCommodity = (TextView) convertView.findViewById(R.id.txt_commodity);
        TextView mTvSystemStock = (TextView) convertView.findViewById(R.id.txt_system_stock);
        TextView mTvPhysicalStock = (TextView) convertView.findViewById(R.id.txt_physical_stock);
        TextView mTvVariance = (TextView) convertView.findViewById(R.id.txt_variance);
        TextView mTvRemarks = (TextView) convertView.findViewById(R.id.txt_remarks);
        TextView card_label = (TextView) convertView.findViewById(R.id.txt_card_number_label);
        TextView card_number = (TextView) convertView.findViewById(R.id.txt_card_number);
        TextView bill_label = (TextView) convertView.findViewById(R.id.txt_bill_number_label);
        TextView bill_number = (TextView) convertView.findViewById(R.id.txt_bill_number);
        card_label.setVisibility(View.VISIBLE);
        card_number.setVisibility(View.VISIBLE);
        bill_label.setVisibility(View.VISIBLE);
        bill_number.setVisibility(View.VISIBLE);
        card_number.setText(" : " + dto.getCardNo());
        bill_number.setText(" : " + dto.getBillNumber());
        String prodName = FPSDBHelper.getInstance(this).getProductName(dto.getCommodity());
        mTvCommodity.setText(" : " +prodName);
        mTvSystemStock.setText(" : " + dto.getSoldQuantity());
        mTvPhysicalStock.setText(" : " + dto.getObservedQuantity());
        mTvVariance.setText(" : " + dto.getVariance());
        mTvRemarks.setText(" : " +dto.getRemarks());
        return convertView;
    }

    private void loadotherTableValues(List<ShopAndOthersDto> dtolist) {
        transactionLayout.removeAllViews();
        for (int position = dtolist.size() - 1; position >= 0; position--) {
            transactionLayout.addView(returnotherView(lin, dtolist.get(position)));
        }
    }

    private View returnotherView(LayoutInflater entitle, ShopAndOthersDto dto) {
        convertView = entitle.inflate(R.layout.adapter_other_inspection_list_view, new LinearLayout(this), false);
//        TextView mTvshopcode = (TextView) convertView.findViewById(R.id.txt_shopcode);
        TextView mTvRemarks = (TextView) convertView.findViewById(R.id.txt_remarks);
//        mTvshopcode.setText("-");
        mTvRemarks.setText(" : " +dto.getRemarks());
        return convertView;
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(InspectionReviewListViewActivity.this, InspectionReviewListActivity.class);
        startActivity(myIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void emptyview() {
        transactionLayout.removeAllViews();
        transactionLayout.addView(emptyview);
    }

    private void setemptyview() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
        params.height = 100;
        emptyview = new TextView(this);
        emptyview.setText(getResources().getString(R.string.no_data));
        emptyview.setGravity(Gravity.CENTER);
        emptyview.setLayoutParams(params);
        emptyview.setTextSize(30);
    }
}
