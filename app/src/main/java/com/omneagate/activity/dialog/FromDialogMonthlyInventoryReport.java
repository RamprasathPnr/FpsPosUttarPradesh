package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.activity.BillSearchActivity;
import com.omneagate.activity.MonthlyInventoryReportActivity;
import com.omneagate.activity.MonthlySalesReportActivity;
import com.omneagate.activity.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This dialog will appear on the time of user logout
 */
public class FromDialogMonthlyInventoryReport extends Dialog implements View.OnClickListener {


    private final Activity context;  //    Context from the user

    MaterialCalendarView widget;//

    Boolean enableDateSelection = false;
    String minDate = null;
    Boolean isShopCreatedDaysExceeds90 = false;

    /*Constructor class for this dialog*/
    public FromDialogMonthlyInventoryReport(Activity _context) {
        super(_context);
        context = _context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_date_selection);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        widget.setMaximumDate(new Date());
        try {
            String createdDate = FPSDBHelper.getInstance(context).getFPSStoreCreatedDate(SessionId.getInstance().getUserId());
            Log.e("FromDialogMonthlyIn" + "", "createdDate = " + createdDate);
            if (createdDate != null && StringUtils.isNotEmpty(createdDate)) {
                Log.e("createdDate" + " IF ", " = " + createdDate);
                Long timeInMillSec = Long.valueOf(createdDate);
                Date d = new Date(timeInMillSec);
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                minDate = ft.format(d);
                Log.e("createdDate" + " minDate ", " = " + minDate);
                String arr[] = minDate.split("-");
                Calendar presentCalendar = Calendar.getInstance();
                Date past = new Date(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2])); // June 20th, 2010
                Date today = new Date(presentCalendar.get(Calendar.YEAR), presentCalendar.get(Calendar.MONTH), presentCalendar.get(Calendar.DAY_OF_MONTH)); // July 24th
                int days = Days.daysBetween(new DateTime(past), new DateTime(today)).getDays(); // => 34
                Log.e("" + " days ", " = " + days);
                if (days >= 90) {
                    isShopCreatedDaysExceeds90 = true;
                    Date today2 = new Date();
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
                    String date = DATE_FORMAT.format(today2);
                    Log.e("" + "date ", " = " + date);
                    minDate = date;
                } else {
                    isShopCreatedDaysExceeds90 = false;
                }
                minDate = minDate.replace("-", "~");
                Log.e("createdDate" + "!! minDate ", " = " + minDate);
            } else {
                Log.e("createdDate" + "", "ELSE = " + createdDate);
            }
        } catch (Exception e) {}

        if ((minDate != null) && (!minDate.equalsIgnoreCase(""))) {
            Log.e("createdDate" + "++ minDate ", " = " + minDate);
            String arr[] = minDate.split("~");
            DateTime today = new DateTime();
            Calendar cal = Calendar.getInstance();
            if (isShopCreatedDaysExceeds90) {
                Log.e("" + "isShopCreatedDaysEe90 ", " = " + minDate);
                cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 90);
            } else {
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));
                cal.set(Calendar.MONTH, Integer.parseInt(arr[1]) - 1);
                cal.set(Calendar.YEAR, Integer.parseInt(arr[0]));
            }
            widget.setMinimumDate(cal);
            enableDateSelection = true;
        } else {
            enableDateSelection = false;
            Toast.makeText(context, R.string.no_transaction, Toast.LENGTH_SHORT).show();
           /* DateTime today = new DateTime();
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, today.getDayOfMonth() + 1);
            String days = FPSDBHelper.getInstance(context).getMasterData("purgeBill");
            int purgeDays = Integer.parseInt(days);
            if(purgeDays == 90) {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 3);
            }
            else if(purgeDays == 60) {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 2);
            }
            else if(purgeDays == 30) {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
            }
            cal.set(Calendar.YEAR, today.getYear());
            widget.setMinimumDate(cal);*/
        }
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:

                if (enableDateSelection) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    ((MonthlyInventoryReportActivity) context).setFromTextDate(sdf.format(widget.getSelectedDate().getDate()));

                } else {
                    // Toast.makeText(context, "No transactions available", Toast.LENGTH_SHORT).show();

                }
                dismiss();
                break;
            case R.id.buttonNwCancel:
                dismiss();
                break;
        }
    }

}