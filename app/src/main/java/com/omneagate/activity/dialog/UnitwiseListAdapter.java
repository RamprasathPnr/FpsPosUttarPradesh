package com.omneagate.activity.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiarySearchDto;
import com.omneagate.DTO.RationcardSummaryDto;
import com.omneagate.DTO.UnitwiseSummaryDto;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.RationCardSummaryReportActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for BeneficiaryListAdapter
 */
public class UnitwiseListAdapter extends BaseAdapter {



    List<UnitwiseSummaryDto> unitwiseSummaryDto = new ArrayList<>();
    LayoutInflater inflater;
    Activity context;


    public UnitwiseListAdapter(Activity context, List<UnitwiseSummaryDto> myList) {
        this.unitwiseSummaryDto = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return unitwiseSummaryDto.size();
    }

    @Override
    public UnitwiseSummaryDto getItem(int position) {
        return unitwiseSummaryDto.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RegistrationViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_unitwise_list_item, null);
            mViewHolder = new RegistrationViewHolder();
            mViewHolder.cardName = (TextView) convertView.findViewById(R.id.tvCardName);
            mViewHolder.one = (TextView) convertView.findViewById(R.id.oneUnit);
            mViewHolder.oneHalf = (TextView) convertView.findViewById(R.id.oneHalfUnit);
            mViewHolder.two = (TextView) convertView.findViewById(R.id.twoUnit);
            mViewHolder.twoHalf = (TextView) convertView.findViewById(R.id.twoHalfUnit);
            mViewHolder.three = (TextView) convertView.findViewById(R.id.threeUnit);
            mViewHolder.total = (TextView) convertView.findViewById(R.id.total);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (RegistrationViewHolder) convertView.getTag();
        }
        UnitwiseSummaryDto unitwiseSummary = unitwiseSummaryDto.get(position);
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            mViewHolder.cardName.setText(unitwiseSummary.getLCardType());
        }
        else {
            mViewHolder.cardName.setText(unitwiseSummary.getCardType());
        }
        mViewHolder.one.setText(unitwiseSummary.getOneUnit());
        mViewHolder.oneHalf.setText(unitwiseSummary.getOneHalfUnit());
        mViewHolder.two.setText(unitwiseSummary.getTwoUnit());
        mViewHolder.twoHalf.setText(unitwiseSummary.getTwoHalfUnit());
        mViewHolder.three.setText(unitwiseSummary.getThreeUnit());
        mViewHolder.total.setText(unitwiseSummary.getTotal());
        return convertView;
    }


    private class RegistrationViewHolder {
        TextView cardName, one, oneHalf, two, twoHalf, three, total;
    }
}