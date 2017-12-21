package com.omneagate.activity.dialog;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omneagate.activity.R;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<BluetoothDevice> mData;
    Context con;

    public DeviceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        con = context;
    }

    public void setData(List<BluetoothDevice> data) {
        mData = data;
    }


    public int getCount() {
        return (mData == null) ? 0 : mData.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_device, null);

            holder = new ViewHolder();

            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_name);
            holder.addressTv = (TextView) convertView
                    .findViewById(R.id.tv_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mData.get(position);
        holder.nameTv.setText(device.getName());
        holder.addressTv.setText(device.getAddress());

        return convertView;
    }

    static class ViewHolder {
        TextView nameTv;
        TextView addressTv;
    }

}