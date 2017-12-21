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
import com.omneagate.activity.dialog.StockAdjustmentDialog;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class StockAdjustmentActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps_stock_adjustment);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        networkConnection = new NetworkConnection(this);

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

                fpsInwardLinearLayout.addView(returnView(lin, productDtoList.get(position), position, fpsStock.getQuantity()));
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
            int beforeDecimal = 6
                    ,
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


        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(data.getLocalProductName()))
            Util.setTamilText(productName, data.getLocalProductName());
        else
            productName.setText(data.getName());
        unit.setText(" ( " + data.getProductUnit() + " ) ");
        NumberFormat unitFormat = new DecimalFormat("#0.00");
        currentStockQuantity.setText(unitFormat.format(stock));
        adjustQuantity.setId(position);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.adjustmentSpinner));
        adjustSpinner.setAdapter(arrayAdapter);
        adjustSpinner.setId(position + 100);
        Log.e("unit", data.getProductUnit() + "  " + data.getName() + "   " + unitFormat.format(stock));

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

            FPSStockDto stockList = FPSDBHelper.getInstance(this).getAllProductStockDetails(list.get(i).getId());
            products.setQuantity(stockList.getQuantity());
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
                prods.add(products);

            }

        }

        if (valueEntered) {

            StockAdjustmentDialog stockAdjustmentDialog = new StockAdjustmentDialog(this, prods);
            stockAdjustmentDialog.show();
        } else if (!valueEntered) {
            Util.messageBar(this, getString(R.string.enterAdjustQuantity));
        }

    }

    // Cancel Button
    public void onCancel(View v) {
        startActivity(new Intent(this, SaleActivity.class));
        finish();

    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

}

