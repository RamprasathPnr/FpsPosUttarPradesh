package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.activity.R;

/**
 * Created by root on 20/2/17.
 */
public class RationCardDetailDialog extends Dialog {

    private final Activity context;
    private TextView textView;
    private String loadingStatus;


    public RationCardDetailDialog(Activity _context,String loadingStatus) {
        super(_context);
        context = _context;
        this.loadingStatus=loadingStatus;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.tg_loading_dialog);
        textView = (TextView)findViewById(R.id.textViewStatus);
        textView.setText(loadingStatus);
        setCancelable(false);
        PlayGifView pGif = (PlayGifView) findViewById(R.id.viewGif);
        pGif.setImageResource(R.drawable.loading);

    }

}