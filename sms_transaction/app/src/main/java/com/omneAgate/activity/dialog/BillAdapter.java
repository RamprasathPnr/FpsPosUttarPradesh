package com.omneagate.activity.dialog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.BillDto;
import com.omneagate.activity.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BillAdapter extends BaseAdapter {
    Context ct;
    private LayoutInflater mInflater;
    List<BillDto> orderList;

    public BillAdapter(Context context, List<BillDto> orders) {
        ct = context;
        orderList = orders;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return orderList.size();
    }

    public BillDto getItem(int position) {
        return orderList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.billitemsildbills, null);
            holder = new ViewHolder();
            holder.number = (TextView) view.findViewById(R.id.textView1);
            holder.name = (TextView) view.findViewById(R.id.textView4);
            holder.price = (TextView) view.findViewById(R.id.textView5);
            holder.textViewWaiter = (TextView) view
                    .findViewById(R.id.textViewWaiter);
            view.setMinimumHeight(40);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.number.setText((position + 1) + "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(orderList.get(position).getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault());
        holder.name.setText(dateFormat.format(convertedDate));
        NumberFormat formatter = new DecimalFormat("#0.00");
        holder.price.setText(formatter.format(orderList.get(position).getAmount()));
        holder.textViewWaiter.setText(orderList.get(position).getTransactionId());
        return view;
    }

    class ViewHolder {
        TextView number;
        TextView name;
        TextView price;
        TextView textViewWaiter;
    }

}