package com.omneagate.activity;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.RationcardSummaryDto;
import com.omneagate.DTO.UnitwiseSummaryDto;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.PullToRefresh.LoadMoreListView;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.RationcardSummaryListAdapter;
import com.omneagate.activity.dialog.UnitwiseListAdapter;
import com.omneagate.service.HttpClientWrapper;

import java.util.ArrayList;
import java.util.List;

public class RationCardSummaryReportActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

    LoadMoreListView listView, unitListView;

    RationcardSummaryListAdapter adapter;

    UnitwiseListAdapter adapter2;

    RelativeLayout keyBoardCustom;

    KeyboardView keyview;

    KeyBoardEnum keyBoardFocused;

    int loadMore = 0;

//    public int one, oneHalf, two, twoHalf, three, totalValue;

    List<RationcardSummaryDto> beneficiarySearchDtos;

    List<UnitwiseSummaryDto> unitwiseSummaryDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_rationcard_summary_report);
        Util.LoggingQueue(RationCardSummaryReportActivity.this, "RationCardSummaryReportActivity ", "onCreate() Called");

        appState = (GlobalAppState) getApplication();
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpInitialPage();
    }

    private void setUpInitialPage() {
        /*Util.oneTotal = 0;
        Util.oneHalfTotal = 0;
        Util.twoTotal = 0;
        Util.twoHalfTotal = 0;
        Util.threeTotal = 0;
        Util.totalTotal = 0;*/

        //Util.LoggingQueue(this, "Beneficiary List", "Setting up main page");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Util.setTamilText((TextView) findViewById(R.id.top_textView), getString(R.string.card_details));
        Util.setTamilText((TextView) findViewById(R.id.rationCardTypesTv), R.string.ration_card_types);
        Util.setTamilText((Button) findViewById(R.id.back_pressed), R.string.close);
        Util.setTamilText((Button) findViewById(R.id.detailsSummaryButton), R.string.details_summary);
        Util.setTamilText((Button) findViewById(R.id.unitsSummaryButton), R.string.units_summary);
        Util.setTamilText((TextView) findViewById(R.id.totalBeneficiariesLabel), R.string.total_beneficiaries_title);
        Util.setTamilText((TextView) findViewById(R.id.mobileRegLabel), R.string.mobile_reg_title);
        Util.setTamilText((TextView) findViewById(R.id.aadharRegLabel), R.string.aadhar_reg_title);
        Util.setTamilText((TextView) findViewById(R.id.totalAadharCount), R.string.total_aadhar_reg_title);

        Util.setTamilText((TextView) findViewById(R.id.total_cards), R.string.total_cards);
        ((TextView) findViewById(R.id.total_cards_value)).setText(String.valueOf(FPSDBHelper.getInstance(this).getBeneficiaryCount()));
        ((TextView) findViewById(R.id.adultCountTv)).setText(String.valueOf(FPSDBHelper.getInstance(this).getAdultCount()));
        ((TextView) findViewById(R.id.childCountTv)).setText(String.valueOf(FPSDBHelper.getInstance(this).getChildCount()));
        int totalCount = (FPSDBHelper.getInstance(this).getAdultCount()) + (FPSDBHelper.getInstance(this).getChildCount());
        ((TextView) findViewById(R.id.totalCountTv)).setText(String.valueOf(totalCount));
        ((TextView) findViewById(R.id.mobileYesTv)).setText(String.valueOf(FPSDBHelper.getInstance(this).getMobileYesCount()));
        ((TextView) findViewById(R.id.mobileNoTv)).setText(String.valueOf(FPSDBHelper.getInstance(this).getMobileNoCount()));
        ((TextView) findViewById(R.id.aadharYesTv)).setText(String.valueOf(FPSDBHelper.getInstance(this).getAadharYesCount()));
        ((TextView) findViewById(R.id.totalaadharYesTv)).setText(String.valueOf(FPSDBHelper.getInstance(this).getTotalAadharYesCount()));

       // Util.LoggingQueue(RationCardSummaryReportActivity.this, "RationCardSummaryReportActivity ", "((TextView) findViewById(R.id.aadharYesTv))"+((TextView) findViewById(R.id.aadharYesTv)).getText().toString());
        ((TextView) findViewById(R.id.aadharNoTv)).setText(String.valueOf(FPSDBHelper.getInstance(this).getAadharNoCount()));

        ((TextView) findViewById(R.id.cardType)).setText(getString(R.string.card_type));
        ((TextView) findViewById(R.id.oneUnit)).setText(getString(R.string.one_unit));
        ((TextView) findViewById(R.id.oneHalfUnit)).setText(getString(R.string.one_half_unit));
        ((TextView) findViewById(R.id.twoUnit)).setText(getString(R.string.two_unit));
        ((TextView) findViewById(R.id.twoHalfUnit)).setText(getString(R.string.two_half_unit));
        ((TextView) findViewById(R.id.threeUnit)).setText(getString(R.string.three_unit));
        ((TextView) findViewById(R.id.total)).setText(getString(R.string.total));





        keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
        keyBoardCustom.setVisibility(View.GONE);
        setUpPopUpPage();
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.back_pressed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ((RelativeLayout) findViewById(R.id.back_button_layout)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.detailsSummaryLayout)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.unitsSummaryLayout)).setVisibility(View.INVISIBLE);
        ((Button) findViewById(R.id.detailsSummaryButton)).setBackgroundResource(R.color.challanColor);
        ((Button) findViewById(R.id.unitsSummaryButton)).setBackgroundResource(R.color.gray_1);
        ((TextView) findViewById(R.id.total_cards)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.total_cards_value)).setVisibility(View.VISIBLE);

        findViewById(R.id.detailsSummaryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.detailsSummaryLayout)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.unitsSummaryLayout)).setVisibility(View.INVISIBLE);
                ((Button) findViewById(R.id.detailsSummaryButton)).setBackgroundResource(R.color.challanColor);
                ((Button) findViewById(R.id.unitsSummaryButton)).setBackgroundResource(R.color.gray_1);
                ((TextView) findViewById(R.id.total_cards)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.total_cards_value)).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.unitsSummaryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.detailsSummaryLayout)).setVisibility(View.INVISIBLE);
                ((LinearLayout) findViewById(R.id.unitsSummaryLayout)).setVisibility(View.VISIBLE);
                ((Button) findViewById(R.id.detailsSummaryButton)).setBackgroundResource(R.color.gray_1);
                ((Button) findViewById(R.id.unitsSummaryButton)).setBackgroundResource(R.color.challanColor);
                ((TextView) findViewById(R.id.total_cards)).setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.total_cards_value)).setVisibility(View.INVISIBLE);
            }
        });

        listView = (LoadMoreListView) findViewById(R.id.listView);
        beneficiarySearchDtos = new ArrayList<>();
        adapter = new RationcardSummaryListAdapter(com.omneagate.activity.RationCardSummaryReportActivity.this, beneficiarySearchDtos);
        listView.setAdapter(adapter);
        searchAdapter();

        unitListView = (LoadMoreListView) findViewById(R.id.unitWiseListView);
        unitwiseSummaryDto = new ArrayList<>();
        adapter2 = new UnitwiseListAdapter(com.omneagate.activity.RationCardSummaryReportActivity.this, unitwiseSummaryDto);
        unitListView.setAdapter(adapter2);
        searchAdapter2();



    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }

    private void keyBoardAppear() {
        Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
        keyview.setKeyboard(keyboard);
        keyview.setPreviewEnabled(false);
        keyview.setOnKeyboardActionListener(new KeyList());
    }

    private void searchAdapter() {
        /*Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
        //create KeyboardView object
        keyview = (KeyboardView) findViewById(R.id.customkeyboard);
        //attache the keyboard object to the KeyboardView object
        keyview.setKeyboard(keyboard);
        //show the keyboard
        keyview.setVisibility(KeyboardView.VISIBLE);
        keyview.setPreviewEnabled(false);
        //take the keyboard to the front
        keyview.bringToFront();
        //register the keyboard to receive the key pressed
        keyview.setOnKeyboardActionListener(new KeyList());
        keyBoardFocused = KeyBoardEnum.AREGISTER;*/
        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore++;
                Log.e("LoadMore", "loading");
                new CardTypeCountTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
//        keyBoardCustom.setVisibility(View.GONE);
        changeAdapter();
    }

    private void searchAdapter2() {
        /*Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
        //create KeyboardView object
        keyview = (KeyboardView) findViewById(R.id.customkeyboard);
        //attache the keyboard object to the KeyboardView object
        keyview.setKeyboard(keyboard);
        //show the keyboard
        keyview.setVisibility(KeyboardView.VISIBLE);
        keyview.setPreviewEnabled(false);
        //take the keyboard to the front
        keyview.bringToFront();
        //register the keyboard to receive the key pressed
        keyview.setOnKeyboardActionListener(new KeyList());
        keyBoardFocused = KeyBoardEnum.AREGISTER;*/
        listView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore++;
                Log.e("LoadMore", "loading");
                new unitWiseCountTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
//        keyBoardCustom.setVisibility(View.GONE);
        changeAdapter2();
    }

    private void changeAdapter() {
        try {
            new CardTypeCountTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Util.LoggingQueue(this, "BeneficiaryListActivity", "Error in search Invalid value entered");
        }
    }

    private void changeAdapter2() {
        try {
            new unitWiseCountTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Util.LoggingQueue(this, "BeneficiaryListActivity", "Error in search Invalid value entered");
        }
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        switch (what) {
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(this, BeneficiaryListActivity.class));
        Util.LoggingQueue(this, "Beneficiary List", "Back pressed");
        finish();
    }

    public void onViewFullDetailsPressed() {
        startActivity(new Intent(this, BeneficiaryMenuActivity.class));
        Util.LoggingQueue(this, "Beneficiary List", "onViewFullDetailsPressed pressed");
        finish();
    }

    private class CardTypeCountTask extends AsyncTask<String, Void, List<RationcardSummaryDto>> {

        @Override
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.RationCardSummaryReportActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<RationcardSummaryDto> doInBackground(final String... args) {
            try {
                return FPSDBHelper.getInstance(com.omneagate.activity.RationCardSummaryReportActivity.this).retrieveCardTypeCount();
            } catch (Exception e) {
                Log.e("cardTypeCountTask exc....", e.toString(), e);
                return null;
            }
        }

        // can use UI thread here
        protected void onPostExecute(final List<RationcardSummaryDto> beneficiary) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
            catch(Exception e) {}

            if (beneficiary == null) {
                Util.LoggingQueue(com.omneagate.activity.RationCardSummaryReportActivity.this, "BeneficiaryListActivity", "Error in search Invalid value entered");
                return;
            }
            else if (beneficiary.size() > 0) {
                beneficiarySearchDtos.clear();
                beneficiarySearchDtos.addAll(beneficiary);
                listView.setVisibility(View.VISIBLE);
                (findViewById(R.id.textNoRecord)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                listView.invalidate();
                listView.onLoadMoreComplete();
            } else if (beneficiary.size() == 0 && beneficiarySearchDtos.size() == 0) {
                listView.setVisibility(View.GONE);
                ((TextView) findViewById(R.id.textNoRecord)).setVisibility(View.VISIBLE);
            } else {
                listView.onLoadMoreComplete();
            }
        }
    }

    private class unitWiseCountTask extends AsyncTask<String, Void, List<UnitwiseSummaryDto>> {

        @Override
        protected void onPreExecute() {
            try {

                Util.LoggingQueue(RationCardSummaryReportActivity.this, "RationCardSummaryReportActivity ", "unitWiseCountTask Called");

//                progressBar = new CustomProgressDialog(com.omneagate.activity.RationCardSummaryReportActivity.this);
//                progressBar.show();
                Util.oneTotal = 0;
                Util.oneHalfTotal = 0;
                Util.twoTotal = 0;
                Util.twoHalfTotal = 0;
                Util.threeTotal = 0;
                Util.totalTotal = 0;
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<UnitwiseSummaryDto> doInBackground(final String... args) {
            try {
                Util.LoggingQueue(RationCardSummaryReportActivity.this, "RationCardSummaryReportActivity ", "unitWiseCountTask doInBackground ");

                return FPSDBHelper.getInstance(com.omneagate.activity.RationCardSummaryReportActivity.this).retrieveUnitWiseCount();
            } catch (Exception e) {
                Log.e("unitWiseCountTask exc....", e.toString(), e);
                return null;
            }
        }

        // can use UI thread here
        protected void onPostExecute(final List<UnitwiseSummaryDto> unitSummary) {
//            if (progressBar != null) {
//                progressBar.dismiss();
//            }
            if (unitSummary == null) {
                Util.LoggingQueue(RationCardSummaryReportActivity.this, "RationCardSummaryReportActivity ", "unitWiseCountTask onPostExecute -> Error in search Invalid value entered");

              //  Util.LoggingQueue(com.omneagate.activity.RationCardSummaryReportActivity.this, "BeneficiaryListActivity", "Error in search Invalid value entered");
                return;
            }
            if (unitSummary.size() > 0) {
                Util.LoggingQueue(RationCardSummaryReportActivity.this, "RationCardSummaryReportActivity ", "unitWiseCountTask onPostExecute -> ");

                unitwiseSummaryDto.clear();
                unitwiseSummaryDto.addAll(unitSummary);
//                Log.e("activity.......","unitSummary........"+unitSummary);

                listView.setVisibility(View.VISIBLE);
//                (findViewById(R.id.unitTextNoRecord)).setVisibility(View.GONE);
                adapter2.notifyDataSetChanged();

                unitListView.invalidate();
                unitListView.onLoadMoreComplete();


                Util.LoggingQueue(RationCardSummaryReportActivity.this, "RationCardSummaryReportActivity ", "unitWiseCountTask onPostExecute unitSummary -> "+unitSummary);


                ((TextView) findViewById(R.id.totalCount)).setText(getString(R.string.total));
                ((TextView) findViewById(R.id.oneTotal)).setText(String.valueOf(Util.oneTotal));
                ((TextView) findViewById(R.id.oneHalfTotal)).setText(String.valueOf(Util.oneHalfTotal));
                ((TextView) findViewById(R.id.twoTotal)).setText(String.valueOf(Util.twoTotal));
                ((TextView) findViewById(R.id.twoHalfTotal)).setText(String.valueOf(Util.twoHalfTotal));
                ((TextView) findViewById(R.id.threeTotal)).setText(String.valueOf(Util.threeTotal));
                ((TextView) findViewById(R.id.countValue)).setText(String.valueOf(Util.totalTotal));



            } else if (unitSummary.size() == 0 && unitwiseSummaryDto.size() == 0) {
                unitListView.setVisibility(View.GONE);
//                ((TextView) findViewById(R.id.unitTextNoRecord)).setVisibility(View.VISIBLE);
            } else {
                unitListView.onLoadMoreComplete();
            }
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

            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
                loadMore = 0;
                beneficiarySearchDtos = new ArrayList<>();
                adapter = new RationcardSummaryListAdapter(com.omneagate.activity.RationCardSummaryReportActivity.this, beneficiarySearchDtos);
                listView.setAdapter(adapter);
//                new BeneficiarySearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cardSearch, mobileSearch);
            } else {
            }
        }

        public void onRelease(int primaryCode) {

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

}
