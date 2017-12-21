package com.omneagate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.BeneficiarySalesTransaction;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.RationCardListAdapter;
import com.omneagate.service.HttpClientWrapper;

import java.util.ArrayList;
import java.util.List;

//Beneficiary Activity to check Beneficiary Activation
public class RationCardSalesActivity extends BaseActivity implements View.OnClickListener {
    TransactionBaseDto transaction;          //Transaction base DTO
    String rationCardNumberInput = "";
    TextView suffixCard;
    ListView listViewSearch;
    RationCardListAdapter rationCardListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.ration_card_search);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        appState = (GlobalAppState) getApplication();
        transaction = new TransactionBaseDto();
        setUpInitialScreen();
    }

    private void setUpInitialScreen() {
        setUpPopUpPage();
        suffixCard = (TextView) findViewById(R.id.thirdText);
        Util.LoggingQueue(this, "RationCardSalesActivity", "setUpInitialScreen");
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Util.setTamilText((TextView) findViewById(R.id.card_num_search), R.string.card_number_input);
        Util.setTamilText((TextView) findViewById(R.id.select_ration_card_number), R.string.select_ration_card_number);
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sale_entry_activity);
        transaction.setType("com.omneagate.rest.dto.QRRequestDto");
        findViewById(R.id.button_one).setOnClickListener(this);
        findViewById(R.id.button_two).setOnClickListener(this);
        findViewById(R.id.button_three).setOnClickListener(this);
        findViewById(R.id.button_four).setOnClickListener(this);
        findViewById(R.id.button_five).setOnClickListener(this);
        findViewById(R.id.button_six).setOnClickListener(this);
        findViewById(R.id.button_seven).setOnClickListener(this);
        findViewById(R.id.button_eight).setOnClickListener(this);
        findViewById(R.id.button_nine).setOnClickListener(this);
        findViewById(R.id.button_zero).setOnClickListener(this);
        findViewById(R.id.button_bkSp).setOnClickListener(this);
        List<BeneficiaryDto> benef = new ArrayList<BeneficiaryDto>();
        rationCardListAdapter = new RationCardListAdapter(this, benef);
        listViewSearch = (ListView) findViewById(R.id.listView_search);
        listViewSearch.setAdapter(rationCardListAdapter);
        transaction.setTransactionType(TransactionTypes.SALE_QR_OTP_DISABLED);
        TransactionBase.getInstance().setTransactionBase(transaction);
    }

    /**
     * Send FPS_ID and QRCode to get entitlement
     *
     * @params qrCode received from card
     */
    private void getEntitlement(String rationCardNumber) {
        progressBar = new CustomProgressDialog(this);
        try {
            Util.LoggingQueue(RationCardSalesActivity.this, "RationCardSalesActivity", "getEntitlement() for rationCardNumber->" + rationCardNumber);
            progressBar.show();
            BeneficiaryDto benef = FPSDBHelper.getInstance(this).beneficiaryFromOldCard(rationCardNumber);
            //  Log.e("getEntitlement()", "BeneficiaryDto..."+benef.toString());
            if (benef != null) {
                BeneficiarySalesTransaction beneficiarySalesTransaction = new BeneficiarySalesTransaction(this);
                //  Util.LoggingQueue(this, "getEntitlement()", "Calculating entitlement");
                QRTransactionResponseDto qrCodeResponseReceived = beneficiarySalesTransaction.getBeneficiaryDetails(rationCardNumber);
                // change entitlement according to  nfsa rule

                if (qrCodeResponseReceived != null)
                    //    Log.e("getEntitlement()", "QRTransactionResponseDto..." + qrCodeResponseReceived.toString());
                    if (beneficiarySalesTransaction.getBeneficiaryDetails(rationCardNumber) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() > 0) {
                        NetworkConnection network = new NetworkConnection(this);
                        if (network.isNetworkAvailable()) {
                            qrCodeResponseReceived.setMode('D');
                        } else {
                            qrCodeResponseReceived.setMode('F');
                        }
                        Util.LoggingQueue(com.omneagate.activity.RationCardSalesActivity.this, "Ration card Sales", "Moving to Sales Entry Page");
                        qrCodeResponseReceived.setRegistered(true);
                        EntitlementResponse.getInstance().setQrcodeTransactionResponseDto(qrCodeResponseReceived);
                    /*startActivity(new Intent(this, SalesEntryActivity.class));
                    finish();*/
                        Intent intent = new Intent(this, SalesEntryActivity.class);
                        intent.putExtra("SaleType", "RationCardSale");
                        startActivity(intent);
                        finish();
                    } else if (beneficiarySalesTransaction.getBeneficiaryDetails(rationCardNumber) != null && qrCodeResponseReceived.getEntitlementList() != null && qrCodeResponseReceived.getEntitlementList().size() == 0) {
                        Util.LoggingQueue(com.omneagate.activity.RationCardSalesActivity.this, "Ration card Sales", "Beneficiary Entitlement details not available");
                        errorNavigation(getString(R.string.entitlemnt_finished));
                    } else {
                        Util.LoggingQueue(com.omneagate.activity.RationCardSalesActivity.this, "Ration card Sales", "Beneficiary Entitlement details not available");
                        errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
                    }
            } else {
                Util.LoggingQueue(com.omneagate.activity.RationCardSalesActivity.this, "Ration card Sales", "Beneficiary Data is not available in db");
                errorNavigation(getString(R.string.fpsBeneficiaryMismatch));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.getStackTrace().toString());
            Log.e("RationCardSalesActivity", e.toString(), e);
            errorNavigation(getString(R.string.invalid_card_no));
        } finally {
            if (progressBar != null)
                progressBar.dismiss();
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            default:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                } catch (Exception e) {
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleOrderActivity.class));
        Util.LoggingQueue(this, "Ration card Sales", "On Back pressed Called");
        finish();
    }

    //Handler for 5 secs
    private void errorNavigation(String messages) {
        Intent intent = new Intent(this, SuccessFailureSalesActivity.class);
        Util.LoggingQueue(this, "Error in QRcode", "Navigating to error page");
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_one:
                addNumber("1");
                break;
            case R.id.button_two:
                addNumber("2");
                break;
            case R.id.button_three:
                addNumber("3");
                break;
            case R.id.button_four:
                addNumber("4");
                break;
            case R.id.button_five:
                addNumber("5");
                break;
            case R.id.button_six:
                addNumber("6");
                break;
            case R.id.button_seven:
                addNumber("7");
                break;
            case R.id.button_eight:
                addNumber("8");
                break;
            case R.id.button_nine:
                addNumber("9");
                break;
            case R.id.button_zero:
                addNumber("0");
                break;
            case R.id.button_bkSp:
                removeNumber();
                break;
            case R.id.imageView5:
                rationCardNumberInput = "";
                setText();
                break;
            default:
                break;
        }
    }

    private void removeNumber() {
        if (rationCardNumberInput.length() > 0) {
            rationCardNumberInput = rationCardNumberInput.substring(0, rationCardNumberInput.length() - 1);
        } else {
            rationCardNumberInput = "";
        }
        setText();
    }

    private void addNumber(String text) {
        try {
            if (rationCardNumberInput.length() >= 4) {
                return;
            }
            rationCardNumberInput = rationCardNumberInput + text;
            setText();
        } catch (Exception e) {
            Log.e("RationCardSalesActivity", e.toString(), e);
        }
    }

    private void setText() {
        suffixCard.setText(rationCardNumberInput);
        Util.LoggingQueue(this, "Ration card Sales...", "Entering the number:" + rationCardNumberInput);
        if (rationCardNumberInput.length() == 4) {
            new BeneficiarySearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, rationCardNumberInput);
        } else {
            List<BeneficiaryDto> benef = new ArrayList<BeneficiaryDto>();
            ((TextView) findViewById(R.id.noRecordsFound)).setVisibility(View.GONE);
            rationCardListAdapter = new RationCardListAdapter(this, benef);
            listViewSearch.setAdapter(rationCardListAdapter);
        }
    }

    private class BeneficiarySearchTask extends AsyncTask<String, Void, List<BeneficiaryDto>> {
        @Override
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.RationCardSalesActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Progress Bar", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<BeneficiaryDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.RationCardSalesActivity.this).retrieveAllBeneficiary(rationCardNumberInput);
        }

        // can use UI thread here
        protected void onPostExecute(final List<BeneficiaryDto> beneficiary) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e) {
            }
            if (beneficiary != null && beneficiary.size() > 0) {
                Util.LoggingQueue(com.omneagate.activity.RationCardSalesActivity.this, "Ration card Sales...", "Beneficiary Length:" + beneficiary.size());
                listViewSearch.setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.noRecordsFound)).setVisibility(View.GONE);
                rationCardListAdapter = new RationCardListAdapter(com.omneagate.activity.RationCardSalesActivity.this, beneficiary);
                listViewSearch.setAdapter(rationCardListAdapter);
                listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listViewSearch.setOnItemClickListener(null);
                        // Checking card block status
                        int active = FPSDBHelper.getInstance(RationCardSalesActivity.this).checkBlockStatus(beneficiary.get(position).getOldRationNumber());
                        if (active == 0) {
                            Util.LoggingQueue(com.omneagate.activity.RationCardSalesActivity.this, "Ration card Sales....", "Beneficiary card is blocked");
                            errorNavigation(getString(R.string.beneficiary_blocked));
                        } else {
                            Util.LoggingQueue(com.omneagate.activity.RationCardSalesActivity.this, "RationCardSalesActivity", "Selected Ration Card Number is : "
                                    + beneficiary.get(position).getOldRationNumber());
                            getEntitlement(beneficiary.get(position).getOldRationNumber());
                        }
                    }
                });
            } else {
                Util.LoggingQueue(com.omneagate.activity.RationCardSalesActivity.this, "Ration card Sales...", "Beneficiary Length is Zero");
                listViewSearch.setVisibility(View.GONE);
                ((TextView) findViewById(R.id.noRecordsFound)).setVisibility(View.VISIBLE);
                Util.setTamilText(((TextView) findViewById(R.id.noRecordsFound)), getString(R.string.noRecordsFound));
            }
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

