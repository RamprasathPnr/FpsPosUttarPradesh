package com.omneagate.activity.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.StockManagementActivity;


//Stock Manual Inward Dialog
public class StockAdjustmentDialog extends Dialog implements
        View.OnClickListener {

    //Activity context
    private final Activity context;  //    Context from the user

//    List<StockRequestDto.ProductList> prods;

    /*Constructor class for this dialog*/
    public StockAdjustmentDialog(Activity _context) {
        super(_context);
        context = _context;
//        prods = productList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_stock_inward_response);
        setCancelable(false);
//        FPSDBHelper.getInstance(context).stockAdjustmentUpdate(prods);
        TextView message = (TextView) findViewById(R.id.textViewMessage);
        Util.setTamilText((TextView) findViewById(R.id.tvResponseTitle), R.string.stockAdjustmentTitle);
        Util.setTamilText(message, R.string.messageAdjustUpdated);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Util.setTamilText(okButton, R.string.ok);
        okButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonNwOk:
                dismiss();
                context.startActivity(new Intent(context, StockManagementActivity.class));
                context.finish();
                break;

            default:
                dismiss();
                break;
        }
    }


    //Tamil text textView typeface
    private void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equals("hi")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
        } else {
            textName.setText(context.getString(id));
        }
    }


}
