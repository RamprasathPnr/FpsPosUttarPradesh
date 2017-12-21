package com.omneagate.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.neopixl.pixlui.components.edittext.EditText;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeneficiaryActivationActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    //Progressbar for waiting
    CustomProgressDialog progressBar;

    //HttpConnection service
    HttpClientWrapper httpConnection;

    EditText prefixCard, cardTypeCard, suffixCard, registeredMobile, aRegisterNo;

    BenefActivNewDto benefActivNewDto;

    Button submitButton;

    RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;

    KeyboardView keyview, keyboardViewAlpha;

    KeyBoardEnum keyBoardFocused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.bene_ration_activation);
        Util.LoggingQueue(this, " BeneficiaryActivationActivity", "onCreate called");


        httpConnection = new HttpClientWrapper();
        if (getIntent().getStringExtra("data") != null) {
            String message = getIntent().getStringExtra("data");
            benefActivNewDto = new Gson().fromJson(message, BenefActivNewDto.class);
        }
        setUpCardPage();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUpCardPage() {
        setUpPopUpPage();
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
        prefixCard = (EditText) findViewById(R.id.firstText);
        cardTypeCard = (EditText) findViewById(R.id.secondText);
        aRegisterNo = (EditText) findViewById(R.id.aRegisterNo);
        suffixCard = (EditText) findViewById(R.id.thirdText);
        registeredMobile = (EditText) findViewById(R.id.mobileNumberActivation);
        if (benefActivNewDto != null) {
            String cardNumber = benefActivNewDto.getRationCardNumber();
            prefixCard.setText(StringUtils.substring(cardNumber, 0, 2));
            cardTypeCard.setText(StringUtils.substring(cardNumber, 2, 3));
            suffixCard.setText(StringUtils.substring(cardNumber, 3));
            aRegisterNo.setText(benefActivNewDto.getAregisterNum());
            if (benefActivNewDto.getMobileNum() != null)
                registeredMobile.setText(benefActivNewDto.getMobileNum());
        }
        keyBoardFocused = KeyBoardEnum.AREGISTER;
        aRegisterNo.requestFocus();
        aRegisterNo.setOnClickListener(this);
        aRegisterNo.setShowSoftInputOnFocus(false);
        prefixCard.setOnClickListener(this);
        prefixCard.setShowSoftInputOnFocus(false);
        aRegisterNo.setOnFocusChangeListener(this);
        prefixCard.setOnFocusChangeListener(this);
        cardTypeCard.setOnFocusChangeListener(this);
        suffixCard.setOnFocusChangeListener(this);
        registeredMobile.setOnFocusChangeListener(this);
        cardTypeCard.setOnClickListener(this);
        cardTypeCard.setShowSoftInputOnFocus(false);
        suffixCard.setOnClickListener(this);
        suffixCard.setShowSoftInputOnFocus(false);
        registeredMobile.setOnClickListener(this);
        registeredMobile.setShowSoftInputOnFocus(false);
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BeneficiaryActivationActivity.this, CardActivationActivity.class));
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
        if (v.getId() == R.id.aRegisterNo) {
            checkVisibility();
            keyBoardAppear();
            aRegisterNo.requestFocus();
            keyBoardFocused = KeyBoardEnum.AREGISTER;
            changeLayout(false);
        } else if (v.getId() == R.id.secondText) {
            cardTypeCard.requestFocus();
            checkVisibility();
            changeKeyboard();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.CARDTYPE;
        } else if (v.getId() == R.id.firstText) {
            checkVisibility();
            prefixCard.requestFocus();
            keyBoardAppear();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.thirdText) {
            checkVisibility();
            suffixCard.requestFocus();
            keyBoardAppear();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.mobileNumberActivation) {
            checkVisibility();
            registeredMobile.requestFocus();
            keyBoardAppear();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.MOBILE;
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
        if (v.getId() == R.id.firstText && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            keyBoardFocused = KeyBoardEnum.PREFIX;
            changeLayout(true);
        } else if (v.getId() == R.id.thirdText && hasFocus) {
            keyBoardAppear();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.aRegisterNo && hasFocus) {
            keyBoardAppear();
            checkVisibility();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.AREGISTER;
        } else if (v.getId() == R.id.secondText && hasFocus) {
            changeKeyboard();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.CARDTYPE;
        } else if (v.getId() == R.id.mobileNumberActivation && hasFocus) {
            keyBoardAppear();
            checkVisibility();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.MOBILE;
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
        }
    }

    private void listenersForEditText() {
        prefixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (prefixCard.getText().toString().length() == 2) {
                    String value = prefixCard.getText().toString();
                    if (Integer.parseInt(value) > 33 || Integer.parseInt(value) == 0) {
                        Util.messageBar(BeneficiaryActivationActivity.this, getString(R.string.invalid_card_prefix));
                    } else
                        cardTypeCard.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        aRegisterNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (aRegisterNo.getText().toString().length() == 5)     //size as per your requirement
                {
                    prefixCard.requestFocus();
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
                    if (cardTypeCard.getText().toString().equalsIgnoreCase("X") || cardTypeCard.getText().toString().equalsIgnoreCase("Y"))     //size as per your requirement
                    {
//                        Util.messageBar(com.omneagate.activity.BeneficiaryActivationActivity.this, getString(R.string.invalid_card_prefix));
                    }
                    else {
                        suffixCard.requestFocus();
                    }
//                    suffixCard.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        suffixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (suffixCard.getText().toString().length() == 7) {
                    registeredMobile.requestFocus();
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
        ArrayAdapter<String> cylinderAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.cylindersList));
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
            System.out.print(me2.getKey() + ": ");
            System.out.println(me2.getValue());
            cards2.add(me2.getValue().toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cards2.toArray(new String[cards.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCardType.setAdapter(adapter);
        mSpinnerCardType.setPrompt(getString(R.string.selection));

        String[] adults = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        NoDefaultSpinner spinnerAdult = (NoDefaultSpinner) findViewById(R.id.spinnerAdult);
        ArrayAdapter<String> adultAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, adults);
        adultAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdult.setAdapter(adultAdapt);

        spinnerAdult.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                keyBoardCustom.setVisibility(View.GONE);
                return false;
            }
        });
        String[] persons = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        NoDefaultSpinner spinnerChild = (NoDefaultSpinner) findViewById(R.id.spinnerChild);
        ArrayAdapter<String> childAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, persons);
        childAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChild.setAdapter(childAdapt);
        spinnerChild.setPrompt(getString(R.string.selection));
        spinnerAdult.setPrompt(getString(R.string.selection));
        spinnerChild.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((RelativeLayout) findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
                return false;
            }
        });
        mSpinnerCardType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((RelativeLayout) findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
                return false;
            }
        });
        spinnerCylinder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((RelativeLayout) findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
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

            benefActivNewDto = new BenefActivNewDto();
            String cardNumber1 = prefixCard.getText().toString();
            String cardNumber2 = cardTypeCard.getText().toString();
            String cardNumber3 = suffixCard.getText().toString();
            String mobileNumber = registeredMobile.getText().toString();
            String aRegisterNumber = aRegisterNo.getText().toString();
            Util.LoggingQueue(this, "BeneficiaryActivationActivity", "User entered ::" + "A Register Number" + aRegisterNumber + " cardNumber1:" + cardNumber1 + "cardNumber2:" + cardNumber2 + "cardNumber3:" + cardNumber3 + "::mobile no:" + mobileNumber);
            if (StringUtils.isNotEmpty(aRegisterNumber)) {
                if (Integer.parseInt(aRegisterNumber) == 0) {
                    Util.LoggingQueue(this, "Ration Card Registration", "A Register is zero");
                    Util.messageBar(this, getString(R.string.invalidRegNo));
                    return;
                }
            } else {
                Util.messageBar(this, getString(R.string.emptyRegNo));
                Util.LoggingQueue(this, "Ration Card Registration", "A Register is empty");
                return;
            }


            if (StringUtils.isEmpty(cardNumber1) || StringUtils.isEmpty(cardNumber2) || StringUtils.isEmpty(cardNumber3)) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                Util.LoggingQueue(this, "Ration Card Registration", "Invalid Card number : empty card number");
                return;
            }
            if (StringUtils.isEmpty(cardNumber1) || Integer.parseInt(cardNumber1) > 33 || Integer.parseInt(cardNumber1) == 0) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                Util.LoggingQueue(this, "Ration Card Registration", "Invalid District code --> District code greater than 33");
                return;
            }
            if (cardNumber1.length() == 1) {
                cardNumber1 = "0" + cardNumber1;
                Util.LoggingQueue(this, "Ration Card Registration", "Appending 0 before cardnumber1" + cardNumber1);
            }

            if (cardNumber1.length() != 2 || cardNumber2.length() != 1 || cardNumber3.length() != 7) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                return;
            }
            if (cardNumber2.equalsIgnoreCase("X") || cardNumber2.equalsIgnoreCase("Y")) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
