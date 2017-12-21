package com.omneagate.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsDialog extends Dialog implements View.OnClickListener {
    Context dialogContext;
    Button okButton;
    String ufc;

    public UserDetailsDialog(Context context, String ufc) {
        super(context);
        dialogContext = context;
        this.ufc = ufc;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_for_card_holder);
        setCancelable(false);
        TextView alertTitle = (TextView) findViewById(R.id.alerttitle);
        TextView cardHolderName = (TextView) findViewById(R.id.card_holder_name);
        TextView mobileNumber = (TextView) findViewById(R.id.mobile_no);
        TextView cardHolder = (TextView) findViewById(R.id.bene_card_holder_name);
        TextView aRegisterNo = (TextView) findViewById(R.id.bene_aregister_number1);
        TextView aRegisterNoTitle = (TextView) findViewById(R.id.aregister_number1);
        Button buttonOk = (Button) findViewById(R.id.buttonOk);
        TextView bene_address = (TextView) findViewById(R.id.bene_address);
        TextView ration_card_number = (TextView) findViewById(R.id.ration_card_number1);
        TextView address = (TextView) findViewById(R.id.address);
        TextView tvUfc = (TextView) findViewById(R.id.tvUfc);
        Util.setTamilText(alertTitle, R.string.card_user_details);
        Util.setTamilText(cardHolderName, R.string.card_holder_name);
        Util.setTamilText(mobileNumber, R.string.mobile_no);
        Util.setTamilText(ration_card_number, R.string.ration_card_number1);
        Util.setTamilText(aRegisterNoTitle, R.string.aRegisterNo);
        Util.setTamilText(tvUfc, R.string.billViewUfc);
        Util.setTamilText(address, R.string.address);
        Util.setTamilText(buttonOk, R.string.ok);
        BeneficiaryDto beneficiary = FPSDBHelper.getInstance(dialogContext).beneficiaryDto(ufc);
        Log.e("Bene", beneficiary.toString());
        BeneficiaryMemberDto beneficiaryMemberDto;
            if (beneficiary.getBenefMembersDto() != null && beneficiary.getBenefMembersDto().size() > 0) {
                List<BeneficiaryMemberDto> bene = new ArrayList<>(beneficiary.getBenefMembersDto());
                beneficiaryMemberDto = bene.get(0);
            if (GlobalAppState.language.equalsIgnoreCase("hi") && beneficiaryMemberDto.getLocalName() != null) {
                Util.setTamilText(cardHolder, beneficiaryMemberDto.getLocalName());
                } else {
                cardHolder.setText(beneficiaryMemberDto.getName());
            }

            if (GlobalAppState.language.equalsIgnoreCase("hi") && beneficiaryMemberDto.getLocalAddressLine1() != null) {
                Util.setTamilText(bene_address, beneficiaryMemberDto.getLocalAddressLine1());
            } else {
                bene_address.setText(beneficiaryMemberDto.getLocalAddressLine1());
                }
            }
            if (beneficiary.getMobileNumber() != null) {
                ((TextView) findViewById(R.id.bene_mobile_no)).setText(beneficiary.getMobileNumber());
            }
            if (StringUtils.isNotEmpty(beneficiary.getAregisterNum())) {
                aRegisterNo.setText(beneficiary.getAregisterNum());
            }
            if (beneficiary.getOldRationNumber() != null) {
                ((TextView) findViewById(R.id.bene_ration_card_number1)).setText(beneficiary.getOldRationNumber());
                if (beneficiary.getOldRationNumber().length() > 4) {
//                    String rationNo = StringUtils.substring(beneficiary.getOldRationNumber(), 0, 2) + " / " + StringUtils.substring(beneficiary.getOldRationNumber(), 2, 3)
//                            + " / " + StringUtils.substring(beneficiary.getOldRationNumber(), 3);
                    String rationNo = beneficiary.getOldRationNumber();
                    ((TextView) findViewById(R.id.bene_ration_card_number1)).setText(rationNo);
                }
            }
            if (beneficiary.getEncryptedUfc() != null) {
                ((TextView) findViewById(R.id.bene_ufc)).setText(Util.DecryptedBeneficiary(dialogContext, beneficiary.getEncryptedUfc()));
            }

        buttonOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            /*Typeface tfBamini = Typeface.createFromAsset(dialogContext.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini, Typeface.BOLD);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, dialogContext.getString(id)));*/
            textName.setText(dialogContext.getString(id));
        } else {
            textName.setText(dialogContext.getString(id));
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, String id) {
        Typeface tfBamini = Typeface.createFromAsset(dialogContext.getAssets(), "fonts/Bamini.ttf");
        textName.setTypeface(tfBamini, Typeface.BOLD);
        textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, id));
    }
}
