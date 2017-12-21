package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.Util.EditboxFilter;
import com.omneagate.activity.R;
import com.omneagate.activity.StockInspectionActivity;
import com.omneagate.activity.WeighmentInspectionActivity;

/**
 * Created by user1 on 29/9/16.
 */
public class WeighmentInspectionDialog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener {
    private Context mContext;
    private TextView mTvCancel, mTvSave, systemStock, variance, heading, systemStockLabel, physicalStockLabel, varianceLabel;
    EditText physicalStock;
    double quantity = 0.0;
    double var = 0.0;
    double existingStock = 0.0;
    String prodName;
    String TAG = "StockInspectionDialog";
    /*RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;
    KeyboardView keyview, keyboardViewAlpha;
    KeyBoardEnum keyBoardFocused;*/

    public WeighmentInspectionDialog(Context context, double qty, String productName) {
        super(context);
        this.mContext = context;
        quantity = qty;
        prodName = productName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_stock_inspection);
        findView();
        loadViewData();
        listenersForEditText();
    }

    private void findView() {
        heading = (TextView) findViewById(R.id.heading);
        systemStockLabel = (TextView) findViewById(R.id.sysStockLabel);
        physicalStockLabel = (TextView) findViewById(R.id.phyStockLabel);
        varianceLabel = (TextView) findViewById(R.id.varianceLabel);
        systemStock = (TextView) findViewById(R.id.txt_system_stock);
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
        systemStockLabel.setText(R.string.weight_qtyinbill);
        physicalStockLabel.setText(R.string.observedqty);
        varianceLabel.setText(R.string.variance);
//        systemStock.setText(String.format("%.0f", quantity));
        systemStock.setText(""+quantity);
        Log.e(TAG, "quantity..." + quantity);
//        variance.setText(String.format("%.0f", quantity));
        variance.setText(""+quantity);
        StockInspectionActivity stockInspectionActivity2 = new StockInspectionActivity();
        stockInspectionActivity2.stockInspectionDialogValidation = false;
//        variance.setText(String.format("%.0f", quantity));
        /*keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
        keyboardumber = (RelativeLayout) findViewById(R.id.keyboardNumber);
        keyboardAlpha = (RelativeLayout) findViewById(R.id.keyboardAlpha);
        Keyboard keyboard = new Keyboard(mContext, R.layout.keyboard);
        Keyboard keyboardAlp = new Keyboard(mContext, R.layout.keyboard_alpha);
        //create KeyboardView object
        keyview = (KeyboardView) findViewById(R.id.customkeyboard);
        keyboardViewAlpha = (KeyboardView) findViewById(R.id.customkeyboardAlpha);
        keyboardViewAlpha.setKeyboard(keyboardAlp);
        //attache the keyboard object to the KeyboardView object
        keyview.setKeyboard(keyboard);
        //show the keyboard
        keyview.setVisibility(KeyboardView.VISIBLE);
        keyboardViewAlpha.setVisibility(KeyboardView.VISIBLE);
        keyview.setPreviewEnabled(false);
        keyboardViewAlpha.setPreviewEnabled(false);
        //take the keyboard to the front
        keyview.bringToFront();
        keyboardViewAlpha.bringToFront();
        //register the keyboard to receive the key pressed
        keyview.setOnKeyboardActionListener(new KeyList());
        keyboardViewAlpha.setOnKeyboardActionListener(new KeyListAlpha());
        keyBoardCustom.setVisibility(View.GONE);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_save:
                if (physicalStock.getText().toString().trim().equalsIgnoreCase("") || physicalStock.getText().toString().trim().equalsIgnoreCase(".")) {
                    Toast.makeText(mContext, R.string.enter_physical_stock, Toast.LENGTH_SHORT).show();
                    return;
                }

                WeighmentInspectionActivity.systemStock = quantity;
                WeighmentInspectionActivity.existingStock = existingStock;
                WeighmentInspectionActivity.stockVariance = var;
                WeighmentInspectionActivity.weightInspectionDialogValidation = true;
                dismiss();
                break;
            case R.id.txt_cancel:
                WeighmentInspectionActivity.weightInspectionDialogValidation = false;
                WeighmentInspectionActivity activity = (WeighmentInspectionActivity) mContext;
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
        physicalStock.addTextChangedListener(new TextWatcher() {
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
                    var = quantity - existingStock;
                    Log.e(TAG, "quantity..." + quantity);
                    Log.e(TAG, "existingStock..." + existingStock);
                    Log.e(TAG, "var..." + var);
//                    var=Math.round(var);
                    if (Double.toString(var).contains("."))
                        variance.setText(String.format("%.3f", var));
                    else
                        variance.setText(String.format("%.0f", var));
                } catch (Exception e) {
                    variance.setText(String.format("%.0f", quantity));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
