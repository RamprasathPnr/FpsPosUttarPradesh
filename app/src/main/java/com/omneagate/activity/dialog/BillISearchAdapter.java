package com.omneagate.activity.dialog;

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
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class BillISearchAdapter extends BaseAdapter {

    Context ct;

    List<BillDto> billDto;

    private LayoutInflater mInflater;

    public BillISearchAdapter(Context context, List<BillDto> billDto) {
        ct = context;
        this.billDto = billDto;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return billDto.size();
    }

    public BillDto getItem(int position) {
        return billDto.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = null;
        BillDto bills = billDto.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.adapter_bill_activity_date, null);
            holder = new ViewHolder();
            holder.serialNo = (TextView) view.findViewById(R.id.tvBillViewSerialNo);
            holder.billTransactionId = (TextView) view.findViewById(R.id.tvBillViewBillNo);
            holder.billDate = (TextView) view.findViewById(R.id.tvBillViewDate);
            holder.tvBillViewStatus = (TextView) view.findViewById(R.id.tvBillViewStatus);
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
        String cardNo = bills.getRationCardNumber();
        /*if (StringUtils.isNotEmpty(bills.getARegisterNo()) && !StringUtils.equalsIgnoreCase(bills.getARegisterNo(), "-1")) {
            cardNo = cardNo + "/" + billDto.get(position).getARegisterNo();
        }*/
        holder.billDate.setText(cardNo);
        /*NumberFormat formatter = new DecimalFormat("#0.00");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        String amt1 = Util.priceRoundOffFormat(bills.getAmount());
        holder.billAmount.setText(amt1);
        holder.billTransactionId.setText(bills.getTransactionId());
        Util.setTamilText(holder.tvBillViewStatus, R.string.viewString);
        if (StringUtils.equalsIgnoreCase("R", bills.getBillStatus())) {
            holder.billBackground.setBackgroundColor(Color.parseColor("#ffe4e4"));
        }else{
            holder.billBackground.setBackgroundColor(Color.parseColor("#00000000"));
        }
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
        TextView billTransactionId;
        TextView billDate;
        TextView billAmount, tvBillViewStatus;
        LinearLayout billBackground;
    }
}