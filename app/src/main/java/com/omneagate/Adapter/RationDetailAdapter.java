package com.omneagate.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.FPSRationCard;
import com.omneagate.Util.Util;
import com.omneagate.activity.R;

import java.util.List;

/**
 * Created by root on 17/2/17.
 */
public class RationDetailAdapter extends BaseAdapter{

    private Context context;
    private List<FPSRationCard> rationCardList;

    public RationDetailAdapter(Context context,List<FPSRationCard> rationCardList){
        this.context=context;
        this.rationCardList=rationCardList;

    }

    @Override
    public int getCount() {
        return rationCardList.size();
    }

    @Override
    public Object getItem(int position) {
        return rationCardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = null;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.fps_dealer, null);
            holder = new ViewHolder();
            holder.benficiaryMember = (TextView) view.findViewById(R.id.name);
            holder.memberUid = (TextView) view.findViewById(R.id.uid);
            holder.bestFinger1 = (TextView) view.findViewById(R.id.bf_one);
            holder.bestFinger2 = (TextView) view.findViewById(R.id.bf_two);
            holder.bestFinger3 = (TextView) view.findViewById(R.id.bf_three);

            holder.listItemBackground = (LinearLayout)view.findViewById(R.id.bill_background);
            view.setMinimumHeight(50);
            view.setTag(holder);

        }else{
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        holder.benficiaryMember.setText(rationCardList.get(position).getMemberName());

        if(Util.needUidmMsking && rationCardList.get(position).getUidNo().length()==12){
            holder.memberUid.setText(""+ Util.maskAadhaarNumber(rationCardList.get(position).getUidNo(),"XXXXXXXX####"));
        }else{
            holder.memberUid.setText(rationCardList.get(position).getUidNo());
        }
        holder.bestFinger1.setText(rationCardList.get(position).getBestFinger1());
        holder.bestFinger2.setText(rationCardList.get(position).getBestFinger2());
        holder.bestFinger3.setText(rationCardList.get(position).getBestFinger3());

        if(rationCardList.get(position).isSelectedItem()){
            view.setBackgroundColor(Color.parseColor("#E3E3E3"));
            if(rationCardList.get(position).getStatus().equalsIgnoreCase("Y")){
                holder.bestFinger1.setTextColor(Color.parseColor("#777777"));
                holder.bestFinger2.setTextColor(Color.parseColor("#777777"));
                holder.bestFinger3.setTextColor(Color.parseColor("#777777"));
                holder.memberUid.setTextColor(Color.parseColor("#0288D1"));
                holder.benficiaryMember.setTextColor(Color.parseColor("#777777"));
            }else{
                holder.bestFinger1.setTextColor(Color.parseColor("#FF0000"));
                holder.bestFinger2.setTextColor(Color.parseColor("#FF0000"));
                holder.bestFinger3.setTextColor(Color.parseColor("#FF0000"));
                holder.memberUid.setTextColor(Color.parseColor("#FF0000"));
                holder.benficiaryMember.setTextColor(Color.parseColor("#FF0000"));
            }
        }else{
            view.setBackgroundColor(Color.parseColor("#F4F4F4"));
            if(rationCardList.get(position).getStatus().equalsIgnoreCase("Y")){
                holder.bestFinger1.setTextColor(Color.parseColor("#777777"));
                holder.bestFinger2.setTextColor(Color.parseColor("#777777"));
                holder.bestFinger3.setTextColor(Color.parseColor("#777777"));
                holder.memberUid.setTextColor(Color.parseColor("#0288D1"));
                holder.benficiaryMember.setTextColor(Color.parseColor("#777777"));
            }else{
                holder.bestFinger1.setTextColor(Color.parseColor("#FF0000"));
                holder.bestFinger2.setTextColor(Color.parseColor("#FF0000"));
                holder.bestFinger3.setTextColor(Color.parseColor("#FF0000"));
                holder.memberUid.setTextColor(Color.parseColor("#FF0000"));
                holder.benficiaryMember.setTextColor(Color.parseColor("#FF0000"));
            }

        }

        return view;
    }

    class ViewHolder {
        TextView benficiaryMember;
        TextView memberUid;
        TextView bestFinger1;
        TextView bestFinger2;
        TextView bestFinger3;
        LinearLayout listItemBackground;
    }
}
