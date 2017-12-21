package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.RationCardListAdapter;
import com.omneagate.service.HttpClientWrapper;

public class RcScanEntryActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    EditText rcNumber;
    RationCardListAdapter rationCardListAdapter;
    RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;
    KeyboardView keyview, keyboardViewAlpha;
    KeyBoardEnum keyBoardFocused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.rc_scan_entry);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpInitialScreen();
    }

    private void setUpInitialScreen() {
        setUpPopUpPage();
        Util.LoggingQueue(this, "Rc scan entry activity", "Setting up Rc scan entry activity");
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sales_top_heading);
        Util.setTamilText((TextView) findViewById(R.id.rationCardNoTv), R.string.ration_card_number1);
        rcNumber = (EditText) findViewById(R.id.rationCardNoEt);
        rcNumber.setOnClickListener(this);
        rcNumber.setShowSoftInputOnFocus(false);
        rcNumber.setOnFocusChangeListener(RcScanEntryActivity.this);
        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rcNo = rcNumber.getText().toString().trim();
                //          String rcNo = "02G4567899";
                if (rcNo.length() == 12) {
                    BeneficiaryDto benef = FPSDBHelper.getInstance(RcScanEntryActivity.this).beneficiaryFromOldCard(rcNo);
                    if (benef != null) {
                        Intent intent = new Intent(RcScanEntryActivity.this, BenefProxyDetailsActivity.class);
                        intent.putExtra("RcNumber", rcNo);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(RcScanEntryActivity.this, "Please enter valid ration card number", Toast.LENGTH_SHORT).show();
                    }

                } else if (rcNo.length() == 0) {
                    Toast.makeText(RcScanEntryActivity.this, "Please enter the ration card number", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RcScanEntryActivity.this, "Please enter valid ration card number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.scanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner();
            }
        });
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
        keyboardumber = (RelativeLayout) findViewById(R.id.keyboardNumber);
        keyboardAlpha = (RelativeLayout) findViewById(R.id.keyboardAlpha);
        Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
        Keyboard keyboardAlp = new Keyboard(this, R.layout.keyboard_alpha);
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

        listenersForEditText();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rationCardNoEt) {
            checkVisibility();
            keyBoardAppear();
            rcNumber.requestFocus();
            keyBoardFocused = KeyBoardEnum.RCNUMBER;
            changeLayout(false);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.rationCardNoEt && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            rcNumber.requestFocus();
            keyBoardFocused = KeyBoardEnum.RCNUMBER;
            changeLayout(false);
        }
    }

    private void listenersForEditText() {
        rcNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (rcNumber.getText().toString().length() == 12) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void launchQRScanner() {
        Util.LoggingQueue(this, "Add proxy activity", "QR scanner called");
        String packageString = getApplicationContext().getPackageName();
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(packageString);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    /**
     * QR code response received for card
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String contents = data.getStringExtra(Intents.Scan.RESULT);
                        Log.e("EncryptedUFC", contents);
//                        getCardHolderDetailsFromUfcNo(contents.trim());
                        BeneficiaryDto benef = FPSDBHelper.getInstance(RcScanEntryActivity.this).beneficiaryDto(contents.trim());
                        String rcNo = benef.getOldRationNumber();
                        rcNumber.setText(rcNo);
                    } catch (Exception e) {
                        Util.messageBar(this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(this, "RcScanEntryActivity", "QR exception called:" + e.toString());
                        Log.e("RcScanEntryActivity", "Empty", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(QRCodeSalesActivity.class.getSimpleName(), "Scan cancelled");
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            default:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                } catch (Exception e) {
                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleOrderActivity.class));
        Util.LoggingQueue(this, "Rc scan entry activity", "On Back pressed Called");
        finish();
    }


    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Intent intent = new Intent(this, SuccessFailureSalesActivity.class);
        Util.LoggingQueue(this, "Error in QRcode", "Navigating to error page");
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }


    private void keyBoardAppear() {
        keyboardumber.setVisibility(View.VISIBLE);
        keyboardAlpha.setVisibility(View.GONE);
    }

    private void changeKeyboard() {
        try {
            keyboardumber.setVisibility(View.GONE);
            keyboardAlpha.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("Error", "keyboard");
        }
    }

    private void changeLayout(boolean value) {
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.proxyLayoutMaster);
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
    }

    class KeyList implements KeyboardView.OnKeyboardActionListener {
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
                if (keyBoardFocused == KeyBoardEnum.RCNUMBER) {
                    String text = rcNumber.getText().toString();
                    if (rcNumber.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        rcNumber.setText(text);
                        rcNumber.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);

            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.RCNUMBER) {
                    rcNumber.append("" + ch);
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
            if (primaryCode == 8) {
                if (keyBoardFocused == KeyBoardEnum.RCNUMBER) {
                    String text = rcNumber.getText().toString();
                    if (rcNumber.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        rcNumber.setText(text);
                        rcNumber.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.CARDTYPE) {
                    rcNumber.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}