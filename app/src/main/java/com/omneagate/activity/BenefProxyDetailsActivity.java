package com.omneagate.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.BFDDetailDto;
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
import java.util.ArrayList;
import java.util.List;

public class BenefProxyDetailsActivity extends BaseActivity {

    ListView listViewSearch;
    ProxyListAdapter rationCardListAdapter;
    List<ProxyDetailDto> proxyDetailsDto;
    ArrayList<String> benefDetailsList;
    BeneficiaryDto benef;
    BeneficiaryMemberDto beneficiaryMemberDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.bene_proxy_details);
        networkConnection = new NetworkConnection(this);
        httpConnection = new HttpClientWrapper();
        setUpPopUpPage();
        loadHeaderData();
        String rcNo = getIntent().getStringExtra("RcNumber");
        if((rcNo != null) && (!rcNo.equalsIgnoreCase("null")) && StringUtils.isNotEmpty(rcNo.trim())) {
            benef = FPSDBHelper.getInstance(BenefProxyDetailsActivity.this).beneficiaryFromOldCard(rcNo);
            if (benef != null) {
                beneficiaryMemberDto = FPSDBHelper.getInstance(BenefProxyDetailsActivity.this).getHeadBeneficiaryMember(benef.getEncryptedUfc().trim(), benef.getFamilyHeadAadharNumber());
                if (beneficiaryMemberDto != null) {
                    configureData();
                }
            }
        }
        findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void configureData() {
        String cardType = FPSDBHelper.getInstance(BenefProxyDetailsActivity.this).getCardTypeFromId(benef.getCardTypeId());
        ((TextView) findViewById(R.id.rcNoValue)).setText(benef.getOldRationNumber());
        ((TextView) findViewById(R.id.rcTypeValue)).setText(cardType);
        loadBeneficiaryData();
        loadProxyData();
    }

    private void loadHeaderData() {
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.sales_top_heading);
        Util.setTamilText((TextView) findViewById(R.id.rcNoHeading), R.string.ration_card_number1);
        Util.setTamilText((TextView) findViewById(R.id.rcTypeHeading), R.string.ration_card_type);
        Util.setTamilText((TextView) findViewById(R.id.benefDetailHeading), R.string.benef_details);
        Util.setTamilText((TextView) findViewById(R.id.proxyDetailHeading), R.string.proxy_details);
    }

    private void loadBeneficiaryData() {
        benefDetailsList = new ArrayList<>();
        benefDetailsList.add(beneficiaryMemberDto.getName() + "~" + beneficiaryMemberDto.getUid() + "~" + benef.getMobileNumber());
        ListView listView = (ListView) findViewById(R.id.benefList);
        listView.setDivider(new ColorDrawable(BenefProxyDetailsActivity.this.getResources().getColor(R.color.gray)));
        listView.setDividerHeight(1);
        ListViewSampleAdapter adapter = new ListViewSampleAdapter(this) {};
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                String id = String.valueOf(benef.getId());
//                BFDDetailDto bfdDetailDto = FPSDBHelper.getInstance(BenefProxyDetailsActivity.this).getLocalBfdDetailsForHead(id);
//                if (bfdDetailDto != null) {
                    Intent intent = new Intent(BenefProxyDetailsActivity.this, BenefBfdScanActivity.class);
                    String rcNo = getIntent().getStringExtra("RcNumber");
                    intent.putExtra("BenefId", id);
                    intent.putExtra("ProxyId", "");
                    intent.putExtra("RcNumber", rcNo);
                    intent.putExtra("MemberType", "Beneficiary");
                    startActivity(intent);
                    finish();
//                } else {
//                    Toast.makeText(BenefProxyDetailsActivity.this, "Selected Beneficiary does not have BFD details", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    private void loadProxyData() {
        proxyDetailsDto = FPSDBHelper.getInstance(BenefProxyDetailsActivity.this).retrieveProxy(benef.getId());
        if(proxyDetailsDto != null) {
            rationCardListAdapter = new ProxyListAdapter(this, proxyDetailsDto);
            listViewSearch = (ListView) findViewById(R.id.proxyList);
            listViewSearch.setDivider(new ColorDrawable(BenefProxyDetailsActivity.this.getResources().getColor(R.color.gray)));
            listViewSearch.setDividerHeight(1);
            listViewSearch.setAdapter(rationCardListAdapter);
            listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                    String benefId = String.valueOf(benef.getId());
                    String proxyId = String.valueOf(proxyDetailsDto.get(position).getServerId());
                    String approvalStatus = proxyDetailsDto.get(position).getRequestStatus();
                    BFDDetailDto bfdDetailDto = FPSDBHelper.getInstance(BenefProxyDetailsActivity.this).getLocalBfdDetailsForProxy(benefId, proxyId);
                    if (approvalStatus.equalsIgnoreCase("APPROVED")) {
                        if (bfdDetailDto != null) {
                            Intent intent = new Intent(BenefProxyDetailsActivity.this, BenefBfdScanActivity.class);
                            String rcNo = getIntent().getStringExtra("RcNumber");
                            intent.putExtra("BenefId", benefId);
                            intent.putExtra("ProxyId", proxyId);
                            intent.putExtra("RcNumber", rcNo);
                            intent.putExtra("MemberType", "Proxy");
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(BenefProxyDetailsActivity.this, "Selected Proxy does not have BFD details", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(BenefProxyDetailsActivity.this, "Selected Proxy is not approved yet", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
//            Toast.makeText(BenefProxyDetailsActivity.this, "Proxy details not found", Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(this, RcScanEntryActivity.class));
        finish();
    }

    // List adapter
    public class ListViewSampleAdapter extends BaseAdapter {
       private LayoutInflater mInflater;
       public ListViewSampleAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
       }
       public int getCount() {
            return benefDetailsList.size();
       }
       public Object getItem(int position) {
            return position;
       }
       public long getItemId(int position) {
            return position;
       }
       @SuppressWarnings("deprecation")
       public View getView(final int position, View convertView, ViewGroup parent)
       {
           ViewHolder holder = null;
           if (convertView == null)
           {
                convertView = mInflater.inflate(R.layout.benef_details_list_layout, null);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.benefNameTv);
                holder.subTitle1 = (TextView) convertView.findViewById(R.id.aadharTv);
                holder.subTitle2 = (TextView)convertView.findViewById(R.id.mobileTv);
                holder.subTitle3 = (TextView)convertView.findViewById(R.id.dateTv);
                convertView.setTag(holder);
           }
           else
           {
                holder = (ViewHolder) convertView.getTag();
           }

           try {
               String[] benefDetails = benefDetailsList.get(position).split("~");
               holder.title.setText(benefDetails[0]);
               holder.subTitle1.setText(benefDetails[1]);
               holder.subTitle2.setText(benefDetails[2]);
               SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
               String dateString = sdf.format(beneficiaryMemberDto.getDob());
               holder.subTitle3.setText(dateString);
           }
           catch(Exception e) {
               Log.e("benef list","exc head dob.."+e);
           }

           return convertView;
       }
       class ViewHolder
       {
            TextView title, subTitle1, subTitle2, subTitle3;
       }
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