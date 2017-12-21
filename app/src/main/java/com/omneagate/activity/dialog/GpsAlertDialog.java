package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.Util.Util;
import com.omneagate.activity.AdminActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.LoginActivity;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class GpsAlertDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public GpsAlertDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_gps_alert);
        setCancelable(false);
        Button yesButton = (Button) findViewById(R.id.buttonYes);
        Button noButton = (Button) findViewById(R.id.buttonNo);

        String continueString, warningTxt;
        if(GlobalAppState.language.equalsIgnoreCase("hi")) {
            Util.setTamilText(yesButton, "ठीक");
            Util.setTamilText(noButton, "रद्द करना");
            continueString = "पर स्विच करें GPS आपके डिवाइस में";
            warningTxt = "चेतावनी";
        }
        else {
            Util.setTamilText(yesButton, "Ok");
            Util.setTamilText(noButton, "Cancel");
            continueString = "Please switch on Gps in your device";
            warningTxt = "Warning !";
        }
        Util.setTamilText(((TextView) findViewById(R.id.tvContinue)), continueString);
        Util.setTamilText(((TextView) findViewById(R.id.tvWaring)), warningTxt);

        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonYes:
                dismiss();
                ((LoginActivity) context).turnGPSOn();
                break;
            case R.id.buttonNo:
                dismiss();
                break;
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, String id) {
        textName.setText(id);
    }


}