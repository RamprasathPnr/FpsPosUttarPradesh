package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.BeneficiarySalesTransaction;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.LocalDbRecoveryProcess;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.activity.SMSActivation.BeneficiaryCardActivationActivity;
import com.omneagate.activity.SMSActivation.CardRegistrationActivity;
import com.omneagate.activity.SMSActivation.CardWithOTPActivity;
import com.omneagate.activity.dialog.LogoutDialog;
import com.omneagate.service.ConnectionHeartBeat;
import com.omneagate.service.HttpClientWrapper;
import com.omneagate.service.RemoteLoggingService;
import com.omneagate.service.UpdateDataService;

import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;


public class SaleActivity extends BaseActivity {

    TransactionBaseDto transaction;//Transaction base for sending data to server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_scan);
        appState = (GlobalAppState) getApplication();
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpSalesPage();
    }


    /**
     * Set up data for sales
     * On click listener for buttons
     * Transaction base types set
     */
    private void setUpSalesPage() {
        Util.setTamilText((Button) findViewById(R.id.otpButton), R.string.scanWithOutOTP);
        Util.setTamilText((Button) findViewById(R.id.otpMobile), R.string.mobileOTP);
        Util.setTamilText((Button) findViewById(R.id.beneficiaryActivation), R.string.beneficiaryData);
        Util.setTamilText((Button) findViewById(R.id.button_entitlement), R.string.scanQR);
        Util.setTamilText((Button) findViewById(R.id.button_logout), R.string.logout);
        transaction = new TransactionBaseDto();
        transaction.setType("com.omneagate.rest.dto.QRRequestDto");
        ((Button) findViewById(R.id.button_entitlement)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.setTransactionType(TransactionTypes.SALE_QR_OTP_DISABLED);
                TransactionBase.getInstance().setTransactionBase(transaction);
                /*startActivity(new Intent(SaleActivity.this, CardWithOTPActivity.class));
                finish();*/
                launchQRScanner();
            }
        });

        ((Button) findViewById(R.id.otpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.setTransactionType(TransactionTypes.SALE_QR_OTP_AUTHENTICATION);
                TransactionBase.getInstance().setTransactionBase(transaction);
                if (networkConnection.isNetworkAvailable()) {
                    if (SessionId.getInstance().getSessionId().length() > 0) {
                        GlobalAppState.transactionType = 1;
                    } else {
                        GlobalAppState.transactionType = 0;
                    }
                } else {
                    GlobalAppState.transactionType = 0;
                }
                startActivity(new Intent(SaleActivity.this, QrCodeWithOTPActivity.class));
                finish();
            }
        });

        ((Button) findViewById(R.id.otpMobile)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.setTransactionType(TransactionTypes.SALE_RMN_AUTHENTICATE);
                TransactionBase.getInstance().setTransactionBase(transaction);
                if (networkConnection.isNetworkAvailable()) {
                    if (SessionId.getInstance().getSessionId().length() > 0) {
                        GlobalAppState.transactionType = 1;
                    } else {
                        GlobalAppState.transactionType = 0;
                    }
                } else {
                    GlobalAppState.transactionType = 0;
                }
                startActivity(new Intent(SaleActivity.this, RMNActivity.class));
                finish();
            }
        });

    }

    //Logout button onClick listener
    public void logMeOut(View view) {
        userLogoutResponse();
    }

    //Calling QR Scanner
    private void launchQRScanner() {
        IntentIntegrator.initiateScan(this, R.layout.activity_capture,
                R.id.viewfinder_view, R.id.preview_view, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Send FPS_ID and QRCode to get entitlement
     *
     * @params qrCode received from card
     */
    private void getEntitlement(String qrCodeString) {
        try {
            BeneficiarySalesTransaction beneficiary = new BeneficiarySalesTransaction(this);
            QRTransactionResponseDto qrCodeResponseReceived = beneficiary.getBeneficiaryDetails(qrCodeString);
            Log.e("Error", qrCodeResponseReceived.toString());
            if (beneficiary.getBeneficiaryDetails(qrCodeString) != null) {
                qrCodeResponseReceived.setMode('Q');
                EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                startActivity(new Intent(this, SalesEntryActivity.class));
                finish();
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        }
    }


    @Override
    public void onBackPressed() {
        userLogoutResponse();
    }

    //Concrete method
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case LOGOUT_USER:
                break;
            default:
                if (progressBar != null) {
                    progressBar.dismiss();
                }
                //   Util.messageBar(SaleActivity.this, message.getString(FPSDBConstants.RESPONSE_DATA));
                break;
        }

    }

    //Beneficiary card activation page navigation
    public void beneficiaryCardUpdate(View view) {
        if (networkConnection.isNetworkAvailable()) {
            GlobalAppState.transactionType = 0;
        } else {
            GlobalAppState.transactionType = 1;
        }
        startActivity(new Intent(this, BeneficiaryCardActivationActivity.class));
        finish();
    }


    /**
     * QR code response received
     * From activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                try {
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                            resultCode, data);
                    qrResponse(scanResult.getContents());
                } catch (Exception e) {
                    Log.e("Error", "E", e);
                }
                break;
            default:
                Log.e("Error", "E");
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
            getEntitlement(lines[0]);
        }
    }


    //After user give logout this method will call dialog
    private void userLogoutResponse() {
        LogoutDialog logout = new LogoutDialog(this);
        logout.show();

    }

    //Logout request from user success and send to server
    public void logOutSuccess() {
        SessionId.getInstance().setSessionId("");
        if (networkConnection.isNetworkAvailable()) {
            String url = "/login/logmeout";
            httpConnection = new HttpClientWrapper();
            httpConnection.sendRequest(url, null, ServiceListenerType.LOGOUT_USER,
                    SyncHandler, RequestType.GET, null, this);
        }
        FPSDBHelper.getInstance(this).closeConnection();
        Intent intent = new Intent(this, ConnectionHeartBeat.class);
        stopService(intent);
        stopService(new Intent(this, UpdateDataService.class));
        stopService(new Intent(this, RemoteLoggingService.class));
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.importDb:
                LocalDbRecoveryProcess localDbRecoveryProcess = new LocalDbRecoveryProcess(this);
                localDbRecoveryProcess.restoresDb();//From sdcard backup folder to database
                return true;
            case R.id.viewBill:
                startActivity(new Intent(this, BillActivity.class));
                return true;
            case R.id.retrieveDb:
                LocalDbRecoveryProcess localDbRecoveryPro = new LocalDbRecoveryProcess(this);
                localDbRecoveryPro.backupDb();
                return true;
           /* case R.id.loadLocalDB:
                try {
                    DownloadDataProcessor downloadDataProcessor = new DownloadDataProcessor(this);
                    downloadDataProcessor.processor();
                } catch (Exception e) {
                    Util.LoggingQueue(this, "Error", e.toString());
                    Log.e("Error", e.toString(), e);
                }
                return true;*/
            case R.id.loadFpsStock:
                startActivity(new Intent(this, FpsStockInwardActivity.class));
                return true;
           /*  case R.id.manualInwardStock:
                startActivity(new Intent(this, FpsManualInwardActivity.class));
                return true;*/

            case R.id.autoUpgradeVersion:
                startActivity(new Intent(this, AutoUpgrationActivity.class));
                return true;
            case R.id.transactionInOutBalance:
                startActivity(new Intent(this, TransactionCommodityActivity.class));
                return true;
            case R.id.stockCheck:
                startActivity(new Intent(this, StockCheckActivity.class));
                return true;

            case R.id.FpsIntentRequest:
                startActivity(new Intent(this, FpsIndentRequestActivity.class));
                return true;
/*
              case R.id.fpsPrint:
                  startActivity(new Intent(this, PrintActivity.class));
                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.changeLanguage(this, GlobalAppState.language);
        super.onSaveInstanceState(outState);
    }

    public void beneficiaryCardRegistration(View v) {
        startActivity(new Intent(this, CardRegistrationActivity.class));
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.databasemenu, menu);
        getLayoutInflater().setFactory(new LayoutInflater.Factory() {

            @Override
            public View onCreateView(String name, Context context,
                                     AttributeSet attrs) {
                if (name.equalsIgnoreCase("TextView")) {
                    try {
                        LayoutInflater f = getLayoutInflater();
                        final View view = f.createView(name, null, attrs);
                        new Handler().post(new Runnable() {
                            public void run() {
                                TextView text = (TextView)view.findViewById(view.getId());
                                Util.setTamilText(text,text.getText().toString());
                            }
                        });
                        return view;
                    } catch (InflateException e) {
                    } catch (ClassNotFoundException e) {
                    }
                }
                return null;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}
