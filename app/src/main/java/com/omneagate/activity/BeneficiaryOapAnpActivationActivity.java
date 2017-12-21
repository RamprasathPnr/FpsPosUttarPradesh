package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.neopixl.pixlui.components.edittext.EditText;
import com.omneagate.DTO.AadharSeedingDto;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.AadhaarVerhoeffAlgorithm;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BeneficiaryOapAnpActivationActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    //Progressbar for waiting
    CustomProgressDialog progressBar;

    //HttpConnection service
    HttpClientWrapper httpConnection;

    EditText suffixCard, registeredMobile, aRegisterNo;

    TextView firstAadhar, secondAadhar, thirdAadhar;

    TextView prefixCard, cardTypeCard;

    BenefActivNewDto benefActivNewDto;

    Button submitButton;

    RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;

    KeyboardView keyview, keyboardViewAlpha;

    KeyBoardEnum keyBoardFocused;

    AadharSeedingDto aadharSeedingDto;


    // Zbar variables
    /*private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;*/

    int cardTypeSpinnerPosition;
    String message;
    boolean aadhaarSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.bene_oap_anp_activation);

        Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity", "onCreate() called ");


        httpConnection = new HttpClientWrapper();
        if (getIntent().getStringExtra("data") != null) {
            message = getIntent().getStringExtra("data");
            benefActivNewDto = new Gson().fromJson(message, BenefActivNewDto.class);


          //  Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity", "onCreate() called message = "+message);
            Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity", "onCreate() called benefActivNewDto = "+benefActivNewDto);


        }
        setUpCardPage();

    }


    private void setUpCardPage() {
        aadharSeedingDto = new AadharSeedingDto();
        setUpPopUpPage();
       // Util.LoggingQueue(this, "Oap/Anp Card Registration", "Setting up main page");
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

        keyBoardCustom.setVisibility(View.GONE);

        submitButton = (Button) findViewById(R.id.submit_button);
        prefixCard = (TextView) findViewById(R.id.firstText);
        cardTypeCard = (TextView) findViewById(R.id.secondText);
        suffixCard = (EditText) findViewById(R.id.thirdText);
        firstAadhar = (TextView) findViewById(R.id.firstAadharText);
        secondAadhar = (TextView) findViewById(R.id.secondAadharText);
        thirdAadhar = (TextView) findViewById(R.id.thirdAadharText);
        aRegisterNo = (EditText) findViewById(R.id.aRegisterNoEt);
        registeredMobile = (EditText) findViewById(R.id.mobileNumberActivationEt);


        /*try {
            String prefixCardStr = getIntent().getStringExtra("prefixCard");
            String cardTypeCardStr = getIntent().getStringExtra("cardTypeCard");
            String suffixCardStr = getIntent().getStringExtra("suffixCard");
            String aRegisterNoStr = getIntent().getStringExtra("aRegisterNo");
            String registeredMobileStr = getIntent().getStringExtra("registeredMobile");
            prefixCard.setText(prefixCardStr);
            cardTypeCard.setText(cardTypeCardStr);
            suffixCard.setText(suffixCardStr);
            aRegisterNo.setText(aRegisterNoStr);
            registeredMobile.setText(registeredMobileStr);
        }
        catch(Exception e) {}*/


        if (benefActivNewDto != null) {
            String cardNumber = benefActivNewDto.getRationCardNumber();
            prefixCard.setText(StringUtils.substring(cardNumber, 0, 2));
            cardTypeCard.setText(StringUtils.substring(cardNumber, 2, 3));
            suffixCard.setText(StringUtils.substring(cardNumber, 3));

            String regNo = benefActivNewDto.getAregisterNum();
            regNo = regNo.substring(2);

            aRegisterNo.setText(regNo);
            if (benefActivNewDto.getMobileNum() != null)
                registeredMobile.setText(benefActivNewDto.getMobileNum());
            if (benefActivNewDto.getAadhaarSeedingDto() != null) {
                if (benefActivNewDto.getAadhaarSeedingDto().getUid() != null) {
                    String aadharNumber = benefActivNewDto.getAadhaarSeedingDto().getUid();
                    firstAadhar.setText(StringUtils.substring(aadharNumber, 0, 4));
                    secondAadhar.setText(StringUtils.substring(aadharNumber, 4, 8));
                    thirdAadhar.setText(StringUtils.substring(aadharNumber, 8));
                }
            }

        }else{
            benefActivNewDto = new BenefActivNewDto();
        }

        /*keyBoardFocused = KeyBoardEnum.SUFFIX;
        suffixCard.requestFocus();*/
//        changeLayout(true);

        String uidMandatory = FPSDBHelper.getInstance(this).getMasterData("UID_MANDATORY");
        if((uidMandatory != null) && (StringUtils.isNotEmpty(uidMandatory.trim())) && (!uidMandatory.equalsIgnoreCase("null"))) {
            if(uidMandatory.equalsIgnoreCase("true")) {
                aadhaarSkip = true;
            }
            else if(uidMandatory.equalsIgnoreCase("false")) {
                aadhaarSkip = false;
            }
        }
        else {
            aadhaarSkip = false;
        }


        prefixCard.setOnClickListener(this);
        prefixCard.setShowSoftInputOnFocus(false);
        prefixCard.setOnFocusChangeListener(this);

        cardTypeCard.setOnClickListener(this);
        cardTypeCard.setShowSoftInputOnFocus(false);
        cardTypeCard.setOnFocusChangeListener(this);

        suffixCard.setOnClickListener(this);
        suffixCard.setShowSoftInputOnFocus(false);
        suffixCard.setOnFocusChangeListener(this);

        aRegisterNo.setOnClickListener(this);
        aRegisterNo.setShowSoftInputOnFocus(false);
        aRegisterNo.setOnFocusChangeListener(this);

        registeredMobile.setOnClickListener(this);
        registeredMobile.setShowSoftInputOnFocus(false);
        registeredMobile.setOnFocusChangeListener(this);

        /*firstAadhar.setOnClickListener(this);
        firstAadhar.setShowSoftInputOnFocus(false);
        firstAadhar.setOnFocusChangeListener(this);

        secondAadhar.setOnClickListener(this);
        secondAadhar.setShowSoftInputOnFocus(false);
        secondAadhar.setOnFocusChangeListener(this);

        thirdAadhar.setOnClickListener(this);
        thirdAadhar.setShowSoftInputOnFocus(false);
        thirdAadhar.setOnFocusChangeListener(this);*/

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this, CardActivationActivity.class));
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

        findViewById(R.id.scanAadhar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner();
            }
        });

    }

    // Calling QR Scanner
    private void launchQRScanner() {
        Util.LoggingQueue(this, "QRcode sales", "QR scanner called");
        String packageString = getApplicationContext().getPackageName();
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(packageString);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
        /*if(thirdAadhar.getText().toString().equalsIgnoreCase("")) {
            ((FrameLayout) findViewById(R.id.cameraPreview)).setVisibility(View.VISIBLE);
            ((LinearLayout) findViewById(R.id.userLayout)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
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

                        benefActivNewDto.setAadhaarSeedingDto(aadharSeedingDto);
                        Log.e("aadharNum........ ", aadharSeedingDto.toString());
                        String aadharNum = aadharSeedingDto.getUid();
                        firstAadhar.setText(aadharNum.substring(0,4));
                        secondAadhar.setText(aadharNum.substring(4,8));
                        thirdAadhar.setText(aadharNum.substring(8,12));
//                        Log.e("aadhaarSeedingDto ",aadhaarSeedingDto.toString());
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
        }
*/



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

            if (dob != null && !dob.isEmpty()) {

                if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                    // Pattern dd/MM/yyyy
                    DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "stringParsing() called -> dob dd/MM/yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());
                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                    // Pattern dd-MM-yyyy
                    DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "stringParsing() called -> dob dd-MM-yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                    // Pattern yyyy/MM/dd
                    DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "stringParsing() called -> dob dd MM yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "stringParsing() called -> dob yyyy/MM/dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "stringParsing() called -> dob yyyy-MM-dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "stringParsing() called -> dob yyyy MM dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else {

                    Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "stringParsing() called -> dob Unknown Pattern ->" + dob
                    );
                }
            }else{
                aadharSeedingDto.setDob(null);
            }


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
                    catch(Exception e) {}

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

                        if (dob != null && !dob.isEmpty()) {

                            if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                                // Pattern dd/MM/yyyy
                                DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "xmlParsing() called -> dob dd/MM/yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());
                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                                // Pattern dd-MM-yyyy
                                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "xmlParsing() called -> dob dd-MM-yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                                // Pattern yyyy/MM/dd
                                DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "xmlParsing() called -> dob dd MM yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "xmlParsing() called -> dob yyyy/MM/dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "xmlParsing() called -> dob yyyy-MM-dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "xmlParsing() called -> dob yyyy MM dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else {

                                Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity ", "xmlParsing() called -> dob Unknown Pattern ->" + dob
                                );
                            }
                        }else{
                            aadharSeedingDto.setDob(null);
                        }


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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.thirdText) {
            setARegnoPadding();
            checkVisibility();
            suffixCard.requestFocus();
            keyBoardAppear();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.aRegisterNoEt) {
                setSuffixPadding();
                checkVisibility();
                keyBoardAppear();
                aRegisterNo.requestFocus();
                keyBoardFocused = KeyBoardEnum.AREGISTER;
                changeLayout(false);
        }/* else if (v.getId() == R.id.secondText) {
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
        }*/ else if (v.getId() == R.id.mobileNumberActivationEt) {
            setARegnoPadding();
            setSuffixPadding();
            checkVisibility();
            registeredMobile.requestFocus();
            keyBoardAppear();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.MOBILE;
        }
        /*else if (v.getId() == R.id.firstAadharText) {
            setARegnoPadding();
            setSuffixPadding();
            checkVisibility();
            firstAadhar.requestFocus();
            keyBoardAppear();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.AADHARPREFIX;
        } else if (v.getId() == R.id.secondAadharText) {
            setARegnoPadding();
            setSuffixPadding();
            checkVisibility();
            secondAadhar.requestFocus();
            keyBoardAppear();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.AADHARMIDDLE;
        } else if (v.getId() == R.id.thirdAadharText) {
            setARegnoPadding();
            setSuffixPadding();
            checkVisibility();
            thirdAadhar.requestFocus();
            keyBoardAppear();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.AADDHARSUFFIX;
        }*/
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
            setARegnoPadding();
            setSuffixPadding();
            checkVisibility();
            keyBoardAppear();
            keyBoardFocused = KeyBoardEnum.PREFIX;
            changeLayout(true);
        } else if (v.getId() == R.id.thirdText && hasFocus) {
            setARegnoPadding();
            keyBoardAppear();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.aRegisterNoEt && hasFocus) {
            setSuffixPadding();
            keyBoardAppear();
            checkVisibility();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.AREGISTER;
        } else if (v.getId() == R.id.secondText && hasFocus) {
            setARegnoPadding();
            setSuffixPadding();
            changeKeyboard();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.CARDTYPE;
        } else if (v.getId() == R.id.mobileNumberActivationEt && hasFocus) {
            setARegnoPadding();
            setSuffixPadding();
            keyBoardAppear();
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.MOBILE;
        }
        /*else if (v.getId() == R.id.firstAadharText && hasFocus) {
            setARegnoPadding();
            setSuffixPadding();
            checkVisibility();
            keyBoardAppear();
            keyBoardFocused = KeyBoardEnum.AADHARPREFIX;
            changeLayout(false);
        } else if (v.getId() == R.id.thirdAadharText && hasFocus) {
            setARegnoPadding();
            setSuffixPadding();
            keyBoardAppear();
            checkVisibility();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.AADDHARSUFFIX;
        }
        else if (v.getId() == R.id.secondAadharText && hasFocus) {
            setARegnoPadding();
            setSuffixPadding();
//            changeKeyboard();
            checkVisibility();
            keyBoardAppear();
            changeLayout(false);
            keyBoardFocused = KeyBoardEnum.AADHARMIDDLE;
        }*/

    }

    private void keyBoardAppear() {
        keyboardumber.setVisibility(View.VISIBLE);
        keyboardAlpha.setVisibility(View.GONE);
    }

    private void keyBoardDisappear() {
        keyboardumber.setVisibility(View.GONE);
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

    private void setSuffixPadding() {
        if (suffixCard.getText().toString().length() == 7) {
        }
        else if (suffixCard.getText().toString().length() == 6) {
            suffixCard.setText("0" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 5) {
            suffixCard.setText("00" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 4) {
            suffixCard.setText("000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 3) {
            suffixCard.setText("0000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 2) {
            suffixCard.setText("00000" + suffixCard.getText().toString());
        }
        else if (suffixCard.getText().toString().length() == 1) {
            suffixCard.setText("000000" + suffixCard.getText().toString());
        }
    }

    private void setARegnoPadding() {
        if (aRegisterNo.getText().toString().length() == 3) {

        }
        else if (aRegisterNo.getText().toString().length() == 2) {
            aRegisterNo.setText("0" + aRegisterNo.getText().toString());
        }
        else if (aRegisterNo.getText().toString().length() == 1) {
            aRegisterNo.setText("00" + aRegisterNo.getText().toString());
        }
    }

    private void listenersForEditText() {
        /*prefixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (prefixCard.getText().toString().length() == 2) {
                    String value = prefixCard.getText().toString();
                    if (Integer.parseInt(value) > 33 || Integer.parseInt(value) == 0) {
                        Util.messageBar(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this, getString(R.string.invalid_card_prefix));
                    } else
                        cardTypeCard.requestFocus();
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
                if (aRegisterNo.getText().toString().length() == 3)     //size as per your requirement
                {
                    registeredMobile.requestFocus();
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
                    aRegisterNo.requestFocus();
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
                    firstAadhar.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /*firstAadhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (firstAadhar.getText().toString().length() == 4) {
                    secondAadhar.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        secondAadhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (secondAadhar.getText().toString().length() == 4)     //size as per your requirement
                {
                    thirdAadhar.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        thirdAadhar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (thirdAadhar.getText().toString().length() == 4) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    private void setSpinner() {
        /*NoDefaultSpinner spinnerCylinder = (NoDefaultSpinner) findViewById(R.id.spinner_number_cylinder);
        ArrayAdapter<String> cylinderAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.cylindersList));
        cylinderAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCylinder.setAdapter(cylinderAdapt);
        spinnerCylinder.setPrompt(getString(R.string.selection));*/

//        final NoDefaultSpinner mSpinnerCardType = (NoDefaultSpinner) findViewById(R.id.rationCardTypeSpinner);
        final NoDefaultSpinner mSpinnerCardType = (NoDefaultSpinner) findViewById(R.id.rationCardTypeSpinner);
        Map<Integer, String> cards = FPSDBHelper.getInstance(this).getOapAnpCardType();
        List<String> cards2 = new ArrayList<String>();
        Set set2 = cards.entrySet();
        Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
            Map.Entry me2 = (Map.Entry)iterator2.next();
            Log.e("benef activaiton","map values..."+me2.getKey() + ": " + me2.getValue());
            cards2.add(me2.getValue().toString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cards2);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.rationCardTypeList));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCardType.setAdapter(adapter);
        mSpinnerCardType.setPrompt(getString(R.string.selection));
        mSpinnerCardType.setFocusable(true);
        mSpinnerCardType.setFocusableInTouchMode(true);
        mSpinnerCardType.requestFocus();

        /*String[] adults = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        NoDefaultSpinner spinnerAdult = (NoDefaultSpinner) findViewById(R.id.spinnerAdult);
        ArrayAdapter<String> adultAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, adults);
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
        ArrayAdapter<String> childAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, persons);
        childAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChild.setAdapter(childAdapt);
        spinnerChild.setPrompt(getString(R.string.selection));
        spinnerAdult.setPrompt(getString(R.string.selection));
        spinnerChild.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ( findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
                return false;
            }
        });*/
        mSpinnerCardType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                (findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
//                Toast.makeText(BeneficiaryOapAnpActivationActivity.this,"selected...",Toast.LENGTH_SHORT).show();
//                prefixCard.setText("");
                return false;
            }
        });

        mSpinnerCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                cardTypeSpinnerPosition = position;
                // your code here
//                Toast.makeText(BeneficiaryOapAnpActivationActivity.this, "selected..." + mSpinnerCardType.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                String[] cardTypeData = StringUtils.split(mSpinnerCardType.getSelectedItem().toString(), ":");
                /*benefActivNewDto.setCardTypeDef(cardTypeData[1].trim());
                benefActivNewDto.setCardType(mSpinnerCardType.getSelectedItem().toString().charAt(0));*/
                String selString = cardTypeData[0];

//                Toast.makeText(BeneficiaryOapAnpActivationActivity.this, "selected.11.." + selString, Toast.LENGTH_SHORT).show();

                if (selString.trim().equalsIgnoreCase("X")) {
//                    Toast.makeText(BeneficiaryOapAnpActivationActivity.this, "selected.1.." + selString, Toast.LENGTH_SHORT).show();

                    prefixCard.setText("98");
                    cardTypeCard.setText("X");
                    suffixCard.requestFocus();
                } else if (selString.trim().equalsIgnoreCase("Y")) {
//                  Toast.makeText(BeneficiaryOapAnpActivationActivity.this, "selected.2.." + selString, Toast.LENGTH_SHORT).show();

                    prefixCard.setText("99");
                    cardTypeCard.setText("Y");
                    suffixCard.requestFocus();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        /*spinnerCylinder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                (findViewById(R.id.key_board_custom)).setVisibility(View.GONE);
                return false;
            }
        });*/
        if (benefActivNewDto != null) {
            /*int selection = getSelection(cards, benefActivNewDto.getCardTypeDef());
            mSpinnerCardType.setSelection(selection);
            spinnerChild.setSelection(benefActivNewDto.getNumOfChild());
            spinnerAdult.setSelection(benefActivNewDto.getNumOfAdults() - 1);
            spinnerCylinder.setSelection(benefActivNewDto.getNumOfCylinder());*/
            if(benefActivNewDto.getCardTypeDef().equalsIgnoreCase("ANP Card")) {
                mSpinnerCardType.setSelection(0);
            }
            else if(benefActivNewDto.getCardTypeDef().equalsIgnoreCase("OAP Card")) {
                mSpinnerCardType.setSelection(1);
            }
        }
    }

    /*private int getSelection(List<String> cards, String cardDef) {
        int selection = 0;
        for (int i = 0; i < cards.size(); i++) {
            if (StringUtils.contains(cards.get(i), cardDef)) {
                return i;
            }
        }
        return selection;

    }*/

    private void setTamilTextForLabel() {
//        setOapAnpText((TextView) findViewById(R.id.top_textView), R.string.oap_anp_card_registration);
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.oap_anp_card_registration));
        if(GlobalAppState.language.equalsIgnoreCase("hi")) {
            ((TextView) findViewById(R.id.top_textView)).setTextSize(30);
            ((TextView) findViewById(R.id.top_textView)).setTypeface(Typeface.DEFAULT_BOLD);
        }
        Util.setTamilText((TextView) findViewById(R.id.rationCardTypeLabel), R.string.cardCap);
        Util.setTamilText((TextView) findViewById(R.id.rationCardNoLabel), R.string.ration_card_number);
        Util.setTamilText((TextView) findViewById(R.id.aRegisterNoLabel), R.string.aRegisterNo);
        Util.setTamilText((TextView) findViewById(R.id.mobileNoLabel), R.string.mob_number);
        Util.setTamilText((TextView) findViewById(R.id.aadharCardNoLabel), R.string.aadharNo);
        Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.submit_button), R.string.submit);
    }

    private void getCardNumber() {
        try {

            Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity", "getCardNumber() called ");


            String cardNumber1 = prefixCard.getText().toString();
            String cardNumber2 = cardTypeCard.getText().toString();
            String cardNumber3 = suffixCard.getText().toString();
            String aRegisterNumber = aRegisterNo.getText().toString();
            String mobileNumber = registeredMobile.getText().toString();
            String aadharNumber1 = firstAadhar.getText().toString();
            String aadharNumber2 = secondAadhar.getText().toString();
            String aadharNumber3 = thirdAadhar.getText().toString();

            Util.LoggingQueue(this, "OAp/Anp Card Registration", "User entered ::" + "A Register Number" + aRegisterNumber + " cardNumber1:" + cardNumber1 + "cardNumber2:" + cardNumber2 + "cardNumber3:" + cardNumber3 + "::mobile no:" + mobileNumber+ "firstAadhar:" + aadharNumber1+ "secondAadhar:" + aadharNumber2+ "thirdAadhar:" + aadharNumber3);

            try {
                if (Integer.parseInt(cardNumber3) == 0) {
                    Util.messageBar(this, getString(R.string.invalid_card_no));
                    return;
                }
            }
            catch(Exception e) {}

            if (StringUtils.isEmpty(cardNumber1) && StringUtils.isEmpty(cardNumber2) && StringUtils.isEmpty(cardNumber3)) {
                Util.messageBar(this, getString(R.string.enter_ration));
                Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                return;
            }
            if (StringUtils.isEmpty(cardNumber1) || StringUtils.isEmpty(cardNumber2) || StringUtils.isEmpty(cardNumber3)) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                Util.LoggingQueue(this, "OAp/Anp Card Registration", "Invalid Card number : empty card number");
                return;
            }
            /*if (StringUtils.isEmpty(cardNumber1) || Integer.parseInt(cardNumber1) == 0) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                Util.LoggingQueue(this, "OAp/Anp Card Registration", "Invalid District code --> District code greater than 33");
                return;
            }*/
            /*if (cardNumber1.length() == 1) {
                cardNumber1 = "0" + cardNumber1;
                Util.LoggingQueue(this, "OAp/Anp Card Registration", "Appending 0 before cardnumber1" + cardNumber1);
            }*/
            if (cardNumber1.length() != 2 || cardNumber2.length() != 1 || cardNumber3.length() != 7) {
                Util.messageBar(this, getString(R.string.invalid_card_no));
                Util.LoggingQueue(this, "OAp/Anp Card Registration", "Card number length invalid");
                return;
            }
            String cardNumber = cardNumber1 + cardNumber2 + cardNumber3;

            if (StringUtils.isNotEmpty(aRegisterNumber)) {
                try {
                    if (Integer.parseInt(aRegisterNumber) == 0) {
                        Util.LoggingQueue(this, "OAp/Anp Card Registration", "A Register is zero");
                        Util.messageBar(this, getString(R.string.invalidRegNo));
                        return;
                    }
                }
                catch(Exception e) {}
            } else {
                Util.messageBar(this, getString(R.string.emptyRegNo));
                Util.LoggingQueue(this, "OAp/Anp Card Registration", "A Register is empty");
                return;
            }

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

                if (mobileNumber.length() != 10) {
                    Util.messageBar(this, getString(R.string.invalidMobile));
                    Util.LoggingQueue(this, "OAp/Anp Card Registration", "Length of Mobile Number is not eqeual to 10");
                    return;
                }
            }

            if(aadhaarSkip) {
                String aadharNumber = "";
                if (StringUtils.isEmpty(aadharNumber1) && StringUtils.isEmpty(aadharNumber2) && StringUtils.isEmpty(aadharNumber3)) {
                    Util.messageBar(this, getString(R.string.enterAadharNo));
                    Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                    return;
                } else if (StringUtils.isEmpty(aadharNumber1) || StringUtils.isEmpty(aadharNumber2) || StringUtils.isEmpty(aadharNumber3)) {
                    Util.messageBar(this, getString(R.string.invalidAadharNo));
                    Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                    return;
                } else {
                    if (aadharNumber1.length() != 4 || aadharNumber2.length() != 4 || aadharNumber3.length() != 4) {
                        Util.messageBar(this, getString(R.string.invalidAadharNo));
                        Util.LoggingQueue(this, "OAp/Anp Card Registration", "Aadhar number length invalid");
                        return;
                    } else if (String.valueOf(aadharNumber1.charAt(0)).equalsIgnoreCase("0")) {
                        Util.messageBar(this, getString(R.string.aadharNumberZero));
                        return;
                    } else if (String.valueOf(aadharNumber1.charAt(0)).equalsIgnoreCase("1")) {
                        Util.messageBar(this, getString(R.string.aadharNumberOne));
                        return;
                    } else {
                        aadharNumber = aadharNumber1 + aadharNumber2 + aadharNumber3;
                        AadhaarVerhoeffAlgorithm aadhaarVerhoeffAlgorithm = new AadhaarVerhoeffAlgorithm();
                        Boolean isAadharNumber = aadhaarVerhoeffAlgorithm.validateVerhoeff(aadharNumber);
                        if (!isAadharNumber) {
                            Util.messageBar(BeneficiaryOapAnpActivationActivity.this, getString(R.string.checksumValidationFail));
                            return;
                        }
                    }
                }
                try {
                    aadharSeedingDto.setAadhaarNum(Long.valueOf(aadharNumber));
                }
                catch(Exception e) {}
                aadharSeedingDto.setUid(aadharNumber);
            }
            else {
                aadharSeedingDto.setAadhaarNum(0l);
                aadharSeedingDto.setUid(null);
            }


            Util.LoggingQueue(this, "OAp/Anp Card Registration", "Card no:" + cardNumber + "::mobile no:" + mobileNumber);

            if (!checkSpinnerValues()) {
                return;
            }

            benefActivNewDto.setRationCardNumber(cardNumber);
            benefActivNewDto.setMobileNum(mobileNumber);
            benefActivNewDto.setAadhaarSeedingDto(aadharSeedingDto);

            String prefixText = prefixCard.getText().toString();
            new SearchARegNoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, prefixText + aRegisterNumber);

        } catch (Exception e) {
            Util.LoggingQueue(this, "OAp/Anp Card Registration", e.getMessage());
            Util.messageBar(this, getString(R.string.internalError));
            return;
        }
    }

    private void submitCard() {

        Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity", "submitCard() called ");
        Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "BeneficiaryOapAnpActivationActivity", "Entered Mobile Number = "+benefActivNewDto.getMobileNum());

        Intent intent = new Intent(this, OapAnpRegistrationConfirmActivity.class);
        intent.putExtra("data", new Gson().toJson(benefActivNewDto));
        startActivity(intent);
        finish();


    }

    private boolean checkSpinnerValues() {
        NoDefaultSpinner mSpinnerCardType = (NoDefaultSpinner) findViewById(R.id.rationCardTypeSpinner);
        /*NoDefaultSpinner spinnerCylinder = (NoDefaultSpinner) findViewById(R.id.spinner_number_cylinder);
        NoDefaultSpinner spinnerAdult = (NoDefaultSpinner) findViewById(R.id.spinnerAdult);
        NoDefaultSpinner spinnerChild = (NoDefaultSpinner) findViewById(R.id.spinnerChild);*/
        /*if (spinnerAdult.getSelectedItem() == null) {
            Util.messageBar(this, getString(R.string.number_of_adults));
            Util.LoggingQueue(this, "Ration Card Registration", "No of Adults not selected");
            return false;
        } else {*/
        benefActivNewDto.setNumOfAdults(Integer.parseInt("1"));
//        }
        /*if (spinnerChild.getSelectedItem() == null) {
            Util.messageBar(this, getString(R.string.number_of_children));
            Util.LoggingQueue(this, "Ration Card Registration", "No of Children not selected");
            return false;
        } else {*/
        benefActivNewDto.setNumOfChild(Integer.parseInt("0"));
//        }
//        Log.d("card type spinner...","selected item.."+mSpinnerCardType.getSelectedItem().toString());
        if (mSpinnerCardType.getSelectedItem() == null) {
            Util.messageBar(this, getString(R.string.select_ration_card_type));
            Util.LoggingQueue(this, "OAp/Anp Card Registration", "Ration Card Type not selected");
            return false;
        } else {
//            Log.d("card type spinner...", "inside else");
//            benefActivNewDto.setRationCardType(mSpinnerCardType.getSelectedItem().toString());
            String[] cardTypeData = StringUtils.split(mSpinnerCardType.getSelectedItem().toString(), ":");
            benefActivNewDto.setCardTypeDef(cardTypeData[1].trim());

//            Log.d("card type spinner...", "inside else 2 " + mSpinnerCardType.getSelectedItem().toString().trim().charAt(0));
            benefActivNewDto.setCardType(mSpinnerCardType.getSelectedItem().toString().trim().charAt(0));



        }

        /*if (spinnerCylinder.getSelectedItem() == null) {
            Util.messageBar(this, getString(R.string.number_of_cylinder));
            Util.LoggingQueue(this, "Ration Card Registration", "No of Cyclinder not selected");
            return false;
        } else {*/
        benefActivNewDto.setNumOfCylinder(Integer.parseInt("2"));
//        }
        Util.LoggingQueue(this, "OAp/Anp Card Registration", "Check spinner returning true");
        return true;
    }

    @Override
    public void onBackPressed() {
        /*if(((FrameLayout) findViewById(R.id.cameraPreview)).getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(this,BeneficiaryOapAnpActivationActivity.class);
            intent.putExtra("prefixCard",prefixCard.getText().toString());
            intent.putExtra("cardTypeCard",cardTypeCard.getText().toString());
            intent.putExtra("suffixCard",suffixCard.getText().toString());
            intent.putExtra("aRegisterNo",aRegisterNo.getText().toString());
            intent.putExtra("registeredMobile", registeredMobile.getText().toString());
            intent.putExtra("mSpinnerCardType", cardTypeSpinnerPosition);
            intent.putExtra("data", message);
            startActivity(intent);
            finish();
        }
        else {*/
            startActivity(new Intent(this, CardActivationActivity.class));
            Util.LoggingQueue(this, "OAp/Anp Card Registration", "On back pressed");
            finish();
//        }
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
                    /*String text = prefixCard.getText().toString();
                    if (prefixCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        prefixCard.setText(text);
                        prefixCard.setSelection(text.length());
                    } else {
                        aRegisterNo.requestFocus();
                    }*/
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    String text = suffixCard.getText().toString();
                    if (suffixCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        suffixCard.setText(text);
                        suffixCard.setSelection(text.length());
                    } else {
//                        cardTypeCard.requestFocus();
                    }
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    String text = registeredMobile.getText().toString();
                    if (registeredMobile.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        registeredMobile.setText(text);
                        registeredMobile.setSelection(text.length());
                    }
                }
                /*else if (keyBoardFocused == KeyBoardEnum.AADHARPREFIX) {
                    String text = firstAadhar.getText().toString();
                    if (firstAadhar.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        firstAadhar.setText(text);
//                        firstAadhar.setSelection(text.length());
                    }
                }
                else if (keyBoardFocused == KeyBoardEnum.AADHARMIDDLE) {
                    String text = secondAadhar.getText().toString();
                    if (secondAadhar.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        secondAadhar.setText(text);
//                        secondAadhar.setSelection(text.length());
                    }
                }
                else if (keyBoardFocused == KeyBoardEnum.AADDHARSUFFIX) {
                    String text = thirdAadhar.getText().toString();
                    if (thirdAadhar.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        thirdAadhar.setText(text);
//                        thirdAadhar.setSelection(text.length());
                    }
                }*/


            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
                if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    setSuffixPadding();
                    aRegisterNo.requestFocus();
                } else if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                    setARegnoPadding();
                    registeredMobile.requestFocus();
                }
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    suffixCard.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                    aRegisterNo.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    registeredMobile.append("" + ch);
                } /*else if (keyBoardFocused == KeyBoardEnum.AADHARPREFIX) {
                    firstAadhar.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.AADHARMIDDLE) {
                    secondAadhar.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.AADDHARSUFFIX) {
                    thirdAadhar.append("" + ch);
                }*/
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    private class SearchARegNoTask extends AsyncTask<String, Void, Boolean> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this).retrieveARegNoFromBeneficiary(args[0]);
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
                Util.messageBar(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this, getString(R.string.reg_no_exists));
                Util.LoggingQueue(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this, "Oap/Anp Card Registration", "A Register number is already exists");
                return;
            }
            String prefixText = prefixCard.getText().toString();
          //  benefActivNewDto.setAregisterNum(prefixText+aRegisterNo.getText().toString());

             String aRegistrationNumStr =  Integer.parseInt(aRegisterNo.getText().toString())+"";
            benefActivNewDto.setAregisterNum(prefixText+aRegistrationNumStr);

            new SearchCardNoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, benefActivNewDto.getRationCardNumber());
        }
    }

    private class SearchCardNoTask extends AsyncTask<String, Void, Boolean> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected Boolean doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this).retrieveCardNoBeneficiary(args[0]);
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
                Util.messageBar(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this, getString(R.string.ration_card_exists));
                Util.LoggingQueue(com.omneagate.activity.BeneficiaryOapAnpActivationActivity.this, "Oap/Anp Card Registration", "Ration Card number already exists");
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

           /* if (primaryCode == 8) {
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
            }*/
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
                        benefActivNewDto.setAadhaarSeedingDto(aadharSeedingDto);
                        Log.e("aadharNum........ ", aadharSeedingDto.toString());
                        String aadharNum = aadharSeedingDto.getUid();
                        firstAadhar.setText(aadharNum.substring(0,4));
                        secondAadhar.setText(aadharNum.substring(4,8));
                        thirdAadhar.setText(aadharNum.substring(8,12));
//                        Log.e("aadhaarSeedingDto ",aadhaarSeedingDto.toString());
//                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(BeneficiaryOapAnpActivationActivity.this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(BeneficiaryOapAnpActivationActivity.this, "QRcode sales", "QR exception called:" + e.toString());
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
