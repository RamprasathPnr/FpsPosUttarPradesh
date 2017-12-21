package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.omneagate.DTO.AadharSeedingDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.BeneficiarySalesQRTransaction;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by user1 on 27-06-2016.
 */


public class AadharQRBasedSalesOrderActivity extends BaseActivity {


    AadharSeedingDto aadharSeedingDto;
    final Handler handler = new Handler();
    //TransactionBaseDto transaction;
    String number = "";
    String aadharNum = "";
    TransactionBaseDto transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.aadhar_qr_based_sales_activity);
        Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "onCreate called");
        try{
            aadharSeedingDto = new AadharSeedingDto();
            networkConnection = new NetworkConnection(this);
            httpConnection = new HttpClientWrapper();
            appState = (GlobalAppState) getApplication();
            transaction = new TransactionBaseDto();
            transaction.setType("com.omneagate.rest.dto.QRRequestDto");
            transaction.setTransactionType(TransactionTypes.SALE_QR_OTP_DISABLED);
            TransactionBase.getInstance().setTransactionBase(transaction);
            // Launch QR Scanner
            launchQRScanner();
        }catch (Exception e){
            Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "onCreate called -> Exception e "+e);

        }
    }



    /**
     * Calling QR Scanner
     */
    private void launchQRScanner() {

       // Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "QR scanner called");
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
                        aadharSeedingDto.setScannedQRData(contents);
                      //  Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "onActivityResult() EncryptedUFC -> "+contents);

                        if (contents.contains("<PrintLetterBarcodeData")) {
                            String resultString = null;
                            StringBuilder sb = new StringBuilder(contents);
                            if ((sb.charAt(1) == '/')) {
                                sb.deleteCharAt(1);
                                resultString = sb.toString();
                            } else {
                                resultString = contents;
                            }
                            xmlParsing(resultString);
                        } else {
                            stringParsing(contents);
                        }


                        if (aadharSeedingDto != null){
                            aadharNum = aadharSeedingDto.getUid();
                            Util.LoggingQueue(this, "Scanned Aadhar Details ", " aadharNum -> "+aadharNum);
                           // Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "onActivityResult() aadhaarSeedingDto -> "+aadharSeedingDto.toString());
                            getBeneficiaryID(aadharNum);
                        }else{
                            errorNavigation(getString(R.string.qrCodeInvalid));
                         //   Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "onActivityResult() Aadhar QR Invalid Error ->" + getString(R.string.qrCodeInvalid));

                        }

                    } catch (Exception e) {
//                        Util.messageBar(this, getString(R.string.qrCodeInvalid));
                        errorNavigation(getString(R.string.qrCodeInvalid));
                      //  Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "onActivityResult() Aadhar QR exception called:" + e.toString());
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                   // Log.e(QRCodeSalesActivity.class.getSimpleName(), "Scan cancelled");
                    Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "Scan cancelled");
                    finish();
                    Intent myIntent = new Intent(getApplicationContext(), SaleOrderActivity.class);
                    startActivity(myIntent);
                }
                break;

            default:
                break;
        }
    }

    private void stringParsing(String text) {
        Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "stringParsing() started text = "+text);

        String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                state = "", pc = "", dob = "";
        String[] strArr = text.split(",");
        for (int i = 0; i < strArr.length; i++) {
            try {
               // Log.e("mara", "strArr contents" + strArr[i].toString());
                String element = strArr[i].toString();
                String[] strArr2 = element.split(":");

                if (strArr2[0].equalsIgnoreCase(" aadhaar no")) {
                    uid = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" Name")) {
                    name = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" Gender")) {
                    gender = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" YOB")) {
                    yob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" co")) {
                    co = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" house")) {
                    house = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" street")) {
                    street = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" lmark")) {
                    lm = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" loc")) {
                    loc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" vtc")) {
                    vtc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" po")) {
                    po = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" dist")) {
                    dist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" subdist")) {
                    subdist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" state")) {
                    state = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" pc")) {
                    pc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" DOB")) {
                    dob = strArr2[1];
                }

                if (strArr2[0].equalsIgnoreCase("aadhaar no")) {
                    uid = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("Name")) {
                    name = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("Gender")) {
                    gender = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("YOB")) {
                    yob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("co")) {
                    co = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("house")) {
                    house = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("street")) {
                    street = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("lmark")) {
                    lm = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("loc")) {
                    loc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("vtc")) {
                    vtc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("po")) {
                    po = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("dist")) {
                    dist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("subdist")) {
                    subdist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("state")) {
                    state = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("pc")) {
                    pc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("DOB")) {
                    dob = strArr2[1];
                }
            } catch (Exception e) {
                Log.e("mara", "string exception" + e);
            }
        }

        aadharSeedingDto.setAadhaarNum(Long.parseLong(uid));
        aadharSeedingDto.setUid(uid);
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
        if (!yob.equalsIgnoreCase("")) {
            aadharSeedingDto.setYob(Long.parseLong(yob));
        }
        try {
            if (!dob.equalsIgnoreCase("")) {
                SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = sourceFormat.parse(dob);
                SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String dateStr = targetFormat.format(date1);
                Date date2 = targetFormat.parse(dateStr);
                aadharSeedingDto.setDob(date2.getTime());
            }
        } catch (Exception e) {
            try {
                if (!dob.equalsIgnoreCase("")) {
                    SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = sourceFormat.parse(dob);
                    SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    String dateStr = targetFormat.format(date1);
                    Date date2 = targetFormat.parse(dateStr);
                    aadharSeedingDto.setDob(date2.getTime());
                }
            } catch (Exception e2) {
            }
        }

       /* Log.e("UIDValue", uid);
        Log.e("name", name);
        Log.e("gender", gender);
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
        Log.e("dob", dob);*/

        Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "stringParsing() finished uid =  "+uid);


    }

    private void xmlParsing(String xmlData) {
        Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "stringParsing() started xmlData = "+xmlData);

        try {
            String xmlRecords = xmlData.replaceAll("&", "&amp;").replaceAll("'", "&apos;");
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            //Log.e("update user", "xmlRecords..." + xmlRecords + "...");
            is.setCharacterStream(new StringReader(xmlRecords));
            Document dom = db.parse(is);
            NodeList l = dom.getElementsByTagName("PrintLetterBarcodeData");
            for (int j = 0; j < l.getLength(); ++j) {
                Node prop = l.item(j);
                NamedNodeMap attr = prop.getAttributes();
                if (null != attr) {
                    //Node nodeUid = attr.getNamedItem("uid");
                    String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                            state = "", pc = "", dob = "";

                    try {
                        uid = attr.getNamedItem("uid").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        name = attr.getNamedItem("name").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        gender = attr.getNamedItem("gender").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        yob = attr.getNamedItem("yob").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        co = attr.getNamedItem("co").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        house = attr.getNamedItem("house").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        street = attr.getNamedItem("street").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        lm = attr.getNamedItem("lm").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        loc = attr.getNamedItem("loc").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        vtc = attr.getNamedItem("vtc").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        po = attr.getNamedItem("po").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        dist = attr.getNamedItem("dist").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        subdist = attr.getNamedItem("subdist").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        state = attr.getNamedItem("state").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        pc = attr.getNamedItem("pc").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        dob = attr.getNamedItem("dob").getNodeValue();
                    } catch (Exception e) {
                    }


//                    aadhaarSeedingDto.setRationCardNumber(rationcardNo);
                    aadharSeedingDto.setAadhaarNum(Long.parseLong(uid));
                    aadharSeedingDto.setUid(uid);
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
                    if (!yob.equalsIgnoreCase("")) {
                        aadharSeedingDto.setYob(Long.parseLong(yob));
                    }

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            aadharSeedingDto.setDob(date2.getTime());
                        }
                    } catch (Exception e) {
                    }

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy/MM/dd");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            aadharSeedingDto.setDob(date2.getTime());
                        }
                    } catch (Exception e) {
                    }

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            aadharSeedingDto.setDob(date2.getTime());
                        }
                    } catch (Exception e2) {
                    }

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            aadharSeedingDto.setDob(date2.getTime());
                        }
                    } catch (Exception e2) {
                    }

                  /*  Log.e("UIDValue", uid);
                    Log.e("name", name);
                    Log.e("gender", gender);
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
                    Log.e("dob", dob);*/

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

                   /* Log.e("Xml Records", xmlRecords);
                    Log.e(">>>>>>>>>>", ">>>>>>>>>");
                    Log.e("Details", stringBuffer.toString());*/

                    Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "xmlParsing() finished uid =  "+uid);

                }
            }
        } catch (Exception e) {
            Log.e("QRCode scan", "exc..", e);
        }
    }

    public void getBeneficiaryID(String aadharNumber) {
        try {
            Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "getBeneficiaryID() started aadharNumber = "+aadharNumber);

            String beneficiaryID = FPSDBHelper.getInstance(AadharQRBasedSalesOrderActivity.this).retrieveBeneficiaryId(aadharNumber);

            if (beneficiaryID.isEmpty()){
                Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "Beneficiary is not Found ");

                Toast.makeText(AadharQRBasedSalesOrderActivity.this, getString(R.string.no_benficiary_found), Toast.LENGTH_LONG).show();
                //startActivity(new Intent(this, SaleOrderActivity.class));
                onBackPressed();
               // finish();
            }else{

                long id = Long.parseLong(beneficiaryID);
                BeneficiaryDto beneficiaryDto =  FPSDBHelper.getInstance(AadharQRBasedSalesOrderActivity.this).retrieveBeneficiary(id);
              //  Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "getBeneficiaryID() beneficiaryDto -> "+beneficiaryDto);
                Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "Beneficiary Found  -> "+beneficiaryID);

                if (beneficiaryDto != null){

                    String encrypted_ufc_code = beneficiaryDto.getEncryptedUfc();

                    Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "Beneficiary encrypted_ufc_code -> "+encrypted_ufc_code);

                    getEntitlement(encrypted_ufc_code);

                }else{
                    Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "No beneficiary record found for this beneficiaryID -> "+beneficiaryID);

                }

            }



        } catch (Exception e) {
            Log.e("getBeneficiaryID Excp", "" + e.toString());
        }


    }
    private void getEntitlement(String qrCodeString) {
        try {

            Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "getEntitlement() started qrCodeString -> "+qrCodeString);

            BeneficiarySalesQRTransaction beneficiary = new BeneficiarySalesQRTransaction(this);

            QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeString);

            if (qrCodeResponseReceived != null)
             //   Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "getEntitlement() QRTransactionResponseDto -> "+qrCodeResponseReceived.toString());

            if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null &&
                    qrCodeResponseReceived.getEntitlementList().size() > 0) {

//                qrCodeResponseReceived.setMode('H');
                EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                if (SessionId.getInstance().isQrOTPEnabled()) {
                } else {
                    /** 08/07/2016
                     * SaleType defines Mode while inserting into FPSDB.db in SalesSummaryWithOutOTPActivity.class
                     * Online Mode  - H
                     * No Offline Mode for Aadhar card QR based sale
                     */

                    // Log.e("RationCardSalesActivity", "QRTransactionResponseDto..." + qrCodeResponseReceived.toString());
                    if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
                        NetworkConnection network = new NetworkConnection(this);
                        if (network.isNetworkAvailable()) {
                            qrCodeResponseReceived.setMode('H');
                        }
                        else {
                            qrCodeResponseReceived.setMode('I');
                        }
                    }

                    Intent intent = new Intent(this, SalesEntryActivity.class);
                    transaction = new TransactionBaseDto();
                    transaction.setType("com.omneagate.rest.dto.QRRequestDto");
                    transaction.setTransactionType(TransactionTypes.SALE_QR_OTP_DISABLED);
                    TransactionBase.getInstance().setTransactionBase(transaction);
                    intent.putExtra("SaleType", "AadharQRSale");
                    startActivity(intent);
                    finish();
                }
            } else {
                errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity ", "getEntitlement() Exception -> "+e);
            errorNavigation(getString(R.string.qrCodeInvalid));
        }
    }



    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Intent intent = new Intent(this, SuccessFailureSalesActivity.class);
        Util.LoggingQueue(this, "Error in AadharQRCodeSalesActiv", "Navigating to error page");
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleOrderActivity.class));
        Util.LoggingQueue(this, "AadharQRBasedSalesOrderActivity", "On Back pressed Called");
        finish();
    }


}
