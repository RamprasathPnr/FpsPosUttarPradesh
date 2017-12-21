package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
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

    private int language;

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
        ((TextView) findViewById(R.id.textViewNwTitle)).setText(context.getString(R.string.languageSelection));
//        Util.setTamilText((TextView) findViewById(R.id.textViewNwTitleTamil), R.string.languageSelectionTa);
        Util.setTamilText((TextView) findViewById(R.id.textViewNwTitleHindi), R.string.languageSelectionHi);
//        Util.setTamilText((TextView) findViewById(R.id.tamilText), R.string.tamil);
        Util.setTamilText((TextView) findViewById(R.id.hindiText), R.string.hindi);
        if (GlobalAppState.language.equals("hi")) {
            language = 2;
        }
        else if (GlobalAppState.language.equals("hi")) {
            language = 1;
        }
        else {
            language = 0;
        }

        Util.LoggingQueue(context, "Dialog language", "Language Selection");
        setBackGround(language);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        cancelButton.setOnClickListener(this);
        findViewById(R.id.language_english).setOnClickListener(this);
//        findViewById(R.id.language_tamil).setOnClickListener(this);
        findViewById(R.id.language_hindi).setOnClickListener(this);
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
            case R.id.language_english:
                language = 0;
                setBackGround(language);
                break;
            /*case R.id.language_tamil:
                language = 1;
                setBackGround(language);
                break;*/
            case R.id.language_hindi:
                language = 2;
                setBackGround(language);
                break;
        }
    }

    private void setBackGround(int lang) {
        if (lang == 2) {
            Util.LoggingQueue(context, "Current language", "Hindi");
            findViewById(R.id.hindiSelection).setVisibility(View.VISIBLE);
            findViewById(R.id.englishSelection).setVisibility(View.GONE);
//            findViewById(R.id.tamilSelection).setVisibility(View.GONE);
            findViewById(R.id.language_english).setBackgroundResource(R.drawable.background_grey);
//            findViewById(R.id.language_tamil).setBackgroundResource(R.drawable.background_grey);
            findViewById(R.id.language_hindi).setBackgroundResource(R.drawable.background_pink);
        } else if (lang == 1) {
            Util.LoggingQueue(context, "Current language", "Tamil");
//            findViewById(R.id.tamilSelection).setVisibility(View.VISIBLE);
            findViewById(R.id.englishSelection).setVisibility(View.GONE);
            findViewById(R.id.language_english).setBackgroundResource(R.drawable.background_grey);
            findViewById(R.id.language_hindi).setBackgroundResource(R.drawable.background_grey);
//            findViewById(R.id.language_tamil).setBackgroundResource(R.drawable.background_pink);
        } else if (lang == 0) {
            Util.LoggingQueue(context, "Current", "English");
//            findViewById(R.id.tamilSelection).setVisibility(View.GONE);
            findViewById(R.id.englishSelection).setVisibility(View.VISIBLE);
            findViewById(R.id.hindiSelection).setVisibility(View.GONE);
            findViewById(R.id.language_english).setBackgroundResource(R.drawable.background_pink);
//            findViewById(R.id.language_tamil).setBackgroundResource(R.drawable.background_grey);
            findViewById(R.id.language_hindi).setBackgroundResource(R.drawable.background_grey);
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
        if (language == 2) {
            Util.LoggingQueue(context, "Selected", "Hindi");
            Util.changeLanguage(context, "hi");
        } else if (language == 1) {
            Util.LoggingQueue(context, "Selected", "Tamil");
            Util.changeLanguage(context, "hi");
        } else if (language == 0) {
            Util.LoggingQueue(context, "Selected", "English");
            Util.changeLanguage(context, "en");
        }
        restartApplication();
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        Log.e("LangSelectionDialog", "Util.setTamilText , id passing");
        Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
        textName.setTypeface(tfBamini);
        textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
    }
}