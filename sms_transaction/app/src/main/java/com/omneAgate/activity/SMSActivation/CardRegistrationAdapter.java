package com.omneagate.activity.SMSActivation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.activity.R;
import com.omneagate.activity.dialog.OTPDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 4/5/15.
 */
public class CardRegistrationAdapter extends BaseAdapter {

    List<BenefActivNewDto> beneficiaryActivation = new ArrayList<BenefActivNewDto>();
    LayoutInflater inflater;
    Activity context;


    public CardRegistrationAdapter(Activity context, List<BenefActivNewDto> myList) {
        this.beneficiaryActivation = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return beneficiaryActivation.size();
    }

    @Override
    public BenefActivNewDto getItem(int position) {
        return beneficiaryActivation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RegistrationViewHolder mViewHolder;
        final BenefActivNewDto beneficiaryActivate;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_list_item, null);
            mViewHolder = new RegistrationViewHolder();
            mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            mViewHolder.tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
            mViewHolder.tvPosition = (TextView) convertView.findViewById(R.id.tvHash);
            mViewHolder.tvAction = (Button) convertView.findViewById(R.id.tvAction);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (RegistrationViewHolder) convertView.getTag();
        }
        beneficiaryActivate = beneficiaryActivation.get(position);
        mViewHolder.tvTitle.setText(beneficiaryActivate.getRationCardNumber());
        mViewHolder.tvDesc.setText(beneficiaryActivate.getMobileNum());
        mViewHolder.tvPosition.setText((position + 1) + "");
        mViewHolder.tvAction.setText("ACTIVATE");
        mViewHolder.tvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beneficiaryActivate.getActivationType().equalsIgnoreCase("PENDING_ACTIVATION")) {
                    ((CardRegistrationActivity) context).activateData(beneficiaryActivation.get(position));
                } else
                    new OTPDialog(context, beneficiaryActivation.get(position)).show();
            }
        });
        return convertView;
    }


    private class RegistrationViewHolder {
        TextView tvTitle, tvDesc, tvPosition;
        Button tvAction;
    }

}