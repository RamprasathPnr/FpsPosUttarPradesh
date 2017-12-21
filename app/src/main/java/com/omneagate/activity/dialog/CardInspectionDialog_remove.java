package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.Util.EditboxFilter;
import com.omneagate.activity.CardInspectionActivity;
import com.omneagate.activity.R;

/**
 * Created by user1 on 30/9/16.
 */
public class CardInspectionDialog_remove extends Dialog implements View.OnClickListener, View.OnFocusChangeListener {
    private Context mContext;
    private TextView mTvCancel, mTvSave, variance, heading, systemStockLabel, physicalStockLabel, varianceLabel;
    EditText physicalStock, systemStock;
    double quantity = 0.0;
    double var = 0.0;
    double existingStock = 0.0;
    String prodName;
    String TAG = "StockInspectionDialog";
    private TextWatcher watcher;

    public CardInspectionDialog_remove(Context context, double qty, String productName) {
        super(context);
        this.mContext = context;
        quantity = qty;
        prodName = productName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_card_inspection);
        findView();
        loadViewData();
        listenersForEditText();
    }

    private void findView() {
        heading = (TextView) findViewById(R.id.heading);
        systemStockLabel = (TextView) findViewById(R.id.sysStockLabel);
        physicalStockLabel = (TextView) findViewById(R.id.phyStockLabel);
        varianceLabel = (TextView) findViewById(R.id.varianceLabel);
        systemStock = (EditText) findViewById(R.id.txt_system_stock);
        systemStock.setFilters(new InputFilter[]{new EditboxFilter(5, 3)});
        physicalStock = (EditText) findViewById(R.id.txt_physical_stock);
        physicalStock.setFilters(new InputFilter[]{new EditboxFilter(5, 3)});
        variance = (TextView) findViewById(R.id.txt_variance);
        mTvCancel = (TextView) findViewById(R.id.txt_cancel);
        mTvSave = (TextView) findViewById(R.id.txt_save);
        mTvSave.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }

    private void loadViewData() {
        heading.setText(prodName);
        systemStockLabel.setText(R.string.card_quantityinpos);
        physicalStockLabel.setText(R.string.card_issued_qty);
        varianceLabel.setText(R.string.variance);
//        systemStock.setText(String.format("%.0f", quantity));
//        Log.e(TAG, "quantity..." + quantity);
//        variance.setText(String.format("%.0f", quantity));
        variance.setText("0");
        CardInspectionActivity.cardInspectionDialogValidation = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_save:
                if (systemStockLabel.getText().toString().trim().equalsIgnoreCase("") || systemStockLabel.getText().toString().trim().equalsIgnoreCase(".")) {
                    Toast.makeText(mContext, R.string.enter_quantityinpos, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (physicalStock.getText().toString().trim().equalsIgnoreCase("") || physicalStock.getText().toString().trim().equalsIgnoreCase(".")) {
                    Toast.makeText(mContext, R.string.enter_issued_qty, Toast.LENGTH_SHORT).show();
                    return;
                }
                CardInspectionActivity.systemStock = quantity;
                CardInspectionActivity.existingStock = existingStock;
                CardInspectionActivity.stockVariance = var;
                CardInspectionActivity.cardInspectionDialogValidation = true;
                dismiss();
                break;
            case R.id.txt_cancel:
                CardInspectionActivity.cardInspectionDialogValidation = false;
                CardInspectionActivity activity = (CardInspectionActivity) mContext;
                activity.commoditySpinner.setAdapter(activity.adapter);
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



    /*private void changeLayout(boolean value) {
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.bill_layout_master);
        relativelayout.removeView(keyBoardCustom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (value) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.leftMargin = 30;
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.rightMargin = 30;
        }
        lp.bottomMargin = 30;
        keyBoardCustom.setPadding(10, 10, 10, 10);
        relativelayout.addView(keyBoardCustom, lp);
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }*/

    private void listenersForEditText() {
        watcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (physicalStock.getText().toString().trim().isEmpty())
                        existingStock = 0;
                    else
                        existingStock = Double.parseDouble(physicalStock.getText().toString().trim());
                    if (systemStock.getText().toString().trim().isEmpty())
                        quantity = 0;
                    else
                        quantity = Double.parseDouble(systemStock.getText().toString().trim());
                    var = quantity - existingStock;
//                    Log.e(TAG, "quantity..." + quantity);
//                    Log.e(TAG, "existingStock..." + existingStock);
//                    Log.e(TAG, "var..." + var);
//                    var=Math.round(var);
                    if (Double.toString(var).contains("."))
                        variance.setText(String.format("%.3f", var));
                    else
                        variance.setText(String.format("%.0f", var));
//                    variance.setText("" + var);
                } catch (Exception e) {
                    variance.setText(String.format("%.0f", quantity));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        systemStock.addTextChangedListener(watcher);
        physicalStock.addTextChangedListener(watcher);

    }
}