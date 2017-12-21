package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProxyDetailDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.ProxyListAdapter;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class AddProxyActivity extends BaseActivity {

    ListView listViewSearch;
    ProxyListAdapter rationCardListAdapter;
    List<ProxyDetailDto> proxyDetailsDto;
    String rcNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.add_proxy);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpPopUpPage();
        configureData();
    }

    private void configureData() {
        Util.setTamilText((TextView) findViewById(R.id.holderName), R.string.holder_name);
        Util.setTamilText((TextView) findViewById(R.id.holderAge), R.string.holder_age);
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.add_proxy);
        Util.setTamilText((TextView) findViewById(R.id.addProxyTv), R.string.add_proxy);
        Util.setTamilText((TextView) findViewById(R.id.benefDetailHeading), R.string.card_holder_details);
        Util.setTamilText((TextView) findViewById(R.id.proxyDetailHeading), R.string.proxy_nominee_details);
        Util.setTamilText((TextView) findViewById(R.id.rcNumberTv), R.string.ration_card_number1);
        rcNo = getIntent().getStringExtra("RcNumber");
        getCardHolderDetailsFromRcNo(rcNo);
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.addProxyLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((rcNo != null) && (!rcNo.equalsIgnoreCase("null")) && StringUtils.isNotEmpty(rcNo.trim())) {
                    Intent intent = new Intent(AddProxyActivity.this, AddProxyDetailsActivity.class);
                    intent.putExtra("RcNumber", rcNo);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void getCardHolderDetailsFromRcNo(String rcNo) {
        BeneficiaryDto benef = FPSDBHelper.getInstance(AddProxyActivity.this).beneficiaryFromOldCard(rcNo);
        if (benef != null) {
            proxyDetailsDto = FPSDBHelper.getInstance(AddProxyActivity.this).retrieveProxy(benef.getId());
            if(proxyDetailsDto != null) {
                rationCardListAdapter = new ProxyListAdapter(this, proxyDetailsDto);
                listViewSearch = (ListView) findViewById(R.id.proxyList);
                listViewSearch.setAdapter(rationCardListAdapter);
            }
            BeneficiaryMemberDto beneficiaryMemberDto = FPSDBHelper.getInstance(AddProxyActivity.this).getHeadBeneficiaryMember(benef.getEncryptedUfc().trim(), benef.getFamilyHeadAadharNumber());
            if (beneficiaryMemberDto != null) {
                ((TextView) findViewById(R.id.holderNameTxt)).setText(beneficiaryMemberDto.getName());
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String dateString = sdf.format(beneficiaryMemberDto.getDob());
                ((TextView) findViewById(R.id.holderAgeTxt)).setText(dateString);
            }
            ((TextView) findViewById(R.id.adultCountTv)).setText(String.valueOf(benef.getNumOfAdults()));
            ((TextView) findViewById(R.id.childCountTv)).setText(String.valueOf(benef.getNumOfChild()));
            ((TextView) findViewById(R.id.cylinderCountTv)).setText(String.valueOf(benef.getNumOfCylinder()));
            if (benef.getMobileNumber() == null || StringUtils.isEmpty(benef.getMobileNumber().trim())) {
                ((TextView) findViewById(R.id.mobileTv)).setText("No");
            } else {
                ((TextView) findViewById(R.id.mobileTv)).setText("Yes");
            }
            if (benef.getFamilyHeadAadharNumber() == null || StringUtils.isEmpty(benef.getFamilyHeadAadharNumber().trim())) {
                ((TextView) findViewById(R.id.aadharTv)).setText("No");
            } else {
                ((TextView) findViewById(R.id.aadharTv)).setText("Yes");
            }
            ((TextView) findViewById(R.id.rcNumberTxt)).setText(rcNo);
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            default:
                try {
                    if (progressBar != null) {
                        progressBar.dismiss();
                    }
                }
                catch(Exception e) {}
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, RcScanEntryProxyActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        }
        catch(Exception e) {}
        super.onDestroy();
    }
}