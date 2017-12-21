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
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.SalesSummaryWithOutOTPActivity;

public class PaymentModeDialog extends Dialog implements View.OnClickListener{
    //Activity context
    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public PaymentModeDialog(Activity _context) {
        super(_context);
        context = _context;

    }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            setContentView(R.layout.payment_dialog);
            setCancelable(false);
         // setTamilText((TextView) findViewById(R.id.tvResponseTitle), R.string.openingstocktitle);
         // setTamilText((TextView) findViewById(R.id.tvloginBack), R.string.initalstockUpdation);
            Button buttonCash = (Button) findViewById(R.id.buttonCash);
            //setTamilText(okButton, R.string.ok);
            buttonCash.setOnClickListener(this);

            Button buttonCard = (Button) findViewById(R.id.buttonCard);
            //setTamilText(okButton, R.string.ok);
            buttonCard.setOnClickListener(this);
        }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonCash:
                ((SalesSummaryWithOutOTPActivity)context).submitBill();
                dismiss();
                break;

            case R.id.buttonCard:
                ((SalesSummaryWithOutOTPActivity)context).submitCardPayment();
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
