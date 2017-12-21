package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryUpdateDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.Util;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

public class UpdateConfirmUserDetailsActivity extends BaseActivity {

    BeneficiaryDto benef;

    BeneficiaryUpdateDto beneUpdate;

    String aregisterFlag = "", mobileFlag = "", aadharFlag = "";

//    AadharSeedingDto aadharSeedingDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_confirm_user_details);
        Util.LoggingQueue(this, " UpdateConfirmUserDetailsActivity", "onCreate called");


        String beneficiary = getIntent().getStringExtra("benef");
        String updateString = getIntent().getStringExtra("beneUpdate");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        benef = gson.fromJson(beneficiary, BeneficiaryDto.class);
        beneUpdate = gson.fromJson(updateString, BeneficiaryUpdateDto.class);

        Util.LoggingQueue(this, " UpdateConfirmUserDetailsActivity", "onCreate benef =  " + benef);
        Util.LoggingQueue(this, " UpdateConfirmUserDetailsActivity", "onCreate beneUpdate = " + beneUpdate);


        initialPage();
    }

    private void initialPage() {
//        aadharSeedingDto = new AadharSeedingDto();

        setUpPopUpPage();
        TextView rationCardNumber = (TextView) findViewById(R.id.ration_card_value);
        String cardNo = benef.getOldRationNumber();
//        String cardNumber = StringUtils.substring(cardNo, 0, 2) + "/" + StringUtils.substring(cardNo, 2, 3) + "/" + StringUtils.substring(cardNo, 3, 10);
        rationCardNumber.setText(cardNo);
//        ((TextView) findViewById(R.id.a_register_value)).setText(benef.getAregisterNum());
        if (StringUtils.isNotEmpty(beneUpdate.getMobileNumber())) {
            ((TextView) findViewById(R.id.mobileNumberUpdation)).setText(beneUpdate.getMobileNumber());
        } else if (StringUtils.isNotEmpty(benef.getMobileNumber())) {
            ((TextView) findViewById(R.id.mobileNumberUpdation)).setText(benef.getMobileNumber());
        }

        if (StringUtils.isNotEmpty(beneUpdate.getAregisterNumber())) {
            ((TextView) findViewById(R.id.cylinder_value)).setText(beneUpdate.getAregisterNumber());
        } else if (StringUtils.isNotEmpty(benef.getAregisterNum()) && !StringUtils.equalsIgnoreCase(benef.getAregisterNum(), "-1")) {
            ((TextView) findViewById(R.id.cylinder_value)).setText(benef.getAregisterNum());
        }

        if (StringUtils.isNotEmpty(beneUpdate.getAadhaarSeedingDto().getUid())) {
            ((TextView) findViewById(R.id.aadharNumber)).setText(beneUpdate.getAadhaarSeedingDto().getUid());
        } else if (StringUtils.isNotEmpty(benef.getFamilyHeadAadharNumber())) {
            ((TextView) findViewById(R.id.aadharNumber)).setText(benef.getFamilyHeadAadharNumber());
        }
//        ((TextView) findViewById(R.id.a_register_value)).setText(benef.getAregisterNum());
//        ((TextView) findViewById(R.id.cylinder_value)).setText(String.valueOf(benef.getAregisterNum()));
        ((TextView) findViewById(R.id.number_adult)).setText(String.valueOf(benef.getNumOfAdults()));
        ((TextView) findViewById(R.id.card_value)).setText(String.valueOf(benef.getNumOfCylinder()));
        ((TextView) findViewById(R.id.number_children)).setText(String.valueOf(benef.getNumOfChild()));
        String cardType = FPSDBHelper.getInstance(this).getCardTypeFromId(benef.getCardTypeId());
        ((TextView) findViewById(R.id.a_register_value)).setText(cardType);
        Util.setTamilText((TextView) findViewById(R.id.button_cancel), R.string.cancel);
        Util.setTamilText((TextView) findViewById(R.id.button_Submit), R.string.submit);
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.card_registration_confirm);
        Util.setTamilText((TextView) findViewById(R.id.number_aadhar), R.string.aadharNo);
        Util.setTamilText((TextView) findViewById(R.id.number_mobile), R.string.mobile_no);
        Util.setTamilText((TextView) findViewById(R.id.registration_text), R.string.check_details);
        Util.setTamilText((TextView) findViewById(R.id.ration_card_no), R.string.ration_card_number);
        Util.setTamilText((TextView) findViewById(R.id.noOfCylinderTitle), R.string.aRegisterNo);
        Util.setTamilText((TextView) findViewById(R.id.card_type), R.string.cylinderHints);
        Util.setTamilText((TextView) findViewById(R.id.a_reg_number), R.string.cardCap);
        Util.setTamilText((TextView) findViewById(R.id.number_adults), R.string.number_adult_cap);
        Util.setTamilText((TextView) findViewById(R.id.button_edit), R.string.edit);
        Util.setTamilText((TextView) findViewById(R.id.number_child), R.string.number_child_cap);
        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "Cancel called ... Moving to RationCardUpdateActivity");
                Intent intent = new Intent(UpdateConfirmUserDetailsActivity.this, RationCardUpdateActivity.class);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.button_Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((StringUtils.isEmpty(beneUpdate.getAregisterNumber())) && (StringUtils.isEmpty(beneUpdate.getMobileNumber())) && (StringUtils.isEmpty(beneUpdate.getAadhaarSeedingDto().getUid()))) {
                    noChange();
                } else {

                    checkValue();
                }
            }
        });

        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.button_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void checkValue() {
        try {
            Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "checkValue() called");


            if (NetworkUtil.getConnectivityStatus(this) == 0) {
                Util.messageBar(this, getString(R.string.no_connectivity));
            } else {
                findViewById(R.id.button_Submit).setOnClickListener(null);
                findViewById(R.id.button_Submit).setBackgroundColor(Color.LTGRAY);


                beneUpdate.setDeviceNumber(Settings.Secure.getString(
                        getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase());
                beneUpdate.setBeneficiaryId(benef.getId());
//              beneUpdate.getAadhaarSeedingDto().setBeneficiaryID(benef.getId());
              /*aadharSeedingDto.setBeneficiaryID(benef.getId());
              beneUpdate.setAadhaarSeedingDto(aadharSeedingDto);*/

                try {
                  if(GlobalAppState.language.equalsIgnoreCase("hi")) {
                        if (beneUpdate.getAregisterNumber() != null && StringUtils.isNotEmpty(beneUpdate.getAregisterNumber())) {
                            aregisterFlag = "अ-संख्या रजिस्टर ";
                        }
                        if (beneUpdate.getMobileNumber() != null && StringUtils.isNotEmpty(beneUpdate.getMobileNumber())) {
                            if (!aregisterFlag.equalsIgnoreCase("") && (beneUpdate.getAadhaarSeedingDto().getUid() != null && StringUtils.isNotEmpty(beneUpdate.getAadhaarSeedingDto().getUid()))) {
                                mobileFlag = " , मोबाइल नंबर";
                            } else if (!aregisterFlag.equalsIgnoreCase("") && (beneUpdate.getAadhaarSeedingDto().getUid() != null && StringUtils.isEmpty(beneUpdate.getAadhaarSeedingDto().getUid()))) {
                                mobileFlag = " और मोबाइल नंबर ";
                            } else {
                                mobileFlag = "मोबाइल नंबर ";
                            }
                        }
                        if (beneUpdate.getAadhaarSeedingDto().getUid() != null && StringUtils.isNotEmpty(beneUpdate.getAadhaarSeedingDto().getUid())) {
                            if ((!aregisterFlag.equalsIgnoreCase("")) || (!mobileFlag.equalsIgnoreCase(""))) {
                                aadharFlag = " और आधार संख्या ";
                            } else {
                                aadharFlag = "आधार संख्या ";
                            }
                        }
                    } else {
                        if (beneUpdate.getAregisterNumber() != null && StringUtils.isNotEmpty(beneUpdate.getAregisterNumber())) {
                            aregisterFlag = "Aregister number";
                        }
                        if (beneUpdate.getMobileNumber() != null && StringUtils.isNotEmpty(beneUpdate.getMobileNumber())) {
                            if (!aregisterFlag.equalsIgnoreCase("") && (beneUpdate.getAadhaarSeedingDto().getUid() != null && StringUtils.isNotEmpty(beneUpdate.getAadhaarSeedingDto().getUid()))) {
                                mobileFlag = " , Mobile number";
                            } else if (!aregisterFlag.equalsIgnoreCase("") && (beneUpdate.getAadhaarSeedingDto().getUid() != null && StringUtils.isEmpty(beneUpdate.getAadhaarSeedingDto().getUid()))) {
                                mobileFlag = " and Mobile number";
                            } else {
                                mobileFlag = "Mobile number";
                            }
                        }
                        if (beneUpdate.getAadhaarSeedingDto().getUid() != null && StringUtils.isNotEmpty(beneUpdate.getAadhaarSeedingDto().getUid())) {
                            if ((!aregisterFlag.equalsIgnoreCase("")) || (!mobileFlag.equalsIgnoreCase(""))) {
                                aadharFlag = " and Aadhar number";
                            } else {
                                aadharFlag = "Aadhar number";
                            }
                        }
                    }
                } catch (Exception e) {
                }

                String mobileAadharUpdate = new Gson().toJson(beneUpdate);

                Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity ", "mobileAadharUpdate ->" + mobileAadharUpdate);
                StringEntity stringEntity = new StringEntity(mobileAadharUpdate, HTTP.UTF_8);
                httpConnection = new HttpClientWrapper();
                String url = "/bene/update";
                Util.LoggingQueue(this, "Ration Card Registration", "Sending Benefeciary registration request to FPS server" + stringEntity);
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_REGISTRATION,
                        SyncHandler, RequestType.POST, stringEntity, this);
            }
        } catch (Exception e) {
            Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "checkValue() Exception = " + e);

        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity ", "processMessage() is called , message ->" + message);
        Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity ", "processMessage() is called , what ->" + what);

        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }


        try{
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto baseDto = gson.fromJson(response, BaseDto.class);
            Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity ", "processMessage() is called , baseDto ->" + baseDto);

            if(baseDto.getStatusCode() == 500){
                Util.messageBar(UpdateConfirmUserDetailsActivity.this, getString(R.string.connectionError));

            }

        }catch (Exception e){

        }



        switch (what) {
            case CARD_REGISTRATION:
                registrationSubmissionResponse(message);
                break;
            default:
                break;
            case ERROR_MSG:

                Util.messageBar(this, getString(R.string.connectionRefused));

                break;
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

    private void registrationSubmissionResponse(Bundle message) {
        Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity ", "registrationSubmissionResponse() message ->" + message);
        try {

            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e) {


            }



            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto beneficiaryDto = gson.fromJson(response, BaseDto.class);


            Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "registrationSubmissionResponse() beneficiaryDto = " + beneficiaryDto);


            if (beneficiaryDto.getStatusCode() == 0) {
                FPSDBHelper.getInstance(this).beneficiaryUpdate(beneUpdate);

                try {
                    FPSDBHelper.getInstance(this).beneficiaryMemberAadhar(beneUpdate.getAadhaarSeedingDto());
                } catch (Exception e) {
                }
                Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "registrationSubmissionResponse() 0  getStatusCode = " + beneficiaryDto.getStatusCode());

                Intent intent = new Intent(this, SuccessFailureUpdationActivity.class);
                intent.putExtra("error", String.valueOf(beneficiaryDto.getStatusCode()));
                intent.putExtra("aregister", aregisterFlag);
                intent.putExtra("mobile", mobileFlag);
                intent.putExtra("aadhar", aadharFlag);
                startActivity(intent);
                finish();
            } else if (beneficiaryDto.getStatusCode() == 449) {
                /** 11-07-2016
                 * MSFixes
                 * 449 Error Handled for Aadhar Number Registered For All Beneficiaries
                 *
                 */
                Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "registrationSubmissionResponse() 449 getStatusCode = " + beneficiaryDto.getStatusCode());

                Toast.makeText(getBaseContext(), R.string.all_aadhar_registered, Toast.LENGTH_LONG).show();

            } else if (beneficiaryDto.getStatusCode() == 7006) {
                Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "registrationSubmissionResponse() 7006 getStatusCode = " + beneficiaryDto.getStatusCode());

                Toast.makeText(getBaseContext(), R.string.aadhar_available, Toast.LENGTH_LONG).show();

            }  else if (beneficiaryDto.getStatusCode() == 12013) {
                Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "registrationSubmissionResponse() 12013 getStatusCode = " + beneficiaryDto.getStatusCode());
                Toast.makeText(getBaseContext(), R.string.aadhar_available_for_head, Toast.LENGTH_LONG).show();

            }else {

                Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "registrationSubmissionResponse() else getStatusCode = " + beneficiaryDto.getStatusCode());

                String messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(beneficiaryDto.getStatusCode()));
                if (messageData == null) {
                    messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(5037));
                }
                if (messageData.contains("~")) {
                    messageData = messageData.replace("~", beneUpdate.getAadhaarSeedingDto().getUid());
                }


                errorNavigation(messageData);
            }
        } catch (Exception e) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e1) {
            }
            Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "registrationSubmissionResponse() Exception = " + e);
            Util.messageBar(UpdateConfirmUserDetailsActivity.this, getString(R.string.connectionError));
        }

    }

    private void errorNavigation(String messages) {

        Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity", "errorNavigation() called");

        if (StringUtils.isEmpty(messages)) {
            messages = getString(R.string.genericDatabaseError);
        }
        Intent intent = new Intent(this, SuccessFailureUpdateActivity.class);
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    private void noChange() {

        Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity ", "noChange() called ->");


        Intent intent = new Intent(this, SuccessFailureUpdationActivity.class);
        try {
//                Util.messageBar(this, getString(R.string.updateionSuccess));
//                FPSDBHelper.getInstance(this).beneficiaryUpdate(beneUpdate);
            intent.putExtra("error", "1");
        } catch (Exception e) {
            intent.putExtra("error", String.valueOf(4000));
            Log.e("Error", e.toString(), e);
        }
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        Util.LoggingQueue(UpdateConfirmUserDetailsActivity.this, "UpdateConfirmUserDetailsActivity ", "onBackPressed() UpdateUserDetailsActivity called beneUpdate ->" + beneUpdate);

        Intent intent = new Intent(this, UpdateUserDetailsActivity.class);

        beneUpdate.setAadhaarSeedingDto(null);
        intent.putExtra("qrCode", benef.getOldRationNumber());
        intent.putExtra("beneUpdate", new Gson().toJson(beneUpdate));
        startActivity(intent);
        finish();
    }


}
