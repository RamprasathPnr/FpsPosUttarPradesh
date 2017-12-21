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


//Stock Inward Dialog
public class CompleteObservationDialog extends Dialog implements
        View.OnClickListener {

    //Activity context
    private final Activity context;  //    Context from the user

    //Username
    private String userName = "";

    //Type of Inspection
    private String typeOfInspection="";

    /*Constructor class for this dialog*/
    public CompleteObservationDialog(Activity _context, String userName, String typeOfInspection) {
        super(_context);
        context = _context;
        this.userName = userName;
        this.typeOfInspection = typeOfInspection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_complete_finding);
        setCancelable(false);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonNwOk:
                dismiss();
//                if(typeOfInspection.equalsIgnoreCase("unscheduled")){
                    context.startActivity(new Intent(context, InspectionDashboardActivity.class).putExtra("userName",userName));
                    context.finish();
                /*}else{
                    context.startActivity(new Intent(context, ScheduledInspectionListActivity.class).putExtra("userName",userName));
                    context.finish();
                }*/

                break;

            default:
                dismiss();
                break;
        }
    }





}
