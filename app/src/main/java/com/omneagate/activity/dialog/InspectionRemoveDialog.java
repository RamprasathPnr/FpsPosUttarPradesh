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

/**
 * This dialog will appear on the time of user logout
 */
public class InspectionRemoveDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user
    int serialNo;
    String pName;
    String classname;

    /*Constructor class for this dialog*/
    public InspectionRemoveDialog(Activity _context, int sNo, String productName, String classname) {
        super(_context);
        context = _context;
        serialNo = sNo;
        pName = productName;
        this.classname = classname;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_stock_inspection_remove);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Util.setTamilText(((TextView) findViewById(R.id.tvWaring)), R.string.warning);
        setmessage();
        Button yesButton = (Button) findViewById(R.id.buttonYes);
        Util.setTamilText(yesButton, R.string.yes);
        yesButton.setOnClickListener(this);
        Button noButton = (Button) findViewById(R.id.buttonNo);
        Util.setTamilText(noButton, R.string.no);
        noButton.setOnClickListener(this);
    }

    private void setmessage() {
        int message = 0;
        switch (classname) {
            case "StockInspectionActivity":
                message = R.string.stockInspectionRemoveAlert;
                break;
            case "CardInspectionActivity":
                message = R.string.cardInspectionRemoveAlert;
                break;
            case "WeighmentInspectionActivity":
                message = R.string.weightInspectionRemoveAlert;
                break;
            case "ShopOpenCloseActivity":
                message = R.string.shop_OtherInspectionRemoveAlert;
                break;
            case "OtherInspectionActivity":
                message = R.string.shop_OtherInspectionRemoveAlert;
                break;
        }
        Util.setTamilText(((TextView) findViewById(R.id.tvloginBack)), message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNo:
                dismiss();
                break;
            case R.id.buttonYes:
                switch (classname) {
                    case "StockInspectionActivity":
                        Util.findingCriteriaDto.getStockInspection().remove(serialNo);
                        break;
                    case "CardInspectionActivity":
                        Util.findingCriteriaDto.getCardInspection().remove(serialNo);
                        break;
                    case "WeighmentInspectionActivity":
                        Util.findingCriteriaDto.getWeighmentInspection().remove(serialNo);
                        break;
                    case "ShopOpenCloseActivity":
                        Util.findingCriteriaDto.getShopInpsection().remove(serialNo);
                        break;
                    case "OtherInspectionActivity":
                        Util.findingCriteriaDto.getOtherInsection().remove(serialNo);
                        break;
                }
                dismiss();
                Intent intent = null;
                try {
                    intent = new Intent(context, Class.forName("com.omneagate.activity." + classname));
                    context.startActivity(intent);
                    context.finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equals("ta")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
        } else {
            textName.setText(context.getString(id));
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, String text) {
        if (GlobalAppState.language.equals("ta")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, text));
        } else {
            textName.setText(text);
        }
    }
}