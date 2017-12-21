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

import java.util.List;

/**
 * This dialog will appear on the time of user logout
 */
public class RetryFailedMasterDataDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user

    List<String> masterData;

    /*Constructor class for this dialog*/
    public RetryFailedMasterDataDialog(Activity _context, List<String> masterData) {
        super(_context);
        context = _context;
        this.masterData = masterData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_retry_failed);
        setCancelable(false);
        TextView message = (TextView) findViewById(R.id.textViewNwText);
        String userText = "Following master tables missing";
        ((TextView) findViewById(R.id.textViewNwTitle)).setText("Master Table Missing \n" + masterData());
        message.setText(userText);
        userText = "Please contact HelpDesk";
        ((TextView) findViewById(R.id.textViewNwTextSecond)).setText(userText);
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

    private String masterData() {
        String data = "";
        for (String masters : masterData) {
            data = data + masters + "\n";
        }
        return data;
    }
}