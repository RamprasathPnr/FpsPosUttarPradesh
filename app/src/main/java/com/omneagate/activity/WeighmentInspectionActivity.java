package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.DTO.WeighmentInspectionDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.InspectionRemoveDialog;
import com.omneagate.activity.dialog.UnsavedStockInspectionDialog;
import com.omneagate.activity.dialog.WeighmentInspectionDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WeighmentInspectionActivity extends BaseActivity
        implements View.OnClickListener {
    public static boolean weightInspectionDialogValidation;
    public static double stockVariance;
    public static double existingStock;
    public static double systemStock;
    private TextView mTvTitle, total;
    private Button submitBtn, addBtn, mBtCancel;
    private ImageView mIvBack;
    EditText remarks, et_bill_number, firstText, secondText, thirdText;
    public NoDefaultSpinner commoditySpinner;
    List<ProductDto> getProductDetails;
    List<String> productSpinnerList;
    private String productId;
    private TextView noCommodity;
    private UnsavedStockInspectionDialog unsavedStockInspectionDialog;
    private WeighmentInspectionDialog weighInspectionDialog;
    private Set<BillItemProductDto> product_list;
    List<String> productName = new ArrayList<>();
    private InspectionRemoveDialog inspectionRemoveDialog;
    private TextWatcher watcher;
    public ArrayAdapter<String> adapter;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weighing_inspection);
        findView();
//        getProductList();
        setUpInspectionPopUpPage();
        loadWeighmentInspectionList();
    }

    private void findView() {
        mTvTitle = (TextView) findViewById(R.id.top_textView);
        mTvTitle.setText(getResources().getString(R.string.title_weight_inspection));
        noCommodity = (TextView) findViewById(R.id.noCommodity);
        mIvBack = (ImageView) findViewById(R.id.imageViewBack);
        mIvBack.setOnClickListener(this);
        et_bill_number = (EditText) findViewById(R.id.et_bill_number);
//        et_bill_number.setText("1016000001");
        et_bill_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!FPSDBConstants.checkedittext(et_bill_number)) {
                        product_list = FPSDBHelper.getInstance(WeighmentInspectionActivity.this).getBillItems(et_bill_number.getText().toString());
                        if (product_list != null && product_list.size() > 0) {
                            getBillProductList();
                            loadProductSpinner();
                        } else {
                            productSpinnerList.clear();
                            loadProductSpinner();
                            Toast.makeText(WeighmentInspectionActivity.this, R.string.valid_bill_number, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        firstText = (EditText) findViewById(R.id.firstText);
        secondText = (EditText) findViewById(R.id.secondText);
        thirdText = (EditText) findViewById(R.id.thirdText);
        watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable == firstText.getEditableText() && firstText.getText().length() == 2) {
                    secondText.requestFocus();
                } else if (editable == secondText.getEditableText()) {
                    thirdText.requestFocus();
                }
            }
        };
        firstText.addTextChangedListener(watcher);
        secondText.addTextChangedListener(watcher);
        commoditySpinner = (NoDefaultSpinner) findViewById(R.id.commoditySpinner);
        productSpinnerList = new ArrayList<String>();
        remarks = (EditText) findViewById(R.id.edt_remark);
        mBtCancel = (Button) findViewById(R.id.btn_cancel);
        submitBtn = (Button) findViewById(R.id.btn_submit);
        addBtn = (Button) findViewById(R.id.btn_add_new);
//        total = (TextView) findViewById(R.id.txt_total);
        submitBtn.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mBtCancel.setOnClickListener(this);
