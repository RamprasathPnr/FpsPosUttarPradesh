package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.zxing.client.android.Intents;
import com.omneagate.DTO.EnumDTO.RoleFeature;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.RoleFeatureDto;
import com.omneagate.DTO.RollMenuDto;
import com.omneagate.Util.BeneficiarySalesQRTransaction;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.fpsRollViewAdpter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SaleOrderActivity extends BaseActivity {
    final ArrayList<String> fpsRoleName = new ArrayList<String>();
    GridView fps_rollview;
    List<RollMenuDto> roleMenuDto = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sale_order);
        Log.e("SalesType-SaleOrderActivity", "onCreate");
        rollViews();
        setUpSaleOrders();
    }

    private void rollViews() {
        long id = FPSDBHelper.getInstance(this).retrieveId("SALES_ORDER_MENU");
        List<RoleFeatureDto> retriveRollFeature = new ArrayList<>();
        Log.e("SaleOrderActivity", "rollViews");

        retriveRollFeature = FPSDBHelper.getInstance(this).retrieveSalesOrderData(id, SessionId.getInstance().getUserId());
        int rollFeatureSize = retriveRollFeature.size();
        Log.e("rollFeatureSize", ""+rollFeatureSize);
        for (int i = 0; i < rollFeatureSize; i++) {
            String roll_Name = retriveRollFeature.get(i).getRollName();
            Log.e("rollViews roll_Name", ""+roll_Name);

            try {
                RoleFeature rolls = RoleFeature.valueOf(roll_Name);
                roleMenuDto.add(new RollMenuDto(getString(rolls.getRollName()), rolls.getBackground(), rolls.getColorCode(), rolls.getDescription()));
                fpsRoleName.add(roll_Name);
            } catch (Exception e) {

            }
        }


        Log.e("rollViews retrive_arr_feature", "" + fpsRoleName);
        fps_rollview = (GridView) findViewById(R.id.fpsroll);
        fps_rollview.setAdapter(new fpsRollViewAdpter(this, roleMenuDto));
        fps_rollview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if(roleMenuDto.get(i).getClassName().equalsIgnoreCase("QRCodeSalesActivity")) {
                        Util.LoggingQueue(com.omneagate.activity.SaleOrderActivity.this, "rollViews if SaleActivity", "Moving to Sale Order activity");
                        launchQRScanner();
                    }
                    else {

                        Util.LoggingQueue(com.omneagate.activity.SaleOrderActivity.this, "rollViews else SaleActivity", "Moving to Sale Order activity");
                        String myClass = "com.omneagate.activity." + roleMenuDto.get(i).getClassName();
                        Log.e("rollViews myClass", ""+myClass);
                        Intent myIntent = new Intent(getApplicationContext(), Class.forName(myClass));
                        startActivity(myIntent);
                        finish();
                    }
                } catch (Exception e) {}
            }
        });

    }

    /**
     * Initial setUp
     */
    private void setUpSaleOrders() {
        Util.LoggingQueue(this, "->Saleorderactivity", "->Setting up sales order activity");
        setUpPopUpPage();
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sales_order);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /*
    *  Mobile OTP page calling
    * */
    private void mobileOTP() {
        Util.LoggingQueue(this, "Sale order activity", "Mobile otp called");
        startActivity(new Intent(this, MobileOTPOptionsActivity.class));
        finish();
    }


    /*
    *
    * Qr code scan by user
    * if OTP enabled or not
    *
    * */
    private void qrCodeDataScan() {
        Util.LoggingQueue(this, "->Saleorderactivity", "->Qr code sales activity called");
       // startActivity(new Intent(this, QRCodeSalesActivity.class));
        finish();
    }


    /**
     * Called when user press back button
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        Util.LoggingQueue(this, "Sale order activity", "Back button pressed");
        finish();
    }

    //Concrete method
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {

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



    private void launchQRScanner() {
        /*autoFocusHandler = new Handler();
        mCamera = getCameraInstance();
        *//* Instance barcode scanner *//*
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);*/

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
                        Log.e("EncryptedUFC",contents);
                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(this, "QRcode sales", "QR exception called:" + e.toString());
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                 //   Log.e(QRCodeSalesActivity.class.getSimpleName(),"Scan cancelled");
                }

                break;

            default:
                break;
        }
    }


    //Response from QR reader
    private void qrResponse(String result) {
        String languageCode = FPSDBHelper.getInstance(this).getMasterData("language");
        Util.changeLanguage(this, languageCode);
        GlobalAppState.language = languageCode;
        if (result == null) {
            Util.LoggingQueue(this, "Result null", "Back page");
            startActivity(new Intent(this, SaleOrderActivity.class));
            finish();
        } else {
            String qrCode = Util.DecryptedBeneficiary(this, result);
            if (StringUtils.isEmpty(qrCode)) {
                Util.LoggingQueue(this, "QRcode invalid", "back page called");
                startActivity(new Intent(this, SaleOrderActivity.class));
                finish();
                return;
            }
            String lines[] = result.split("\\r?\\n");
            getEntitlement(lines[0]);
        }
    }

    /**
     * Send FPS_ID and QRCode to get entitlement
     *
     * @params qrCode received from card
     */
    private void getEntitlement(String qrCodeString) {
        try {
            BeneficiarySalesQRTransaction beneficiary = new BeneficiarySalesQRTransaction(this);
            Log.e("QRCodeSalesActivity", "BeneficiaryDto..."+beneficiary.toString());
            QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeString);
            Util.LoggingQueue(this, "Entitlement", "Calculating entitlement "+qrCodeResponseReceived);
            if (qrCodeResponseReceived != null)
                Log.e("RationCardSalesActivity", "QRTransactionResponseDto..." + qrCodeResponseReceived.toString());
            if (beneficiary.getBeneficiaryDetails(qrCodeString) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
//                qrCodeResponseReceived.setMode('Q');
                qrCodeResponseReceived.setMode('A');
                EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                /*if (SessionId.getInstance().isQrOTPEnabled()) {
                    getOTPFromServer(qrCodeString);
                } else {*/
                    /*startActivity(new Intent(this, SalesEntryActivity.class));
                    finish();*/
                Intent intent = new Intent(this, SalesEntryActivity.class);
                intent.putExtra("SaleType", "QrCodeSale");
                startActivity(intent);
                finish();
//                }
            } else {
                errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.getStackTrace().toString());
            Log.e("QRCodeSalesActivity", e.toString(), e);
            errorNavigation(getString(R.string.qrCodeInvalid));
        }
    }

    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Intent intent = new Intent(this, SuccessFailureSalesActivity.class);
        Util.LoggingQueue(this, "Error in QRcode", "Navigating to error page");
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }
}