package com.omneagate.activity.SMSActivation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.TransactionController.*;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.DownloadDataProcessor;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.BaseActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.SaleActivity;
import com.omneagate.activity.SuccessFailureActivity;
import com.omneagate.service.HttpClientWrapper;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;

public class BeneficiarySubmissionActivity extends BaseActivity  implements SMSForCardListener {

    String address = "";
    String qrCode = "";
    //Progressbar for waiting
    CustomProgressDialog progressBar;
    //HttpConnection service
    HttpClientWrapper httpConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiary_submission);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actionBarCreation();
        String data = getIntent().getExtras().getString("response");
        httpConnection = new HttpClientWrapper();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        BenefActivNewDto benefActivNewDto = gson.fromJson(data, BenefActivNewDto.class);
        userDetails(benefActivNewDto);
    }

    public void submitImageCapture(View view) {
        startActivityForResult(new Intent(this, CameraActivity.class), 1);
    }

    public void submitScanQr(View view) {
        IntentIntegrator.initiateScan(this, R.layout.activity_capture,
                R.id.viewfinder_view, R.id.preview_view, true);
    }

    @Override
    public void smsCardReceived(BenefActivNewDto benefActivNewDto) {
//        stopTimerTask(benefActivNewDto);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("req", "Req" + requestCode + "::" + "result::" + resultCode);
        if (requestCode == 1 && resultCode == 2) {
            address = data.getStringExtra("address");
            imageChange(address);
        }
        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode != 2) {
            try {
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                qrCode = scanResult.getContents();
                changeData(qrCode);
            } catch (Exception e) {
            }
        }
    }

    private void changeData(String qrCodeString) {
        try {
            String ufc = Util.DecryptedBeneficiary(this, qrCodeString);
            ((Button) findViewById(R.id.btnScanQr)).setText(ufc);
        } catch (Exception e) {
            qrCode = "";
            Util.messageBar(this, "Invalid UFC");
        }
    }
 private void imageChange(String photoPath) {
        try {
            ImageView images = (ImageView)findViewById(R.id.rationImage);
            images.setVisibility(View.VISIBLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
            images.setImageBitmap(bitmap);
        } catch (Exception e) {

        }
    }

    private byte[] getImageFromAddress() {
        try {
            Log.e("Inside", "getImageFromAddress" + new Date().getTime());
            /*File imagefile = new File(address);*/
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inSampleSize = 2;
            options.inPurgeable = true;
            options.inPreferQualityOverSpeed = true;
            options.inTempStorage = new byte[32 * 1024];
            File file = new File(address);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
            byte[] bytes = stream.toByteArray();
           /* FileInputStream fis = new FileInputStream(imagefile);
            Bitmap bi = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bi.compress(Bitmap.CompressFormat.PNG, 50, baos);
            byte[] data = baos.toByteArray();*/
            Log.e("Inside", "get Image  Address" + new Date().getTime());
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    private void userDetails(BenefActivNewDto benefActivNewDto) {

        ((EditText) findViewById(R.id.edtOldFamilyCardNo)).setText(benefActivNewDto.getRationCardNumber());
        ((EditText) findViewById(R.id.edtOldFamilyCardNo)).setEnabled(false);

        ((Button) findViewById(R.id.btnSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar = new CustomProgressDialog(BeneficiarySubmissionActivity.this);
                progressBar.setCancelable(false);
                progressBar.show();
                submitData();
            }
        });

        ((Button) findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BeneficiarySubmissionActivity.this, SaleActivity.class));
                finish();
            }
        });
        ((EditText) findViewById(R.id.edtRegisteredMobileNo)).setText(benefActivNewDto.getMobileNum());
        ((EditText) findViewById(R.id.edtRegisteredMobileNo)).setEnabled(false);
        Util.setTamilText((TextView) findViewById(R.id.tvCardType), R.string.cardTypes);
        Util.setTamilText((TextView) findViewById(R.id.tvNoOfCylinder), "No of Cylinders");
        Util.setTamilText((TextView) findViewById(R.id.tvBeneficiaryActivation), R.string.beneficiaryActivate);
        Util.setTamilText((TextView) findViewById(R.id.btnSubmit), R.string.submit);
        Util.setTamilText((TextView) findViewById(R.id.btnCancel), R.string.cancel);
        List<String> cards = FPSDBHelper.getInstance(this).getCardType();
        Spinner mSpinnerCardType = (Spinner) findViewById(R.id.spinnerCardType);
        if (cards == null || cards.size() == 0) {
            cards = new ArrayList<>();
            cards.add("AAY");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cards.toArray(new String[cards.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCardType.setAdapter(adapter);
        String[] persons = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        Spinner spinnerAdult = (Spinner) findViewById(R.id.spinnerAdult);
        ArrayAdapter<String> adultAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, persons);
        adultAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerAdult.setAdapter(adultAdapt);
        Spinner spinnerChild = (Spinner) findViewById(R.id.spinnerChild);
        ArrayAdapter<String> childAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, persons);
        childAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerChild.setAdapter(childAdapt);

        Spinner spinnerCylindersType = (Spinner) findViewById(R.id.spinnerCylindersType);
        ArrayAdapter<String> cylinderAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.cylindersList));
        cylinderAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCylindersType.setAdapter(cylinderAdapt);
        if (benefActivNewDto.getNumOfCylinder() <= 2) {
            spinnerCylindersType.setSelection(benefActivNewDto.getNumOfCylinder());
        }
    }


    public void setEditTextValue(String header, int value) {

    }

    private void submitData() {
        try {
            BenefActivNewDto benefActivNewDto = new BenefActivNewDto();
            int childValue = ((Spinner) findViewById(R.id.spinnerChild)).getSelectedItemPosition();
            int adultValue = ((Spinner) findViewById(R.id.spinnerAdult)).getSelectedItemPosition();
            benefActivNewDto.setNumOfAdults(adultValue);
            benefActivNewDto.setNumOfChild(childValue);
            if(qrCode.trim().length()==0){
                 Util.messageBar(this,"Please Check UFC code");
                 return;
            }
          /*  if(address.trim().length()==0){
                Util.messageBar(this,"Please Check Ration card Image");
                return;
            }*/
            FPSDBHelper.getInstance(this).insertImageData(benefActivNewDto.getRationCardNumber(),address);
            benefActivNewDto.setEncryptedUfc(qrCode);
            benefActivNewDto.setMobileNum(((EditText) findViewById(R.id.edtRegisteredMobileNo)).getText().toString());
            benefActivNewDto.setRationCardNumber(((EditText) findViewById(R.id.edtOldFamilyCardNo)).getText().toString());
            Spinner spinNoCylinder = (Spinner) findViewById(R.id.spinnerCylindersType);
            benefActivNewDto.setNumOfCylinder(spinNoCylinder.getSelectedItemPosition());
            Spinner cardType = (Spinner) findViewById(R.id.spinnerCardType);
            String cards = cardType.getSelectedItem().toString().trim();
            String cardTypes = FPSDBHelper.getInstance(this).getCardType(cards);
            benefActivNewDto.setCardType(cardTypes.charAt(0));
            benefActivNewDto.setCardTypeDef(cardTypes);
            benefActivNewDto.setDeviceNum(Settings.Secure.getString(
                    getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
            TransactionBaseDto transaction = new TransactionBaseDto();
            transaction.setTransactionType(TransactionTypes.BENEFICIARY_ACTIVATION_NEW);
            if(NetworkUtil.getConnectivityStatus(this) == 0|| SessionId.getInstance().getSessionId().length()<=0){
                GlobalAppState.smsListener = this;
//                checkMessage();
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                com.omneagate.TransactionController.CardRegistration trans = CardRegistrationFactory.getTransaction(0);
                trans.process(this, transaction, benefActivNewDto);
            }else {
                transaction.setType("com.omneagate.rest.dto.BenefActivNewDto");
                transaction.setBaseDto(benefActivNewDto);
                String url = "/transaction/process";
                String login = new Gson().toJson(transaction);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_ACTIVATION,
                        SyncHandler, RequestType.POST, se, this);
            }
        } catch (Exception e) {
            Log.e("error", e.toString(), e);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        switch (what) {
            case CARD_ACTIVATION:
                responseRegistration(message);
                break;
            default:
                break;
        }

    }

    private void responseRegistration(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BenefActivNewDto benefActivNewDto = gson.fromJson(response, BenefActivNewDto.class);
            Log.e("Response", "Resp" + benefActivNewDto);
            if (benefActivNewDto.getStatusCode() == 0) {
                FPSDBHelper.getInstance(this).deleteCardDetails(benefActivNewDto.getRationCardNumber());
                DownloadDataProcessor downloadDataProcessor = new DownloadDataProcessor(this);
                downloadDataProcessor.processor();
                Intent intent = new Intent(this, SuccessFailureActivity.class);
                intent.putExtra("message", "Card Registered and activated Successfully");
                startActivity(intent);
                finish();
            } else {
                String data = FPSDBHelper.getInstance(this).retrieveLanguageTable(benefActivNewDto.getStatusCode(), GlobalAppState.language).getDescription();
                Util.messageBar(this, data);
                Intent intent = new Intent(this, SuccessFailureActivity.class);
                intent.putExtra("message", data);
                startActivity(intent);
                finish();
                Util.messageBar(this, data);
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }
}
