package com.omneagate.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.client.android.Intents;
import com.neopixl.pixlui.components.edittext.EditText;
import com.omneagate.DTO.AadharSeedingDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryUpdateDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.AadhaarVerhoeffAlgorithm;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;


import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UpdateUserDetailsActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    BeneficiaryDto benef;

    EditText mobileNumber, aRegNumber;

    TextView aadharNumber1, aadharNumber2, aadharNumber3;

    RelativeLayout keyBoardCustom;

    KeyboardView keyview;

    KeyBoardEnum keyBoardFocused;

    BeneficiaryUpdateDto beneUpdate;

    AadharSeedingDto aadharSeedingDto;


    // Zbar variables
    /*private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;*/
    String rationCardNumberStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_update_user_details);
        rationCardNumberStr = getIntent().getStringExtra("qrCode");
        Util.LoggingQueue(this, "UpdateUserDetailsActivity", "onCreate() called ");

        Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity", "onCreate() called rationCardNumberStr ="+rationCardNumberStr);


        benef = FPSDBHelper.getInstance(this).beneficiaryFromOldCard(rationCardNumberStr);
        String updateString = getIntent().getStringExtra("beneUpdate");
        Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity", "onCreate() called beneficiaryFromOldCard(rationCardNumberStr) ="+benef.toString());

        if (updateString != null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            beneUpdate = gson.fromJson(updateString, BeneficiaryUpdateDto.class);
            Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity", "if old card exists  beneUpdate="+beneUpdate);


        } else {
            beneUpdate = new BeneficiaryUpdateDto();
            Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity", "No old card found  beneUpdate="+beneUpdate);


        }
        initialPage();
    }

    private void initialPage() {
        aadharSeedingDto = new AadharSeedingDto();

        setUpPopUpPage();
        mobileNumber = (EditText) findViewById(R.id.mobileNumberUpdationText);
        aadharNumber1 = (TextView) findViewById(R.id.aadharNumberFirst);
        aadharNumber2 = (TextView) findViewById(R.id.aadharNumberMiddle);
        aadharNumber3 = (TextView) findViewById(R.id.aadharNumberFinal);
        aRegNumber = (EditText) findViewById(R.id.cylinder_value);
        mobileNumber.setShowSoftInputOnFocus(false);
        mobileNumber.disableCopyAndPaste();
        /*aadharNumber1.setShowSoftInputOnFocus(false);
        aadharNumber1.disableCopyAndPaste();
        aadharNumber2.setShowSoftInputOnFocus(false);
        aadharNumber2.disableCopyAndPaste();
        aadharNumber3.setShowSoftInputOnFocus(false);
        aadharNumber3.disableCopyAndPaste();*/
        aRegNumber.setShowSoftInputOnFocus(false);
        aRegNumber.disableCopyAndPaste();

        try {
            if (benef.getAregisterNum() != null && StringUtils.isNotEmpty(benef.getAregisterNum()) && !StringUtils.equalsIgnoreCase(benef.getAregisterNum(), "-1")) {
                aRegNumber.setText(benef.getAregisterNum());
                aRegNumber.setOnFocusChangeListener(null);
                aRegNumber.setOnClickListener(null);
                aRegNumber.setClickable(false);
                aRegNumber.setFocusable(false);
            } else {
                if (StringUtils.isNotEmpty(beneUpdate.getAregisterNumber())) {
                    aRegNumber.setText(beneUpdate.getAregisterNumber());
                }
                aRegNumber.setOnFocusChangeListener(this);
                aRegNumber.setOnClickListener(this);
                aRegNumber.setAutoFocus(true);
                keyBoardFocused = KeyBoardEnum.AREG;
            }
        }
        catch(Exception e) {}

        try {
            if (benef.getMobileNumber() != null && StringUtils.isNotEmpty(benef.getMobileNumber())) {
                mobileNumber.setText(benef.getMobileNumber());
                mobileNumber.setOnFocusChangeListener(null);
                mobileNumber.setOnClickListener(null);
                mobileNumber.setClickable(false);
                mobileNumber.setFocusable(false);
            } else {
                if (StringUtils.isNotEmpty(beneUpdate.getMobileNumber())) {
                    mobileNumber.setText(beneUpdate.getMobileNumber());
                }
                mobileNumber.setOnFocusChangeListener(this);
                mobileNumber.setOnClickListener(this);
                mobileNumber.setAutoFocus(true);
//            keyBoardFocused = KeyBoardEnum.MOBILE;
                if (benef.getAregisterNum() != null) {
                    keyBoardFocused = KeyBoardEnum.MOBILE;
                }
            }
        }
        catch(Exception e) {}

        try {
            if (benef.getFamilyHeadAadharNumber() != null && StringUtils.isNotEmpty(benef.getFamilyHeadAadharNumber())) {
                String aadharNumber = benef.getFamilyHeadAadharNumber();
                if (StringUtils.isNoneEmpty(aadharNumber)) {
                    aadharNumber1.setText(StringUtils.substring(aadharNumber, 0, 4));
                    aadharNumber2.setText(StringUtils.substring(aadharNumber, 4, 8));
                    aadharNumber3.setText(StringUtils.substring(aadharNumber, 8));
                }
                ((ImageView) findViewById(R.id.scanAadhar)).setVisibility(View.INVISIBLE);
                /*aadharNumber1.setOnFocusChangeListener(null);
                aadharNumber1.setOnClickListener(null);
                aadharNumber2.setOnFocusChangeListener(null);
                aadharNumber2.setOnClickListener(null);
                aadharNumber3.setOnFocusChangeListener(null);
                aadharNumber3.setOnClickListener(null);
                aadharNumber1.setClickable(false);
                aadharNumber1.setFocusable(false);
                aadharNumber1.setAutoFocus(false);
                aadharNumber2.setClickable(false);
                aadharNumber2.setFocusable(false);
                aadharNumber2.setAutoFocus(false);
                aadharNumber3.setClickable(false);
                aadharNumber3.setFocusable(false);
                aadharNumber3.setAutoFocus(false);*/
            } else {
//                String aadharNumber = beneUpdate.getFamilyHeadAadharNumber();
                try {
                    String aadharNumber = beneUpdate.getAadhaarSeedingDto().getUid();
                    if (StringUtils.isNotEmpty(aadharNumber)) {
                        aadharNumber1.setText(StringUtils.substring(aadharNumber, 0, 4));
                        aadharNumber2.setText(StringUtils.substring(aadharNumber, 4, 8));
                        aadharNumber3.setText(StringUtils.substring(aadharNumber, 8));
                    }
                }
                catch(Exception e) {}

                /*aadharNumber1.setOnFocusChangeListener(this);
                aadharNumber1.setOnClickListener(this);
                aadharNumber2.setOnFocusChangeListener(this);
                aadharNumber2.setOnClickListener(this);
                aadharNumber3.setOnFocusChangeListener(this);
                aadharNumber3.setOnClickListener(this);*/



                if (benef.getMobileNumber() != null) {
                    keyBoardFocused = KeyBoardEnum.PREFIX;
                }
            }
        }
        catch(Exception e) {
            Log.e("exc........",""+e);
        }

        listenersForEditText();
        TextView rationCardNumber = (TextView) findViewById(R.id.ration_card_value);
        String cardNo = benef.getOldRationNumber();
//        String cardNumber = StringUtils.substring(cardNo, 0, 2) + "/" + StringUtils.substring(cardNo, 2, 3) + "/" + StringUtils.substring(cardNo, 3, 10);

        rationCardNumber.setText(cardNo);
//        ((TextView) findViewById(R.id.a_register_value)).setText(benef.getAregisterNum());
//        ((TextView) findViewById(R.id.cylinder_value)).setText(String.valueOf(benef.getAregisterNum()));
        ((TextView) findViewById(R.id.number_adult)).setText(String.valueOf(benef.getNumOfAdults()));
        ((TextView) findViewById(R.id.card_value)).setText(String.valueOf(benef.getNumOfCylinder()));
        ((TextView) findViewById(R.id.number_children)).setText(String.valueOf(benef.getNumOfChild()));
        keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
        Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
        //create KeyboardView object
        keyview = (KeyboardView) findViewById(R.id.customkeyboard);
        //attache the keyboard object to the KeyboardView object
        keyview.setKeyboard(keyboard);
        //show the keyboard
        keyview.setVisibility(KeyboardView.VISIBLE);
        keyview.setPreviewEnabled(false);
        //take the keyboard to the front
        keyview.bringToFront();
        //register the keyboard to receive the key pressed
        keyview.setOnKeyboardActionListener(new KeyList());

        String cardType = FPSDBHelper.getInstance(this).getCardTypeFromId(benef.getCardTypeId());
        ((TextView) findViewById(R.id.a_register_value)).setText(cardType);
        Util.setTamilText((TextView) findViewById(R.id.button_cancel), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.button_Submit), R.string.submit);
        Util.setTamilText((TextView) findViewById(R.id.benef_members_aadhar_reg), R.string.members_aadhar_registration);
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.updateRationCard));
        Util.setTamilText((TextView) findViewById(R.id.number_aadhar), R.string.headAadharNo);
        Util.setTamilText((TextView) findViewById(R.id.number_mobile), R.string.mobile_no);
        Util.setTamilText((TextView) findViewById(R.id.registration_text), R.string.check_details);
        Util.setTamilText((TextView) findViewById(R.id.ration_card_no), R.string.ration_card_number);
        Util.setTamilText((TextView) findViewById(R.id.noOfCylinderTitle), R.string.aRegisterNo);
        Util.setTamilText((TextView) findViewById(R.id.card_type), R.string.cylinderHints);
        Util.setTamilText((TextView) findViewById(R.id.a_reg_number), R.string.cardCap);
        Util.setTamilText((TextView) findViewById(R.id.number_adults), R.string.number_adult_cap);
        Util.setTamilText((TextView) findViewById(R.id.number_child), R.string.number_child_cap);
        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.LoggingQueue(UpdateUserDetailsActivity.this, "RegistrationConfirmActivity", "Cancel called ... Moving to card activation");
                Intent intent = new Intent(UpdateUserDetailsActivity.this, RationCardUpdateActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.button_Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValue();
            }
        });


        findViewById(R.id.benef_members_aadhar_reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ufcCode = benef.getEncryptedUfc();
                Intent intent = new Intent(UpdateUserDetailsActivity.this, MembersAadharRegistrationActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putString("UfcCode", ufcCode);
                bundle.putString("Beneficiary", String.valueOf(new Gson().toJson(benef)));
                bundle.putString("BeneficiaryId", String.valueOf(benef.getId()));
                int totalMembers = benef.getNumOfAdults() + benef.getNumOfChild();
                bundle.putString("TotalMembers", String.valueOf(totalMembers));
                if(StringUtils.isNotEmpty(benef.getFamilyHeadAadharNumber())) {
                    bundle.putString("FamilyHeadAadhar", String.valueOf(benef.getFamilyHeadAadharNumber()));
                }
                else {
                    bundle.putString("FamilyHeadAadhar", "0");
                }
                bundle.putString("QrCode", getIntent().getStringExtra("qrCode"));
                bundle.putString("UpdateString", getIntent().getStringExtra("beneUpdate"));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.scanAadhar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner();
            }
        });
    }

    /**
     * Calling QR Scanner
     */
    private void launchQRScanner() {
        Util.LoggingQueue(this, "QRcode sales", "QR scanner called");
        String packageString = getApplicationContext().getPackageName();
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(packageString);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
        /*if (aadharNumber3.getText().toString().equalsIgnoreCase("")) {
            ((FrameLayout) findViewById(R.id.cameraPreview)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.userLayout)).setVisibility(View.INVISIBLE);
            autoFocusHandler = new Handler();
            mCamera = getCameraInstance();
            *//* Instance barcode scanner *//*
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);
            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
            preview.addView(mPreview);
        }*/
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
                        aadharSeedingDto.setScannedQRData(contents);
                        Log.e("EncryptedUFC", contents);

                        if(contents.contains("<PrintLetterBarcodeData")) {
                            String resultString = null;
                            StringBuilder sb = new StringBuilder(contents);
                            if ((sb.charAt(1) == '/')) {
                                sb.deleteCharAt(1);
                                resultString = sb.toString();
                            } else {
                                resultString = contents;
                            }
                            xmlParsing(resultString);
                        }
                        else {
                            stringParsing(contents);
                        }


                        beneUpdate.setAadhaarSeedingDto(aadharSeedingDto);
                        Log.e("aadharNum........ ", aadharSeedingDto.toString());
                        String aadharNum = aadharSeedingDto.getUid();

                        aadharNumber1.setText(aadharNum.substring(0,4));
                        aadharNumber2.setText(aadharNum.substring(4,8));
                        aadharNumber3.setText(aadharNum.substring(8,12));
                        Log.e("aadhaarSeedingDto ",aadharSeedingDto.toString());
//                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(this, "QRcode sales", "QR exception called:" + e.toString());
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(QRCodeSalesActivity.class.getSimpleName(),"Scan cancelled");
                }
                break;

            default:
                break;
        }
    }






    private void stringParsing(String text) {

        String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                state = "", pc= "", dob = "";
        String[] strArr = text.split(",");
        for(int i=0;i<strArr.length;i++) {
            try {
                Log.e("mara", "strArr contents" + strArr[i].toString());
                String element = strArr[i].toString();
                String[] strArr2 = element.split(":");

                if(strArr2[0].equalsIgnoreCase(" aadhaar no")) {
                    uid = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" Name")) {
                    name = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" Gender")) {
                    gender = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" YOB")) {
                    yob = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" co")) {
                    co = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" house")) {
                    house = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" street")) {
                    street = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" lmark")) {
                    lm = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" loc")) {
                    loc = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" vtc")) {
                    vtc = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" po")) {
                    po = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" dist")) {
                    dist = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" subdist")) {
                    subdist = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" state")) {
                    state = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" pc")) {
                    pc = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase(" DOB")) {
                    dob = strArr2[1];
                }

                if(strArr2[0].equalsIgnoreCase("aadhaar no")) {
                    uid = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("Name")) {
                    name = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("Gender")) {
                    gender = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("YOB")) {
                    yob = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("co")) {
                    co = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("house")) {
                    house = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("street")) {
                    street = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("lmark")) {
                    lm = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("loc")) {
                    loc = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("vtc")) {
                    vtc = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("po")) {
                    po = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("dist")) {
                    dist = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("subdist")) {
                    subdist = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("state")) {
                    state = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("pc")) {
                    pc = strArr2[1];
                }
                if(strArr2[0].equalsIgnoreCase("DOB")) {
                    dob = strArr2[1];
                }
            }
            catch(Exception e) {
                Log.e("mara","string exception"+e);
            }
        }

        if(!uid.equalsIgnoreCase("")) {
            aadharSeedingDto.setAadhaarNum(Long.parseLong(uid));
            aadharSeedingDto.setUid(uid);
        }
        aadharSeedingDto.setName(name);
        aadharSeedingDto.setGender(gender.charAt(0));
        aadharSeedingDto.setCo(co);
        aadharSeedingDto.setHouse(house);
        aadharSeedingDto.setStreet(street);
        aadharSeedingDto.setLm(lm);
        aadharSeedingDto.setLoc(loc);
        aadharSeedingDto.setVtc(vtc);
        aadharSeedingDto.setPo(po);
        aadharSeedingDto.setDist(dist);
        aadharSeedingDto.setSubdist(subdist);
        aadharSeedingDto.setState(state);
        aadharSeedingDto.setPc(pc);
        aadharSeedingDto.setDateOfBirth(dob);
        if(!yob.equalsIgnoreCase("")) {
            aadharSeedingDto.setYob(Long.parseLong(yob));
        }
     /*   try {
            if (!dob.equalsIgnoreCase("")) {
                SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = sourceFormat.parse(dob);
                SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String dateStr = targetFormat.format(date1);
                Date date2 = targetFormat.parse(dateStr);
                aadharSeedingDto.setDob(date2.getTime());
            }
        }
        catch(Exception e) {
            try {
                if (!dob.equalsIgnoreCase("")) {
                    SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = sourceFormat.parse(dob);
                    SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    String dateStr = targetFormat.format(date1);
                    Date date2 = targetFormat.parse(dateStr);
                    aadharSeedingDto.setDob(date2.getTime());
                }
            }
            catch(Exception e2) {}
        }*/

        Log.e("UIDValue", uid);
        Log.e("name",name);
        Log.e("gender",gender);
        Log.e("yob", yob);
        Log.e("co", co);
        Log.e("house", house);
        Log.e("street", street);
        Log.e("lm", lm);
        Log.e("loc", loc);
        Log.e("vtc", vtc);
        Log.e("po", po);
        Log.e("dist", dist);
        Log.e("subdist", subdist);
        Log.e("state", state);
        Log.e("pc", pc);
        Log.e("dob", dob);


        try {
            /** 11-07-2016
             * MSFixes
             * Added to fix aadhar card date of birth error
             *
             */


            if (dob != null && !dob.isEmpty()) {

                if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                    // Pattern dd/MM/yyyy
                    DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "stringParsing() called -> dob dd/MM/yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());
                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                    // Pattern dd-MM-yyyy
                    DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "stringParsing() called -> dob dd-MM-yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                    // Pattern yyyy/MM/dd
                    DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "stringParsing() called -> dob dd MM yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "stringParsing() called -> dob yyyy/MM/dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "stringParsing() called -> dob yyyy-MM-dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "stringParsing() called -> dob yyyy MM dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else {

                    Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "stringParsing() called -> dob Unknown Pattern ->" + dob
                    );
                }
            }else{
                aadharSeedingDto.setDob(null);
            }

            /** 11-07-2016
             * MSFixes
             * Added to fix created_date error
             *
             */
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String date = sdf.format(gc.getTime());
            Date createdDate = sdf.parse(date);
            aadharSeedingDto.setCreatedDate(createdDate.getTime());


        } catch (Exception e) {

        }

    }

    private void xmlParsing(String xmlData) {
        try
        {
            String xmlRecords = xmlData.replaceAll("&", "&amp;").replaceAll("'", "&apos;").replace("?\"", "\"?");
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            Log.e("update user","xmlRecords..."+xmlRecords+"...");
            is.setCharacterStream(new StringReader(xmlRecords));
            Document dom = db.parse(is);
            NodeList l = dom.getElementsByTagName("PrintLetterBarcodeData");
            for (int j=0; j<l.getLength(); ++j) {
                Node prop = l.item(j);
                NamedNodeMap attr = prop.getAttributes();
                if (null != attr) {
                    //Node nodeUid = attr.getNamedItem("uid");
                    String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                           state = "", pc= "", dob = "";

                    try { uid = attr.getNamedItem("uid").getNodeValue(); } catch(Exception e) {}

                    try { name= attr.getNamedItem("name").getNodeValue(); } catch(Exception e) {}

                    try { gender= attr.getNamedItem("gender").getNodeValue(); } catch(Exception e) {}

                    try { yob = attr.getNamedItem("yob").getNodeValue(); } catch(Exception e) {}

                    try { co = attr.getNamedItem("co").getNodeValue(); } catch(Exception e) {}

                    try { house= attr.getNamedItem("house").getNodeValue(); } catch(Exception e) {}

                    try { street= attr.getNamedItem("street").getNodeValue(); } catch(Exception e) {}

                    try { lm= attr.getNamedItem("lm").getNodeValue(); } catch(Exception e) {}

                    try { loc= attr.getNamedItem("loc").getNodeValue(); } catch(Exception e) {}

                    try { vtc = attr.getNamedItem("vtc").getNodeValue(); } catch(Exception e) {}

                    try { po= attr.getNamedItem("po").getNodeValue(); } catch(Exception e) {}

                    try { dist= attr.getNamedItem("dist").getNodeValue(); } catch(Exception e) {}

                    try { subdist= attr.getNamedItem("subdist").getNodeValue(); } catch(Exception e) {}

                    try { state = attr.getNamedItem("state").getNodeValue(); } catch(Exception e) {}

                    try { pc = attr.getNamedItem("pc").getNodeValue(); } catch(Exception e) {}

                    try { dob = attr.getNamedItem("dob").getNodeValue(); } catch(Exception e) {}


//                    aadhaarSeedingDto.setRationCardNumber(rationcardNo);
                    if(!uid.equalsIgnoreCase("")) {
                        aadharSeedingDto.setAadhaarNum(Long.parseLong(uid));
                        aadharSeedingDto.setUid(uid);
                    }
                    aadharSeedingDto.setName(name);
                    aadharSeedingDto.setGender(gender.charAt(0));
                    aadharSeedingDto.setCo(co);
                    aadharSeedingDto.setHouse(house);
                    aadharSeedingDto.setStreet(street);
                    aadharSeedingDto.setLm(lm);
                    aadharSeedingDto.setLoc(loc);
                    aadharSeedingDto.setVtc(vtc);
                    aadharSeedingDto.setPo(po);
                    aadharSeedingDto.setDist(dist);
                    aadharSeedingDto.setSubdist(subdist);
                    aadharSeedingDto.setState(state);
                    aadharSeedingDto.setPc(pc);
                    aadharSeedingDto.setDateOfBirth(dob);
                    if(!yob.equalsIgnoreCase("")) {
                        aadharSeedingDto.setYob(Long.parseLong(yob));
                    }

                    /*try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            aadharSeedingDto.setDob(date2.getTime());
                        }
                    }
                    catch(Exception e) {}

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy/MM/dd");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            aadharSeedingDto.setDob(date2.getTime());
                        }
                    }
                    catch(Exception e) {}*/

                    /*try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            aadharSeedingDto.setDob(date2.getTime());
                        }
                    }
                    catch(Exception e2) {}

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            aadharSeedingDto.setDob(date2.getTime());
                        }
                    }
                    catch(Exception e2) {}*/

                    Log.e("UIDValue", uid);
                    Log.e("name",name);
                    Log.e("gender",gender);
                    Log.e("yob",yob);
                    Log.e("co",co);
                    Log.e("house",house);
                    Log.e("street",street);
                    Log.e("lm",lm);
                    Log.e("loc",loc);
                    Log.e("vtc",vtc);
                    Log.e("po",po);
                    Log.e("dist",dist);
                    Log.e("subdist",subdist);
                    Log.e("state",state);
                    Log.e("pc",pc);
                    Log.e("dob",dob);

                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("UIDValue : ").append(uid).append("\n");
                    stringBuffer.append("Name     : ").append(name).append("\n");
                    stringBuffer.append("Gender   : ").append(gender).append("\n");
                    stringBuffer.append("Yob      : ").append(yob).append("\n");
                    stringBuffer.append("Co       : ").append(co).append("\n");
                    stringBuffer.append("House    : ").append(house).append("\n");
                    stringBuffer.append("Street   : ").append(street).append("\n");
                    stringBuffer.append("Lm       : ").append(lm).append("\n");
                    stringBuffer.append("LOC      : ").append(loc).append("\n");
                    stringBuffer.append("VTC      : ").append(vtc).append("\n");
                    stringBuffer.append("PO       : ").append(po).append("\n");
                    stringBuffer.append("Dist     : ").append(dist).append("\n");
                    stringBuffer.append("SubDist  : ").append(subdist).append("\n");
                    stringBuffer.append("State    : ").append(state).append("\n");
                    stringBuffer.append("Pc       : ").append(pc).append("\n");
                    stringBuffer.append("Dob       : ").append(dob).append("\n");

                    Log.e("Xml Records",xmlRecords);
                    Log.e(">>>>>>>>>>",">>>>>>>>>");
                    Log.e("Details",stringBuffer.toString());


                    try {
                        /** 11-07-2016
                         * MSFixes
                         * Added to fix aadhar card date of birth error
                         *
                         */
                        if (dob != null && !dob.isEmpty()) {

                            if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                                // Pattern dd/MM/yyyy
                                DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "xmlParsing() called -> dob dd/MM/yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());
                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                                // Pattern dd-MM-yyyy
                                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "xmlParsing() called -> dob dd-MM-yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                                // Pattern yyyy/MM/dd
                                DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "xmlParsing() called -> dob dd MM yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "xmlParsing() called -> dob yyyy/MM/dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "xmlParsing() called -> dob yyyy-MM-dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "xmlParsing() called -> dob yyyy MM dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else {

                                Util.LoggingQueue(UpdateUserDetailsActivity.this, "UpdateUserDetailsActivity ", "xmlParsing() called -> dob Unknown Pattern ->" + dob
                                );
                            }
                        }else{
                            aadharSeedingDto.setDob(null);
                        }

                        /** 11-07-2016
                         * MSFixes
                         * Added to fix created_date error
                         *
                         */
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        GregorianCalendar gc = new GregorianCalendar();
                        String date = sdf.format(gc.getTime());
                        Date createdDate = sdf.parse(date);
                        aadharSeedingDto.setCreatedDate(createdDate.getTime());


                    } catch (Exception e) {

                    }

                }
            }
        }
        catch (Exception e)
        {
            Log.e("QRCode scan", "exc..", e);
        }
    }

    private void checkValue() {

        if (benef.getAregisterNum() != null && benef.getAregisterNum().length() > 0 && benef.getMobileNumber() != null && benef.getMobileNumber().length() > 0 && benef.getFamilyHeadAadharNumber() != null && benef.getFamilyHeadAadharNumber().length() == 12) {
            /*Log.e("Already Filled", "Datas Filled");
            Toast.makeText(getBaseContext(), "already have mobile & aadhar.........", Toast.LENGTH_SHORT).show();*/
            /*try {
                Intent intent = new Intent(this, UpdateConfirmUserDetailsActivity.class);
                intent.putExtra("benef", new Gson().toJson(benef));
                intent.putExtra("beneUpdate", new Gson().toJson(beneUpdate));
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("Error", e.toString(), e);
            }*/
            noChange();
        } else {
            if (mobileNumber.getText().toString().trim().length() == 0) {
                Log.i("Already Filled", "Mobile Number not filled");
                beneUpdate.setMobileNumber(null);
//                beneUpdate.setMobileNumber(null);
            } else if (mobileNumber.getText().toString().trim().length() <= 9) {
                Util.messageBar(this, getString(R.string.invalidMobile));
                return;
            } else if (String.valueOf(mobileNumber.getText().toString().trim().charAt(0)).equalsIgnoreCase("0")) {
                Util.messageBar(this, getString(R.string.mobileNumberZero));
                return;
            } else {
                if (benef.getMobileNumber() == null) {
//                  beneUpdate.setMobileNumber(null);
                    beneUpdate.setMobileNumber(mobileNumber.getText().toString().trim());
                }
                else {
                    beneUpdate.setMobileNumber(null);
                }
            }

            if (aRegNumber.getText().toString().trim().length() == 0) {
                beneUpdate.setAregisterNumber(null);
//                beneUpdate.setMobileNumber(null);
            } else {
                if ( StringUtils.isEmpty(benef.getAregisterNum()) || (benef.getAregisterNum() == null) || (benef.getAregisterNum().equalsIgnoreCase("-1"))) {
//                  beneUpdate.setMobileNumber(null);
                    beneUpdate.setAregisterNumber(aRegNumber.getText().toString().trim());
                }
                else {
                    beneUpdate.setAregisterNumber(null);
                }

            }

            String cardNumber1 = aadharNumber1.getText().toString();
            String cardNumber2 = aadharNumber2.getText().toString();
            String cardNumber3 = aadharNumber3.getText().toString();
            if (StringUtils.isEmpty(cardNumber1) && StringUtils.isEmpty(cardNumber2) && StringUtils.isEmpty(cardNumber3)) {
                Util.LoggingQueue(this, "Ration Card Registration", "Aadhar number length Zero");
//                beneUpdate.setFamilyHeadAadharNumber(null);
                aadharSeedingDto.setAadhaarNum(0l);
                aadharSeedingDto.setUid(null);
                beneUpdate.setAadhaarSeedingDto(aadharSeedingDto);

               /* beneUpdate.getAadhaarSeedingDto().setAadhaarNum(0l);
                beneUpdate.getAadhaarSeedingDto().setUid(null);*/
            } else {
                if (cardNumber1.length() != 4 || cardNumber2.length() != 4 || cardNumber3.length() != 4) {
                    Util.messageBar(this, getString(R.string.invalidAadharNo));
                    Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                    return;
                }
                if (String.valueOf(cardNumber1.charAt(0)).equalsIgnoreCase("0")) {
                    Util.messageBar(this, getString(R.string.aadharNumberZero));
                    return;
                }
                if (String.valueOf(cardNumber1.charAt(0)).equalsIgnoreCase("1")) {
                    Util.messageBar(this, getString(R.string.aadharNumberOne));
                    return;
                }
                String cardNumber = cardNumber1 + cardNumber2 + cardNumber3;

                Log.e("cardNumber.........",""+cardNumber);

                try {
                    if (benef.getFamilyHeadAadharNumber() != null && StringUtils.isNotEmpty(benef.getFamilyHeadAadharNumber())) {

                    } else {
                        AadhaarVerhoeffAlgorithm aadhaarVerhoeffAlgorithm = new AadhaarVerhoeffAlgorithm();
                        Boolean isAadharNumber = aadhaarVerhoeffAlgorithm.validateVerhoeff(cardNumber);
                        if(!isAadharNumber){
                            Util.messageBar(this, getString(R.string.checksumValidationFail));
                            return;
                        }
                    }
                }
                catch(Exception e) {
                    AadhaarVerhoeffAlgorithm aadhaarVerhoeffAlgorithm = new AadhaarVerhoeffAlgorithm();
                    Boolean isAadharNumber = aadhaarVerhoeffAlgorithm.validateVerhoeff(cardNumber);
                    if(!isAadharNumber){
                        Util.messageBar(this, getString(R.string.checksumValidationFail));
                        return;
                    }
                }



                if (benef.getFamilyHeadAadharNumber() == null) {
//                    beneUpdate.setFamilyHeadAadharNumber(cardNumber);
                    try {
                        aadharSeedingDto.setAadhaarNum(Long.valueOf(cardNumber));
                    }
                    catch(Exception e) {}
                    aadharSeedingDto.setUid(cardNumber);
                    aadharSeedingDto.setBeneficiaryID(benef.getId());
                    beneUpdate.setAadhaarSeedingDto(aadharSeedingDto);



                    /*beneUpdate.getAadhaarSeedingDto().setAadhaarNum(Long.valueOf(cardNumber));
                    beneUpdate.getAadhaarSeedingDto().setUid(cardNumber);*/
                } else {
//                    beneUpdate.setFamilyHeadAadharNumber(null);
                    aadharSeedingDto.setAadhaarNum(0l);
                    aadharSeedingDto.setUid(null);
                    beneUpdate.setAadhaarSeedingDto(aadharSeedingDto);

                    /*beneUpdate.getAadhaarSeedingDto().setAadhaarNum(0l);
                    beneUpdate.getAadhaarSeedingDto().setUid(null);*/
                }


            }
            if ((beneUpdate.getAregisterNumber() == null || beneUpdate.getAregisterNumber().equalsIgnoreCase("-1")) && beneUpdate.getMobileNumber() == null && beneUpdate.getAadhaarSeedingDto().getUid() == null && (benef.getAregisterNum() == null || benef.getAregisterNum().equalsIgnoreCase("-1")) && benef.getMobileNumber() == null && benef.getFamilyHeadAadharNumber() == null) {
                Util.messageBar(this, getString(R.string.emptyaadharMobile));
            } else {
                try {
                    Intent intent = new Intent(this, UpdateConfirmUserDetailsActivity.class);
                    intent.putExtra("benef", new Gson().toJson(benef));
                    intent.putExtra("beneUpdate", new Gson().toJson(beneUpdate));
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e("Error", e.toString(), e);
                }
            }
        }
    }

    private void noChange() {
        Intent intent = new Intent(this,SuccessFailureUpdationActivity.class);
        try {
//                Util.messageBar(this, getString(R.string.updateionSuccess));
//                FPSDBHelper.getInstance(this).beneficiaryUpdate(beneUpdate);
            intent.putExtra("error","1");
        } catch (Exception e) {
            intent.putExtra("error",String.valueOf(4000));
            Log.e("Error", e.toString(), e);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (v.getId() == R.id.cylinder_value) {
            aRegNumber.requestFocus();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.AREG;
        }
        else if (v.getId() == R.id.mobileNumberUpdationText) {
            mobileNumber.requestFocus();
            checkVisibility();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.MOBILE;
        } /*else if (v.getId() == R.id.aadharNumberFirst) {
            aadharNumber1.requestFocus();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.aadharNumberMiddle) {
            aadharNumber2.requestFocus();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.aadharNumberFinal) {
            aadharNumber3.requestFocus();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.FINALSTRING;
        }*/
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (v.getId() == R.id.cylinder_value && hasFocus) {
            aRegNumber.requestFocus();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.AREG;
        }
        else if (v.getId() == R.id.mobileNumberUpdationText && hasFocus) {
            mobileNumber.requestFocus();
            checkVisibility();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.MOBILE;
        } /*else if (v.getId() == R.id.aadharNumberFirst && hasFocus) {
            aadharNumber1.requestFocus();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.aadharNumberMiddle && hasFocus) {
            aadharNumber2.requestFocus();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.aadharNumberFinal && hasFocus) {
            aadharNumber3.requestFocus();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.FINALSTRING;
        }*/
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }

    private void updationSucess() {
        if (beneUpdate.getMobileNumber() != null && beneUpdate.getMobileNumber().length() > 0) {
            mobileNumber.setOnFocusChangeListener(null);
            mobileNumber.setOnClickListener(null);
            mobileNumber.setClickable(false);
            mobileNumber.setFocusable(false);
            benef.setMobileNumber(beneUpdate.getMobileNumber());
        }
        if (beneUpdate.getAadhaarSeedingDto().getUid() != null && beneUpdate.getAadhaarSeedingDto().getUid().length() == 12) {
            /*aadharNumber1.setOnFocusChangeListener(null);
            aadharNumber1.setOnClickListener(null);
            aadharNumber2.setOnFocusChangeListener(null);
            aadharNumber2.setOnClickListener(null);
            aadharNumber3.setOnFocusChangeListener(null);
            aadharNumber3.setOnClickListener(null);
            aadharNumber1.setClickable(false);
            aadharNumber1.setFocusable(false);
            aadharNumber1.setAutoFocus(false);
            aadharNumber2.setClickable(false);
            aadharNumber2.setFocusable(false);
            aadharNumber2.setAutoFocus(false);
            aadharNumber3.setClickable(false);
            aadharNumber3.setFocusable(false);
            aadharNumber3.setAutoFocus(false);*/
            benef.setFamilyHeadAadharNumber(beneUpdate.getAadhaarSeedingDto().getUid());
        }
    }

    @Override
    public void onBackPressed() {
        /*if(((FrameLayout) findViewById(R.id.cameraPreview)).getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(this,UpdateUserDetailsActivity.class);
            intent.putExtra("qrCode",qrCode);
            startActivity(intent);
            finish();
        }
        else {*/
            startActivity(new Intent(this, RationCardUpdateActivity.class));
            finish();
//        }
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
                if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    String text = mobileNumber.getText().toString();
                    if (mobileNumber.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        mobileNumber.setText(text);
                        mobileNumber.setSelection(text.length());
                    }
                } else if (keyBoardFocused == KeyBoardEnum.AREG) {
                    String text = aRegNumber.getText().toString();
                    if (aRegNumber.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aRegNumber.setText(text);
                        aRegNumber.setSelection(text.length());
                    }
                } /*else if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    String text = aadharNumber1.getText().toString();
                    if (aadharNumber1.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber1.setText(text);
//                        aadharNumber1.setSelection(text.length());
                    } else {
                        if (benef.getMobileNumber() == null)
                            mobileNumber.requestFocus();
                    }
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    String text = aadharNumber2.getText().toString();
                    if (aadharNumber2.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber2.setText(text);
//                        aadharNumber2.setSelection(text.length());
                    } else {
                        aadharNumber1.requestFocus();
                    }
                } else if (keyBoardFocused == KeyBoardEnum.FINALSTRING) {
                    String text = aadharNumber3.getText().toString();
                    if (aadharNumber3.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber3.setText(text);
//                        aadharNumber3.setSelection(text.length());
                    } else {
                        aadharNumber2.requestFocus();
                    }
                }*/
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.PREFIX) {
//                    aadharNumber1.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
//                    aadharNumber2.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.FINALSTRING) {
//                    aadharNumber3.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    mobileNumber.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.AREG) {
                    aRegNumber.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    private void listenersForEditText() {

        aRegNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aRegNumber.getText().toString().length() == 5) {
//                    keyBoardCustom.setVisibility(View.GONE);
                    if (benef.getMobileNumber() == null) {
                        keyBoardFocused = KeyBoardEnum.MOBILE;
                        mobileNumber.requestFocus();

                    }
                    else if(benef.getFamilyHeadAadharNumber() == null) {
                        keyBoardFocused = KeyBoardEnum.PREFIX;
                        aadharNumber1.requestFocus();

                    }
                    else {
                        keyBoardCustom.setVisibility(View.GONE);
                    }
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
                if (mobileNumber.getText().toString().length() == 10) {
                    if(benef.getFamilyHeadAadharNumber() == null) {
                        aadharNumber1.requestFocus();
                        keyBoardFocused = KeyBoardEnum.PREFIX;
                    }
                    else {
                        keyBoardCustom.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        /*aadharNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber1.getText().toString().length() == 4)     //size as per your requirement
                {
                    aadharNumber2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        aadharNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber2.getText().toString().length() == 4) {
                    aadharNumber3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        aadharNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber3.getText().toString().length() == 4) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
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

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
    }


    /*public void onPause() {
        super.onPause();
        releaseCamera();
    }

    *//** A safe way to get an instance of the Camera object. *//*
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                String contents = "";
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
//                    scanText.setText("barcode result " + sym.getData());
                    contents = sym.getData();
                    barcodeScanned = true;
                }
                ((FrameLayout) findViewById(R.id.cameraPreview)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.userLayout)).setVisibility(View.VISIBLE);
                if(!contents.equalsIgnoreCase("")) {
                    try {
                        Log.e("EncryptedUFC", contents);
                        xmlParsing(contents);
                        beneUpdate.setAadhaarSeedingDto(aadharSeedingDto);
                        Log.e("aadharNum........ ", aadharSeedingDto.toString());
                        String aadharNum = aadharSeedingDto.getUid();

                        aadharNumber1.setText(aadharNum.substring(0,4));
                        aadharNumber2.setText(aadharNum.substring(4,8));
                        aadharNumber3.setText(aadharNum.substring(8,12));
                        Log.e("aadhaarSeedingDto ",aadharSeedingDto.toString());
//                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(UpdateUserDetailsActivity.this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(UpdateUserDetailsActivity.this, "QRcode sales", "QR exception called:" + e.toString());
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };*/


}
