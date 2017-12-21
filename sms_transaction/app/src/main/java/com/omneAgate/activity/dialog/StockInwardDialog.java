package com.omneagate.activity.dialog;

/**
 * Created by user1 on 17/3/15.
 */


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
import com.omneagate.activity.FpsStockInwardActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

//Stock Inward Dialog
public class StockInwardDialog extends Dialog implements
        View.OnClickListener {

    //Activity context
    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public StockInwardDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_stock_inward_response);
        setCancelable(false);
        TextView message = (TextView) findViewById(R.id.textViewMessage);
        Util.setTamilText((TextView) findViewById(R.id.tvResponseTitle), R.string.stockInwardResponseTitle);
        Util.setTamilText(message, R.string.messageStockInwardupdate);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Util.setTamilText(okButton, R.string.ok);
        okButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonNwOk:
                dismiss();
                context.startActivity(new Intent(context, FpsStockInwardActivity.class));
                context.finish();
                break;

            default:
                dismiss();
                break;
        }
    }


    //Tamil text textView typeface
    private void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equals("ta")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
        } else {
            textName.setText(context.getString(id));
        }
    }


}
