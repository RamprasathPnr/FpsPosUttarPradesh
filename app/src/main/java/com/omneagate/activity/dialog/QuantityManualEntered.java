package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import com.omneagate.DTO.Product;

import com.omneagate.Util.Util;
import com.omneagate.activity.R;
import com.omneagate.activity.TgProductListActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 2/3/17.
 */
public class QuantityManualEntered extends Dialog implements View.OnClickListener {
    Context dialogContext;

    String number = "";

    int position;

    Product product;

    List<String> numberValues;
    private TextView productName;


    public QuantityManualEntered(Context context, Product product , int position) {
        super(context);
        this.dialogContext = context;
        this.position=position;
        this.product=product;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_quantity_manual);
        setCanceledOnTouchOutside(false);

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
        findViewById(R.id.button_dot).setOnClickListener(this);
        findViewById(R.id.button_bkSp).setOnClickListener(this);
        findViewById(R.id.entitledOK).setOnClickListener(this);
        findViewById(R.id.fivekgs).setOnClickListener(this);
        findViewById(R.id.tenkgs).setOnClickListener(this);
        findViewById(R.id.fifteenkgs).setOnClickListener(this);
        findViewById(R.id.fullkgs).setOnClickListener(this);

        productName=(TextView)findViewById(R.id.sale_dialog_header);




      /*  if (product.getProductBalanceQty() > 15.0) {
            numberValues.add("5.000");
            numberValues.add("10.000");
            numberValues.add("15.000");
        } else if (product.getProductBalanceQty() > 10) {
            numberValues.add("3.000");
            numberValues.add("6.000");
            numberValues.add("9.000");
        } else if (product.getProductBalanceQty()> 5) {
            numberValues.add("2.000");
            numberValues.add("4.000");
            numberValues.add("6.000");
        } else if (product.getProductBalanceQty() > 2) {
            numberValues.add("1.000");
            numberValues.add("2.000");
            numberValues.add("3.000");
        } else if (product.getProductBalanceQty() == 2) {
            numberValues.add("1.500");
            numberValues.add("1.000");
            numberValues.add("1.500");
        } else {
            numberValues.add("1.500");
            numberValues.add("1.000");
            numberValues.add("1.000");
        }*/
        setButtonText();
    }

    private void setButtonText() {
        String productUnit = product.getUnitName();

       /* Util.setTamilText((TextView) findViewById(R.id.fivekgs), numberValues.get(0) + " " + productUnit);
        Util.setTamilText((TextView) findViewById(R.id.tenkgs), numberValues.get(1) + " " + productUnit);
        Util.setTamilText((TextView) findViewById(R.id.fifteenkgs), numberValues.get(2) + " " + productUnit);
        Util.setTamilText((TextView) findViewById(R.id.entitledOK), dialogContext.getString(R.string.ok));
        Util.setTamilText((TextView) findViewById(R.id.fullkgs), dialogContext.getString(R.string.full));*/
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
            case R.id.tenkgs:
              //  checkAvailability(numberValues.get(1));
                break;
            case R.id.fivekgs:
               // checkAvailability(numberValues.get(0));
                break;
            case R.id.fifteenkgs:
               // checkAvailability(numberValues.get(2));
                break;
            case R.id.fullkgs:
                setFullKg();
                break;
            case R.id.button_zero:
                addNumber("0");
                break;
            case R.id.button_dot:
               /* if (number.contains(".")) {
                } else
                    addNumber(".");*/
                break;
            case R.id.entitledOK:
                // addNumber(((TextView) findViewById(R.id.sale_dialog_text)).getText().toString());
                setEntitlementData();
                break;
            case R.id.button_bkSp:
                backSpace();
                break;
            default:
                break;
        }

    }

    private void setEntitlementData() {

        number =((TextView) findViewById(R.id.sale_dialog_text)).getText().toString();
        double userReceived = Double.parseDouble(number);
        //   if (stockValidation) {
        if (userReceived > product.getProductBalanceQty()) {
            Toast.makeText(dialogContext, dialogContext.getString(R.string.exceedsLimit), Toast.LENGTH_SHORT).show();
            return;
        }

        if (userReceived < 0) {
            Toast.makeText(dialogContext, "Invalid Quantity", Toast.LENGTH_SHORT).show();
            return;
        }


        double total = Double.parseDouble(number) * product.getUnitRate();
        ((TgProductListActivity) dialogContext).setPurchasedQuantity(position,Double.parseDouble(number));

      /*  EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(id).setBought(Double.parseDouble(number));
        EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(id).setTotalPrice(total);*/
        dismiss();
        //((SalesEntryActivity) dialogContext).setEntitlementText(id, Double.parseDouble(number));
    }


    /**
     * FPS stock available or not
     * if quantity availed is greater than stock returns false else true
     *
     * @params availed quantity and productId
     */


    //set full entitlement
    private void setFullKg() {
        String qty2 = Util.quantityRoundOffFormat(product.getProductBalanceQty());
        number = qty2;
        setText();
    }

    private void setText() {
        ((TextView) findViewById(R.id.sale_dialog_text)).setText(number);
    }

    private void addNumber(String text) {
        try {
            if (number.contains(".")) {
                String[] numberData = StringUtils.split(number, ".");
                if (numberData.length > 1)
                    if (numberData[1].length() == 3) {
                        return;
                    }
            }
            if (number.length() >= 7) {
                return;
            }
            if (number.equals("0") && !text.equals(".")) {
                number = text;
            } else if (text.equals(".")) {
                number = number + ".";
            } else {
                number = number + text;
            }
            double userReceived = Double.parseDouble(number);
            Log.e("userReceived", ""+userReceived);
            double roundOff = (double) Math.round(product.getProductBalanceQty() * 100) / 100;

            //  if (stockValidation) {
            if (userReceived > roundOff) {
                Toast.makeText(dialogContext, dialogContext.getString(R.string.exceedsLimit), Toast.LENGTH_SHORT).show();
                backSpace();
                return;
            }
            //}
            setText();
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    private void backSpace() {
        if (number.length() > 1) {
            number = number.substring(0, number.length() - 1);
        } else {
            number = "0";
        }
        setText();
    }

    private void checkAvailability(String entitledText) {
        double userReceived = Double.parseDouble(entitledText);

        if (userReceived > product.getProductBalanceQty()) {
            Toast.makeText(dialogContext, dialogContext.getString(R.string.exceedsLimit), Toast.LENGTH_SHORT).show();
            return;
        }
        number = entitledText;
        setText();
    }


}
