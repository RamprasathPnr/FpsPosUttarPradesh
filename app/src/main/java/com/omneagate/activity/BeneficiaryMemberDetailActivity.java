package com.omneagate.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.BeneficiarySearchDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.DTO.UserDto.StockCheckDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by user1 on 9/5/16.
 */
public class BeneficiaryMemberDetailActivity extends BaseActivity {

    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.beneficiary_member_activity);
        String ufc_code = getIntent().getExtras().getString("ufc_code");
        configureData(ufc_code);
    }

    private void configureData(String ufc_code) {
        try {
            setUpPopUpPage();
            position =Integer.parseInt( getIntent().getExtras().getString("position"));

            Util.LoggingQueue(this, "Beneficiary Member Detail activity", " Called....."+position);
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.beneficiary_member);
            Util.setTamilText((TextView) findViewById(R.id.tvBeneficiaryNameLabel), R.string.benef_name);
            Util.setTamilText((TextView) findViewById(R.id.tvBeneficiaryGenderLabel), R.string.gender);
            Util.setTamilText((TextView) findViewById(R.id.tvBeneficiaryDobLabel), R.string.benef_dob);
            Util.setTamilText((TextView) findViewById(R.id.tvBeneficiaryAgeLabel), R.string.benef_age);
            Util.setTamilText((TextView) findViewById(R.id.tvBeneficiaryAadhaarNoLabel), R.string.benef_aadhaar);
            Util.setTamilText((TextView) findViewById(R.id.tvAddressLabel), R.string.address);
            Util.setTamilText((TextView) findViewById(R.id.btnClose), R.string.close);
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_benficiary_member);
            findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


            Log.e("BeneMemDetails for : ", "ufc_code = " + ufc_code);
            List<BeneficiaryMemberDto> benefMemberList = FPSDBHelper.getInstance(this).getBenefMemberDetail(ufc_code);
            transactionLayout.removeAllViews();
            for (int position = 0; position < benefMemberList.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);
                transactionLayout.addView(returnView(lin, benefMemberList.get(position), position));
            }

            // Log.e("BeneficiMembDetalActi","benef dto = "+benefMemberList.get(0));
            try {
                if (!GlobalAppState.language.equalsIgnoreCase("hi")) {
                    String address = "";

                    try {
                        if (!benefMemberList.get(0).getAddressLine1().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getAddressLine1() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    try {
                        if (!benefMemberList.get(0).getAddressLine2().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getAddressLine2() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    try {
                        if (!benefMemberList.get(0).getAddressLine3().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getAddressLine3() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    try {
                        if (!benefMemberList.get(0).getAddressLine4().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getAddressLine4() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    try {
                        if (!benefMemberList.get(0).getAddressLine5().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getAddressLine5() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    ((TextView) findViewById(R.id.tvAddressLine1)).setText(address);
                /* ((TextView)findViewById(R.id.tvAddressLine2)).setText(benefMemberList.get(0).getAddressLine1()+benefMemberList.get(0).getAddressLine2()+benefMemberList.get(0).getAddressLine3());
                ((TextView)findViewById(R.id.tvAddressLine3)).setText(benefMemberList.get(0).getAddressLine1()+benefMemberList.get(0).getAddressLine2()+benefMemberList.get(0).getAddressLine3());*/

                } else {
                    String address = "";

                    try {
                        if (!benefMemberList.get(0).getLocalAddressLine1().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getLocalAddressLine1() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    try {
                        if (!benefMemberList.get(0).getLocalAddressLine2().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getLocalAddressLine2() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    try {
                        if (!benefMemberList.get(0).getLocalAddressLine3().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getLocalAddressLine3() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    try {
                        if (!benefMemberList.get(0).getLocalAddressLine4().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getLocalAddressLine4() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }

                    try {
                        if (!benefMemberList.get(0).getLocalAddressLine5().equalsIgnoreCase("null"))
                            address = address + benefMemberList.get(0).getLocalAddressLine5() + " ";
                        else address = address + "";
                    } catch (Exception e) {
                    }
                    //((TextView) findViewById(R.id.tvAddressLine1)).setText(benefMemberList.get(0).getLocalAddressLine1() + benefMemberList.get(0).getLocalAddressLine2() + benefMemberList.get(0).getLocalAddressLine3());
                    ((TextView) findViewById(R.id.tvAddressLine1)).setText(address);


                /* ((TextView)findViewById(R.id.tvAddressLine2)).setText(benefMemberList.get(0).getLocalAddressLine1()+benefMemberList.get(0).getLocalAddressLine2()+benefMemberList.get(0).getLocalAddressLine3());
                ((TextView)findViewById(R.id.tvAddressLine3)).setText(benefMemberList.get(0).getLocalAddressLine1()+benefMemberList.get(0).getLocalAddressLine2()+benefMemberList.get(0).getLocalAddressLine3());*/

                }
            } catch (Exception e) {
                Log.e("Beneficiary ", e.toString(), e);
            }
        } catch (Exception e) {
            Log.e(" Error Bene 2", e.toString(), e);
        }
    }

    private View returnView(LayoutInflater entitle, BeneficiaryMemberDto beneficiaryMemberDto, final int position) {

        Log.e("returnView", "beneficiaryMemberDto = " + beneficiaryMemberDto);

        View convertView = entitle.inflate(R.layout.adapter_member_beneficiary, null);
        TextView serialNoTv = (TextView) convertView.findViewById(R.id.tvSerialNo);
        TextView benefNameTv = (TextView) convertView.findViewById(R.id.tvBeneficiaryName);
        TextView benefGenderTv = (TextView) convertView.findViewById(R.id.tvBeneficiaryGender);
        TextView benefDobTv = (TextView) convertView.findViewById(R.id.tvBeneficiaryDob);
        TextView benefAgeTv = (TextView) convertView.findViewById(R.id.tvBeneficiaryAge);
        TextView benefAadhaarNumberTv = (TextView) convertView.findViewById(R.id.tvBeneficiaryAadhaarNo);
        try {
            serialNoTv.setText("" + (position + 1));
            if (GlobalAppState.language.equalsIgnoreCase("hi")) {
                if (beneficiaryMemberDto.getLocalName() != null) {
                  //  benefNameTv.setText(beneficiaryMemberDto.getLocalName());
                }
            } else {
                if (beneficiaryMemberDto.getName() != null) {
                    benefNameTv.setText(beneficiaryMemberDto.getName());
                }
            }
            if (beneficiaryMemberDto.getGender() == 'M') {
                benefGenderTv.setText(getString(R.string.male));
            } else  if (beneficiaryMemberDto.getGender() == 'F') {
                benefGenderTv.setText(getString(R.string.female));
            }else  if (beneficiaryMemberDto.getGender() == 'O') {
                benefGenderTv.setText(getString(R.string.other));
            }
            if (beneficiaryMemberDto.getDob() != 0) {
                Log.e("getDob()",""+beneficiaryMemberDto.getDob());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                Log.e("simpleDateFormat()",""+simpleDateFormat.format(beneficiaryMemberDto.getDob()));

                benefDobTv.setText("" + simpleDateFormat.format(beneficiaryMemberDto.getDob()));//Convert long DOB
            }
            if (beneficiaryMemberDto.getDurationInYear() != 0)
                benefAgeTv.setText("" + beneficiaryMemberDto.getDurationInYear());


            if (beneficiaryMemberDto.getUid() != null && !beneficiaryMemberDto.getUid().equalsIgnoreCase(""))
                benefAadhaarNumberTv.setText(beneficiaryMemberDto.getUid().substring(0, 4) + "-" + beneficiaryMemberDto.getUid().substring(4, 8) + "-" + beneficiaryMemberDto.getUid().substring(8, 12));
            else
                benefAadhaarNumberTv.setText("---");

            if (beneficiaryMemberDto.getName() == null && beneficiaryMemberDto.getFirstName() == null && beneficiaryMemberDto.getLocalName() == null)
                benefAadhaarNumberTv.setText("---");

            if (beneficiaryMemberDto.getDurationInYear() == 0)
                benefAgeTv.setText("0");


        } catch (Exception e) {
            Log.e("BenefMemberException ", BeneficiaryMemberDetailActivity.class.getSimpleName() + " " + e.toString());
        }
        return convertView;
    }


    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(this, BeneficiaryListActivity.class);
        intent.putExtra("position", ""+position);
        startActivity(intent);


        //startActivity(new Intent(this, BeneficiaryListActivity.class));
        Util.LoggingQueue(this, "Beneficiary Member Detail Activity", "Back pressed Called..."+position);*/
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
        super.onDestroy();
    }
}
