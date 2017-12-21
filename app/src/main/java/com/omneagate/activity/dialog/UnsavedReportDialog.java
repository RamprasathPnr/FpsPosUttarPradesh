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

import com.omneagate.DTO.FindingCriteriaDto;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.InspectionDashboardActivity;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class UnsavedReportDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public UnsavedReportDialog(Activity _context) {
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
        setContentView(R.layout.dialog_unsaved_inspection_report);
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
                Util.findingCriteriaDto = new FindingCriteriaDto();
                context.finish();
                context.startActivity(new Intent(context, InspectionDashboardActivity.class));
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