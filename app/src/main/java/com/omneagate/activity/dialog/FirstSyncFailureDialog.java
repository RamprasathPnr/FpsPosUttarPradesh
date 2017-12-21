package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.activity.R;
import com.omneagate.activity.SyncPageActivity;

/**
 * This dialog will appear on the time of user logout
 */
public class FirstSyncFailureDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user
    String errorMessage;

    /*Constructor class for this dialog*/
    public FirstSyncFailureDialog(Activity _context, String errorMsg) {
        super(_context);
        context = _context;
        errorMessage = errorMsg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_first_sync_failure);
        setCancelable(false);
        TextView message = (TextView) findViewById(R.id.textViewNwText);
        String userText = errorMessage;
        ((TextView) findViewById(R.id.textViewNwTitle)).setText(context.getString(R.string.sync_failed));
        message.setText(userText);
//        userText = context.getString(R.string.contact_helpdesk);
//        ((TextView) findViewById(R.id.textViewNwTextSecond)).setText(userText);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                ((SyncPageActivity) context).logOut();
                dismiss();
                break;
        }
    }


}