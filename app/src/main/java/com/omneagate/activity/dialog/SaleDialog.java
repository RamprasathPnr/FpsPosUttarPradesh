package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EntitlementDTO;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.SalesEntryActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class SaleDialog extends Dialog implements View.OnClickListener {
    Activity dialogContext;

    int id;

    String number = "";

    EntitlementDTO entitled;

//    NumberFormat formatter;

    List<String> numberValues;

    boolean stockValidation = false;

    public SaleDialog(Activity context, int id) {
        super(context);
        dialogContext = context;
        this.id = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_sale_page);
        setCanceledOnTouchOutside(false);

        String stock_validation = "" + FPSDBHelper.getInstance(dialogContext).getMasterData("stock_validation");
        if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
            if (stock_validation.equalsIgnoreCase("1")) {
                stockValidation = true;
            }
        }

        /*formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        entitled = EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(id);
        String qty1 = Util.quantityRoundOffFormat(entitled.getBought());
        Log.e("qty1", ""+qty1);
        number = qty1;
        if (entitled.getBought() == 0.0) {
            Log.e("getBought", "0.0"+qty1);

            number = "0";
        }
        numberValues = new ArrayList<>();
        setText();
        String productName = entitled.getProductName();
        if (GlobalAppState.language.equals("hi") && StringUtils.isNotEmpty(entitled.getLproductUnit())) {
            productName = entitled.getLproductName();
        }
//        Util.setTamilText((TextView) findViewById(R.id.sale_dialog_header), productName);
        ((TextView) findViewById(R.id.sale_dialog_header)).setText(productName);
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

        Log.e("getCurrentQuantity() = ", ""+entitled.getCurrentQuantity());


        if (entitled.getCurrentQuantity() > 15.0) {
            numberValues.add("5.000");
            numberValues.add("10.000");
            numberValues.add("15.000");
        } else if (entitled.getCurrentQuantity() > 10) {
            numberValues.add("3.000");
            numberValues.add("6.000");
            numberValues.add("9.000");
        } else if (entitled.getCurrentQuantity() > 5) {
            numberValues.add("2.000");
            numberValues.add("4.000");
            numberValues.add("6.000");
        } else if (entitled.getCurrentQuantity() > 2) {
            numberValues.add("1.000");
            numberValues.add("2.000");
            numberValues.add("3.000");
        } else if (entitled.getCurrentQuantity() == 2) {
            numberValues.add("0.500");
            numberValues.add("1.000");
            numberValues.add("1.500");
        } else {
            numberValues.add("0.500");
            numberValues.add("1.000");
            numberValues.add("1.000");
        }
        setButtonText();
    }

    private void setButtonText() {
        String productUnit = entitled.getProductUnit();
        if (GlobalAppState.language.equals("hi")) {
            if (StringUtils.isNotEmpty(entitled.getLproductUnit())) {
                productUnit = entitled.getLproductUnit();
            }

        }
        Util.setTamilText((TextView) findViewById(R.id.fivekgs), numberValues.get(0) + " " + productUnit);
        Util.setTamilText((TextView) findViewById(R.id.tenkgs), numberValues.get(1) + " " + productUnit);
        Util.setTamilText((TextView) findViewById(R.id.fifteenkgs), numberValues.get(2) + " " + productUnit);
        Util.setTamilText((TextView) findViewById(R.id.entitledOK), dialogContext.getString(R.string.ok));
        Util.setTamilText((TextView) findViewById(R.id.fullkgs), dialogContext.getString(R.string.full));
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
                checkAvailability(numberValues.get(1));
                break;
            case R.id.fivekgs:
                checkAvailability(numberValues.get(0));
                break;
            case R.id.fifteenkgs:
                checkAvailability(numberValues.get(2));
                break;
            case R.id.fullkgs:
                setFullKg();
                break;
            case R.id.button_zero:
                addNumber("0");
                break;
            case R.id.button_dot:
                if (number.contains(".")) {
                } else
                    addNumber(".");
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

        if (stockValidation) {
            if (!isFPSStockAvailable(Double.parseDouble(number), entitled.getProductId())) {
                Toast.makeText(dialogContext, dialogContext.getString(R.string.stock_not_available), Toast.LENGTH_SHORT).show();
                return;
            }
        }


        number =((TextView) findViewById(R.id.sale_dialog_text)).getText().toString();
        double userReceived = Double.parseDouble(number);
        //   if (stockValidation) {
        if (userReceived > entitled.getCurrentQuantity()) {
            Toast.makeText(dialogContext, dialogContext.getString(R.string.exceedsLimit), Toast.LENGTH_SHORT).show();
            return;
        }

        if (userReceived < 0) {
            Toast.makeText(dialogContext, "Invalid Quantity", Toast.LENGTH_SHORT).show();
            return;
        }


        double total = Double.parseDouble(number) * entitled.getProductPrice();
        EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(id).setBought(Double.parseDouble(number));
        EntitlementResponse.getInstance().getQrcodeTransactionResponseDto().getEntitlementList().get(id).setTotalPrice(total);
        dismiss();
        ((SalesEntryActivity) dialogContext).setEntitlementText(id, Double.parseDouble(number));
    }


    /**
     * FPS stock available or not
     * if quantity availed is greater than stock returns false else true
     *
     * @params availed quantity and productId
     */
    private boolean isFPSStockAvailable(double bought, long productId) {
        try {
            FPSStockDto fpsStockDto = FPSDBHelper.getInstance(dialogContext).getAllProductStockDetails(productId);
            double quantity = 0.0;
            if(fpsStockDto != null) {
                quantity = fpsStockDto.getQuantity();
            }
            return quantity >= bought;
        } catch (Exception e) {
            return false;
        }
    }

    //set full entitlement
    private void setFullKg() {
        String qty2 = Util.quantityRoundOffFormat(entitled.getCurrentQuantity());
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
            double roundOff = (double) Math.round(entitled.getCurrentQuantity() * 100) / 100;

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
     //   if (stockValidation) {
            if (userReceived > entitled.getCurrentQuantity()) {
                Toast.makeText(dialogContext, dialogContext.getString(R.string.exceedsLimit), Toast.LENGTH_SHORT).show();
                return;
            }
       // }
        number = entitledText;
        setText();
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, String value) {
        if (GlobalAppState.language.equals("hi")) {
            Typeface tfBamini = Typeface.createFromAsset(dialogContext.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, value));
        } else {
            textName.setText(value);
        }
    }
}
