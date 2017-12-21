package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.omneagate.Util.TamilUtil;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;

/**
 * This dialog will appear on the time of user login
 */
public class PurgeBillBItemDialog extends Dialog implements
        View.OnClickListener {

    //    Context from the user
    private final Activity context;

    /*Constructor class for this dialog*/
    public PurgeBillBItemDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_purge_billbitem);
        setCancelable(false);
        Util.setTamilText((TextView) findViewById(R.id.textViewNwTitle), R.string.purgeBillBItem);
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        int purgeBillBItem = prefs.getInt("purgeBill", 30);
        ((EditText) findViewById(R.id.editTextNoOfDays)).setText(purgeBillBItem + "");
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

                if (storeInLocal()) {
                    dismiss();
                }
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
     * Store changed ip in shared preference
     * returns true if value present else false
     */
    private boolean storeInLocal() {
        String noOfDays = ((EditText) findViewById(R.id.editTextNoOfDays)).getText().toString().trim();
        if (StringUtils.isEmpty(noOfDays) || noOfDays == null) {
            return false;
        }
        SharedPreferences prefs = context.getSharedPreferences("FPS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("purgeBill", Integer.parseInt(noOfDays));
        editor.commit();
        return true;
    }
}

