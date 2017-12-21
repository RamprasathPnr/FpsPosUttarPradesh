package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.omneagate.DTO.AadharSeedingDto;
import com.omneagate.DTO.BenefActivNewDto;
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

public class RationCardActivationAadharActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {


    TextView prefixCard, cardTypeCard, suffixCard;

    BenefActivNewDto benefActivNewDto;

    Button submitButton;

    RelativeLayout keyBoardCustom, aadhaarSkipLay;

    KeyboardView keyview;

    KeyBoardEnum keyBoardFocused;

    AadharSeedingDto aadharSeedingDto;

    // Zbar variables
    /*private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;*/
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.bene_ration_activation_aadhar);

        Util.LoggingQueue(this, "RationCardActivationAadharActivity", "onCreate() called ");


        try {
            if (getIntent().getStringExtra("data") != null) {
                message = getIntent().getStringExtra("data");
                benefActivNewDto = new Gson().fromJson(message, BenefActivNewDto.class);
                Util.LoggingQueue(this, "RationCardActivationAadharActivity", "onCreate() called benefActivNewDto = "+benefActivNewDto);

            }




        }
        catch(Exception e) {}
        setUpCardPage();
    }


    private void setUpCardPage() {
        aadharSeedingDto = new AadharSeedingDto();
        setUpPopUpPage();
        try {
            Util.LoggingQueue(this, "RationCardActivationAadharActivity", "Setting up main page");
            appState = (GlobalAppState) getApplication();
            setTamilTextForLabel();
            keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
            Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
            //create KeyboardView object
            keyview = (KeyboardView) findViewById(R.id.customkeyboard);
            //attache the keyboard object to the KeyboardView object
            keyview.setKeyboard(keyboard);
            //show the keyboard
            keyview.setVisibility(KeyboardView.GONE);
            keyview.setPreviewEnabled(false);
            //take the keyboard to the front
            keyview.bringToFront();
            //register the keyboard to receive the key pressed
            keyview.setOnKeyboardActionListener(new KeyList());
            submitButton = (Button) findViewById(R.id.submit_button);
            prefixCard = (TextView) findViewById(R.id.firstText);
            cardTypeCard = (TextView) findViewById(R.id.secondText);
            suffixCard = (TextView) findViewById(R.id.thirdText);
            aadhaarSkipLay = (RelativeLayout) findViewById(R.id.acknowledgeRelativeLayout);

            String uidMandatory = FPSDBHelper.getInstance(this).getMasterData("UID_MANDATORY");
            if((uidMandatory != null) && (StringUtils.isNotEmpty(uidMandatory.trim())) && (!uidMandatory.equalsIgnoreCase("null"))) {
                if(uidMandatory.equalsIgnoreCase("false")) {
                    aadhaarSkipLay.setVisibility(View.VISIBLE);
                }
                else if(uidMandatory.equalsIgnoreCase("true")) {
                    aadhaarSkipLay.setVisibility(View.INVISIBLE);
                }
            }
            else {
                aadhaarSkipLay.setVisibility(View.VISIBLE);
            }

            try {
                if (benefActivNewDto != null) {

                    if (benefActivNewDto.getAadhaarSeedingDto() != null) {
                        if (benefActivNewDto.getAadhaarSeedingDto().getUid() != null) {
                            String cardNumber = benefActivNewDto.getAadhaarSeedingDto().getUid();
                           // if (StringUtils.isNoneEmpty(cardNumber))
                            prefixCard.setText(StringUtils.substring(cardNumber, 0, 4));
                            cardTypeCard.setText(StringUtils.substring(cardNumber, 4, 8));
                            suffixCard.setText(StringUtils.substring(cardNumber, 8));
                        }
                    }
                }
            }
            catch(Exception e) {}

            Util.setTamilText((TextView) findViewById(R.id.aadharSkip), R.string.skipAdhar);

            keyBoardFocused = KeyBoardEnum.PREFIX;
            /*prefixCard.setOnClickListener(this);
            prefixCard.setShowSoftInputOnFocus(false);
            prefixCard.setOnFocusChangeListener(this);
            cardTypeCard.setOnFocusChangeListener(this);
            suffixCard.setOnFocusChangeListener(this);
            cardTypeCard.setOnClickListener(this);
            cardTypeCard.setShowSoftInputOnFocus(false);
            suffixCard.setOnClickListener(this);
            suffixCard.setShowSoftInputOnFocus(false);*/
            findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
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
            ((CheckBox) findViewById(R.id.aadharSkipAcknowledge)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    acknowledgeAadhar(isChecked);
                }
            });

            if (benefActivNewDto.isChecked()) {
                ((CheckBox) findViewById(R.id.aadharSkipAcknowledge)).setChecked(true);
                acknowledgeAadhar(true);
            }

            findViewById(R.id.scanAadhar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchQRScanner();
                }
            });

        } catch (Exception e) {
            Log.e("dfsd", e.toString(), e);
        }
    }

    // Calling QR Scanner
    private void launchQRScanner() {
//        IntentIntegrator.initiateScan(this, R.layout.activity_capture, R.id.viewfinder_view, R.id.preview_view, true);
        Util.LoggingQueue(this, "QRcode sales", "QR scanner called");
        String packageString = getApplicationContext().getPackageName();
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(packageString);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
        /*if(suffixCard.getText().toString().equalsIgnoreCase("")) {
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

                        benefActivNewDto.setAadhaarSeedingDto(aadharSeedingDto);
                        Log.e("aadharNum........ ", aadharSeedingDto.toString());
                        String aadharNum = aadharSeedingDto.getUid();
                        prefixCard.setText(aadharNum.substring(0,4));
                        cardTypeCard.setText(aadharNum.substring(4,8));
                        suffixCard.setText(aadharNum.substring(8,12));
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
       /* try {
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
 * Added to fix aadhar card seeding date of birth error
 *
 */

            if (dob != null && !dob.isEmpty()) {

                if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                    // Pattern dd/MM/yyyy
                    DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "stringParsing() called -> dob dd/MM/yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());
                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                    // Pattern dd-MM-yyyy
                    DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "stringParsing() called -> dob dd-MM-yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                    // Pattern yyyy/MM/dd
                    DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "stringParsing() called -> dob dd MM yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "stringParsing() called -> dob yyyy/MM/dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "stringParsing() called -> dob yyyy-MM-dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "stringParsing() called -> dob yyyy MM dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    aadharSeedingDto.setDob(date1.getTime());

                } else {

                    Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "stringParsing() called -> dob Unknown Pattern ->" + dob
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

                   /* try {
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
                    catch(Exception e2) {}
*/

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
 * Added to fix aadhar card seeding date of birth error
 *
 */


                        if (dob != null && !dob.isEmpty()) {

                            if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                                // Pattern dd/MM/yyyy
                                DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "xmlParsing() called -> dob dd/MM/yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());
                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                                // Pattern dd-MM-yyyy
                                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "xmlParsing() called -> dob dd-MM-yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                                // Pattern dd MM yyyy
                                DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "xmlParsing() called -> dob dd MM yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy/MM/dd
                                DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "xmlParsing() called -> dob yyyy/MM/dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "xmlParsing() called -> dob yyyy-MM-dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy MM dd
                                DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "xmlParsing() called -> dob yyyy MM dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                aadharSeedingDto.setDob(date1.getTime());

                            } else {

                                Util.LoggingQueue(RationCardActivationAadharActivity.this, "RationCardActivationAadharActivity ", "xmlParsing() called -> dob Unknown Pattern ->" + dob
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


    private void acknowledgeAadhar(boolean isChecked) {
        try {
            if (isChecked) {
                prefixCard.setText("");
                suffixCard.setText("");
                cardTypeCard.setText("");
               /* prefixCard.setOnClickListener(null);
                prefixCard.setCursorVisible(false);
                suffixCard.setCursorVisible(false);
                cardTypeCard.setCursorVisible(false);
                prefixCard.setShowSoftInputOnFocus(false);
                prefixCard.setOnFocusChangeListener(null);
                cardTypeCard.setOnFocusChangeListener(null);
                suffixCard.setOnFocusChangeListener(null);
                cardTypeCard.setOnClickListener(null);
                cardTypeCard.setShowSoftInputOnFocus(false);
                suffixCard.setOnClickListener(null);
                suffixCard.setShowSoftInputOnFocus(false);*/
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                /*prefixCard.setOnClickListener(this);
                prefixCard.setShowSoftInputOnFocus(false);
                prefixCard.setOnFocusChangeListener(this);
                cardTypeCard.setOnFocusChangeListener(this);
                suffixCard.setOnFocusChangeListener(this);
                cardTypeCard.setOnClickListener(this);
                cardTypeCard.setShowSoftInputOnFocus(false);
                suffixCard.setOnClickListener(this);
                suffixCard.setShowSoftInputOnFocus(false);
                prefixCard.setCursorVisible(true);
                suffixCard.setCursorVisible(true);
                cardTypeCard.setCursorVisible(true);
                keyBoardFocused = KeyBoardEnum.PREFIX;
                prefixCard.requestFocus();*/
                keyBoardCustom.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.e("dfsd", e.toString(), e);
        }
    }

    @Override
    public void onClick(View v) {
       /* if (v.getId() == R.id.secondText) {
            cardTypeCard.requestFocus();
            checkVisibility();
            keyBoardFocused = KeyBoardEnum.CARDTYPE;
        } else if (v.getId() == R.id.firstText) {
            checkVisibility();
            prefixCard.requestFocus();
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.thirdText) {
            checkVisibility();
            suffixCard.requestFocus();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        }*/
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        /*if (v.getId() == R.id.firstText && hasFocus) {
            checkVisibility();
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.thirdText && hasFocus) {
            checkVisibility();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.secondText && hasFocus) {
            checkVisibility();
            keyBoardFocused = KeyBoardEnum.CARDTYPE;
        }*/
    }

    private void listenersForEditText() {
        /*prefixCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (prefixCard.getText().toString().length() == 4) {
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

                if (cardTypeCard.getText().toString().length() == 4)     //size as per your requirement
                {
                    suffixCard.requestFocus();
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
                if (suffixCard.getText().toString().length() == 4) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }


    private void setTamilTextForLabel() {
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.card_registration);
        Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.back);
        Util.setTamilText((TextView) findViewById(R.id.submit_button), R.string.submit);
        Util.setTamilText((TextView) findViewById(R.id.ration_card_no), R.string.aadharNo);
    }

    private void getCardNumber() {
        try {
            String cardNumber1 = prefixCard.getText().toString();
            String cardNumber2 = cardTypeCard.getText().toString();
            String cardNumber3 = suffixCard.getText().toString();

            if (((CheckBox) findViewById(R.id.aadharSkipAcknowledge)).isChecked()) {
                benefActivNewDto.setChecked(true);
//                benefActivNewDto.setFamilyHeadAadharNumber(null);
                aadharSeedingDto.setAadhaarNum(0l);
                aadharSeedingDto.setUid(null);
                benefActivNewDto.setAadhaarSeedingDto(aadharSeedingDto);
            } else {
                benefActivNewDto.setChecked(false);
                if (StringUtils.isEmpty(cardNumber1) && StringUtils.isEmpty(cardNumber2) && StringUtils.isEmpty(cardNumber3)) {
                    Util.messageBar(this, getString(R.string.enterAadharNo));
                    Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                    return;
                }
                else if (StringUtils.isEmpty(cardNumber1) || StringUtils.isEmpty(cardNumber2) || StringUtils.isEmpty(cardNumber3)) {
                    Util.messageBar(this, getString(R.string.invalidAadharNo));
                    Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                    return;
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
                    AadhaarVerhoeffAlgorithm aadhaarVerhoeffAlgorithm = new AadhaarVerhoeffAlgorithm();
                    Boolean isAadharNumber = aadhaarVerhoeffAlgorithm.validateVerhoeff(cardNumber);
                    if(!isAadharNumber){
                        Util.messageBar(this, getString(R.string.checksumValidationFail));
                        return;
                    }

//                    benefActivNewDto.setFamilyHeadAadharNumber(cardNumber);
                    try {
                        aadharSeedingDto.setAadhaarNum(Long.valueOf(cardNumber));
                    }
                    catch(Exception e) {}
                    aadharSeedingDto.setUid(cardNumber);
                    benefActivNewDto.setAadhaarSeedingDto(aadharSeedingDto);
                }
            }
            submitCard();
        } catch (Exception e) {
            Util.LoggingQueue(this, "Ration Card Registration", e.getMessage());
            Util.messageBar(this, getString(R.string.internalError));
        }
    }

    private void submitCard() {

        Util.LoggingQueue(this, "RationCardActivationAadharActivity", "Moving to Registration confirmation page");

        Intent intent = new Intent(this, RegistrationConfirmActivity.class);
        intent.putExtra("data", new Gson().toJson(benefActivNewDto));


        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
       /* if(((FrameLayout) findViewById(R.id.cameraPreview)).getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(this, RationCardActivationAadharActivity.class);
            intent.putExtra("data", message);
            startActivity(intent);
            finish();
        }
        else {*/
            editCard();
//        }
    }

    private void editCard() {
        Intent intent = new Intent(this, BeneficiaryRationCardActivationNewActivity.class);
        intent.putExtra("data", new Gson().toJson(benefActivNewDto));
        startActivity(intent);
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
                /*if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    String text = prefixCard.getText().toString();
                    if (prefixCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        prefixCard.setText(text);
                        prefixCard.setSelection(text.length());
                    }
                } else if (keyBoardFocused == KeyBoardEnum.CARDTYPE) {
                    String text = cardTypeCard.getText().toString();
                    if (cardTypeCard.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        cardTypeCard.setText(text);
                        cardTypeCard.setSelection(text.length());
                    } else {
                        prefixCard.requestFocus();
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
                }*/
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                /*if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    prefixCard.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    suffixCard.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.CARDTYPE) {
                    cardTypeCard.append("" + ch);
                }*/
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
        }
        catch(Exception e) {}
        super.onDestroy();
    }


   /* public void onPause() {
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
                        prefixCard.setText(aadharNum.substring(0,4));
                        cardTypeCard.setText(aadharNum.substring(4,8));
                        suffixCard.setText(aadharNum.substring(8,12));
//                        Log.e("aadhaarSeedingDto ",aadhaarSeedingDto.toString());
//                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(RationCardActivationAadharActivity.this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(RationCardActivationAadharActivity.this, "QRcode sales", "QR exception called:" + e.toString());
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
