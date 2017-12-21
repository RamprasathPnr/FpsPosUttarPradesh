package com.omneagate.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.BillDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user1 on 27/3/15.
 */
public class BillViewAdapter extends BaseAdapter {

    private Activity mContext;
    private List<BillDto> mList;
    private LayoutInflater mLayoutInflater = null;

    public BillViewAdapter(Activity context, List<BillDto> billViewDatas) {
        mContext = context;
        mList = billViewDatas;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BillDto billDto = mList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.adapter_bill_activity, parent, false);

            viewHolder.serialNo = (TextView) convertView.findViewById(R.id.tvBillViewSerialNo);
            viewHolder.billNo = (TextView) convertView.findViewById(R.id.tvBillViewBillNo);
            viewHolder.billDate = (TextView) convertView.findViewById(R.id.tvBillViewDate);
//            viewHolder.ufc = (TextView) convertView.findViewById(R.id.tvBillViewUfc);
            viewHolder.status = (TextView) convertView.findViewById(R.id.tvBillViewStatus);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            viewHolder.serialNo.setText("" + (position + 1));
         /* if (StringUtils.isNotEmpty(billViewData.getTransactionId()))
            viewHolder.billNo.setText(billViewData.getTransactionId());*/
            //viewHolder.billNo.setText("" + billDto.getId());
            viewHolder.billNo.setText("" + billDto.getBillLocalRefId());
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date date2 = formatter.parse(billDto.getBillDate());
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            if (StringUtils.isNotEmpty(billDto.getBillDate()))
                viewHolder.billDate.setText(sdf.format(date2));
            String encryptedUFC = FPSDBHelper.getInstance(mContext).retrieveDataFromBeneficiary(billDto.getBeneficiaryId());
            String beneficiaryUfc = Util.DecryptedBeneficiary(mContext, encryptedUFC);

            if (StringUtils.isNotEmpty(beneficiaryUfc))
                viewHolder.ufc.setText(beneficiaryUfc);
            billDto.setUfc(beneficiaryUfc);// This is we need for Bill Detail Activity
            if (billDto.getBillStatus().equalsIgnoreCase("T")) {
                viewHolder.status.setText(billDto.getBillStatus());
                convertView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                convertView.setBackgroundColor(Color.GRAY);
            }
        } catch (Exception e) {
            Log.e("BillView", e.toString(), e);
        }

        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView serialNo;
        TextView billNo;
        TextView billDate;
        TextView ufc;
        TextView status;

    }
}