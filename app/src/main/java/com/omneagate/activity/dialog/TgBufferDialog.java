package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.activity.AadharCardSalesActivity;
import com.omneagate.activity.BenefBfdScanActivity;
import com.omneagate.activity.OtpReqEnterActivity;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class TgBufferDialog extends Dialog  {

    private final Activity context;  //    Context from the user
    private ImageView image;
    private String status;
    private TextView tvStatus;
    /*Constructor class for this dialog*/
    public TgBufferDialog(Activity _context,String loadingStatus) {
        super(_context);
        this.context = _context;
        this.status = loadingStatus;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.tg_buffer);

        setCancelable(false);
        tvStatus=(TextView)findViewById(R.id.textViewNwTextSecond);
        tvStatus.setText(""+status);
        image=(ImageView) findViewById(R.id.textViewNwTitle);
        PlayGifView pGif = (PlayGifView) findViewById(R.id.viewGif);
        pGif.setImageResource(R.drawable.loading);



}
}