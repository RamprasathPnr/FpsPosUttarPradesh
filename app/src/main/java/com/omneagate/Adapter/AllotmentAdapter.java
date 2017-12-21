package com.omneagate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.omneagate.DTO.Product;
import com.omneagate.activity.R;

import java.util.List;


/**
 * Created by user on 23/2/17.
 */
public class AllotmentAdapter extends RecyclerView.Adapter<AllotmentAdapter.AllotmentHolder> {

    private Context context;

    private LayoutInflater mLayoutInflater;
    List<Product> fpsAllotmentProductList;


    public AllotmentAdapter(Context context, List<Product> fpsAllotmentProductList) {
        this.context = context;
        this.fpsAllotmentProductList = fpsAllotmentProductList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public AllotmentAdapter.AllotmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_allotment_list, parent, false);
        return new AllotmentHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AllotmentAdapter.AllotmentHolder holder, int position) {
        holder.name.setText(fpsAllotmentProductList.get(position).getDisplayName());
        holder.qty.setText(""+fpsAllotmentProductList.get(position).getProductAllotment());
        holder.price.setText(""+String.format("%.2f",fpsAllotmentProductList.get(position).getProductPrice()));
    }

    @Override
    public int getItemCount() {
        return fpsAllotmentProductList.size();
    }

    public class AllotmentHolder extends RecyclerView.ViewHolder {
        private TextView name, qty, price;

        public AllotmentHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.tvItemName);
            qty = (TextView) v.findViewById(R.id.tvItemQty);
            price = (TextView) v.findViewById(R.id.tvItemPrice);
        }
    }
}
