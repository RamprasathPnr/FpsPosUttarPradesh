package com.omneagate.activity.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.DTO.MenuDataDto;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.util.List;

public class MenuAdapter extends BaseAdapter {
    Context ct;
    List<MenuDataDto> menuList;
    private LayoutInflater mInflater;


    public MenuAdapter(Context context, List<MenuDataDto> orders) {
        ct = context;
        menuList = orders;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return menuList.size();
    }

    public MenuDataDto getItem(int position) {
        return menuList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.menu_background, null);
            holder = new ViewHolder();
            holder.number = (TextView) view.findViewById(R.id.textViewMenu);
           holder.imageView = (ImageView) view.findViewById(R.id.imageViewMenu);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.number.setText(menuList.get(position).getName());
        if (GlobalAppState.language != null && GlobalAppState.language.equalsIgnoreCase("te")) {
            holder.number.setText(menuList.get(position).getLName());
        }


       // holder.imageView.setImageResource(menuList.get(position).getId());
        return view;
    }

    class ViewHolder {
        TextView number;
        ImageView imageView;
    }

}