package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
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

public class UnackAdjustmentDialog extends Dialog implements
        View.OnClickListener {

    //Activity context
    private final Activity context;  //    Context from the user
    int unackAdjustment;

    /*Constructor class for this dialog*/
    public UnackAdjustmentDialog(Activity _context, int _unackAdjustment) {
        super(_context);
        context = _context;
        unackAdjustment = _unackAdjustment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_unack_adjustment);
        setCancelable(false);
        TextView message = (TextView) findViewById(R.id.textViewMessage);
//        Util.setTamilText((TextView) findViewById(R.id.tvResponseTitle), R.string.stockInwardResponseTitle);
//        Util.setTamilText(message, unackAdjustment+R.string.unack_adjustment);
        message.setText(""+unackAdjustment+" "+context.getResources().getString(R.string.unack_adjustment));
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Util.setTamilText(okButton, R.string.ok);
        okButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
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
