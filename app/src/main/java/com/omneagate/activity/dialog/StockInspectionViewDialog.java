package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.omneagate.DTO.StockInspectionDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.activity.R;
import com.omneagate.activity.StockInspectionActivity;

public class StockInspectionViewDialog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener {

    private Context mContext;
    private TextView mTvCancel, mTvSave, systemStock, variance, heading;
    EditText physicalStock, remarks;
    String productId = "", productName = "";
    StockInspectionDto stockInspectionDto;
    String TAG = "StockInspectionViewDialog";


    public StockInspectionViewDialog(Context context, StockInspectionDto stockInspDto, String prodId) {
        super(context);
        this.mContext = context;
        stockInspectionDto = stockInspDto;
        productId = prodId;
        productName = FPSDBHelper.getInstance(context).getProductName(stockInspectionDto.getCommodity());
        Log.e(TAG,"stockInspDto..."+stockInspDto.toString());
        Log.e(TAG,"stockInspectionDto..."+stockInspectionDto.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_stock_inspection_view);
        findView();
        loadViewData();
        listenersForEditText();
    }

    private void findView() {
        heading = (TextView) findViewById(R.id.heading);
        mTvCancel = (TextView) findViewById(R.id.txt_cancel);
        mTvSave = (TextView) findViewById(R.id.txt_save);
        mTvSave.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        systemStock = (TextView) findViewById(R.id.txt_system_stock);
        physicalStock = (EditText) findViewById(R.id.txt_physical_stock);
        variance = (TextView) findViewById(R.id.txt_variance);
        remarks = (EditText) findViewById(R.id.ed_remarks);
    }

    private void loadViewData() {
        heading.setText(productName);
        systemStock.setText(String.valueOf(stockInspectionDto.getPosStock()));
        physicalStock.setText(String.valueOf(stockInspectionDto.getActualStock()));
        variance.setText(String.valueOf(stockInspectionDto.getVariance()));
        remarks.setText(String.valueOf(stockInspectionDto.getRemarks()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_save:
                long pId = Long.valueOf(productId);
                double sysStock = Double.parseDouble(systemStock.getText().toString());
                double phyStock = Double.parseDouble(physicalStock.getText().toString());
                double varStock = Double.parseDouble(variance.getText().toString());
                String remarkStock = remarks.getText().toString();

                StockInspectionActivity stockInspectionActivity = new StockInspectionActivity();
                stockInspectionActivity.editedStockInspectionDto = new StockInspectionDto();
                stockInspectionActivity.editedStockInspectionDto.setCommodity(pId);
                stockInspectionActivity.editedStockInspectionDto.setPosStock(sysStock);
                stockInspectionActivity.editedStockInspectionDto.setActualStock(phyStock);
                stockInspectionActivity.editedStockInspectionDto.setVariance(varStock);
                stockInspectionActivity.editedStockInspectionDto.setRemarks(remarkStock);
                dismiss();
                break;
            case R.id.txt_cancel:
                dismiss();
                break;
            case R.id.txt_physical_stock:
//                checkVisibility();
                physicalStock.requestFocus();
                /*keyBoardAppear();
                changeLayout(true);
                keyBoardFocused = KeyBoardEnum.PHYSICALSTOCK;*/
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.txt_physical_stock && hasFocus) {
            physicalStock.requestFocus();
            /*checkVisibility();
            keyBoardAppear();
            keyBoardFocused = KeyBoardEnum.PHYSICALSTOCK;
            changeLayout(true);*/
        }
    }

    private void listenersForEditText() {
        physicalStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double existingStock = Double.parseDouble(physicalStock.getText().toString());
                    double var = stockInspectionDto.getPosStock() - existingStock;
                    variance.setText(String.valueOf(var));
                }
                catch(Exception e) {
                    variance.setText(String.valueOf(stockInspectionDto.getVariance()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
