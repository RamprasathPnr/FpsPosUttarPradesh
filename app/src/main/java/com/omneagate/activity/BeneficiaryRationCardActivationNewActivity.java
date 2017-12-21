package com.omneagate.activity;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import com.neopixl.pixlui.components.edittext.EditText;

public class BeneficiaryRationCardActivationNewActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    //Progressbar for waiting
    CustomProgressDialog progressBar;

    //HttpConnection service
    HttpClientWrapper httpConnection;

//    EditText prefixCard, cardTypeCard, suffixCard, registeredMobile, aRegisterNo;

    EditText suffixCard, registeredMobile, aRegisterNo;

    BenefActivNewDto benefActivNewDto;

    Button submitButton;

    RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;

    KeyboardView keyview, keyboardViewAlpha;

    KeyBoardEnum keyBoardFocused;

    String TAG = "BeneficiaryRationCardActivationNewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.bene_ration_activation);

        Util.LoggingQueue(this, "BeneficiaryRationCardActivationNewActivity", "onCreate() called ");

        httpConnection = new HttpClientWrapper();
        if (getIntent().getStringExtra("data") != null) {
            String message = getIntent().getStringExtra("data");
            benefActivNewDto = new Gson().fromJson(message, BenefActivNewDto.class);
            Util.LoggingQueue(this, "BeneficiaryRationCardActivationNewActivity", "onCreate() called message = "+message);

        }
        setUpCardPage();
    }


    private void setUpCardPage() {
        setUpPopUpPage();
        Util.LoggingQueue(this, "BeneficiaryRationCardActivationNewActivity", "Setting up main page");
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        appState = (GlobalAppState) getApplication();
        setTamilTextForLabel();
        setSpinner();
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
        submitButton = (Button) findViewById(R.id.submit_button);
        /*prefixCard = (EditText) findViewById(R.id.firstText);
        cardTypeCard = (EditText) findViewById(R.id.secondText);*/
        aRegisterNo = (EditText) findViewById(R.id.aRegisterNo);
        suffixCard = (EditText) findViewById(R.id.rcActivationSuffix);
        registeredMobile = (EditText) findViewById(R.id.mobileNumberActivation);
        if (benefActivNewDto != null) {
            String cardNumber = benefActivNewDto.getRationCardNumber();
            /*prefixCard.setText(StringUtils.substring(cardNumber, 0, 2));
            cardTypeCard.setText(StringUtils.substring(cardNumber, 2, 3));*/
            suffixCard.setText(cardNumber);
            aRegisterNo.setText(benefActivNewDto.getAregisterNum());
            if (benefActivNewDto.getMobileNum() != null)
                registeredMobile.setText(benefActivNewDto.getMobileNum());
        }else{
            benefActivNewDto = new BenefActivNewDto();
        }
        keyBoardFocused = KeyBoardEnum.AREGISTER;
        aRegisterNo.requestFocus();
        aRegisterNo.setOnClickListener(this);
        aRegisterNo.setShowSoftInputOnFocus(false);
        /*prefixCard.setOnClickListener(this);
        prefixCard.setShowSoftInputOnFocus(false);*/
        aRegisterNo.setOnFocusChangeListener(this);
        /*prefixCard.setOnFocusChangeListener(this);
        cardTypeCard.setOnFocusChangeListener(this);*/
        suffixCard.setOnFocusChangeListener(this);
        registeredMobile.setOnFocusChangeListener(this);
        /*cardTypeCard.setOnClickListener(this);
        cardTypeCard.setShowSoftInputOnFocus(false);*/
        suffixCard.setOnClickListener(this);
        suffixCard.setShowSoftInputOnFocus(false);
        registeredMobile.setOnClickListener(this);
        registeredMobile.setShowSoftInputOnFocus(false);
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this, CardActivationActivity.class));
                finish();
            }
        });
        findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCardNumber();
            }
        });
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listenersForEditText();

    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.aRegisterNo) {
                setSuffixPadding();
                checkVisibility();
                keyBoardAppear();
                aRegisterNo.requestFocus();
                keyBoardFocused = KeyBoardEnum.AREGISTER;
                changeLayout(false);
        } /*else if (v.getId() == R.id.secondText) {
                setSuffixPadding();
                cardTypeCard.requestFocus();
                checkVisibility();
                changeKeyboard();
                changeLayout(true);
                keyBoardFocused = KeyBoardEnum.CARDTYPE;
            } else if (v.getId() == R.id.firstText) {
                setSuffixPadding();
                checkVisibility();
                prefixCard.requestFocus();
                keyBoardAppear();
                changeLayout(true);
                keyBoardFocused = KeyBoardEnum.PREFIX;
        } */else if (v.getId() == R.id.thirdText) {
                checkVisibility();
                suffixCard.requestFocus();
                keyBoardAppear();
                changeLayout(true);
                keyBoardFocused = KeyBoardEnum.RCACTIVATIONSUFFIX;
            } else if (v.getId() == R.id.mobileNumberActivation) {
                setSuffixPadding();
                checkVisibility();
                registeredMobile.requestFocus();
                keyBoardAppear();
                changeLayout(false);
                keyBoardFocused = KeyBoardEnum.MOBILE;
            }
        }
        catch(Exception e) {
            Log.e(TAG,"onClick..."+e);
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

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        try {
            if (v.getId() == R.id.firstText && hasFocus) {
                setSuffixPadding();
                checkVisibility();
                keyBoardAppear();
                keyBoardFocused = KeyBoardEnum.PREFIX;
                changeLayout(true);
            } else if (v.getId() == R.id.rcActivationSuffix && hasFocus) {
                keyBoardAppear();
                checkVisibility();
                changeLayout(true);
                keyBoardFocused = KeyBoardEnum.RCACTIVATIONSUFFIX;
            } else if (v.getId() == R.id.aRegisterNo && hasFocus) {
                setSuffixPadding();
                keyBoardAppear();
                checkVisibility();
                changeLayout(false);
                keyBoardFocused = KeyBoardEnum.AREGISTER;
            } else if (v.getId() == R.id.secondText && hasFocus) {
                setSuffixPadding();
                changeKeyboard();
                checkVisibility();
                changeLayout(true);
                keyBoardFocused = KeyBoardEnum.CARDTYPE;
            } else if (v.getId() == R.id.mobileNumberActivation && hasFocus) {
                setSuffixPadding();
                keyBoardAppear();
                checkVisibility();
                changeLayout(false);
                keyBoardFocused = KeyBoardEnum.MOBILE;
            }
        }
        catch(Exception e) {
            Log.e(TAG,"onFocusChange"+e);
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
            Log.e("Error","keyboard");
        }
    }

    private void listenersForEditText() {
        /*prefixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    Util.LoggingQueue(BeneficiaryRationCardActivationNewActivity.this, "BeneficiaryRationCardActivationNewActivity", "listenersForEditText() called");
                    if (prefixCard.getText().toString().length() == 2) {
                        String value = prefixCard.getText().toString();
                        if (Integer.parseInt(value) > 33 || Integer.parseInt(value) == 0 || Integer.parseInt(value) == 00) {
                            Util.messageBar(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this, getString(R.string.invalid_card_prefix));
                        } else
                            cardTypeCard.requestFocus();
                    }

                    if (cardTypeCard.getText().toString().length() == 1) {
                        String value = cardTypeCard.getText().toString();
                        if (value.equalsIgnoreCase("X") || value.equals("Y")) {

                            //  Util.messageBar(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this, getString(R.string.invalid_card_type));
                        } else
                            suffixCard.requestFocus();
                    }
                }

                catch(Exception e) {
                    Log.e(TAG, "prefixCard onTextChanged exc..."+e);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/


        aRegisterNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (aRegisterNo.getText().toString().length() == 5)     //size as per your requirement
                {
//                    prefixCard.requestFocus();
                    suffixCard.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*cardTypeCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (cardTypeCard.getText().toString().length() == 1)     //size as per your requirement
                    {
                        suffixCard.requestFocus();
                    }

            }
            catch(Exception e) {
                Log.e(TAG, "cardTypeCard onTextChanged exc..."+e);
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
                try {
                if (suffixCard.getText().toString().length() == 7) {
                    registeredMobile.requestFocus();
                }
                }
                catch(Exception e) {
                    Log.e(TAG, "suffixCard onTextChanged exc..."+e);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registeredMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (registeredMobile.getText().toString().length() == 10) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setSpinner() {
        NoDefaultSpinner spinnerCylinder = (NoDefaultSpinner) findViewById(R.id.spinner_number_cylinder);
        ArrayAdapter<String> cylinderAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.cylindersList));
        cylinderAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCylinder.setAdapter(cylinderAdapt);
        spinnerCylinder.setPrompt(getString(R.string.selection));

        NoDefaultSpinner mSpinnerCardType = (NoDefaultSpinner) findViewById(R.id.spinnerCardType);
        Map<Integer, String> cards = FPSDBHelper.getInstance(this).getCardType();
        List<String> cards2 = new ArrayList<String>();
        Set set2 = cards.entrySet();
        Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            Log.e("benef activaiton","map values..."+me2.getKey() + ": " + me2.getValue());
            cards2.add(me2.getValue().toString());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cards2.toArray(new String[cards2.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCardType.setAdapter(adapter);
        mSpinnerCardType.setPrompt(getString(R.string.selection));

        String[] adults = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        NoDefaultSpinner spinnerAdult = (NoDefaultSpinner) findViewById(R.id.spinnerAdult);
        ArrayAdapter<String> adultAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, adults);
        adultAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdult.setAdapter(adultAdapt);

        spinnerAdult.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setSuffixPadding();
                keyBoardCustom.setVisibility(View.GONE);
                return false;
            }
        });
        String[] persons = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        NoDefaultSpinner spinnerChild = (NoDefaultSpinner) findViewById(R.id.spinnerChild);
        ArrayAdapter<String> childAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, persons);
        childAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChild.setAdapter(childAdapt);
        spinnerChild.setPrompt(getString(R.string.selection));
        spinnerAdult.setPrompt(getString(R.string.selection));
        spinnerChild.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setSuffixPadding();
                ( findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
                return false;
            }
        });
        mSpinnerCardType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setSuffixPadding();
                (findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
                return false;
            }
        });
        spinnerCylinder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setSuffixPadding();
                (findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
                return false;
            }
        });
        if (benefActivNewDto != null) {
            int selection = getSelection(cards2, benefActivNewDto.getCardTypeDef());
            mSpinnerCardType.setSelection(selection);
            spinnerChild.setSelection(benefActivNewDto.getNumOfChild());
            spinnerAdult.setSelection(benefActivNewDto.getNumOfAdults() - 1);
            spinnerCylinder.setSelection(benefActivNewDto.getNumOfCylinder());
        }
    }

    private int getSelection(List<String> cards, String cardDef) {
        int selection = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (StringUtils.contains(cards.get(i), cardDef)) {
                return i;
            }
        }
        return selection;

    }

    private void setTamilTextForLabel() {
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.card_registration);
        Util.setTamilText((TextView) findViewById(R.id.a_reg_number), R.string.aRegisterNo);
        Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.submit_button), R.string.submit);
        Util.setTamilText((TextView) findViewById(R.id.ration_card_no), R.string.normal_ration_card_number);
        Util.setTamilText((TextView) findViewById(R.id.card_type), R.string.normal_cardCap);
        Util.setTamilText((TextView) findViewById(R.id.noOfCylinderTitle), R.string.cylinderHints);
        Util.setTamilText((TextView) findViewById(R.id.mob_number), R.string.mob_number);
        Util.setTamilText((TextView) findViewById(R.id.number_adults), R.string.number_adult_cap);
        Util.setTamilText((TextView) findViewById(R.id.number_child), R.string.number_child_cap);
    }

    private void getCardNumber() {
        try {
            /*String cardNumber1 = prefixCard.getText().toString();
            String cardNumber2 = cardTypeCard.getText().toString();*/
            String cardNumber3 = suffixCard.getText().toString();
            String mobileNumber = registeredMobile.getText().toString();
            String aRegisterNumber = aRegisterNo.getText().toString();
//            Util.LoggingQueue(this, "Ration Card Registration", "User entered ::" + "A Register Number" + aRegisterNumber + " cardNumber1:" + cardNumber1 + "cardNumber2:" + cardNumber2 + "cardNumber3:" + cardNumber3 + "::mobile no:" + mobileNumber);
            if (StringUtils.isNotEmpty(aRegisterNumber)) {
                try {
                    if (Integer.parseInt(aRegisterNumber) == 0) {
                        Util.LoggingQueue(this, "Ration Card Registration", "A Register is zero");
                        Util.messageBar(this, getString(R.string.invalidRegNo));
                        return;
                    }
                }
                catch(Exception e) {}
            } else {
                Util.messageBar(this, getString(R.string.emptyRegNo));
                Util.LoggingQueue(this, "Ration Card Registration", "A Register is empty");
                return;
            }

            try {
                if (Integer.parseInt(cardNumber3) == 0) {
                    Util.messageBar(this, getString(R.string.invalid_card_no));
                    return;
                }
            }
            catch(Exception e) {}
            if (StringUtils.isEmpty(cardNumber3)) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                Util.LoggingQueue(this, "Ration Card Registration", "Invalid Card number : empty card number");
                return;
            }
            if (cardNumber3.length() != 12) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                return;
            }
            String cardNumber = cardNumber3;
            if (StringUtils.isNotEmpty(mobileNumber)) {
                try {
                    if (Integer.parseInt(mobileNumber) == 0) {
                        Util.messageBar(this, getString(R.string.invalidMobile));
                        return;
                    }
                }
                catch(Exception e) {}

                if (String.valueOf(mobileNumber.charAt(0)).equalsIgnoreCase("0")) {
                    Util.messageBar(this, getString(R.string.mobileNumberZero));
                    return;
                }

                if ((mobileNumber.length() != 10)) {
                    Util.messageBar(this, getString(R.string.invalidMobile));
                    Util.LoggingQueue(this, "Ration Card Registration", "Length of Mobile Number is not eqeual to 10");
                    return;
                }
            }
            Util.LoggingQueue(this, "Ration Card Registration", "Card no:" + cardNumber + "::mobile no:" + mobileNumber);
            if (!checkSpinnerValues()) {
                return;
            }

            benefActivNewDto.setRationCardNumber(cardNumber);
            benefActivNewDto.setMobileNum(mobileNumber);
            

            new SearchARegNoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, aRegisterNumber);
        } catch (Exception e) {
            Util.LoggingQueue(this, "Ration Card Registration", e.getMessage());
            Util.messageBar(this, getString(R.string.internalError));
        }
    }

    //String isMobileNoEmpty = "";

    private void submitCard() {
        Util.LoggingQueue(this, "Ration Card Registration", "Moving to Registration confirmation page");
            Intent intent = new Intent(this, RationCardActivationAadharActivity.class);
            intent.putExtra("data", new Gson().toJson(benefActivNewDto));
            startActivity(intent);
            finish();

    }

    private boolean checkSpinnerValues() {
        NoDefaultSpinner mSpinnerCardType = (NoDefaultSpinner) findViewById(R.id.spinnerCardType);
        NoDefaultSpinner spinnerCylinder = (NoDefaultSpinner) findViewById(R.id.spinner_number_cylinder);
        NoDefaultSpinner spinnerAdult = (NoDefaultSpinner) findViewById(R.id.spinnerAdult);
        NoDefaultSpinner spinnerChild = (NoDefaultSpinner) findViewById(R.id.spinnerChild);
        if (spinnerAdult.getSelectedItem() == null) {
            Util.messageBar(this, getString(R.string.number_of_adults));
            Util.LoggingQueue(this, "Ration Card Registration", "No of Adults not selected");
            return false;
        } else {
            benefActivNewDto.setNumOfAdults(Integer.parseInt(spinnerAdult.getSelectedItem().toString()));
        }
        if (spinnerChild.getSelectedItem() == null) {
            Util.messageBar(this, getString(R.string.number_of_children));
            Util.LoggingQueue(this, "Ration Card Registration", "No of Children not selected");
            return false;
        } else {
            benefActivNewDto.setNumOfChild(Integer.parseInt(spinnerChild.getSelectedItem().toString()));
        }
        if (mSpinnerCardType.getSelectedItem() == null) {
            Util.messageBar(this, getString(R.string.select_card_type));
            Util.LoggingQueue(this, "Ration Card Registration", "Card Type not selected");
            return false;
        } else {
            String[] cardTypeData = StringUtils.split(mSpinnerCardType.getSelectedItem().toString(), ":");
            benefActivNewDto.setCardTypeDef(cardTypeData[1].trim());
            benefActivNewDto.setCardType(mSpinnerCardType.getSelectedItem().toString().charAt(0));
        }

        if (spinnerCylinder.getSelectedItem() == null) {
            Util.messageBar(this, getString(R.string.number_of_cylinder));
            Util.LoggingQueue(this, "Ration Card Registration", "No of Cyclinder not selected");
            return false;
        } else {
            benefActivNewDto.setNumOfCylinder(Integer.parseInt(spinnerCylinder.getSelectedItem().toString()));
        }
        Util.LoggingQueue(this, "Ration Card Registration", "Check spinner returning true");
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CardActivationActivity.class));
        Util.LoggingQueue(this, "Ration Card Registration", "On back pressed");
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {


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
            try {
                if (primaryCode == 8) {
                    if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                        String text = aRegisterNo.getText().toString();
                        if (aRegisterNo.length() > 0) {
                            text = text.substring(0, text.length() - 1);
                            aRegisterNo.setText(text);
                            aRegisterNo.setSelection(text.length());
                        }
                } /*else if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                        String text = prefixCard.getText().toString();
                        if (prefixCard.length() > 0) {
                            text = text.substring(0, text.length() - 1);
                            prefixCard.setText(text);
                            prefixCard.setSelection(text.length());
                        } else {
                            aRegisterNo.requestFocus();
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
                        String text = registeredMobile.getText().toString();
                        if (registeredMobile.length() > 0) {
                            text = text.substring(0, text.length() - 1);
                            registeredMobile.setText(text);
                            registeredMobile.setSelection(text.length());
                        }
                    }
                } else if (primaryCode == 46) {
                    keyBoardCustom.setVisibility(View.GONE);
                    if (keyBoardFocused == KeyBoardEnum.RCACTIVATIONSUFFIX) {
                        setSuffixPadding();
                        registeredMobile.requestFocus();
                    }
                } else {
                    char ch = (char) primaryCode;
                    if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                        aRegisterNo.append("" + ch);
                } /*else if (keyBoardFocused == KeyBoardEnum.PREFIX) {
//                    prefixCard.append("" + ch);
                } */else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                        suffixCard.append("" + ch);
                    } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                        registeredMobile.append("" + ch);
                    }
                }
            }
            catch(Exception e) {
                Log.e(TAG,"on press keylist exc..."+e);
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    private class SearchARegNoTask extends AsyncTask<String, Void, Boolean> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this).retrieveARegNoFromBeneficiary(args[0]);
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean unused) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
            catch(Exception e) {}

            if (unused) {
                Util.messageBar(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this, getString(R.string.reg_no_exists));
                Util.LoggingQueue(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this, "Ration Card Registration", "A Register number is already exists");
                return;
            }

            //benefActivNewDto.setAregisterNum(aRegisterNo.getText().toString());
            String aRegistrationNumStr =  Integer.parseInt(aRegisterNo.getText().toString())+"";
            benefActivNewDto.setAregisterNum(aRegistrationNumStr);

            new SearchCardNoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, benefActivNewDto.getRationCardNumber());
        }
    }

    private class SearchCardNoTask extends AsyncTask<String, Void, Boolean> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this).retrieveCardNoBeneficiary(args[0]);
        }

        // can use UI thread here
        protected void onPostExecute(final Boolean unused) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
            catch(Exception e) {}

            if (unused) {
                Util.messageBar(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this, getString(R.string.ration_card_exists));
                Util.LoggingQueue(com.omneagate.activity.BeneficiaryRationCardActivationNewActivity.this, "Ration Card Registration", "Ration Card number already exists");
                return;
            }
            submitCard();
        }
    }

    private void setSuffixPadding() {
        if (suffixCard.getText().toString().length() == 12) {
        }
        else if (suffixCard.getText().toString().length() == 11) {
            suffixCard.setText("0" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 10) {
            suffixCard.setText("00" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 9) {
            suffixCard.setText("000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 8) {
            suffixCard.setText("0000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 7) {
            suffixCard.setText("00000" + suffixCard.getText().toString());
            }
            else if (suffixCard.getText().toString().length() == 6) {
            suffixCard.setText("000000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 5) {
            suffixCard.setText("0000000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 4) {
            suffixCard.setText("00000000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 3) {
            suffixCard.setText("000000000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 2) {
            suffixCard.setText("0000000000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 1) {
            suffixCard.setText("00000000000" + suffixCard.getText().toString());
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
                /*if (keyBoardFocused == KeyBoardEnum.CARDTYPE) {
                    String text = cardTypeCard.getText().toString();

                    if (cardTypeCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        cardTypeCard.setText(text);
                        cardTypeCard.setSelection(text.length());
                    } else {
                        prefixCard.requestFocus();
                    }
                }*/
            } else if (primaryCode == 46) {
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
    }

}
