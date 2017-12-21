package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.omneagate.DTO.BillDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.R;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EntitlementCompletedAlertDialog extends Dialog implements View.OnClickListener {
    Context dialogContext;
    Button okButton;
    ArrayList<String> productsOutOfStock;

    public EntitlementCompletedAlertDialog(Context context, ArrayList<String> products) {
        super(context);
        dialogContext = context;
        productsOutOfStock = new ArrayList<String>();
        this.productsOutOfStock = products;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_out_of_stock_alert);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setCancelable(false);
        ImageView warningIcon = (ImageView) findViewById(R.id.alertTitleIcon);
        warningIcon.setVisibility(View.VISIBLE);
        okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(this);
        Util.setTamilText((Button) findViewById(R.id.okButton), R.string.ok);
        Util.setTamilText((TextView) findViewById(R.id.alertTitleText), R.string.warning);
        Util.setTamilText((TextView) findViewById(R.id.alertMsgText), R.string.entitlemnt_finished_products );


        ListView list = (ListView) findViewById(R.id.listView1);
        list.setAdapter(new OutOfStockListAdapter(dialogContext, productsOutOfStock));


    }

    public void setTamilText(TextView textName, int id) {
        if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi")) {
            Typeface tfBamini = Typeface.createFromAsset(dialogContext.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini, Typeface.BOLD);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, dialogContext.getString(id)));
        } else {
            textName.setText(dialogContext.getString(id));
        }
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) dialogContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        switch (v.getId()) {
            case R.id.okButton:
                dismiss();
                break;
            default:
                break;
        }

    }

}
