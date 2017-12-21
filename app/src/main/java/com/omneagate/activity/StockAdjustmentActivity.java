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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.StockRequestDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StockAdjustmentActivity extends BaseActivity {

    List<StockRequestDto.ProductList> prods;
    boolean stockValidation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps_stock_adjustment);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        networkConnection = new NetworkConnection(this);
        String stock_validation = "" + FPSDBHelper.getInstance(StockAdjustmentActivity.this).getMasterData("stock_validation");
        if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
            if (stock_validation.equalsIgnoreCase("1")) {
                stockValidation = true;
            }
        }
        createPage();

    }


    private void createPage() {
        Util.setTamilText((TextView) findViewById(R.id.fpsStockAdjustmentProductLabel), getString(R.string.fpsStockAdjustmentProductName));
        Util.setTamilText((TextView) findViewById(R.id.fpsStockAdjustmentCurrentStockLabel), getString(R.string.fpsStockAdjustmentcurrentStock));
        Util.setTamilText((TextView) findViewById(R.id.fpsStockAdjustmentQuantityLabel), getString(R.string.fpsStockAdjustmentQuantity));
        Util.setTamilText((TextView) findViewById(R.id.fpsStockAdjustmentOperationLabel), getString(R.string.fpsStockAdjustmentOperation));
        Util.setTamilText((TextView) findViewById(R.id.btnfpsStockAdjustmentSubmit), getString(R.string.submit));
        Util.setTamilText((TextView) findViewById(R.id.btnfpsStockAdjustmentCancel), getString(R.string.cancel));

        configureData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_fpsStockAdjustment);
            fpsInwardLinearLayout.removeAllViews();
            List<ProductDto> productDtoList = FPSDBHelper.getInstance(this).getAllProductDetails();
            for (int position = 0; position < productDtoList.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);
                FPSStockDto fpsStock = FPSDBHelper.getInstance(this).getAllProductStockDetails(productDtoList.get(position).getId());
                if(fpsStock != null) {
                    fpsInwardLinearLayout.addView(returnView(lin, productDtoList.get(position), position, fpsStock.getQuantity()));
                }
                else {
                    fpsInwardLinearLayout.addView(returnView(lin, productDtoList.get(position), position, 0.0));
                }
            }

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        }
    }


    /*User entitlement view*/
    private View returnView(LayoutInflater entitle, ProductDto data, int position, double stock) {
        View convertView = entitle.inflate(R.layout.adapter_fps_stock_adjustment, null);

        TextView productName = (TextView) convertView.findViewById(R.id.tvProductName);
        TextView unit = (TextView) convertView.findViewById(R.id.tvQuantityUnit);
        TextView currentStockQuantity = (TextView) convertView.findViewById(R.id.tvCurrentStock);
        final EditText adjustQuantity = (EditText) convertView.findViewById(R.id.edtAdjustQuantity);
        Spinner adjustSpinner = (Spinner) convertView.findViewById(R.id.adjustmentSpinner);


        adjustQuantity.setFilters(new InputFilter[]{new DigitsKeyListener(
                Boolean.FALSE, Boolean.TRUE) {
            int beforeDecimal = 6,
                    afterDecimal = 2;

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                String etText = adjustQuantity.getText().toString();
                String temp = adjustQuantity.getText() + source.toString();

                if (temp.equals(".")) {
                    return "0.";
                } else if (temp.toString().indexOf(".") == -1) {
                    // no decimal point placed yet
                    if (temp.length() > beforeDecimal) {
                        return "";
                    }
                } else {
                    int dotPosition;
                    int cursorPositon = adjustQuantity.getSelectionStart();
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


        if (GlobalAppState.language.equals("hi") && StringUtils.isNotEmpty(data.getLocalProductName()))
//            Util.setTamilText(productName, data.getLocalProductName());
            productName.setText(data.getLocalProductName());
        else
            productName.setText(data.getName());
        unit.setText(" ( " + data.getProductUnit() + " ) ");
        /*NumberFormat unitFormat = new DecimalFormat("#0.00");
        unitFormat.setRoundingMode(RoundingMode.CEILING);*/
//        stock = Util.priceRoundOffFormat(stock);
        currentStockQuantity.setText(Util.priceRoundOffFormat(stock));
        adjustQuantity.setId(position);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.adjustmentSpinner));
        adjustSpinner.setAdapter(arrayAdapter);
        adjustSpinner.setId(position + 100);
//        Log.i("unit", data.getProductUnit() + "  " + data.getName() + "   " + unitFormat.format(stock));

        return convertView;

    }

    // This method submit all data Fps stock outward data with received quantity

    public void onSubmit(View v) {
        List<ProductDto> list = FPSDBHelper.getInstance(this).getAllProductDetails();
        boolean valueEntered = isValueEntered(list);
        boolean valueCorrect = isValueCorrect(list);
        if (!valueEntered) {
            Util.messageBar(this, getString(R.string.enterAdjustQuantity));
            return;
        }
        if (!valueCorrect) {
            Util.messageBar(this, getString(R.string.enterAdjustQuantityWrong));
            return;
        }
       /* StockAdjustmentDialog stockAdjustmentDialog = new StockAdjustmentDialog(this, prods);
        stockAdjustmentDialog.show();*/

    }


    private boolean isValueEntered(List<ProductDto> list) {
        boolean valueEntered = false;
        prods = new ArrayList<StockRequestDto.ProductList>();
        for (int i = 0; i < list.size(); i++) {
            StockRequestDto.ProductList products = new StockRequestDto.ProductList();
            products.setId(list.get(i).getId());
            FPSStockDto stockList = FPSDBHelper.getInstance(this).getAllProductStockDetails(list.get(i).getId());
            if(stockList != null) {
                products.setQuantity(stockList.getQuantity());
            }
            else {
                products.setQuantity(0.0);
            }
            String adQuantity = ((EditText) findViewById(i)).getText().toString();
            Double reqQuantity = 0.0;
            if (adQuantity.length() > 0) {
                reqQuantity = Double.parseDouble(adQuantity);
            }
            if (reqQuantity > 0.0) {
                valueEntered = true;
                products.setRecvQuantity(reqQuantity);
                Spinner spin = (Spinner) findViewById(i + 100);
                products.setAdjustment(spin.getSelectedItem().toString());
                products.setAdjustmentItem(spin.getSelectedItemPosition());
                prods.add(products);

            }
        }

        return valueEntered;
    }

    private boolean isValueCorrect(List<ProductDto> list) {
        boolean isValueCorrect = true;
        for (int i = 0; i < prods.size(); i++) {
            FPSStockDto stockList = FPSDBHelper.getInstance(this).getAllProductStockDetails(prods.get(i).getId());
            StockRequestDto.ProductList products = prods.get(i);
            if (products.getAdjustmentItem() == 1) {
                Double totalQuantity;
                if(stockList != null) {
                    totalQuantity = stockList.getQuantity() - products.getRecvQuantity();
                }
                else {
                    totalQuantity = 0.0 - products.getRecvQuantity();
                }
                if (totalQuantity < 0.0)
                    return false;
            }
//            Double totalQuantity  = stockList
        }
        return isValueCorrect;
    }

    // Cancel Button
    public void onCancel(View v) {
        startActivity(new Intent(this, SaleActivity.class));
        finish();

    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

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

