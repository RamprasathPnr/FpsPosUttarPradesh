package com.omneagate.activity.dialog;

import android.content.Context;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OutOfStockListAdapter extends BaseAdapter {
    Context ct;
    ArrayList<String> productList;
    private LayoutInflater mInflater;

    public OutOfStockListAdapter(Context context, ArrayList<String> products) {
        ct = context;
        productList = products;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return productList.size();
    }

    public String getItem(int position) {
        return productList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.out_of_stock_list_adapter, null);
            holder = new ViewHolder();
            holder.productName = (TextView) view.findViewById(R.id.textView1);
            holder.productName.setText(productList.get(position));
            holder.productName.setTextColor(Color.BLACK);
            view.setMinimumHeight(40);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        return view;
    }

    class ViewHolder {
        TextView productName;
    }

}