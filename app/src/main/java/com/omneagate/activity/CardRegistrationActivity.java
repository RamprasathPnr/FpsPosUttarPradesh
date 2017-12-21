package com.omneagate.activity;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neopixl.pixlui.components.edittext.EditText;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.CardRegistrationAdapter;
import com.omneagate.activity.dialog.DateSelectionRegistrationDialog;
import com.omneagate.service.HttpClientWrapper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//FPS user can view the summary of selection
public class CardRegistrationActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

    final Handler handler = new Handler();

    ListView listView;

    EditText inputCardSearch, inputMobileSearch;

    TextView inputDateSearch;

    RelativeLayout keyBoardCustom;

    KeyboardView keyview;

    KeyBoardEnum keyBoardFocused;

    CardRegistrationAdapter adapter;

    boolean filter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_card_registration);
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
        Util.LoggingQueue(this, "Card Request", "Setting up main page");
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.card_activation);
        Util.setTamilText((TextView) findViewById(R.id.tvTitle), R.string.card_no);
        Util.setTamilText((TextView) findViewById(R.id.tvDesc), R.string.mob_number);
        Util.setTamilText((TextView) findViewById(R.id.reg_date), R.string.reg_date);
        Util.setTamilText((TextView) findViewById(R.id.tvAction), R.string.action);
        keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
        keyBoardCustom.setVisibility(View.GONE);
        setUpPopUpPage();
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView = (ListView) findViewById(R.id.listView);
        inputCardSearch = (EditText) findViewById(R.id.inputCardSearch);
        inputCardSearch.setShowSoftInputOnFocus(false);
        inputCardSearch.setOnClickListener(this);
        inputCardSearch.setOnFocusChangeListener(this);
        inputMobileSearch = (EditText) findViewById(R.id.inputMobileSearch);
        inputMobileSearch.setShowSoftInputOnFocus(false);
        inputMobileSearch.setOnClickListener(this);
        inputMobileSearch.setOnFocusChangeListener(this);
        inputDateSearch = (TextView) findViewById(R.id.inputDateSearch);
        findViewById(R.id.filter_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter = !filter;
                searchAdapter();
            }
        });
        searchAdapter();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.inputCardSearch) {
            checkVisibility();
            keyBoardAppear();
            inputCardSearch.requestFocus();
            keyBoardFocused = KeyBoardEnum.AREGISTER;
        } else if (v.getId() == R.id.inputMobileSearch) {
            checkVisibility();
            keyBoardAppear();
            inputMobileSearch.requestFocus();
            keyBoardFocused = KeyBoardEnum.MOBILE;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.inputCardSearch && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            inputCardSearch.requestFocus();
            keyBoardFocused = KeyBoardEnum.AREGISTER;
        } else if (v.getId() == R.id.inputMobileSearch && hasFocus) {
            checkVisibility();
            keyBoardAppear();
            inputMobileSearch.requestFocus();
            keyBoardFocused = KeyBoardEnum.MOBILE;
        }
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
        if (filter) {
            findViewById(R.id.linearSearch).setVisibility(View.VISIBLE);
            Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
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
            keyBoardFocused = KeyBoardEnum.AREGISTER;
            inputCardSearch.requestFocus();
            inputDateSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DateSelectionRegistrationDialog(com.omneagate.activity.CardRegistrationActivity.this).show();
                }
            });
        } else {
            findViewById(R.id.linearSearch).setVisibility(View.GONE);
            inputCardSearch.setText("");
            inputMobileSearch.setText("");
            inputDateSearch.setText("");
            changeAdapter();
        }
    }

    private void changeAdapter() {
        String dateSearch = inputDateSearch.getText().toString().trim();
        String cardSearch = inputCardSearch.getText().toString().trim();
        String mobileSearch = inputMobileSearch.getText().toString().trim();
        new BeneficiarySearchTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dateSearch, cardSearch, mobileSearch);
    }

    public void setTextDate(String textDate) {
        inputDateSearch.setText(textDate);
        changeAdapter();
    }


    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
    }


    public void activateData(BenefActivNewDto beneficiary) {
        if (progressBar != null) {
            progressBar.dismiss();
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        beneficiary.setValueAdded(true);
        if (beneficiary != null)
            Util.LoggingQueue(this, "Card Request", "Req" + beneficiary.toString());
        Intent intent = new Intent(this, BeneficiaryActivationActivity.class);
        String response = gson.toJson(beneficiary);
        intent.putExtra("data", response);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CardActivationActivity.class));
        Util.LoggingQueue(this, "Card Request", "Back pressed");
        finish();
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
                if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                    String text = inputCardSearch.getText().toString();
                    if (inputCardSearch.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        inputCardSearch.setText(text);
                        inputCardSearch.setSelection(text.length());
                    }
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    String text = inputMobileSearch.getText().toString();
                    if (inputMobileSearch.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        inputMobileSearch.setText(text);
                        inputMobileSearch.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
                changeAdapter();
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.AREGISTER) {
                    inputCardSearch.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.MOBILE) {
                    inputMobileSearch.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    private class BeneficiarySearchTask extends AsyncTask<String, Void, List<BenefActivNewDto>> {

        @Override
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.CardRegistrationActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<BenefActivNewDto> doInBackground(final String... args) {
            try {

                return FPSDBHelper.getInstance(com.omneagate.activity.CardRegistrationActivity.this).allBeneficiaryDetailsPending(args[0], args[1], args[2]);
            } catch (Exception e) {
                Log.e("sdfsdf", e.toString(), e);
                return null;
            }

        }

        // can use UI thread here
        protected void onPostExecute(final List<BenefActivNewDto> beneficiary) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
            catch(Exception e) {}

            if (beneficiary == null) {
                inputCardSearch.setText("");
                inputMobileSearch.setText("");
                Util.LoggingQueue(com.omneagate.activity.CardRegistrationActivity.this, "BeneficiaryListActivity", "Error in search Invalid value entered");
                return;
            }
            if (beneficiary.size() > 0) {
                Collections.sort(beneficiary, new Comparator<BenefActivNewDto>() {
                    @Override
                    public int compare(final BenefActivNewDto beneRhs, final BenefActivNewDto benefLhs) {
                        return benefLhs.getReqDate().compareTo(beneRhs.getReqDate());
                    }
                });
            }
            adapter = new CardRegistrationAdapter(com.omneagate.activity.CardRegistrationActivity.this, beneficiary);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
        }
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
}
