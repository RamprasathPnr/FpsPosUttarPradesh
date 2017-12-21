package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.omneagate.activity.InspectionDashboardActivity;
import com.omneagate.activity.R;
import com.omneagate.activity.ReconciliationManualsyncActivity;


public class ReconciliationSuccessDialog extends Dialog implements View.OnClickListener {

    private final Activity context;

    public ReconciliationSuccessDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_reconciliation_success);
        setCancelable(false);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
//                context.startActivity(new Intent(context, ReconciliationManualsyncActivity.class));
                context.finish();
                break;

            default:
                break;
        }
    }
}
