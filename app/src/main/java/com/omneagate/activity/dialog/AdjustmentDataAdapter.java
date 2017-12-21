package com.omneagate.activity.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdjustmentDataAdapter extends BaseAdapter {

    Context ct;

    List<POSStockAdjustmentDto> billDto;

    public AdjustmentDataAdapter(Context context, List<POSStockAdjustmentDto> billDto) {
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
        final POSStockAdjustmentDto bills = billDto.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.stock_adjustment_adapter, null);

            holder = new ViewHolder();
            holder.serialNo = (TextView) view.findViewById(R.id.tvSerialNo);
            holder.deliveryChellanId = (TextView) view.findViewById(R.id.tvDeliveryChellanId);
            holder.dispatchDate = (TextView) view.findViewById(R.id.tvOutwardDate);
            holder.godownName = (TextView) view.findViewById(R.id.tvGodownName);
            holder.lapsedTime = (TextView) view.findViewById(R.id.tvLapsedTime);
            holder.status = (TextView) view.findViewById(R.id.btnStatus);
//            holder.status.setTag(position);
            holder.acknowledge = (CheckBox) view.findViewById(R.id.fpsAdjustmentAcknowledge);
            view.setMinimumHeight(50);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) convertView.getTag();
        }
        holder.serialNo.setText(String.valueOf(position + 1));

        if(bills.getRequestType().equalsIgnoreCase("STOCK_INCREMENT")) {
            Util.setTamilText(holder.status, R.string.stock_increment);
        }
        else if(bills.getRequestType().equalsIgnoreCase("STOCK_DECREMENT")) {
            Util.setTamilText(holder.status, R.string.stock_decrement);
        }



        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedOutwardDate = df.format(bills.getCreatedDate());
        holder.deliveryChellanId.setText(bills.getGodownStockOutwardReferenceNumber());
        holder.dispatchDate.setText(formattedOutwardDate);

//        String lapsedString = lapsedTimeAndDay(bills.getCreatedDate());
        holder.lapsedTime.setText(String.valueOf(bills.getQuantity()));
        String productName = FPSDBHelper.getInstance(ct).getProductName(bills.getProductId());
        holder.godownName.setText(productName);
        holder.acknowledge.setTag(position);
//        holder.acknowledge.setChecked(false);

        holder.acknowledge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if(holder.acknowledge.isChecked()) {
//                        Toast.makeText(ct, "checked.."+position, Toast.LENGTH_LONG).show();
                        if(!Util.ackAdjustmentList.contains(bills)) {
                            Util.ackAdjustmentList.add(bills);
                        }
                    }
                    else {
//                        Toast.makeText(ct, "unchecked.."+position, Toast.LENGTH_LONG).show();
                        if(Util.ackAdjustmentList.contains(bills)) {
                            Util.ackAdjustmentList.remove(bills);
                        }
                    }
               }
           }
        );



        return view;
    }


    /*private String lapsedTimeAndDay(long outwardDateAndTime) {

        long difference = System.currentTimeMillis() - outwardDateAndTime;
        if (difference < 0) {
            difference = 1;
        }
        long differenceInMins = difference / 60000;
        long minutes = differenceInMins % 60;
        differenceInMins /= 60;
        long hours = differenceInMins % 24;
        differenceInMins /= 24;
        long days = differenceInMins;
        String dateTimeData = "";
        if (days > 0) {
            dateTimeData = days + " " + ct.getString(R.string.day) + " ";
            if (hours > 0) {
                dateTimeData = dateTimeData + hours + " " + ct.getString(R.string.hr) + " ";
            } else {
                dateTimeData = dateTimeData + minutes + " " + ct.getString(R.string.min);
            }
        } else if (hours > 0) {
            dateTimeData = dateTimeData + hours + " " + ct.getString(R.string.hr) + " ";
            if (minutes > 0)
                dateTimeData = dateTimeData + minutes + " " + ct.getString(R.string.min);
        } else {
            dateTimeData = dateTimeData + minutes + " " + ct.getString(R.string.min);
        }
        return dateTimeData;
    }*/

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
            /*Typeface tfBamini = Typeface.createFromAsset(ct.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini, Typeface.BOLD);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, ct.getString(id)));*/
            textName.setText(ct.getString(id));
        } else {
            textName.setText(ct.getString(id));
        }
    }

    class ViewHolder {
        TextView serialNo;
        TextView deliveryChellanId;
        TextView dispatchDate;
        TextView godownName, lapsedTime;
        TextView status;
        CheckBox acknowledge;
    }
}