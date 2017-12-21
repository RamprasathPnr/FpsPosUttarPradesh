package com.omneagate.activity.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockAdjustmentListAdapter extends BaseAdapter {

    Context ct;

    List<POSStockAdjustmentDto> billDto;

    public StockAdjustmentListAdapter(Context context, List<POSStockAdjustmentDto> billDto) {
        ct = context;
        this.billDto = billDto;

    }

    public int getCount() {
        return billDto.size();
    }

    public POSStockAdjustmentDto getItem(int position) {
        return billDto.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view;
        POSStockAdjustmentDto bills = billDto.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.adapter_fps_stock_adjustment_list, null);

            holder = new ViewHolder();
            holder.serialNo = (TextView) view.findViewById(R.id.tvSerialNo);
            holder.stockAdjustListChellanId = (TextView) view.findViewById(R.id.tvStockAdjustListChellanId);
            holder.dispatchDate = (TextView) view.findViewById(R.id.tvStockAdjustListDate);
            holder.status = (TextView) view.findViewById(R.id.btnStatus);
            holder.sync = (ImageView) view.findViewById(R.id.sync);
            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }
        holder.serialNo.setText(String.valueOf(position + 1));
        holder.stockAdjustListChellanId.setText(String.valueOf(bills.getReferenceNumber()));
        Util.setTamilText(holder.status, R.string.accepted);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        if(bills.getPosAckDate() == null){
            bills.setPosAckDate(new Date().getTime());
        }
        String formattedOutwardDate = df.format(bills.getPosAckDate());
        holder.dispatchDate.setText(formattedOutwardDate);
        Drawable  syncStatus;
        if(bills.getPosAckStatus()){
            if(android.os.Build.VERSION.SDK_INT >= 21){
                syncStatus = ct.getResources().getDrawable(R.drawable.icon_sync_suc, ct.getTheme());
            } else {
                syncStatus = ct.getResources().getDrawable(R.drawable.icon_sync_suc);
            }
        }else{
            if(android.os.Build.VERSION.SDK_INT >= 21){
                syncStatus = ct.getResources().getDrawable(R.drawable.icon_unsync, ct.getTheme());
            } else {
                syncStatus = ct.getResources().getDrawable(R.drawable.icon_unsync);
            }

        }
        holder.sync.setImageDrawable(syncStatus);
        return view;
    }


    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            Typeface tfBamini = Typeface.createFromAsset(ct.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini, Typeface.BOLD);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, ct.getString(id)));
        } else {
            textName.setText(ct.getString(id));
        }
    }

    class ViewHolder {
        TextView serialNo;
        TextView stockAdjustListChellanId;
        TextView dispatchDate;
        TextView status;
        ImageView sync;
    }
}