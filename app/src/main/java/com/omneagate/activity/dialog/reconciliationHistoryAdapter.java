package com.omneagate.activity.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.BackgroundServiceDto;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.ReconciliationRequestDto;
import com.omneagate.Util.TamilUtil;
import com.omneagate.activity.BackgroundServiceHistoryActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.ReconciliationHistoryActivity;
import com.omneagate.activity.ServiceHistoryDetailActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class reconciliationHistoryAdapter extends BaseAdapter {

    Context ct;

    List<ReconciliationRequestDto> reconciliationRequestDtoList;

    public reconciliationHistoryAdapter(Context context, List<ReconciliationRequestDto> reconciliationRequestDtoList1) {
        ct = context;
        this.reconciliationRequestDtoList = reconciliationRequestDtoList1;
    }

    public int getCount() {
        return reconciliationRequestDtoList.size();
    }

    public ReconciliationRequestDto getItem(int position) {
        return reconciliationRequestDtoList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view;
        final ReconciliationRequestDto reconciliationRequestDto = reconciliationRequestDtoList.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.adapter_reconciliation_history_list, null);
            holder = new ViewHolder();
            holder.serialNo = (TextView) view.findViewById(R.id.tvSerialNo);
            holder.reconciliationId = (TextView) view.findViewById(R.id.tvReconciliationId);
            holder.requestDateTime = (TextView) view.findViewById(R.id.tvRequestDateTime);
            holder.responseDateTime = (TextView) view.findViewById(R.id.tvResponseDateTime);
            holder.status = (TextView) view.findViewById(R.id.tvStatus);
            holder.btnCheckStatus = (TextView) view.findViewById(R.id.btncheckStatus);
            holder.btnCheckStatus.setTag(position);
            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        holder.serialNo.setText(String.valueOf(position + 1));
        holder.reconciliationId.setText(reconciliationRequestDto.getTransactionId());
        try {
            String requestDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(reconciliationRequestDto.getRequestDateTime());
            holder.requestDateTime.setText(requestDate);
        }
        catch(Exception e) {}
        try {
            String responseDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(reconciliationRequestDto.getResponseDateTime());
            holder.responseDateTime.setText(responseDate);
        }
        catch(Exception e) {}
        holder.status.setText(reconciliationRequestDto.getStatus());
        holder.btnCheckStatus.setText(ct.getResources().getString(R.string.check_status));
        if(reconciliationRequestDto.getStatus().equalsIgnoreCase("INPROGRESS")) {
            holder.btnCheckStatus.setVisibility(View.VISIBLE);
        }
        else {
            holder.btnCheckStatus.setVisibility(View.INVISIBLE);
        }
        holder.btnCheckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ReconciliationHistoryActivity reconciliationHistoryActivity = new ReconciliationHistoryActivity();*/
                try {
                    ReconciliationHistoryActivity reconciliationHistoryActivity = (ReconciliationHistoryActivity) ct;
                    reconciliationHistoryActivity.getStatus(reconciliationRequestDto);
                }
                catch(Exception e) {}
            }
        });
        return view;
    }

    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equalsIgnoreCase("ta")) {
            Typeface tfBamini = Typeface.createFromAsset(ct.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini, Typeface.BOLD);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, ct.getString(id)));
        } else {
            textName.setText(ct.getString(id));
        }
    }

    class ViewHolder {
        TextView serialNo;
        TextView reconciliationId;
        TextView responseDateTime, requestDateTime;
        TextView status, btnCheckStatus;
    }
}