package com.omneagate.activity.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.ReconciliationStockDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.TamilUtil;
import com.omneagate.activity.BackgroundServiceHistoryActivity;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
import com.omneagate.activity.ServiceHistoryDetailActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReconciliationAdapter extends BaseAdapter {

    Context ct;

    List<ReconciliationStockDto> reconciliationStockDtoList;

    public ReconciliationAdapter(Context context, List<ReconciliationStockDto> reconciliationStockDtoList1) {
        ct = context;
        this.reconciliationStockDtoList = reconciliationStockDtoList1;
    }

    public int getCount() {
        return reconciliationStockDtoList.size();
    }

    public ReconciliationStockDto getItem(int position) {
        return reconciliationStockDtoList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view;
        final ReconciliationStockDto reconciliationStockDto = reconciliationStockDtoList.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.adapter_reconciliation, null);
            holder = new ViewHolder();
            holder.serialNo = (TextView) view.findViewById(R.id.tvSerialNo);
            holder.commodityName = (TextView) view.findViewById(R.id.tvCommodityName);
            holder.unit = (TextView) view.findViewById(R.id.tvUnit);
            holder.stock = (TextView) view.findViewById(R.id.tvStock);
            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }
        holder.serialNo.setText(String.valueOf(position + 1));
        if(GlobalAppState.language.equalsIgnoreCase("ta")) {
            holder.commodityName.setText(reconciliationStockDto.getLName());
            holder.unit.setText(reconciliationStockDto.getLUnit());
        }
        else {
            holder.commodityName.setText(reconciliationStockDto.getName());
            holder.unit.setText(reconciliationStockDto.getUnit());
        }
        holder.stock.setText(String.valueOf(reconciliationStockDto.getQuantity()));
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
        TextView commodityName;
        TextView unit;
        TextView stock;
    }
}