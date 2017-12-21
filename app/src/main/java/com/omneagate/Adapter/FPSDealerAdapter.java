package com.omneagate.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.FPSDealer;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FPSDealerAdapter extends BaseAdapter {

    Context ct;

    List<FPSDealer> fpsdealer;

    public FPSDealerAdapter(Context context, List<FPSDealer> fpsdealer) {
        ct = context;
        this.fpsdealer = fpsdealer;
    }

    public int getCount() {
        return fpsdealer.size();
    }

    public FPSDealer getItem(int position) {
        return fpsdealer.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = null;
        FPSDealer bills = fpsdealer.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.fps_dealer, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.uid = (TextView) view.findViewById(R.id.uid);
            holder.bf_one = (TextView) view.findViewById(R.id.bf_one);
            holder.bf_two = (TextView) view.findViewById(R.id.bf_two);
            holder.bf_three = (TextView) view.findViewById(R.id.bf_three);

            holder.billBackground = (LinearLayout)view.findViewById(R.id.bill_background);
            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(bills.getDealerOrNomine());

        if(Util.needUidmMsking && bills.getDealerOrNomineUidNo().length()==12){
            holder.uid.setText(""+ Util.maskAadhaarNumber(bills.getDealerOrNomineUidNo(),"XXXXXXXX####"));
        }else{
            holder.uid.setText(bills.getDealerOrNomineUidNo());
        }

        holder.bf_one.setText(bills.getBestFinger1());
        holder.bf_two.setText(bills.getBestFinger2());
        holder.bf_three.setText(bills.getBestFinger3());

        if(fpsdealer.get(position).isSelectedItem()){
            view.setBackgroundColor(Color.parseColor("#E3E3E3"));
        }else{
            view.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }

        return view;
    }



    class ViewHolder {
        TextView name;
        TextView uid;

        TextView bf_one;
        TextView bf_two;
        TextView bf_three;

        LinearLayout billBackground;
    }
}