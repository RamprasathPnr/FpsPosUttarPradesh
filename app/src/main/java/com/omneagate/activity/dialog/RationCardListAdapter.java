package com.omneagate.activity.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 4/5/15.
 */
public class RationCardListAdapter extends BaseAdapter {

    List<BeneficiaryDto> beneficiaryActivation = new ArrayList<BeneficiaryDto>();
    LayoutInflater inflater;
    Activity context;


    public RationCardListAdapter(Activity context, List<BeneficiaryDto> myList) {
        this.beneficiaryActivation = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return beneficiaryActivation.size();
    }

    @Override
    public BeneficiaryDto getItem(int position) {
        return beneficiaryActivation.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RegistrationViewHolder mViewHolder;
        final BeneficiaryDto beneficiaryActivate;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_ration_list_item, null);
            mViewHolder = new RegistrationViewHolder();
            mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            mViewHolder.tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (RegistrationViewHolder) convertView.getTag();
        }
        beneficiaryActivate = beneficiaryActivation.get(position);
        mViewHolder.tvTitle.setText(beneficiaryActivate.getOldRationNumber());
        if (beneficiaryActivate.getAregisterNum() != null || !beneficiaryActivate.getAregisterNum().equals("-1"))
//            mViewHolder.tvDesc.setText(beneficiaryActivate.getAregisterNum());
            mViewHolder.tvDesc.setText("");
        else {
            mViewHolder.tvDesc.setText("");          // A Register number is not available
        }
        return convertView;
    }


    private class RegistrationViewHolder {
        TextView tvTitle, tvDesc;
    }

}