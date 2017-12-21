package com.omneagate.activity.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.BackgroundServiceDto;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.ServiceHistoryDetailActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ServiceHistoryAdapter extends BaseAdapter {

    Context ct;

    List<BackgroundServiceDto> backgroundServiceDtoList;

    public ServiceHistoryAdapter(Context context, List<BackgroundServiceDto> backgroundServiceDtoList1) {
        ct = context;
        this.backgroundServiceDtoList = backgroundServiceDtoList1;
    }

    public int getCount() {
        return backgroundServiceDtoList.size();
    }

    public BackgroundServiceDto getItem(int position) {
        return backgroundServiceDtoList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view;
        final BackgroundServiceDto backgroundServiceDto = backgroundServiceDtoList.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.adapter_background_service_history, null);
            holder = new ViewHolder();
            holder.serialNo = (TextView) view.findViewById(R.id.tvSerialNo);
            holder.requestData = (TextView) view.findViewById(R.id.tvRequestData);
            holder.responseData = (TextView) view.findViewById(R.id.tvResponseData);
            holder.requestDateTime = (TextView) view.findViewById(R.id.tvRequestDateTime);
            holder.responseDateTime = (TextView) view.findViewById(R.id.tvResponseDateTime);
            holder.status = (TextView) view.findViewById(R.id.tvStatus);
            holder.btn_view = (TextView) view.findViewById(R.id.btnView);
            holder.btn_view.setTag(position);
            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ct, ServiceHistoryDetailActivity.class);
                myIntent.putExtra("backgroundServiceDto", backgroundServiceDto);
                ct.startActivity(myIntent);
            }
        });
        holder.serialNo.setText(String.valueOf(position + 1));
        Util.setTamilText(holder.btn_view, R.string.view);
        holder.requestData.setText(backgroundServiceDto.getRequestData());
        holder.responseData.setText(backgroundServiceDto.getResponseData());
        try {
            String requestDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(backgroundServiceDto.getRequestDateTime());
            holder.requestDateTime.setText(requestDate);
        }
        catch(Exception e) {}
        try {
            String responseDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(backgroundServiceDto.getResponseDateTime());
            holder.responseDateTime.setText(responseDate);
        }
        catch(Exception e) {}
        holder.status.setText(backgroundServiceDto.getStatus());
        return view;
    }




    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
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
        TextView requestData;
        TextView responseData;
        TextView responseDateTime, requestDateTime;
        TextView status, btn_view;
    }
}