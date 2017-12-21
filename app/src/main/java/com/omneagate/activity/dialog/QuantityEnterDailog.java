package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.Product;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.SalesEntryActivity;
import com.omneagate.activity.TgProductListActivity;
import com.omneagate.printer.Usb_Printer;

/**
 * Created by root on 28/2/17.
 */
public class QuantityEnterDailog extends Dialog implements View.OnClickListener {
    private EditText edtSelectedCommodity,edtQuantity;
    private Button btCancel,btSubmit;
    private Context context;
    private int position;
    private Product product;
    TextView unit;

    public QuantityEnterDailog(Context context, Product product ,int position) {
        super(context);
        this.context=context;
        this.product=product;
        this.position=position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_quantity_entered);
        setCancelable(false);
        initView();

//        Weight in
    }
    private void initView(){
        edtSelectedCommodity=(EditText)findViewById(R.id.edtSelectedCommodity);
        unit=(TextView) findViewById(R.id.unit);
        /*if (GlobalAppState.language.equalsIgnoreCase("te")) {
            unit.setText("Weight in "+product.getUnitName());
        } else {
            unit.setText("Weight in "+product.getUnitName());
        }*/

        unit.setText(context.getResources().getString(R.string.weight_in_kg)+" "+product.getUnitName());

        edtQuantity=(EditText)findViewById(R.id.edtQuantity);
        btCancel=(Button) findViewById(R.id.btCancel);
        btSubmit=(Button) findViewById(R.id.btSubmit);
        btCancel.setOnClickListener(this);
        btSubmit.setOnClickListener(this);

        edtSelectedCommodity.setText(product.getDisplayName());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btSubmit:
                try {
                    if (edtQuantity.getText().toString() != null && !edtQuantity.getText().toString().equals("")) {
                        String firstPart = null;
                        String secondPart = null;
                        String number = edtQuantity.getText().toString();
                        int idx = number.indexOf(".");
                        if (idx > -1) {
                            firstPart = number.substring(0, idx);
                            secondPart = number.substring(idx + 1, number.length());
                        }
                        Log.e("firstPart >>>> ", "" + firstPart);
                        Log.e("secondPart >>>> ", "" + secondPart);
                    /*String[] numSplit=number.split(".");

                    if(numSplit!=null && numSplit.length>1) {
                        if(numSplit.length==2) {
                            firstPart = numSplit[0];
                            secondPart = numSplit[1];
                        }else{
                            firstPart = numSplit[0];
                        }
                    }*/
                        if (secondPart != null) {
                            int secondPartInt = Integer.parseInt(secondPart.trim());
                            Log.e("secondPartInt >>>> ", "" + secondPartInt);
                            if (secondPartInt > 25) {
                                Toast.makeText(context, context.getString(R.string.invalid_quantity), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        Double d = new Double(number);
                        int userReceived = d.intValue();

                        if (userReceived > product.getProductBalanceQty()) {
                            Toast.makeText(context, context.getString(R.string.quantity_exceeds), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (userReceived > product.getClosingBalance()) {
                            Toast.makeText(context, context.getString(R.string.stock_not_available), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (userReceived < 0) {
                            Toast.makeText(context, context.getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ((TgProductListActivity) context).setPurchasedQuantity(position, userReceived);
                    }
                    dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.invalid_quantity), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btCancel:
                dismiss();
                break;
        }
    }
}
