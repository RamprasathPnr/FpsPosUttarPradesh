package com.omneagate.activity.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omneagate.DTO.RollMenuDto;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.util.List;

/**
 * Created by user1 on 29/7/15.
 */
public class fpsRollViewAdpter extends BaseAdapter {
    Context mActivity;
    List<RollMenuDto> rollMenuDtos;
    private LayoutInflater mInflater;

    public fpsRollViewAdpter(Context saleActivity, List<RollMenuDto> gridData) {
        mActivity = saleActivity;
        rollMenuDtos = gridData;
        mInflater = (LayoutInflater) saleActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return rollMenuDtos.size();
    }

    @Override
    public RollMenuDto getItem(int i) {
        return rollMenuDtos.get(i);
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
            view = mInflater.inflate(R.layout.fps_rollrow, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.sales_order_text);
            holder.menuIcon = (ImageView) view.findViewById(R.id.imgeicon);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Util.setTamilText(holder.name, rollMenuDtos.get(position).getName());
        holder.menuIcon.setImageResource(rollMenuDtos.get(position).getImgId());
        return view;
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, String id) {
        if (GlobalAppState.language.equalsIgnoreCase("hi")) {
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
