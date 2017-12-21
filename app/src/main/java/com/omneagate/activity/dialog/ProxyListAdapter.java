package com.omneagate.activity.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.ProxyDetailDto;
import com.omneagate.activity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by user1 on 4/5/15.
 */
public class ProxyListAdapter extends BaseAdapter {

    List<ProxyDetailDto> beneficiaryActivation = new ArrayList<ProxyDetailDto>();
    LayoutInflater inflater;
    Activity context;


    public ProxyListAdapter(Activity context, List<ProxyDetailDto> myList) {
        this.beneficiaryActivation = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return beneficiaryActivation.size();
    }

    @Override
    public ProxyDetailDto getItem(int position) {
        return beneficiaryActivation.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RegistrationViewHolder mViewHolder;
        final ProxyDetailDto beneficiaryActivate;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_proxy_list_item, null);
            mViewHolder = new RegistrationViewHolder();
            mViewHolder.proxyListMasterLay = (RelativeLayout) convertView.findViewById(R.id.proxyListMasterLayout);
            mViewHolder.proxyName = (TextView) convertView.findViewById(R.id.proxyNameTv);
            mViewHolder.aadharNumber = (TextView) convertView.findViewById(R.id.aadharNoTv);
            mViewHolder.dateOfBirth = (TextView) convertView.findViewById(R.id.dateTv);
            mViewHolder.mobileNumber = (TextView) convertView.findViewById(R.id.mobileNoTv);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (RegistrationViewHolder) convertView.getTag();
        }
        beneficiaryActivate = beneficiaryActivation.get(position);
        Log.e("ProxyListAdapter","proxy dto..."+beneficiaryActivate.toString());
        mViewHolder.proxyName.setText(beneficiaryActivate.getName());
        mViewHolder.aadharNumber.setText(beneficiaryActivate.getUid());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = sdf.format(beneficiaryActivate.getDob());
        mViewHolder.dateOfBirth.setText(dateString);
        mViewHolder.mobileNumber.setText(beneficiaryActivate.getMobile());
        if (beneficiaryActivate.getRequestStatus().equalsIgnoreCase("Pending")) {
            mViewHolder.proxyListMasterLay.setBackgroundColor(Color.parseColor("#FFB6C1"));
        }
        else {
            mViewHolder.proxyListMasterLay.setBackgroundColor(Color.parseColor("#00000000"));
        }
        return convertView;
    }


    private class RegistrationViewHolder {
        TextView proxyName, aadharNumber, dateOfBirth, mobileNumber;
        RelativeLayout proxyListMasterLay;
    }

}