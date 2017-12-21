package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.activity.MembersAadharRegistrationActivity;
import com.omneagate.activity.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This dialog will appear on the time of user logout
 */
public class DateOfBirthSelectionDialog extends Dialog implements
        View.OnClickListener {



    private final Activity context;  //    Context from the user

    MaterialCalendarView widget;//

    /*Constructor class for this dialog*/
    public DateOfBirthSelectionDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_date_selection);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.HOUR_OF_DAY, 23);
        maxDate.set(Calendar.MINUTE, 59);
        widget.setMaximumDate(new Date());
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        cancelButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                Log.e("DOBSelectionDialog", ""+sdf.format(widget.getSelectedDate().getDate()));

                Log.e("getSelectedDate", ""+widget.getSelectedDate().getDate());

                String str = sdf.format(widget.getSelectedDate().getDate());

                try {
                    Date d = sdf.parse(str);
                    long milliseconds = d.getTime();
                   // ((MembersAadharRegistrationActivity) context).setTextDate(sdf.format(widget.getSelectedDate().getDate()) , milliseconds);

                }catch (ParseException e){

                }



                dismiss();


                break;
            case R.id.buttonNwCancel:
                dismiss();
                break;
        }
    }
}