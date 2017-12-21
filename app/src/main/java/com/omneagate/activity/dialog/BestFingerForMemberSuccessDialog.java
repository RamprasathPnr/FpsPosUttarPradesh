package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;

import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.Button;


import com.omneagate.activity.R;
import com.omneagate.activity.TgDashBoardActivity;
import com.omneagate.activity.TgFpsMembersActivity;
import com.omneagate.activity.TgSalesEntryActivity;

public class BestFingerForMemberSuccessDialog extends Dialog implements
        View.OnClickListener {


    private final Activity context;  //    Context from the user
    String activityName;
    /*Constructor class for this dialog*/
    public BestFingerForMemberSuccessDialog(Activity _context,String activityName) {
        super(_context);
        context = _context;
        this.activityName=activityName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.activity_best_finger_for_member_success_dialog);
        setCancelable(false);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
                if(activityName.equalsIgnoreCase("TgTenFingerRegistrationActivity")){
                    context.startActivity(new Intent(context, TgDashBoardActivity.class));
                }else if (activityName.equalsIgnoreCase("TgTenFingerFpsMemberRegistrationActivity")){
                    context.startActivity(new Intent(context, TgFpsMembersActivity.class));
                }
                break;
        }
    }








}
