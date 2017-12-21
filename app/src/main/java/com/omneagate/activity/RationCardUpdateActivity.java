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
import com.omneagate.DTO.TransactionBaseDto;
import com.omneagate.DTO.TransactionTypes;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.TransactionBase;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.RationCardListAdapter;
import com.omneagate.service.HttpClientWrapper;

import java.util.ArrayList;
import java.util.List;

//Beneficiary Activity to check Beneficiary Activation
public class RationCardUpdateActivity extends BaseActivity implements View.OnClickListener {

    TransactionBaseDto transaction;          //Transaction base DTO

    String number = "";

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
        Util.LoggingQueue(this, "RationCardUpdateActivity", "Setting up setUpInitialScreen");
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Util.setTamilText((TextView) findViewById(R.id.card_num_search), R.string.card_number_input);
        Util.setTamilText((TextView) findViewById(R.id.select_ration_card_number), R.string.select_ration_card_number);
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.updateRationCard));
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


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            default:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                }
                catch(Exception e) {}
                break;
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, BeneficiaryMenuActivity.class));
        Util.LoggingQueue(this, "RationCardUpdateActivity", "On Back pressed Called");
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
                number = "";
                setText();
                break;
            default:
                break;
        }

    }

    private void removeNumber() {
        if (number.length() > 0) {
            number = number.substring(0, number.length() - 1);
        } else {
            number = "";
        }
        setText();
    }

    private void addNumber(String text) {
        try {
            if (number.length() >= 4) {
                return;
            }
            number = number + text;
            setText();
        } catch (Exception e) {
            Log.e("RationCardUpdateActiv", e.toString(), e);
        }
    }

    private void setText() {
        suffixCard.setText(number);
        Util.LoggingQueue(this, "RationCardUpdateActivity", "Entering the number:" + number);
        if (number.length() == 4) {
            new BeneficiarySearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, number);
        } else {
            List<BeneficiaryDto> benef = new ArrayList<BeneficiaryDto>();
            findViewById(R.id.noRecordsFound).setVisibility(View.GONE);
            rationCardListAdapter = new RationCardListAdapter(this, benef);
            listViewSearch.setAdapter(rationCardListAdapter);
        }
    }

    private class BeneficiarySearchTask extends AsyncTask<String, Void, List<BeneficiaryDto>> {

        @Override
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(RationCardUpdateActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Progress Bar", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<BeneficiaryDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(RationCardUpdateActivity.this).retrieveAllBeneficiary(number);
        }

        // can use UI thread here
        protected void onPostExecute(final List<BeneficiaryDto> beneficiary) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
            catch(Exception e) {}

            if (beneficiary != null && beneficiary.size() > 0) {
                Util.LoggingQueue(RationCardUpdateActivity.this, "Ration card Sales....//", "Beneficiary Length:" + beneficiary.size());
                listViewSearch.setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.noRecordsFound)).setVisibility(View.GONE);
                rationCardListAdapter = new RationCardListAdapter(RationCardUpdateActivity.this, beneficiary);
                listViewSearch.setAdapter(rationCardListAdapter);
                listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listViewSearch.setOnItemClickListener(null);
                        Log.e("Ration card update///","isActive..."+beneficiary.get(position).isActive()+" , "+beneficiary.get(position).getOldRationNumber());
                        if(beneficiary.get(position).isActive()) {
                            updatePage(beneficiary.get(position).getOldRationNumber());
                        }
                        else {
                            Util.messageBar(RationCardUpdateActivity.this, getString(R.string.beneficiary_blocked));
                            return;
                        }
                    }
                });
            } else {
                Util.LoggingQueue(RationCardUpdateActivity.this, "Ration card Sales///", "Beneficiary Length is Zero");
                listViewSearch.setVisibility(View.GONE);
                ((TextView) findViewById(R.id.noRecordsFound)).setVisibility(View.VISIBLE);
                Util.setTamilText(((TextView) findViewById(R.id.noRecordsFound)), getString(R.string.noRecordsFound));
            }
        }
    }

    private void updatePage(String RationCardNumber){
        Log.e("RationCardUpdate","RationCardNumber..."+RationCardNumber);
        Intent intent = new Intent(this,UpdateUserDetailsActivity.class);
        intent.putExtra("qrCode",RationCardNumber);
        startActivity(intent);
        finish();
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

}