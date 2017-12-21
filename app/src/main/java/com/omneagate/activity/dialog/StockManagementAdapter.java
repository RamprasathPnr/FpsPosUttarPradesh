package com.omneagate.activity.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.DTO.GridMenuDto;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.util.List;

public class StockManagementAdapter extends BaseAdapter {
    Context mActivity;
    List<GridMenuDto> GridMenuDto;
    private LayoutInflater mInflater;

    public StockManagementAdapter(Context beneficiaryMenuActivity, List<GridMenuDto> gridData) {
        mActivity = beneficiaryMenuActivity;
        GridMenuDto = gridData;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return GridMenuDto.size();
    }

    @Override
    public GridMenuDto getItem(int i) {
        return GridMenuDto.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        View view = convertView;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.adapter_stock_management, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.sales_order_text);
            holder.menuIcon = (ImageView) view.findViewById(R.id.imgeicon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Util.setTamilText(holder.name, GridMenuDto.get(position).getName());
        holder.menuIcon.setImageResource(GridMenuDto.get(position).getImgId());
        return view;
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, String id) {
        if (GlobalAppState.language.equalsIgnoreCase("ta")) {
//            Typeface tfBamini = Typeface.createFromAsset(mActivity.getAssets(), "fonts/Bamini.ttf");
//            textName.setTypeface(tfBamini);
//            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, id));
            textName.setText(id);
        } else {
            textName.setText(id);
        }
    }

    class ViewHolder {
        TextView name;
        ImageView menuIcon;
    }

}
