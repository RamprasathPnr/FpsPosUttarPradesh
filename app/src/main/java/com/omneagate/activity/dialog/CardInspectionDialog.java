package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.activity.R;
import com.omneagate.activity.CardInspectionActivity;

public class CardInspectionDialog extends Dialog implements View.OnClickListener, View.OnFocusChangeListener {

    private Context mContext;
    private TextView mTvCancel, mTvSave, variance, heading, systemStockLabel, physicalStockLabel, varianceLabel;
    EditText physicalStock;
    TextView systemStock;
//    double quantity = 0.0;
    double var = 0.0;
    double existingStock = 0.0;
    double sysStock = 0.0;
    String prodName;
    /*RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;
    KeyboardView keyview, keyboardViewAlpha;
    KeyBoardEnum keyBoardFocused;*/

    public CardInspectionDialog(Context context) {
        super(context);
        this.mContext = context;
//        quantity = qty;
//        prodName = productName;
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
        variance = (TextView) findViewById(R.id.txt_variance);
        mTvCancel = (TextView) findViewById(R.id.txt_cancel);
        mTvSave = (TextView) findViewById(R.id.txt_save);
        mTvSave.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
    }

    private void loadViewData() {
        heading.setText(prodName);
        systemStockLabel.setText(R.string.issued_qty_pos);
        physicalStockLabel.setText(R.string.issued_qty_card);
        varianceLabel.setText(R.string.variance);
        /*systemStock.setText(String.valueOf(quantity));
        variance.setText(String.valueOf(quantity));*/
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
                CardInspectionActivity CardInspectionActivity = new CardInspectionActivity();
                CardInspectionActivity.systemStock = sysStock;
                CardInspectionActivity.existingStock = existingStock;
                CardInspectionActivity.stockVariance = var;
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
        systemStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    sysStock = Double.parseDouble(systemStock.getText().toString());
                    existingStock = Double.parseDouble(physicalStock.getText().toString());
                    var = sysStock - existingStock;
                    variance.setText(String.valueOf(var));
                }
                catch(Exception e) {}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        physicalStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    sysStock = Double.parseDouble(systemStock.getText().toString());
                    existingStock = Double.parseDouble(physicalStock.getText().toString());
                    var = sysStock - existingStock;
                    variance.setText(String.valueOf(var));
                }
                catch(Exception e) {}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*class KeyList implements KeyboardView.OnKeyboardActionListener {
        public void onKey(View v, int keyCode, KeyEvent event) {

        }

        public void onText(CharSequence text) {

        }

        public void swipeLeft() {

        }

        public void onKey(int primaryCode, int[] keyCodes) {

        }

        public void swipeUp() {

        }

        public void swipeDown() {

        }

        public void swipeRight() {

        }

        public void onPress(int primaryCode) {
            if (primaryCode == 8) {
                if (keyBoardFocused == KeyBoardEnum.PHYSICALSTOCK) {
                    String text = physicalStock.getText().toString();
                    if (physicalStock.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        physicalStock.setText(text);
                        physicalStock.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
                if (keyBoardFocused == KeyBoardEnum.PHYSICALSTOCK) {
                    physicalStock.requestFocus();
                }
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.PHYSICALSTOCK) {
                    physicalStock.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    class KeyListAlpha implements KeyboardView.OnKeyboardActionListener {
        public void onKey(View v, int keyCode, KeyEvent event) {

        }

        public void onText(CharSequence text) {

        }

        public void swipeLeft() {

        }

        public void onKey(int primaryCode, int[] keyCodes) {

        }

        public void swipeUp() {

        }

        public void swipeDown() {

        }

        public void swipeRight() {

        }

        public void onPress(int primaryCode) {

           *//* if (primaryCode == 8) {
                if (keyBoardFocused == KeyBoardEnum.CARDTYPE) {
                    String text = cardTypeCard.getText().toString();
                    if (cardTypeCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        cardTypeCard.setText(text);
                        cardTypeCard.setSelection(text.length());
                    } else {
                        prefixCard.requestFocus();
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.CARDTYPE) {
                    cardTypeCard.append("" + ch);
                }
            }*//*
        }

        public void onRelease(int primaryCode) {

        }
    }*/



}

