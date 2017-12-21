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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class LanguageSelectionDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user
    private RadioGroup radioGroupLanguage;  //    Context from the user

    /*Constructor class for this dialog*/
    public LanguageSelectionDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_language);
        setCancelable(false);
        ((TextView) findViewById(R.id.textViewNwTitle)).setText("Language");
        Util.setTamilText((RadioButton) findViewById(R.id.radioGroupTamil), R.string.tamil);
        radioGroupLanguage = (RadioGroup) findViewById
                (R.id.radioGroup1);
        if (!GlobalAppState.language.equals("ta")) {
            ((RadioButton) findViewById(R.id.radioGroupEnglish)).setChecked(true);
        }
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Util.setTamilText(okButton, R.string.ok);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        Util.setTamilText(cancelButton, R.string.cancel);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                changeLanguage();
                dismiss();
                break;
            case R.id.buttonNwCancel:
                dismiss();
                break;
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(RadioButton textName, int id) {
        Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
        textName.setTypeface(tfBamini);
        textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
    }

    //Re-starts the application where language change take effects
    private void restartApplication() {
        Intent restart = context.getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(context.getBaseContext().getPackageName());
        restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(restart);
    }

    /**
     * Used to change language
     */
    private void changeLanguage() {

        int selected =
                radioGroupLanguage.getCheckedRadioButtonId();
        if (selected == R.id.radioGroupTamil) {
            Util.changeLanguage(context, "ta");
        } else {
            Util.changeLanguage(context, "en");
        }
        restartApplication();
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
}