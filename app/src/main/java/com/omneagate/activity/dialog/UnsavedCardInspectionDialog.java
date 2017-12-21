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

import com.omneagate.DTO.POSOperatingHoursDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.InspectionFindingActivity;
import com.omneagate.activity.R;
import com.omneagate.activity.StockInspectionActivity;
import com.omneagate.activity.TransactionCommodityActivity;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This dialog will appear on the time of user logout
 */
public class UnsavedCardInspectionDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public UnsavedCardInspectionDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    String openTime = "";
    String timePeriod = "AM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_unsaved_stock_inspection);
        setCancelable(false);
        Util.setTamilText(((TextView) findViewById(R.id.tvWaring)), R.string.warning);
        Button yesButton = (Button) findViewById(R.id.buttonYes);
        Util.setTamilText(yesButton, R.string.yes);
        yesButton.setOnClickListener(this);
        Button noButton = (Button) findViewById(R.id.buttonNo);
        Util.setTamilText(noButton, R.string.no);
        noButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNo:
                dismiss();
                break;

            case R.id.buttonYes:
                dismiss();
                String userName = context.getIntent().getStringExtra("UserName");
                Intent intent = new Intent(context, InspectionFindingActivity.class);
                intent.putExtra("UserName", userName);
                context.startActivity(intent);
                context.finish();
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