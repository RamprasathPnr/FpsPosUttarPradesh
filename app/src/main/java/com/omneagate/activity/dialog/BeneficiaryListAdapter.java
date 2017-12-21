package com.omneagate.activity.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiarySearchDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.activity.BeneficiaryMemberDetailActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created for BeneficiaryListAdapter
 */
public class BeneficiaryListAdapter extends BaseAdapter {

    List<BeneficiarySearchDto> beneficiaryActivation = new ArrayList<>();
    LayoutInflater inflater;
    Activity context;


    public BeneficiaryListAdapter(Activity context, List<BeneficiarySearchDto> myList) {
        this.beneficiaryActivation = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return beneficiaryActivation.size();
    }

    @Override
    public BeneficiarySearchDto getItem(int position) {
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
            convertView = inflater.inflate(R.layout.layout_bene_list_item, null);
            mViewHolder = new RegistrationViewHolder();
            mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            mViewHolder.tvDesc = (TextView) convertView.findViewById(R.id.tvDesc);
            mViewHolder.tvPosition = (TextView) convertView.findViewById(R.id.tvHash);
            mViewHolder.regDate = (TextView) convertView.findViewById(R.id.tvRegistration);
            mViewHolder.noOfAdults = (TextView) convertView.findViewById(R.id.noOfAdults);
            mViewHolder.noOfChild = (TextView) convertView.findViewById(R.id.noOfChild);
            mViewHolder.noOfCylinder = (TextView) convertView.findViewById(R.id.noOfCylinder);
            mViewHolder.mobile_avail = (TextView) convertView.findViewById(R.id.mobile_avail);
            mViewHolder.aadhar_avail = (TextView) convertView.findViewById(R.id.aadhar_avail);
//            mViewHolder.cardCategory = (TextView) convertView.findViewById(R.id.tvCardCategory);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (RegistrationViewHolder) convertView.getTag();
        }
        BeneficiarySearchDto beneficiaryActivate = beneficiaryActivation.get(position);
        mViewHolder.tvTitle.setText(beneficiaryActivate.getMobileNo());
        SpannableString content = new SpannableString(beneficiaryActivate.getCardNo());
        content.setSpan(new UnderlineSpan(), 0, beneficiaryActivate.getCardNo().length(), 0);
        mViewHolder.tvDesc.setText(content);
        if(GlobalAppState.language.equalsIgnoreCase("hi")) {
            mViewHolder.regDate.setText(beneficiaryActivate.getLocalCardType());
        }
        else {
            mViewHolder.regDate.setText(beneficiaryActivate.getCardType());
        }
        mViewHolder.noOfAdults.setText(String.valueOf(beneficiaryActivate.getNoOfAdult()));
//        mViewHolder.noOfChild.setText(String.valueOf(beneficiaryActivate.getNoOfChild()));
        mViewHolder.noOfCylinder.setText(String.valueOf(beneficiaryActivate.getNoOfCylinder()));
        mViewHolder.tvPosition.setText(String.valueOf((position + 1)));
        if(beneficiaryActivate.isMobileAvail()) {
            mViewHolder.mobile_avail.setText("\u2714");
        } else {
            mViewHolder.mobile_avail.setText("X");
        }

        try {
            int aadharCount = FPSDBHelper.getInstance(context).getMembersAadharCount(String.valueOf(beneficiaryActivate.getBenefId()));
            int totalMember = beneficiaryActivate.getNoOfAdult() + beneficiaryActivate.getNoOfChild();
            mViewHolder.aadhar_avail.setText(String.valueOf(aadharCount) +" / "+ String.valueOf(totalMember));
            if(aadharCount == 0) {
                mViewHolder.aadhar_avail.setTextColor(Color.parseColor("#FF0000"));
            }
            else if(aadharCount == totalMember) {
                mViewHolder.aadhar_avail.setTextColor(Color.parseColor("#006400"));
            }
            else {
                mViewHolder.aadhar_avail.setTextColor(Color.parseColor("#FF8C00"));
            }

            mViewHolder.tvDesc.setTextColor(Color.BLUE);
            mViewHolder.tvDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BeneficiaryMemberDetailActivity.class);
                    intent.putExtra("ufc_code", beneficiaryActivation.get(position).getUfc_code());
                    intent.putExtra("position", ""+position);
                    context.startActivity(intent);
                }
            });
        }
        catch(Exception e) {
            Log.e("BeneficiaryListAdapter"," exc..."+e);
        }

       /* NfsaPosDataDto dto = FPSDBHelper.getInstance(context).get_nfsaStatus(beneficiaryActivate.getBenefId());
        if(dto != null) {
            mViewHolder.cardCategory.setText(dto.getCardTypeName());
        }
        else {
            String defaultCardCategory = "NPHH";
            mViewHolder.cardCategory.setText(defaultCardCategory);
        }*/

        return convertView;
    }


    private class RegistrationViewHolder {
        TextView tvTitle, tvDesc, tvPosition, regDate, noOfAdults, noOfChild, noOfCylinder,mobile_avail, aadhar_avail, cardCategory;
    }
}