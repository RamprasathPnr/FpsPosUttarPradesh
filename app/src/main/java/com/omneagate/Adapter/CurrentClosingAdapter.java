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
 * Created by root on 27/2/17.
 */
public class CurrentClosingAdapter extends RecyclerView.Adapter<CurrentClosingAdapter.CurrentClosingHolder> {

    private Context context;

    private LayoutInflater mLayoutInflater;
    private List<Product> fpsReceiptProductList;


    public CurrentClosingAdapter(Context context, List<Product> fpsReceiptProductList) {
        this.context = context;
        this.fpsReceiptProductList = fpsReceiptProductList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public CurrentClosingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_sales_list, parent, false);
        return new CurrentClosingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CurrentClosingHolder holder, int position) {
        holder.name.setText(fpsReceiptProductList.get(position).getDisplayName());
        holder.qty.setText(""+fpsReceiptProductList.get(position).getClosingBalance());
    }

    @Override
    public int getItemCount() {
        return fpsReceiptProductList.size();
    }

    public class CurrentClosingHolder extends RecyclerView.ViewHolder {
        private TextView name, qty;

        public CurrentClosingHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.tvItemName);
            qty = (TextView) v.findViewById(R.id.tvItemQty);

        }
    }
}