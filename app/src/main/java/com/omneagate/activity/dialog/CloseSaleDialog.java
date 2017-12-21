package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.DTO.POSOperatingHoursDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.TransactionCommodityActivity;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This dialog will appear on the time of user logout
 */
public class CloseSaleDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public CloseSaleDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    String openTime = "";
    String timePeriod = "AM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_closesale);
        setCancelable(false);
        Util.setTamilText(((TextView) findViewById(R.id.tvWaring)), R.string.caution);


        try {
            if (SessionId.getInstance().getUserId() == 0) {

            } else {

                //openTime = "" + FPSDBHelper.getInstance(context).getOpeningTime(SessionId.getInstance().getUserId());

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat dayDateFormat = new SimpleDateFormat("EEE");

                String currentDayStr = "" + dayDateFormat.format(calendar.getTime());

                Util.LoggingQueue(context, "CloseSaleDialog", "Current Day = " + currentDayStr);
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                String nextDayStr = "" + dayDateFormat.format(calendar.getTime());
                Util.LoggingQueue(context, "CloseSaleDialog", "Next Day = " + nextDayStr);

                POSOperatingHoursDto posOperatingHoursDto = FPSDBHelper.getInstance(context).getPOSOperatingHoursForToday(nextDayStr);


                if (posOperatingHoursDto.getFirstSessionOpeningTime() != null) {

                    Util.LoggingQueue(context, "CloseSaleDialog", "getFirstSessionOpeningTime = " + posOperatingHoursDto.getFirstSessionOpeningTime());

                    String firstSessionOpeningTimeStr = posOperatingHoursDto.getFirstSessionOpeningTime();
                    String[] openingHour1 = StringUtils.split(firstSessionOpeningTimeStr, ":");
                    String opHours1 = "AM";
                    int opens1 = Integer.parseInt(openingHour1[0]);
                    if (opens1 > 12) {
                        opHours1 = "PM";
                        opens1 = opens1 % 12;
                    }


                    String operationHour1 = opens1 + ":" + openingHour1[1] ;
                    //String operationHour1 = opens1 + ":" + openingHour1[1] + opHours1;
                    timePeriod = opHours1;

                    openTime = operationHour1;
                } else if (openTime.isEmpty() || openTime == null) {
                    Util.LoggingQueue(context, "CloseSaleDialog", "openTime.isEmpty() = || openTime == null");

                    if (posOperatingHoursDto.getSecondSessionOpeningTime() != null) {


                        Util.LoggingQueue(context, "CloseSaleDialog", "getSecondSessionOpeningTime = " + posOperatingHoursDto.getSecondSessionOpeningTime());

                        String secondSessionOpeningTimeStr = posOperatingHoursDto.getSecondSessionOpeningTime();
                        String[] openingHour = StringUtils.split(secondSessionOpeningTimeStr, ":");
                        String opHours = "AM";
                        int opens = Integer.parseInt(openingHour[0]);
                        if (opens > 12) {
                            opHours = "PM";
                            opens = opens % 12;
                        }


                        // String operationHour = opens + ":" + openingHour[1] + opHours;
                        String operationHour = opens + ":" + openingHour[1];
                        openTime = operationHour;
                        timePeriod = opHours;

                    } else {
                        openTime = "";
                        timePeriod = "";
                    }

                }
            }
        }catch (Exception e){

            Util.LoggingQueue(context, "CloseSaleDialog", "Exception = " + e);


        }

        Util.setTamilText(((TextView) findViewById(R.id.tvloginBack)), context.getString(R.string.loginBack));
        Util.LoggingQueue(context, "CloseSaleDialog", "timePeriod = " + timePeriod);

        /*if(timePeriod.equalsIgnoreCase("AM")){
            Util.setTamilText(((TextView) findViewById(R.id.tvNextDay)), context.getString(R.string.nextMorning) + " " + openTime );


            ((TextView) findViewById(R.id.tvNextDayPeriod)).setText( " " + timePeriod);


        }else if(timePeriod.equalsIgnoreCase("PM")){

            Util.setTamilText(((TextView) findViewById(R.id.tvNextDay)), context.getString(R.string.nextAfternoon) + " " + openTime );
            ((TextView) findViewById(R.id.tvNextDayPeriod)).setText(" " + timePeriod);

        }*/

//        Util.setTamilText(((TextView) findViewById(R.id.tvContinue)), R.string.continues);
        Button yesButton = (Button) findViewById(R.id.buttonYes);
        Util.setTamilText(yesButton, R.string.yes);
        yesButton.setOnClickListener(this);
        Util.LoggingQueue(context, "Close sale dialog", "Dialog starting up");
        Button noButton = (Button) findViewById(R.id.buttonNo);
        Util.setTamilText(noButton, R.string.no);
        noButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNo:
                dismiss();
                break;

            case R.id.buttonYes:
                submitCloseSale();
                dismiss();
                break;
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equals("hi")) {
//            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
//            textName.setTypeface(tfBamini);
//            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
            textName.setText(context.getString(id));
        } else {
            textName.setText(context.getString(id));
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, String text) {
        if (GlobalAppState.language.equals("hi")) {
//            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
//            textName.setTypeface(tfBamini);
//            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, text));
            textName.setText(text);
        } else {
            textName.setText(text);
        }
    }


    private void submitCloseSale() {
        int count = FPSDBHelper.getInstance(context).totalBillsToday();
        Double sum_amount = FPSDBHelper.getInstance(context).totalAmountToday();
        Util.LoggingQueue(context, "Close sale dialog", "Sale closed:bill count" + count + ":tot Amt:" + sum_amount);
        ((TransactionCommodityActivity) context).getUserPassword();
    }
}