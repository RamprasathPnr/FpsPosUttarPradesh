package com.omneagate.activity.dialog;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.omneagate.activity.R;

import java.util.ArrayList;

/**
 * Created by user1 on 18-06-2016.
 */


public class GenderSelectListAdapter extends BaseAdapter implements SpinnerAdapter {

    private final Context activity;
    private ArrayList<String> asr;

    public GenderSelectListAdapter(Context context, ArrayList<String> asr) {
        this.asr = new ArrayList<>();
        this.asr = asr;
        activity = context;
    }


    public int getCount() {

        return asr.size();
    }

    public Object getItem(int i) {
        return asr.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 0, 16, 0);
            txt.setTextSize(0);
            txt.setGravity(Gravity.CENTER);
            txt.setVisibility(View.GONE);
            txt.setTextColor(Color.parseColor("#000000"));
            txt.setAllCaps(false);
            if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi"))
            {
//                Typeface tfBamini = Typeface.createFromAsset(activity.getAssets(), "fonts/Bamini.ttf");
//                 txt.setTypeface(tfBamini, Typeface.NORMAL);
                txt.setText(activity.getString(R.string.select));
            }
            else {
                txt.setText("select");
            }
            return txt;
        } else {
            TextView txt = new TextView(activity);
            txt.setPadding(16, 12, 16, 12);
            txt.setTextSize(23);
            txt.setGravity(Gravity.CENTER);
            txt.setTextColor(Color.parseColor("#000000"));
            txt.setBackgroundResource(R.drawable.gender_selection_spinner_bg);
            txt.setAllCaps(false);

            if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi")) {
//               Typeface tfBamini = Typeface.createFromAsset(activity.getAssets(), "fonts/Bamini.ttf");
//                txt.setTypeface(tfBamini, Typeface.NORMAL);
                if (position == 1)
                    txt.setText(activity.getString(R.string.male));
                else if (position == 2)
                    txt.setText(activity.getString(R.string.female));
                else if (position == 3)
                    txt.setText(activity.getString(R.string.other));
            } else {
                txt.setText(asr.get(position));
            }

         //   Util.LoggingQueue(activity, " GenderSelectListAdapter", "getDropDownView Position ->" +position + " value -> "+txt.getText().toString());

            return txt;
        }

    }


    public View getView(int i, View view, ViewGroup viewgroup) {

        TextView txt = new TextView(activity);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 12, 16, 12);
        txt.setTextSize(23);
        txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_arrow_down, 0);
        txt.setTextColor(Color.parseColor("#000000"));
        txt.setAllCaps(false);


        if (com.omneagate.activity.GlobalAppState.language.equalsIgnoreCase("hi")) {
//            Typeface tfBamini = Typeface.createFromAsset(activity.getAssets(), "fonts/Bamini.ttf");
//            txt.setTypeface(tfBamini, Typeface.NORMAL);
            if (i == 1)
                txt.setText(activity.getString(R.string.male));
            else if (i == 2)
                txt.setText(activity.getString(R.string.female));
            else if  (i == 3)
                txt.setText(activity.getString(R.string.other));
            else
            txt.setText(activity.getString(R.string.select));

        } else {
            txt.setText(asr.get(i));
        }

       // Util.LoggingQueue(activity, "GenderSelectListAdapter ", "getView Position ->" +i + " value -> "+txt.getText().toString());

        return txt;
    }

}
