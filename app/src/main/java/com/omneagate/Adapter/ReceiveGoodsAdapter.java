package com.omneagate.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.FPSDealer;
import com.omneagate.DTO.Product;
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

public class ReceiveGoodsAdapter extends BaseAdapter {

    Context ct;

    List<Product> product;

    public ReceiveGoodsAdapter(Context context, List<Product> product) {
        ct = context;
        this.product = product;
    }

    public int getCount() {
        return product.size();
    }

    public Product getItem(int position) {
        return product.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = null;
        final Product prods = product.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.receive_goods_adapter, null);
            holder = new ViewHolder();
            holder.commodity = (TextView) view.findViewById(R.id.commodity);
            holder.roQuantity = (TextView) view.findViewById(R.id.roQuantity);
            holder.currentStock = (EditText) view.findViewById(R.id.received_stock);

            prods.setValueET(holder.currentStock);

            holder.billBackground = (LinearLayout)view.findViewById(R.id.bill_background);
            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        /*holder.currentStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s!=null && s.length() > 0) {
                    Log.e("Receive Goods Adapter","CharSequence "+s.toString());
                    prods.setReceivedQuantity(Double.parseDouble(s.toString()));
                }else{
                    prods.setReceivedQuantity(0.0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        holder.commodity.setText(prods.getDisplayName());
        holder.roQuantity.setText(""+(prods.getIssuedQuantity()).intValue());
        holder.currentStock.setText(""+(prods.getReceivedQuantity()).intValue());

        return view;
    }



    class ViewHolder {
        TextView commodity;
        TextView roQuantity;
        EditText currentStock;
        LinearLayout billBackground;
    }
}