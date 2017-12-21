package com.omneagate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.Product;
import com.omneagate.DTO.UserDto.MonthDto;
import com.omneagate.activity.R;

import java.util.List;

/**
 * Created by root on 10/3/17.
 */
public class MonthAdapter extends BaseAdapter {

    Context ct;

    List<MonthDto> monthList;

    public MonthAdapter(Context context, List<MonthDto> monthList) {
        ct = context;
        this.monthList = monthList;
    }

    public int getCount() {
        return monthList.size();
    }

    public MonthDto getItem(int position) {
        return monthList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = null;
        MonthDto monthdto = monthList.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.list_item_month, null);
            holder = new ViewHolder();
            holder.month = (TextView) view.findViewById(R.id.month);

            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }
        holder.month.setText(monthdto.getCode());


        return view;
    }

    class ViewHolder {
        TextView month;

    }
}
