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
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptHolder> {

    private Context context;

    private LayoutInflater mLayoutInflater;
    private List<Product> fpsReceiptProductList;


    public ReceiptAdapter(Context context, List<Product> fpsReceiptProductList) {
        this.context = context;
        this.fpsReceiptProductList = fpsReceiptProductList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public ReceiptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_sales_list, parent, false);
        return new ReceiptHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReceiptHolder holder, int position) {
        holder.name.setText(fpsReceiptProductList.get(position).getDisplayName());
        holder.qty.setText(""+fpsReceiptProductList.get(position).getReceivedQuantity());
    }

    @Override
    public int getItemCount() {
        return fpsReceiptProductList.size();
    }

    public class ReceiptHolder extends RecyclerView.ViewHolder {
        private TextView name, qty;

        public ReceiptHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.tvItemName);
            qty = (TextView) v.findViewById(R.id.tvItemQty);

        }
    }
}
