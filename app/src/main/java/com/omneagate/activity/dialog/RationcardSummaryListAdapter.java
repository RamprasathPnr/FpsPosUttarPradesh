package com.omneagate.activity.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiarySearchDto;
import com.omneagate.DTO.RationcardSummaryDto;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for BeneficiaryListAdapter
 */
public class RationcardSummaryListAdapter extends BaseAdapter {

    List<RationcardSummaryDto> beneficiaryActivation = new ArrayList<>();
    LayoutInflater inflater;
    Activity context;


    public RationcardSummaryListAdapter(Activity context, List<RationcardSummaryDto> myList) {
        this.beneficiaryActivation = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return beneficiaryActivation.size();
    }

    @Override
    public RationcardSummaryDto getItem(int position) {
        return beneficiaryActivation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RegistrationViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_rationcard_summary_list_item, null);
            mViewHolder = new RegistrationViewHolder();
            mViewHolder.tvPosition = (TextView) convertView.findViewById(R.id.tvHash);
            mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            mViewHolder.tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (RegistrationViewHolder) convertView.getTag();
        }
        RationcardSummaryDto beneficiaryActivate = beneficiaryActivation.get(position);
        mViewHolder.tvPosition.setText(String.valueOf((position + 1)));
        if(GlobalAppState.language.equalsIgnoreCase("hi")) {
            mViewHolder.tvTitle.setText(beneficiaryActivate.getLCardType());
        }
        else {
            mViewHolder.tvTitle.setText(beneficiaryActivate.getCardType());
        }
        mViewHolder.tvDesc.setText(beneficiaryActivate.getNoOfCards());


        return convertView;
    }


    private class RegistrationViewHolder {
        TextView tvTitle, tvDesc, tvPosition;
    }
}