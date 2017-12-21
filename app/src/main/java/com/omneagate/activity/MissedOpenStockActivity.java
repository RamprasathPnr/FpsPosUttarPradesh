package com.omneagate.activity;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.FpsStockEntryDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created for openStock
 */
public class MissedOpenStockActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

    RelativeLayout keyBoardCustom;

    KeyboardView keyView;

    int keyBoardFocused;

    Map<Integer, ProductDto> productReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missed_openstock);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        configureData();

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void configureData() {
        try {
            setUpPopUpPage();
            keyView = (KeyboardView) findViewById(R.id.customkeyboard);
            Util.LoggingQueue(this, "Stock Status activity", "Main page Called");
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.opening_stock);
            Util.setTamilText((TextView) findViewById(R.id.comments), R.string.missedOpeningStockComment);
            Util.setTamilText((TextView) findViewById(R.id.commodity), R.string.commodity);
            Util.setTamilText((TextView) findViewById(R.id.unit), R.string.unit);
            Util.setTamilText((TextView) findViewById(R.id.current_stock), R.string.current_stock);
            Util.setTamilText((TextView) findViewById(R.id.opening_stock), R.string.opening_stock);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.submit);
            Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.close);
            keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
            new fpsStockListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

            findViewById(R.id.cancel_button).setVisibility(View.INVISIBLE);
            findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();

                }
            });
            findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getResult();

                }
            });

            findViewById(R.id.imageViewBack).setVisibility(View.VISIBLE);
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            Log.e("StockCheckActivity", e.toString(), e);
        }
    }


    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }

    private void keyBoardAppear() {
        Keyboard keyboard = new Keyboard(this, R.layout.keyboardopenstock);
        keyView.setKeyboard(keyboard);
        keyView.setPreviewEnabled(false);
        keyView.bringToFront();
        keyView.setVisibility(KeyboardView.VISIBLE);
        keyView.setOnKeyboardActionListener(new KeyList());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StockManagementActivity.class));
        Util.LoggingQueue(this, "openStock activity", "Back button pressed");
        finish();
    }

    @Override
    public void onFocusChange(View view, boolean focusChanged) {
        if (focusChanged) {
            keyBoardFocused = view.getId();
            checkVisibility();
            keyBoardAppear();
        }
    }

    @Override
    public void onClick(View view) {
        keyBoardFocused = view.getId();
        checkVisibility();
        keyBoardAppear();
    }

    private void getResult() {
        List<FPSStockDto> fpsStockDto = new ArrayList<>();
        for (Integer data : productReceived.keySet()) {
            int position = (int) data;
            String value = ((EditText) findViewById(position)).getText().toString().trim();
            FPSStockDto addfpsstockDto = new FPSStockDto();
            value = value.replaceAll("[^\\d.]", "");
            Log.e("MissedOpenStockActivity","value....."+value);

            if (StringUtils.isNotEmpty(value) && Double.parseDouble(value) >= 0.0) {
                addfpsstockDto.setProductId(productReceived.get(data).getId());
                addfpsstockDto.setFpsId(SessionId.getInstance().getFpsId());
                addfpsstockDto.setProductName(productReceived.get(data).getName());
                addfpsstockDto.setQuantity(Double.parseDouble(value));
                addfpsstockDto.setEmailAction(true);
                addfpsstockDto.setSmsMSAction(true);
                fpsStockDto.add(addfpsstockDto);
            } else {
               /* addfpsstockDto.setProductId(productReceived.get(data).getId());
                addfpsstockDto.setFpsId(SessionId.getInstance().getFpsId());
                addfpsstockDto.setProductName(productReceived.get(data).getName());
                addfpsstockDto.setQuantity(0.0);
                addfpsstockDto.setEmailAction(true);
                addfpsstockDto.setSmsMSAction(true);
                fpsStockDto.add(addfpsstockDto);*/
                Util.messageBar(MissedOpenStockActivity.this, getString(R.string.opening_stock_mandatory_alert));
                return;
            }
        }
        if (fpsStockDto.size() > 0) {
            String androidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID).toUpperCase();
            FpsStockEntryDto fpsEntryDto = new FpsStockEntryDto();
            fpsEntryDto.setDeviceNumber(androidDeviceId);
            fpsEntryDto.setOpeningStockList(fpsStockDto);
            String fpsStcokList = new Gson().toJson(fpsEntryDto);
            Intent nextStockIntent;
            nextStockIntent = new Intent(getApplicationContext(), PendingOpenstockActivityEntry.class);
            Log.e("missed...","values..."+fpsStcokList);
            nextStockIntent.putExtra("Dtolist", fpsStcokList);
            startActivity(nextStockIntent);
            Util.LoggingQueue(com.omneagate.activity.MissedOpenStockActivity.this, "openstockEntry activity", "Back button pressed");
            finish();

        } else {
            Util.messageBar(com.omneagate.activity.MissedOpenStockActivity.this, getString(R.string.no_records));
        }
    }

    private void processData(List<ProductDto> productDtos) {
        if (productDtos.size() != 0) {
            productReceived = new HashMap<>();
            int position = 0;
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            transactionLayout.removeAllViews();
            for (ProductDto products : productDtos) {
                LayoutInflater lin = LayoutInflater.from(com.omneagate.activity.MissedOpenStockActivity.this);
//                FPSStockHistoryDto stockHistory = FPSDBHelper.getInstance(this).getAllMissedProductDetails(products.getId());
//                FPSStockDto fpsStock = FPSDBHelper.getInstance(com.omneagate.activity.MissedOpenStockActivity.this).getAllProductStockDetails(products.getId());
//                if (stockHistory != null && stockHistory.getProductId() != 0 && fpsStock != null) {
//                    transactionLayout.addView(returnViewTextView(lin, products, fpsStock.getQuantity(), stockHistory.getPrevQuantity()));
//                    Log.e("opening stock2", "" + products.getLocalProductName().toString());
//                } else {
                    transactionLayout.addView(returnViewTextView(lin, products, position));


                    findViewById(R.id.btnClose).setVisibility(View.VISIBLE);
                    Util.setTamilText((TextView) findViewById(R.id.cancel_button), R.string.cancel);
                    productReceived.put(position, products);
//                    transactionLayout.addView(returnView(lin, products, 0.0, position));*/
//                    Log.e("opening stock1", "" + products.getLocalProductName().toString());
//                }
                position++;
            }
        }
    }

    /*private View returnView(LayoutInflater entitle, final ProductDto data, double sale, final int position) {
        View convertView = entitle.inflate(R.layout.adpter_openstock, null);
        TextView productNameTv = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView unitTv = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView openingStockTv = (TextView) convertView.findViewById(R.id.entitlement_opening);
        EditText addOpenStockEt = (EditText) convertView.findViewById(R.id.amount_current);
        addOpenStockEt.setShowSoftInputOnFocus(false);
        NumberFormat format = new DecimalFormat("#0.000");
        format.setMaximumFractionDigits(3);
        addOpenStockEt.setId(position);
        addOpenStockEt.setOnFocusChangeListener(this);
        addOpenStockEt.setOnClickListener(this);
        productNameTv.setText(data.getName());
        if (GlobalAppState.language.equalsIgnoreCase("ta") && StringUtils.isNotEmpty(data.getLocalProductUnit())) {
            productNameTv.setText(unicodeToLocalLanguage(data.getLocalProductName()));
        }

        unitTv.setText(data.getProductUnit());
        if (GlobalAppState.language.equalsIgnoreCase("ta") && StringUtils.isNotEmpty(data.getLocalProductUnit())) {
            unitTv.setText(unicodeToLocalLanguage(data.getLocalProductUnit()));
        }
        openingStockTv.setText(format.format(sale));


        return convertView;
    }*/

    private View returnViewTextView(LayoutInflater entitle, final ProductDto data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_missed_openstock, null);
        TextView productNameTv = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView unitTv = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView openingStockTv = (TextView) convertView.findViewById(R.id.entitlement_opening);
        EditText addOpenStockEt = (EditText) convertView.findViewById(R.id.amount_current);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addOpenStockEt.setShowSoftInputOnFocus(false);
        }
        addOpenStockEt.setId(position);
        addOpenStockEt.setOnFocusChangeListener(this);
        addOpenStockEt.setOnClickListener(this);

        /*openingStockTv.setShowSoftInputOnFocus(false);
        openingStockTv.setId(position+100);
        openingStockTv.setOnFocusChangeListener(this);
        openingStockTv.setOnClickListener(this);*/

        openingStockTv.setText("0.000");
        addOpenStockEt.setText("");

        productNameTv.setText(data.getName());
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(data.getLocalProductUnit())) {
//            productNameTv.setText(unicodeToLocalLanguage(data.getLocalProductName()));
            productNameTv.setText(data.getLocalProductName());
        }

        unitTv.setText(data.getProductUnit());
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(data.getLocalProductUnit())) {
            unitTv.setText(unicodeToLocalLanguage(data.getLocalProductUnit()));
        }



        return convertView;
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
            try {
                // Back Space key
                if (primaryCode == 8) {
                    String number = ((EditText) findViewById(keyBoardFocused)).getText().toString();
                    Log.e("number_count for dot", "" + number.length());
                    if (number.length() > 0) {
                        number = number.substring(0, number.length() - 1);
                        ((EditText) findViewById(keyBoardFocused)).setText(number);
                        ((EditText) findViewById(keyBoardFocused)).setSelection(number.length());
                    }

                } else if (primaryCode == 46) { //Done key
                    keyBoardCustom.setVisibility(View.GONE);
                } else if (primaryCode == 21) { //Dot Key
                    String value = ((EditText) findViewById(keyBoardFocused)).getText().toString().trim();
                    if (!StringUtils.contains(value, "."))
                        ((EditText) findViewById(keyBoardFocused)).append(".");
                } else { //Other Keys
                    char ch = (char) primaryCode;
                    String number = ((EditText) findViewById(keyBoardFocused)).getText().toString();
                    String testArray[] = StringUtils.split(number, ".");
                    if (testArray.length == 0 || testArray.length == 1) {
                        ((EditText) findViewById(keyBoardFocused)).append("" + ch);
                    } else if (testArray.length > 1 && testArray[1].length() <= 2)
                        ((EditText) findViewById(keyBoardFocused)).append("" + ch);


                }
            } catch (Exception e) {
                Log.e("onPress", e.toString(), e);
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    private class fpsStockListTask extends AsyncTask<String, Void, List<ProductDto>> {

        // can use UI thread here
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(MissedOpenStockActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected List<ProductDto> doInBackground(final String... args) {
            return FPSDBHelper.getInstance(MissedOpenStockActivity.this).getAllMissedProductDetails();
        }

        // can use UI thread here
        protected void onPostExecute(final List<ProductDto> result) {
            Log.e("productDtoList", "" + result.toString());
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            }
            catch(Exception e) {}
            processData(result);
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