package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * This dialog will appear on the time of user logout
 */
public class RetrieveLocalDbDialog extends Dialog implements
        View.OnClickListener {

    private final Activity context;  //    Context from the user
    File sourcePath, destinationPath;

    /*Constructor class for this dialog*/
    public RetrieveLocalDbDialog(Activity _context, File source, File destination) {
        super(_context);
        context = _context;
        sourcePath = source;
        destinationPath = destination;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_retrieve_db);
        setCancelable(false);
        TextView message = (TextView) findViewById(R.id.textViewNwText);
        Util.setTamilText((TextView) findViewById(R.id.textViewNwTitle), R.string.retrieveDbHeading);
//        Util.setTamilText(message, R.string.retrieveDbString);
        message.setText(R.string.retrieveDbString);
        message.setTextSize(17);
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
                try {
                    FileChannel source = new FileInputStream(sourcePath).getChannel();
                    FileChannel destination = new FileOutputStream(destinationPath).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                } catch (IOException e) {
                    Log.e("source recovery", e.toString());
                }
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
    private void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
        } else {
            textName.setText(context.getString(id));
        }
    }
}