//        mBtCancel.setOnClickListener(this);
        addBtn.setOnClickListener(this);
        loadProductSpinner();
    }

    private void getProductList() {
        getProductDetails = new ArrayList<>();
        getProductDetails = FPSDBHelper.getInstance(this).getProduct();
        productSpinnerList = new ArrayList<String>();
        for (ProductDto productDto : getProductDetails) {
            String productName = productDto.getName();
            String productLocalName = productDto.getLocalProductName();
            String productId = String.valueOf(productDto.getId());
            if (productName != null) {
                if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                    productSpinnerList.add(productLocalName + "~" + productId);
                } else {
                    productSpinnerList.add(productName + "~" + productId);
                }
            }
        }
    }

    private void getBillProductList() {
        productSpinnerList.clear();
        for (BillItemProductDto dto : product_list) {
            if (dto.getProductName() != null) {
                if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                    productSpinnerList.add(dto.getLocalProductName() + "~" + String.valueOf(dto.getProductId() + "~" + dto.getQuantity()));
                } else {
                    productSpinnerList.add(dto.getProductName() + "~" + String.valueOf(dto.getProductId() + "~" + dto.getQuantity()));
                }
            }
        }
    }

    private void loadProductSpinner() {
//        Log.e("WeighmentInspectionActivity", "productSpinnerList..." + productSpinnerList);
        productName.clear();
        for (int i = 0; i < productSpinnerList.size(); i++) {
            String[] productDetail = productSpinnerList.get(i).split("~");
            productName.add(productDetail[0]);
        }
        if (productSpinnerList.size() == 0) {
            commoditySpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        et_bill_number.clearFocus();
                        if (FPSDBConstants.checkedittext(et_bill_number)) {
                            Toast.makeText(WeighmentInspectionActivity.this, R.string.alert_bill_number, Toast.LENGTH_SHORT).show();
                        } else if (productSpinnerList.size() == 0) {
                            Toast.makeText(WeighmentInspectionActivity.this, R.string.valid_bill_number, Toast.LENGTH_SHORT).show();
                        }
                    }
                    return false;
                }
            });
        } else {
            commoditySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (!FPSDBConstants.checkedittext(et_bill_number)) {
                        String[] productDetail = productSpinnerList.get(position).split("~");
//                String productNameStr = productDetail[0];
                        productId = productDetail[1];
//                double qty = FPSDBHelper.getInstance(WeighmentInspectionActivity.this).getStockOfSpecificProduct(productId);
                        double qty = Double.parseDouble(productDetail[2]);
                        weighInspectionDialog = new WeighmentInspectionDialog(com.omneagate.activity.WeighmentInspectionActivity.this, qty, productDetail[0]);
                        weighInspectionDialog.show();
//                weighInspectionDialog.setCanceledOnTouchOutside(false);
                        remarks.setText("");
                    } else {
                        Toast.makeText(WeighmentInspectionActivity.this, R.string.alert_bill_number, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productName);
        commoditySpinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commoditySpinner.setPrompt(getString(R.string.selection));
    }

    private void loadTableValues(List<WeighmentInspectionDto> value) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            transactionLayout.removeAllViews();
            LayoutInflater lin = LayoutInflater.from(WeighmentInspectionActivity.this);
            int sno = 1;
            /*for (FindingCriteriaDto findingCriteriaDto : value) {
                transactionLayout.addView(returnView(lin, sno, findingCriteriaDto));
                sno++;
            }*/
            for (int j = value.size() - 1; j >= 0; j--) {
                transactionLayout.addView(returnView(lin, sno, value.get(j), j));
                sno++;
            }
        } catch (Exception e) {
            Log.e("WeighmentInsActivity", "loadTableValues exc..." + e);
        }
    }

    private View returnView(LayoutInflater entitle, final int sno, final WeighmentInspectionDto weighmentCriteriaDto, final int position) {
        View convertView = entitle.inflate(R.layout.adapter_weight_inspection, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvType = (TextView) convertView.findViewById(R.id.txt_type);
        TextView mTv_product = (TextView) convertView.findViewById(R.id.txt_product);
        ImageView remove = (ImageView) convertView.findViewById(R.id.img_remove);
//        ImageView viewRecord = (ImageView) convertView.findViewById(R.id.img_explore);
        mTvSno.setText("" + sno);
//        final String productName = FPSDBHelper.getInstance(this).getProductName(findingCriteriaDto.getWeighmentInspection().getCommodity());
        mTvType.setText(weighmentCriteriaDto.getBillNumber());
        mTv_product.setText(FPSDBHelper.getInstance(this).getProductName(weighmentCriteriaDto.getCommodity()));
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inspectionRemoveDialog = new InspectionRemoveDialog(com.omneagate.activity.WeighmentInspectionActivity.this, position, "", "WeighmentInspectionActivity");
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
                loadWeighmentInspectionList();
            }
        });*/
        return convertView;
    }

    private boolean addWeighingInspection() {
//        Log.e(TAG, "existingStock..." + existingStock);
        if (getbillnumber().isEmpty()) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.alert_bill_number, Toast.LENGTH_SHORT).show();
            return false;
        } else if (firstText.getText().toString().trim().isEmpty()) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.enter_ration, Toast.LENGTH_SHORT).show();
            return false;
        } else if (secondText.getText().toString().trim().isEmpty()) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.enter_ration, Toast.LENGTH_SHORT).show();
            return false;
        } else if (thirdText.getText().toString().trim().isEmpty()) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.enter_ration, Toast.LENGTH_SHORT).show();
            return false;
        }  else if (commoditySpinner.getSelectedItem() == null) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.sel_commodity, Toast.LENGTH_SHORT).show();
            return false;
        } else if (remarks.getText().toString().trim().isEmpty()) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.enter_remarks, Toast.LENGTH_SHORT).show();
            return false;
        } else if (FPSDBHelper.getInstance(this).checkRationCardNumber(getrationcardnumber()) == 0) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.ration_exists, Toast.LENGTH_SHORT).show();
            return false;
        } else if (FPSDBHelper.getInstance(this).compare_CardandBillnumber(getrationcardnumber(), getbillnumber()) == 0) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.rationnumber_billnumber, Toast.LENGTH_SHORT).show();
            return false;
        } else if (containsProductId(Util.findingCriteriaDto.getWeighmentInspection(), Long.valueOf(productId))) {
            Toast.makeText(WeighmentInspectionActivity.this, R.string.stock_already_inspected, Toast.LENGTH_SHORT).show();
            return false;
        }else {

//            InspectionReportDto inspectionReportDto = FPSDBHelper.getInstance(WeighmentInspectionActivity.this).getLastInsertedReportClientId();
            WeighmentInspectionDto WeighInspectionDto = new WeighmentInspectionDto();
            WeighInspectionDto.setCommodity(Long.valueOf(productId));
            WeighInspectionDto.setBillNumber(et_bill_number.getText().toString());
            WeighInspectionDto.setCardNo(merge_cardnumber());
            WeighInspectionDto.setVariance(stockVariance);
            WeighInspectionDto.setFpsId(LoginData.getInstance().getFpsId());
            WeighInspectionDto.setRemarks(remarks.getText().toString());
            WeighInspectionDto.setSoldQuantity(systemStock);
            WeighInspectionDto.setObservedQuantity(existingStock);
//            WeighInspectionDto.setClientReportId(inspectionReportDto.getClientId());
//                    WeighInspectionDtoList.add(WeighInspectionDto);
            systemStock = 0.0;
            existingStock = 0.0;
            stockVariance = 0.0;
//            stockInspectionDialogValidation = false;
//            FindingCriteriaDto findingCriteria = new FindingCriteriaDto();
//            findingCriteria.setCriteria(InspectionConstants.Weighment_Inspection);
//            findingCriteria.setWeighmentInspection(WeighInspectionDto);
            Util.findingCriteriaDto.getWeighmentInspection().add(WeighInspectionDto);
//            listSize = Util.findingCriteriaDto.size();
            loadWeighmentInspectionList();
            remarks.setText("");
            et_bill_number.setText("");
            firstText.setText("");
            secondText.setText("");
            thirdText.setText("");
            productId = "";
            productSpinnerList.clear();
            loadProductSpinner();
//            commoditySpinner.setAdapter(null);
            return true;
        }
    }

    private String getrationcardnumber() {
        return firstText.getText().toString().trim() + secondText.getText().toString().trim() + thirdText.getText().toString().trim();
    }

    private String merge_cardnumber() {
        return firstText.getText().toString() + secondText.getText().toString() + thirdText.getText().toString();
    }

    public boolean containsProductId(List<WeighmentInspectionDto> list, long id) {
        for (WeighmentInspectionDto dto : list) {
            if (dto.getCommodity() == id && dto.getBillNumber().equals(getbillnumber()) && dto.getCardNo().equals(getrationcardnumber())) {
                return true;
            }
        }
        return false;
    }

    private String getbillnumber() {
        return et_bill_number.getText().toString().trim();
    }

    private void loadWeighmentInspectionList() {
        if (Util.findingCriteriaDto.getWeighmentInspection().size() > 0) {
            noCommodity.setVisibility(View.GONE);
        } else {
            noCommodity.setVisibility(View.VISIBLE);
        }
        loadTableValues(Util.findingCriteriaDto.getWeighmentInspection());
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
            case R.id.btn_add_new:
                addWeighingInspection();
                break;
            case R.id.btn_submit:
                if (!getbillnumber().isEmpty()) {
                    boolean added = addWeighingInspection();
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

    @Override
    public void onBackPressed() {
        if (validateall()) {
            call_unsaveddialog();
        } else {
            callfindingactivity();
        }
    }

    private boolean validateall() {
        return commoditySpinner.getSelectedItem() != null || !remarks.getText().toString().equalsIgnoreCase("") || !et_bill_number.getText().toString().equalsIgnoreCase("") || !firstText.getText().toString().equalsIgnoreCase("") || !secondText.getText().toString().equalsIgnoreCase("") || !thirdText.getText().toString().equalsIgnoreCase("");
    }

    private void call_unsaveddialog() {
        unsavedStockInspectionDialog = new UnsavedStockInspectionDialog(com.omneagate.activity.WeighmentInspectionActivity.this, getResources().getString(R.string.unsavedWeighmentInspection));
        unsavedStockInspectionDialog.show();
        unsavedStockInspectionDialog.setCanceledOnTouchOutside(false);
    }
}
