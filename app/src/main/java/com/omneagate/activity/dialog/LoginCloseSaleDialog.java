package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class LoginCloseSaleDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public LoginCloseSaleDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_login_closesale);
        setCancelable(false);

        Button yesButton = (Button) findViewById(R.id.buttonYes);
        yesButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonYes:
                dismiss();
                break;
        }
    }

}