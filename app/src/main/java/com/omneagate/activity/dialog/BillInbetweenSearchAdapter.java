package com.omneagate.activity.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.BillInbetweenDto;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class BillInbetweenSearchAdapter extends BaseAdapter {

    Context ct;

    List<BillInbetweenDto> billDto;

    private LayoutInflater mInflater;

    public BillInbetweenSearchAdapter(Context context, List<BillInbetweenDto> billDto) {
        ct = context;
        this.billDto = billDto;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return billDto.size();
    }

    public BillInbetweenDto getItem(int position) {
        return billDto.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = null;
        BillInbetweenDto bills = billDto.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.adapter_bill_activity_inbetween_date, null);
            holder = new ViewHolder();
            holder.serialNo = (TextView) view.findViewById(R.id.tvBillViewSerialNo);
            holder.comodityTv = (TextView) view.findViewById(R.id.comodityTv);
            holder.unitTv = (TextView) view.findViewById(R.id.unitTv);
            holder.quantityTv = (TextView) view.findViewById(R.id.quantityTv);
            holder.billAmount = (TextView) view
                    .findViewById(R.id.tvBillViewAmount);
            holder.billBackground = (LinearLayout)view.findViewById(R.id.bill_background);
            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }
        holder.serialNo.setText(String.valueOf(position + 1));
        if(GlobalAppState.language.toString().equalsIgnoreCase("hi"))
        {
            holder.comodityTv.setText(bills.getLocalProductName());
            holder.unitTv.setText(bills.getLocalProductUnit());
        }
        else if(GlobalAppState.language.toString().equalsIgnoreCase("en"))
        {
            holder.comodityTv.setText(bills.getProductName());
            holder.unitTv.setText(bills.getProductUnit());
        }

       // Log.e("Qty from db", ""+String.valueOf(bills.getQuantity()));
        //Log.e("quantityRoundOffFormat", ""+Util.quantityRoundOffFormat(bills.getQuantity()));

        holder.quantityTv.setText(""+Util.quantityRoundOffFormat(bills.getQuantity()));
        /*NumberFormat formatter = new DecimalFormat("#0.00");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        String amt1 = Util.priceRoundOffFormat(bills.getCost());
        holder.billAmount.setText(amt1);
        return view;
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            /*Typeface tfBamini = Typeface.createFromAsset(ct.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini, Typeface.BOLD);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, ct.getString(id)));*/
            textName.setText(ct.getString(id));
        } else {
            textName.setText(ct.getString(id));
        }
    }

    class ViewHolder {
        TextView serialNo;
        TextView comodityTv;
        TextView unitTv;
        TextView billAmount, quantityTv;
        LinearLayout billBackground;
    }
}