//    private void check_card(BeneficiaryDto benef_dto, QRTransactionResponseDto qrCodeResponseReceived) {
//        List<EntitlementDTO> list;
//        if (mastercheck(benef_dto.getId()) && checkcardtype(benef_dto) /*&& benef_member.size() > 3*/) {
//            list = qrCodeResponseReceived.getEntitlementList();
//            for (int i = 0; i < list.size(); i++) {
//                if (checkgroupIdtype(list.get(i).getGroupId())) {
//                    double old_entitle_qty = list.get(i).getEntitledQuantity();
//                    int members = benef_dto.getNumOfAdults() + benef_dto.getNumOfChild();
//                    double new_entitle_qty = (double) members * 5;
//                    if (new_entitle_qty > old_entitle_qty) {
//                        list.get(i).setEntitledQuantity(new_entitle_qty);
//                        double nfsa_purchasedQuantity = list.get(i).getNfsa_purchasedQuantity();
//                        list.get(i).setPurchasedQuantity(new_entitle_qty >= nfsa_purchasedQuantity ? nfsa_purchasedQuantity : new_entitle_qty);
//                        double currentQty = new_entitle_qty - nfsa_purchasedQuantity;
//                        list.get(i).setCurrentQuantity(currentQty >= 0 ? currentQty : 0);
//                    }
//                    Log.e("check_card", "member =" + members + "old_entitle =" + old_entitle_qty + "new_entitle_qty =" + new_entitle_qty);
//                }
//            }
//        }
//    }
//
//    private boolean mastercheck(long benef_id) {
//        NfsaPosDataDto dto = FPSDBHelper.getInstance(this).get_nfsaStatus(benef_id);
//        if (dto != null && dto.getNfsaStatus() != null) {
//            return dto.getNfsaStatus();
//        }
//        return false;
//    }
//
//    private boolean checkcardtype(BeneficiaryDto list) {
//        String cardid[] = {"3", "13", "14"};
//        for (int i = 0; i < cardid.length; i++) {
//            Log.e("getCardTypeId", "" + list.getCardTypeId());
//            if (list.getCardTypeId().equals(cardid[i])) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private boolean checkgroupIdtype(long id) {
//        long groupId[] = {1, 12}; //production
//        Log.e("group id", "" + id);
//        for (int i = 0; i < groupId.length; i++) {
//            if (id == groupId[i]) {
//                return true;
//            }
//        }
//        return false;
//    }
}