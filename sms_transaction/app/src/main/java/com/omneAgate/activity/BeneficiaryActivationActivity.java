package com.omneagate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.RMNResponseDto;
import com.omneagate.DTO.RmnValidationReqDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;

//Beneficiary Activity to check Beneficiary Activation
public class BeneficiaryActivationActivity extends BaseActivity {

    //Edit old ration card no
    private EditText mEdtOldRationCardNo;

    //Enter registered mobile no
    private EditText mEdtRegisteredMobileNo;

    //Choose card type
    private Spinner mSpinnerCardType, spinnerCylindersType, spinnerCylinderCount;


    String qrCode = "", refNo = "";

    String mobileNumber = "";

    RmnValidationReqDto qrCodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiary_activation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        httpConnection = new HttpClientWrapper();
        mEdtOldRationCardNo = (EditText) findViewById(R.id.edtOldFamilyCardNo);
        mEdtRegisteredMobileNo = (EditText) findViewById(R.id.edtRegisteredMobileNo);
        IntentIntegrator.initiateScan(this, R.layout.activity_capture,
                R.id.viewfinder_view, R.id.preview_view, true);
        ((Button) findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BeneficiaryActivationActivity.this, SaleActivity.class));
                finish();
            }
        });
        // setUpActivationPage();
    }

    /**
     * QR code response received for card
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                qrResponse(scanResult.getContents());
                break;
            default:
                break;
        }
    }

    //Response from QR reader
    private void qrResponse(String result) {
        SharedPreferences prefs = getSharedPreferences("FPS", MODE_PRIVATE);
        String languageCode = prefs.getString("lCode", "ta");
        Util.changeLanguage(this, languageCode);
        GlobalAppState.language = languageCode;
        if (result == null) {
            startActivity(new Intent(this, SaleActivity.class));
            finish();
        } else {
            String lines[] = result.split("\\r?\\n");
            qrCode = lines[0];
            getOTPFromServer(lines[0]);
        }
    }


    /**
     * send request to get otp from server
     *
     * @param qrCodeString
     */
    private void getOTPFromServer(String qrCodeString) {
        try {
            RmnValidationReqDto qrCode = new RmnValidationReqDto();
            qrCode.setUfc(qrCodeString);
            TransactionBaseDto base = new TransactionBaseDto();
            base.setType("com.omneagate.rest.dto.RmnValidationReqDto");
            base.setTransactionType(TransactionTypes.BENEFICIARY_RMN_VALIDATION);
            base.setBaseDto(qrCode);
            String url = "/transaction/process";
            String qrCodes = new Gson().toJson(base);
            StringEntity se = new StringEntity(qrCodes, HTTP.UTF_8);
            progressBar = new CustomProgressDialog(this);
            progressBar.setCancelable(false);
            progressBar.show();
            httpConnection.sendRequest(url, null, ServiceListenerType.QR_CODE,
                    SyncHandler, RequestType.POST, se, this);
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }
    }

    /**
     * this function used to set initial page for activation
     */
    private void setUpActivationPage() {
        Util.setTamilText((TextView) findViewById(R.id.tvOldRationCardNo), R.string.oldFamilyCard);
        Util.setTamilText((TextView) findViewById(R.id.tvCardType), R.string.cardTypes);
        Util.setTamilText((TextView) findViewById(R.id.tvNoOfCylinder), R.string.cylindersAvail);
        Util.setTamilText((TextView) findViewById(R.id.tvRMN), R.string.rMN);
        Util.setTamilText((TextView) findViewById(R.id.tvBeneficiaryActivation), R.string.beneficiaryActivate);
        Util.setTamilText((TextView) findViewById(R.id.tvNoOfCylinderCount), R.string.noOfCylinderTitle);
        Util.setTamilText((TextView) findViewById(R.id.btnSubmit), R.string.submit);
        Util.setTamilText((TextView) findViewById(R.id.btnCancel), R.string.cancel);
        List<String> cards = FPSDBHelper.getInstance(this).getCardType();
        if (mobileNumber != null && mobileNumber.length() > 0) {
            mEdtRegisteredMobileNo.setText(mobileNumber);
        }
        mSpinnerCardType = (Spinner) findViewById(R.id.spinnerCardType);
        if (cards == null || cards.size() == 0) {
            cards = new ArrayList<>();
            cards.add("AAY");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cards.toArray(new String[cards.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCardType.setAdapter(adapter);

        spinnerCylindersType = (Spinner) findViewById(R.id.spinnerCylindersType);
        ArrayAdapter<String> cylinderAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.cylindersList));
        cylinderAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCylindersType.setAdapter(cylinderAdapt);
        spinnerCylindersType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    ((LinearLayout) findViewById(R.id.cylinderCount)).setVisibility(View.VISIBLE);
                } else {
                    ((LinearLayout) findViewById(R.id.cylinderCount)).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        String[] count = {"1", "2"};
        spinnerCylinderCount = (Spinner) findViewById(R.id.spinnerCylindersCount);
        ArrayAdapter<String> spinnerCylindersCount = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, count);
        spinnerCylindersCount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCylinderCount.setAdapter(spinnerCylindersCount);
    }

    //Orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.changeLanguage(this, GlobalAppState.language);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        switch (what) {
            case QR_CODE:
                QRCodeResponseReceived(message);
                break;
            case BENEFICIARY_UPDATION:
                otpActivationReceived(message);
                break;
            default:
                break;
        }

    }


    //QrCode response from server for respective card
    private void QRCodeResponseReceived(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            RMNResponseDto qrCodeResponseReceived = gson.fromJson(response,
                    RMNResponseDto.class);
            if (qrCodeResponseReceived.getStatusCode() == 0) {
                if (qrCodeResponseReceived.isActivationStatus()) {
                    Util.messageBar(this, getString(R.string.cardAlreadyActivated));
                    Intent intent = new Intent(this, SuccessFailureActivity.class);
                    intent.putExtra("message", getString(R.string.cardAlreadyActivated));
                    startActivity(intent);
                    finish();

                } else {
                    refNo = qrCodeResponseReceived.getReferenceNum();
                    if (qrCodeResponseReceived.getBenefRMN() != null)
                        mobileNumber = qrCodeResponseReceived.getBenefRMN();
                    setUpActivationPage();
                }
            } else {
                String errorMessage = FPSDBHelper.getInstance(this).retrieveLanguageTable(qrCodeResponseReceived.getStatusCode(), GlobalAppState.language).getDescription();
                Util.messageBar(this, errorMessage);
                Intent intent = new Intent(this, SuccessFailureActivity.class);
                intent.putExtra("message", errorMessage);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
            Util.LoggingQueue(this, "Error", e.toString());
        }

    }

    //QrCode response from server for respective card
    private void otpActivationReceived(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            RMNResponseDto qrCodeResponseReceived = gson.fromJson(response,
                    RMNResponseDto.class);
            if (qrCodeResponseReceived.getStatusCode() == 0) {
                if (qrCodeResponseReceived.getOtpTransactionDto() != null) {
                    Intent intent = new Intent(this, BeneficiaryOTPActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("activation", response);
                    Gson gsonData = new Gson();
                    bundle.putString("activationData", gsonData.toJson(qrCodeData));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = FPSDBHelper.getInstance(this).retrieveLanguageTable(qrCodeResponseReceived.getStatusCode(), GlobalAppState.language).getDescription();
                    Util.messageBar(this, errorMessage);
                    Intent intent = new Intent(this, SuccessFailureActivity.class);
                    intent.putExtra("message", errorMessage);
                    startActivity(intent);
                    finish();
                }
            } else {
                String errorMessage = FPSDBHelper.getInstance(this).retrieveLanguageTable(qrCodeResponseReceived.getStatusCode(), GlobalAppState.language).getDescription();
                Util.messageBar(this, errorMessage);
                Intent intent = new Intent(this, SuccessFailureActivity.class);
                intent.putExtra("message", errorMessage);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
            Util.LoggingQueue(this, "Error", e.toString());
        }

    }


    //It is submitting beneficiary activation details
    public void submitBeneficiaryActivationDetails(View view) {
        try {
            qrCodeData = new RmnValidationReqDto();
            qrCodeData.setUfc(qrCode);
            String oldRationCardNo = mEdtOldRationCardNo.getText().toString().trim();
            String rmn = mEdtRegisteredMobileNo.getText().toString().trim();
            if (StringUtils.isEmpty(oldRationCardNo)) {
                Util.messageBar(this, getString(R.string.oldRationNull));
                return;
            }

            if (StringUtils.isEmpty(rmn)) {
                Util.messageBar(this, getString(R.string.rmnNull));
                return;
            }
            if (rmn.length() != 10) {
                Util.messageBar(this, getString(R.string.invalidMobile));
                return;
            }
            int cylinderNo = 0;
            if (spinnerCylindersType.getSelectedItemPosition() == 1) {
                String cylinders = spinnerCylinderCount.getSelectedItem().toString().trim();
                cylinderNo = Integer.parseInt(cylinders);
            }


            qrCodeData.setCardType(mSpinnerCardType.getSelectedItem().toString().charAt(0));
            qrCodeData.setMobileNumber(rmn);
            qrCodeData.setRefNumber(refNo);
            qrCodeData.setOldCardNumber(oldRationCardNo);
            qrCodeData.setNoOfCylinder(cylinderNo);
            AndroidDeviceProperties deviceProperties = new AndroidDeviceProperties(this);
            qrCodeData.setDeviceNumber(deviceProperties.getDeviceProperties().getSerialNumber());
            TransactionBaseDto base = new TransactionBaseDto();
            base.setType("com.omneagate.rest.dto.RmnValidationReqDto");
            base.setTransactionType(TransactionTypes.BENEFICIARY_UPDATION);
            base.setBaseDto(qrCodeData);
            String url = "/transaction/process";
            String qrCodes = new Gson().toJson(base);
            StringEntity se = new StringEntity(qrCodes, HTTP.UTF_8);
            progressBar = new CustomProgressDialog(this);
            progressBar.setCancelable(false);
            progressBar.show();
            httpConnection.sendRequest(url, null, ServiceListenerType.BENEFICIARY_UPDATION,
                    SyncHandler, RequestType.POST, se, this);
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }
}