//                Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                return;
            }


            String cardNumber = cardNumber1 + cardNumber2 + cardNumber3;
            if (StringUtils.isNotEmpty(mobileNumber)) {
                if (mobileNumber.length() != 10) {
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

    private void submitCard() {
        Util.LoggingQueue(this, "BeneficiaryActivationActivity", "Moving to Registration confirmation page");
        Intent intent = new Intent(this, RegistrationConfirmActivity.class);
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
        Util.LoggingQueue(this, "BeneficiaryActivationActivity Card ", "Check spinner returning true");
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CardActivationActivity.class));
        Util.LoggingQueue(this, "BeneficiaryActivationActivity Card ", "On back pressed");
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

            if (primaryCode == 8) {
                if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                    String text = aRegisterNo.getText().toString();
                    if (aRegisterNo.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aRegisterNo.setText(text);
                        aRegisterNo.setSelection(text.length());
                    }
                } else if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    String text = prefixCard.getText().toString();
                    if (prefixCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        prefixCard.setText(text);
                        prefixCard.setSelection(text.length());
                    } else {
                        aRegisterNo.requestFocus();
                    }
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    String text = suffixCard.getText().toString();
                    if (suffixCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        suffixCard.setText(text);
                        suffixCard.setSelection(text.length());
                    } else {
                        cardTypeCard.requestFocus();
                    }
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
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                    aRegisterNo.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    prefixCard.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    suffixCard.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    registeredMobile.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    private class SearchARegNoTask extends AsyncTask<String, Void, Boolean> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(BeneficiaryActivationActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            return FPSDBHelper.getInstance(BeneficiaryActivationActivity.this).retrieveARegNoFromBeneficiary(args[0]);
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
                Util.messageBar(BeneficiaryActivationActivity.this, getString(R.string.reg_no_exists));
                Util.LoggingQueue(BeneficiaryActivationActivity.this, "Ration Card Registration", "A Register number is already exists");
                return;
            }
            String aRegistrationNumStr =  Integer.parseInt(aRegisterNo.getText().toString())+"";
            benefActivNewDto.setAregisterNum(aRegistrationNumStr);
            new SearchCardNoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, benefActivNewDto.getRationCardNumber());
        }
    }

    private class SearchCardNoTask extends AsyncTask<String, Void, Boolean> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(BeneficiaryActivationActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            return FPSDBHelper.getInstance(BeneficiaryActivationActivity.this).retrieveCardNoBeneficiary(args[0]);
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
                Util.messageBar(BeneficiaryActivationActivity.this, getString(R.string.ration_card_exists));
                Util.LoggingQueue(BeneficiaryActivationActivity.this, "Ration Card Registration", "Ration Card number already exists");
                return;
            }
            submitCard();
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
