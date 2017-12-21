package com.omneagate.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.client.android.Intents;
import com.neopixl.pixlui.components.edittext.EditText;
import com.omneagate.DTO.AadharSeedingDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProxyDetailDto;
import com.omneagate.Util.AadhaarVerhoeffAlgorithm;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.RationCardListAdapter;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class AddProxyDetailsActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    RationCardListAdapter rationCardListAdapter;
    ArrayList<String> benefDetailsList;
    RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;
    KeyboardView keyview, keyboardViewAlpha;
    KeyBoardEnum keyBoardFocused;
    EditText mobileNo, dob, proxyName;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    com.neopixl.pixlui.components.edittext.EditText aadharNumber1, aadharNumber2, aadharNumber3;
    AadharSeedingDto beneMemberUpdate;
    BeneficiaryDto benef;
    BeneficiaryMemberDto beneficiaryMemberDto;
    String rcNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.add_proxy_details);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpPopUpPage();
        configureData();
    }

    private void configureData() {
        loadHeaderData();
        rcNo = getIntent().getStringExtra("RcNumber");
        benef = FPSDBHelper.getInstance(AddProxyDetailsActivity.this).beneficiaryFromOldCard(rcNo);
        if(benef != null) {
            beneficiaryMemberDto = FPSDBHelper.getInstance(AddProxyDetailsActivity.this).getHeadBeneficiaryMember(benef.getEncryptedUfc().trim(), benef.getFamilyHeadAadharNumber());
            if (beneficiaryMemberDto != null) {
                ((TextView) findViewById(R.id.rcNoValue)).setText(benef.getOldRationNumber());
                ((TextView) findViewById(R.id.cardHolderNameValue)).setText(beneficiaryMemberDto.getName());
                loadOnClickEvent();
            }
        }
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadHeaderData() {
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.add_proxy);
        Util.setTamilText((TextView) findViewById(R.id.rcNoHeading), R.string.ration_card_number1);
        Util.setTamilText((TextView) findViewById(R.id.cardHolderNameHeading), R.string.card_holder_name);
        Util.setTamilText((Button) findViewById(R.id.submitButton), R.string.submit);
        Util.setTamilText((Button) findViewById(R.id.cancelButton), R.string.cancel);
        beneMemberUpdate = new AadharSeedingDto();
        proxyName = (EditText) findViewById(R.id.proxyNameEt);
        dob = (EditText) findViewById(R.id.dobEt);
        dob.setOnFocusChangeListener(this);
        dob.setOnClickListener(this);
        dob.setShowSoftInputOnFocus(false);
        mobileNo = (EditText) findViewById(R.id.mobileNoEt);
        mobileNo.setOnClickListener(this);
        mobileNo.setShowSoftInputOnFocus(false);
        mobileNo.setOnFocusChangeListener(AddProxyDetailsActivity.this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        setDateTimeField();
        aadharNumber1 = (com.neopixl.pixlui.components.edittext.EditText) findViewById(R.id.firstText);
        aadharNumber2 = (com.neopixl.pixlui.components.edittext.EditText) findViewById(R.id.secondText);
        aadharNumber3 = (com.neopixl.pixlui.components.edittext.EditText) findViewById(R.id.thirdText);
        aadharNumber1.setShowSoftInputOnFocus(false);
        aadharNumber1.disableCopyAndPaste();
        aadharNumber2.setShowSoftInputOnFocus(false);
        aadharNumber2.disableCopyAndPaste();
        aadharNumber3.setShowSoftInputOnFocus(false);
        aadharNumber3.disableCopyAndPaste();
        aadharNumber1.setOnFocusChangeListener(this);
        aadharNumber1.setOnClickListener(this);
        aadharNumber2.setOnFocusChangeListener(this);
        aadharNumber2.setOnClickListener(this);
        aadharNumber3.setOnFocusChangeListener(this);
        aadharNumber3.setOnClickListener(this);
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
    }

    private void loadOnClickEvent() {
        findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proxyNameStr = proxyName.getText().toString();
                String mobileNoStr = mobileNo.getText().toString();
                String dobStr = dob.getText().toString();
                String cardNumber1 = aadharNumber1.getText().toString();
                String cardNumber2 = aadharNumber2.getText().toString();
                String cardNumber3 = aadharNumber3.getText().toString();
                if (StringUtils.isEmpty(cardNumber1) && StringUtils.isEmpty(cardNumber2) && StringUtils.isEmpty(cardNumber3)) {
                    Util.messageBar(AddProxyDetailsActivity.this, getString(R.string.aadhar_empty));
                } else {
                    if (cardNumber1.length() != 4 || cardNumber2.length() != 4 || cardNumber3.length() != 4) {
                        Util.messageBar(AddProxyDetailsActivity.this, getString(R.string.invalidAadharNo));
                        return;
                    }
                    if (String.valueOf(cardNumber1.charAt(0)).equalsIgnoreCase("0")) {
                        Util.messageBar(AddProxyDetailsActivity.this, getString(R.string.aadharNumberZero));
                        return;
                    }
                    if (String.valueOf(cardNumber1.charAt(0)).equalsIgnoreCase("1")) {
                        Util.messageBar(AddProxyDetailsActivity.this, getString(R.string.aadharNumberOne));
                        return;
                    }
                    String cardNumber = cardNumber1 + cardNumber2 + cardNumber3;
                    AadhaarVerhoeffAlgorithm aadhaarVerhoeffAlgorithm = new AadhaarVerhoeffAlgorithm();
                    Boolean isAadharNumber = aadhaarVerhoeffAlgorithm.validateVerhoeff(cardNumber);
                    if(!isAadharNumber){
                        Util.messageBar(AddProxyDetailsActivity.this, getString(R.string.checksumValidationFail));
                        return;
                    }
                    beneficiaryMemberDto = new BeneficiaryMemberDto();
                    beneficiaryMemberDto = FPSDBHelper.getInstance(AddProxyDetailsActivity.this).getProxyBeneficiaryMember(cardNumber);
                    if(beneficiaryMemberDto == null)
                        beneficiaryMemberDto.setId(null);
                    submitValue(proxyNameStr, dobStr, cardNumber, mobileNoStr);
                }
            }
        });
        findViewById(R.id.scanAadhar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchQRScanner();
            }
        });
        listenersForEditText();
    }

    private void submitValue(String proxyName, String dob, String aadharNo, String mobileNo) {
        ProxyDetailDto proxyDetailsDto = new ProxyDetailDto();
        proxyDetailsDto.setBeneficiary(benef);
        proxyDetailsDto.setBeneficiaryMember(beneficiaryMemberDto);
        proxyDetailsDto.setName(proxyName);
        proxyDetailsDto.setMobile(mobileNo);
        proxyDetailsDto.setUid(aadharNo);
        if(SessionId.getInstance().getFpsId() != 0)
            proxyDetailsDto.setCreatedBy(SessionId.getInstance().getFpsId());
        proxyDetailsDto.setRequestStatus("Pending");
        try {
            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = sourceFormat.parse(dob);
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String dateStr = targetFormat.format(date1);
            Date date2 = targetFormat.parse(dateStr);
            proxyDetailsDto.setDob(date2.getTime());
        }
        catch(Exception e) {}
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            Date created = sdf.parse(dateString);
            proxyDetailsDto.setCreatedDate(created.getTime());
        }
        catch(Exception e) {}
        sendProxyDetailToServer(proxyDetailsDto);
    }

    private void sendProxyDetailToServer(ProxyDetailDto proxyDetailsDtos) {
        // Proxy Details sync process
        try {
            NetworkConnection network = new NetworkConnection(AddProxyDetailsActivity.this);
            if (network.isNetworkAvailable()) {
                BufferedReader in = null;
                String url = "/proxydetail/add";
                String updateData = new Gson().toJson(proxyDetailsDtos);
                StringEntity se = null;
                se = new StringEntity(updateData, HTTP.UTF_8);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.PROXY_REGISTRATION, SyncHandler, RequestType.POST, se, this);
            }
        }
        catch(Exception e) {
            Util.LoggingQueue(AddProxyDetailsActivity.this, "proxy details service exception..", e.getMessage());
        }
    }

    // Calling QR Scanner
    private void launchQRScanner() {
        Util.LoggingQueue(this, "QRcode sales", "QR scanner called");
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

                        String aadharNum = beneMemberUpdate.getUid();
                        aadharNumber1.setText(aadharNum.substring(0,4));
                        aadharNumber2.setText(aadharNum.substring(4,8));
                        aadharNumber3.setText(aadharNum.substring(8,12));
                        Log.e("aadhaarSeedingDto ",beneMemberUpdate.toString());
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

        beneMemberUpdate.setAadhaarNum(Long.parseLong(uid));
        beneMemberUpdate.setUid(uid);
        beneMemberUpdate.setName(name);
        beneMemberUpdate.setGender(gender.charAt(0));
        beneMemberUpdate.setCo(co);
        beneMemberUpdate.setHouse(house);
        beneMemberUpdate.setStreet(street);
        beneMemberUpdate.setLm(lm);
        beneMemberUpdate.setLoc(loc);
        beneMemberUpdate.setVtc(vtc);
        beneMemberUpdate.setPo(po);
        beneMemberUpdate.setDist(dist);
        beneMemberUpdate.setSubdist(subdist);
        beneMemberUpdate.setState(state);
        beneMemberUpdate.setPc(pc);
        if(!yob.equalsIgnoreCase("")) {
            beneMemberUpdate.setYob(Long.parseLong(yob));
        }
        try {
            if (!dob.equalsIgnoreCase("")) {
                SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = sourceFormat.parse(dob);
                SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String dateStr = targetFormat.format(date1);
                Date date2 = targetFormat.parse(dateStr);
                beneMemberUpdate.setDob(date2.getTime());
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
                    beneMemberUpdate.setDob(date2.getTime());
                }
            }
            catch(Exception e2) {}
        }

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

    }

    private void xmlParsing(String xmlData) {
        try
        {
            String xmlRecords = xmlData.replaceAll("&", "&amp;").replaceAll("'", "&apos;").replace("?\"", "\"?");
            Log.e("members aadhar","xmlRecords..."+xmlRecords);
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
                    beneMemberUpdate.setAadhaarNum(Long.parseLong(uid));
                    beneMemberUpdate.setUid(uid);
                    beneMemberUpdate.setName(name);
                    beneMemberUpdate.setGender(gender.charAt(0));
                    beneMemberUpdate.setCo(co);
                    beneMemberUpdate.setHouse(house);
                    beneMemberUpdate.setStreet(street);
                    beneMemberUpdate.setLm(lm);
                    beneMemberUpdate.setLoc(loc);
                    beneMemberUpdate.setVtc(vtc);
                    beneMemberUpdate.setPo(po);
                    beneMemberUpdate.setDist(dist);
                    beneMemberUpdate.setSubdist(subdist);
                    beneMemberUpdate.setState(state);
                    beneMemberUpdate.setPc(pc);
                    if(!yob.equalsIgnoreCase("")) {
                        beneMemberUpdate.setYob(Long.parseLong(yob));
                    }
                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            beneMemberUpdate.setDob(date2.getTime());
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
                                beneMemberUpdate.setDob(date2.getTime());
                            }
                        }
                        catch(Exception e2) {}
                    }

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
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setDateTimeField() {
        dob.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dob.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View v) {
        /*if (v.getId() == R.id.proxyNameEt) {
            checkVisibility();
            keyBoardAppear();
            proxyName.requestFocus();
            keyBoardFocused = KeyBoardEnum.PROXYNAME;
            changeLayout(false);
        }
        else*/
        if (v.getId() == R.id.firstText) {
            checkVisibility();
            keyBoardAppear();
            aadharNumber1.requestFocus();
            keyBoardFocused = KeyBoardEnum.PREFIX;
            changeLayout(true);
        } else if (v.getId() == R.id.secondText) {
            checkVisibility();
            keyBoardAppear();
            aadharNumber2.requestFocus();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
            changeLayout(true);
        } else if (v.getId() == R.id.thirdText) {
            checkVisibility();
            keyBoardAppear();
            aadharNumber3.requestFocus();
            keyBoardFocused = KeyBoardEnum.FINALSTRING;
            changeLayout(true);
        } else if (v.getId() == R.id.mobileNoEt) {
            checkVisibility();
            keyBoardAppear();
            mobileNo.requestFocus();
            keyBoardFocused = KeyBoardEnum.MOBILE;
            changeLayout(true);
        } else if (v.getId() == R.id.dobEt) {
            dob.requestFocus();
            keyBoardCustom.setVisibility(View.GONE);
            keyBoardFocused = KeyBoardEnum.NOTHING;
            fromDatePickerDialog.show();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        /*if (v.getId() == R.id.proxyNameEt && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            proxyName.requestFocus();
            keyBoardFocused = KeyBoardEnum.PROXYNAME;
            changeLayout(false);
        }
        else*/
        if (v.getId() == R.id.firstText && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            changeLayout(true);
            aadharNumber1.requestFocus();
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.secondText && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            changeLayout(true);
            aadharNumber2.requestFocus();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.thirdText && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            changeLayout(true);
            aadharNumber3.requestFocus();
            keyBoardFocused = KeyBoardEnum.FINALSTRING;
        } else if (v.getId() == R.id.mobileNoEt && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            mobileNo.requestFocus();
            keyBoardFocused = KeyBoardEnum.MOBILE;
            changeLayout(true);
        } else if (v.getId() == R.id.dobEt && hasFocus) {
            dob.requestFocus();
            keyBoardCustom.setVisibility(View.GONE);
            keyBoardFocused = KeyBoardEnum.NOTHING;
            fromDatePickerDialog.show();
        }
    }

    private void listenersForEditText() {
        aadharNumber1.addTextChangedListener(new TextWatcher() {
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
        });
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

    private void changeLayout(boolean value) {
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.proxyDetailsLayoutMaster);
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
                /*if (keyBoardFocused == KeyBoardEnum.PROXYNAME) {
                    String text = proxyName.getText().toString();
                    if (proxyName.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        proxyName.setText(text);
                        proxyName.setSelection(text.length());
                    }
                }
                else*/
                if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    String text = aadharNumber1.getText().toString();
                    if (aadharNumber1.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber1.setText(text);
                        aadharNumber1.setSelection(text.length());
                    }
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    String text = aadharNumber2.getText().toString();
                    if (aadharNumber2.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber2.setText(text);
                        aadharNumber2.setSelection(text.length());
                    } else {
                        aadharNumber1.requestFocus();
                    }
                } else if (keyBoardFocused == KeyBoardEnum.FINALSTRING) {
                    String text = aadharNumber3.getText().toString();
                    if (aadharNumber3.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber3.setText(text);
                        aadharNumber3.setSelection(text.length());
                    } else {
                        aadharNumber2.requestFocus();
                    }
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    String text = mobileNo.getText().toString();
                    if (mobileNo.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        mobileNo.setText(text);
                        mobileNo.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);

            } else {
                char ch = (char) primaryCode;
                /*if (keyBoardFocused == KeyBoardEnum.PROXYNAME) {
                    proxyName.append("" + ch);
                }
                else*/
                if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    aadharNumber1.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    aadharNumber2.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.FINALSTRING) {
                    aadharNumber3.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    mobileNo.append("" + ch);
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
                /*if (keyBoardFocused == KeyBoardEnum.PROXYNAME) {
                    String text = proxyName.getText().toString();
                    if (proxyName.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        proxyName.setText(text);
                        proxyName.setSelection(text.length());
                    }
                }
                else*/ if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    String text = mobileNo.getText().toString();
                    if (mobileNo.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        mobileNo.setText(text);
                        mobileNo.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                /*if (keyBoardFocused == KeyBoardEnum.PROXYNAME) {
                    proxyName.append("" + ch);
                }
                else*/ if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    mobileNo.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }



    @Override
    public void onBackPressed() {
        if((rcNo != null) && (!rcNo.equalsIgnoreCase("null")) && StringUtils.isNotEmpty(rcNo.trim())) {
            Intent intent = new Intent(this, AddProxyActivity.class);
            intent.putExtra("RcNumber", rcNo);
            startActivity(intent);
            finish();
        }
    }

    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Toast.makeText(AddProxyDetailsActivity.this, messages, Toast.LENGTH_SHORT).show();
    }

    private void dismissProgress() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
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

    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case PROXY_REGISTRATION:
                proxyRegSubmissionResponse(message);
                break;
            default:
                errorNavigation("");
                break;
        }
    }

    private void proxyRegSubmissionResponse(Bundle message) {
        dismissProgress();
        String response = message.getString(FPSDBConstants.RESPONSE_DATA);
        Log.e("Statistics service", "proxy details response..."+response);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        ProxyDetailDto proxyDetailsDto = gson.fromJson(response, ProxyDetailDto.class);
        if(proxyDetailsDto.getStatusCode() == 0) {
            boolean inserted = FPSDBHelper.getInstance(AddProxyDetailsActivity.this).insertProxyDetails(proxyDetailsDto);
//            FPSDBHelper.getInstance(AddProxyDetailsActivity.this).updateProxyDetailsSyncStatus(proxyDetailsDto.getBeneficiary().getId());
            if (inserted) {
                Intent intent = new Intent(AddProxyDetailsActivity.this, TenFingerRegistrationActivity.class);
                intent.putExtra("AadharNo", proxyDetailsDto.getUid());
                intent.putExtra("MemberType", "1");
                intent.putExtra("BenefId", benef.getId());
                String proxyDto = new Gson().toJson(proxyDetailsDto);
                intent.putExtra("ProxyDetailsDto", proxyDto);
                String rcNo = getIntent().getStringExtra("RcNumber");
                intent.putExtra("RcNumber", rcNo);
                startActivity(intent);
                finish();
            }
        }
        else {
            String messageData = "";
            try {
                messageData = Util.messageSelection(FPSDBHelper.getInstance(AddProxyDetailsActivity.this).retrieveLanguageTable(proxyDetailsDto.getStatusCode()));
            }
            catch(Exception e) {
                if (StringUtils.isEmpty(messageData))
                    messageData = "Proxy sync failed";
            }
            if (StringUtils.isEmpty(messageData))
                messageData = "Proxy sync failed";
            Toast.makeText(AddProxyDetailsActivity.this, messageData, Toast.LENGTH_SHORT).show();
            Util.LoggingQueue(AddProxyDetailsActivity.this, "Statistics service", "Error syncing proxy detail..." + messageData);
        }
    }

}