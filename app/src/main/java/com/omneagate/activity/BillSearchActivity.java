package com.omneagate.activity;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neopixl.pixlui.components.edittext.EditText;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.SearchSelected;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.DateSelectionDialog;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/**
 * Created for Bill Search
 */
public class BillSearchActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

//    EditText prefixCard, cardTypeCard, suffixCard, mobileNumber, dateOfPurchase, aRegNumber;

    EditText suffixCard, mobileNumber, dateOfPurchase, aRegNumber;

    SearchSelected searchSelection;

    RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;

    KeyboardView keyview, keyboardViewAlpha;

    KeyBoardEnum keyBoardFocused;

    long dateSelection = 0l;

    DateSelectionDialog dateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_search);
        setUpInitialPage();
        dateDialog = new DateSelectionDialog(com.omneagate.activity.BillSearchActivity.this);
    }

    private void setUpInitialPage() {
        try {
            setUpPopUpPage();
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
            keyBoardFocused = KeyBoardEnum.PREFIX;

            Util.LoggingQueue(this, "BillSearchActivity", "Starting up page Called");
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.bill_search);
            Util.setTamilText((TextView) findViewById(R.id.bill_search), R.string.search);
            Util.setTamilText((TextView) findViewById(R.id.scan_qr), R.string.scan_qr);
            Util.setTamilText((TextView) findViewById(R.id.or_string), R.string.or);
            /*prefixCard = (EditText) findViewById(R.id.firstText);
            cardTypeCard = (EditText) findViewById(R.id.secondText);*/
            suffixCard = (EditText) findViewById(R.id.thirdText);
            /*prefixCard.setOnClickListener(this);
            prefixCard.setShowSoftInputOnFocus(false);
            prefixCard.setOnFocusChangeListener(this);
            cardTypeCard.setOnFocusChangeListener(this);*/
            suffixCard.setOnFocusChangeListener(this);
            /*cardTypeCard.setOnClickListener(this);
            cardTypeCard.setShowSoftInputOnFocus(false);*/
            suffixCard.setOnClickListener(this);
            suffixCard.setShowSoftInputOnFocus(false);
            mobileNumber = (EditText) findViewById(R.id.mobileNumber);
            aRegNumber = (EditText) findViewById(R.id.aRegNumber);
            dateOfPurchase = (EditText) findViewById(R.id.dateOfPurchase);
            mobileNumber.setOnFocusChangeListener(this);
            mobileNumber.setOnClickListener(this);
            mobileNumber.setOnClickListener(this);
            dateOfPurchase.setOnFocusChangeListener(this);
            dateOfPurchase.setOnClickListener(this);
            aRegNumber.setOnFocusChangeListener(this);
            aRegNumber.setOnClickListener(this);
            mobileNumber.setShowSoftInputOnFocus(false);
            aRegNumber.setShowSoftInputOnFocus(false);
            dateOfPurchase.setShowSoftInputOnFocus(false);
            textChangeListeners();
            findViewById(R.id.scan_qr_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.LoggingQueue(com.omneagate.activity.BillSearchActivity.this, "Bill search", "Searching by qr");
                    launchQRScanner();
                }
            });

            findViewById(R.id.bill_search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchSelection != null) {
                        switch (searchSelection) {
                            case DATE_TRANSACTION:
                                dateBySearch();
                                break;
                            case MOBILE:
                                mobileNumberSearch();
                                break;
                            case AREGISTER:
                                aRegisterNumberSearch();
                                break;
                            case RATION_CARD:
                                rationNumberSearch();
                                break;
                        }
                    }
                }
            });
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            Log.e("error",e.toString());
        }
    }


    private void textChangeListeners() {
        /*prefixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (prefixCard.getText().toString().length() == 2) {
                    String value = prefixCard.getText().toString();
//                    if (Integer.parseInt(value) > 33 || Integer.parseInt(value) == 0) {
                    if (Integer.parseInt(value) == 0) {
                        Util.messageBar(com.omneagate.activity.BillSearchActivity.this, getString(R.string.invalid_card_prefix));
                    } else
                        cardTypeCard.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cardTypeCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (cardTypeCard.getText().toString().length() == 1)     //size as per your requirement
                {
                    suffixCard.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        suffixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (suffixCard.getText().toString().length() == 7) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        aRegNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 5) {
                    searchSelection = SearchSelected.AREGISTER;
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateOfPurchase:
                if (!dateDialog.isShowing()) {
                    dateOfPurchase.requestFocus();
                    keyBoardCustom.setVisibility(View.GONE);
                    searchSelection = SearchSelected.DATE_TRANSACTION;
                    keyBoardFocused = KeyBoardEnum.NOTHING;
                    dateDialog.show();
                }
                break;
            /*case R.id.firstText:
                checkVisibility();
                keyBoardAppear();
                changeLayout(false);
                searchSelection = SearchSelected.RATION_CARD;
                keyBoardFocused = KeyBoardEnum.PREFIX;
                prefixCard.requestFocus();
                break;
            case R.id.secondText:
                checkVisibility();
                changeLayout(false);
                searchSelection = SearchSelected.RATION_CARD;
                keyBoardFocused = KeyBoardEnum.CARDTYPE;
                cardTypeCard.requestFocus();
                changeKeyboard();
                break;*/
            case R.id.thirdText:
                checkVisibility();
                keyBoardAppear();
                changeLayout(false);
                searchSelection = SearchSelected.RATION_CARD;
                keyBoardFocused = KeyBoardEnum.SUFFIX;
                suffixCard.requestFocus();
                break;
            case R.id.aRegNumber:
                checkVisibility();
                keyBoardAppear();
                changeLayout(true);
                searchSelection = SearchSelected.AREGISTER;
                keyBoardFocused = KeyBoardEnum.AREGISTER;
                aRegNumber.requestFocus();
                break;
            case R.id.mobileNumber:
                checkVisibility();
                keyBoardAppear();
                changeLayout(true);
                searchSelection = SearchSelected.MOBILE;
                keyBoardFocused = KeyBoardEnum.MOBILE;
                mobileNumber.requestFocus();
                break;
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.firstText && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            mobileNumber.setText("");
            dateOfPurchase.setText("");
            changeLayout(false);
            aRegNumber.setText("");
            searchSelection = SearchSelected.RATION_CARD;
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.thirdText && hasFocus) {
            keyBoardAppear();
            checkVisibility();
            searchSelection = SearchSelected.RATION_CARD;
            mobileNumber.setText("");
            dateOfPurchase.setText("");
            aRegNumber.setText("");
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.aRegNumber && hasFocus) {
            keyBoardAppear();
            checkVisibility();
            mobileNumber.setText("");
            /*prefixCard.setText("");
            cardTypeCard.setText("");*/
            suffixCard.setText("");
            changeLayout(true);
            dateOfPurchase.setText("");
            keyBoardFocused = KeyBoardEnum.AREGISTER;
            searchSelection = SearchSelected.AREGISTER;
        } else if (v.getId() == R.id.secondText && hasFocus) {
            changeKeyboard();
            searchSelection = SearchSelected.RATION_CARD;
            mobileNumber.setText("");
            dateOfPurchase.setText("");
            aRegNumber.setText("");
            checkVisibility();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.CARDTYPE;
        } else if (v.getId() == R.id.mobileNumber && hasFocus) {
            keyBoardAppear();
            checkVisibility();
            changeLayout(true);
            searchSelection = SearchSelected.MOBILE;
            dateOfPurchase.setText("");
            /*prefixCard.setText("");
            cardTypeCard.setText("");*/
            suffixCard.setText("");
            aRegNumber.setText("");
            keyBoardFocused = KeyBoardEnum.MOBILE;
        } else if (v.getId() == R.id.dateOfPurchase && hasFocus) {
            if (!dateDialog.isShowing()) {
                keyBoardCustom.setVisibility(View.GONE);
                searchSelection = SearchSelected.DATE_TRANSACTION;
                keyBoardFocused = KeyBoardEnum.NOTHING;
                mobileNumber.setText("");
                /*prefixCard.setText("");
                cardTypeCard.setText("");*/
                suffixCard.setText("");
                aRegNumber.setText("");
                dateDialog.show();
            }
        }
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
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
            Log.e("Error",e.toString(),e);
        }

    }

    private void launchQRScanner() {
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
                /*if (resultCode == Activity.RESULT_OK) {
                    try {
                        String contents = data.getStringExtra(Intents.Scan.RESULT);
                        Log.e("EncryptedUFC",contents);
                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(this, "QRcode sales", "QR exception called:" + e.toString());
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(QRCodeSalesActivity.class.getSimpleName(),"Scan cancelled");
                }
*/
                break;

            default:
                break;
        }
    }

    //Response from QR reader
    private void qrResponse(String result) {
        String languageCode = FPSDBHelper.getInstance(this).getMasterData("language");
        Util.changeLanguage(this, languageCode);
        GlobalAppState.language = languageCode;
        if (StringUtils.isNotEmpty(result)) {
            String lines[] = result.split("\\r?\\n");
            searchQRBills(lines[0]);

        }
    }

    private void searchQRBills(String qrCode) {
        if (StringUtils.isNotEmpty(qrCode)) {
            BeneficiaryDto beneficiaryDto = FPSDBHelper.getInstance(this).beneficiaryDto(qrCode);
            if (beneficiaryDto == null) {
                Util.messageBar(this, getString(R.string.qr_exists));
            } else {
                searchBills(qrCode, "qrCode");
            }
        } else {
            Util.messageBar(this, getString(R.string.invalid_qr));
        }
    }

    public void setTextDate(String textDate) {
        searchSelection = SearchSelected.DATE_TRANSACTION;
        Util.LoggingQueue(com.omneagate.activity.BillSearchActivity.this, "Bill search", "Searching by date of purchase");
        dateOfPurchase.setText(textDate);
    }

    private void mobileNumberSearch() {
        String mobileNo = mobileNumber.getText().toString();
        if (StringUtils.isNotEmpty(mobileNo)) {
            if (StringUtils.length(mobileNo) != 10) {
                Util.messageBar(this, getString(R.string.invalidMobile));
            } else {
                BeneficiaryDto bene = FPSDBHelper.getInstance(this).retrieveIdFromBeneficiary(mobileNo);
                if (bene == null) {
                    Util.messageBar(this, getString(R.string.mobile_exists));
                } else {
                    searchBills(mobileNo, "mobileNo");
                }
            }
        } else {
            Util.messageBar(this, getString(R.string.mobile_no_empty));
        }
    }

    private void aRegisterNumberSearch() {
        String aRegister = aRegNumber.getText().toString();
        if (StringUtils.isNotEmpty(aRegister)) {
            if (Integer.parseInt(aRegister) == 0) {
                Util.messageBar(this, getString(R.string.invalidRegNo));
            } else {
                BeneficiaryDto bene = FPSDBHelper.getInstance(this).retrieveIdFromBeneficiaryReg(Integer.parseInt(aRegister));
                if (bene == null){
                    Util.messageBar(this, getString(R.string.notExistsRegNo));
                } else {
                    searchBills(aRegister, "aRegister");
                }
            }
        } else {
            Util.messageBar(this, getString(R.string.emptyRegNo));
        }
    }


    private void searchBills(String bills, String type) {
        Intent intent = new Intent(this, BillActivity.class);
        intent.putExtra("bills", bills);
        intent.putExtra("search", type);
        startActivity(intent);
        finish();
    }

    private void rationNumberSearch() {
        try {
            /*String cardNumber1 = prefixCard.getText().toString();
            String cardNumber2 = cardTypeCard.getText().toString();*/
            String cardNumber3 = suffixCard.getText().toString();
            if (StringUtils.isEmpty(cardNumber3)) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                return;
            }
//            if (StringUtils.isEmpty(cardNumber1) || Integer.parseInt(cardNumber1) > 33 || Integer.parseInt(cardNumber1) == 0) {
            /*if (StringUtils.isEmpty(cardNumber1) || Integer.parseInt(cardNumber1) == 0) {

                    Util.messageBar(this, getString(R.string.invalid_card_no));
                return;
            }
            if (cardNumber1.length() == 1) {
                cardNumber1 = "0" + cardNumber1;
            }*/
            if (cardNumber3.length() != 12) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                return;
            }
            String cardNumber = cardNumber3;
            Util.LoggingQueue(this, "Bill search", "Ration card:" + cardNumber);
            BeneficiaryDto beneficiaryDto = FPSDBHelper.getInstance(this).beneficiaryFromOldCard(cardNumber.toUpperCase());

            if (beneficiaryDto == null) {
                Util.messageBar(this, getString(R.string.ration_exists));
            } else {
                Util.LoggingQueue(this, "Bill search", "Beneficiary:" + beneficiaryDto.toString());
                searchBills(cardNumber, "cardNumber");
            }
        } catch (Exception e) {
            Util.messageBar(this, getString(R.string.ration_exists));
        }
    }


    private void changeLayout(boolean value) {
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

    private void dateBySearch() {
        try {
            String dateSearch = dateOfPurchase.getText().toString();
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = format.parse(dateSearch);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String searchDate = simpleDateFormat.format(date);
            Util.LoggingQueue(this, "Bill search", "date search:" + searchDate);
            Intent intent = new Intent(this, BillByDateActivity.class);
            intent.putExtra("bills", searchDate);
            intent.putExtra("search", "date");
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Util.LoggingQueue(this, "Bill search", "error:" + e.toString());
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TransactionsSubmenuActivity.class));
        Util.LoggingQueue(this, "Bill search", "On back pressed calling");
        finish();
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
                if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                    String text = aRegNumber.getText().toString();
                    if (aRegNumber.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aRegNumber.setText(text);
                        aRegNumber.setSelection(text.length());
                    }
                } /*else if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    String text = prefixCard.getText().toString();
                    if (prefixCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        prefixCard.setText(text);
                        prefixCard.setSelection(text.length());
                    }
                }*/ else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    String text = suffixCard.getText().toString();
                    if (suffixCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        suffixCard.setText(text);
                        suffixCard.setSelection(text.length());
                    } /*else {
                        cardTypeCard.requestFocus();
                    }*/
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    String text = mobileNumber.getText().toString();
                    if (mobileNumber.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        mobileNumber.setText(text);
                        mobileNumber.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                    aRegNumber.append("" + ch);
                } /*else if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    prefixCard.append("" + ch);
                }*/ else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    suffixCard.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    mobileNumber.append("" + ch);
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

            /*if (primaryCode == 8) {
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
            } else*/ if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                /*if (keyBoardFocused == KeyBoardEnum.CARDTYPE) {
                    cardTypeCard.append("" + ch);
                }*/
            }
        }

        public void onRelease(int primaryCode) {

        }
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
        try {
            if ((dateDialog != null) && dateDialog.isShowing()) {
                dateDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            dateDialog = null;
        }
    }

}