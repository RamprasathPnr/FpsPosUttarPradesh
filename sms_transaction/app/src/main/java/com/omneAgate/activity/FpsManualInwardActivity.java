package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.EnumDTO.StockTransactionType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.GodownDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.StockReqBaseDto;
import com.omneagate.DTO.StockRequestDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.ManualInwardSearchableAdapter;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.StockManualInwardDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class FpsManualInwardActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {


    long inwardId;
    GodownDto godownDto;
    AutoCompleteTextView autoCompleteTextView;
    EditText edtChellan;
    boolean valueSelectedGodown = false;
    boolean valueEnteredGodown = false;
    List<String> godownNameList = null;

    long challan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fps_stock_manual);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        networkConnection = new NetworkConnection(this);
        createPage();
    }


    private void createPage() {
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardDetailProductLabel), getString(R.string.product));
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardDetailQuantityLabel), getString(R.string.quantity));
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardDetailReceivedQuantityLabel), getString(R.string.receivedQuantity));
        Util.setTamilText((TextView) findViewById(R.id.btnfpsIDSubmit), getString(R.string.submit));
        Util.setTamilText((TextView) findViewById(R.id.btnfpsIDCancel), getString(R.string.cancel));
        Util.setTamilText((TextView) findViewById(R.id.tvChallanId), getString(R.string.challanNo));


        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.tvAutoCompletegodown);
        edtChellan = (EditText) findViewById(R.id.edtChallanId);

        List<GodownDto> godownDtoList = FPSDBHelper.getInstance(this).getGodownDetails();


        Log.e("godownList", godownDtoList.toString());
        godownNameList = new ArrayList<String>();

        if (godownDtoList != null) {
            for (GodownDto godownDto : godownDtoList) {
                String godownName = godownDto.getName();
                godownNameList.add(godownName);

            }

        }

        if (godownNameList != null) {
            Log.e("godownNameList", godownNameList.toString());
            ManualInwardSearchableAdapter customerAdapter = new ManualInwardSearchableAdapter(this, godownNameList);
            autoCompleteTextView.setAdapter(customerAdapter);

        }

        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemSelectedListener(this);
        autoCompleteTextView.setOnItemClickListener(this);


        configureData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Log.e("position", "" + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        /*InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);*/

    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


        godownDto = FPSDBHelper.getInstance(this).getGodown(String.valueOf(arg0.getItemAtPosition(arg2)));

        Log.e("gdto", "" + godownDto.toString());
        valueSelectedGodown = true;

      /*  Toast.makeText(getBaseContext(), "Position:" + arg2 + " Godown:" + arg0.getItemAtPosition(arg2),
                Toast.LENGTH_LONG).show();*/


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_fps_stock_inward_detail);
            fpsInwardLinearLayout.removeAllViews();
            List<ProductDto> productDtoList = FPSDBHelper.getInstance(this).getAllProductDetails();

            for (int position = 0; position < productDtoList.size(); position++) {

                LayoutInflater lin = LayoutInflater.from(this);
                FPSStockDto fpsStock = FPSDBHelper.getInstance(this).getAllProductStockDetails(productDtoList.get(position).getId());

                fpsInwardLinearLayout.addView(returnView(lin, productDtoList.get(position), position, fpsStock.getQuantity()));
            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        }
    }

    /*User entitlement view*/
    private View returnView(LayoutInflater entitle, ProductDto data, int position, double stock) {
        View convertView = entitle.inflate(R.layout.adapter_fps_stock_inward_details, null);

        TextView productName = (TextView) convertView.findViewById(R.id.tvProductName);
        TextView quantity = (TextView) convertView.findViewById(R.id.tvQuantity);
        TextView unit = (TextView) convertView.findViewById(R.id.tvQuantityUnit);

        final EditText receivedQuantity = (EditText) convertView.findViewById(R.id.tvReceivedQuantity);


        receivedQuantity.setFilters(new InputFilter[]{new DigitsKeyListener(
                Boolean.FALSE, Boolean.TRUE) {
            int beforeDecimal = 6
                    ,
                    afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                String etText = receivedQuantity.getText().toString();
                String temp = receivedQuantity.getText() + source.toString();

                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = receivedQuantity.getSelectionStart();
                    if (etText.indexOf(".") == -1) {
                        dotPosition = temp.indexOf(".");
                    } else {
                        dotPosition = etText.indexOf(".");
                    }
                    if (cursorPositon <= dotPosition) {
                        String beforeDot = etText.substring(0, dotPosition);
                        if (beforeDot.length() < beforeDecimal) {
                            return source;
                        } else {
                            if (source.toString().equalsIgnoreCase(".")) {
                                return source;
                            } else {
                                return "";
                            }

                        }
                    } else {
                        Log.i("cursor position", "in right");
                        temp = temp.substring(temp.indexOf(".") + 1);
                        if (temp.length() > afterDecimal) {
                            return "";
                        }
                    }
                }

                return super.filter(source, start, end, dest, dstart, dend);
            }
        }});


        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(data.getLocalProductName()))
            Util.setTamilText(productName, data.getLocalProductName());
        else
            productName.setText(data.getName());
        unit.setText(" ( " + data.getProductUnit() + " ) ");
        NumberFormat unitFormat = new DecimalFormat("#0.00");
        quantity.setText(unitFormat.format(stock));

        Log.e("unit", data.getProductUnit() + "  " + data.getName() + "   " + unitFormat.format(stock));
        receivedQuantity.setId(position);

        return convertView;

    }


    // This method submit all data Fps stock outward data with received quantity

    public void onSubmit(View v) {
        List<ProductDto> list = FPSDBHelper.getInstance(this).getAllProductDetails();
        boolean valueEntered = false;
        List<StockRequestDto.ProductList> prods = new ArrayList<StockRequestDto.ProductList>();
        for (int i = 0; i < list.size(); i++) {
            StockRequestDto.ProductList products = new StockRequestDto.ProductList();
            products.setId(list.get(i).getId());
            products.setQuantity(0.0);
            String quantity = ((EditText) findViewById(i)).getText().toString();
            Double reqQuantity = 0.0;
            if (quantity.length() > 0) {
                reqQuantity = Double.parseDouble(quantity);
            }
            if (reqQuantity > 0.0) {
                valueEntered = true;
                products.setRecvQuantity(reqQuantity);
                prods.add(products);
            }

        }


        String challanNo = edtChellan.getText().toString();
        boolean valueEnteredChallan = false;
        if (StringUtils.isNotEmpty(challanNo)) {
            challan = Long.parseLong(challanNo);
            if (challan == 0) {
                Util.messageBar(this, getString(R.string.challanNoGreaterZero));
                return;
            } else {
                valueEnteredChallan = true;

            }
        }
        if (!StringUtils.isNotEmpty(autoCompleteTextView.getText().toString())) {
            Util.messageBar(this, getString(R.string.selectGodown));
            return;
        }
        String godownName = autoCompleteTextView.getText().toString().trim();
        for (String godown : godownNameList) {  ////user type before godown name
            if (!godown.equalsIgnoreCase(godownName)) {
                Util.messageBar(this, getString(R.string.godownNameIncorrect));
                return;
            }
        }

        if (godownDto.getName().equalsIgnoreCase(godownName)) {
            valueEnteredGodown = true;
        }


        if (valueEntered && valueSelectedGodown && valueEnteredGodown && valueEnteredChallan) {
            getRequest(prods);
        } else if (!valueSelectedGodown) {
            Util.messageBar(this, getString(R.string.selectGodown));
        } else if (!valueEnteredChallan) {
            Util.messageBar(this, getString(R.string.enterChellanNo));
        } else if (!valueEntered) {
            Util.messageBar(this, getString(R.string.itemZero));
        } else if (!valueEnteredGodown) {//user type atfter godown name
            Util.messageBar(this, getString(R.string.godownNameIncorrect));
        }

    }

    // Cancel Button
    public void onCancel(View v) {
        startActivity(new Intent(this, SaleActivity.class));
        finish();

    }


    //Reguest to Server

    public void getRequest(List<StockRequestDto.ProductList> products) {
        try {

            StockReqBaseDto stockReqBaseDto = new StockReqBaseDto();
            StockRequestDto stockRequestDto = new StockRequestDto();
            stockReqBaseDto.setType("com.omneagate.rest.dto.StockRequestDto");
            stockRequestDto.setType(StockTransactionType.INWARD);//Stock transaction type
            GlobalAppState appState = (GlobalAppState) getApplication();
            stockRequestDto.setFpsId(appState.getFpsId());
            stockRequestDto.setProductLists(products);
            stockRequestDto.setGodownId(godownDto.getId());
            stockRequestDto.setDeliveryChallanId("" + challan);
            stockRequestDto.setDate(System.currentTimeMillis());
            stockRequestDto.setBatchNo("" + 3);
            stockRequestDto.setCreatedBy("100");
            stockRequestDto.setUnit("5");

            inwardId = FPSDBHelper.getInstance(this).insertStockInward(stockRequestDto);

            stockRequestDto.setInwardKey(inwardId);

            stockReqBaseDto.setBaseDto(stockRequestDto);

            if (networkConnection.isNetworkAvailable()) {
                if (inwardId > 0) {
                    String login = new Gson().toJson(stockReqBaseDto);

                    Log.e("Req", login);
                    StringEntity se = new StringEntity(login, HTTP.UTF_8);
                    httpConnection = new HttpClientWrapper();

                    String url = "/fpsStock/inward";
                    httpConnection.sendRequest(url, null, ServiceListenerType.FPS_INTENT_REQUEST, SyncHandler, RequestType.POST, se, this);

                } else {
                    Log.e("Error", "Error in DB");
                }
            } else {
                Log.e("SreqDto", stockRequestDto.toString());
                FPSDBHelper.getInstance(this).insertStockInward(stockRequestDto);
                startActivity(new Intent(this, SaleActivity.class));
            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case FPS_INTENT_REQUEST:
                getStockResponse(message);
                break;

            default:
                Log.e("Error", "Fps Manual Stock Entry");
                break;
        }
    }

    // After response received from server successfully in android
    public void getStockResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            Log.e("responseSrbd", response);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            StockRequestDto stockRequestDto = gson.fromJson(response, StockRequestDto.class);
            int statusCode = stockRequestDto.getStatusCode();

            long inwardKey = stockRequestDto.getInwardKey();

            if (statusCode == 0) {

                FPSDBHelper.getInstance(this).updateStockInward(inwardKey, 0);
                StockManualInwardDialog stockManualInwardDialog = new StockManualInwardDialog(this);
                stockManualInwardDialog.show();
            } else {
                Util.messageBar(this, FPSDBHelper.getInstance(this).retrieveLanguageTable(stockRequestDto.getStatusCode(), GlobalAppState.language).getDescription());
            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }


}
