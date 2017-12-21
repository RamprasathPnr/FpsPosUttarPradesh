package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class NumberSelectionDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user
    int value;
    private String header;

    /*Constructor class for this dialog*/
    public NumberSelectionDialog(Activity _context, String header, int value) {
        super(_context);
        context = _context;
        this.header = header;
        this.value = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_number_picker);
        setCancelable(false);
        ((TextView) findViewById(R.id.textViewNwTitle)).setText(header);
        ((TextView) findViewById(R.id.textViewNwHead)).setText("Number of " + header);
        NumberPicker np = (NumberPicker) findViewById(R.id.np);
        np.setMaxValue(20);
        np.setValue(value);
        np.setMinValue(0);
        np.setWrapSelectorWheel(true);
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
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equals("hi")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
        } else {
            textName.setText(context.getString(id));
        }
    }
}